package com.airesumeanalyzer.backend.embedding.service.impl;

import com.airesumeanalyzer.backend.embedding.config.OllamaEmbeddingProperties;
import com.airesumeanalyzer.backend.embedding.entity.EmbeddingRecord;
import com.airesumeanalyzer.backend.embedding.repository.EmbeddingRecordRepository;
import com.airesumeanalyzer.backend.embedding.service.EmbeddingService;
import com.airesumeanalyzer.backend.rie.capability.embedding.Embedding;
import com.airesumeanalyzer.backend.rie.capability.embedding.EmbeddingProvider;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmbeddingServiceImpl
        implements EmbeddingService {

    private final EmbeddingRecordRepository embeddingRecordRepository;
    private final EmbeddingProvider embeddingProvider;
    private final OllamaEmbeddingProperties properties;
    private final MeterRegistry meterRegistry;


    @Override
    public Map<String, Embedding> embedBatch(Collection<String> values) {
        Objects.requireNonNull(values, "values must not be null");

        if (values.isEmpty()) {
            return Map.of();
        }

        Timer.Sample sample = Timer.start(meterRegistry);

        try {
           List<String> normalizedValues = values.stream()
                   .map(value -> {
                       if (value == null || value.isBlank()) {
                           throw new IllegalArgumentException(
                                   "embedding input must not be null or blank"
                           );
                       }

                       return value.trim();
                   })
                   .distinct()
                   .toList();

           Map<String, String> inputHashes = new LinkedHashMap<>();

           for (String value : normalizedValues) {
               inputHashes.put(value, hashInput(value));
           }

           List<EmbeddingRecord> existingRecords =
                   embeddingRecordRepository.findAllByModelAndInputHashIn(
                           properties.model(),
                           inputHashes.values()
                   );
           Map<String, EmbeddingRecord> existingByHash =
                   existingRecords.stream()
                           .collect(Collectors.toMap(
                                   EmbeddingRecord::getInputHash,
                                   Function.identity()
                           ));

           Map<String, Embedding> embeddings =
                   new LinkedHashMap<>();

           for (String value : normalizedValues) {
               String hash = inputHashes.get(value);

               EmbeddingRecord record =
                       existingByHash.get(hash);

               if (record != null) {
                   embeddings.put(
                           value,
                           toEmbedding(record)
                   );
               }
           }

           List<String> missingValues =
                   normalizedValues.stream()
                           .filter(value -> !existingByHash.containsKey(
                                   inputHashes.get(value)
                           ))
                           .toList();

           if (!missingValues.isEmpty()) {

               Map<String, Embedding> generatedEmbeddings =
                       embeddingProvider.embedBatch(missingValues);

               if (generatedEmbeddings.size() != missingValues.size()
                       || !generatedEmbeddings.keySet().containsAll(missingValues)) {
                   throw new IllegalStateException(
                           "Embedding provider returned an incomplete result"
                   );
               }
               embeddings.putAll(generatedEmbeddings);

               List<EmbeddingRecord> newRecords =
                       new ArrayList<>();

               for (String value : missingValues) {

                   Embedding embedding =
                           generatedEmbeddings.get(value);

                   if (embedding == null) {
                       throw new IllegalStateException(
                               "Missing generated embedding for: " + value
                       );
                   }

                   newRecords.add(
                           EmbeddingRecord.builder()
                                   .model(properties.model())
                                   .inputHash(inputHashes.get(value))
                                   .inputText(value)
                                   .dimensions(embedding.vector().length)
                                   .vector(toBytes(embedding))
                                   .build()
                   );
               }
               if (!newRecords.isEmpty()) {
                   embeddingRecordRepository.saveAll(newRecords);
               }
           }
           return Map.copyOf(embeddings);
    }finally{
            sample.stop(
                    meterRegistry.timer("resume.embedding.duration")
            );
        }
    }

    private String hashInput(String value) {
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    value.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is not available",
                    exception
            );
        }
    }

    private Embedding toEmbedding(EmbeddingRecord record) {
        Objects.requireNonNull(record, "embedding record must not be null");

        byte[] vector = record.getVector();

        if (vector == null || vector.length == 0) {
            throw new IllegalStateException(
                    "Embedding record contains an empty vector: "
                            + record.getId()
            );
        }

        int dimensions = record.getDimensions();

        if (dimensions <= 0) {
            throw new IllegalStateException(
                    "Embedding record contains invalid dimensions: "
                            + record.getId()
            );
        }

        int expectedByteLength = dimensions * Float.BYTES;

        if (vector.length != expectedByteLength) {
            throw new IllegalStateException(
                    "Embedding vector byte length does not match dimensions: "
                            + record.getId()
            );
        }

        ByteBuffer buffer =
                ByteBuffer.wrap(vector)
                        .order(ByteOrder.BIG_ENDIAN);

        float[] values = new float[dimensions];

        for (int i = 0; i < dimensions; i++) {
            float value = buffer.getFloat();

            if (!Float.isFinite(value)) {
                throw new IllegalStateException(
                        "Embedding record contains an invalid vector value: "
                                + record.getId()
                );
            }

            values[i] = value;
        }

        return new Embedding(values);
    }

    private byte[] toBytes(Embedding embedding) {
        Objects.requireNonNull(embedding, "embedding must not be null");

        float[] values = embedding.vector();

        ByteBuffer buffer =
                ByteBuffer.allocate(values.length * Float.BYTES)
                        .order(ByteOrder.BIG_ENDIAN);

        for (float value : values) {
            if (!Float.isFinite(value)) {
                throw new IllegalArgumentException(
                        "embedding contains an invalid vector value"
                );
            }

            buffer.putFloat(value);
        }

        return buffer.array();
    }
}
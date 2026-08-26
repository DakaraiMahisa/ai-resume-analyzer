package com.airesumeanalyzer.backend.processing.extraction;

import com.airesumeanalyzer.backend.processing.exception.ProcessingException;
import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class TikaTextExtractor implements TextExtractor {

    private final Tika tika;

    public TikaTextExtractor() {
        this.tika = new Tika();
    }

    @Override
    public String extract(InputStream inputStream) {

        try {
            String text = tika.parseToString(inputStream);

            if (text == null || text.isBlank()) {
                throw new ProcessingException(
                        "No text could be extracted from the document."
                );
            }

            return text;

        } catch (TikaException | IOException exception) {
            throw new ProcessingException(
                    "Unable to extract text from the document.",
                    exception
            );
        }
    }
}
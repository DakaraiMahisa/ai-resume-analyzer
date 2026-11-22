package com.airesumeanalyzer.backend.career.languages.service.impl;

import com.airesumeanalyzer.backend.career.languages.entity.CandidateLanguage;
import com.airesumeanalyzer.backend.career.languages.repository.CandidateLanguageRepository;
import com.airesumeanalyzer.backend.career.languages.dto.response.CandidateLanguageResponse;
import com.airesumeanalyzer.backend.career.languages.dto.request.CreateCandidateLanguageRequest;
import com.airesumeanalyzer.backend.career.languages.dto.request.UpdateCandidateLanguageRequest;
import com.airesumeanalyzer.backend.career.languages.service.CandidateLanguageService;
import com.airesumeanalyzer.backend.common.exception.base.ConflictException;
import com.airesumeanalyzer.backend.common.exception.base.ResourceNotFoundException;
import com.airesumeanalyzer.backend.user.entity.User;
import com.airesumeanalyzer.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CandidateLanguageServiceImpl implements CandidateLanguageService {

    private final CandidateLanguageRepository languageRepository;
    private final UserRepository userRepository;

    @Override
    public CandidateLanguageResponse create(
            UUID userId,
            CreateCandidateLanguageRequest request
    ) {
        String languageName = request.languageName().trim();

        if (languageRepository.existsByUserIdAndLanguageNameIgnoreCase(
                userId, languageName
        )) {
            throw new ConflictException(
                    "This language has already been added to your profile"
            );
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        CandidateLanguage language = CandidateLanguage.builder()
                .user(user)
                .languageName(languageName)
                .speakingLevel(request.speakingLevel())
                .listeningLevel(request.listeningLevel())
                .readingLevel(request.readingLevel())
                .writingLevel(request.writingLevel())
                .nativeLanguage(request.nativeLanguage())
                .build();

        return mapToResponse(languageRepository.save(language));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CandidateLanguageResponse> getAll(UUID userId) {
        return languageRepository.findAllByUserIdOrderByLanguageNameAsc(userId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CandidateLanguageResponse getById(UUID userId, UUID languageId) {
        return mapToResponse(findLanguage(userId, languageId));
    }

    @Override
    public CandidateLanguageResponse update(
            UUID userId,
            UUID languageId,
            UpdateCandidateLanguageRequest request
    ) {
        CandidateLanguage language = findLanguage(userId, languageId);

        if (request.languageName() != null) {
            String languageName = request.languageName().trim();

            if (languageName.isBlank()) {
                throw new com.airesumeanalyzer.backend.common.exception.base.BadRequestException(
                        "Language name cannot be blank"
                );
            }

            if (languageRepository.existsByUserIdAndLanguageNameIgnoreCaseAndIdNot(
                    userId, languageName, languageId
            )) {
                throw new ConflictException(
                        "This language has already been added to your profile"
                );
            }

            language.setLanguageName(languageName);
        }

        if (request.speakingLevel() != null) {
            language.setSpeakingLevel(request.speakingLevel());
        }

        if (request.listeningLevel() != null) {
            language.setListeningLevel(request.listeningLevel());
        }

        if (request.readingLevel() != null) {
            language.setReadingLevel(request.readingLevel());
        }

        if (request.writingLevel() != null) {
            language.setWritingLevel(request.writingLevel());
        }

        if (request.nativeLanguage() != null) {
            language.setNativeLanguage(request.nativeLanguage());
        }

        return mapToResponse(languageRepository.save(language));
    }

    @Override
    public void delete(UUID userId, UUID languageId) {
        CandidateLanguage language = findLanguage(userId, languageId);
        languageRepository.delete(language);
    }

    private CandidateLanguage findLanguage(UUID userId, UUID languageId) {
        return languageRepository.findByIdAndUserId(languageId, userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Language not found"
                ));
    }

    private CandidateLanguageResponse mapToResponse(CandidateLanguage language) {
        return new CandidateLanguageResponse(
                language.getId(),
                language.getLanguageName(),
                language.getSpeakingLevel(),
                language.getListeningLevel(),
                language.getReadingLevel(),
                language.getWritingLevel(),
                language.isNativeLanguage(),
                language.getCreatedAt(),
                language.getUpdatedAt()
        );
    }
}

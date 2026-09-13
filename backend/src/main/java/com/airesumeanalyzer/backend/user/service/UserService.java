package com.airesumeanalyzer.backend.user.service;

import com.airesumeanalyzer.backend.common.exception.base.ResourceNotFoundException;
import com.airesumeanalyzer.backend.user.dto.CurrentUserResponse;
import com.airesumeanalyzer.backend.user.entity.User;
import com.airesumeanalyzer.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public CurrentUserResponse getCurrentUser(UUID userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authenticated user not found: " + userId
                        )
                );

        return new CurrentUserResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}




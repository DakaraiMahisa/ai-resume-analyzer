package com.airesumeanalyzer.backend.resume.repository;

import com.airesumeanalyzer.backend.resume.entity.Resume;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ResumeRepository extends JpaRepository<Resume, UUID> {
    List<Resume> findAllByOwnerIdOrderByCreatedAtDesc(UUID ownerId);

    Optional<Resume> findByIdAndOwnerId(UUID resumeId, UUID ownerId);

    Optional<Resume> findByChecksum(String checksum);

    boolean existsByOwnerIdAndChecksum(UUID ownerId, String checksum);
}
package com.airesumeanalyzer.backend.jobdescription.repository;

import com.airesumeanalyzer.backend.jobdescription.entity.JobDescription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobDescriptionRepository extends JpaRepository<JobDescription, UUID> {

    List<JobDescription> findAllByOwnerId(UUID ownerId);

    Optional<JobDescription> findByIdAndOwnerId(
            UUID jobDescriptionId,
            UUID ownerId
    );

    Optional<JobDescription> findByChecksum(String checksum);
}
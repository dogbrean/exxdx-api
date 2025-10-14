package com.exxdx.exxdx_api.diary.infra;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataDiaryJpa extends JpaRepository<JpaDiaryEntryEntity, Long> {
    Page<JpaDiaryEntryEntity> findAll(Pageable pageable);
    long countByOwnerId(Long ownerId);
    boolean existsById(Long id);
}
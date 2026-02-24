package com.profilehub.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.profilehub.entity.Document;
import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByUserId(Long userId);
    Page<Document> findByUserId(Long userId, Pageable pageable);
    Page<Document> findByIsPublic(Boolean isPublic, Pageable pageable);
    Optional<Document> findByIdAndUserId(Long id, Long userId);
    long countByUserId(Long userId);
}

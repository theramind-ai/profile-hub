package com.profilehub.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import com.profilehub.dto.DocumentResponse;
import com.profilehub.entity.Document;
import com.profilehub.entity.User;
import com.profilehub.exception.ApiException;
import com.profilehub.repository.DocumentRepository;
import com.profilehub.repository.UserRepository;
import com.profilehub.util.FileUtils;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    @Autowired
    private UserRepository userRepository;

    public DocumentResponse uploadDocument(Long userId, MultipartFile file, String description, Boolean isPublic) {
        try {
            // Validate file
            if (file.isEmpty()) {
                throw new ApiException("File is empty", 400);
            }

            if (!FileUtils.isValidFileSize(file.getSize())) {
                throw new ApiException("File size exceeds maximum limit of 50MB", 400);
            }

            if (!FileUtils.isAllowedFileType(file.getOriginalFilename())) {
                throw new ApiException("File type not allowed", 400);
            }

            // Get user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ApiException("User not found", 404));

            // Save file
            byte[] fileContent = file.getBytes();
            String fileUrl = FileUtils.saveFile(fileContent, file.getOriginalFilename());

            // Create document
            Document document = Document.builder()
                    .name(file.getOriginalFilename())
                    .fileName(file.getOriginalFilename())
                    .fileUrl(fileUrl)
                    .fileSize(file.getSize())
                    .fileType(file.getContentType())
                    .description(description)
                    .user(user)
                    .isPublic(isPublic != null ? isPublic : false)
                    .build();

            document = documentRepository.save(document);
            log.info("Document uploaded: {} by user: {}", document.getId(), userId);

            return mapDocumentToResponse(document);

        } catch (IOException e) {
            log.error("Error uploading file: {}", e.getMessage());
            throw new ApiException("Error uploading file", 500);
        }
    }

    public DocumentResponse getDocument(Long documentId, Long userId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ApiException("Document not found", 404));

        // Check if document belongs to user or is public
        if (!document.getUser().getId().equals(userId) && !document.getIsPublic()) {
            throw new ApiException("Unauthorized access to document", 403);
        }

        return mapDocumentToResponse(document);
    }

    public List<DocumentResponse> getUserDocuments(Long userId) {
        return documentRepository.findByUserId(userId)
                .stream()
                .map(this::mapDocumentToResponse)
                .collect(Collectors.toList());
    }

    public Page<DocumentResponse> getUserDocumentsPaginated(Long userId, Pageable pageable) {
        return documentRepository.findByUserId(userId, pageable)
                .map(this::mapDocumentToResponse);
    }

    public Page<DocumentResponse> getPublicDocuments(Pageable pageable) {
        return documentRepository.findByIsPublic(true, pageable)
                .map(this::mapDocumentToResponse);
    }

    public DocumentResponse updateDocument(Long documentId, Long userId, Document documentDetails) {
        Document document = documentRepository.findByIdAndUserId(documentId, userId)
                .orElseThrow(() -> new ApiException("Document not found or unauthorized", 404));

        if (documentDetails.getDescription() != null) {
            document.setDescription(documentDetails.getDescription());
        }
        if (documentDetails.getIsPublic() != null) {
            document.setIsPublic(documentDetails.getIsPublic());
        }

        document = documentRepository.save(document);
        log.info("Document updated: {}", documentId);

        return mapDocumentToResponse(document);
    }

    public void deleteDocument(Long documentId, Long userId) {
        Document document = documentRepository.findByIdAndUserId(documentId, userId)
                .orElseThrow(() -> new ApiException("Document not found or unauthorized", 404));

        // Delete file from system
        FileUtils.deleteFile(document.getFileUrl());

        documentRepository.delete(document);
        log.info("Document deleted: {}", documentId);
    }

    @SuppressWarnings("null")
    public byte[] downloadDocument(Long documentId, Long userId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ApiException("Document not found", 404));

        // Check if document belongs to user or is public
        if (!document.getUser().getId().equals(userId) && !document.getIsPublic()) {
            throw new ApiException("Unauthorized access to document", 403);
        }

        return FileUtils.readFile(document.getFileUrl());
    }

    private DocumentResponse mapDocumentToResponse(Document document) {
        return DocumentResponse.builder()
                .id(document.getId())
                .name(document.getName())
                .fileName(document.getFileName())
                .fileUrl(document.getFileUrl())
                .fileSize(document.getFileSize())
                .fileType(document.getFileType())
                .description(document.getDescription())
                .userId(document.getUser().getId())
                .isPublic(document.getIsPublic())
                .createdAt(document.getCreatedAt())
                .updatedAt(document.getUpdatedAt())
                .build();
    }
}

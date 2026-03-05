package com.profilehub.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.profilehub.dto.DocumentResponse;
import com.profilehub.entity.Document;
import com.profilehub.service.DocumentService;
import com.profilehub.repository.DocumentRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentRepository documentRepository;

    @PostMapping("/upload")
    public ResponseEntity<DocumentResponse> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "isPublic", required = false) Boolean isPublic,
            Authentication authentication) {
        log.info("Uploading document: {}", file.getOriginalFilename());

        Long userId = extractUserIdFromAuth(authentication);
        DocumentResponse documentResponse = documentService.uploadDocument(userId, file, description, isPublic);
        return ResponseEntity.status(HttpStatus.CREATED).body(documentResponse);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<DocumentResponse> getDocument(
            @PathVariable Long documentId,
            Authentication authentication) {
        log.info("Getting document: {}", documentId);

        Long userId = extractUserIdFromAuth(authentication);
        DocumentResponse documentResponse = documentService.getDocument(documentId, userId);
        return ResponseEntity.ok(documentResponse);
    }

    @GetMapping("/user/documents")
    public ResponseEntity<List<DocumentResponse>> getUserDocuments(Authentication authentication) {
        log.info("Getting user documents");

        Long userId = extractUserIdFromAuth(authentication);
        List<DocumentResponse> documents = documentService.getUserDocuments(userId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/user/documents/paginated")
    public ResponseEntity<Page<DocumentResponse>> getUserDocumentsPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Authentication authentication) {
        log.info("Getting user documents paginated");

        Long userId = extractUserIdFromAuth(authentication);
        Pageable pageable = PageRequest.of(page, size);
        Page<DocumentResponse> documents = documentService.getUserDocumentsPaginated(userId, pageable);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/public")
    public ResponseEntity<Page<DocumentResponse>> getPublicDocuments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        log.info("Getting public documents");

        Pageable pageable = PageRequest.of(page, size);
        Page<DocumentResponse> documents = documentService.getPublicDocuments(pageable);
        return ResponseEntity.ok(documents);
    }

    @PutMapping("/{documentId}")
    public ResponseEntity<DocumentResponse> updateDocument(
            @PathVariable Long documentId,
            @RequestBody Document documentDetails,
            Authentication authentication) {
        log.info("Updating document: {}", documentId);

        Long userId = extractUserIdFromAuth(authentication);
        DocumentResponse documentResponse = documentService.updateDocument(documentId, userId, documentDetails);
        return ResponseEntity.ok(documentResponse);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<String> deleteDocument(
            @PathVariable Long documentId,
            Authentication authentication) {
        log.info("Deleting document: {}", documentId);

        Long userId = extractUserIdFromAuth(authentication);
        documentService.deleteDocument(documentId, userId);
        return ResponseEntity.ok("Document deleted successfully");
    }

    @SuppressWarnings("null")
    @GetMapping("/download/{documentId}")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable Long documentId,
            Authentication authentication) {
        log.info("Downloading document: {}", documentId);

        Long userId = extractUserIdFromAuth(authentication);
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        byte[] content = documentService.downloadDocument(documentId, userId);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + document.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(content);
    }

    private Long extractUserIdFromAuth(Authentication authentication) {
        com.profilehub.security.UserDetailsImpl userDetails = (com.profilehub.security.UserDetailsImpl) authentication
                .getPrincipal();
        return userDetails.getId();
    }
}

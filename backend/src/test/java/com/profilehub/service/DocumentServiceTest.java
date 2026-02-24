package com.profilehub.service;

import com.profilehub.dto.DocumentResponse;
import com.profilehub.entity.Document;
import com.profilehub.repository.DocumentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class DocumentServiceTest {
    @Mock
    private DocumentRepository documentRepository;
    @InjectMocks
    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getDocument_DocumentExists_ReturnsDocumentResponse() {
        Document doc = Document.builder().id(1L).fileName("file.pdf").build();
        when(documentRepository.findByIdAndUserId(1L, 2L)).thenReturn(Optional.of(doc));
        DocumentResponse response = documentService.getDocument(1L, 2L);
        assertNotNull(response);
        assertEquals("file.pdf", response.getFileName());
    }

    @Test
    void getDocument_DocumentNotFound_ReturnsNull() {
        when(documentRepository.findByIdAndUserId(anyLong(), anyLong())).thenReturn(Optional.empty());
        DocumentResponse response = documentService.getDocument(99L, 2L);
        assertNull(response);
    }
}

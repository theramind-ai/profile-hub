package com.profilehub.controller;

import com.profilehub.dto.DocumentResponse;
import com.profilehub.service.DocumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DocumentControllerTest {
    @Mock
    private DocumentService documentService;
    @InjectMocks
    private DocumentController documentController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserDocuments_ReturnsList() {
        List<DocumentResponse> docs = Collections.singletonList(DocumentResponse.builder().id(1L).fileName("file.pdf").build());
        when(documentService.getUserDocuments(anyLong())).thenReturn(docs);
        ResponseEntity<List<DocumentResponse>> response = documentController.getUserDocuments(null);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }
}

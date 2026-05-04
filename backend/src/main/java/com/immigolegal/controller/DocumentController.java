package com.immigolegal.controller;

import com.immigolegal.model.DocumentAnalysisRequest;
import com.immigolegal.model.DocumentAnalysisResponse;
import com.immigolegal.service.ClaudeService;
import com.immigolegal.service.DocumentParserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/documents")
@CrossOrigin(origins = "${app.frontend.url:http://localhost:5173}")
public class DocumentController {

    private final ClaudeService claudeService;
    private final DocumentParserService documentParserService;

    public DocumentController(ClaudeService claudeService, DocumentParserService documentParserService) {
        this.claudeService = claudeService;
        this.documentParserService = documentParserService;
    }

    @PostMapping("/analyze/text")
    public ResponseEntity<DocumentAnalysisResponse> analyzeText(
            @Valid @RequestBody DocumentAnalysisRequest request) {
        DocumentAnalysisResponse response = claudeService.analyzeDocument(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/analyze/upload")
    public ResponseEntity<DocumentAnalysisResponse> analyzeUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "documentType", required = false, defaultValue = "UNKNOWN")
            DocumentAnalysisRequest.DocumentType documentType,
            @RequestParam(value = "preferredLanguage", required = false, defaultValue = "en")
            String preferredLanguage) throws IOException {

        String extractedText = documentParserService.extractText(file);

        DocumentAnalysisRequest request = new DocumentAnalysisRequest();
        request.setDocumentText(extractedText);
        request.setDocumentType(documentType);
        request.setPreferredLanguage(preferredLanguage);

        DocumentAnalysisResponse response = claudeService.analyzeDocument(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Immigration Document Analyzer API is running");
    }
}

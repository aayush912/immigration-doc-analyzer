package com.immigolegal.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class DocumentParserService {

    public String extractText(MultipartFile file) throws IOException {
        String contentType = file.getContentType();
        if (contentType == null) {
            throw new IllegalArgumentException("Cannot determine file type");
        }

        return switch (contentType) {
            case "application/pdf" -> extractFromPdf(file.getInputStream());
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document" ->
                    extractFromDocx(file.getInputStream());
            case "text/plain" -> new String(file.getBytes());
            default -> throw new IllegalArgumentException("Unsupported file type: " + contentType);
        };
    }

    private String extractFromPdf(InputStream inputStream) throws IOException {
        try (PDDocument doc = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(doc);
        }
    }

    private String extractFromDocx(InputStream inputStream) throws IOException {
        try (XWPFDocument doc = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
            return extractor.getText();
        }
    }
}

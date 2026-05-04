package com.immigolegal.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DocumentAnalysisRequest {
    @NotBlank(message = "Document text must not be empty")
    private String documentText;

    private DocumentType documentType;

    private String preferredLanguage = "en";

    public enum DocumentType {
        VISA_DECISION_LETTER,
        RFE,
        NMSBA,
        I485,
        I130,
        I765,
        UNKNOWN
    }
}

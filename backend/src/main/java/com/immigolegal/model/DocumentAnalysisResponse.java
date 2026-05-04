package com.immigolegal.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class DocumentAnalysisResponse {
    private String documentType;
    private String summary;
    private String plainLanguageExplanation;
    private List<String> keyDates;
    private List<String> requiredActions;
    private List<String> missingDocuments;
    private String urgencyLevel;
    private String disclaimer;
    private long processingTimeMs;
}

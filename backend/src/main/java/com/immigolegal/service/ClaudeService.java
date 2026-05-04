package com.immigolegal.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.Model;
import com.anthropic.models.messages.TextBlockParam;
import com.immigolegal.model.DocumentAnalysisRequest;
import com.immigolegal.model.DocumentAnalysisResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ClaudeService {

    @Value("${anthropic.api.key}")
    private String apiKey;

    private AnthropicClient client;

    private static final String SYSTEM_PROMPT = """
            You are an expert immigration document analyst. Your role is to help immigrants understand \
            complex legal documents in plain, simple language.

            When analyzing a document, you MUST respond in the following exact JSON format:
            {
              "documentType": "<detected type, e.g. RFE, Visa Denial, Approval Notice>",
              "summary": "<1-2 sentence summary of what this document is>",
              "plainLanguageExplanation": "<clear, plain-English explanation of what this means for the applicant>",
              "keyDates": ["<date 1 with context>", "<date 2 with context>"],
              "requiredActions": ["<action 1>", "<action 2>"],
              "missingDocuments": ["<document 1>", "<document 2>"],
              "urgencyLevel": "<LOW | MEDIUM | HIGH | CRITICAL>",
              "disclaimer": "This analysis is for informational purposes only and does not constitute legal advice. Please consult a licensed immigration attorney for guidance specific to your situation."
            }

            Rules:
            - Use simple language a non-lawyer can understand
            - Always include the standard disclaimer
            - If a field has no applicable data, use an empty array [] or "N/A"
            - Never fabricate information not present in the document
            - Flag hallucination risks explicitly if the document is unclear
            """;

    @PostConstruct
    public void init() {
        client = AnthropicOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
    }

    public DocumentAnalysisResponse analyzeDocument(DocumentAnalysisRequest request) {
        long startTime = System.currentTimeMillis();

        String userPrompt = buildUserPrompt(request);

        Message message = client.messages().create(
                MessageCreateParams.builder()
                        .model(Model.CLAUDE_SONNET_4_5)
                        .maxTokens(2048)
                        .system(SYSTEM_PROMPT)
                        .addUserMessageOfText(userPrompt)
                        .build()
        );

        String rawResponse = message.content().get(0).text().get().text();
        long processingTime = System.currentTimeMillis() - startTime;

        return parseClaudeResponse(rawResponse, processingTime);
    }

    private String buildUserPrompt(DocumentAnalysisRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("Please analyze the following immigration document");
        if (request.getDocumentType() != null && request.getDocumentType() != DocumentAnalysisRequest.DocumentType.UNKNOWN) {
            sb.append(" (type hint: ").append(request.getDocumentType()).append(")");
        }
        sb.append(":\n\n---\n");
        sb.append(request.getDocumentText());
        sb.append("\n---");
        if (!"en".equals(request.getPreferredLanguage())) {
            sb.append("\n\nPlease provide the plainLanguageExplanation in ").append(request.getPreferredLanguage());
        }
        return sb.toString();
    }

    private DocumentAnalysisResponse parseClaudeResponse(String rawResponse, long processingTime) {
        // Strip markdown code fences if present
        String json = rawResponse.trim();
        if (json.startsWith("```")) {
            json = json.replaceFirst("```(?:json)?\\n?", "").replaceAll("```$", "").trim();
        }

        // Basic JSON field extraction (production code should use Jackson ObjectMapper)
        return DocumentAnalysisResponse.builder()
                .documentType(extractField(json, "documentType"))
                .summary(extractField(json, "summary"))
                .plainLanguageExplanation(extractField(json, "plainLanguageExplanation"))
                .keyDates(extractArray(json, "keyDates"))
                .requiredActions(extractArray(json, "requiredActions"))
                .missingDocuments(extractArray(json, "missingDocuments"))
                .urgencyLevel(extractField(json, "urgencyLevel"))
                .disclaimer(extractField(json, "disclaimer"))
                .processingTimeMs(processingTime)
                .build();
    }

    private String extractField(String json, String fieldName) {
        String pattern = "\"" + fieldName + "\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"";
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern).matcher(json);
        return m.find() ? m.group(1) : "";
    }

    private List<String> extractArray(String json, String fieldName) {
        String pattern = "\"" + fieldName + "\"\\s*:\\s*\\[([^\\]]*)\\]";
        java.util.regex.Matcher m = java.util.regex.Pattern.compile(pattern, java.util.regex.Pattern.DOTALL).matcher(json);
        if (!m.find()) return List.of();
        String arrayContent = m.group(1).trim();
        if (arrayContent.isEmpty()) return List.of();
        return Arrays.stream(arrayContent.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"))
                .map(s -> s.trim().replaceAll("^\"|\"$", ""))
                .filter(s -> !s.isBlank())
                .toList();
    }
}

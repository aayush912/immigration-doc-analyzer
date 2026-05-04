# ImmigAI — AI-Powered Immigration Document Analyzer

Upload or paste any USCIS letter, visa decision, RFE, or immigration form and get a clear, plain-language explanation powered by Claude.

## Features

- **Multi-format support** — PDF, DOCX, and plain text
- **Document type detection** — Visa decisions, RFEs, I-485, I-130, I-765, NMSBA
- **Plain-language explanations** — No legal jargon
- **Multi-language output** — English, Spanish, Chinese, Hindi, Arabic, Portuguese, French
- **Key dates & required actions** — Never miss a deadline
- **Urgency classification** — LOW / MEDIUM / HIGH / CRITICAL

## Tech Stack

| Layer    | Technology                          |
|----------|-------------------------------------|
| Backend  | Java 21, Spring Boot 3.2, Anthropic Java SDK |
| AI       | Claude (`claude-sonnet-4-5`)        |
| Parsing  | Apache PDFBox, Apache POI (DOCX)    |
| Frontend | React 18, Vite, CSS Modules         |

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.9+
- Node.js 20+
- An [Anthropic API key](https://console.anthropic.com/)

### Backend

```bash
cd backend
export ANTHROPIC_API_KEY=your_key_here
./mvnw spring-boot:run
# Runs on http://localhost:8080
```

### Frontend

```bash
cd frontend
npm install
npm run dev
# Runs on http://localhost:5173
```

## API Reference

### `POST /api/v1/documents/analyze/text`
```json
{
  "documentText": "<full document text>",
  "documentType": "RFE",
  "preferredLanguage": "en"
}
```

### `POST /api/v1/documents/analyze/upload`
`multipart/form-data` with fields: `file`, `documentType`, `preferredLanguage`

### Response
```json
{
  "documentType": "Request for Evidence (RFE)",
  "summary": "...",
  "plainLanguageExplanation": "...",
  "keyDates": ["..."],
  "requiredActions": ["..."],
  "missingDocuments": ["..."],
  "urgencyLevel": "HIGH",
  "disclaimer": "...",
  "processingTimeMs": 2341
}
```

## Disclaimer

ImmigAI is for **informational purposes only** and does not constitute legal advice. Always consult a licensed immigration attorney for guidance specific to your situation.

## License

MIT

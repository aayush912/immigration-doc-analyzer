import styles from './AnalysisResult.module.css'

const URGENCY_CONFIG = {
  LOW:      { label: 'Low Urgency',      color: '#057a55', bg: '#f0fdf4' },
  MEDIUM:   { label: 'Medium Urgency',   color: '#c27803', bg: '#fffbeb' },
  HIGH:     { label: 'High Urgency',     color: '#c81e1e', bg: '#fef2f2' },
  CRITICAL: { label: 'Critical — Act Now', color: '#7f1d1d', bg: '#fde8e8' },
}

export default function AnalysisResult({ result }) {
  const urgency = URGENCY_CONFIG[result.urgencyLevel] || URGENCY_CONFIG.MEDIUM

  return (
    <div className={styles.wrapper} id="result">
      <div className={styles.header}>
        <div>
          <span className={styles.docType}>{result.documentType}</span>
          <h2 className={styles.title}>Analysis Complete</h2>
        </div>
        <span
          className={styles.urgencyBadge}
          style={{ color: urgency.color, background: urgency.bg }}
        >
          {urgency.label}
        </span>
      </div>

      <Section title="Summary">
        <p className={styles.prose}>{result.summary}</p>
      </Section>

      <Section title="What This Means For You">
        <p className={styles.prose}>{result.plainLanguageExplanation}</p>
      </Section>

      {result.requiredActions?.length > 0 && (
        <Section title="Required Actions">
          <ul className={styles.list}>
            {result.requiredActions.map((action, i) => (
              <li key={i} className={styles.actionItem}>{action}</li>
            ))}
          </ul>
        </Section>
      )}

      {result.keyDates?.length > 0 && (
        <Section title="Key Dates & Deadlines">
          <ul className={styles.list}>
            {result.keyDates.map((date, i) => (
              <li key={i} className={styles.dateItem}>{date}</li>
            ))}
          </ul>
        </Section>
      )}

      {result.missingDocuments?.length > 0 && (
        <Section title="Missing / Required Documents">
          <ul className={styles.list}>
            {result.missingDocuments.map((doc, i) => (
              <li key={i} className={styles.docItem}>{doc}</li>
            ))}
          </ul>
        </Section>
      )}

      <div className={styles.disclaimer} id="disclaimer">
        <strong>Legal Disclaimer:</strong> {result.disclaimer}
      </div>

      <div className={styles.meta}>
        Analysis completed in {(result.processingTimeMs / 1000).toFixed(1)}s &mdash; powered by Claude AI
      </div>
    </div>
  )
}

function Section({ title, children }) {
  return (
    <div className={styles.section}>
      <h3 className={styles.sectionTitle}>{title}</h3>
      {children}
    </div>
  )
}

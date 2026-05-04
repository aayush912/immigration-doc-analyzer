import { useState } from 'react'
import { Toaster } from 'react-hot-toast'
import Header from './components/Header'
import DocumentUpload from './components/DocumentUpload'
import AnalysisResult from './components/AnalysisResult'
import styles from './App.module.css'

export default function App() {
  const [analysisResult, setAnalysisResult] = useState(null)
  const [isLoading, setIsLoading] = useState(false)

  return (
    <div className={styles.app}>
      <Toaster position="top-right" />
      <Header />
      <main className={styles.main}>
        <div className={styles.container}>
          <div className={styles.hero}>
            <h1 className={styles.title}>Understand Your Immigration Documents</h1>
            <p className={styles.subtitle}>
              Upload any USCIS letter, visa decision, RFE, or immigration form and get a
              clear, plain-language explanation in seconds.
            </p>
          </div>

          <DocumentUpload
            onResult={setAnalysisResult}
            onLoading={setIsLoading}
            isLoading={isLoading}
          />

          {analysisResult && !isLoading && (
            <AnalysisResult result={analysisResult} />
          )}
        </div>
      </main>
      <footer className={styles.footer}>
        <p>
          ImmigAI is for informational purposes only and does not constitute legal advice.
          Always consult a licensed immigration attorney.
        </p>
      </footer>
    </div>
  )
}

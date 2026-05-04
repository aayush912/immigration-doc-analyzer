import { useState, useCallback } from 'react'
import { useDropzone } from 'react-dropzone'
import axios from 'axios'
import toast from 'react-hot-toast'
import styles from './DocumentUpload.module.css'

const DOCUMENT_TYPES = [
  { value: 'UNKNOWN', label: 'Auto-detect' },
  { value: 'VISA_DECISION_LETTER', label: 'Visa Decision Letter' },
  { value: 'RFE', label: 'Request for Evidence (RFE)' },
  { value: 'NMSBA', label: 'NMSBA Form' },
  { value: 'I485', label: 'Form I-485' },
  { value: 'I130', label: 'Form I-130' },
  { value: 'I765', label: 'Form I-765' },
]

const LANGUAGES = [
  { value: 'en', label: 'English' },
  { value: 'es', label: 'Spanish' },
  { value: 'zh', label: 'Chinese (Simplified)' },
  { value: 'hi', label: 'Hindi' },
  { value: 'ar', label: 'Arabic' },
  { value: 'pt', label: 'Portuguese' },
  { value: 'fr', label: 'French' },
]

export default function DocumentUpload({ onResult, onLoading, isLoading }) {
  const [mode, setMode] = useState('upload') // 'upload' | 'paste'
  const [pastedText, setPastedText] = useState('')
  const [docType, setDocType] = useState('UNKNOWN')
  const [language, setLanguage] = useState('en')
  const [uploadedFile, setUploadedFile] = useState(null)

  const onDrop = useCallback((accepted) => {
    if (accepted.length > 0) setUploadedFile(accepted[0])
  }, [])

  const { getRootProps, getInputProps, isDragActive } = useDropzone({
    onDrop,
    accept: {
      'application/pdf': ['.pdf'],
      'application/vnd.openxmlformats-officedocument.wordprocessingml.document': ['.docx'],
      'text/plain': ['.txt'],
    },
    maxFiles: 1,
    maxSize: 20 * 1024 * 1024,
    onDropRejected: () => toast.error('File must be PDF, DOCX, or TXT and under 20 MB'),
  })

  const handleSubmit = async (e) => {
    e.preventDefault()
    if (mode === 'upload' && !uploadedFile) return toast.error('Please select a file')
    if (mode === 'paste' && !pastedText.trim()) return toast.error('Please paste document text')

    onLoading(true)
    onResult(null)

    try {
      let response
      if (mode === 'upload') {
        const formData = new FormData()
        formData.append('file', uploadedFile)
        formData.append('documentType', docType)
        formData.append('preferredLanguage', language)
        response = await axios.post('/api/v1/documents/analyze/upload', formData)
      } else {
        response = await axios.post('/api/v1/documents/analyze/text', {
          documentText: pastedText,
          documentType: docType,
          preferredLanguage: language,
        })
      }
      onResult(response.data)
      toast.success('Analysis complete')
    } catch (err) {
      const msg = err.response?.data?.message || 'Analysis failed. Please try again.'
      toast.error(msg)
    } finally {
      onLoading(false)
    }
  }

  return (
    <form className={styles.card} onSubmit={handleSubmit}>
      <div className={styles.modeToggle}>
        <button
          type="button"
          className={mode === 'upload' ? styles.activeTab : styles.tab}
          onClick={() => setMode('upload')}
        >
          Upload File
        </button>
        <button
          type="button"
          className={mode === 'paste' ? styles.activeTab : styles.tab}
          onClick={() => setMode('paste')}
        >
          Paste Text
        </button>
      </div>

      {mode === 'upload' ? (
        <div {...getRootProps()} className={`${styles.dropzone} ${isDragActive ? styles.dragActive : ''} ${uploadedFile ? styles.hasFile : ''}`}>
          <input {...getInputProps()} />
          {uploadedFile ? (
            <div className={styles.fileInfo}>
              <span className={styles.fileIcon}>📄</span>
              <span className={styles.fileName}>{uploadedFile.name}</span>
              <button type="button" className={styles.clearBtn} onClick={(e) => { e.stopPropagation(); setUploadedFile(null) }}>
                Remove
              </button>
            </div>
          ) : (
            <div className={styles.dropPrompt}>
              <span className={styles.dropIcon}>⬆</span>
              <p><strong>Drag & drop</strong> your document here, or <strong>click to browse</strong></p>
              <p className={styles.dropHint}>PDF, DOCX, or TXT — max 20 MB</p>
            </div>
          )}
        </div>
      ) : (
        <textarea
          className={styles.textarea}
          placeholder="Paste the full text of your immigration document here..."
          value={pastedText}
          onChange={(e) => setPastedText(e.target.value)}
          rows={10}
        />
      )}

      <div className={styles.options}>
        <label className={styles.label}>
          Document Type
          <select className={styles.select} value={docType} onChange={(e) => setDocType(e.target.value)}>
            {DOCUMENT_TYPES.map((t) => (
              <option key={t.value} value={t.value}>{t.label}</option>
            ))}
          </select>
        </label>

        <label className={styles.label}>
          Explanation Language
          <select className={styles.select} value={language} onChange={(e) => setLanguage(e.target.value)}>
            {LANGUAGES.map((l) => (
              <option key={l.value} value={l.value}>{l.label}</option>
            ))}
          </select>
        </label>
      </div>

      <button type="submit" className={styles.submitBtn} disabled={isLoading}>
        {isLoading ? (
          <span className={styles.spinner}>Analyzing...</span>
        ) : (
          'Analyze Document'
        )}
      </button>
    </form>
  )
}

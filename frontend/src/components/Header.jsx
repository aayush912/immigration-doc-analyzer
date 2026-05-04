import styles from './Header.module.css'

export default function Header() {
  return (
    <header className={styles.header}>
      <div className={styles.inner}>
        <div className={styles.brand}>
          <span className={styles.logo}>ImmigAI</span>
          <span className={styles.tagline}>Legal Document Analyzer</span>
        </div>
        <nav className={styles.nav}>
          <a href="https://github.com/aayush912/immigration-doc-analyzer" target="_blank" rel="noreferrer">
            GitHub
          </a>
          <a href="#disclaimer">Disclaimer</a>
        </nav>
      </div>
    </header>
  )
}

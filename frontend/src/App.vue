<template>
  <router-view />
</template>

<style>
@import url('https://fonts.googleapis.com/css2?family=Fira+Sans:wght@300;400;500;600;700&display=swap');

:root {
  --color-primary: #2563eb;
  --color-primary-light: #3b82f6;
  --color-primary-lighter: #60a5fa;
  --color-primary-dark: #1d4ed8;
  --color-on-primary: #ffffff;
  --color-secondary: #3b82f6;
  --color-accent: #059669;
  --color-accent-light: #10b981;
  --color-background: #f8fafc;
  --color-foreground: #0f172a;
  --color-muted: #f1f5f9;
  --color-border: #e4ecfc;
  --color-destructive: #dc2626;
  --color-warning: #d97706;
  --color-info: #0284c7;
  --color-success: #059669;
  --color-text-primary: #1e293b;
  --color-text-secondary: #64748b;
  --color-text-muted: #94a3b8;
  --color-ring: #2563eb;
  --radius-xs: 4px;
  --radius-sm: 6px;
  --radius-md: 8px;
  --radius-lg: 12px;
  --radius-xl: 16px;
  --radius-2xl: 20px;
  --shadow-xs: 0 1px 2px rgba(0, 0, 0, 0.04);
  --shadow-sm: 0 1px 3px rgba(0, 0, 0, 0.06), 0 1px 2px rgba(0, 0, 0, 0.04);
  --shadow-md: 0 4px 6px -1px rgba(0, 0, 0, 0.07), 0 2px 4px -2px rgba(0, 0, 0, 0.05);
  --shadow-lg: 0 10px 15px -3px rgba(0, 0, 0, 0.08), 0 4px 6px -4px rgba(0, 0, 0, 0.04);
  --shadow-xl: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.04);
  --transition-fast: 150ms ease;
  --transition-normal: 200ms ease;
  --transition-slow: 300ms ease;
  --font-sans: 'Fira Sans', 'PingFang SC', 'Microsoft YaHei', system-ui, sans-serif;
  --font-mono: 'Fira Code', 'Cascadia Code', monospace;
  --z-dropdown: 10;
  --z-sticky: 20;
  --z-fixed: 40;
  --z-modal-backdrop: 100;
  --z-modal: 110;
  --z-popover: 120;
  --z-tooltip: 130;
}

* { margin: 0; padding: 0; box-sizing: border-box; }

html, body, #app {
  width: 100%;
  height: 100%;
  font-family: var(--font-sans);
  color: var(--color-text-primary);
  background-color: var(--color-background);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  line-height: 1.5;
  font-size: 14px;
}

:focus-visible {
  outline: 2px solid var(--color-ring);
  outline-offset: 2px;
}

.page-container { padding: 0; }

.el-card {
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  box-shadow: var(--shadow-sm);
  transition: box-shadow var(--transition-normal), transform var(--transition-normal);
}

.el-card:hover { box-shadow: var(--shadow-md); }

.el-card__header {
  font-weight: 600;
  font-size: 15px;
  color: var(--color-text-primary);
  border-bottom: 1px solid var(--color-border);
  padding: 16px 20px;
}

.el-card__body { padding: 20px; }

.el-button {
  border-radius: var(--radius-md);
  font-weight: 500;
  transition: all var(--transition-fast);
}

.el-button--primary {
  --el-button-bg-color: var(--color-primary);
  --el-button-border-color: var(--color-primary);
  --el-button-hover-bg-color: var(--color-primary-light);
  --el-button-hover-border-color: var(--color-primary-light);
  --el-button-active-bg-color: var(--color-primary-dark);
  --el-button-active-border-color: var(--color-primary-dark);
}

.el-button--success {
  --el-button-bg-color: var(--color-accent);
  --el-button-border-color: var(--color-accent);
  --el-button-hover-bg-color: var(--color-accent-light);
  --el-button-hover-border-color: var(--color-accent-light);
}

.el-button--danger {
  --el-button-bg-color: var(--color-destructive);
  --el-button-border-color: var(--color-destructive);
}

.el-button--warning {
  --el-button-bg-color: var(--color-warning);
  --el-button-border-color: var(--color-warning);
}

.el-table {
  --el-table-border-color: var(--color-border);
  --el-table-header-bg-color: #f8fafc;
  --el-table-header-text-color: var(--color-text-secondary);
  --el-table-row-hover-bg-color: #eff6ff;
  border-radius: var(--radius-md);
  overflow: hidden;
}

.el-table th.el-table__cell {
  font-weight: 600;
  font-size: 13px;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  padding: 12px 0;
}

.el-table td.el-table__cell {
  font-size: 14px;
  color: var(--color-text-primary);
  padding: 10px 0;
}

.el-dialog {
  border-radius: var(--radius-2xl) !important;
  overflow: hidden;
}

.el-dialog__header {
  padding: 20px 24px 16px;
  border-bottom: 1px solid var(--color-border);
}

.el-dialog__title {
  font-weight: 600;
  font-size: 16px;
  color: var(--color-text-primary);
}

.el-dialog__body { padding: 20px 24px; }

.el-dialog__footer {
  padding: 16px 24px 20px;
  border-top: 1px solid var(--color-border);
}

.el-form-item__label {
  font-weight: 500;
  color: var(--color-text-secondary);
  font-size: 14px;
}

.el-input__wrapper {
  border-radius: var(--radius-md);
  transition: box-shadow var(--transition-fast), border-color var(--transition-fast);
}

.el-input__wrapper.is-focus {
  box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.15) !important;
}

.el-select__wrapper { border-radius: var(--radius-md); }

.el-tag {
  border-radius: var(--radius-sm);
  font-weight: 500;
  font-size: 12px;
}

.el-pagination {
  --el-pagination-button-bg-color: transparent;
  font-weight: 500;
}

.el-empty__description p {
  color: var(--color-text-muted);
  font-size: 14px;
}

.el-message-box { border-radius: var(--radius-xl) !important; }

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
}

.title-icon {
  display: flex;
  align-items: center;
}

.header-btns {
  display: flex;
  gap: 8px;
}

.search-bar {
  display: flex;
  gap: 12px;
  margin-bottom: 16px;
  flex-wrap: wrap;
  align-items: center;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  margin-top: 16px;
}

.stat-cards .stat-card {
  border-radius: var(--radius-lg);
  border: 1px solid var(--color-border);
  transition: all var(--transition-normal);
  cursor: default;
}

.stat-cards .stat-card:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.stat-cards .stat-card :deep(.el-card__body) { padding: 20px; }

.stat-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.stat-label {
  font-size: 13px;
  color: var(--color-text-secondary);
  margin-bottom: 4px;
  font-weight: 500;
  letter-spacing: 0.3px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--color-text-primary);
  font-variant-numeric: tabular-nums;
  line-height: 1.2;
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: var(--radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--color-on-primary);
  flex-shrink: 0;
}

@media (prefers-reduced-motion: reduce) {
  *, *::before, *::after {
    animation-duration: 0.01ms !important;
    animation-iteration-count: 1 !important;
    transition-duration: 0.01ms !important;
  }
}
</style>

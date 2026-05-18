import type { Component } from 'vue'

export type TableActionButtonType = 'primary' | 'success' | 'warning' | 'danger' | 'info'
export type TableActionConfirmType = 'success' | 'info' | 'warning' | 'error'

export interface TableActionItem {
  key?: string | number
  label: string
  icon?: Component
  type?: TableActionButtonType
  visible?: boolean
  disabled?: boolean
  confirmText?: string
  confirmTitle?: string
  confirmType?: TableActionConfirmType
  onClick: () => void | Promise<void>
}

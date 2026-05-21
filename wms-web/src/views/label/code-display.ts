import type { ElectronicLabelVo } from '@/types/label'

export const LABEL_TYPE_QR = 1
export const LABEL_TYPE_BARCODE = 2
export const LABEL_TYPE_RFID = 3

export const LABEL_CODE_UNSUPPORTED_MESSAGE = '暂不支持该标签类型的编码展示'
export const LABEL_CODE_EMPTY_MESSAGE = '当前标签缺少可渲染的编码内容'

export type LabelCodeDisplayMode = 'render' | 'text' | 'error'
export type LabelCodeDisplayType = 'qr' | 'barcode' | null

export interface LabelCodeDisplayResult {
  mode: LabelCodeDisplayMode
  codeType: LabelCodeDisplayType
  codeValue: string
  textValue: string
  errorMessage: string
}

/**
 * 统一解析标签详情与打印预览中的编码展示模式，确保多场景输出一致。
 */
export function resolveLabelCodeDisplay(label: ElectronicLabelVo): LabelCodeDisplayResult {
  if (label.labelType === LABEL_TYPE_QR) {
    const codeValue = label.qrContent?.trim() || label.labelNo?.trim() || ''
    if (!codeValue) {
      return createErrorResult(LABEL_CODE_EMPTY_MESSAGE)
    }

    return {
      mode: 'render',
      codeType: 'qr',
      codeValue,
      textValue: '',
      errorMessage: '',
    }
  }

  if (label.labelType === LABEL_TYPE_BARCODE) {
    const codeValue = label.barcodeContent?.trim() || label.labelNo?.trim() || ''
    if (!codeValue) {
      return createErrorResult(LABEL_CODE_EMPTY_MESSAGE)
    }

    return {
      mode: 'render',
      codeType: 'barcode',
      codeValue,
      textValue: '',
      errorMessage: '',
    }
  }

  if (label.labelType === LABEL_TYPE_RFID) {
    const textValue = label.rfidCode?.trim() || label.labelNo?.trim() || ''
    if (!textValue) {
      return createErrorResult(LABEL_CODE_EMPTY_MESSAGE)
    }

    return {
      mode: 'text',
      codeType: null,
      codeValue: '',
      textValue,
      errorMessage: '',
    }
  }

  return createErrorResult(LABEL_CODE_UNSUPPORTED_MESSAGE)
}

function createErrorResult(errorMessage: string): LabelCodeDisplayResult {
  return {
    mode: 'error',
    codeType: null,
    codeValue: '',
    textValue: '',
    errorMessage,
  }
}

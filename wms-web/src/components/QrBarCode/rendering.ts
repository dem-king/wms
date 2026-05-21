export type CodeRenderType = 'qr' | 'barcode'

export interface BuildRenderableCodeInput {
  type: CodeRenderType
  value?: string | null
}

export interface BuildRenderableCodeResult {
  type: CodeRenderType
  value: string
  errorMessage: string
  showReadableText: boolean
}

export const EMPTY_QR_CODE_MESSAGE = '编码内容为空，无法生成二维码'
export const EMPTY_BARCODE_MESSAGE = '编码内容为空，无法生成条形码'

/**
 * 统一处理编码内容的空值与展示元数据，避免页面各处重复兜底。
 */
export function buildRenderableCode(input: BuildRenderableCodeInput): BuildRenderableCodeResult {
  const value = input.value?.trim() ?? ''
  const showReadableText = input.type === 'barcode'

  if (!value) {
    return {
      type: input.type,
      value: '',
      errorMessage: input.type === 'qr' ? EMPTY_QR_CODE_MESSAGE : EMPTY_BARCODE_MESSAGE,
      showReadableText,
    }
  }

  return {
    type: input.type,
    value,
    errorMessage: '',
    showReadableText,
  }
}

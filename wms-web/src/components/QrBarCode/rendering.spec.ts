import { describe, expect, it } from 'vitest'
import { buildRenderableCode } from './rendering'

describe('qr bar code rendering helper', () => {
  it('returns an explicit error state when content is blank', () => {
    expect(buildRenderableCode({ type: 'qr', value: '   ' })).toEqual({
      type: 'qr',
      value: '',
      errorMessage: '编码内容为空，无法生成二维码',
      showReadableText: false,
    })
  })

  it('keeps barcode content and enables readable text', () => {
    expect(buildRenderableCode({ type: 'barcode', value: 'WB-2026-001' })).toEqual({
      type: 'barcode',
      value: 'WB-2026-001',
      errorMessage: '',
      showReadableText: true,
    })
  })
})

import { describe, expect, it } from 'vitest'
import {
  createPrintBlockedFeedback,
  createPrintErrorFeedback,
  createPrintSuccessFeedback,
} from './print-feedback'

describe('label print feedback helpers', () => {
  it('creates a success summary after printing', () => {
    expect(createPrintSuccessFeedback(4)).toEqual({
      type: 'success',
      message: '打印任务已发送，共 4 个标签',
    })
  })

  it('creates a blocked summary when invalid labels exist', () => {
    expect(createPrintBlockedFeedback(2)).toEqual({
      type: 'warning',
      message: '有 2 个标签缺少可打印编码，请先修正后再打印',
    })
  })

  it('falls back to a generic error message when printing fails', () => {
    expect(createPrintErrorFeedback()).toEqual({
      type: 'error',
      message: '打印失败，请检查浏览器打印设置后重试',
    })
  })
})

export interface PrintFeedback {
  type: 'success' | 'warning' | 'error'
  message: string
}

export function createPrintSuccessFeedback(count: number): PrintFeedback {
  return {
    type: 'success',
    message: `打印任务已发送，共 ${count} 个标签`,
  }
}

export function createPrintBlockedFeedback(count: number): PrintFeedback {
  return {
    type: 'warning',
    message: `有 ${count} 个标签缺少可打印编码，请先修正后再打印`,
  }
}

export function createPrintErrorFeedback(message?: string): PrintFeedback {
  return {
    type: 'error',
    message: message || '打印失败，请检查浏览器打印设置后重试',
  }
}

import type { LabelBatchPrintDto } from '@/types/label'
import type { PrintFeedback } from './print-feedback'

export interface PrintStatusSyncOptions {
  syncLabels: (data: LabelBatchPrintDto) => Promise<unknown>
  onSuccess?: () => void | Promise<void>
}

export async function syncPrintedLabelStatus(
  labelIds: string[],
  options: PrintStatusSyncOptions,
): Promise<PrintFeedback> {
  const { syncLabels, onSuccess } = options

  try {
    await syncLabels({ labelIds })
    await onSuccess?.()

    return {
      type: 'success',
      message: '打印状态同步成功',
    }
  } catch {
    return {
      type: 'error',
      message: '打印状态同步失败，请稍后重试',
    }
  }
}

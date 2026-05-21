import { describe, expect, it, vi } from 'vitest'
import { syncPrintedLabelStatus } from './print-status-sync'

describe('print status sync', () => {
  it('returns success feedback and refreshes data when batch sync succeeds', async () => {
    const syncLabels = vi.fn().mockResolvedValue(undefined)
    const refresh = vi.fn()

    await expect(
      syncPrintedLabelStatus([101, 102], {
        syncLabels,
        onSuccess: refresh,
      }),
    ).resolves.toEqual({
      type: 'success',
      message: '打印状态同步成功',
    })

    expect(syncLabels).toHaveBeenCalledWith({ labelIds: [101, 102] })
    expect(refresh).toHaveBeenCalledTimes(1)
  })

  it('returns sync failure feedback without throwing when batch sync fails', async () => {
    const syncLabels = vi.fn().mockRejectedValue(new Error('sync failed'))
    const refresh = vi.fn()

    await expect(
      syncPrintedLabelStatus([201], {
        syncLabels,
        onSuccess: refresh,
      }),
    ).resolves.toEqual({
      type: 'error',
      message: '打印状态同步失败，请稍后重试',
    })

    expect(syncLabels).toHaveBeenCalledWith({ labelIds: [201] })
    expect(refresh).not.toHaveBeenCalled()
  })
})

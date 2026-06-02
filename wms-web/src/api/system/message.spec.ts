import { describe, expect, it, vi } from 'vitest'

const requestMocks = vi.hoisted(() => ({
  get: vi.fn(),
  put: vi.fn(),
}))

vi.mock('../request', () => requestMocks)

import {
  MESSAGE_READ_STATUS_UNREAD,
  getMessagePage,
  getUnreadMessageCount,
  markAllMessagesRead,
  markMessageRead,
} from './message'

describe('message api', () => {
  it('requests current user messages with filters', () => {
    const params = { page: 1, size: 10, readStatus: MESSAGE_READ_STATUS_UNREAD }

    getMessagePage(params)

    expect(requestMocks.get).toHaveBeenCalledWith('/system/messages', params)
  })

  it('requests unread message count', () => {
    getUnreadMessageCount()

    expect(requestMocks.get).toHaveBeenCalledWith('/system/messages/unread-count')
  })

  it('marks one message as read', () => {
    markMessageRead('1001')

    expect(requestMocks.put).toHaveBeenCalledWith('/system/messages/1001/read')
  })

  it('marks all messages as read', () => {
    markAllMessagesRead()

    expect(requestMocks.put).toHaveBeenCalledWith('/system/messages/read-all')
  })
})

import { get, put } from '../request'
import type { EntityId, PageResult, SysMessageQuery, SysMessageVo } from '@/types/system'

export const MESSAGE_READ_STATUS_UNREAD = 0 as const
export const MESSAGE_READ_STATUS_READ = 1 as const

export function getMessagePage(params?: SysMessageQuery) {
  return get<PageResult<SysMessageVo>>('/system/messages', params)
}

export function getUnreadMessageCount() {
  return get<number>('/system/messages/unread-count')
}

export function markMessageRead(id: EntityId) {
  return put<void>(`/system/messages/${id}/read`)
}

export function markAllMessagesRead() {
  return put<void>('/system/messages/read-all')
}

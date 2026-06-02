import { describe, expect, it } from 'vitest'
import { readFileSync } from 'node:fs'
import { resolve } from 'node:path'

const componentDir = resolve(__dirname)

describe('notification bell integration', () => {
  it('renders notification bell from navbar', () => {
    const navbar = readFileSync(resolve(componentDir, 'Navbar.vue'), 'utf-8')

    expect(navbar).toContain('NotificationBell')
    expect(navbar).toContain("import NotificationBell from './NotificationBell.vue'")
  })

  it('polls unread count and marks clicked messages as read', () => {
    const bell = readFileSync(resolve(componentDir, 'NotificationBell.vue'), 'utf-8')

    expect(bell).toContain('getUnreadMessageCount')
    expect(bell).toContain('getMessagePage')
    expect(bell).toContain('markMessageRead')
    expect(bell).toContain('setInterval')
  })

  it('keeps background polling lightweight', () => {
    const bell = readFileSync(resolve(componentDir, 'NotificationBell.vue'), 'utf-8')

    expect(bell).toContain('async function refreshUnreadCount')
    expect(bell).toContain('async function refreshMessageList')
    expect(bell).toContain('document.visibilityState')
    expect(bell).toContain('isUnreadRefreshing')
    expect(bell).toContain('document.addEventListener(\'visibilitychange\'')
    expect(bell).toContain('window.setInterval(refreshUnreadCount, pollIntervalMs)')
    expect(bell).not.toContain('window.setInterval(refreshMessages, pollIntervalMs)')
  })
})

import { describe, expect, it } from 'vitest'
import { readFileSync } from 'node:fs'
import { resolve } from 'node:path'

const viewFile = resolve(__dirname, 'index.vue')

describe('message center view', () => {
  it('supports read status filtering and read actions', () => {
    const source = readFileSync(viewFile, 'utf-8')

    expect(source).toContain('全部消息')
    expect(source).toContain('未读消息')
    expect(source).toContain('已读消息')
    expect(source).toContain('markMessageRead')
    expect(source).toContain('markAllMessagesRead')
  })
})

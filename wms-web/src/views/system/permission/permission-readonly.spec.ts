import { readFileSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { describe, expect, it } from 'vitest'

const source = readFileSync(path.resolve(path.dirname(fileURLToPath(import.meta.url)), 'index.vue'), 'utf-8')

describe('system permission readonly page', () => {
  it('hides mutation actions and exposes detail viewing', () => {
    expect(source).not.toContain('addPermission')
    expect(source).not.toContain('updatePermission')
    expect(source).not.toContain('deletePermission')
    expect(source).not.toContain('system:perm:add')
    expect(source).not.toContain('system:perm:edit')
    expect(source).not.toContain('system:perm:delete')
    expect(source).toContain('handleViewDetail')
    expect(source).toContain('detailVisible')
    expect(source).toContain('权限详情')
  })
})

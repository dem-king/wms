import { readFileSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { describe, expect, it } from 'vitest'

const currentDir = path.dirname(fileURLToPath(import.meta.url))
const rolePageFile = path.resolve(currentDir, 'index.vue')

describe('role list page', () => {
  it('supports both array and paged role list responses', () => {
    const source = readFileSync(rolePageFile, 'utf-8')

    expect(source).toContain('Array.isArray(res.data)')
    expect(source).toContain('res.data.records')
    expect(source).toContain('roles.length : res.data.total')
  })
})

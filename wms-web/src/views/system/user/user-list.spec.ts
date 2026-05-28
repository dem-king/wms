import { readFileSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { describe, expect, it } from 'vitest'

const currentDir = path.dirname(fileURLToPath(import.meta.url))
const userPageFile = path.resolve(currentDir, 'index.vue')

describe('user list page', () => {
  it('uses the fixed pagination layout and normalizes page totals', () => {
    const source = readFileSync(userPageFile, 'utf-8')

    expect(source).toContain('class="app-container list-page"')
    expect(source).toContain('class="table-section"')
    expect(source).toContain('class="pagination-container"')
    expect(source).toContain('normalizePageTotal(res.data.total)')
    expect(source).toContain('flex-direction: column')
    expect(source).toContain('min-height: 0')
  })
})

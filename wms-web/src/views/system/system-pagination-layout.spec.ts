import { readFileSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { describe, expect, it } from 'vitest'

const currentDir = path.dirname(fileURLToPath(import.meta.url))

type PageExpectation = {
  relativePath: string
  totalPattern: string
}

const pages: PageExpectation[] = [
  { relativePath: 'supplier/index.vue', totalPattern: 'normalizePageTotal(res.data.total)' },
  { relativePath: 'role/index.vue', totalPattern: 'normalizePageTotal(res.data.total)' },
  { relativePath: 'permission/index.vue', totalPattern: 'normalizePageTotal(res.data.total)' },
  { relativePath: 'config/index.vue', totalPattern: 'normalizePageTotal(res.data.total)' },
]

describe('system pagination layout', () => {
  it('uses fixed pagination layout and normalized totals on paged system pages', () => {
    const invalidPages = pages.filter(({ relativePath, totalPattern }) => {
      const source = readFileSync(path.resolve(currentDir, relativePath), 'utf-8')

      return !source.includes('class="app-container list-page"')
        || !source.includes('class="table-section"')
        || !source.includes('class="pagination-container"')
        || !source.includes(totalPattern)
        || !source.includes('flex-direction: column')
        || !source.includes('min-height: 0')
    })

    expect(invalidPages).toEqual([])
  })
})

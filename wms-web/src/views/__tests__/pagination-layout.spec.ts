import { readFileSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { describe, expect, it } from 'vitest'

const currentDir = path.dirname(fileURLToPath(import.meta.url))
const viewsRoot = path.resolve(currentDir, '..')

const listPages = [
  'item/list/index.vue',
  'item/machineSpare/index.vue',
  'item/tag/index.vue',
  'business/inbound/index.vue',
  'business/outbound/index.vue',
  'business/return/index.vue',
  'business/scrap/index.vue',
  'business/transfer/index.vue',
  'log/login/index.vue',
  'log/operation/index.vue',
]

describe('pagination layout', () => {
  it('uses fixed pagination layout on paged list views', () => {
    const invalidPages = listPages.filter((relativePath) => {
      const source = readFileSync(path.resolve(viewsRoot, relativePath), 'utf-8')

      return !source.includes('class="app-container list-page"')
        || !source.includes('class="table-section"')
        || !source.includes('class="pagination"')
        || !source.includes('flex-direction: column')
        || !source.includes('min-height: 0')
    })

    expect(invalidPages).toEqual([])
  })

  it('keeps category detail panel paginated and scrollable', () => {
    const source = readFileSync(path.resolve(viewsRoot, 'item/category/index.vue'), 'utf-8')

    expect(source).toContain('class="category-detail-panel"')
    expect(source).toContain('class="table-section"')
    expect(source).toContain('class="pagination-container"')
    expect(source).toContain('min-height: 0')
  })
})

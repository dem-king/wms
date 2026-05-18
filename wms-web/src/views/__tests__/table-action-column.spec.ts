import { readdirSync, readFileSync, statSync } from 'node:fs'
import path from 'node:path'
import { describe, expect, it } from 'vitest'

const viewsRoot = path.resolve(__dirname, '..')
const stylesFile = path.resolve(viewsRoot, '..', 'styles', 'index.scss')
const actionColumnTagPattern = /<el-table-column\b[^>]*label="操作"[^>]*>/g

function collectVueFiles(dir: string): string[] {
  return readdirSync(dir).flatMap((entry) => {
    const entryPath = path.join(dir, entry)
    const stats = statSync(entryPath)

    if (stats.isDirectory()) {
      return collectVueFiles(entryPath)
    }

    return entryPath.endsWith('.vue') ? [entryPath] : []
  })
}

describe('table action columns', () => {
  it('marks every 操作 column with the shared action class', () => {
    const filesWithMissingClass = collectVueFiles(viewsRoot)
      .map((filePath) => {
        const source = readFileSync(filePath, 'utf-8')
        const actionColumnTags = source.match(actionColumnTagPattern) ?? []

        if (actionColumnTags.length === 0) {
          return null
        }

        const missingClassTags = actionColumnTags.filter((tag) => !tag.includes('class-name="table-action-column"'))

        return missingClassTags.length > 0 ? path.relative(viewsRoot, filePath) : null
      })
      .filter((filePath): filePath is string => Boolean(filePath))

    expect(filesWithMissingClass).toEqual([])
  })

  it('defines shared nowrap styles for action columns', () => {
    const source = readFileSync(stylesFile, 'utf-8')

    expect(source).toContain('.table-action-column')
    expect(source).toContain('flex-wrap: nowrap')
  })
})

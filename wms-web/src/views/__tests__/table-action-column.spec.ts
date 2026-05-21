import { readdirSync, readFileSync, statSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { describe, expect, it } from 'vitest'

const currentDir = path.dirname(fileURLToPath(import.meta.url))
const viewsRoot = path.resolve(currentDir, '..')
const stylesFile = path.resolve(viewsRoot, '..', 'styles', 'index.scss')
const rolePageFile = path.resolve(viewsRoot, 'system', 'role', 'index.vue')
const userPageFile = path.resolve(viewsRoot, 'system', 'user', 'index.vue')
const actionColumnTagPattern = /<el-table-column\b[^>]*label="操作"[^>]*>/g
const sharedClassPattern = /class-name="[^"]*\btable-action-column\b[^"]*"/
const tableActionGroupPattern = /<TableActionGroup\b/

function collectVueFiles(dir: string): string[] {
  return readdirSync(dir).flatMap((entry: string) => {
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

        const missingClassTags = actionColumnTags.filter((tag: string) => !sharedClassPattern.test(tag))

        return missingClassTags.length > 0 ? path.relative(viewsRoot, filePath) : null
      })
      .filter((filePath): filePath is string => Boolean(filePath))

    expect(filesWithMissingClass).toEqual([])
  })

  it('uses min-width instead of fixed width for action columns', () => {
    const filesWithFixedWidth = collectVueFiles(viewsRoot)
      .map((filePath) => {
        const source = readFileSync(filePath, 'utf-8')
        const actionColumnTags = source.match(actionColumnTagPattern) ?? []

        if (actionColumnTags.length === 0) {
          return null
        }

        const fixedWidthTags = actionColumnTags.filter((tag: string) => tag.includes(' width='))

        return fixedWidthTags.length > 0 ? path.relative(viewsRoot, filePath) : null
      })
      .filter((filePath): filePath is string => Boolean(filePath))

    expect(filesWithFixedWidth).toEqual([])
  })

  it('uses TableActionGroup in every action column', () => {
    const filesWithMissingActionGroup = collectVueFiles(viewsRoot)
      .map((filePath) => {
        const source = readFileSync(filePath, 'utf-8')
        const actionColumnTags = source.match(actionColumnTagPattern) ?? []

        if (actionColumnTags.length === 0) {
          return null
        }

        return tableActionGroupPattern.test(source) ? null : path.relative(viewsRoot, filePath)
      })
      .filter((filePath): filePath is string => Boolean(filePath))

    expect(filesWithMissingActionGroup).toEqual([])
  })

  it('defines shared nowrap styles for action columns', () => {
    const source = readFileSync(stylesFile, 'utf-8')

    expect(source).toContain('.table-action-column')
    expect(source).toContain('flex-wrap: nowrap')
    expect(source).toContain('.table-action-group')
    expect(source).toContain('.table-action-group__more')
  })

  it('sets explicit min-width on role and user action columns with three inline actions', () => {
    const roleSource = readFileSync(rolePageFile, 'utf-8')
    const userSource = readFileSync(userPageFile, 'utf-8')

    expect(roleSource).toMatch(/<el-table-column\b[^>]*label="操作"[^>]*min-width="240"[^>]*>/)
    expect(userSource).toMatch(/<el-table-column\b[^>]*label="操作"[^>]*min-width="240"[^>]*>/)
  })
})

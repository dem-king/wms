import { readFileSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { describe, expect, it } from 'vitest'

const currentDir = path.dirname(fileURLToPath(import.meta.url))
const source = readFileSync(path.resolve(currentDir, 'index.vue'), 'utf-8')

describe('item list display', () => {
  it('labels the inventory column as realtime stock', () => {
    expect(source).toContain('label="实时库存"')
  })

  it('does not fall back to item master stockQty for realtime stock display', () => {
    expect(source).toContain('return row.currentStock ?? 0')
    expect(source).not.toContain('return row.currentStock ?? row.stockQty')
  })
})

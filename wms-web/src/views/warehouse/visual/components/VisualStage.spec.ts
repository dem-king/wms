import { readFileSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { describe, expect, it } from 'vitest'

const currentDir = path.dirname(fileURLToPath(import.meta.url))
const source = readFileSync(path.resolve(currentDir, 'VisualStage.vue'), 'utf-8')

describe('VisualStage Konva tree contract', () => {
  it('does not nest Konva layers inside another layer', () => {
    expect(source).not.toMatch(/<v-layer[^>]*>[\s\S]*<v-layer\b/)
  })
})

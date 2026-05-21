import { readFileSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { describe, expect, it } from 'vitest'

const currentDir = path.dirname(fileURLToPath(import.meta.url))
const source = readFileSync(path.resolve(currentDir, 'index.vue'), 'utf-8')

describe('warehouse visual page shell', () => {
  it('contains workbench component boundaries and stage orchestration markers', () => {
    expect(source).toContain('VisualToolbar')
    expect(source).toContain('VisualStage')
    expect(source).toContain('VisualSummary')
    expect(source).toContain('CabinetDetailDialog')
    expect(source).toContain('viewMode')
    expect(source).toContain('handleQuickLocate')
  })

  it('contains quick locate and cabinet detail interaction markers', () => {
    expect(source).toContain('quickLocateKeyword')
    expect(source).toContain('quickLocateFeedback')
    expect(source).toContain('handleOpenCabinetDetail')
    expect(source).toContain('isCabinetDetailVisible')
  })

  it('contains layout editing orchestration markers', () => {
    expect(source).toContain('layoutEditor')
    expect(source).toContain('isEditMode')
    expect(source).toContain('pendingLayoutCount')
    expect(source).toContain('handleCabinetPositionChange')
    expect(source).toContain('handleSaveLayout')
  })
})

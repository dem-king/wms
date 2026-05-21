import { existsSync, readFileSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { describe, expect, it } from 'vitest'

const currentDir = path.dirname(fileURLToPath(import.meta.url))

describe('warehouse visual api compatibility', () => {
  it('keeps visual queries on dedicated modules and removes the old aggregate api出口', () => {
    const areaSource = readFileSync(path.resolve(currentDir, 'area.ts'), 'utf-8')
    const cabinetSource = readFileSync(path.resolve(currentDir, 'cabinet.ts'), 'utf-8')
    const binSource = readFileSync(path.resolve(currentDir, 'bin.ts'), 'utf-8')
    const legacyApiPath = path.resolve(currentDir, 'index.ts')

    expect(areaSource).toContain('/warehouse/areas/warehouse/')
    expect(cabinetSource).toContain('/warehouse/cabinets/area/')
    expect(binSource).toContain('/warehouse/bins/cabinet/')
    expect(existsSync(legacyApiPath)).toBe(false)
  })

  it('exposes cabinet layout save api on the dedicated cabinet module', () => {
    const cabinetSource = readFileSync(path.resolve(currentDir, 'cabinet.ts'), 'utf-8')

    expect(cabinetSource).toContain('saveCabinetLayout')
    expect(cabinetSource).toContain('/warehouse/cabinets/layout')
  })
})

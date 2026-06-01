import { describe, expect, it } from 'vitest'
import { readFileSync } from 'node:fs'
import path from 'node:path'

const componentRoot = path.resolve(__dirname)

describe('TableActionGroup permission contract', () => {
  it('supports filtering actions by permission code', () => {
    const types = readFileSync(path.resolve(componentRoot, 'types.ts'), 'utf-8')
    const component = readFileSync(path.resolve(componentRoot, 'TableActionGroup.vue'), 'utf-8')

    expect(types).toContain('permission?: string | string[]')
    expect(component).toContain('hasActionPermission')
    expect(component).toContain('userStore.hasPermission')
  })
})

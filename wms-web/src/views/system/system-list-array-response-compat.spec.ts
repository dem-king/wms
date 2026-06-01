import { readFileSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { describe, expect, it } from 'vitest'

const currentDir = path.dirname(fileURLToPath(import.meta.url))
const viewsRoot = currentDir
const apiRoot = path.resolve(currentDir, '..', '..', 'api', 'system')

describe('system list pages', () => {
  it('supports array responses for non-paged system list endpoints', () => {
    const configPage = readFileSync(path.resolve(viewsRoot, 'config', 'index.vue'), 'utf-8')
    const configApi = readFileSync(path.resolve(apiRoot, 'config.ts'), 'utf-8')

    expect(configPage).toContain('Array.isArray(res.data)')
    expect(configApi).toContain('PageResult<SysConfigVo> | SysConfigVo[]')
  })
})

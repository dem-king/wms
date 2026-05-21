import { readFileSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { describe, expect, it } from 'vitest'

const currentDir = path.dirname(fileURLToPath(import.meta.url))
const viewsRoot = currentDir
const apiRoot = path.resolve(currentDir, '..', '..', 'api', 'system')

describe('system list pages', () => {
  it('supports array responses for non-paged system list endpoints', () => {
    const permissionPage = readFileSync(path.resolve(viewsRoot, 'permission', 'index.vue'), 'utf-8')
    const configPage = readFileSync(path.resolve(viewsRoot, 'config', 'index.vue'), 'utf-8')
    const permissionApi = readFileSync(path.resolve(apiRoot, 'permission.ts'), 'utf-8')
    const configApi = readFileSync(path.resolve(apiRoot, 'config.ts'), 'utf-8')

    expect(permissionPage).toContain('Array.isArray(res.data)')
    expect(configPage).toContain('Array.isArray(res.data)')
    expect(permissionApi).toContain('PageResult<SysPermissionVo> | SysPermissionVo[]')
    expect(configApi).toContain('PageResult<SysConfigVo> | SysConfigVo[]')
  })
})

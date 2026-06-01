import { readFileSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { describe, expect, it } from 'vitest'

const currentDir = path.dirname(fileURLToPath(import.meta.url))
const roleView = path.resolve(currentDir, 'index.vue')
const roleApi = path.resolve(currentDir, '..', '..', '..', 'api', 'system', 'role.ts')
const rolePermission = path.resolve(currentDir, 'components', 'RolePermission.vue')

describe('role permission assignment UI', () => {
  it('exposes role permission APIs and a role-page action entry', () => {
    const roleViewContent = readFileSync(roleView, 'utf-8')
    const roleApiContent = readFileSync(roleApi, 'utf-8')
    const rolePermissionContent = readFileSync(rolePermission, 'utf-8')

    expect(roleApiContent).toContain('getRolePermissions')
    expect(roleApiContent).toContain('assignRolePermissions')
    expect(roleApiContent).toContain('/permissions')
    expect(roleViewContent).toContain('RolePermission')
    expect(roleViewContent).toContain('handleAssignPermission')
    expect(roleViewContent).toContain("label: '权限'")
    expect(rolePermissionContent).toContain('getPermissionList')
    expect(rolePermissionContent).toContain('assignRolePermissions')
    expect(rolePermissionContent).toContain('setCheckedKeys')
  })
})

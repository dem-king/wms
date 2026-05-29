import { readFileSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { describe, expect, it } from 'vitest'

const currentDir = path.dirname(fileURLToPath(import.meta.url))
const userPageFile = path.resolve(currentDir, 'index.vue')

describe('user list page', () => {
  it('uses the fixed pagination layout and normalizes page totals', () => {
    const source = readFileSync(userPageFile, 'utf-8')

    expect(source).toContain('class="app-container list-page"')
    expect(source).toContain('class="table-section"')
    expect(source).toContain('class="pagination-container"')
    expect(source).toContain('normalizePageTotal(res.data.total)')
    expect(source).toContain('flex-direction: column')
    expect(source).toContain('min-height: 0')
  })

  it('fills department names from the department tree when the user API only returns deptId', () => {
    const source = readFileSync(userPageFile, 'utf-8')

    expect(source).toContain('Promise.all([getUserList(queryParams), getDeptTree()])')
    expect(source).toContain("deptName: deptNameMap.get(user.deptId) ?? user.deptName ?? ''")
  })

  it('renders a role column and joins role names for display', () => {
    const source = readFileSync(userPageFile, 'utf-8')

    expect(source).toContain('label="角色"')
    expect(source).toContain("roleNamesText: user.roleNames?.join('、') ?? ''")
  })
})

import { readFileSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { describe, expect, it } from 'vitest'

const currentDir = path.dirname(fileURLToPath(import.meta.url))
const userFormFile = path.resolve(currentDir, 'UserForm.vue')

describe('UserForm', () => {
  it('uses a single-role selector and wraps the selected role for submit', () => {
    const source = readFileSync(userFormFile, 'utf-8')

    expect(source).toContain('v-model="form.roleId"')
    expect(source).not.toContain('multiple placeholder="请选择角色"')
    expect(source).toContain('roleIds: form.roleId !== undefined ? [form.roleId] : []')
  })
})

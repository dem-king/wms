import { readFileSync } from 'node:fs'
import path from 'node:path'
import { fileURLToPath } from 'node:url'
import { describe, expect, it } from 'vitest'

const currentDir = path.dirname(fileURLToPath(import.meta.url))
const source = readFileSync(path.resolve(currentDir, 'index.vue'), 'utf-8')

describe('warehouse form fields', () => {
  it('includes phone and remark inputs in the dialog form', () => {
    expect(source).toContain('label="联系电话"')
    expect(source).toContain('v-model="form.phone"')
    expect(source).toContain('label="备注"')
    expect(source).toContain('v-model="form.remark"')
  })

  it('submits phone and remark with warehouse updates', () => {
    expect(source).toContain('phone: form.phone')
    expect(source).toContain('remark: form.remark')
    expect(source).toContain('phone: row.phone')
    expect(source).toContain('remark: row.remark')
  })
})

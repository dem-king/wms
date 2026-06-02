import { describe, expect, it } from 'vitest'
import { readFileSync } from 'node:fs'
import { resolve } from 'node:path'

const componentFile = resolve(__dirname, 'ApprovalOrderPage.vue')

describe('ApprovalOrderPage notification deep link', () => {
  it('opens approval detail when focusApprovalId is present', () => {
    const source = readFileSync(componentFile, 'utf-8')

    expect(source).toContain('focusApprovalId?: number')
    expect(source).toContain('watch(')
    expect(source).toContain('handleViewById')
    expect(source).toContain('getApprovalOrder(approvalId)')
  })
})

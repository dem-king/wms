import { describe, expect, it } from 'vitest'
import { readFileSync } from 'node:fs'
import { resolve } from 'node:path'

const viewFile = resolve(__dirname, 'index.vue')
const approvalOrderPageFile = resolve(__dirname, '..', '..', '..', 'components', 'ApprovalFlow', 'ApprovalOrderPage.vue')

describe('pending approval notification landing', () => {
  it('passes approvalId query to ApprovalOrderPage as focusApprovalId', () => {
    const source = readFileSync(viewFile, 'utf-8')

    expect(source).toContain('useRoute')
    expect(source).toContain('route.query.approvalId')
    expect(source).toContain('focusApprovalId')
    expect(source).toContain(':focus-approval-id="focusApprovalId"')
  })

  it('opens approval detail when focusApprovalId is provided', () => {
    const source = readFileSync(approvalOrderPageFile, 'utf-8')

    expect(source).toContain('focusApprovalId?: number')
    expect(source).toContain('watch(')
    expect(source).toContain('handleViewById')
    expect(source).toContain('getApprovalOrder(approvalId)')
  })
})

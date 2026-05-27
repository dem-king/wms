import { describe, expect, it } from 'vitest'
import { normalizePageTotal } from './pagination'

describe('normalizePageTotal', () => {
  it('converts mixed backend totals to numbers for pagination', () => {
    expect(normalizePageTotal(0)).toBe(0)
    expect(normalizePageTotal(25)).toBe(25)
    expect(normalizePageTotal('977')).toBe(977)
  })

  it('falls back to zero for missing or invalid totals', () => {
    expect(normalizePageTotal(undefined)).toBe(0)
    expect(normalizePageTotal(null)).toBe(0)
    expect(normalizePageTotal('not-a-number')).toBe(0)
  })
})

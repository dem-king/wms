import { describe, expect, it } from 'vitest'
import { buildItemSubmitPayload } from './submit-payload'

describe('buildItemSubmitPayload', () => {
  it('maps item form data to backend ItemDto fields', () => {
    const payload = buildItemSubmitPayload({
      itemCode: 'WP202605290001',
      itemName: '轴承',
      specModel: '6205-ZZ',
      unit: '个',
      categoryId: '1001',
      subCategoryId: '1002',
      tagIds: ['1', '2'],
      binIds: ['2001', '2002'],
      supplierId: '3001',
      stockLowerLimit: 5,
      stockUpperLimit: 20,
      replenishThreshold: 8,
      status: 1,
    })

    expect(payload).toEqual({
      itemName: '轴承',
      model: '6205-ZZ',
      spec: '6205-ZZ',
      unit: '个',
      categoryId: '1001',
      subCategoryId: '1002',
      tagIds: ['1', '2'],
      binIds: ['2001', '2002'],
      supplierId: '3001',
      stockLowerLimit: 5,
      stockUpperLimit: 20,
      replenishThreshold: 8,
      status: 1,
    })
    expect(payload).not.toHaveProperty('itemCode')
    expect(payload).not.toHaveProperty('specModel')
  })
})

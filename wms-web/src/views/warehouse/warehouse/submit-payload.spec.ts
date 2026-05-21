import { describe, expect, it } from 'vitest'
import { buildWarehouseSubmitPayload } from './submit-payload'

describe('buildWarehouseSubmitPayload', () => {
  it('omits warehouseCode when creating a warehouse', () => {
    const payload = buildWarehouseSubmitPayload(
      {
        warehouseName: '一号库房',
        warehouseCode: 'KF0001',
        address: 'A区',
        manager: '张三',
        area: 120,
        status: 1,
      },
      false,
    )

    expect(payload).toEqual({
      warehouseName: '一号库房',
      address: 'A区',
      manager: '张三',
      area: 120,
      status: 1,
    })
    expect(payload).not.toHaveProperty('warehouseCode')
  })

  it('keeps warehouseCode when editing a warehouse', () => {
    const payload = buildWarehouseSubmitPayload(
      {
        warehouseName: '一号库房',
        warehouseCode: 'KF0001',
        address: 'A区',
        manager: '张三',
        area: 120,
        status: 1,
      },
      true,
    )

    expect(payload).toEqual({
      warehouseName: '一号库房',
      warehouseCode: 'KF0001',
      address: 'A区',
      manager: '张三',
      area: 120,
      status: 1,
    })
  })
})

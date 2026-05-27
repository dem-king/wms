import { describe, expect, it, vi } from 'vitest'

const requestMocks = vi.hoisted(() => ({
  get: vi.fn(() => Promise.resolve({ data: undefined })),
  post: vi.fn(() => Promise.resolve({ data: undefined })),
  put: vi.fn(() => Promise.resolve({ data: undefined })),
  del: vi.fn(() => Promise.resolve({ data: undefined })),
}))

vi.mock('../request', () => requestMocks)

import {
  addInboundOrder,
  deleteInboundOrder,
  getInboundOrder,
  getInboundOrders,
  scanInboundOrder,
  submitInboundOrder,
  updateInboundOrder,
} from './inbound'

describe('inbound api', () => {
  it('maps list requests to the backend inbound path and numeric status', () => {
    getInboundOrders({ page: 2, size: 50, status: 'PENDING_REVIEW', orderNo: 'RK20260521001' })

    expect(requestMocks.get).toHaveBeenCalledWith('/inbound', {
      page: 2,
      size: 50,
      status: 1,
      orderNo: 'RK20260521001',
    })
  })

  it('maps form payloads to the backend inbound dto fields', () => {
    addInboundOrder({
      warehouseId: '1',
      supplierId: '2',
      inboundType: 'RETURN',
      remark: '扫码入库',
      details: [
        { itemId: '101', quantity: 3, unitPrice: 12.5, binId: '9' },
      ],
    })

    expect(requestMocks.post).toHaveBeenCalledWith('/inbound', {
      warehouseId: '1',
      supplierId: '2',
      orderType: 2,
      remark: '扫码入库',
      details: [
        { itemId: '101', quantity: 3, unitPrice: 12.5, binId: '9' },
      ],
    })
  })

  it('uses backend detail, submit and scan endpoints', () => {
    getInboundOrder('10')
    updateInboundOrder('10', {
      warehouseId: '1',
      supplierId: '2',
      inboundType: 'PURCHASE',
      remark: '',
      details: [{ itemId: '1', quantity: 1, unitPrice: 0 }],
    })
    submitInboundOrder('10')
    deleteInboundOrder('10')
    scanInboundOrder({ code: 'LBL-001', currentLabelIds: ['1', '2'] })

    expect(requestMocks.get).toHaveBeenCalledWith('/inbound/10')
    expect(requestMocks.put).toHaveBeenCalledWith('/inbound/10', {
      warehouseId: '1',
      supplierId: '2',
      orderType: 1,
      remark: '',
      details: [{ itemId: '1', quantity: 1, unitPrice: 0, binId: undefined }],
    })
    expect(requestMocks.post).toHaveBeenCalledWith('/inbound/10/submit')
    expect(requestMocks.del).toHaveBeenCalledWith('/inbound/10')
    expect(requestMocks.post).toHaveBeenCalledWith('/inbound/scan', { code: 'LBL-001', currentLabelIds: ['1', '2'] })
  })
})

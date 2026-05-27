import { describe, expect, it, vi } from 'vitest'

const requestMocks = vi.hoisted(() => ({
  get: vi.fn(() => Promise.resolve({ data: undefined })),
  post: vi.fn(() => Promise.resolve({ data: undefined })),
  put: vi.fn(() => Promise.resolve({ data: undefined })),
  del: vi.fn(() => Promise.resolve({ data: undefined })),
}))

vi.mock('../request', () => requestMocks)

import {
  addOutboundOrder,
  deleteOutboundOrder,
  getOutboundOrder,
  getOutboundOrders,
  scanOutboundOrder,
  submitOutboundOrder,
  updateOutboundOrder,
} from './outbound'

describe('outbound api', () => {
  it('maps list requests to the backend outbound path and numeric status', () => {
    getOutboundOrders({ page: 3, size: 10, status: 'COMPLETED', orderNo: 'CK20260521002' })

    expect(requestMocks.get).toHaveBeenCalledWith('/outbound', {
      page: 3,
      size: 10,
      status: 3,
      orderNo: 'CK20260521002',
    })
  })

  it('maps form payloads to the backend outbound dto fields', () => {
    addOutboundOrder({
      warehouseId: '1',
      outboundType: 'BORROW',
      recipient: '张三',
      purpose: '检修领用',
      returnDate: '2026-05-30',
      remark: '扫码出库',
      details: [
        { itemId: '101', quantity: 2, unitPrice: 99, binId: '7' },
      ],
    })

    expect(requestMocks.post).toHaveBeenCalledWith('/outbound', {
      warehouseId: '1',
      orderType: 1,
      receiver: '张三',
      purpose: '检修领用',
      expectedReturnDate: '2026-05-30T00:00:00',
      remark: '扫码出库',
      details: [
        { itemId: '101', quantity: 2, binId: '7' },
      ],
    })
  })

  it('uses backend detail, submit and scan endpoints', () => {
    getOutboundOrder('20')
    updateOutboundOrder('20', {
      warehouseId: '1',
      outboundType: 'SCRAP',
      recipient: '李四',
      purpose: '报废处理',
      returnDate: '',
      remark: '',
      details: [{ itemId: '1', quantity: 1, unitPrice: 0 }],
    })
    submitOutboundOrder('20')
    deleteOutboundOrder('20')
    scanOutboundOrder({ code: 'LBL-002', currentLabelIds: ['3', '4'] })

    expect(requestMocks.get).toHaveBeenCalledWith('/outbound/20')
    expect(requestMocks.put).toHaveBeenCalledWith('/outbound/20', {
      warehouseId: '1',
      orderType: 3,
      receiver: '李四',
      purpose: '报废处理',
      expectedReturnDate: undefined,
      remark: '',
      details: [{ itemId: '1', quantity: 1, binId: undefined }],
    })
    expect(requestMocks.post).toHaveBeenCalledWith('/outbound/20/submit')
    expect(requestMocks.del).toHaveBeenCalledWith('/outbound/20')
    expect(requestMocks.post).toHaveBeenCalledWith('/outbound/scan', { code: 'LBL-002', currentLabelIds: ['3', '4'] })
  })
})

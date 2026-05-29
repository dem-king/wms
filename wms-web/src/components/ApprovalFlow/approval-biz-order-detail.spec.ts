import { beforeEach, describe, expect, it, vi } from 'vitest'
import { BIZ_TYPE } from '@/constants/approval'

const getInboundOrderMock = vi.hoisted(() => vi.fn())
const getOutboundOrderMock = vi.hoisted(() => vi.fn())
const getScrapOrderMock = vi.hoisted(() => vi.fn())
const getTransferOrderMock = vi.hoisted(() => vi.fn())
const getReturnOrderMock = vi.hoisted(() => vi.fn())

vi.mock('@/api/business/inbound', () => ({
  getInboundOrder: getInboundOrderMock,
}))

vi.mock('@/api/business/outbound', () => ({
  getOutboundOrder: getOutboundOrderMock,
}))

vi.mock('@/api/business/scrap', () => ({
  getScrapOrder: getScrapOrderMock,
}))

vi.mock('@/api/business/transfer', () => ({
  getTransferOrder: getTransferOrderMock,
}))

vi.mock('@/api/business/return', () => ({
  getReturnOrder: getReturnOrderMock,
}))

import { loadApprovalBizOrder } from './approval-biz-order-detail'

describe('loadApprovalBizOrder', () => {
  beforeEach(() => {
    getInboundOrderMock.mockReset()
    getOutboundOrderMock.mockReset()
    getScrapOrderMock.mockReset()
    getTransferOrderMock.mockReset()
    getReturnOrderMock.mockReset()
  })

  it('loads the matching business order by approval business type', async () => {
    getInboundOrderMock.mockResolvedValue({ data: { orderNo: 'RK001' } })
    getOutboundOrderMock.mockResolvedValue({ data: { orderNo: 'CK001' } })
    getScrapOrderMock.mockResolvedValue({ data: { orderNo: 'BF001' } })
    getTransferOrderMock.mockResolvedValue({ data: { orderNo: 'DB001' } })
    getReturnOrderMock.mockResolvedValue({ data: { orderNo: 'GH001' } })

    await expect(loadApprovalBizOrder(BIZ_TYPE.INBOUND, 11)).resolves.toEqual({ orderNo: 'RK001' })
    await expect(loadApprovalBizOrder(BIZ_TYPE.OUTBOUND, 12)).resolves.toEqual({ orderNo: 'CK001' })
    await expect(loadApprovalBizOrder(BIZ_TYPE.SCRAP, 13)).resolves.toEqual({ orderNo: 'BF001' })
    await expect(loadApprovalBizOrder(BIZ_TYPE.TRANSFER, 14)).resolves.toEqual({ orderNo: 'DB001' })
    await expect(loadApprovalBizOrder(BIZ_TYPE.RETURN, 15)).resolves.toEqual({ orderNo: 'GH001' })

    expect(getInboundOrderMock).toHaveBeenCalledWith('11')
    expect(getOutboundOrderMock).toHaveBeenCalledWith('12')
    expect(getScrapOrderMock).toHaveBeenCalledWith('13')
    expect(getTransferOrderMock).toHaveBeenCalledWith('14')
    expect(getReturnOrderMock).toHaveBeenCalledWith('15')
  })

  it('returns null for unsupported or missing business information', async () => {
    await expect(loadApprovalBizOrder(undefined, 11)).resolves.toBeNull()
    await expect(loadApprovalBizOrder(BIZ_TYPE.INBOUND, undefined)).resolves.toBeNull()
    await expect(loadApprovalBizOrder(99, 11)).resolves.toBeNull()

    expect(getInboundOrderMock).not.toHaveBeenCalled()
  })
})

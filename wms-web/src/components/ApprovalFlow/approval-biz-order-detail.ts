import { BIZ_TYPE } from '@/constants/approval'
import { getInboundOrder } from '@/api/business/inbound'
import { getOutboundOrder } from '@/api/business/outbound'
import { getScrapOrder } from '@/api/business/scrap'
import { getTransferOrder } from '@/api/business/transfer'
import { getReturnOrder } from '@/api/business/return'
import type {
  BizType,
  EntityId,
  InboundOrderVo,
  OutboundOrderVo,
  ReturnOrderVo,
  ScrapOrderVo,
  TransferOrderVo,
} from '@/types/business'

export type ApprovalBizOrder =
  | InboundOrderVo
  | OutboundOrderVo
  | ScrapOrderVo
  | TransferOrderVo
  | ReturnOrderVo

export async function loadApprovalBizOrder(
  bizType?: BizType | number | null,
  bizId?: EntityId | number | null,
): Promise<ApprovalBizOrder | null> {
  if (!bizType || !bizId) {
    return null
  }

  const orderId = String(bizId) as EntityId

  if (bizType === BIZ_TYPE.INBOUND) {
    return (await getInboundOrder(orderId)).data
  }
  if (bizType === BIZ_TYPE.OUTBOUND) {
    return (await getOutboundOrder(orderId)).data
  }
  if (bizType === BIZ_TYPE.SCRAP) {
    return (await getScrapOrder(orderId)).data
  }
  if (bizType === BIZ_TYPE.TRANSFER) {
    return (await getTransferOrder(orderId)).data
  }
  if (bizType === BIZ_TYPE.RETURN) {
    return (await getReturnOrder(orderId)).data
  }

  return null
}

<script setup lang="ts">
import { computed, watch, ref, shallowRef } from 'vue'
import { BIZ_TYPE, getBizTypeLabel } from '@/constants/approval'
import { loadApprovalBizOrder, type ApprovalBizOrder } from './approval-biz-order-detail'
import type { BizType, EntityId, OrderStatus } from '@/types/business'

interface Props {
  bizType?: BizType | number | null
  bizId?: EntityId | number | null
}

interface SummaryItem {
  label: string
  value: unknown
}

interface DetailColumn {
  prop: string
  label: string
  minWidth?: number
  formatter?: (value: unknown) => string
}

const props = defineProps<Props>()

const loading = shallowRef(false)
const errorMessage = shallowRef('')
const order = ref<ApprovalBizOrder | null>(null)

const ORDER_STATUS_LABEL: Record<OrderStatus, string> = {
  DRAFT: '草稿',
  PENDING_REVIEW: '待审核',
  APPROVED: '已审核',
  COMPLETED: '已完成',
  REJECTED: '已驳回',
}

const INBOUND_TYPE_LABEL: Record<string, string> = {
  PURCHASE: '采购入库',
  RETURN: '归还入库',
  TRANSFER: '调拨入库',
}

const OUTBOUND_TYPE_LABEL: Record<string, string> = {
  BORROW: '领用出库',
  TRANSFER: '调拨出库',
  SCRAP: '报废出库',
}

function displayValue(value: unknown): string {
  if (value === undefined || value === null || value === '') {
    return '-'
  }
  return String(value)
}

function formatMoney(value: unknown): string {
  const amount = Number(value || 0)
  return amount.toFixed(2)
}

function formatStatus(value: unknown): string {
  return ORDER_STATUS_LABEL[value as OrderStatus] || displayValue(value)
}

function formatCondition(value: unknown): string {
  return Number(value) === 1 ? '异常' : '正常'
}

function getOrderDetails(currentOrder: ApprovalBizOrder | null): Record<string, unknown>[] {
  if (!currentOrder || !('details' in currentOrder)) {
    return []
  }
  return (currentOrder.details || []) as unknown as Record<string, unknown>[]
}

const detailTitle = computed(() => `${getBizTypeLabel(props.bizType as BizType)}单据详情`)

const summaryItems = computed<SummaryItem[]>(() => {
  const currentOrder = order.value
  if (!currentOrder) {
    return []
  }

  if (props.bizType === BIZ_TYPE.INBOUND && 'inboundType' in currentOrder) {
    return [
      { label: '单号', value: currentOrder.orderNo },
      { label: '库房', value: currentOrder.warehouseName },
      { label: '供应商', value: currentOrder.supplierName },
      { label: '入库类型', value: INBOUND_TYPE_LABEL[currentOrder.inboundType] || currentOrder.inboundType },
      { label: '状态', value: formatStatus(currentOrder.status) },
      { label: '金额', value: formatMoney(currentOrder.totalAmount) },
      { label: '备注', value: currentOrder.remark },
    ]
  }

  if (props.bizType === BIZ_TYPE.OUTBOUND && 'outboundType' in currentOrder) {
    return [
      { label: '单号', value: currentOrder.orderNo },
      { label: '库房', value: currentOrder.warehouseName },
      { label: '出库类型', value: OUTBOUND_TYPE_LABEL[currentOrder.outboundType] || currentOrder.outboundType },
      { label: '领用人', value: currentOrder.recipient },
      { label: '用途', value: currentOrder.purpose },
      { label: '归还日期', value: currentOrder.returnDate },
      { label: '状态', value: formatStatus(currentOrder.status) },
      { label: '金额', value: formatMoney(currentOrder.totalAmount) },
      { label: '备注', value: currentOrder.remark },
    ]
  }

  if (props.bizType === BIZ_TYPE.SCRAP && 'scrapReason' in currentOrder) {
    return [
      { label: '单号', value: currentOrder.orderNo },
      { label: '库房', value: currentOrder.warehouseName },
      { label: '报废原因', value: currentOrder.scrapReason },
      { label: '状态', value: formatStatus(currentOrder.status) },
      { label: '备注', value: currentOrder.remark },
    ]
  }

  if (props.bizType === BIZ_TYPE.TRANSFER && 'fromWarehouseName' in currentOrder) {
    return [
      { label: '单号', value: currentOrder.orderNo },
      { label: '调出库房', value: currentOrder.fromWarehouseName },
      { label: '调入库房', value: currentOrder.toWarehouseName },
      { label: '状态', value: formatStatus(currentOrder.status) },
      { label: '备注', value: currentOrder.remark },
    ]
  }

  if (props.bizType === BIZ_TYPE.RETURN && 'outboundOrderNo' in currentOrder) {
    return [
      { label: '单号', value: currentOrder.orderNo },
      { label: '关联出库单号', value: currentOrder.outboundOrderNo },
      { label: '领用人', value: currentOrder.recipient },
      { label: '归还人', value: currentOrder.receiver },
      { label: '状态', value: formatStatus(currentOrder.status) },
      { label: '备注', value: currentOrder.remark },
    ]
  }

  return [
    { label: '单号', value: 'orderNo' in currentOrder ? currentOrder.orderNo : '' },
    { label: '状态', value: 'status' in currentOrder ? formatStatus(currentOrder.status) : '' },
  ]
})

const detailColumns = computed<DetailColumn[]>(() => {
  if (props.bizType === BIZ_TYPE.INBOUND || props.bizType === BIZ_TYPE.OUTBOUND) {
    return [
      { prop: 'itemCode', label: '物品编码', minWidth: 120 },
      { prop: 'itemName', label: '物品名称', minWidth: 150 },
      { prop: 'specModel', label: '规格型号', minWidth: 120 },
      { prop: 'unit', label: '单位', minWidth: 80 },
      { prop: 'quantity', label: '数量', minWidth: 80 },
      { prop: 'unitPrice', label: '单价', minWidth: 100, formatter: formatMoney },
      { prop: 'amount', label: '金额', minWidth: 100, formatter: formatMoney },
    ]
  }

  if (props.bizType === BIZ_TYPE.RETURN) {
    return [
      { prop: 'itemCode', label: '物品编码', minWidth: 120 },
      { prop: 'itemName', label: '物品名称', minWidth: 150 },
      { prop: 'quantity', label: '归还数量', minWidth: 100 },
      { prop: 'conditionStatus', label: '物品状态', minWidth: 100, formatter: formatCondition },
      { prop: 'abnormalRemark', label: '异常说明', minWidth: 160 },
    ]
  }

  return [
    { prop: 'itemCode', label: '物品编码', minWidth: 120 },
    { prop: 'itemName', label: '物品名称', minWidth: 150 },
    { prop: 'quantity', label: '数量', minWidth: 80 },
  ]
})

const details = computed(() => getOrderDetails(order.value))

watch(
  () => [props.bizType, props.bizId],
  async () => {
    loading.value = true
    errorMessage.value = ''
    try {
      order.value = await loadApprovalBizOrder(props.bizType, props.bizId)
    } catch (error) {
      order.value = null
      errorMessage.value = error instanceof Error ? error.message : '业务单据加载失败'
    } finally {
      loading.value = false
    }
  },
  { immediate: true },
)
</script>

<template>
  <section class="approval-biz-detail">
    <el-divider content-position="left">{{ detailTitle }}</el-divider>
    <div v-loading="loading" class="approval-biz-detail__body">
      <el-alert
        v-if="errorMessage"
        :title="errorMessage"
        type="error"
        :closable="false"
        class="approval-biz-detail__alert"
      />
      <template v-else-if="order">
        <el-descriptions :column="2" border>
          <el-descriptions-item
            v-for="item in summaryItems"
            :key="item.label"
            :label="item.label"
            :span="item.label === '备注' ? 2 : 1"
          >
            {{ displayValue(item.value) }}
          </el-descriptions-item>
        </el-descriptions>
        <el-table :data="details" border class="approval-biz-detail__table">
          <el-table-column
            v-for="column in detailColumns"
            :key="column.prop"
            :prop="column.prop"
            :label="column.label"
            :min-width="column.minWidth"
          >
            <template #default="{ row }">
              {{ column.formatter ? column.formatter(row[column.prop]) : displayValue(row[column.prop]) }}
            </template>
          </el-table-column>
        </el-table>
      </template>
      <el-empty v-else description="暂无业务单据信息" />
    </div>
  </section>
</template>

<style scoped lang="scss">
.approval-biz-detail {
  &__body {
    min-height: 120px;
  }

  &__alert {
    margin-bottom: 16px;
  }

  &__table {
    margin-top: 16px;
  }
}
</style>

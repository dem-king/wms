<!--
  WMS-PDA StatusTag状态标签组件
  根据状态值和类型映射对应颜色和文字，支持单据/标签/打印三种类型
-->
<template>
  <view class="status-tag" :style="{ backgroundColor: tagColor, borderColor: tagColor }">
    <text class="status-tag__text" :style="{ color: tagColor }">{{ tagText }}</text>
  </view>
</template>

<script setup lang="ts">
/**
 * StatusTag状态标签组件
 * 用于单据状态、标签状态、打印状态的展示
 */
import { computed } from 'vue'
import { OrderStatus, LabelStatus, PrintStatus } from '@/utils/constants'

/** 组件属性 */
const props = defineProps<{
  /** 状态值 */
  status: number
  /** 状态类型：order-单据状态, label-标签状态, print-打印状态 */
  type: 'order' | 'label' | 'print'
}>()

/** 单据状态映射：status → { text, color } */
const ORDER_STATUS_MAP: Record<number, { text: string; color: string }> = {
  [OrderStatus.DRAFT]: { text: '草稿', color: '#999999' },
  [OrderStatus.PENDING]: { text: '待审核', color: '#faad14' },
  [OrderStatus.APPROVED]: { text: '已审核', color: '#1890ff' },
  [OrderStatus.COMPLETED]: { text: '已完成', color: '#52c41a' },
  [OrderStatus.REJECTED]: { text: '已驳回', color: '#ff4d4f' }
}

/** 标签状态映射：status → { text, color } */
const LABEL_STATUS_MAP: Record<number, { text: string; color: string }> = {
  [LabelStatus.IN_STOCK]: { text: '在库', color: '#52c41a' },
  [LabelStatus.IN_USE]: { text: '正在使用', color: '#1890ff' },
  [LabelStatus.RETURNED]: { text: '已归还', color: '#999999' },
  [LabelStatus.SCRAPPED]: { text: '报废', color: '#ff4d4f' },
  [LabelStatus.IDLE]: { text: '闲置', color: '#faad14' }
}

/** 打印状态映射：status → { text, color } */
const PRINT_STATUS_MAP: Record<number, { text: string; color: string }> = {
  [PrintStatus.NOT_PRINTED]: { text: '未打印', color: '#999999' },
  [PrintStatus.PRINTED]: { text: '已打印', color: '#52c41a' }
}

/** 默认未知状态 */
const UNKNOWN_STATUS = { text: '未知', color: '#999999' }

/** 根据type获取对应的状态映射表 */
function getStatusMap(): Record<number, { text: string; color: string }> {
  switch (props.type) {
    case 'order':
      return ORDER_STATUS_MAP
    case 'label':
      return LABEL_STATUS_MAP
    case 'print':
      return PRINT_STATUS_MAP
    default:
      return ORDER_STATUS_MAP
  }
}

/** 标签文字 */
const tagText = computed(() => {
  const map = getStatusMap()
  return (map[props.status] ?? UNKNOWN_STATUS).text
})

/** 标签颜色 */
const tagColor = computed(() => {
  const map = getStatusMap()
  return (map[props.status] ?? UNKNOWN_STATUS).color
})
</script>

<style lang="scss" scoped>
.status-tag {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  padding: 2rpx 16rpx;
  border-radius: $border-radius-sm;
  border: 1rpx solid transparent;
  background-color: transparent;
  line-height: 1;

  &__text {
    font-size: $font-size-sm;
    font-weight: 500;
    line-height: 1.5;
  }
}
</style>
<!--
  WMS-PDA OrderCard单据卡片组件
  展示单据摘要信息：单号、库房、状态标签、时间、明细数量，支持点击和提交操作
-->
<template>
  <view class="order-card" @click="handleClick">
    <view class="order-card__header">
      <text class="order-card__order-no">{{ order.orderNo }}</text>
      <StatusTag :status="order.status" :type="orderType" />
    </view>
    <view class="order-card__body">
      <view class="order-card__row">
        <text class="order-card__label">库房</text>
        <text class="order-card__value">{{ order.warehouseName }}</text>
      </view>
      <view class="order-card__row">
        <text class="order-card__label">创建时间</text>
        <text class="order-card__value">{{ order.createTime }}</text>
      </view>
      <view class="order-card__row">
        <text class="order-card__label">明细数量</text>
        <text class="order-card__value order-card__value--number">{{ order.detailCount }}</text>
      </view>
    </view>
    <!-- 提交按钮：仅草稿状态且showSubmit=true时显示 -->
    <view
      class="order-card__footer"
      v-if="showSubmit && order.status === OrderStatus.DRAFT"
    >
      <view class="order-card__submit-btn" @click.stop="handleSubmit">
        <text class="order-card__submit-btn-text">提交</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
/**
 * OrderCard单据卡片组件
 * 展示单据摘要信息，点击卡片跳转详情，草稿状态可提交
 */
import StatusTag from './StatusTag.vue'
import { OrderStatus } from '@/utils/constants'

/** 单据卡片数据结构 */
export interface OrderCardData {
  /** 单据ID */
  id: number
  /** 单据编号 */
  orderNo: string
  /** 库房名称 */
  warehouseName: string
  /** 单据状态 */
  status: number
  /** 创建时间 */
  createTime: string
  /** 明细数量 */
  detailCount: number
}

/** 组件属性 */
const props = withDefaults(defineProps<{
  /** 单据数据 */
  order: OrderCardData
  /** 状态标签类型：order-单据, label-标签, print-打印 */
  orderType?: string
  /** 是否显示提交按钮 */
  showSubmit?: boolean
}>(), {
  orderType: 'order',
  showSubmit: false
})

/** 组件事件 */
const emit = defineEmits<{
  /** 点击卡片事件 */
  click: []
  /** 点击提交事件 */
  submit: []
}>()

/**
 * 处理卡片点击
 * 触发click事件，由父组件处理跳转详情
 */
function handleClick(): void {
  emit('click')
}

/**
 * 处理提交按钮点击
 * 阻止事件冒泡，触发submit事件
 */
function handleSubmit(): void {
  emit('submit')
}
</script>

<style lang="scss" scoped>
.order-card {
  background-color: $bg-color-card;
  border-radius: $border-radius-md;
  padding: $spacing-md;
  margin-bottom: $spacing-sm;
  box-shadow: $shadow-sm;
  cursor: pointer;

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: $spacing-sm;
    padding-bottom: $spacing-sm;
    border-bottom: 1rpx solid $border-color-light;
  }

  &__order-no {
    font-size: $font-size-lg;
    color: $text-color-primary;
    font-weight: 600;
    font-family: 'Courier New', monospace;
    letter-spacing: 1rpx;
  }

  &__body {
    display: flex;
    flex-direction: column;
    gap: 8rpx;
  }

  &__row {
    display: flex;
    align-items: center;
    gap: $spacing-sm;
  }

  &__label {
    font-size: $font-size-sm;
    color: $text-color-secondary;
    width: 120rpx;
    flex-shrink: 0;
  }

  &__value {
    font-size: $font-size-md;
    color: $text-color-regular;
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;

    &--number {
      font-weight: 600;
      color: $color-primary;
    }
  }

  &__footer {
    display: flex;
    justify-content: flex-end;
    margin-top: $spacing-sm;
    padding-top: $spacing-sm;
    border-top: 1rpx solid $border-color-light;
  }

  &__submit-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 12rpx 40rpx;
    background-color: $color-primary;
    border-radius: $border-radius-md;
    cursor: pointer;
  }

  &__submit-btn-text {
    font-size: $font-size-md;
    color: $text-color-inverse;
    font-weight: 500;
  }
}
</style>
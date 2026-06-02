<!--
  WMS-PDA DetailItem明细项组件
  展示扫码明细信息：标签编号、物品名称、物品编码、数量，右侧可选删除按钮
-->
<template>
  <view class="detail-item">
    <view class="detail-item__content">
      <!-- 标签编号 -->
      <view class="detail-item__row">
        <text class="detail-item__label">标签编号</text>
        <text class="detail-item__value detail-item__value--mono">{{ item.labelNo }}</text>
      </view>
      <!-- 物品名称 -->
      <view class="detail-item__row">
        <text class="detail-item__label">物品名称</text>
        <text class="detail-item__value">{{ item.itemName }}</text>
      </view>
      <!-- 物品编码 -->
      <view class="detail-item__row">
        <text class="detail-item__label">物品编码</text>
        <text class="detail-item__value detail-item__value--mono">{{ item.itemCode }}</text>
      </view>
      <!-- 数量 -->
      <view class="detail-item__row">
        <text class="detail-item__label">数量</text>
        <text class="detail-item__value detail-item__value--number">{{ item.quantity }}</text>
      </view>
    </view>
    <!-- 删除按钮 -->
    <view
      class="detail-item__remove"
      v-if="removable"
      @click="handleRemove"
    >
      <text class="detail-item__remove-text">删除</text>
    </view>
  </view>
</template>

<script setup lang="ts">
/**
 * DetailItem明细项组件
 * 展示扫码明细信息，物品信息只读不可编辑
 */

/** 明细项数据结构 */
export interface DetailItemData {
  /** 标签编号 */
  labelNo: string
  /** 物品名称 */
  itemName: string
  /** 物品编码 */
  itemCode: string
  /** 数量 */
  quantity: number
}

/** 组件属性 */
const props = withDefaults(defineProps<{
  /** 明细项数据 */
  item: DetailItemData
  /** 是否可删除 */
  removable?: boolean
}>(), {
  removable: true
})

/** 组件事件 */
const emit = defineEmits<{
  /** 点击删除事件 */
  remove: []
}>()

/**
 * 处理删除按钮点击
 */
function handleRemove(): void {
  emit('remove')
}
</script>

<style lang="scss" scoped>
.detail-item {
  display: flex;
  align-items: center;
  background-color: $bg-color-card;
  border-radius: $border-radius-md;
  padding: $spacing-md;
  margin-bottom: $spacing-sm;
  box-shadow: $shadow-sm;

  &__content {
    flex: 1;
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
    color: $text-color-primary;
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;

    &--mono {
      font-family: 'Courier New', monospace;
      letter-spacing: 1rpx;
    }

    &--number {
      font-weight: 600;
      color: $color-primary;
    }
  }

  &__remove {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 80rpx;
    height: 80rpx;
    flex-shrink: 0;
    margin-left: $spacing-sm;
    cursor: pointer;
  }

  &__remove-text {
    font-size: $font-size-sm;
    color: $color-error;
    font-weight: 500;
  }
}
</style>
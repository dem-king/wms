<!--
  WMS-PDA EmptyState空状态组件
  居中展示空状态图片+文字，支持自定义内容和操作按钮
-->
<template>
  <view class="empty-state">
    <image
      class="empty-state__image"
      :src="image"
      mode="aspectFit"
    />
    <text class="empty-state__text">{{ text }}</text>
    <!-- 自定义内容插槽 -->
    <slot />
    <!-- 操作按钮区域插槽 -->
    <view class="empty-state__action" v-if="$slots.action">
      <slot name="action" />
    </view>
  </view>
</template>

<script setup lang="ts">
/**
 * EmptyState空状态组件
 * 用于列表无数据、搜索无结果等场景
 */

/** 组件属性 */
const props = withDefaults(defineProps<{
  /** 空状态图片路径 */
  image?: string
  /** 空状态提示文字 */
  text?: string
}>(), {
  image: '/static/images/empty.png',
  text: '暂无数据'
})

/** 组件插槽 */
defineSlots<{
  /** 自定义内容区域 */
  default?: () => void
  /** 操作按钮区域 */
  action?: () => void
}>()
</script>

<style lang="scss" scoped>
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: $spacing-xxl $spacing-lg;
  min-height: 400rpx;

  &__image {
    width: 200rpx;
    height: 200rpx;
    margin-bottom: $spacing-lg;
  }

  &__text {
    font-size: $font-size-md;
    color: $text-color-secondary;
    text-align: center;
    line-height: 1.5;
  }

  &__action {
    margin-top: $spacing-lg;
  }
}
</style>
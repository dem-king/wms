<!--
  WMS-PDA OfflineBar离线状态栏组件
  离线时显示黄色提示栏，同步中显示动画，在线时隐藏
-->
<template>
  <view class="offline-bar" v-if="!isOnline">
    <view class="offline-bar__content">
      <text class="offline-bar__text">离线模式</text>
      <text class="offline-bar__divider">|</text>
      <text class="offline-bar__text">队列: {{ queueSize }}条</text>
      <text class="offline-bar__divider">|</text>
      <!-- 同步中显示动画 -->
      <view class="offline-bar__sync" v-if="syncing">
        <view class="offline-bar__sync-dot" />
        <text class="offline-bar__sync-text">同步中...</text>
      </view>
      <!-- 未同步时显示同步按钮 -->
      <view class="offline-bar__sync-btn" v-else @click="handleSync">
        <text class="offline-bar__sync-btn-text">同步</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
/**
 * OfflineBar离线状态栏组件
 * 离线模式下显示提示信息和手动同步按钮
 */

/** 组件属性 */
const props = withDefaults(defineProps<{
  /** 是否在线 */
  isOnline: boolean
  /** 离线队列大小 */
  queueSize?: number
  /** 是否正在同步 */
  syncing?: boolean
}>(), {
  queueSize: 0,
  syncing: false
})

/** 组件事件 */
const emit = defineEmits<{
  /** 手动触发同步 */
  sync: []
}>()

/**
 * 处理同步按钮点击
 */
function handleSync(): void {
  emit('sync')
}
</script>

<style lang="scss" scoped>
.offline-bar {
  width: 100%;
  background-color: #fffbe6;
  border-bottom: 1rpx solid #ffe58f;

  &__content {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 12rpx $spacing-md;
    gap: 8rpx;
  }

  &__text {
    font-size: $font-size-sm;
    color: #d48806;
    font-weight: 500;
  }

  &__divider {
    font-size: $font-size-xs;
    color: #ffe58f;
    margin: 0 4rpx;
  }

  &__sync {
    display: flex;
    align-items: center;
    gap: 6rpx;
  }

  &__sync-dot {
    width: 12rpx;
    height: 12rpx;
    border-radius: 50%;
    background-color: #d48806;
    animation: sync-pulse 1s ease-in-out infinite;
  }

  &__sync-text {
    font-size: $font-size-sm;
    color: #d48806;
    font-weight: 500;
  }

  &__sync-btn {
    padding: 4rpx 20rpx;
    border-radius: $border-radius-sm;
    background-color: #d48806;
    cursor: pointer;
  }

  &__sync-btn-text {
    font-size: $font-size-sm;
    color: $text-color-inverse;
    font-weight: 500;
  }
}

@keyframes sync-pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.3;
  }
}
</style>
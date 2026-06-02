<!--
  WMS-PDA RfidReader RFID读取组件
  大按钮+状态指示+计数显示，支持开始/停止读取、脉冲动画、硬件不可用降级提示
-->
<template>
  <view class="rfid-reader">
    <!-- 状态区域插槽（默认展示状态指示） -->
    <slot name="status">
      <view class="rfid-reader__status">
        <view
          class="rfid-reader__indicator"
          :class="{
            'rfid-reader__indicator--idle': !reading,
            'rfid-reader__indicator--reading': reading
          }"
        >
          <!-- 读取中脉冲动画 -->
          <view v-if="reading" class="rfid-reader__pulse" />
          <view v-if="reading" class="rfid-reader__pulse rfid-reader__pulse--delay" />
        </view>
        <text class="rfid-reader__status-text">
          {{ statusText }}
        </text>
      </view>
    </slot>

    <!-- 进度区域插槽（默认展示计数） -->
    <slot name="progress">
      <view class="rfid-reader__count" v-if="readCount > 0 || reading">
        <text class="rfid-reader__count-number">{{ readCount }}</text>
        <text class="rfid-reader__count-label">个标签</text>
      </view>
    </slot>

    <!-- 大按钮：开始/停止读取 -->
    <view class="rfid-reader__action">
      <button
        class="rfid-reader__btn"
        :class="{
          'rfid-reader__btn--start': !reading,
          'rfid-reader__btn--stop': reading,
          'rfid-reader__btn--disabled': !hardwareAvailable && !reading
        }"
        :disabled="!hardwareAvailable && !reading"
        @click="handleClick"
      >
        <text class="rfid-reader__btn-text">
          {{ buttonText }}
        </text>
      </button>
    </view>

    <!-- 硬件不可用提示 -->
    <view class="rfid-reader__warning" v-if="!hardwareAvailable">
      <text class="rfid-reader__warning-text">⚠ RFID模块不可用，请检查设备或使用手动输入</text>
    </view>
  </view>
</template>

<script setup lang="ts">
/**
 * RfidReader RFID读取组件
 * 提供RFID批量读取的开始/停止控制、状态指示、计数显示
 * 读取中显示脉冲动画效果，硬件不可用时禁用开始按钮并提示
 */
import { computed } from 'vue'

/** 组件属性 */
const props = withDefaults(defineProps<{
  /** 是否正在读取 */
  reading?: boolean
  /** 已读取标签数量 */
  readCount?: number
  /** RFID硬件是否可用 */
  hardwareAvailable?: boolean
}>(), {
  reading: false,
  readCount: 0,
  hardwareAvailable: false
})

/** 组件事件 */
const emit = defineEmits<{
  /** 点击开始读取 */
  start: []
  /** 点击停止读取 */
  stop: []
}>()

/** 组件插槽 */
defineSlots<{
  /** 状态区域（替换默认的状态指示） */
  status?: (props: { reading: boolean; readCount: number; hardwareAvailable: boolean }) => void
  /** 进度区域（替换默认的计数显示） */
  progress?: (props: { reading: boolean; readCount: number }) => void
}>()

/** 状态文字 */
const statusText = computed<string>(() => {
  if (!props.hardwareAvailable) {
    return 'RFID模块不可用'
  }
  if (props.reading) {
    return '正在读取...'
  }
  if (props.readCount > 0) {
    return `已停止，共读取 ${props.readCount} 个标签`
  }
  return '就绪，点击开始读取'
})

/** 按钮文字 */
const buttonText = computed<string>(() => {
  if (props.reading) {
    return '停止读取'
  }
  return '开始读取'
})

/**
 * 处理按钮点击
 * 读取中点击触发停止，否则触发开始
 */
function handleClick(): void {
  if (props.reading) {
    emit('stop')
  } else {
    emit('start')
  }
}
</script>

<style lang="scss" scoped>
.rfid-reader {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: $spacing-lg $spacing-md;
  background-color: $bg-color-card;
  border-radius: $border-radius-lg;
  box-shadow: $shadow-md;

  // ==================== 状态指示 ====================
  &__status {
    display: flex;
    align-items: center;
    gap: $spacing-sm;
    margin-bottom: $spacing-md;
  }

  &__indicator {
    position: relative;
    width: 24rpx;
    height: 24rpx;
    border-radius: 50%;
    flex-shrink: 0;

    &--idle {
      background-color: $text-color-secondary;
    }

    &--reading {
      background-color: $color-primary;
    }
  }

  // 脉冲动画：读取时从指示器向外扩散
  &__pulse {
    position: absolute;
    top: 50%;
    left: 50%;
    width: 24rpx;
    height: 24rpx;
    border-radius: 50%;
    background-color: rgba($color-primary, 0.3);
    transform: translate(-50%, -50%);
    animation: rfid-pulse 1.5s ease-out infinite;

    &--delay {
      animation-delay: 0.75s;
    }
  }

  &__status-text {
    font-size: $font-size-md;
    color: $text-color-regular;
  }

  // ==================== 计数显示 ====================
  &__count {
    display: flex;
    align-items: baseline;
    gap: $spacing-xs;
    margin-bottom: $spacing-lg;
  }

  &__count-number {
    font-size: 72rpx;
    font-weight: 700;
    color: $color-primary;
    line-height: 1;
  }

  &__count-label {
    font-size: $font-size-md;
    color: $text-color-secondary;
  }

  // ==================== 操作按钮 ====================
  &__action {
    width: 100%;
    padding: 0 $spacing-lg;
  }

  &__btn {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 100%;
    height: 96rpx;
    border-radius: $border-radius-round;
    border: none;
    font-size: $font-size-xl;
    font-weight: 600;
    transition: all 0.2s ease;

    &--start {
      background-color: $color-primary;
      color: $text-color-inverse;

      &:active {
        background-color: $color-primary-dark;
      }
    }

    &--stop {
      background-color: $color-error;
      color: $text-color-inverse;

      &:active {
        background-color: darken($color-error, 10%);
      }
    }

    &--disabled {
      background-color: $bg-color-grey;
      color: $text-color-secondary;
      opacity: 0.6;
    }
  }

  &__btn-text {
    font-size: $font-size-xl;
    font-weight: 600;
  }

  // ==================== 硬件不可用提示 ====================
  &__warning {
    margin-top: $spacing-md;
    padding: $spacing-sm $spacing-md;
    background-color: rgba($color-warning, 0.1);
    border-radius: $border-radius-md;
  }

  &__warning-text {
    font-size: $font-size-sm;
    color: $color-warning;
    text-align: center;
  }
}

// 脉冲动画关键帧
@keyframes rfid-pulse {
  0% {
    width: 24rpx;
    height: 24rpx;
    opacity: 0.6;
  }
  100% {
    width: 64rpx;
    height: 64rpx;
    opacity: 0;
  }
}
</style>
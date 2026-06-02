<!--
  WMS-PDA ScanInput扫码输入组件
  大输入框+扫码图标，支持PDA硬件扫码键触发，自动聚焦，扫码后自动清空
-->
<template>
  <view class="scan-input">
    <view class="scan-input__wrapper" :class="{ 'scan-input__wrapper--disabled': disabled }">
      <view class="scan-input__icon" @click="handleScanClick">
        <text class="scan-input__icon-text">📷</text>
      </view>
      <input
        class="scan-input__field"
        :value="modelValue"
        :placeholder="placeholder"
        :disabled="disabled"
        :focus="internalFocus"
        confirm-type="done"
        @input="handleInput"
        @confirm="handleConfirm"
      />
      <!-- 右侧附加内容插槽 -->
      <slot />
    </view>
  </view>
</template>

<script setup lang="ts">
/**
 * ScanInput扫码输入组件
 * 支持PDA硬件扫码键触发、自动聚焦、扫码后自动清空
 * 适合手持终端操作场景
 */
import { ref, onMounted, onBeforeUnmount, nextTick } from 'vue'
import { PDA_SCAN_KEY_CODES } from '@/utils/constants'

/** 组件属性 */
const props = withDefaults(defineProps<{
  /** 输入值（v-model） */
  modelValue?: string
  /** 占位文字 */
  placeholder?: string
  /** 是否禁用 */
  disabled?: boolean
  /** 是否自动聚焦 */
  autoFocus?: boolean
}>(), {
  modelValue: '',
  placeholder: '请扫码或输入编码',
  disabled: false,
  autoFocus: true
})

/** 组件事件 */
const emit = defineEmits<{
  /** 扫码完成事件 */
  scan: [value: string]
  /** 手动输入事件 */
  input: [value: string]
  /** 更新modelValue */
  'update:modelValue': [value: string]
}>()

/** 内部聚焦状态 */
const internalFocus = ref<boolean>(false)

/**
 * 处理输入事件
 * 手动输入时触发input事件
 */
function handleInput(event: { detail: { value: string } }): void {
  const value = event.detail.value
  emit('update:modelValue', value)
  emit('input', value)
}

/**
 * 处理确认事件（键盘确认/回车）
 * 触发扫码完成事件，并清空输入框
 */
function handleConfirm(): void {
  const value = props.modelValue?.trim()
  if (value) {
    emit('scan', value)
    // 扫码后自动清空输入框
    nextTick(() => {
      emit('update:modelValue', '')
    })
  }
}

/**
 * 处理扫码图标点击
 * 在支持相机的设备上可触发相机扫码
 */
function handleScanClick(): void {
  if (props.disabled) {
    return
  }

  // 触发uni扫码
  uni.scanCode({
    scanType: ['barCode', 'qrCode'],
    success: (res) => {
      if (res.result) {
        emit('scan', res.result)
        // 扫码后自动清空输入框
        nextTick(() => {
          emit('update:modelValue', '')
        })
      }
    },
    fail: () => {
      // 扫码失败，静默处理（可能设备不支持相机扫码）
    }
  })
}

/**
 * PDA硬件扫码键监听
 * 通过plus.key.addEventListener监听扫码键按下事件
 */
function setupHardwareScanListener(): void {
  try {
    if (typeof plus !== 'undefined' && plus.key) {
      // 监听PDA扫码键按下事件（不同厂商的扫码键keycode不同，常见为120或293）
      plus.key.addEventListener('keydown', (e: { keyCode: number }) => {
        // 常见PDA扫码键keyCode：120(Zebra)、293(优博讯)、280(新大陆)
        const scanKeyCodes = PDA_SCAN_KEY_CODES
        if (scanKeyCodes.includes(e.keyCode)) {
          // 确保输入框聚焦
          internalFocus.value = true
        }
      })
    }
  } catch {
    // plus API不可用，静默跳过
  }
}

/**
 * 自动聚焦
 * 在onReady时聚焦输入框
 */
function autoFoucsInput(): void {
  if (props.autoFocus && !props.disabled) {
    // 延迟聚焦，确保渲染完成
    setTimeout(() => {
      internalFocus.value = true
    }, 300)
  }
}

onMounted(() => {
  setupHardwareScanListener()
  autoFoucsInput()
})

onBeforeUnmount(() => {
  internalFocus.value = false
})
</script>

<style lang="scss" scoped>
.scan-input {
  width: 100%;

  &__wrapper {
    display: flex;
    align-items: center;
    height: 88rpx;
    background-color: $bg-color-card;
    border: 2rpx solid $border-color-base;
    border-radius: $border-radius-md;
    padding: 0 $spacing-sm;
    gap: $spacing-sm;

    &--disabled {
      background-color: $bg-color-grey;
      opacity: 0.6;
    }
  }

  &__icon {
    display: flex;
    align-items: center;
    justify-content: center;
    width: 56rpx;
    height: 56rpx;
    flex-shrink: 0;
    cursor: pointer;
  }

  &__icon-text {
    font-size: 40rpx;
    line-height: 1;
  }

  &__field {
    flex: 1;
    height: 88rpx;
    font-size: $font-size-lg;
    color: $text-color-primary;
    line-height: 88rpx;
    border: none;
    background-color: transparent;
  }
}
</style>
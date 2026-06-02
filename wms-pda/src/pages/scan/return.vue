<!--
  WMS-PDA 归还扫码页面
  扫码后校验labelStatus=2(正在使用)才可归还
-->
<template>
  <view class="scan-return-page">
    <!-- 扫码输入 -->
    <view class="scan-return-page__scan">
      <ScanInput
        v-model="scanCodeInput"
        placeholder="请扫码或输入编码"
        @scan="handleScan"
      />
    </view>

    <!-- 扫码明细列表 -->
    <view class="scan-return-page__details">
      <view class="scan-return-page__details-header">
        <text class="scan-return-page__details-title">扫码明细</text>
        <text class="scan-return-page__details-count">共 {{ details.length }} 条</text>
      </view>

      <view v-if="details.length > 0" class="scan-return-page__details-list">
        <DetailItem
          v-for="(item, index) in details"
          :key="item.labelId"
          :item="item"
          :removable="true"
          @remove="handleRemoveDetail(index)"
        />
      </view>
      <EmptyState v-else text="暂无扫码明细" />
    </view>

    <!-- 底部操作栏 -->
    <view class="scan-return-page__footer">
      <view
        class="scan-return-page__btn scan-return-page__btn--clear"
        @click="handleClear"
        v-if="details.length > 0"
      >
        <text class="scan-return-page__btn-text">清空</text>
      </view>
      <view
        class="scan-return-page__btn scan-return-page__btn--submit"
        :class="{ 'scan-return-page__btn--disabled': !canSubmit }"
        @click="handleSubmit"
      >
        <text class="scan-return-page__btn-text scan-return-page__btn-text--submit">
          {{ scanning ? '扫码中...' : '提交' }}
        </text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
/**
 * 归还扫码页面
 * 扫码 → 校验labelStatus=2(正在使用) → 添加明细 → 删除 → 提交
 */
import { ref, onBeforeUnmount } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { useScan } from '@/composables/useScan'
import ScanInput from '@/components/ScanInput.vue'
import DetailItem from '@/components/DetailItem.vue'
import EmptyState from '@/components/EmptyState.vue'

const { details, scanning, canSubmit, setJobType, scanCode, removeDetail, submitOrder, reset } = useScan()

const scanCodeInput = ref<string>('')

async function handleScan(code: string): Promise<void> {
  await scanCode(code)
}

function handleRemoveDetail(index: number): void {
  removeDetail(index)
}

function handleClear(): void {
  uni.showModal({
    title: '提示',
    content: '确定清空所有扫码明细吗？',
    success: (res) => {
      if (res.confirm) {
        reset()
        setJobType('return')
      }
    }
  })
}

async function handleSubmit(): Promise<void> {
  if (!canSubmit.value) {
    if (details.value.length === 0) {
      uni.showToast({ title: '请先添加扫码明细', icon: 'none' })
    }
    return
  }

  uni.showModal({
    title: '提示',
    content: '确定提交归还单吗？',
    success: async (res) => {
      if (res.confirm) {
        const orderId = await submitOrder()
        if (orderId) {
          setTimeout(() => { uni.navigateBack() }, 1500)
        }
      }
    }
  })
}

onLoad(() => {
  setJobType('return')
})

onBeforeUnmount(() => {
  reset()
})
</script>

<style lang="scss" scoped>
.scan-return-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: $bg-color-page;
  padding-bottom: 140rpx;

  &__scan {
    padding: $spacing-md $spacing-lg;
    background-color: $bg-color-card;
  }

  &__details { flex: 1; padding: $spacing-md $spacing-lg; }

  &__details-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: $spacing-md;
  }

  &__details-title { font-size: $font-size-lg; color: $text-color-primary; font-weight: 600; }
  &__details-count { font-size: $font-size-sm; color: $text-color-secondary; }
  &__details-list { display: flex; flex-direction: column; }

  &__footer {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    display: flex;
    align-items: center;
    padding: $spacing-md $spacing-lg;
    background-color: $bg-color-card;
    box-shadow: 0 -2rpx 8rpx rgba(0, 0, 0, 0.06);
    gap: $spacing-md;
  }

  &__btn {
    flex: 1;
    height: 80rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: $border-radius-md;
    cursor: pointer;

    &--clear { background-color: $bg-color-grey; border: 2rpx solid $border-color-base; }
    &--submit { background-color: $color-primary; }
    &--disabled { opacity: 0.5; }
  }

  &__btn-text {
    font-size: $font-size-lg;
    color: $text-color-regular;
    &--submit { color: $text-color-inverse; font-weight: 600; }
  }
}
</style>

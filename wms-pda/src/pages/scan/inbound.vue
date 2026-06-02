<!--
  WMS-PDA 入库扫码页面
  库房选择 + ScanInput扫码输入 + 扫码明细列表 + 明细删除 + 提交
-->
<template>
  <view class="scan-inbound-page">
    <!-- 库房选择 -->
    <view class="scan-inbound-page__field">
      <text class="scan-inbound-page__label">选择库房</text>
      <picker
        class="scan-inbound-page__picker"
        :range="warehouses"
        range-key="warehouseName"
        @change="handleWarehouseChange"
      >
        <view class="scan-inbound-page__picker-value">
          <text v-if="selectedWarehouse">{{ selectedWarehouse.warehouseName }}</text>
          <text v-else class="scan-inbound-page__placeholder">请选择库房</text>
        </view>
      </picker>
    </view>

    <!-- 扫码输入 -->
    <view class="scan-inbound-page__scan">
      <ScanInput
        v-model="scanCode"
        placeholder="请扫码或输入编码"
        :disabled="!selectedWarehouse"
        @scan="handleScan"
      />
    </view>

    <!-- 扫码明细列表 -->
    <view class="scan-inbound-page__details">
      <view class="scan-inbound-page__details-header">
        <text class="scan-inbound-page__details-title">扫码明细</text>
        <text class="scan-inbound-page__details-count">共 {{ details.length }} 条</text>
      </view>

      <view v-if="details.length > 0" class="scan-inbound-page__details-list">
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
    <view class="scan-inbound-page__footer">
      <view
        class="scan-inbound-page__btn scan-inbound-page__btn--clear"
        @click="handleClear"
        v-if="details.length > 0"
      >
        <text class="scan-inbound-page__btn-text">清空</text>
      </view>
      <view
        class="scan-inbound-page__btn scan-inbound-page__btn--submit"
        :class="{ 'scan-inbound-page__btn--disabled': !canSubmit }"
        @click="handleSubmit"
      >
        <text class="scan-inbound-page__btn-text scan-inbound-page__btn-text--submit">
          {{ scanning ? '扫码中...' : '提交' }}
        </text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
/**
 * 入库扫码页面
 * 选库房 → 扫码 → 添加明细 → 重复检测 → 删除 → 提交
 */
import { ref, computed, onBeforeUnmount } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import type { WarehouseVo } from '@/utils/constants'
import { useScan } from '@/composables/useScan'
import { getWarehouseListApi } from '@/api/warehouse'
import ScanInput from '@/components/ScanInput.vue'
import DetailItem from '@/components/DetailItem.vue'
import EmptyState from '@/components/EmptyState.vue'

/** useScan组合函数 */
const { details, scanning, canSubmit, setJobType, setWarehouse, scanCode, removeDetail, submitOrder, reset } = useScan()

/** 库房列表 */
const warehouses = ref<WarehouseVo[]>([])
/** 选中的库房 */
const selectedWarehouse = ref<WarehouseVo | null>(null)
/** 扫码输入值 */
const scanCode = ref<string>('')

/**
 * 加载库房列表
 */
async function loadWarehouses(): Promise<void> {
  try {
    warehouses.value = await getWarehouseListApi()
  } catch {
    warehouses.value = []
  }
}

/**
 * 处理库房选择变更
 */
function handleWarehouseChange(e: { detail: { value: number } }): void {
  const index = e.detail.value
  if (index >= 0 && index < warehouses.value.length) {
    selectedWarehouse.value = warehouses.value[index]
    setWarehouse(warehouses.value[index].id)
  }
}

/**
 * 处理扫码事件
 */
async function handleScan(code: string): Promise<void> {
  if (!selectedWarehouse.value) {
    uni.showToast({ title: '请先选择库房', icon: 'none' })
    return
  }
  await scanCode(code)
}

/**
 * 处理删除明细
 */
function handleRemoveDetail(index: number): void {
  removeDetail(index)
}

/**
 * 处理清空明细
 */
function handleClear(): void {
  uni.showModal({
    title: '提示',
    content: '确定清空所有扫码明细吗？',
    success: (res) => {
      if (res.confirm) {
        reset()
        // 重新设置作业类型和库房
        setJobType('inbound')
        if (selectedWarehouse.value) {
          setWarehouse(selectedWarehouse.value.id)
        }
      }
    }
  })
}

/**
 * 处理提交
 */
async function handleSubmit(): Promise<void> {
  if (!canSubmit.value) {
    if (details.value.length === 0) {
      uni.showToast({ title: '请先添加扫码明细', icon: 'none' })
    }
    return
  }

  uni.showModal({
    title: '提示',
    content: '确定提交入库单吗？',
    success: async (res) => {
      if (res.confirm) {
        const orderId = await submitOrder()
        if (orderId) {
          // 提交成功，返回首页
          setTimeout(() => {
            uni.navigateBack()
          }, 1500)
        }
      }
    }
  })
}

/**
 * 页面加载时初始化
 */
onLoad(() => {
  setJobType('inbound')
  loadWarehouses()
})

/**
 * 页面卸载时重置状态
 */
onBeforeUnmount(() => {
  reset()
})
</script>

<style lang="scss" scoped>
.scan-inbound-page {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: $bg-color-page;
  padding-bottom: 140rpx;

  &__field {
    padding: $spacing-md $spacing-lg;
    background-color: $bg-color-card;
  }

  &__label {
    font-size: $font-size-md;
    color: $text-color-regular;
    margin-bottom: 8rpx;
    display: block;
  }

  &__picker {
    width: 100%;
  }

  &__picker-value {
    height: 72rpx;
    display: flex;
    align-items: center;
    padding: 0 $spacing-sm;
    border: 2rpx solid $border-color-base;
    border-radius: $border-radius-md;
    background-color: $bg-color-page;
    font-size: $font-size-md;
    color: $text-color-primary;
  }

  &__placeholder {
    color: $text-color-placeholder;
  }

  &__scan {
    padding: $spacing-md $spacing-lg;
    background-color: $bg-color-card;
    margin-top: 2rpx;
  }

  &__details {
    flex: 1;
    padding: $spacing-md $spacing-lg;
  }

  &__details-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: $spacing-md;
  }

  &__details-title {
    font-size: $font-size-lg;
    color: $text-color-primary;
    font-weight: 600;
  }

  &__details-count {
    font-size: $font-size-sm;
    color: $text-color-secondary;
  }

  &__details-list {
    display: flex;
    flex-direction: column;
  }

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

    &--clear {
      background-color: $bg-color-grey;
      border: 2rpx solid $border-color-base;
    }

    &--submit {
      background-color: $color-primary;
    }

    &--disabled {
      opacity: 0.5;
    }
  }

  &__btn-text {
    font-size: $font-size-lg;
    color: $text-color-regular;

    &--submit {
      color: $text-color-inverse;
      font-weight: 600;
    }
  }
}
</style>

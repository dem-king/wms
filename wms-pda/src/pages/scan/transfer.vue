<!--
  WMS-PDA 调拨扫码页面
  源库房+目标库房选择 + 扫码校验labelStatus=1(在库)才可调拨
-->
<template>
  <view class="scan-transfer-page">
    <!-- 源库房选择 -->
    <view class="scan-transfer-page__field">
      <text class="scan-transfer-page__label">源库房</text>
      <picker
        class="scan-transfer-page__picker"
        :range="warehouses"
        range-key="warehouseName"
        @change="handleFromWarehouseChange"
      >
        <view class="scan-transfer-page__picker-value">
          <text v-if="fromWarehouse">{{ fromWarehouse.warehouseName }}</text>
          <text v-else class="scan-transfer-page__placeholder">请选择源库房</text>
        </view>
      </picker>
    </view>

    <!-- 目标库房选择 -->
    <view class="scan-transfer-page__field">
      <text class="scan-transfer-page__label">目标库房</text>
      <picker
        class="scan-transfer-page__picker"
        :range="warehouses"
        range-key="warehouseName"
        @change="handleToWarehouseChange"
      >
        <view class="scan-transfer-page__picker-value">
          <text v-if="toWarehouse">{{ toWarehouse.warehouseName }}</text>
          <text v-else class="scan-transfer-page__placeholder">请选择目标库房</text>
        </view>
      </picker>
    </view>

    <!-- 扫码输入 -->
    <view class="scan-transfer-page__scan">
      <ScanInput
        v-model="scanCodeInput"
        placeholder="请扫码或输入编码"
        :disabled="!fromWarehouse || !toWarehouse"
        @scan="handleScan"
      />
    </view>

    <!-- 扫码明细列表 -->
    <view class="scan-transfer-page__details">
      <view class="scan-transfer-page__details-header">
        <text class="scan-transfer-page__details-title">扫码明细</text>
        <text class="scan-transfer-page__details-count">共 {{ details.length }} 条</text>
      </view>

      <view v-if="details.length > 0" class="scan-transfer-page__details-list">
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
    <view class="scan-transfer-page__footer">
      <view
        class="scan-transfer-page__btn scan-transfer-page__btn--clear"
        @click="handleClear"
        v-if="details.length > 0"
      >
        <text class="scan-transfer-page__btn-text">清空</text>
      </view>
      <view
        class="scan-transfer-page__btn scan-transfer-page__btn--submit"
        :class="{ 'scan-transfer-page__btn--disabled': !canSubmit }"
        @click="handleSubmit"
      >
        <text class="scan-transfer-page__btn-text scan-transfer-page__btn-text--submit">
          {{ scanning ? '扫码中...' : '提交' }}
        </text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
/**
 * 调拨扫码页面
 * 选源库房+目标库房 → 扫码 → 校验labelStatus=1(在库) → 添加明细 → 删除 → 提交
 * 源库房与目标库房不能相同
 */
import { ref, onBeforeUnmount } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import type { WarehouseVo } from '@/utils/constants'
import { useScan } from '@/composables/useScan'
import { getWarehouseListApi } from '@/api/warehouse'
import ScanInput from '@/components/ScanInput.vue'
import DetailItem from '@/components/DetailItem.vue'
import EmptyState from '@/components/EmptyState.vue'

const { details, scanning, canSubmit, setJobType, setWarehouse, setToWarehouse, scanCode, removeDetail, submitOrder, reset } = useScan()

const warehouses = ref<WarehouseVo[]>([])
const fromWarehouse = ref<WarehouseVo | null>(null)
const toWarehouse = ref<WarehouseVo | null>(null)
const scanCodeInput = ref<string>('')

async function loadWarehouses(): Promise<void> {
  try {
    warehouses.value = await getWarehouseListApi()
  } catch {
    warehouses.value = []
  }
}

function handleFromWarehouseChange(e: { detail: { value: number } }): void {
  const index = e.detail.value
  if (index >= 0 && index < warehouses.value.length) {
    fromWarehouse.value = warehouses.value[index]
    setWarehouse(warehouses.value[index].id)
  }
}

function handleToWarehouseChange(e: { detail: { value: number } }): void {
  const index = e.detail.value
  if (index >= 0 && index < warehouses.value.length) {
    toWarehouse.value = warehouses.value[index]
    setToWarehouse(warehouses.value[index].id)
  }
}

/**
 * 校验源库房与目标库房不能相同
 */
function validateWarehouses(): boolean {
  if (!fromWarehouse.value || !toWarehouse.value) {
    uni.showToast({ title: '请选择源库房和目标库房', icon: 'none' })
    return false
  }
  if (fromWarehouse.value.id === toWarehouse.value.id) {
    uni.showToast({ title: '源库房与目标库房不能相同', icon: 'none', duration: 2000 })
    return false
  }
  return true
}

async function handleScan(code: string): Promise<void> {
  if (!validateWarehouses()) return
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
        setJobType('transfer')
        if (fromWarehouse.value) setWarehouse(fromWarehouse.value.id)
        if (toWarehouse.value) setToWarehouse(toWarehouse.value.id)
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

  if (!validateWarehouses()) return

  uni.showModal({
    title: '提示',
    content: '确定提交调拨单吗？',
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
  setJobType('transfer')
  loadWarehouses()
})

onBeforeUnmount(() => {
  reset()
})
</script>

<style lang="scss" scoped>
.scan-transfer-page {
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

  &__picker { width: 100%; }

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

  &__placeholder { color: $text-color-placeholder; }

  &__scan {
    padding: $spacing-md $spacing-lg;
    background-color: $bg-color-card;
    margin-top: 2rpx;
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

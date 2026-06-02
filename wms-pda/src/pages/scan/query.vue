<!--
  WMS-PDA 查询扫码页面（TabBar页）
  扫码后展示标签详情与物品信息，不产生业务单据
-->
<template>
  <view class="scan-query-page">
    <!-- 扫码输入 -->
    <view class="scan-query-page__scan">
      <ScanInput
        v-model="scanCodeInput"
        placeholder="请扫码或输入编码查询"
        @scan="handleScan"
      />
    </view>

    <!-- 标签详情展示 -->
    <view v-if="labelInfo" class="scan-query-page__result">
      <view class="scan-query-page__card">
        <text class="scan-query-page__card-title">标签信息</text>

        <view class="scan-query-page__row">
          <text class="scan-query-page__label">标签编号</text>
          <text class="scan-query-page__value scan-query-page__value--mono">{{ labelInfo.labelNo }}</text>
        </view>

        <view class="scan-query-page__row">
          <text class="scan-query-page__label">标签类型</text>
          <text class="scan-query-page__value">{{ labelTypeText }}</text>
        </view>

        <view class="scan-query-page__row">
          <text class="scan-query-page__label">标签状态</text>
          <StatusTag :status="labelInfo.labelStatus" type="label" />
        </view>
      </view>

      <view class="scan-query-page__card">
        <text class="scan-query-page__card-title">物品信息</text>

        <view class="scan-query-page__row">
          <text class="scan-query-page__label">物品名称</text>
          <text class="scan-query-page__value">{{ labelInfo.itemName }}</text>
        </view>

        <view class="scan-query-page__row">
          <text class="scan-query-page__label">物品编码</text>
          <text class="scan-query-page__value scan-query-page__value--mono">{{ labelInfo.itemCode }}</text>
        </view>

        <view class="scan-query-page__row">
          <text class="scan-query-page__label">批次号</text>
          <text class="scan-query-page__value">{{ labelInfo.batchNo || '-' }}</text>
        </view>

        <view class="scan-query-page__row" v-if="labelInfo.rfidCode">
          <text class="scan-query-page__label">RFID码</text>
          <text class="scan-query-page__value scan-query-page__value--mono">{{ labelInfo.rfidCode }}</text>
        </view>
      </view>
    </view>

    <!-- 空状态 -->
    <EmptyState v-else text="请扫码查询标签信息" />
  </view>
</template>

<script setup lang="ts">
/**
 * 查询扫码页面
 * 扫码 → 调用GET /api/labels/scan/{code} → 展示标签详情与物品信息
 * 不产生业务单据
 */
import { ref, computed } from 'vue'
import type { ElectronicLabelVo, LabelTypeValue } from '@/utils/constants'
import { LabelType } from '@/utils/constants'
import { labelScanApi } from '@/api/scan'
import { useVibrate } from '@/composables/useVibrate'
import { useSound } from '@/composables/useSound'
import ScanInput from '@/components/ScanInput.vue'
import StatusTag from '@/components/StatusTag.vue'
import EmptyState from '@/components/EmptyState.vue'

const { shortVibrate, longVibrate } = useVibrate()
const { playSuccess, playError } = useSound()

/** 扫码输入值 */
const scanCodeInput = ref<string>('')
/** 标签信息 */
const labelInfo = ref<ElectronicLabelVo | null>(null)
/** 是否正在查询 */
const querying = ref<boolean>(false)

/** 标签类型文字映射 */
const labelTypeText = computed(() => {
  if (!labelInfo.value) return ''
  const typeMap: Record<number, string> = {
    [LabelType.QRCODE]: '二维码',
    [LabelType.BARCODE]: '条形码',
    [LabelType.RFID]: 'RFID'
  }
  return typeMap[labelInfo.value.labelType] || '未知'
})

/**
 * 处理扫码事件
 * 调用标签查询接口，展示标签详情
 */
async function handleScan(code: string): Promise<void> {
  if (querying.value) return
  querying.value = true

  try {
    const result = await labelScanApi(code)
    labelInfo.value = result

    // 查询成功反馈
    shortVibrate()
    playSuccess()
  } catch {
    // 查询失败
    labelInfo.value = null
    longVibrate()
    playError()

    uni.showToast({
      title: '未识别的标签',
      icon: 'none',
      duration: 2000
    })
  } finally {
    querying.value = false
  }
}
</script>

<style lang="scss" scoped>
.scan-query-page {
  min-height: 100vh;
  background-color: $bg-color-page;

  &__scan {
    padding: $spacing-md $spacing-lg;
    background-color: $bg-color-card;
  }

  &__result {
    padding: $spacing-md $spacing-lg;
  }

  &__card {
    background-color: $bg-color-card;
    border-radius: $border-radius-md;
    padding: $spacing-md;
    margin-bottom: $spacing-md;
    box-shadow: $shadow-sm;
  }

  &__card-title {
    font-size: $font-size-lg;
    color: $text-color-primary;
    font-weight: 600;
    margin-bottom: $spacing-md;
    display: block;
    padding-bottom: $spacing-sm;
    border-bottom: 1rpx solid $border-color-light;
  }

  &__row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: 8rpx 0;
  }

  &__label {
    font-size: $font-size-md;
    color: $text-color-secondary;
    flex-shrink: 0;
    margin-right: $spacing-md;
  }

  &__value {
    font-size: $font-size-md;
    color: $text-color-primary;
    text-align: right;
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;

    &--mono {
      font-family: 'Courier New', monospace;
      letter-spacing: 1rpx;
    }
  }
}
</style>

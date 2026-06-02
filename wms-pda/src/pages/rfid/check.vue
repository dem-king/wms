<!--
  WMS-PDA RFID盘点页面
  完整盘点流程：选库房→开始读取→实时计数→停止→上报→差异对比→确认提交
  集成RfidReader组件和useRfid组合函数，支持降级手动输入EPC码
-->
<template>
  <view class="rfid-check-page">
    <!-- 库房选择 -->
    <view class="rfid-check-page__section">
      <text class="rfid-check-page__label">选择库房</text>
      <picker
        class="rfid-check-page__picker"
        :range="warehouseList"
        range-key="warehouseName"
        :value="selectedWarehouseIndex"
        @change="handleWarehouseChange"
      >
        <view class="rfid-check-page__picker-value">
          <text v-if="selectedWarehouse" class="rfid-check-page__picker-text">
            {{ selectedWarehouse.warehouseName }}
          </text>
          <text v-else class="rfid-check-page__picker-placeholder">请选择库房</text>
          <text class="rfid-check-page__picker-arrow">▼</text>
        </view>
      </picker>
    </view>

    <!-- RFID读取区域 -->
    <view class="rfid-check-page__section">
      <RfidReader
        :reading="rfidReading"
        :read-count="rfidReadCount"
        :hardware-available="rfidHardwareAvailable"
        @start="handleStartRead"
        @stop="handleStopRead"
      />
    </view>

    <!-- 降级方案：手动输入EPC码（硬件不可用时显示） -->
    <view class="rfid-check-page__manual" v-if="!rfidHardwareAvailable && !checkResult">
      <text class="rfid-check-page__manual-title">手动输入EPC码</text>
      <view class="rfid-check-page__manual-input">
        <input
          class="rfid-check-page__manual-field"
          v-model="manualEpcInput"
          placeholder="请输入EPC编码"
          confirm-type="done"
          @confirm="handleManualAdd"
        />
        <button class="rfid-check-page__manual-btn" @click="handleManualAdd">添加</button>
      </view>
      <!-- 手动输入的标签列表 -->
      <view class="rfid-check-page__manual-list" v-if="manualEpcList.length > 0">
        <view
          class="rfid-check-page__manual-item"
          v-for="(epc, index) in manualEpcList"
          :key="index"
        >
          <text class="rfid-check-page__manual-epc">{{ epc }}</text>
          <text class="rfid-check-page__manual-remove" @click="handleManualRemove(index)">✕</text>
        </view>
      </view>
      <!-- 手动输入后上报按钮 -->
      <button
        class="rfid-check-page__submit-btn rfid-check-page__submit-btn--primary"
        v-if="manualEpcList.length > 0 && !checkResult"
        :disabled="submitting"
        @click="handleManualSubmit"
      >
        上报读取结果（{{ manualEpcList.length }}个标签）
      </button>
    </view>

    <!-- 读取结果列表（只读） -->
    <view class="rfid-check-page__section" v-if="rfidReadCount > 0 && !checkResult">
      <view class="rfid-check-page__result-header">
        <text class="rfid-check-page__result-title">读取结果</text>
        <text class="rfid-check-page__result-count">共 {{ rfidReadCount }} 个标签</text>
      </view>
      <scroll-view class="rfid-check-page__result-list" scroll-y>
        <view
          class="rfid-check-page__result-item"
          v-for="(epc, index) in epcTagList"
          :key="index"
        >
          <text class="rfid-check-page__result-index">{{ index + 1 }}</text>
          <text class="rfid-check-page__result-epc">{{ epc }}</text>
        </view>
      </scroll-view>
      <!-- 上报按钮 -->
      <button
        class="rfid-check-page__submit-btn rfid-check-page__submit-btn--primary"
        :disabled="submitting"
        @click="handleReportRead"
      >
        上报读取结果
      </button>
    </view>

    <!-- 差异对比结果 -->
    <view class="rfid-check-page__section" v-if="checkResult">
      <!-- 统计概览 -->
      <view class="rfid-check-page__summary">
        <view class="rfid-check-page__summary-item">
          <text class="rfid-check-page__summary-label">系统在库</text>
          <text class="rfid-check-page__summary-value">{{ checkResult.systemCount }}</text>
        </view>
        <view class="rfid-check-page__summary-item">
          <text class="rfid-check-page__summary-label">实际读取</text>
          <text class="rfid-check-page__summary-value">{{ checkResult.actualCount }}</text>
        </view>
        <view class="rfid-check-page__summary-item">
          <text class="rfid-check-page__summary-label">匹配数量</text>
          <text class="rfid-check-page__summary-value rfid-check-page__summary-value--success">
            {{ checkResult.matchCount }}
          </text>
        </view>
      </view>

      <!-- 盘盈明细（实际有系统无） -->
      <view class="rfid-check-page__diff" v-if="checkResult.surplusDetails.length > 0">
        <view class="rfid-check-page__diff-header">
          <text class="rfid-check-page__diff-title rfid-check-page__diff-title--surplus">
            盘盈（{{ checkResult.surplusDetails.length }}项）
          </text>
          <text class="rfid-check-page__diff-desc">实际有，系统无</text>
        </view>
        <view
          class="rfid-check-page__diff-item rfid-check-page__diff-item--surplus"
          v-for="(item, index) in checkResult.surplusDetails"
          :key="'surplus-' + index"
        >
          <view class="rfid-check-page__diff-row">
            <text class="rfid-check-page__diff-name">{{ item.itemName }}</text>
            <text class="rfid-check-page__diff-code">{{ item.itemCode }}</text>
          </view>
          <view class="rfid-check-page__diff-row">
            <text class="rfid-check-page__diff-label">标签：{{ item.labelNo }}</text>
            <text class="rfid-check-page__diff-qty">
              系统{{ item.systemQty }} → 实际{{ item.actualQty }}
            </text>
          </view>
        </view>
      </view>

      <!-- 盘亏明细（系统有实际无） -->
      <view class="rfid-check-page__diff" v-if="checkResult.deficitDetails.length > 0">
        <view class="rfid-check-page__diff-header">
          <text class="rfid-check-page__diff-title rfid-check-page__diff-title--deficit">
            盘亏（{{ checkResult.deficitDetails.length }}项）
          </text>
          <text class="rfid-check-page__diff-desc">系统有，实际无</text>
        </view>
        <view
          class="rfid-check-page__diff-item rfid-check-page__diff-item--deficit"
          v-for="(item, index) in checkResult.deficitDetails"
          :key="'deficit-' + index"
        >
          <view class="rfid-check-page__diff-row">
            <text class="rfid-check-page__diff-name">{{ item.itemName }}</text>
            <text class="rfid-check-page__diff-code">{{ item.itemCode }}</text>
          </view>
          <view class="rfid-check-page__diff-row">
            <text class="rfid-check-page__diff-label">标签：{{ item.labelNo }}</text>
            <text class="rfid-check-page__diff-qty">
              系统{{ item.systemQty }} → 实际{{ item.actualQty }}
            </text>
          </view>
        </view>
      </view>

      <!-- 无差异提示 -->
      <view
        class="rfid-check-page__no-diff"
        v-if="checkResult.surplusDetails.length === 0 && checkResult.deficitDetails.length === 0"
      >
        <text class="rfid-check-page__no-diff-text">✓ 盘点一致，无差异</text>
      </view>

      <!-- 操作按钮 -->
      <view class="rfid-check-page__actions">
        <button
          class="rfid-check-page__submit-btn rfid-check-page__submit-btn--default"
          @click="handleReset"
        >
          重新盘点
        </button>
        <button
          class="rfid-check-page__submit-btn rfid-check-page__submit-btn--primary"
          :disabled="submitting"
          @click="handleSubmitCheck"
        >
          {{ submitting ? '提交中...' : '确认提交' }}
        </button>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
/**
 * RFID盘点页面
 * 完整盘点流程：选库房→开始读取→实时计数→停止→上报→差异对比→确认提交
 * 集成RfidReader组件和useRfid组合函数
 * RFID硬件不可用时提供手动输入EPC码的降级方案
 */
import { ref, computed, onMounted } from 'vue'
import { useRfid } from '@/composables/useRfid'
import { useSound } from '@/composables/useSound'
import { useVibrate } from '@/composables/useVibrate'
import { rfidBatchReadApi, stockCheckApi } from '@/api/pda'
import { getWarehouseListApi } from '@/api/warehouse'
import RfidReader from '@/components/RfidReader.vue'
import type {
  WarehouseVo,
  RfidBatchReadResultVo,
  StockCheckDetailDto
} from '@/utils/constants'
import { CheckType, BizConstants } from '@/utils/constants'

// ==================== 组合函数 ====================

const { reading: rfidReading, readCount: rfidReadCount, hardwareAvailable: rfidHardwareAvailable, epcTags, init: rfidInit, startRead, stopRead } = useRfid()
const { playSuccess, playError } = useSound()
const { shortVibrate, longVibrate } = useVibrate()

// ==================== 页面状态 ====================

/** 库房列表 */
const warehouseList = ref<WarehouseVo[]>([])
/** 选中的库房索引 */
const selectedWarehouseIndex = ref<number>(-1)
/** 选中的库房 */
const selectedWarehouse = computed<WarehouseVo | null>(() => {
  if (selectedWarehouseIndex.value >= 0 && selectedWarehouseIndex.value < warehouseList.value.length) {
    return warehouseList.value[selectedWarehouseIndex.value]
  }
  return null
})

/** 是否正在上报/提交 */
const submitting = ref<boolean>(false)
/** 对比结果 */
const checkResult = ref<RfidBatchReadResultVo | null>(null)
/** 读取开始时间（用于计算读取耗时） */
let readStartTime = 0

// ==================== 降级方案：手动输入 ====================

/** 手动输入的EPC码 */
const manualEpcInput = ref<string>('')
/** 手动输入的EPC码列表 */
const manualEpcList = ref<string[]>([])

/** EPC码列表（用于展示） */
const epcTagList = computed<string[]>(() => {
  return Array.from(epcTags.value)
})

// ==================== 库房选择 ====================

/**
 * 处理库房选择变更
 *
 * @param e picker事件
 */
function handleWarehouseChange(e: { detail: { value: number } }): void {
  selectedWarehouseIndex.value = e.detail.value
}

/**
 * 加载库房列表
 */
async function loadWarehouses(): Promise<void> {
  try {
    const list = await getWarehouseListApi()
    // 仅显示启用状态的库房
    warehouseList.value = list.filter(w => w.status === BizConstants.STATUS_ENABLED)
  } catch (error) {
    console.error('加载库房列表失败:', error)
    uni.showToast({
      title: '加载库房列表失败',
      icon: 'none',
      duration: 2000
    })
  }
}

// ==================== RFID读取 ====================

/**
 * 处理开始读取
 * 校验库房选择后触发RFID批量读取
 */
function handleStartRead(): void {
  // 校验：必须选择库房
  if (!selectedWarehouse.value) {
    uni.showToast({
      title: '请先选择库房',
      icon: 'none',
      duration: 2000
    })
    return
  }

  // 清空之前的对比结果
  checkResult.value = null
  // 记录读取开始时间
  readStartTime = Date.now()

  // 触发RFID批量读取
  startRead({
    onRead: (_epc: string) => {
      // 每读取到新EPC码的回调，UI通过rfidReadCount自动更新
    },
    onComplete: (_tags: string[]) => {
      // 读取完成回调（停止读取后触发）
      // 读取结果已保存在epcTags中，等待用户手动上报
    }
  })
}

/**
 * 处理停止读取
 * 停止后保留已读取数据，等待用户上报
 */
function handleStopRead(): void {
  stopRead()

  // 读取范围内无标签提示
  if (rfidReadCount.value === 0) {
    uni.showToast({
      title: '未读取到任何标签',
      icon: 'none',
      duration: 2000
    })
  }
}

// ==================== 上报读取结果 ====================

/**
 * 上报RFID读取结果
 * 将读取到的EPC码上报后端，获取差异对比结果
 */
async function handleReportRead(): Promise<void> {
  if (!selectedWarehouse.value) {
    uni.showToast({
      title: '请先选择库房',
      icon: 'none',
      duration: 2000
    })
    return
  }

  if (epcTags.value.size === 0) {
    uni.showToast({
      title: '未读取到任何标签',
      icon: 'none',
      duration: 2000
    })
    return
  }

  submitting.value = true
  try {
    // 计算读取耗时（秒）
    const readDuration = readStartTime > 0
      ? Math.round((Date.now() - readStartTime) / 1000)
      : undefined

    const result = await rfidBatchReadApi({
      warehouseId: selectedWarehouse.value.id,
      epcCodes: Array.from(epcTags.value),
      readDuration
    })

    checkResult.value = result
    shortVibrate()
    playSuccess()
  } catch (error) {
    console.error('上报读取结果失败:', error)
    longVibrate()
    playError()
  } finally {
    submitting.value = false
  }
}

// ==================== 盘点提交 ====================

/**
 * 确认提交盘点结果
 * 将差异明细提交后端，创建盘点单
 */
async function handleSubmitCheck(): Promise<void> {
  if (!selectedWarehouse.value || !checkResult.value) {
    return
  }

  // 构建盘点明细：将盘盈和盘亏明细转换为StockCheckDetailDto
  const items: StockCheckDetailDto[] = []

  // 盘盈明细
  for (const detail of checkResult.value.surplusDetails) {
    items.push({
      itemId: detail.itemId,
      systemQty: detail.systemQty,
      actualQty: detail.actualQty
    })
  }

  // 盘亏明细
  for (const detail of checkResult.value.deficitDetails) {
    items.push({
      itemId: detail.itemId,
      systemQty: detail.systemQty,
      actualQty: detail.actualQty
    })
  }

  // 无差异时也需要提交（确认盘点一致）
  if (items.length === 0) {
    // 无差异，提交空明细表示盘点一致
  }

  submitting.value = true
  try {
    const result = await stockCheckApi({
      warehouseId: selectedWarehouse.value.id,
      checkType: CheckType.RFID,
      items
    })

    shortVibrate()
    playSuccess()
    uni.showToast({
      title: '盘点结果已提交',
      icon: 'success',
      duration: 2000
    })

    // 提交成功后延迟返回上一页
    setTimeout(() => {
      uni.navigateBack()
    }, 1500)
  } catch (error) {
    console.error('提交盘点结果失败:', error)
    longVibrate()
    playError()
  } finally {
    submitting.value = false
  }
}

// ==================== 重新盘点 ====================

/**
 * 重置盘点状态，重新开始
 */
function handleReset(): void {
  checkResult.value = null
  epcTags.value = new Set<string>()
  rfidReadCount.value = 0
  manualEpcList.value = []
  manualEpcInput.value = ''
  readStartTime = 0
}

// ==================== 降级方案：手动输入 ====================

/**
 * 手动添加EPC码
 */
function handleManualAdd(): void {
  const epc = manualEpcInput.value.trim()
  if (!epc) {
    return
  }

  // 去重检查
  if (manualEpcList.value.includes(epc)) {
    uni.showToast({
      title: '该EPC码已添加',
      icon: 'none',
      duration: 1500
    })
    return
  }

  manualEpcList.value.push(epc)
  manualEpcInput.value = ''
}

/**
 * 手动移除EPC码
 *
 * @param index 要移除的索引
 */
function handleManualRemove(index: number): void {
  manualEpcList.value.splice(index, 1)
}

/**
 * 手动输入后上报读取结果
 */
async function handleManualSubmit(): Promise<void> {
  if (!selectedWarehouse.value) {
    uni.showToast({
      title: '请先选择库房',
      icon: 'none',
      duration: 2000
    })
    return
  }

  if (manualEpcList.value.length === 0) {
    uni.showToast({
      title: '未输入任何EPC码',
      icon: 'none',
      duration: 2000
    })
    return
  }

  submitting.value = true
  try {
    const result = await rfidBatchReadApi({
      warehouseId: selectedWarehouse.value.id,
      epcCodes: manualEpcList.value
    })

    checkResult.value = result
    // 同步到epcTags以便显示
    epcTags.value = new Set<string>(manualEpcList.value)
    shortVibrate()
    playSuccess()
  } catch (error) {
    console.error('上报读取结果失败:', error)
    longVibrate()
    playError()
  } finally {
    submitting.value = false
  }
}

// ==================== 生命周期 ====================

onMounted(async () => {
  // 加载库房列表
  await loadWarehouses()

  // 初始化RFID模块
  await rfidInit()
})
</script>

<style lang="scss" scoped>
.rfid-check-page {
  min-height: 100vh;
  background-color: $bg-color-page;
  padding: $spacing-md;
  display: flex;
  flex-direction: column;
  gap: $spacing-md;

  // ==================== 通用section ====================
  &__section {
    background-color: $bg-color-card;
    border-radius: $border-radius-lg;
    padding: $spacing-md;
    box-shadow: $shadow-sm;
  }

  // ==================== 库房选择 ====================
  &__label {
    font-size: $font-size-md;
    color: $text-color-primary;
    font-weight: 500;
    margin-bottom: $spacing-sm;
    display: block;
  }

  &__picker {
    width: 100%;
  }

  &__picker-value {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 80rpx;
    padding: 0 $spacing-md;
    background-color: $bg-color-grey;
    border-radius: $border-radius-md;
    border: 2rpx solid $border-color-base;
  }

  &__picker-text {
    font-size: $font-size-lg;
    color: $text-color-primary;
  }

  &__picker-placeholder {
    font-size: $font-size-lg;
    color: $text-color-placeholder;
  }

  &__picker-arrow {
    font-size: $font-size-sm;
    color: $text-color-secondary;
  }

  // ==================== 读取结果列表 ====================
  &__result-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: $spacing-sm;
  }

  &__result-title {
    font-size: $font-size-lg;
    color: $text-color-primary;
    font-weight: 500;
  }

  &__result-count {
    font-size: $font-size-sm;
    color: $text-color-secondary;
  }

  &__result-list {
    max-height: 400rpx;
    margin-bottom: $spacing-md;
  }

  &__result-item {
    display: flex;
    align-items: center;
    gap: $spacing-sm;
    padding: $spacing-sm 0;
    border-bottom: 1rpx solid $border-color-light;

    &:last-child {
      border-bottom: none;
    }
  }

  &__result-index {
    width: 48rpx;
    height: 48rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: $font-size-sm;
    color: $text-color-secondary;
    background-color: $bg-color-grey;
    border-radius: 50%;
    flex-shrink: 0;
  }

  &__result-epc {
    font-size: $font-size-md;
    color: $text-color-primary;
    font-family: monospace;
    word-break: break-all;
  }

  // ==================== 统计概览 ====================
  &__summary {
    display: flex;
    justify-content: space-around;
    padding: $spacing-md 0;
    margin-bottom: $spacing-md;
    border-bottom: 1rpx solid $border-color-light;
  }

  &__summary-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: $spacing-xs;
  }

  &__summary-label {
    font-size: $font-size-sm;
    color: $text-color-secondary;
  }

  &__summary-value {
    font-size: $font-size-xxl;
    font-weight: 700;
    color: $text-color-primary;

    &--success {
      color: $color-success;
    }
  }

  // ==================== 差异明细 ====================
  &__diff {
    margin-bottom: $spacing-md;
  }

  &__diff-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: $spacing-sm;
  }

  &__diff-title {
    font-size: $font-size-lg;
    font-weight: 600;

    &--surplus {
      color: $color-warning;
    }

    &--deficit {
      color: $color-error;
    }
  }

  &__diff-desc {
    font-size: $font-size-sm;
    color: $text-color-secondary;
  }

  &__diff-item {
    padding: $spacing-sm $spacing-md;
    margin-bottom: $spacing-xs;
    border-radius: $border-radius-md;
    border-left: 6rpx solid transparent;

    &--surplus {
      background-color: rgba($color-warning, 0.06);
      border-left-color: $color-warning;
    }

    &--deficit {
      background-color: rgba($color-error, 0.06);
      border-left-color: $color-error;
    }
  }

  &__diff-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
    gap: $spacing-sm;

    & + & {
      margin-top: 4rpx;
    }
  }

  &__diff-name {
    font-size: $font-size-md;
    color: $text-color-primary;
    font-weight: 500;
  }

  &__diff-code {
    font-size: $font-size-sm;
    color: $text-color-secondary;
  }

  &__diff-label {
    font-size: $font-size-sm;
    color: $text-color-regular;
  }

  &__diff-qty {
    font-size: $font-size-sm;
    color: $text-color-regular;
    font-weight: 500;
  }

  // ==================== 无差异提示 ====================
  &__no-diff {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: $spacing-xl 0;
  }

  &__no-diff-text {
    font-size: $font-size-lg;
    color: $color-success;
    font-weight: 500;
  }

  // ==================== 操作按钮 ====================
  &__actions {
    display: flex;
    gap: $spacing-md;
    margin-top: $spacing-md;
  }

  &__submit-btn {
    flex: 1;
    height: 80rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: $border-radius-md;
    font-size: $font-size-lg;
    font-weight: 500;
    border: none;
    transition: opacity 0.2s;

    &--primary {
      background-color: $color-primary;
      color: $text-color-inverse;

      &:active {
        opacity: 0.8;
      }

      &[disabled] {
        background-color: $color-primary-light;
        opacity: 0.5;
      }
    }

    &--default {
      background-color: $bg-color-grey;
      color: $text-color-regular;

      &:active {
        opacity: 0.8;
      }
    }
  }

  // ==================== 降级方案：手动输入 ====================
  &__manual {
    background-color: $bg-color-card;
    border-radius: $border-radius-lg;
    padding: $spacing-md;
    box-shadow: $shadow-sm;
  }

  &__manual-title {
    font-size: $font-size-md;
    color: $text-color-primary;
    font-weight: 500;
    margin-bottom: $spacing-sm;
    display: block;
  }

  &__manual-input {
    display: flex;
    gap: $spacing-sm;
    margin-bottom: $spacing-sm;
  }

  &__manual-field {
    flex: 1;
    height: 72rpx;
    padding: 0 $spacing-sm;
    font-size: $font-size-md;
    color: $text-color-primary;
    background-color: $bg-color-grey;
    border-radius: $border-radius-md;
    border: 2rpx solid $border-color-base;
  }

  &__manual-btn {
    width: 120rpx;
    height: 72rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: $color-primary;
    color: $text-color-inverse;
    font-size: $font-size-md;
    border-radius: $border-radius-md;
    border: none;

    &:active {
      opacity: 0.8;
    }
  }

  &__manual-list {
    max-height: 300rpx;
    overflow-y: auto;
    margin-bottom: $spacing-sm;
  }

  &__manual-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: $spacing-xs $spacing-sm;
    margin-bottom: 4rpx;
    background-color: $bg-color-grey;
    border-radius: $border-radius-sm;
  }

  &__manual-epc {
    font-size: $font-size-sm;
    color: $text-color-primary;
    font-family: monospace;
  }

  &__manual-remove {
    font-size: $font-size-md;
    color: $text-color-secondary;
    padding: 0 $spacing-xs;
  }
}
</style>

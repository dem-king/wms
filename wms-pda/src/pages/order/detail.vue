<!--
  WMS-PDA 单据详情页面
  根据单据类型和ID调用详情接口，展示单据基本信息+明细列表
  草稿状态显示提交按钮，非草稿隐藏/禁用
-->
<template>
  <view class="order-detail-page">
    <!-- 加载中 -->
    <view class="order-detail-page__loading" v-if="loading">
      <text class="order-detail-page__loading-text">加载中...</text>
    </view>

    <template v-else-if="orderData">
      <!-- 单据基本信息 -->
      <view class="order-detail-page__card">
        <view class="order-detail-page__card-header">
          <text class="order-detail-page__order-no">{{ orderData.orderNo }}</text>
          <StatusTag :status="orderData.status" type="order" />
        </view>

        <view class="order-detail-page__row">
          <text class="order-detail-page__label">库房</text>
          <text class="order-detail-page__value">{{ warehouseName }}</text>
        </view>

        <view class="order-detail-page__row">
          <text class="order-detail-page__label">创建时间</text>
          <text class="order-detail-page__value">{{ orderData.createTime }}</text>
        </view>

        <view class="order-detail-page__row">
          <text class="order-detail-page__label">创建人</text>
          <text class="order-detail-page__value">{{ orderData.createBy }}</text>
        </view>

        <view class="order-detail-page__row" v-if="orderData.remark">
          <text class="order-detail-page__label">备注</text>
          <text class="order-detail-page__value">{{ orderData.remark }}</text>
        </view>

        <!-- 调拨单特有字段 -->
        <template v-if="orderType === 'transfer'">
          <view class="order-detail-page__row">
            <text class="order-detail-page__label">源库房</text>
            <text class="order-detail-page__value">{{ (orderData as any).fromWarehouseName }}</text>
          </view>
          <view class="order-detail-page__row">
            <text class="order-detail-page__label">目标库房</text>
            <text class="order-detail-page__value">{{ (orderData as any).toWarehouseName }}</text>
          </view>
        </template>

        <!-- 报废单特有字段 -->
        <view class="order-detail-page__row" v-if="orderType === 'scrap' && (orderData as any).scrapReason">
          <text class="order-detail-page__label">报废原因</text>
          <text class="order-detail-page__value">{{ (orderData as any).scrapReason }}</text>
        </view>
      </view>

      <!-- 明细列表 -->
      <view class="order-detail-page__details">
        <text class="order-detail-page__details-title">明细列表</text>

        <view v-if="orderDetails.length > 0" class="order-detail-page__details-list">
          <view
            class="order-detail-page__detail-item"
            v-for="(detail, index) in orderDetails"
            :key="detail.id || index"
          >
            <view class="order-detail-page__detail-row">
              <text class="order-detail-page__detail-name">{{ detail.itemName }}</text>
              <text class="order-detail-page__detail-qty">× {{ detail.quantity }}</text>
            </view>
            <view class="order-detail-page__detail-row">
              <text class="order-detail-page__detail-code">{{ detail.itemCode }}</text>
            </view>
          </view>
        </view>
        <EmptyState v-else text="暂无明细" />
      </view>

      <!-- 底部提交按钮（仅草稿状态） -->
      <view class="order-detail-page__footer" v-if="orderData.status === OrderStatus.DRAFT">
        <view
          class="order-detail-page__submit-btn"
          :class="{ 'order-detail-page__submit-btn--disabled': submitting }"
          @click="handleSubmit"
        >
          <text class="order-detail-page__submit-btn-text">
            {{ submitting ? '提交中...' : '提交单据' }}
          </text>
        </view>
      </view>
    </template>
  </view>
</template>

<script setup lang="ts">
/**
 * 单据详情页面
 * 根据单据类型和ID调用详情接口
 * 展示单据基本信息+明细列表
 * 草稿状态显示提交按钮，非草稿隐藏/禁用
 */
import { ref, computed } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { OrderStatus } from '@/utils/constants'
import {
  getInboundDetailApi, submitInboundApi,
  getOutboundDetailApi, submitOutboundApi,
  getReturnDetailApi, submitReturnApi,
  getTransferDetailApi, submitTransferApi,
  getScrapDetailApi, submitScrapApi
} from '@/api/order'
import { useVibrate } from '@/composables/useVibrate'
import { useSound } from '@/composables/useSound'
import StatusTag from '@/components/StatusTag.vue'
import EmptyState from '@/components/EmptyState.vue'

const { shortVibrate, longVibrate } = useVibrate()
const { playSuccess, playError } = useSound()

/** 单据类型 */
const orderType = ref<string>('inbound')
/** 单据ID */
const orderId = ref<number>(0)
/** 单据数据 */
const orderData = ref<any>(null)
/** 是否正在加载 */
const loading = ref<boolean>(false)
/** 是否正在提交 */
const submitting = ref<boolean>(false)

/** 库房名称（根据类型取不同字段） */
const warehouseName = computed(() => {
  if (!orderData.value) return ''
  if (orderType.value === 'transfer') {
    return `${(orderData.value as any).fromWarehouseName} → ${(orderData.value as any).toWarehouseName}`
  }
  return orderData.value.warehouseName || ''
})

/** 明细列表 */
const orderDetails = computed(() => {
  if (!orderData.value || !orderData.value.details) return []
  return orderData.value.details
})

/**
 * 加载单据详情
 */
async function loadOrderDetail(): Promise<void> {
  loading.value = true
  try {
    switch (orderType.value) {
      case 'inbound':
        orderData.value = await getInboundDetailApi(orderId.value)
        break
      case 'outbound':
        orderData.value = await getOutboundDetailApi(orderId.value)
        break
      case 'return':
        orderData.value = await getReturnDetailApi(orderId.value)
        break
      case 'transfer':
        orderData.value = await getTransferDetailApi(orderId.value)
        break
      case 'scrap':
        orderData.value = await getScrapDetailApi(orderId.value)
        break
      default:
        orderData.value = null
    }
  } catch {
    orderData.value = null
    uni.showToast({ title: '单据不存在', icon: 'none', duration: 2000 })
    setTimeout(() => { uni.navigateBack() }, 2000)
  } finally {
    loading.value = false
  }
}

/**
 * 提交单据
 */
async function handleSubmit(): Promise<void> {
  if (submitting.value) return

  uni.showModal({
    title: '提示',
    content: '确定提交此单据吗？',
    success: async (res) => {
      if (!res.confirm) return

      submitting.value = true
      try {
        switch (orderType.value) {
          case 'inbound':
            await submitInboundApi(orderId.value)
            break
          case 'outbound':
            await submitOutboundApi(orderId.value)
            break
          case 'return':
            await submitReturnApi(orderId.value)
            break
          case 'transfer':
            await submitTransferApi(orderId.value)
            break
          case 'scrap':
            await submitScrapApi(orderId.value)
            break
        }

        // 提交成功反馈
        shortVibrate()
        playSuccess()
        uni.showToast({ title: '提交成功', icon: 'success', duration: 1500 })

        // 刷新详情
        setTimeout(() => {
          loadOrderDetail()
        }, 1500)
      } catch {
        longVibrate()
        playError()
        uni.showToast({ title: '提交失败', icon: 'none', duration: 2000 })
      } finally {
        submitting.value = false
      }
    }
  })
}

/**
 * 页面加载时获取参数并加载数据
 */
onLoad((options: Record<string, string> | undefined) => {
  if (options) {
    orderType.value = options.type || 'inbound'
    orderId.value = Number(options.id) || 0
  }
  if (orderId.value) {
    loadOrderDetail()
  }
})
</script>

<style lang="scss" scoped>
.order-detail-page {
  min-height: 100vh;
  background-color: $bg-color-page;
  padding-bottom: 140rpx;

  &__loading {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 400rpx;
  }

  &__loading-text {
    font-size: $font-size-md;
    color: $text-color-secondary;
  }

  &__card {
    background-color: $bg-color-card;
    border-radius: $border-radius-md;
    padding: $spacing-md;
    margin: $spacing-md $spacing-lg;
    box-shadow: $shadow-sm;
  }

  &__card-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: $spacing-md;
    padding-bottom: $spacing-sm;
    border-bottom: 1rpx solid $border-color-light;
  }

  &__order-no {
    font-size: $font-size-lg;
    color: $text-color-primary;
    font-weight: 600;
    font-family: 'Courier New', monospace;
  }

  &__row {
    display: flex;
    align-items: flex-start;
    padding: 8rpx 0;
  }

  &__label {
    font-size: $font-size-md;
    color: $text-color-secondary;
    width: 140rpx;
    flex-shrink: 0;
  }

  &__value {
    font-size: $font-size-md;
    color: $text-color-primary;
    flex: 1;
  }

  &__details {
    padding: 0 $spacing-lg;
  }

  &__details-title {
    font-size: $font-size-lg;
    color: $text-color-primary;
    font-weight: 600;
    margin-bottom: $spacing-md;
    display: block;
  }

  &__details-list {
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
  }

  &__detail-item {
    background-color: $bg-color-card;
    border-radius: $border-radius-md;
    padding: $spacing-md;
    box-shadow: $shadow-sm;
  }

  &__detail-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  &__detail-name {
    font-size: $font-size-md;
    color: $text-color-primary;
    font-weight: 500;
  }

  &__detail-qty {
    font-size: $font-size-lg;
    color: $color-primary;
    font-weight: 600;
  }

  &__detail-code {
    font-size: $font-size-sm;
    color: $text-color-secondary;
    font-family: 'Courier New', monospace;
    margin-top: 4rpx;
  }

  &__footer {
    position: fixed;
    bottom: 0;
    left: 0;
    right: 0;
    padding: $spacing-md $spacing-lg;
    background-color: $bg-color-card;
    box-shadow: 0 -2rpx 8rpx rgba(0, 0, 0, 0.06);
  }

  &__submit-btn {
    width: 100%;
    height: 88rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: $color-primary;
    border-radius: $border-radius-md;
    cursor: pointer;

    &--disabled { opacity: 0.5; }
  }

  &__submit-btn-text {
    font-size: $font-size-lg;
    color: $text-color-inverse;
    font-weight: 600;
    letter-spacing: 4rpx;
  }
}
</style>

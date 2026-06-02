<!--
  WMS-PDA 库存预警页面
  调用GET /api/item/stock/alert展示预警列表
  下拉刷新 + 上拉加载 + 空状态
-->
<template>
  <view class="stock-alert-page">
    <!-- 预警列表 -->
    <view class="stock-alert-page__list">
      <view v-if="alertList.length > 0" class="stock-alert-page__items">
        <view
          class="stock-alert-page__item"
          v-for="item in alertList"
          :key="item.id"
        >
          <view class="stock-alert-page__item-content">
            <view class="stock-alert-page__item-header">
              <text class="stock-alert-page__item-name">{{ item.itemName }}</text>
              <view class="stock-alert-page__item-badge">
                <text class="stock-alert-page__item-badge-text">{{ item.alertLevel }}</text>
              </view>
            </view>
            <view class="stock-alert-page__item-row">
              <text class="stock-alert-page__item-code">{{ item.itemCode }}</text>
              <text class="stock-alert-page__item-warehouse">{{ item.warehouseName }}</text>
            </view>
            <view class="stock-alert-page__item-row">
              <text class="stock-alert-page__item-stock">当前库存: {{ item.quantity }}</text>
              <text class="stock-alert-page__item-limit">下限: {{ item.stockLowerLimit }}</text>
            </view>
          </view>
        </view>
      </view>
      <EmptyState v-else-if="!loading" text="暂无库存预警" />

      <!-- 加载更多提示 -->
      <view class="stock-alert-page__loading" v-if="loading">
        <text class="stock-alert-page__loading-text">加载中...</text>
      </view>
      <view class="stock-alert-page__nomore" v-else-if="noMore && alertList.length > 0">
        <text class="stock-alert-page__nomore-text">没有更多了</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
/**
 * 库存预警页面
 * 调用GET /api/item/stock/alert展示预警列表
 * 支持下拉刷新、上拉加载
 */
import { ref } from 'vue'
import { onShow, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'
import type { StockAlertVo } from '@/utils/constants'
import { getStockAlertApi } from '@/api/stock'
import EmptyState from '@/components/EmptyState.vue'

/** 预警列表 */
const alertList = ref<StockAlertVo[]>([])
/** 当前页码 */
const currentPage = ref<number>(1)
/** 每页条数 */
const pageSize = ref<number>(10)
/** 是否正在加载 */
const loading = ref<boolean>(false)
/** 是否没有更多数据 */
const noMore = ref<boolean>(false)

/**
 * 加载预警列表
 *
 * @param reset 是否重置列表
 */
async function loadAlertList(reset: boolean = false): Promise<void> {
  if (loading.value) return

  if (reset) {
    currentPage.value = 1
    noMore.value = false
  }

  loading.value = true
  try {
    const result = await getStockAlertApi({
      current: currentPage.value,
      size: pageSize.value
    })
    if (reset) {
      alertList.value = result.records
    } else {
      alertList.value = [...alertList.value, ...result.records]
    }
    noMore.value = alertList.value.length >= result.total
  } catch {
    // 加载失败，保持当前列表
  } finally {
    loading.value = false
  }
}

onShow(() => {
  loadAlertList(true)
})

onPullDownRefresh(async () => {
  await loadAlertList(true)
  uni.stopPullDownRefresh()
})

onReachBottom(() => {
  if (!noMore.value && !loading.value) {
    currentPage.value++
    loadAlertList()
  }
})
</script>

<style lang="scss" scoped>
.stock-alert-page {
  min-height: 100vh;
  background-color: $bg-color-page;

  &__list { padding: $spacing-md $spacing-lg; }

  &__items {
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
  }

  &__item {
    background-color: $bg-color-card;
    border-radius: $border-radius-md;
    padding: $spacing-md;
    box-shadow: $shadow-sm;
    border-left: 6rpx solid $color-warning;
  }

  &__item-content {
    display: flex;
    flex-direction: column;
    gap: 8rpx;
  }

  &__item-header {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  &__item-name {
    font-size: $font-size-lg;
    color: $text-color-primary;
    font-weight: 600;
    flex: 1;
  }

  &__item-badge {
    padding: 2rpx 12rpx;
    background-color: $color-warning;
    border-radius: $border-radius-sm;
  }

  &__item-badge-text {
    font-size: $font-size-xs;
    color: $text-color-inverse;
    font-weight: 500;
  }

  &__item-row {
    display: flex;
    align-items: center;
    justify-content: space-between;
  }

  &__item-code {
    font-size: $font-size-sm;
    color: $text-color-secondary;
    font-family: 'Courier New', monospace;
  }

  &__item-warehouse {
    font-size: $font-size-sm;
    color: $text-color-secondary;
  }

  &__item-stock {
    font-size: $font-size-md;
    color: $color-error;
    font-weight: 600;
  }

  &__item-limit {
    font-size: $font-size-sm;
    color: $text-color-secondary;
  }

  &__loading, &__nomore {
    display: flex;
    justify-content: center;
    padding: $spacing-lg 0;
  }

  &__loading-text, &__nomore-text {
    font-size: $font-size-sm;
    color: $text-color-secondary;
  }
}
</style>

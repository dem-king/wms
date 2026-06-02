<!--
  WMS-PDA 库存列表页面（TabBar页）
  库存分页列表 + 库房筛选 + 关键词搜索 + 下拉刷新 + 上拉加载 + 库存预警入口
-->
<template>
  <view class="stock-list-page">
    <!-- 搜索栏 -->
    <view class="stock-list-page__search">
      <input
        class="stock-list-page__search-input"
        v-model="keyword"
        placeholder="搜索物品名称/编码"
        confirm-type="search"
        @confirm="handleSearch"
      />
      <view class="stock-list-page__search-btn" @click="handleSearch">
        <text class="stock-list-page__search-btn-text">搜索</text>
      </view>
    </view>

    <!-- 库房筛选 -->
    <view class="stock-list-page__filter">
      <picker
        class="stock-list-page__filter-picker"
        :range="warehouseOptions"
        range-key="warehouseName"
        @change="handleWarehouseFilter"
      >
        <view class="stock-list-page__filter-value">
          <text>{{ selectedWarehouseName || '全部库房' }}</text>
        </view>
      </picker>
      <!-- 库存预警入口 -->
      <view class="stock-list-page__alert-btn" @click="goToAlert">
        <text class="stock-list-page__alert-btn-text">⚠️ 预警</text>
      </view>
    </view>

    <!-- 库存列表 -->
    <view class="stock-list-page__list">
      <view v-if="stockList.length > 0" class="stock-list-page__items">
        <view
          class="stock-list-page__item"
          v-for="item in stockList"
          :key="item.id"
        >
          <view class="stock-list-page__item-content">
            <view class="stock-list-page__item-header">
              <text class="stock-list-page__item-name">{{ item.itemName }}</text>
              <text
                class="stock-list-page__item-qty"
                :class="{ 'stock-list-page__item-qty--alert': item.alert }"
              >{{ item.quantity }}</text>
            </view>
            <view class="stock-list-page__item-row">
              <text class="stock-list-page__item-code">{{ item.itemCode }}</text>
              <text class="stock-list-page__item-locked">锁定: {{ item.lockedQuantity }}</text>
            </view>
          </view>
        </view>
      </view>
      <EmptyState v-else-if="!loading" text="暂无库存数据" />

      <!-- 加载更多提示 -->
      <view class="stock-list-page__loading" v-if="loading">
        <text class="stock-list-page__loading-text">加载中...</text>
      </view>
      <view class="stock-list-page__nomore" v-else-if="noMore && stockList.length > 0">
        <text class="stock-list-page__nomore-text">没有更多了</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
/**
 * 库存列表页面
 * 调用GET /api/item/stock展示库存分页列表
 * 支持库房筛选、关键词搜索、下拉刷新、上拉加载
 */
import { ref } from 'vue'
import { onShow, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'
import type { StockVo, WarehouseVo } from '@/utils/constants'
import { getStockListApi } from '@/api/stock'
import { getWarehouseListApi } from '@/api/warehouse'
import EmptyState from '@/components/EmptyState.vue'

/** 库存列表 */
const stockList = ref<StockVo[]>([])
/** 库房选项（含"全部"） */
const warehouseOptions = ref<WarehouseVo[]>([])
/** 选中的库房ID */
const selectedWarehouseId = ref<number | null>(null)
/** 选中的库房名称 */
const selectedWarehouseName = ref<string>('')
/** 搜索关键词 */
const keyword = ref<string>('')
/** 当前页码 */
const currentPage = ref<number>(1)
/** 每页条数 */
const pageSize = ref<number>(10)
/** 总记录数 */
const total = ref<number>(0)
/** 是否正在加载 */
const loading = ref<boolean>(false)
/** 是否没有更多数据 */
const noMore = ref<boolean>(false)

/**
 * 加载库房列表
 */
async function loadWarehouses(): Promise<void> {
  try {
    warehouseOptions.value = await getWarehouseListApi()
  } catch {
    warehouseOptions.value = []
  }
}

/**
 * 加载库存列表
 *
 * @param reset 是否重置列表（下拉刷新时为true）
 */
async function loadStockList(reset: boolean = false): Promise<void> {
  if (loading.value) return

  if (reset) {
    currentPage.value = 1
    noMore.value = false
  }

  loading.value = true
  try {
    const params: Record<string, unknown> = {
      current: currentPage.value,
      size: pageSize.value
    }
    if (selectedWarehouseId.value) {
      params.warehouseId = selectedWarehouseId.value
    }
    if (keyword.value.trim()) {
      params.keyword = keyword.value.trim()
    }

    const result = await getStockListApi(params as any)
    if (reset) {
      stockList.value = result.records
    } else {
      stockList.value = [...stockList.value, ...result.records]
    }
    total.value = result.total
    noMore.value = stockList.value.length >= result.total
  } catch {
    // 加载失败，保持当前列表
  } finally {
    loading.value = false
  }
}

/**
 * 处理库房筛选变更
 */
function handleWarehouseFilter(e: { detail: { value: number } }): void {
  const index = e.detail.value
  if (index < 0 || index >= warehouseOptions.value.length) {
    selectedWarehouseId.value = null
    selectedWarehouseName.value = ''
  } else {
    const warehouse = warehouseOptions.value[index]
    selectedWarehouseId.value = warehouse.id
    selectedWarehouseName.value = warehouse.warehouseName
  }
  loadStockList(true)
}

/**
 * 处理搜索
 */
function handleSearch(): void {
  loadStockList(true)
}

/**
 * 跳转库存预警页面
 */
function goToAlert(): void {
  uni.navigateTo({
    url: '/pages/stock/alert'
  })
}

/**
 * 页面显示时刷新数据
 */
onShow(() => {
  loadWarehouses()
  loadStockList(true)
})

/**
 * 下拉刷新
 */
onPullDownRefresh(async () => {
  await loadStockList(true)
  uni.stopPullDownRefresh()
})

/**
 * 上拉加载更多
 */
onReachBottom(() => {
  if (!noMore.value && !loading.value) {
    currentPage.value++
    loadStockList()
  }
})
</script>

<style lang="scss" scoped>
.stock-list-page {
  min-height: 100vh;
  background-color: $bg-color-page;

  &__search {
    display: flex;
    align-items: center;
    padding: $spacing-md $spacing-lg;
    background-color: $bg-color-card;
    gap: $spacing-sm;
  }

  &__search-input {
    flex: 1;
    height: 64rpx;
    font-size: $font-size-md;
    color: $text-color-primary;
    border: 2rpx solid $border-color-base;
    border-radius: $border-radius-md;
    padding: 0 $spacing-sm;
    background-color: $bg-color-page;
  }

  &__search-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 $spacing-md;
    height: 64rpx;
    background-color: $color-primary;
    border-radius: $border-radius-md;
    cursor: pointer;
  }

  &__search-btn-text {
    font-size: $font-size-md;
    color: $text-color-inverse;
  }

  &__filter {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: $spacing-sm $spacing-lg;
    background-color: $bg-color-card;
    border-top: 1rpx solid $border-color-light;
  }

  &__filter-picker { flex: 1; }

  &__filter-value {
    height: 56rpx;
    display: flex;
    align-items: center;
    padding: 0 $spacing-sm;
    border: 2rpx solid $border-color-base;
    border-radius: $border-radius-md;
    background-color: $bg-color-page;
    font-size: $font-size-sm;
    color: $text-color-regular;
  }

  &__alert-btn {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 $spacing-md;
    height: 56rpx;
    background-color: $color-warning;
    border-radius: $border-radius-md;
    margin-left: $spacing-sm;
    cursor: pointer;
  }

  &__alert-btn-text {
    font-size: $font-size-sm;
    color: $text-color-inverse;
    font-weight: 500;
  }

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
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__item-qty {
    font-size: $font-size-xl;
    color: $color-primary;
    font-weight: 700;
    flex-shrink: 0;
    margin-left: $spacing-sm;

    &--alert { color: $color-error; }
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

  &__item-locked {
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

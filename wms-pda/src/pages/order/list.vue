<!--
  WMS-PDA 单据列表页面
  单据类型Tab切换 + 分页列表 + 状态筛选 + 关键词搜索 + 下拉刷新 + 上拉加载
-->
<template>
  <view class="order-list-page">
    <!-- 单据类型Tab -->
    <view class="order-list-page__tabs">
      <view
        class="order-list-page__tab"
        v-for="tab in tabs"
        :key="tab.type"
        :class="{ 'order-list-page__tab--active': activeTab === tab.type }"
        @click="handleTabChange(tab.type)"
      >
        <text class="order-list-page__tab-text">{{ tab.label }}</text>
      </view>
    </view>

    <!-- 搜索与筛选 -->
    <view class="order-list-page__filter">
      <input
        class="order-list-page__search-input"
        v-model="keyword"
        placeholder="搜索单号/物品"
        confirm-type="search"
        @confirm="handleSearch"
      />
      <picker
        class="order-list-page__status-picker"
        :range="statusOptions"
        range-key="label"
        @change="handleStatusFilter"
      >
        <view class="order-list-page__status-value">
          <text>{{ selectedStatusLabel || '全部状态' }}</text>
        </view>
      </picker>
    </view>

    <!-- 单据列表 -->
    <view class="order-list-page__list">
      <view v-if="orderList.length > 0" class="order-list-page__items">
        <OrderCard
          v-for="order in orderList"
          :key="order.id"
          :order="order"
          orderType="order"
          @click="goToDetail(order)"
        />
      </view>
      <EmptyState v-else-if="!loading" text="暂无单据数据" />

      <!-- 加载更多提示 -->
      <view class="order-list-page__loading" v-if="loading">
        <text class="order-list-page__loading-text">加载中...</text>
      </view>
      <view class="order-list-page__nomore" v-else-if="noMore && orderList.length > 0">
        <text class="order-list-page__nomore-text">没有更多了</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
/**
 * 单据列表页面
 * 单据类型Tab切换：入库/出库/归还/调拨/报废
 * 根据类型调用对应单据列表接口
 * 支持分页、状态筛选、关键词搜索、下拉刷新、上拉加载
 */
import { ref } from 'vue'
import { onShow, onPullDownRefresh, onReachBottom } from '@dcloudio/uni-app'
import type { OrderPageDto, OrderStatusValue } from '@/utils/constants'
import { OrderStatus } from '@/utils/constants'
import {
  getInboundListApi,
  getOutboundListApi,
  getReturnListApi,
  getTransferListApi,
  getScrapListApi
} from '@/api/order'
import OrderCard from '@/components/OrderCard.vue'
import EmptyState from '@/components/EmptyState.vue'

/** 单据类型Tab定义 */
interface TabItem {
  type: string
  label: string
}

const tabs: TabItem[] = [
  { type: 'inbound', label: '入库' },
  { type: 'outbound', label: '出库' },
  { type: 'return', label: '归还' },
  { type: 'transfer', label: '调拨' },
  { type: 'scrap', label: '报废' }
]

/** 状态筛选选项 */
const statusOptions = [
  { value: -1, label: '全部状态' },
  { value: OrderStatus.DRAFT, label: '草稿' },
  { value: OrderStatus.PENDING, label: '待审核' },
  { value: OrderStatus.APPROVED, label: '已审核' },
  { value: OrderStatus.COMPLETED, label: '已完成' },
  { value: OrderStatus.REJECTED, label: '已驳回' }
]

/** 当前激活的Tab */
const activeTab = ref<string>('inbound')
/** 搜索关键词 */
const keyword = ref<string>('')
/** 选中的状态值 */
const selectedStatus = ref<number>(-1)
/** 选中的状态名称 */
const selectedStatusLabel = ref<string>('')
/** 单据列表（统一为OrderCard可用的格式） */
const orderList = ref<any[]>([])
/** 当前页码 */
const currentPage = ref<number>(1)
/** 每页条数 */
const pageSize = ref<number>(10)
/** 是否正在加载 */
const loading = ref<boolean>(false)
/** 是否没有更多数据 */
const noMore = ref<boolean>(false)

/**
 * 加载单据列表
 * 根据activeTab调用不同接口
 *
 * @param reset 是否重置列表
 */
async function loadOrderList(reset: boolean = false): Promise<void> {
  if (loading.value) return

  if (reset) {
    currentPage.value = 1
    noMore.value = false
  }

  loading.value = true
  try {
    const params: OrderPageDto = {
      current: currentPage.value,
      size: pageSize.value
    }
    if (selectedStatus.value >= 0) {
      params.status = selectedStatus.value
    }
    if (keyword.value.trim()) {
      params.keyword = keyword.value.trim()
    }

    let result: any
    switch (activeTab.value) {
      case 'inbound':
        result = await getInboundListApi(params)
        break
      case 'outbound':
        result = await getOutboundListApi(params)
        break
      case 'return':
        result = await getReturnListApi(params)
        break
      case 'transfer':
        result = await getTransferListApi(params)
        break
      case 'scrap':
        result = await getScrapListApi(params)
        break
      default:
        result = { records: [], total: 0 }
    }

    // 统一转换为OrderCard可用格式
    const records = (result.records || []).map((order: any) => ({
      id: order.id,
      orderNo: order.orderNo,
      warehouseName: order.warehouseName || order.fromWarehouseName || '',
      status: order.status,
      createTime: order.createTime,
      detailCount: order.details ? order.details.length : 0
    }))

    if (reset) {
      orderList.value = records
    } else {
      orderList.value = [...orderList.value, ...records]
    }
    noMore.value = orderList.value.length >= result.total
  } catch {
    // 加载失败，保持当前列表
  } finally {
    loading.value = false
  }
}

/**
 * 处理Tab切换
 */
function handleTabChange(type: string): void {
  if (activeTab.value === type) return
  activeTab.value = type
  keyword.value = ''
  selectedStatus.value = -1
  selectedStatusLabel.value = ''
  loadOrderList(true)
}

/**
 * 处理搜索
 */
function handleSearch(): void {
  loadOrderList(true)
}

/**
 * 处理状态筛选
 */
function handleStatusFilter(e: { detail: { value: number } }): void {
  const index = e.detail.value
  if (index >= 0 && index < statusOptions.length) {
    selectedStatus.value = statusOptions[index].value
    selectedStatusLabel.value = statusOptions[index].label
  }
  loadOrderList(true)
}

/**
 * 跳转单据详情
 */
function goToDetail(order: any): void {
  uni.navigateTo({
    url: `/pages/order/detail?type=${activeTab.value}&id=${order.id}`
  })
}

onShow(() => {
  loadOrderList(true)
})

onPullDownRefresh(async () => {
  await loadOrderList(true)
  uni.stopPullDownRefresh()
})

onReachBottom(() => {
  if (!noMore.value && !loading.value) {
    currentPage.value++
    loadOrderList()
  }
})
</script>

<style lang="scss" scoped>
.order-list-page {
  min-height: 100vh;
  background-color: $bg-color-page;

  &__tabs {
    display: flex;
    background-color: $bg-color-card;
    border-bottom: 1rpx solid $border-color-light;
    overflow-x: auto;
  }

  &__tab {
    flex: 1;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 20rpx 0;
    cursor: pointer;
    white-space: nowrap;
    position: relative;

    &--active {
      &::after {
        content: '';
        position: absolute;
        bottom: 0;
        left: 50%;
        transform: translateX(-50%);
        width: 60%;
        height: 4rpx;
        background-color: $color-primary;
        border-radius: 2rpx;
      }

      .order-list-page__tab-text {
        color: $color-primary;
        font-weight: 600;
      }
    }
  }

  &__tab-text {
    font-size: $font-size-md;
    color: $text-color-regular;
  }

  &__filter {
    display: flex;
    align-items: center;
    padding: $spacing-sm $spacing-lg;
    background-color: $bg-color-card;
    gap: $spacing-sm;
  }

  &__search-input {
    flex: 1;
    height: 56rpx;
    font-size: $font-size-sm;
    color: $text-color-primary;
    border: 2rpx solid $border-color-base;
    border-radius: $border-radius-md;
    padding: 0 $spacing-sm;
    background-color: $bg-color-page;
  }

  &__status-picker { flex-shrink: 0; }

  &__status-value {
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

  &__list { padding: $spacing-md $spacing-lg; }

  &__items {
    display: flex;
    flex-direction: column;
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

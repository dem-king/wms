<!--
  WMS-PDA 首页
  作业入口网格 + 待办任务列表 + 离线状态栏
-->
<template>
  <view class="home-page">
    <!-- 离线状态栏 -->
    <OfflineBar
      :isOnline="appStore.isOnline"
      :queueSize="appStore.offlineQueueSize"
      :syncing="appStore.syncing"
    />

    <!-- 作业入口网格 -->
    <view class="home-page__section">
      <text class="home-page__section-title">作业入口</text>
      <view class="home-page__grid">
        <view
          class="home-page__grid-item"
          v-for="item in jobEntries"
          :key="item.type"
          @click="handleJobClick(item)"
        >
          <text class="home-page__grid-icon">{{ item.icon }}</text>
          <text class="home-page__grid-text">{{ item.label }}</text>
        </view>
      </view>
    </view>

    <!-- 待办任务统计 -->
    <view class="home-page__section">
      <text class="home-page__section-title">待办任务</text>
      <view v-if="hasTasks" class="home-page__tasks">
        <view
          class="home-page__task-item"
          v-for="item in taskItems"
          :key="item.type"
          @click="handleTaskItemClick(item)"
        >
          <view class="home-page__task-content">
            <text class="home-page__task-title">{{ item.label }}</text>
          </view>
          <view class="home-page__task-badge">
            <text class="home-page__task-count">{{ item.count }}</text>
          </view>
        </view>
      </view>
      <EmptyState v-else text="暂无待办任务" />
    </view>
  </view>
</template>

<script setup lang="ts">
/**
 * 首页
 * 作业入口网格 + 待办任务列表 + 离线状态栏
 */
import { ref, computed } from 'vue'
import { onShow } from '@dcloudio/uni-app'
import { useAppStore } from '@/store/app'
import { getPdaTasksApi } from '@/api/pda'
import type { PdaTaskVo } from '@/utils/constants'
import OfflineBar from '@/components/OfflineBar.vue'
import EmptyState from '@/components/EmptyState.vue'

/** appStore实例 */
const appStore = useAppStore()

/** 作业入口定义 */
interface JobEntry {
  /** 作业类型 */
  type: string
  /** 图标 */
  icon: string
  /** 标签文字 */
  label: string
  /** 跳转路径 */
  path: string
}

/** 作业入口列表 */
const jobEntries: JobEntry[] = [
  { type: 'inbound', icon: '📥', label: '入库', path: '/pages/scan/inbound' },
  { type: 'outbound', icon: '📤', label: '出库', path: '/pages/scan/outbound' },
  { type: 'return', icon: '🔄', label: '归还', path: '/pages/scan/return' },
  { type: 'transfer', icon: '🔀', label: '调拨', path: '/pages/scan/transfer' },
  { type: 'scrap', icon: '🗑️', label: '报废', path: '/pages/scan/scrap' },
  { type: 'stockcheck', icon: '📋', label: '盘点', path: '/pages/rfid/check' },
  { type: 'query', icon: '🔍', label: '查询扫码', path: '/pages/scan/query' },
  { type: 'order', icon: '📄', label: '单据管理', path: '/pages/order/list' }
]

/** 待办任务统计 */
const taskStats = ref<PdaTaskVo | null>(null)

/** 是否正在加载 */
const loading = ref<boolean>(false)

/** 待办任务项定义 */
interface TaskItem {
  /** 任务类型 */
  type: string
  /** 显示标签 */
  label: string
  /** 数量 */
  count: number
  /** 跳转路径 */
  path: string
}

/** 将PdaTaskVo统计转换为任务项列表 */
const taskItems = computed<TaskItem[]>(() => {
  if (!taskStats.value) return []
  const s = taskStats.value
  const items: TaskItem[] = []
  if (s.pendingInbound > 0) items.push({ type: 'inbound', label: '待提交入库单', count: s.pendingInbound, path: '/pages/order/list?type=inbound' })
  if (s.pendingOutbound > 0) items.push({ type: 'outbound', label: '待提交出库单', count: s.pendingOutbound, path: '/pages/order/list?type=outbound' })
  if (s.pendingReturn > 0) items.push({ type: 'return', label: '待提交归还单', count: s.pendingReturn, path: '/pages/order/list?type=return' })
  if (s.pendingApproval > 0) items.push({ type: 'approval', label: '待审批单据', count: s.pendingApproval, path: '/pages/order/list' })
  if (s.stockAlert > 0) items.push({ type: 'stockAlert', label: '库存预警', count: s.stockAlert, path: '/pages/stock/alert' })
  if (s.overdueReturn > 0) items.push({ type: 'overdue', label: '超期归还', count: s.overdueReturn, path: '/pages/order/list?type=return' })
  return items
})

/** 是否有待办任务 */
const hasTasks = computed(() => taskItems.value.length > 0)

/**
 * 加载待办任务统计
 * 调用GET /api/pda/tasks获取待办任务数量统计
 */
async function loadTasks(): Promise<void> {
  if (loading.value) return
  loading.value = true
  try {
    taskStats.value = await getPdaTasksApi()
  } catch {
    // 加载失败，保持空状态
    taskStats.value = null
  } finally {
    loading.value = false
  }
}

/**
 * 处理作业入口点击
 * 跳转到对应的扫码页面
 *
 * @param entry 作业入口
 */
function handleJobClick(entry: JobEntry): void {
  uni.navigateTo({
    url: entry.path
  })
}

/**
 * 处理待办任务点击
 * 跳转到对应的单据页面
 *
 * @param item 待办任务项
 */
function handleTaskItemClick(item: TaskItem): void {
  uni.navigateTo({
    url: item.path
  })
}

/**
 * 页面显示时刷新数据
 */
onShow(() => {
  loadTasks()
})
</script>

<style lang="scss" scoped>
.home-page {
  min-height: 100vh;
  background-color: $bg-color-page;

  &__section {
    padding: $spacing-lg;
  }

  &__section-title {
    font-size: $font-size-lg;
    color: $text-color-primary;
    font-weight: 600;
    margin-bottom: $spacing-md;
    display: block;
  }

  &__grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: $spacing-md;
  }

  &__grid-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    background-color: $bg-color-card;
    border-radius: $border-radius-md;
    padding: $spacing-md 0;
    box-shadow: $shadow-sm;
    cursor: pointer;

    &:active {
      background-color: $bg-color-hover;
    }
  }

  &__grid-icon {
    font-size: 56rpx;
    line-height: 1;
    margin-bottom: 8rpx;
  }

  &__grid-text {
    font-size: $font-size-sm;
    color: $text-color-regular;
  }

  &__tasks {
    display: flex;
    flex-direction: column;
    gap: $spacing-sm;
  }

  &__task-item {
    display: flex;
    align-items: center;
    justify-content: space-between;
    background-color: $bg-color-card;
    border-radius: $border-radius-md;
    padding: $spacing-md;
    box-shadow: $shadow-sm;
    cursor: pointer;

    &:active {
      background-color: $bg-color-hover;
    }
  }

  &__task-content {
    display: flex;
    flex-direction: column;
    gap: 4rpx;
    flex: 1;
  }

  &__task-title {
    font-size: $font-size-md;
    color: $text-color-primary;
    font-weight: 500;
  }

  &__task-badge {
    min-width: 48rpx;
    height: 48rpx;
    border-radius: 24rpx;
    background-color: $color-error;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 12rpx;
    flex-shrink: 0;
  }

  &__task-count {
    font-size: $font-size-xs;
    color: $text-color-inverse;
    font-weight: 600;
  }
}
</style>

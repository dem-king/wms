<!--
  WMS-PDA 我的页面（TabBar页）
  展示用户信息、功能入口（设置/关于/登出）、离线状态栏
-->
<template>
  <view class="mine-page">
    <!-- 离线状态栏 -->
    <OfflineBar
      :isOnline="isOnline"
      :queueSize="queueSize"
      :syncing="syncing"
      @sync="handleSync"
    />

    <!-- 用户信息卡片 -->
    <view class="user-card">
      <view class="user-card__avatar">
        <image
          class="user-card__avatar-img"
          :src="avatarUrl"
          mode="aspectFill"
        />
      </view>
      <view class="user-card__info">
        <text class="user-card__name">{{ displayName }}</text>
        <text class="user-card__username">{{ username }}</text>
        <text class="user-card__dept" v-if="deptName">{{ deptName }}</text>
      </view>
    </view>

    <!-- 功能入口列表 -->
    <view class="menu-list">
      <!-- 设置 -->
      <view class="menu-item" @click="navigateToSettings">
        <view class="menu-item__left">
          <text class="menu-item__icon">⚙</text>
          <text class="menu-item__label">设置</text>
        </view>
        <text class="menu-item__arrow">›</text>
      </view>

      <!-- 关于 -->
      <view class="menu-item" @click="navigateToAbout">
        <view class="menu-item__left">
          <text class="menu-item__icon">ℹ</text>
          <text class="menu-item__label">关于</text>
        </view>
        <text class="menu-item__arrow">›</text>
      </view>

      <!-- 离线队列（仅离线时显示） -->
      <view class="menu-item" v-if="!isOnline || queueSize > 0" @click="navigateToOfflineQueue">
        <view class="menu-item__left">
          <text class="menu-item__icon">☁</text>
          <text class="menu-item__label">离线队列</text>
          <view class="menu-item__badge" v-if="queueSize > 0">
            <text class="menu-item__badge-text">{{ queueSize }}</text>
          </view>
        </view>
        <text class="menu-item__arrow">›</text>
      </view>
    </view>

    <!-- 登出按钮 -->
    <view class="logout-section">
      <button class="logout-btn" @click="handleLogout">退出登录</button>
    </view>
  </view>
</template>

<script setup lang="ts">
/**
 * 我的页面
 * 展示用户信息、功能入口、登出操作
 */
import { computed } from 'vue'
import { useAuthStore } from '@/store/auth'
import { useAppStore } from '@/store/app'
import { useOfflineStore } from '@/store/offline'
import { useAuth } from '@/composables/useAuth'
import OfflineBar from '@/components/OfflineBar.vue'

const authStore = useAuthStore()
const appStore = useAppStore()
const offlineStore = useOfflineStore()
const { logout } = useAuth()

/** 是否在线 */
const isOnline = computed(() => appStore.isOnline)

/** 队列大小 */
const queueSize = computed(() => offlineStore.queueSize)

/** 是否正在同步 */
const syncing = computed(() => offlineStore.syncing)

/** 用户头像URL */
const avatarUrl = computed(() => {
  const avatar = authStore.userInfo?.avatar
  if (avatar) {
    // 如果是相对路径，拼接服务器地址
    if (avatar.startsWith('/')) {
      const serverUrl = authStore.serverUrl
      return `${serverUrl}${avatar}`
    }
    return avatar
  }
  // 默认头像：使用内置用户图标（无需外部图片文件）
  return ''
})

/** 显示名称（优先使用真实姓名） */
const displayName = computed(() => {
  return authStore.userInfo?.realName || authStore.userInfo?.username || '未登录'
})

/** 用户名 */
const username = computed(() => {
  return authStore.userInfo?.username ? `@${authStore.userInfo.username}` : ''
})

/** 部门名称（暂用deptId占位，后续可扩展部门名称接口） */
const deptName = computed(() => {
  const deptId = authStore.userInfo?.deptId
  return deptId ? `部门ID: ${deptId}` : ''
})

/**
 * 手动同步离线队列
 */
async function handleSync(): Promise<void> {
  if (!appStore.isOnline) {
    uni.showToast({
      title: '当前离线，无法同步',
      icon: 'none',
      duration: 2000
    })
    return
  }
  await offlineStore.syncQueue()
}

/**
 * 跳转设置页面
 */
function navigateToSettings(): void {
  uni.navigateTo({
    url: '/pages/mine/settings'
  })
}

/**
 * 跳转关于页面
 * 暂用弹窗展示版本信息
 */
function navigateToAbout(): void {
  uni.showModal({
    title: '关于',
    content: 'WMS-PDA 库房管理移动端\n版本：1.0.0',
    showCancel: false,
    confirmText: '确定'
  })
}

/**
 * 查看离线队列详情
 * 跳转设置页面的离线队列管理区域
 */
function navigateToOfflineQueue(): void {
  uni.navigateTo({
    url: '/pages/mine/settings'
  })
}

/**
 * 登出操作
 * 确认弹窗后调用useAuth.logout()
 */
async function handleLogout(): Promise<void> {
  uni.showModal({
    title: '提示',
    content: '确定要退出登录吗？',
    confirmText: '确定',
    cancelText: '取消',
    success: async (res) => {
      if (res.confirm) {
        try {
          await authStore.logout()
          // 跳转登录页
          uni.reLaunch({
            url: '/pages/login/index'
          })
        } catch (error) {
          console.error('登出失败:', error)
          // 即使登出接口失败，也清除本地数据并跳转
          authStore.clearAuth()
          uni.reLaunch({
            url: '/pages/login/index'
          })
        }
      }
    }
  })
}
</script>

<style lang="scss" scoped>
.mine-page {
  min-height: 100vh;
  background-color: $bg-color-page;
}

// 用户信息卡片
.user-card {
  display: flex;
  align-items: center;
  padding: $spacing-xl $spacing-lg;
  background-color: $color-primary;
  margin-bottom: $spacing-md;

  &__avatar {
    width: 120rpx;
    height: 120rpx;
    border-radius: 50%;
    overflow: hidden;
    border: 4rpx solid rgba(255, 255, 255, 0.3);
    flex-shrink: 0;
  }

  &__avatar-img {
    width: 100%;
    height: 100%;
  }

  &__info {
    flex: 1;
    margin-left: $spacing-lg;
    display: flex;
    flex-direction: column;
    gap: 6rpx;
  }

  &__name {
    font-size: $font-size-xl;
    color: $text-color-inverse;
    font-weight: 600;
  }

  &__username {
    font-size: $font-size-sm;
    color: rgba(255, 255, 255, 0.8);
  }

  &__dept {
    font-size: $font-size-sm;
    color: rgba(255, 255, 255, 0.7);
  }
}

// 功能菜单列表
.menu-list {
  background-color: $bg-color-card;
  border-radius: $border-radius-md;
  margin: 0 $spacing-md $spacing-md;
  overflow: hidden;
}

.menu-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-lg $spacing-md;
  border-bottom: 1rpx solid $border-color-light;

  &:last-child {
    border-bottom: none;
  }

  &:active {
    background-color: $bg-color-hover;
  }

  &__left {
    display: flex;
    align-items: center;
    gap: $spacing-sm;
  }

  &__icon {
    font-size: $font-size-lg;
    width: 48rpx;
    text-align: center;
  }

  &__label {
    font-size: $font-size-md;
    color: $text-color-primary;
  }

  &__arrow {
    font-size: $font-size-xl;
    color: $text-color-placeholder;
    font-weight: 300;
  }

  &__badge {
    min-width: 36rpx;
    height: 36rpx;
    border-radius: 18rpx;
    background-color: $color-error;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 0 8rpx;
    margin-left: 8rpx;
  }

  &__badge-text {
    font-size: $font-size-xs;
    color: $text-color-inverse;
    font-weight: 500;
  }
}

// 登出按钮
.logout-section {
  padding: $spacing-xl $spacing-md;
}

.logout-btn {
  width: 100%;
  height: 88rpx;
  line-height: 88rpx;
  text-align: center;
  font-size: $font-size-md;
  color: $color-error;
  background-color: $bg-color-card;
  border: 1rpx solid $color-error;
  border-radius: $border-radius-md;

  &:active {
    opacity: 0.8;
  }
}
</style>

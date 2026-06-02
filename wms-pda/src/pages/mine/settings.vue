<!--
  WMS-PDA 设置页面
  服务器地址配置、振动/声音开关、离线队列管理、RFID功率设置
-->
<template>
  <view class="settings-page">
    <!-- 服务器地址配置 -->
    <view class="settings-section">
      <text class="section-title">服务器配置</text>
      <view class="settings-card">
        <view class="form-item">
          <text class="form-item__label">服务器地址</text>
          <view class="form-item__input-wrap">
            <input
              class="form-item__input"
              v-model="serverUrlInput"
              placeholder="如 http://192.168.1.100:8080"
              :maxlength="100"
            />
          </view>
        </view>
        <view class="form-item__actions">
          <button class="btn btn--primary btn--small" @click="handleSaveServerUrl">保存</button>
        </view>
        <text class="form-item__hint">修改服务器地址后需要重新登录</text>
      </view>
    </view>

    <!-- 操作反馈设置 -->
    <view class="settings-section">
      <text class="section-title">操作反馈</text>
      <view class="settings-card">
        <!-- 振动反馈开关 -->
        <view class="switch-item">
          <view class="switch-item__left">
            <text class="switch-item__label">振动反馈</text>
            <text class="switch-item__desc">扫码成功/失败时振动提示</text>
          </view>
          <switch
            :checked="vibrateEnabled"
            @change="handleVibrateChange"
            color="#1890ff"
          />
        </view>

        <!-- 声音反馈开关 -->
        <view class="switch-item">
          <view class="switch-item__left">
            <text class="switch-item__label">声音反馈</text>
            <text class="switch-item__desc">扫码成功/失败时声音提示</text>
          </view>
          <switch
            :checked="soundEnabled"
            @change="handleSoundChange"
            color="#1890ff"
          />
        </view>
      </view>
    </view>

    <!-- RFID设置 -->
    <view class="settings-section">
      <text class="section-title">RFID设置</text>
      <view class="settings-card">
        <view class="slider-item">
          <view class="slider-item__header">
            <text class="slider-item__label">读取功率</text>
            <text class="slider-item__value">{{ rfidPower }}</text>
          </view>
          <slider
            :value="rfidPower"
            :min="5"
            :max="30"
            :step="1"
            activeColor="#1890ff"
            backgroundColor="#e8e8e8"
            block-size="20"
            @change="handleRfidPowerChange"
          />
          <view class="slider-item__range">
            <text class="slider-item__range-text">5</text>
            <text class="slider-item__range-text">30</text>
          </view>
        </view>
      </view>
    </view>

    <!-- 离线队列管理 -->
    <view class="settings-section">
      <text class="section-title">离线队列</text>
      <view class="settings-card">
        <!-- 队列状态 -->
        <view class="queue-item">
          <view class="queue-item__left">
            <text class="queue-item__label">待同步记录</text>
            <text class="queue-item__count">{{ queueSize }}条</text>
          </view>
          <view class="queue-item__status">
            <text class="queue-item__status-text" v-if="syncing">同步中...</text>
            <text class="queue-item__status-text queue-item__status-text--online" v-else-if="isOnline">在线</text>
            <text class="queue-item__status-text queue-item__status-text--offline" v-else>离线</text>
          </view>
        </view>

        <!-- 手动同步按钮 -->
        <view class="queue-actions">
          <button
            class="btn btn--primary btn--small"
            :disabled="syncing || !isOnline || queueSize === 0"
            @click="handleSync"
          >
            {{ syncing ? '同步中...' : '手动同步' }}
          </button>
          <button
            class="btn btn--default btn--small"
            @click="handleViewFailedRecords"
          >
            查看失败记录
          </button>
        </view>
      </view>
    </view>

    <!-- 失败记录弹窗 -->
    <view class="failed-modal" v-if="showFailedModal" @click.self="showFailedModal = false">
      <view class="failed-modal__content">
        <view class="failed-modal__header">
          <text class="failed-modal__title">失败记录</text>
          <text class="failed-modal__close" @click="showFailedModal = false">✕</text>
        </view>
        <scroll-view class="failed-modal__body" scroll-y>
          <view class="failed-record" v-for="record in failedRecords" :key="record.id">
            <view class="failed-record__info">
              <text class="failed-record__method">{{ record.method }}</text>
              <text class="failed-record__url">{{ record.url }}</text>
            </view>
            <text class="failed-record__error">{{ record.errorMsg }}</text>
            <text class="failed-record__time">{{ formatTimestamp(record.timestamp) }}</text>
          </view>
          <view class="failed-modal__empty" v-if="failedRecords.length === 0">
            <text class="failed-modal__empty-text">暂无失败记录</text>
          </view>
        </scroll-view>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
/**
 * 设置页面
 * 服务器地址配置、振动/声音开关、离线队列管理、RFID功率设置
 */
import { ref, computed, onMounted } from 'vue'
import { useAppStore } from '@/store/app'
import { useAuthStore } from '@/store/auth'
import { useOfflineStore } from '@/store/offline'
import { isValidServerUrl } from '@/utils/validate'
import { RFID_POWER_MIN, RFID_POWER_MAX } from '@/utils/constants'
import type { OfflineQueueItem } from '@/utils/constants'

const appStore = useAppStore()
const authStore = useAuthStore()
const offlineStore = useOfflineStore()

// ==================== 服务器地址配置 ====================

/** 服务器地址输入值 */
const serverUrlInput = ref<string>('')

/** 是否在线 */
const isOnline = computed(() => appStore.isOnline)

/** 队列大小 */
const queueSize = computed(() => offlineStore.queueSize)

/** 是否正在同步 */
const syncing = computed(() => offlineStore.syncing)

/** 振动反馈开关 */
const vibrateEnabled = computed(() => appStore.vibrateEnabled)

/** 声音反馈开关 */
const soundEnabled = computed(() => appStore.soundEnabled)

/** RFID读取功率 */
const rfidPower = computed(() => appStore.rfidPower)

/** 失败记录列表 */
const failedRecords = ref<OfflineQueueItem[]>([])

/** 是否显示失败记录弹窗 */
const showFailedModal = ref<boolean>(false)

/**
 * 保存服务器地址
 * 校验URL格式后保存，提示需要重新登录
 */
function handleSaveServerUrl(): void {
  const url = serverUrlInput.value.trim()

  if (!url) {
    uni.showToast({
      title: '请输入服务器地址',
      icon: 'none',
      duration: 2000
    })
    return
  }

  // URL格式校验
  if (!isValidServerUrl(url)) {
    uni.showToast({
      title: '服务器地址格式错误',
      icon: 'none',
      duration: 2000
    })
    return
  }

  // 保存服务器地址
  authStore.setServerUrl(url)

  uni.showModal({
    title: '提示',
    content: '服务器地址已修改，需要重新登录才能生效，是否立即重新登录？',
    confirmText: '重新登录',
    cancelText: '稍后',
    success: async (res) => {
      if (res.confirm) {
        // 清除认证信息并跳转登录页
        authStore.clearAuth()
        uni.reLaunch({
          url: '/pages/login/index'
        })
      }
    }
  })
}

// ==================== 振动/声音开关 ====================

/**
 * 振动反馈开关变化
 */
function handleVibrateChange(e: { detail: { value: boolean } }): void {
  appStore.setVibrateEnabled(e.detail.value)
}

/**
 * 声音反馈开关变化
 */
function handleSoundChange(e: { detail: { value: boolean } }): void {
  appStore.setSoundEnabled(e.detail.value)
}

// ==================== RFID功率设置 ====================

/**
 * RFID功率滑块变化
 */
function handleRfidPowerChange(e: { detail: { value: number } }): void {
  const power = Math.round(e.detail.value)
  // 确保功率在合法范围内
  const clampedPower = Math.max(RFID_POWER_MIN, Math.min(RFID_POWER_MAX, power))
  appStore.setRfidPower(clampedPower)
}

// ==================== 离线队列管理 ====================

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

  if (offlineStore.queueSize === 0) {
    uni.showToast({
      title: '没有待同步的记录',
      icon: 'none',
      duration: 1500
    })
    return
  }

  const result = await offlineStore.syncQueue()

  if (result.successCount > 0 || result.failCount > 0) {
    uni.showToast({
      title: `已同步${result.successCount}条成功，${result.failCount}条失败`,
      icon: result.failCount > 0 ? 'none' : 'success',
      duration: 3000
    })
  }
}

/**
 * 查看失败记录
 */
async function handleViewFailedRecords(): Promise<void> {
  try {
    failedRecords.value = await offlineStore.getFailedRecordsAction()
    showFailedModal.value = true
  } catch (error) {
    console.error('获取失败记录失败:', error)
    uni.showToast({
      title: '获取失败记录失败',
      icon: 'none',
      duration: 2000
    })
  }
}

/**
 * 格式化时间戳为可读时间
 *
 * @param timestamp 毫秒时间戳
 * @returns 格式化后的时间字符串
 */
function formatTimestamp(timestamp: number): string {
  const date = new Date(timestamp)
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const hours = String(date.getHours()).padStart(2, '0')
  const minutes = String(date.getMinutes()).padStart(2, '0')
  const seconds = String(date.getSeconds()).padStart(2, '0')
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// ==================== 生命周期 ====================

onMounted(() => {
  // 初始化服务器地址输入值
  serverUrlInput.value = authStore.serverUrl || ''
})
</script>

<style lang="scss" scoped>
.settings-page {
  min-height: 100vh;
  background-color: $bg-color-page;
  padding-bottom: $spacing-xl;
}

// 设置分区
.settings-section {
  margin-bottom: $spacing-md;
}

.section-title {
  display: block;
  font-size: $font-size-sm;
  color: $text-color-secondary;
  padding: $spacing-sm $spacing-lg $spacing-xs;
}

.settings-card {
  background-color: $bg-color-card;
  padding: 0 $spacing-md;
}

// 表单项
.form-item {
  padding: $spacing-md 0;
  border-bottom: 1rpx solid $border-color-light;

  &__label {
    font-size: $font-size-md;
    color: $text-color-primary;
    margin-bottom: $spacing-xs;
    display: block;
  }

  &__input-wrap {
    display: flex;
    align-items: center;
  }

  &__input {
    flex: 1;
    height: 72rpx;
    font-size: $font-size-md;
    color: $text-color-primary;
    background-color: $bg-color-grey;
    border-radius: $border-radius-md;
    padding: 0 $spacing-sm;
  }

  &__actions {
    padding: $spacing-sm 0;
    display: flex;
    justify-content: flex-end;
  }

  &__hint {
    font-size: $font-size-xs;
    color: $text-color-secondary;
    padding-bottom: $spacing-sm;
    display: block;
  }
}

// 开关项
.switch-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-md 0;
  border-bottom: 1rpx solid $border-color-light;

  &:last-child {
    border-bottom: none;
  }

  &__left {
    flex: 1;
    margin-right: $spacing-md;
  }

  &__label {
    font-size: $font-size-md;
    color: $text-color-primary;
    display: block;
  }

  &__desc {
    font-size: $font-size-xs;
    color: $text-color-secondary;
    margin-top: 4rpx;
    display: block;
  }
}

// 滑块项
.slider-item {
  padding: $spacing-md 0;

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    margin-bottom: $spacing-sm;
  }

  &__label {
    font-size: $font-size-md;
    color: $text-color-primary;
  }

  &__value {
    font-size: $font-size-md;
    color: $color-primary;
    font-weight: 600;
  }

  &__range {
    display: flex;
    justify-content: space-between;
    margin-top: 4rpx;
  }

  &__range-text {
    font-size: $font-size-xs;
    color: $text-color-placeholder;
  }
}

// 离线队列项
.queue-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: $spacing-md 0;
  border-bottom: 1rpx solid $border-color-light;

  &__left {
    display: flex;
    align-items: center;
    gap: $spacing-sm;
  }

  &__label {
    font-size: $font-size-md;
    color: $text-color-primary;
  }

  &__count {
    font-size: $font-size-md;
    color: $color-primary;
    font-weight: 600;
  }

  &__status-text {
    font-size: $font-size-sm;
    color: $text-color-secondary;

    &--online {
      color: $color-success;
    }

    &--offline {
      color: $color-warning;
    }
  }
}

.queue-actions {
  display: flex;
  gap: $spacing-sm;
  padding: $spacing-sm 0 $spacing-md;
}

// 按钮样式
.btn {
  height: 64rpx;
  line-height: 64rpx;
  font-size: $font-size-sm;
  border-radius: $border-radius-md;
  padding: 0 $spacing-lg;
  text-align: center;

  &--small {
    height: 56rpx;
    line-height: 56rpx;
    font-size: $font-size-sm;
  }

  &--primary {
    background-color: $color-primary;
    color: $text-color-inverse;
    border: none;

    &:active {
      opacity: 0.8;
    }

    &[disabled] {
      opacity: 0.5;
    }
  }

  &--default {
    background-color: $bg-color-card;
    color: $text-color-regular;
    border: 1rpx solid $border-color-dark;

    &:active {
      opacity: 0.8;
    }
  }
}

// 失败记录弹窗
.failed-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 999;

  &__content {
    width: 80%;
    max-height: 70vh;
    background-color: $bg-color-card;
    border-radius: $border-radius-lg;
    overflow: hidden;
    display: flex;
    flex-direction: column;
  }

  &__header {
    display: flex;
    align-items: center;
    justify-content: space-between;
    padding: $spacing-md $spacing-lg;
    border-bottom: 1rpx solid $border-color-light;
  }

  &__title {
    font-size: $font-size-lg;
    color: $text-color-primary;
    font-weight: 600;
  }

  &__close {
    font-size: $font-size-xl;
    color: $text-color-secondary;
    padding: 8rpx;
  }

  &__body {
    flex: 1;
    padding: $spacing-md;
    max-height: 60vh;
  }

  &__empty {
    display: flex;
    align-items: center;
    justify-content: center;
    padding: $spacing-xxl 0;
  }

  &__empty-text {
    font-size: $font-size-md;
    color: $text-color-secondary;
  }
}

.failed-record {
  padding: $spacing-sm 0;
  border-bottom: 1rpx solid $border-color-light;

  &:last-child {
    border-bottom: none;
  }

  &__info {
    display: flex;
    align-items: center;
    gap: $spacing-xs;
    margin-bottom: 4rpx;
  }

  &__method {
    font-size: $font-size-xs;
    color: $color-primary;
    font-weight: 600;
    background-color: rgba(24, 144, 255, 0.1);
    padding: 2rpx 8rpx;
    border-radius: $border-radius-sm;
  }

  &__url {
    font-size: $font-size-xs;
    color: $text-color-regular;
    flex: 1;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  &__error {
    font-size: $font-size-xs;
    color: $color-error;
    display: block;
    margin-bottom: 4rpx;
  }

  &__time {
    font-size: $font-size-xs;
    color: $text-color-placeholder;
    display: block;
  }
}
</style>

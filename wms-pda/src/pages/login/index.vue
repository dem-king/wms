<!--
  WMS-PDA 登录页面
  服务器地址配置 + 用户名密码登录 + URL格式校验 + 网络异常提示
  navigationStyle:custom 自定义导航栏
-->
<template>
  <view class="login-page">
    <!-- Logo与标题 -->
    <view class="login-page__header">
      <text class="login-page__logo">📦</text>
      <text class="login-page__title">WMS-PDA</text>
      <text class="login-page__subtitle">备品备件库房管理</text>
    </view>

    <!-- 登录表单 -->
    <view class="login-page__form">
      <!-- 服务器地址 -->
      <view class="login-page__field">
        <text class="login-page__label">服务器地址</text>
        <input
          class="login-page__input"
          v-model="serverUrlInput"
          placeholder="http://192.168.1.100:8080"
          :disabled="loading"
        />
      </view>

      <!-- 用户名 -->
      <view class="login-page__field">
        <text class="login-page__label">用户名</text>
        <input
          class="login-page__input"
          v-model="username"
          placeholder="请输入用户名"
          :disabled="loading"
        />
      </view>

      <!-- 密码 -->
      <view class="login-page__field">
        <text class="login-page__label">密码</text>
        <input
          class="login-page__input"
          v-model="password"
          placeholder="请输入密码"
          password
          :disabled="loading"
        />
      </view>

      <!-- 登录按钮 -->
      <view
        class="login-page__btn"
        :class="{ 'login-page__btn--disabled': loading }"
        @click="handleLogin"
      >
        <text class="login-page__btn-text">{{ loading ? '登录中...' : '登 录' }}</text>
      </view>
    </view>

    <!-- 底部版本信息 -->
    <view class="login-page__footer">
      <text class="login-page__version">v1.0.0</text>
    </view>
  </view>
</template>

<script setup lang="ts">
/**
 * 登录页面
 * 服务器地址配置 + 用户名密码登录
 * URL格式校验、网络异常提示、登录成功跳转首页
 */
import { ref } from 'vue'
import { useAuthStore } from '@/store/auth'
import { useAppStore } from '@/store/app'
import { isValidServerUrl } from '@/utils/validate'

/** authStore实例 */
const authStore = useAuthStore()
/** appStore实例 */
const appStore = useAppStore()

/** 服务器地址输入值 */
const serverUrlInput = ref<string>(authStore.serverUrl || '')
/** 用户名 */
const username = ref<string>('')
/** 密码 */
const password = ref<string>('')
/** 登录中状态 */
const loading = ref<boolean>(false)

/**
 * 处理登录操作
 * 校验服务器地址格式 → 检查网络 → 调用登录接口 → 跳转首页
 */
async function handleLogin(): Promise<void> {
  // 防止重复提交
  if (loading.value) {
    return
  }

  // 校验服务器地址格式
  const trimmedUrl = serverUrlInput.value.trim()
  if (!trimmedUrl) {
    uni.showToast({ title: '请输入服务器地址', icon: 'none' })
    return
  }
  if (!isValidServerUrl(trimmedUrl)) {
    uni.showToast({ title: '服务器地址格式错误', icon: 'none', duration: 2000 })
    return
  }

  // 校验用户名密码非空
  if (!username.value.trim()) {
    uni.showToast({ title: '请输入用户名', icon: 'none' })
    return
  }
  if (!password.value.trim()) {
    uni.showToast({ title: '请输入密码', icon: 'none' })
    return
  }

  // 检查网络连接
  if (!appStore.isOnline) {
    uni.showToast({ title: '网络不可用，请检查网络连接', icon: 'none', duration: 2000 })
    return
  }

  // 保存服务器地址
  authStore.setServerUrl(trimmedUrl)

  loading.value = true
  try {
    // 调用登录接口
    await authStore.login({
      username: username.value.trim(),
      password: password.value
    })

    // 登录成功，跳转首页
    uni.reLaunch({
      url: '/pages/home/index'
    })
  } catch (error: unknown) {
    // 登录失败，显示错误信息
    const errMsg = error instanceof Error ? error.message : '登录失败'
    // 区分网络不可达和其他错误
    if (errMsg.includes('network') || errMsg.includes('connect') || errMsg.includes('网络')) {
      uni.showToast({ title: '无法连接服务器，请检查服务器地址', icon: 'none', duration: 2000 })
    } else {
      uni.showToast({ title: errMsg || '登录失败', icon: 'none', duration: 2000 })
    }
  } finally {
    loading.value = false
  }
}
</script>

<style lang="scss" scoped>
.login-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 100vh;
  padding: 0 $spacing-xl;
  background: linear-gradient(135deg, $color-primary-dark 0%, $color-primary 100%);

  &__header {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: $spacing-xxl;
  }

  &__logo {
    font-size: 120rpx;
    line-height: 1;
    margin-bottom: $spacing-md;
  }

  &__title {
    font-size: $font-size-xxl;
    color: $text-color-inverse;
    font-weight: 700;
    letter-spacing: 4rpx;
    margin-bottom: 8rpx;
  }

  &__subtitle {
    font-size: $font-size-md;
    color: rgba(255, 255, 255, 0.8);
  }

  &__form {
    width: 100%;
    background-color: $bg-color-card;
    border-radius: $border-radius-lg;
    padding: $spacing-xl $spacing-lg;
    box-shadow: $shadow-lg;
  }

  &__field {
    margin-bottom: $spacing-lg;
  }

  &__label {
    display: block;
    font-size: $font-size-md;
    color: $text-color-regular;
    margin-bottom: 8rpx;
    font-weight: 500;
  }

  &__input {
    width: 100%;
    height: 80rpx;
    font-size: $font-size-lg;
    color: $text-color-primary;
    border: 2rpx solid $border-color-base;
    border-radius: $border-radius-md;
    padding: 0 $spacing-sm;
    background-color: $bg-color-page;

    &:focus {
      border-color: $color-primary;
    }
  }

  &__btn {
    width: 100%;
    height: 88rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: $color-primary;
    border-radius: $border-radius-md;
    margin-top: $spacing-md;
    cursor: pointer;

    &--disabled {
      opacity: 0.6;
    }
  }

  &__btn-text {
    font-size: $font-size-lg;
    color: $text-color-inverse;
    font-weight: 600;
    letter-spacing: 8rpx;
  }

  &__footer {
    position: fixed;
    bottom: $spacing-lg;
    left: 0;
    right: 0;
    display: flex;
    justify-content: center;
  }

  &__version {
    font-size: $font-size-sm;
    color: rgba(255, 255, 255, 0.5);
  }
}
</style>

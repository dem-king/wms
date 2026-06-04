<template>
  <view class="login-page">
    <view class="login-page__header">
      <text class="login-page__logo">WMS</text>
      <text class="login-page__title">WMS-PDA</text>
      <text class="login-page__subtitle">备品备件库房管理</text>
    </view>

    <view class="login-page__form">
      <view class="login-page__field">
        <text class="login-page__label">服务器地址</text>
        <input
          v-model="serverUrlInput"
          class="login-page__input"
          placeholder="http://192.168.1.100:8080"
          :disabled="loading || captchaSubmitting"
        />
      </view>

      <view class="login-page__field">
        <text class="login-page__label">用户名</text>
        <input
          v-model="username"
          class="login-page__input"
          placeholder="请输入用户名"
          :disabled="loading || captchaSubmitting"
        />
      </view>

      <view class="login-page__field">
        <text class="login-page__label">密码</text>
        <input
          v-model="password"
          class="login-page__input"
          placeholder="请输入密码"
          type="password"
          :disabled="loading || captchaSubmitting"
        />
      </view>

      <view
        class="login-page__btn"
        :class="{ 'login-page__btn--disabled': loading || captchaSubmitting }"
        @click="handleLogin"
      >
        <text class="login-page__btn-text">{{ loading ? '验证准备中...' : '登录' }}</text>
      </view>
    </view>

    <view class="login-page__footer">
      <text class="login-page__version">v1.0.0</text>
    </view>

    <Verify
      ref="verifyRef"
      :captcha-type="captchaType"
      mode="pop"
      @success="verifySuccess"
      @error="verifyError"
    />
  </view>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { getRsaPublicKey } from '@/api/auth'
import { useAuthStore } from '@/store/auth'
import { useAppStore } from '@/store/app'
import { rsaEncrypt } from '@/utils/crypto'
import { isValidServerUrl } from '@/utils/validate'
import Verify from '@/components/verifition/index.vue'

const authStore = useAuthStore()
const appStore = useAppStore()

const serverUrlInput = ref<string>(authStore.serverUrl || '')
const username = ref<string>('')
const password = ref<string>('')
const loading = ref<boolean>(false)
const captchaSubmitting = ref<boolean>(false)
const verifyRef = ref<{ show: () => void; refresh: () => void }>()
const captchaType = ref<string>('blockPuzzle')
const rsaPublicKey = ref<string>('')

function showToast(title: string, duration = 2000): void {
  uni.showToast({ title, icon: 'none', duration })
}

async function prepareRsaKey(): Promise<void> {
  const keyPair = await getRsaPublicKey()
  rsaPublicKey.value = keyPair.publicKey
}

/**
 * 登录按钮点击后先获取 RSA 公钥，再弹出行为验证码。
 */
async function handleLogin(): Promise<void> {
  if (loading.value || captchaSubmitting.value) {
    return
  }

  const trimmedUrl = serverUrlInput.value.trim()
  if (!trimmedUrl) {
    showToast('请输入服务器地址')
    return
  }
  if (!isValidServerUrl(trimmedUrl)) {
    showToast('服务器地址格式错误')
    return
  }
  if (!username.value.trim()) {
    showToast('请输入用户名')
    return
  }
  if (!password.value.trim()) {
    showToast('请输入密码')
    return
  }
  if (!appStore.isOnline) {
    showToast('网络不可用，请检查网络连接')
    return
  }

  loading.value = true
  try {
    authStore.setServerUrl(trimmedUrl)
    await prepareRsaKey()
    verifyRef.value?.show()
  } catch (error: unknown) {
    const errMsg = error instanceof Error ? error.message : ''
    handleLoginError(errMsg || '安全认证服务不可用，请稍后重试')
  } finally {
    loading.value = false
  }
}

/**
 * 验证码校验成功后，使用 RSA 加密密码并提交登录。
 */
async function verifySuccess(params: { captchaVerification: string }): Promise<void> {
  if (captchaSubmitting.value) {
    return
  }

  captchaSubmitting.value = true
  try {
    if (!rsaPublicKey.value) {
      await prepareRsaKey()
    }
    const encryptedPassword = rsaEncrypt(password.value, rsaPublicKey.value)
    await authStore.login({
      username: username.value.trim(),
      encryptedPassword,
      code: params.captchaVerification,
      randomStr: captchaType.value,
    })
    uni.reLaunch({ url: '/pages/home/index' })
  } catch (error: unknown) {
    const errMsg = error instanceof Error ? error.message : '登录失败'
    handleLoginError(errMsg)
  } finally {
    captchaSubmitting.value = false
  }
}

function verifyError(): void {
  // verifition 组件内部会刷新验证码，这里只保留事件入口。
}

function handleLoginError(errMsg: string): void {
  if (isCaptchaError(errMsg)) {
    showToast(captchaErrorMessage(errMsg))
    verifyRef.value?.refresh()
    return
  }

  if (errMsg.includes('CREDENTIAL_INVALID') || errMsg.includes('用户名或密码错误')) {
    password.value = ''
    showToast('用户名或密码错误')
  } else if (errMsg.includes('LOCKED') || errMsg.includes('锁定')) {
    showToast('账号已被锁定，请稍后重试')
  } else if (errMsg.includes('DISABLED') || errMsg.includes('禁用')) {
    showToast('账号已被禁用')
  } else if (errMsg.includes('DECRYPT') || errMsg.includes('解密')) {
    rsaPublicKey.value = ''
    showToast('认证信息异常，请重新登录')
  } else if (errMsg.includes('network') || errMsg.includes('connect') || errMsg.includes('网络')) {
    showToast('无法连接服务器，请检查服务器地址')
  } else {
    showToast(errMsg || '登录失败')
  }
}

function isCaptchaError(errMsg: string): boolean {
  return errMsg.includes('CAPTCHA_MISMATCH')
    || errMsg.includes('CAPTCHA_INVALID')
    || errMsg.includes('CAPTCHA_EXPIRED')
    || errMsg.includes('CAPTCHA_REQUIRED')
    || errMsg.includes('验证未通过')
    || errMsg.includes('验证码无效')
    || errMsg.includes('请完成滑块验证')
}

function captchaErrorMessage(errMsg: string): string {
  if (errMsg.includes('CAPTCHA_MISMATCH') || errMsg.includes('验证未通过')) {
    return '验证未通过，请重试'
  }
  if (errMsg.includes('CAPTCHA_REQUIRED') || errMsg.includes('请完成滑块验证')) {
    return '请完成滑块验证'
  }
  return '验证码已过期，请重试'
}
</script>

<style lang="scss" scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 0 $spacing-xl;
  background: linear-gradient(135deg, $color-primary-dark 0%, $color-primary 100%);

  &__header {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: $spacing-xxl;
  }

  &__logo {
    width: 120rpx;
    height: 120rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: $spacing-md;
    border-radius: 28rpx;
    color: $color-primary;
    background: $bg-color-card;
    font-size: 36rpx;
    font-weight: 800;
    letter-spacing: 2rpx;
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
    color: rgba(255, 255, 255, 0.82);
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
    height: 88rpx;
    font-size: $font-size-lg;
    color: $text-color-primary;
    border: 2rpx solid $border-color-base;
    border-radius: $border-radius-md;
    padding: 0 $spacing-sm;
    background-color: $bg-color-page;
    box-sizing: border-box;
  }

  &__btn {
    width: 100%;
    height: 92rpx;
    display: flex;
    align-items: center;
    justify-content: center;
    background-color: $color-primary;
    border-radius: $border-radius-md;
    margin-top: $spacing-md;
  }

  &__btn--disabled {
    opacity: 0.6;
  }

  &__btn-text {
    font-size: $font-size-lg;
    color: $text-color-inverse;
    font-weight: 600;
    letter-spacing: 6rpx;
  }

  &__footer {
    position: fixed;
    bottom: calc(#{$spacing-lg} + env(safe-area-inset-bottom));
    left: 0;
    right: 0;
    display: flex;
    justify-content: center;
  }

  &__version {
    font-size: $font-size-sm;
    color: rgba(255, 255, 255, 0.55);
  }
}
</style>

<template>
  <div class="login-page">
    <div class="brand-section">
      <div class="brand-bg">
        <img
          src="https://trae-api-cn.mchost.guru/api/ide/v1/text_to_image?prompt=Modern%20warehouse%20interior%20with%20shelves%2C%20equipment%2C%20and%20inventory%20management%20system%2C%20blue%20toned%2C%20professional%20photography%2C%20low%20saturation%2C%20corporate%20style&image_size=landscape_16_9"
          alt="warehouse background"
          class="bg-image"
        />
        <div class="gradient-overlay" />
      </div>

      <div class="brand-content">
        <div class="floating-stats">
          <div class="stat-card stat-1">
            <span class="stat-label">系统状态</span>
            <span class="stat-value status-online">● 在线</span>
          </div>
          <div class="stat-card stat-2">
            <div class="stat-item">
              <span class="stat-number">1,248</span>
              <span class="stat-text">用户在线</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">27%</span>
              <span class="stat-text">库存利用率</span>
            </div>
          </div>
        </div>

        <div class="brand-info">
          <div class="brand-logo">
            <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect width="48" height="48" rx="12" fill="rgba(255,255,255,0.15)" stroke="rgba(255,255,255,0.3)" stroke-width="1.5"/>
              <path d="M14 16h20v4H14zM14 24h20v4H14zM14 32h14v4H14z" fill="#fff" opacity="0.95" />
            </svg>
          </div>
          <h1 class="brand-title">备品备件库房管理平台</h1>
          <p class="brand-slogan">高效管理，精准运维</p>
        </div>

        <div class="copyright">
          <p>© 2026 西安东信软件技术有限公司 版权所有</p>
        </div>
      </div>
    </div>

    <div class="login-section">
      <div class="login-container">
        <div class="login-header">
          <div class="header-logo">
            <svg viewBox="0 0 48 48" fill="none" xmlns="http://www.w3.org/2000/svg">
              <rect width="48" height="48" rx="12" fill="url(#loginGrad)" />
              <path d="M14 16h20v4H14zM14 24h20v4H14zM14 32h14v4H14z" fill="#fff" opacity="0.95" />
              <defs>
                <linearGradient id="loginGrad" x1="0" y1="0" x2="48" y2="48">
                  <stop offset="0%" stop-color="#3b82f6" />
                  <stop offset="100%" stop-color="#1d4ed8" />
                </linearGradient>
              </defs>
            </svg>
          </div>
          <h2 class="login-title">备品备件库房管理平台</h2>
          <p class="login-subtitle">请登录您的账号访问系统</p>
        </div>

        <el-form
          ref="formRef"
          :model="loginForm"
          :rules="rules"
          class="login-form"
          @keyup.enter="handleLogin"
        >
          <el-form-item prop="username">
            <el-input
              v-model="loginForm.username"
              placeholder="用户名/邮箱"
              :prefix-icon="User"
              size="large"
              class="form-input"
            />
          </el-form-item>

          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              :prefix-icon="Lock"
              size="large"
              show-password
              class="form-input"
            />
          </el-form-item>

          <el-form-item prop="captchaText">
            <div class="captcha-wrapper">
              <el-input
                v-model="loginForm.captchaText"
                placeholder="验证码"
                :prefix-icon="Key"
                size="large"
                class="form-input captcha-input"
              />
              <div class="captcha-box" @click="refreshCaptcha">
                <img v-if="captchaImage" :src="captchaImage" alt="验证码" />
                <span v-else class="captcha-placeholder">{{ captchaPlaceholder }}</span>
              </div>
            </div>
          </el-form-item>

          <el-form-item class="options-row">
            <el-checkbox v-model="rememberMe" class="remember-checkbox">记住我</el-checkbox>
            <a href="javascript:;" class="forgot-link">忘记密码?</a>
          </el-form-item>

          <el-form-item class="submit-row">
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              :disabled="!rsaReady"
              class="submit-button"
              @click="handleLogin"
            >
              <template #icon><Lock /></template>
              {{ loading ? '登录中...' : '安全登录' }}
            </el-button>
          </el-form-item>
        </el-form>

        <div class="login-footer">
          <p class="footer-support">
            遇到问题?
            <a href="javascript:;" class="support-link">联系技术支持</a>
          </p>
        </div>

        <div class="page-copyright">
          <p>© 2026 西东信软件技术有限公司. 保留所有权利.</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { User, Lock, Key } from '@element-plus/icons-vue'
import { useUserStore } from '@/store/modules/user'
import { getRsaPublicKey, getCaptchaImage } from '@/api/system/auth'
import { rsaEncrypt } from '@/utils/crypto'

const REMEMBER_KEY = 'wms_login_remembered_username'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive({ username: '', password: '', captchaText: '' })
const captchaImage = ref('')
const captchaKey = ref('')
const rsaPublicKey = ref('')
const rsaKeyId = ref('')
const rsaReady = ref(false)

const rememberedUsername = localStorage.getItem(REMEMBER_KEY)
const rememberMe = ref(!!rememberedUsername)
if (rememberedUsername) {
  loginForm.username = rememberedUsername
}

const captchaPlaceholder = computed(() => {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'
  return Array.from({ length: 4 }, () => chars[Math.floor(Math.random() * chars.length)]).join('')
})

const rules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 2, max: 50, message: '用户名长度2-50位', trigger: 'blur' }
  ],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  captchaText: [
    { required: true, message: '请输入验证码', trigger: 'blur' },
    { min: 4, max: 6, message: '验证码长度4-6位', trigger: 'blur' }
  ]
}

async function initRsaKey() {
  try {
    const res = await getRsaPublicKey()
    rsaPublicKey.value = res.data.publicKey
    rsaKeyId.value = res.data.keyId
    rsaReady.value = true
  } catch {
    rsaReady.value = false
    ElMessage.error('安全认证服务不可用，请稍后重试')
  }
}

async function refreshCaptcha() {
  try {
    const res = await getCaptchaImage()
    captchaImage.value = res.data.captchaImage
    captchaKey.value = res.data.captchaKey
  } catch {
    ElMessage.error('验证码获取失败，请刷新页面重试')
  }
}

async function handleLogin() {
  await formRef.value?.validate()
  if (!rsaReady.value) {
    ElMessage.error('安全认证服务不可用，请稍后重试')
    return
  }
  loading.value = true
  try {
    const encryptedPassword = rsaEncrypt(loginForm.password, rsaPublicKey.value)
    await userStore.login({
      username: loginForm.username,
      encryptedPassword,
      captchaKey: captchaKey.value,
      captchaText: loginForm.captchaText
    })
    if (rememberMe.value) {
      localStorage.setItem(REMEMBER_KEY, loginForm.username)
    } else {
      localStorage.removeItem(REMEMBER_KEY)
    }
    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
  } catch (e: unknown) {
    const err = e as { message?: string }
    handleLoginError(err.message || '')
  } finally {
    loading.value = false
  }
}

function handleLoginError(msg: string) {
  if (msg.includes('用户名或密码错误') || msg.includes('CREDENTIAL_INVALID')) {
    ElMessage.error('用户名或密码错误')
    loginForm.password = ''
    loginForm.captchaText = ''
    refreshCaptcha()
  } else if (msg.includes('验证码错误') || msg.includes('CAPTCHA_MISMATCH')) {
    ElMessage.error('验证码错误')
    loginForm.captchaText = ''
    refreshCaptcha()
  } else if (msg.includes('验证码无效') || msg.includes('CAPTCHA_INVALID')) {
    ElMessage.error('验证码已过期，请刷新验证码')
    loginForm.captchaText = ''
    refreshCaptcha()
  } else if (msg.includes('锁定') || msg.includes('LOCKED')) {
    ElMessage.error('账号已被锁定，请稍后重试')
    refreshCaptcha()
  } else if (msg.includes('禁用') || msg.includes('DISABLED')) {
    ElMessage.error('账号已被禁用')
  } else if (msg.includes('频繁') || msg.includes('RATE_LIMITED')) {
    ElMessage.error('请求过于频繁，请稍后重试')
  } else if (msg.includes('解密') || msg.includes('DECRYPT')) {
    ElMessage.error('认证信息异常，请重新登录')
    initRsaKey()
    refreshCaptcha()
  } else if (msg.includes('加密')) {
    ElMessage.error(msg)
  }
}

onMounted(() => {
  Promise.all([initRsaKey(), refreshCaptcha()])
})

watch(rememberMe, (val) => {
  if (!val) {
    localStorage.removeItem(REMEMBER_KEY)
  }
})
</script>

<style lang="scss" scoped>
$primary-blue: #2563eb;
$primary-dark: #1e40af;
$text-primary: #1e293b;
$text-secondary: #64748b;
$text-muted: #94a3b8;
$border-color: #e2e8f0;
$input-bg: #f8fafc;
$card-radius: 16px;

.login-page {
  display: flex;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC", "Hiragino Sans GB", "Microsoft YaHei", sans-serif;
}

.brand-section {
  position: relative;
  width: 60%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;

  .brand-bg {
    position: absolute;
    inset: 0;

    .bg-image {
      width: 100%;
      height: 100%;
      object-fit: cover;
    }

    .gradient-overlay {
      position: absolute;
      inset: 0;
      background: linear-gradient(
        135deg,
        rgba(15, 23, 42, 0.85) 0%,
        rgba(30, 58, 95, 0.75) 50%,
        rgba(15, 23, 42, 0.9) 100%
      );
    }
  }

  .brand-content {
    position: relative;
    z-index: 1;
    width: 100%;
    height: 100%;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    padding: 60px;
    color: #fff;
  }

  .floating-stats {
    position: absolute;
    top: 80px;
    right: 60px;
    display: flex;
    flex-direction: column;
    gap: 16px;

    .stat-card {
      background: rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(12px);
      border: 1px solid rgba(255, 255, 255, 0.15);
      border-radius: 12px;
      padding: 16px 20px;

      &.stat-1 {
        display: flex;
        align-items: center;
        gap: 12px;

        .stat-label {
          font-size: 13px;
          color: rgba(255, 255, 255, 0.7);
        }

        .status-online {
          color: #4ade80;
          font-size: 13px;
          font-weight: 500;
        }
      }

      &.stat-2 {
        display: flex;
        gap: 32px;

        .stat-item {
          display: flex;
          flex-direction: column;
          gap: 4px;

          .stat-number {
            font-size: 22px;
            font-weight: 700;
            color: #fff;
          }

          .stat-text {
            font-size: 11px;
            color: rgba(255, 255, 255, 0.6);
          }
        }
      }
    }
  }

  .brand-info {
    margin-bottom: auto;

    .brand-logo {
      width: 64px;
      height: 64px;
      margin-bottom: 24px;

      svg {
        width: 100%;
        height: 100%;
        filter: drop-shadow(0 4px 12px rgba(0, 0, 0, 0.2));
      }
    }

    .brand-title {
      font-size: 32px;
      font-weight: 700;
      margin: 0 0 12px;
      letter-spacing: 2px;
      background: linear-gradient(135deg, #fff 0%, rgba(255, 255, 255, 0.85) 100%);
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
    }

    .brand-slogan {
      font-size: 16px;
      font-weight: 400;
      margin: 0;
      color: rgba(255, 255, 255, 0.7);
      letter-spacing: 4px;
    }
  }

  .copyright {
    p {
      font-size: 12px;
      color: rgba(255, 255, 255, 0.45);
      margin: 0;
      letter-spacing: 1px;
    }
  }
}

.login-section {
  width: 40%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f1f5f9;
  padding: 40px;

  .login-container {
    width: 100%;
    max-width: 460px;
    background: #fff;
    border-radius: $card-radius;
    box-shadow:
      0 4px 6px -1px rgba(0, 0, 0, 0.05),
      0 10px 15px -3px rgba(0, 0, 0, 0.08),
      0 0 0 1px rgba(0, 0, 0, 0.03);
    padding: 48px 44px;
  }

  .login-header {
    text-align: center;
    margin-bottom: 24px;

    .header-logo {
      width: 56px;
      height: 56px;
      margin: 0 auto 20px;

      svg {
        width: 100%;
        height: 100%;
      }
    }

    .login-title {
      font-size: 22px;
      font-weight: 700;
      color: $text-primary;
      margin: 0 0 8px;
      letter-spacing: 1px;
    }

    .login-subtitle {
      font-size: 14px;
      color: $text-secondary;
      margin: 0;
    }
  }

  .login-form {
    :deep(.el-form-item) {
      margin-bottom: 16px;
    }

    :deep(.el-form-item__error) {
      font-size: 12px;
      padding-top: 4px;
    }
  }

  .form-input {
    :deep(.el-input__wrapper) {
      background: $input-bg;
      border-radius: 10px;
      box-shadow: none;
      border: 1.5px solid transparent;
      padding: 4px 16px;
      transition: all 0.25s ease;

      &:hover {
        border-color: #cbd5e1;
        background: #fff;
      }

      &.is-focus {
        border-color: $primary-blue;
        background: #fff;
        box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.08);
      }
    }

    :deep(.el-input__prefix-inner) {
      color: $text-muted;
    }

    :deep(.el-input__inner) {
      font-size: 14px;
      line-height: 1.5;
    }
  }

  .captcha-wrapper {
    display: flex;
    gap: 12px;
    width: 100%;
    align-items: stretch;

    .captcha-input {
      flex: 1;
    }

    .captcha-box {
      width: 120px;
      height: 40px;
      border: 1.5px solid $border-color;
      border-radius: 10px;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      overflow: hidden;
      flex-shrink: 0;
      background: $input-bg;
      transition: all 0.25s ease;

      &:hover {
        border-color: $primary-blue;
        box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.08);
      }

      &:active {
        transform: scale(0.97);
      }

      img {
        width: 100%;
        height: 100%;
        object-fit: cover;
      }

      .captcha-placeholder {
        font-size: 18px;
        font-weight: 600;
        letter-spacing: 4px;
        color: $primary-blue;
        font-family: "Courier New", monospace;
        user-select: none;
      }
    }
  }

  .options-row {
    margin-bottom: 24px !important;

    :deep(.el-form-item__content) {
      justify-content: space-between;
      flex-wrap: nowrap;
    }

    .remember-checkbox {
      :deep(.el-checkbox__label) {
        font-size: 13px;
        color: $text-secondary;
      }
    }

    .forgot-link {
      font-size: 13px;
      color: $primary-blue;
      text-decoration: none;
      font-weight: 500;
      transition: opacity 0.2s;

      &:hover {
        opacity: 0.8;
      }
    }
  }

  .submit-row {
    margin-bottom: 0 !important;
  }

  .submit-button {
    width: 100%;
    height: 46px;
    font-size: 15px;
    font-weight: 600;
    letter-spacing: 2px;
    border-radius: 10px;
    background: $primary-blue;
    border: none;
    box-shadow: 0 4px 12px rgba(37, 99, 235, 0.3);
    transition: all 0.3s ease;

    &:not(.is-disabled):hover {
      background: $primary-dark;
      box-shadow: 0 6px 16px rgba(37, 99, 235, 0.4);
      transform: translateY(-1px);
    }

    &:not(.is-disabled):active {
      transform: translateY(0);
      box-shadow: 0 2px 6px rgba(37, 99, 235, 0.25);
    }

    &.is-loading {
      background: $primary-blue;
    }
  }

  .login-footer {
    margin-top: 32px;
    padding-top: 24px;
    border-top: 1px solid #f1f5f9;
    text-align: center;

    .footer-support {
      font-size: 12px;
      color: $text-muted;
      margin: 0;

      .support-link {
        color: $primary-blue;
        text-decoration: none;
        font-weight: 500;
        margin-left: 4px;

        &:hover {
          text-decoration: underline;
        }
      }
    }
  }

  .page-copyright {
    margin-top: 24px;
    text-align: center;

    p {
      font-size: 11px;
      color: #cbd5e1;
      margin: 0;
    }
  }
}
</style>

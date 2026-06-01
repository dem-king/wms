<template>
  <div class="profile-page">
    <div class="profile-bento">
      <!-- 1. Hero Section -->
      <section class="bento-item bento-hero">
        <div class="hero-bg"></div>
        <div class="hero-content">
          <div class="hero-identity">
            <el-avatar v-if="profileUserInfo?.avatar" :size="80" :src="profileUserInfo.avatar" class="hero-avatar" />
            <div v-else class="hero-avatar hero-avatar--text">{{ displayInitial }}</div>
            <div class="hero-meta">
              <div class="hero-title">账号概览</div>
              <h1 class="hero-name">{{ displayName }}</h1>
              <p class="hero-subtitle">{{ profileUserInfo?.username || '未获取到用户名' }}</p>
            </div>
          </div>
          <div class="hero-actions">
            <el-tag type="success" effect="light" round size="large">
              <template #icon><el-icon><Check /></el-icon></template>
              已登录
            </el-tag>
            <el-tag effect="light" round size="large" :type="userStore.isLogin() ? 'primary' : 'danger'">
              <template #icon><el-icon><Key /></el-icon></template>
              {{ tokenStatusLabel }}
            </el-tag>
          </div>
        </div>
      </section>

      <!-- 2. Stats Section -->
      <div class="bento-stats">
        <el-card shadow="never" class="bento-card stat-card">
          <div class="stat-icon stat-icon--primary">
            <el-icon><Lock /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ profilePermissions.length }}</div>
            <div class="stat-label">权限数量</div>
          </div>
        </el-card>

        <el-card shadow="never" class="bento-card stat-card">
          <div class="stat-icon stat-icon--success">
            <el-icon><User /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ profileRoles.length }}</div>
            <div class="stat-label">角色数量</div>
          </div>
        </el-card>

        <el-card shadow="never" class="bento-card stat-card">
          <div class="stat-icon stat-icon--warning">
            <el-icon><Timer /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value stat-value--text">{{ formattedLoginTime }}</div>
            <div class="stat-label">最近登录时间</div>
          </div>
        </el-card>
      </div>

      <!-- 3. Basic Info -->
      <el-card shadow="never" class="bento-card bento-basic">
        <template #header>
          <div class="card-header">
            <el-icon><InfoFilled /></el-icon>
            <span>基础资料</span>
          </div>
        </template>
        <el-descriptions :column="2" class="profile-descriptions">
          <el-descriptions-item label="用户ID">{{ profileUserInfo?.userId ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="部门ID">{{ profileUserInfo?.deptId ?? '-' }}</el-descriptions-item>
          <el-descriptions-item label="用户名">{{ profileUserInfo?.username || '-' }}</el-descriptions-item>
          <el-descriptions-item label="真实姓名">{{ profileUserInfo?.realName || profileUserInfo?.username || '用户' }}</el-descriptions-item>
          <el-descriptions-item label="最近登录IP" :span="2">{{ lastLoginIpText }}</el-descriptions-item>
        </el-descriptions>
      </el-card>

      <!-- 4. Security Center -->
      <el-card shadow="never" class="bento-card bento-security">
        <template #header>
          <div class="card-header">
            <el-icon><Setting /></el-icon>
            <span>安全中心</span>
          </div>
        </template>
        <div class="security-actions">
          <el-button class="security-btn" type="primary" plain @click="openPasswordDialog">
            <el-icon><Lock /></el-icon>修改密码
          </el-button>
          <el-button class="security-btn" @click="loadProfile">
            <el-icon><Refresh /></el-icon>刷新资料
          </el-button>
          <el-button class="security-btn" type="danger" plain @click="handleLogout">
            <el-icon><SwitchButton /></el-icon>退出登录
          </el-button>
        </div>
      </el-card>

      <!-- 5. Edit Profile -->
      <el-card shadow="never" class="bento-card bento-edit">
        <template #header>
          <div class="card-header">
            <el-icon><Edit /></el-icon>
            <span>编辑资料</span>
          </div>
        </template>
        <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-position="top" class="profile-edit-form">
          <div class="profile-avatar-editor">
            <el-avatar v-if="previewAvatarUrl" :size="84" :src="previewAvatarUrl" class="avatar-preview" />
            <div v-else class="hero-avatar hero-avatar--text avatar-preview">{{ displayInitial }}</div>
            <div class="profile-avatar-editor__actions">
              <input
                ref="avatarInputRef"
                class="profile-avatar-editor__input"
                type="file"
                accept="image/png,image/jpeg,image/jpg,image/webp"
                @change="handleAvatarChange"
              />
              <el-button type="primary" plain @click="triggerAvatarSelect">
                <el-icon><Upload /></el-icon>更换头像
              </el-button>
              <span class="profile-avatar-editor__tip">支持 PNG/JPG/JPEG/WEBP，上传后自动预览</span>
            </div>
          </div>

          <div class="edit-form-grid">
            <el-form-item label="真实姓名" prop="realName">
              <el-input v-model="editForm.realName" placeholder="请输入真实姓名">
                <template #prefix><el-icon><User /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="editForm.phone" placeholder="请输入手机号">
                <template #prefix><el-icon><Iphone /></el-icon></template>
              </el-input>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="editForm.email" placeholder="请输入邮箱">
                <template #prefix><el-icon><Message /></el-icon></template>
              </el-input>
            </el-form-item>
          </div>

          <div class="profile-actions">
            <el-button @click="resetEditForm">重置</el-button>
            <el-button type="primary" :loading="saveLoading" @click="handleSaveProfile">保存资料</el-button>
          </div>
        </el-form>
      </el-card>
    </div>

    <ChangePasswordDialog v-model:visible="passwordDialogVisible" />
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getProfile, updateProfile } from '@/api/system/auth'
import type { LastLoginInfoVO, ProfileResp, UpdateProfileReq, UserInfoVO } from '@/types/auth'
import { useUserStore } from '@/store/modules/user'
import { useFileUpload } from '@/hooks/useFileUpload'
import ChangePasswordDialog from '@/views/system/password/ChangePasswordDialog.vue'
import { 
  Check, 
  Key, 
  Lock, 
  User, 
  Timer, 
  InfoFilled, 
  Setting, 
  Edit, 
  Refresh, 
  SwitchButton, 
  Upload, 
  Iphone, 
  Message 
} from '@element-plus/icons-vue'

const userStore = useUserStore()
const passwordDialogVisible = ref(false)
const profile = ref<ProfileResp | null>(null)
const saveLoading = ref(false)
const editFormRef = ref<FormInstance>()
const avatarInputRef = ref<HTMLInputElement>()
const previewAvatarUrl = ref('')
let previewObjectUrl: string | null = null

const { upload: uploadAvatarFile } = useFileUpload({
  bucket: 'avatars',
  beforeUpload: (file: File) => {
    const validTypes = ['image/png', 'image/jpg', 'image/jpeg', 'image/webp']
    if (!validTypes.includes(file.type)) {
      ElMessage.error('头像仅支持 PNG、JPG、JPEG、WEBP 图片')
      return false
    }
    if (file.size > 2 * 1024 * 1024) {
      ElMessage.error('头像大小不能超过2MB')
      return false
    }
    return true
  }
})

const editForm = reactive<UpdateProfileReq>({
  realName: '',
  phone: '',
  email: '',
  avatar: '',
})

const editRules: FormRules = {
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  phone: [{ pattern: /^$|^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }],
}

const profileUserInfo = computed<UserInfoVO | null>(() => profile.value?.userInfo || userStore.userInfo)
const profilePermissions = computed(() => profile.value?.permissions || userStore.permissions)
const profileRoles = computed(() => profile.value?.roles || userStore.roles)
const lastLoginInfo = computed<LastLoginInfoVO | null>(() => profile.value?.lastLoginInfo || null)

const displayName = computed(() => profileUserInfo.value?.realName || profileUserInfo.value?.username || '用户')
const displayInitial = computed(() => displayName.value.trim().slice(0, 1).toUpperCase() || 'U')
const tokenStatusLabel = computed(() => (userStore.isLogin() ? '有效' : '失效'))
const formattedLoginTime = computed(() => formatDateTime(lastLoginInfo.value?.loginTime || null))
const lastLoginIpText = computed(() => lastLoginInfo.value?.loginIp || '暂无记录')

onMounted(() => {
  loadProfile()
})

onBeforeUnmount(() => {
  cleanupPreviewObjectUrl()
})

async function loadProfile() {
  try {
    const res = await getProfile()
    applyProfileData(res.data)
  } catch (error) {
    console.error('获取个人中心信息失败:', error)
    ElMessage.error('获取个人中心信息失败')
  }
}

function openPasswordDialog() {
  passwordDialogVisible.value = true
}

function handleLogout() {
  userStore.logout()
}

function triggerAvatarSelect() {
  avatarInputRef.value?.click()
}

async function handleAvatarChange(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) {
    return
  }

  cleanupPreviewObjectUrl()
  previewObjectUrl = URL.createObjectURL(file)
  previewAvatarUrl.value = previewObjectUrl

  try {
    // 通过useFileUpload统一上传，自动适配本地/MinIO存储
    const result = await uploadAvatarFile(file)
    editForm.avatar = result.url
    ElMessage.success('头像上传成功')
  } catch (error) {
    console.error('头像上传失败:', error)
    cleanupPreviewObjectUrl()
    previewAvatarUrl.value = profileUserInfo.value?.avatar || ''
    ElMessage.error('头像上传失败')
  } finally {
    input.value = ''
  }
}

async function handleSaveProfile() {
  await editFormRef.value?.validate()
  saveLoading.value = true
  try {
    const res = await updateProfile({
      realName: editForm.realName,
      phone: editForm.phone?.trim() || '',
      email: editForm.email?.trim() || '',
      avatar: editForm.avatar?.trim() || '',
    })
    applyProfileData(res.data)
    ElMessage.success('个人资料保存成功')
  } catch (error) {
    console.error('保存个人资料失败:', error)
    ElMessage.error('保存个人资料失败')
  } finally {
    saveLoading.value = false
  }
}

function resetEditForm() {
  applyEditForm(profileUserInfo.value)
}

function applyProfileData(data: ProfileResp) {
  profile.value = data
  userStore.syncUserInfo(data.userInfo)
  applyEditForm(data.userInfo)
}

function applyEditForm(userInfo: UserInfoVO | null) {
  editForm.realName = userInfo?.realName || ''
  editForm.phone = userInfo?.phone || ''
  editForm.email = userInfo?.email || ''
  editForm.avatar = userInfo?.avatar || ''
  previewAvatarUrl.value = userInfo?.avatar || ''
}

function cleanupPreviewObjectUrl() {
  if (!previewObjectUrl) {
    return
  }
  URL.revokeObjectURL(previewObjectUrl)
  previewObjectUrl = null
}

function formatDateTime(loginTime: string | null) {
  if (!loginTime) {
    return '暂无记录'
  }

  const date = new Date(loginTime)
  if (Number.isNaN(date.getTime())) {
    return loginTime
  }

  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit',
    hour12: false,
  })
}
</script>

<style lang="scss" scoped>
.profile-page {
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
}

.profile-bento {
  display: grid;
  grid-template-columns: repeat(12, 1fr);
  gap: 20px;
}

/* Base Card Style */
.bento-card {
  border: none !important;
  border-radius: 12px !important;
  background: var(--el-bg-color-overlay);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03) !important;
  transition: transform 0.3s ease, box-shadow 0.3s ease;

  &:hover {
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08) !important;
    transform: translateY(-2px);
  }

  :deep(.el-card__header) {
    border-bottom: 1px solid var(--el-border-color-lighter);
    padding: 18px 24px;
  }

  :deep(.el-card__body) {
    padding: 24px;
    height: 100%;
    box-sizing: border-box;
  }
}

.card-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-weight: 600;
  font-size: 16px;
  color: var(--el-text-color-primary);

  .el-icon {
    font-size: 18px;
    color: var(--el-color-primary);
  }
}

/* 1. Hero Section */
.bento-hero {
  grid-column: span 12;
  position: relative;
  border-radius: 12px;
  background: linear-gradient(135deg, var(--el-color-primary-light-8) 0%, var(--el-bg-color-overlay) 100%);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.03);
  overflow: hidden;
  transition: transform 0.3s ease, box-shadow 0.3s ease;

  &:hover {
    box-shadow: 0 8px 30px rgba(0, 0, 0, 0.08);
    transform: translateY(-2px);
  }
}

.hero-bg {
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  width: 50%;
  background: radial-gradient(circle at top right, var(--el-color-primary-light-5), transparent 60%);
  opacity: 0.3;
  pointer-events: none;
}

.hero-content {
  position: relative;
  padding: 32px 40px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
  z-index: 1;
}

.hero-identity {
  display: flex;
  align-items: center;
  gap: 24px;
}

.hero-avatar {
  border: 4px solid var(--el-bg-color-overlay);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  flex-shrink: 0;
}

.hero-avatar--text {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--el-color-primary), var(--el-color-primary-light-3));
  color: #fff;
  font-size: 32px;
  font-weight: 600;
}

.hero-meta {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.hero-title {
  color: var(--el-text-color-secondary);
  font-size: 14px;
  font-weight: 500;
  letter-spacing: 1px;
}

.hero-name {
  margin: 0;
  color: var(--el-text-color-primary);
  font-size: 28px;
  font-weight: 700;
  line-height: 1.2;
}

.hero-subtitle {
  margin: 0;
  color: var(--el-text-color-regular);
  font-size: 15px;
}

.hero-actions {
  display: flex;
  gap: 12px;
}

/* 2. Stats Section */
.bento-stats {
  grid-column: span 12;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.stat-card {
  :deep(.el-card__body) {
    display: flex;
    align-items: center;
    padding: 24px 32px;
    gap: 24px;
  }
}

.stat-icon {
  width: 64px;
  height: 64px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  flex-shrink: 0;
}

.stat-icon--primary {
  background: var(--el-color-primary-light-9);
  color: var(--el-color-primary);
}

.stat-icon--success {
  background: var(--el-color-success-light-9);
  color: var(--el-color-success);
}

.stat-icon--warning {
  background: var(--el-color-warning-light-9);
  color: var(--el-color-warning);
}

.stat-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  min-width: 0;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--el-text-color-primary);
  line-height: 1.2;
}

.stat-value--text {
  font-size: 18px;
}

.stat-label {
  font-size: 14px;
  color: var(--el-text-color-secondary);
}

/* 3. Basic Info & 4. Security Center */
.bento-basic {
  grid-column: span 8;
}

.bento-security {
  grid-column: span 4;
}

.profile-descriptions {
  :deep(.el-descriptions__cell) {
    padding-bottom: 20px !important;
  }
  :deep(.el-descriptions__label) {
    width: 100px;
    color: var(--el-text-color-secondary);
    font-size: 14px;
  }
  :deep(.el-descriptions__content) {
    color: var(--el-text-color-primary);
    font-size: 14px;
    font-weight: 500;
  }
}

.security-actions {
  display: flex;
  flex-direction: column;
  gap: 16px;
  height: 100%;
}

.security-btn {
  width: 100%;
  margin-left: 0 !important;
  justify-content: flex-start;
  padding: 20px 24px;
  height: auto;
  font-size: 15px;
  border-radius: 10px;
  
  .el-icon {
    margin-right: 12px;
    font-size: 20px;
  }
}

/* 5. Edit Profile */
.bento-edit {
  grid-column: span 12;
}

.profile-avatar-editor {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 32px;
  padding-bottom: 32px;
  border-bottom: 1px dashed var(--el-border-color-lighter);
}

.avatar-preview {
  width: 84px !important;
  height: 84px !important;
  font-size: 32px;
}

.profile-avatar-editor__actions {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 12px;
}

.profile-avatar-editor__input {
  display: none;
}

.profile-avatar-editor__tip {
  color: var(--el-text-color-secondary);
  font-size: 13px;
}

.edit-form-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 24px;
}

.profile-actions {
  margin-top: 32px;
  display: flex;
  justify-content: flex-end;
  gap: 16px;
}

/* Responsive */
@media (max-width: 1024px) {
  .bento-basic {
    grid-column: span 12;
  }
  .bento-security {
    grid-column: span 12;
  }
  .security-actions {
    flex-direction: row;
    flex-wrap: wrap;
  }
  .security-btn {
    width: auto;
    flex: 1;
    justify-content: center;
  }
}

@media (max-width: 768px) {
  .hero-content {
    flex-direction: column;
    align-items: flex-start;
    padding: 24px;
  }
  .bento-stats {
    grid-template-columns: 1fr;
  }
  .edit-form-grid {
    grid-template-columns: 1fr;
  }
  .security-actions {
    flex-direction: column;
  }
}
</style>
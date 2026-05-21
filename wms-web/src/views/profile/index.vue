<template>
  <div class="profile-page">
    <section class="profile-hero">
      <div class="profile-hero__identity">
        <el-avatar v-if="profileUserInfo?.avatar" :size="72" :src="profileUserInfo.avatar" />
        <div v-else class="profile-hero__avatar">{{ displayInitial }}</div>
        <div class="profile-hero__meta">
          <div class="profile-hero__title">账号概览</div>
          <h1 class="profile-hero__name">{{ displayName }}</h1>
          <p class="profile-hero__subtitle">{{ profileUserInfo?.username || '未获取到用户名' }}</p>
        </div>
      </div>
      <div class="profile-hero__badges">
        <el-tag type="success">已登录</el-tag>
        <el-tag>{{ tokenStatusLabel }}</el-tag>
      </div>
    </section>

    <div class="profile-grid">
      <el-card shadow="hover" class="profile-card">
        <template #header>基础资料</template>
        <div class="profile-info-list">
          <div class="profile-info-item">
            <span class="profile-info-item__label">用户ID</span>
            <span class="profile-info-item__value">{{ profileUserInfo?.userId ?? '-' }}</span>
          </div>
          <div class="profile-info-item">
            <span class="profile-info-item__label">用户名</span>
            <span class="profile-info-item__value">{{ profileUserInfo?.username || '-' }}</span>
          </div>
          <div class="profile-info-item">
            <span class="profile-info-item__label">真实姓名</span>
            <span class="profile-info-item__value">{{ profileUserInfo?.realName || profileUserInfo?.username || '用户' }}</span>
          </div>
          <div class="profile-info-item">
            <span class="profile-info-item__label">部门ID</span>
            <span class="profile-info-item__value">{{ profileUserInfo?.deptId ?? '-' }}</span>
          </div>
          <div class="profile-info-item">
            <span class="profile-info-item__label">头像地址</span>
            <span class="profile-info-item__value profile-info-item__value--break">{{ profileUserInfo?.avatar || '未设置' }}</span>
          </div>
        </div>
      </el-card>

      <el-card shadow="hover" class="profile-card">
        <template #header>登录信息</template>
        <div class="profile-info-list">
          <div class="profile-info-item">
            <span class="profile-info-item__label">最近登录时间</span>
            <span class="profile-info-item__value">{{ formattedLoginTime }}</span>
          </div>
          <div class="profile-info-item">
            <span class="profile-info-item__label">最近登录IP</span>
            <span class="profile-info-item__value">{{ lastLoginIpText }}</span>
          </div>
        </div>
      </el-card>

      <el-card shadow="hover" class="profile-card">
        <template #header>编辑资料</template>
        <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="88px" class="profile-edit-form">
          <div class="profile-avatar-editor">
            <el-avatar v-if="previewAvatarUrl" :size="84" :src="previewAvatarUrl" />
            <div v-else class="profile-hero__avatar profile-hero__avatar--large">{{ displayInitial }}</div>
            <div class="profile-avatar-editor__actions">
              <input
                ref="avatarInputRef"
                class="profile-avatar-editor__input"
                type="file"
                accept="image/png,image/jpeg,image/jpg,image/webp"
                @change="handleAvatarChange"
              />
              <el-button @click="triggerAvatarSelect">更换头像</el-button>
              <span class="profile-avatar-editor__tip">支持 PNG/JPG/JPEG/WEBP，上传后自动预览</span>
            </div>
          </div>

          <el-form-item label="真实姓名" prop="realName">
            <el-input v-model="editForm.realName" placeholder="请输入真实姓名" />
          </el-form-item>
          <el-form-item label="手机号" prop="phone">
            <el-input v-model="editForm.phone" placeholder="请输入手机号" />
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="editForm.email" placeholder="请输入邮箱" />
          </el-form-item>
          <div class="profile-actions">
            <el-button @click="resetEditForm">重置</el-button>
            <el-button type="primary" :loading="saveLoading" @click="handleSaveProfile">保存资料</el-button>
          </div>
        </el-form>
      </el-card>

      <el-card shadow="hover" class="profile-card">
        <template #header>账户摘要</template>
        <div class="profile-summary">
          <div class="profile-summary__item">
            <span class="profile-summary__value">{{ profilePermissions.length }}</span>
            <span class="profile-summary__label">权限数量</span>
          </div>
          <div class="profile-summary__item">
            <span class="profile-summary__value">{{ profileRoles.length }}</span>
            <span class="profile-summary__label">角色数量</span>
          </div>
          <div class="profile-summary__item">
            <span class="profile-summary__value">{{ tokenStatusLabel }}</span>
            <span class="profile-summary__label">Token状态</span>
          </div>
        </div>
      </el-card>

      <el-card shadow="hover" class="profile-card">
        <template #header>安全中心</template>
        <div class="profile-actions">
          <el-button type="primary" @click="openPasswordDialog">修改密码</el-button>
          <el-button @click="loadProfile">刷新资料</el-button>
          <el-button type="danger" plain @click="handleLogout">退出登录</el-button>
        </div>
      </el-card>
    </div>

    <ChangePasswordDialog v-model:visible="passwordDialogVisible" />
  </div>
</template>

<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { getProfile, updateProfile, uploadAvatar } from '@/api/system/auth'
import type { LastLoginInfoVO, ProfileResp, UpdateProfileReq, UserInfoVO } from '@/types/auth'
import { useUserStore } from '@/store/modules/user'
import ChangePasswordDialog from '@/views/system/password/ChangePasswordDialog.vue'

const userStore = useUserStore()
const passwordDialogVisible = ref(false)
const profile = ref<ProfileResp | null>(null)
const saveLoading = ref(false)
const editFormRef = ref<FormInstance>()
const avatarInputRef = ref<HTMLInputElement>()
const previewAvatarUrl = ref('')
let previewObjectUrl: string | null = null

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

  const formData = new FormData()
  formData.append('file', file)

  try {
    const res = await uploadAvatar(formData)
    editForm.avatar = res.data.avatarUrl
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
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.profile-hero {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 16px;
  padding: 24px 28px;
  border: 1px solid hsl(var(--border));
  border-radius: 24px;
  background: linear-gradient(135deg, hsl(var(--primary) / 0.08), hsl(var(--card)));
}

.profile-hero__identity {
  display: flex;
  align-items: center;
  gap: 18px;
}

.profile-hero__avatar {
  width: 72px;
  height: 72px;
  border-radius: 24px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, hsl(var(--primary)), hsl(220 95% 62%));
  color: #fff;
  font-size: 28px;
  font-weight: 700;
}

.profile-hero__meta {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.profile-hero__title {
  color: var(--text-muted-foreground);
  font-size: 13px;
}

.profile-hero__name {
  margin: 0;
  color: var(--text-foreground);
  font-size: 28px;
  line-height: 1.2;
}

.profile-hero__subtitle {
  margin: 0;
  color: var(--text-muted-foreground);
  font-size: 14px;
}

.profile-hero__badges {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
}

.profile-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 20px;
}

.profile-card {
  border-radius: 20px;
}

.profile-edit-form {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.profile-avatar-editor {
  display: flex;
  align-items: center;
  gap: 18px;
  padding-bottom: 18px;
}

.profile-avatar-editor__actions {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
}

.profile-avatar-editor__input {
  display: none;
}

.profile-avatar-editor__tip {
  color: var(--text-muted-foreground);
  font-size: 12px;
}

.profile-info-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.profile-info-item {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid hsl(var(--border) / 0.7);
}

.profile-info-item:last-child {
  padding-bottom: 0;
  border-bottom: none;
}

.profile-info-item__label {
  color: var(--text-muted-foreground);
}

.profile-info-item__value {
  color: var(--text-foreground);
  font-weight: 500;
  text-align: right;
}

.profile-info-item__value--break {
  word-break: break-all;
}

.profile-hero__avatar--large {
  width: 84px;
  height: 84px;
  border-radius: 28px;
  font-size: 32px;
}

.profile-summary {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 14px;
}

.profile-summary__item {
  padding: 18px 14px;
  border-radius: 18px;
  background: hsl(var(--accent));
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
}

.profile-summary__value {
  color: var(--color-primary);
  font-size: 20px;
  font-weight: 700;
}

.profile-summary__label {
  color: var(--text-muted-foreground);
  font-size: 13px;
}

.profile-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

@media (max-width: 960px) {
  .profile-hero {
    flex-direction: column;
    align-items: flex-start;
  }

  .profile-grid {
    grid-template-columns: 1fr;
  }

  .profile-summary {
    grid-template-columns: 1fr;
  }

  .profile-info-item {
    flex-direction: column;
  }

  .profile-info-item__value {
    text-align: left;
  }
}
</style>

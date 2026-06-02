<script setup lang="ts">
import { computed } from 'vue'

import { ArrowDown, Lock, SwitchButton, User } from '@element-plus/icons-vue'
import { resolveFileUrl } from '@/utils/file-url'

defineOptions({ name: 'UserDropdown' })

const props = withDefaults(
  defineProps<{
    avatar?: string
    description?: string
    name: string
  }>(),
  {
    avatar: '',
    description: 'WMS 管理后台',
  },
)

const emit = defineEmits<{
  profile: []
  changePassword: []
  logout: []
}>()

const initials = computed(() => props.name.trim().slice(0, 1).toUpperCase() || 'U')
const avatarUrl = computed(() => resolveFileUrl(props.avatar))
</script>

<template>
  <el-dropdown trigger="click" popper-class="user-dropdown-popper">
    <button class="user-dropdown-trigger" type="button">
      <el-avatar v-if="avatarUrl" :size="34" :src="avatarUrl" />
      <div v-else class="user-avatar-fallback">{{ initials }}</div>
      <div class="user-dropdown-meta">
        <span class="user-name">{{ name }}</span>
        <span class="user-description">{{ description }}</span>
      </div>
      <el-icon class="caret-icon"><ArrowDown /></el-icon>
    </button>

    <template #dropdown>
      <el-dropdown-menu class="user-dropdown-menu">
        <div class="user-card">
          <el-avatar v-if="avatarUrl" :size="42" :src="avatarUrl" />
          <div v-else class="user-avatar-fallback large">{{ initials }}</div>
          <div class="user-card-text">
            <div class="user-card-name">{{ name }}</div>
            <div class="user-card-description">{{ description }}</div>
          </div>
        </div>
        <el-dropdown-item @click="emit('profile')">
          <el-icon><User /></el-icon>
          个人中心
        </el-dropdown-item>
        <el-dropdown-item @click="emit('changePassword')">
          <el-icon><Lock /></el-icon>
          修改密码
        </el-dropdown-item>
        <el-dropdown-item divided @click="emit('logout')">
          <el-icon><SwitchButton /></el-icon>
          退出登录
        </el-dropdown-item>
      </el-dropdown-menu>
    </template>
  </el-dropdown>
</template>

<style lang="scss" scoped>
.user-dropdown-trigger {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
  padding: 4px 6px 4px 4px;
  border: 1px solid transparent;
  border-radius: 16px;
  background: transparent;
  cursor: pointer;
  transition:
    background-color 0.2s ease,
    border-color 0.2s ease,
    box-shadow 0.2s ease;

  &:hover {
    background: hsl(var(--accent));
    border-color: hsl(var(--border));
    box-shadow: 0 8px 24px hsl(var(--primary) / 0.08);
  }
}

.user-dropdown-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  min-width: 0;
}

.user-name {
  color: var(--text-foreground);
  font-size: 13px;
  font-weight: 600;
  line-height: 1.2;
}

.user-description {
  color: var(--text-muted-foreground);
  font-size: 11px;
  line-height: 1.2;
}

.caret-icon {
  color: var(--text-muted-foreground);
  font-size: 12px;
}

.user-avatar-fallback {
  width: 34px;
  height: 34px;
  border-radius: 12px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, hsl(var(--primary)), hsl(220 95% 62%));
  color: #fff;
  font-size: 14px;
  font-weight: 700;

  &.large {
    width: 42px;
    height: 42px;
    border-radius: 14px;
  }
}

:global(.user-dropdown-popper) {
  padding: 0;
  border: 1px solid hsl(var(--border));
  border-radius: 18px;
  box-shadow: 0 18px 46px hsl(220 43% 11% / 0.14);
}

:global(.user-dropdown-menu) {
  min-width: 240px;
  padding: 8px;
  background: var(--bg-card);
}

.user-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 8px 8px 12px;
}

.user-card-text {
  min-width: 0;
}

.user-card-name {
  color: var(--text-foreground);
  font-size: 14px;
  font-weight: 700;
}

.user-card-description {
  color: var(--text-muted-foreground);
  font-size: 12px;
}
</style>

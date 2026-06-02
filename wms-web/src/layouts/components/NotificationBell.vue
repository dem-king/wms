<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, shallowRef } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, Bell, Check } from '@element-plus/icons-vue'
import {
  MESSAGE_READ_STATUS_UNREAD,
  getMessagePage,
  getUnreadMessageCount,
  markMessageRead,
} from '@/api/system/message'
import type { SysMessageVo } from '@/types/system'

defineOptions({ name: 'NotificationBell' })

const router = useRouter()
const unreadCount = shallowRef(0)
const messages = shallowRef<SysMessageVo[]>([])
const loading = shallowRef(false)
const visible = shallowRef(false)
const pollIntervalMs = 45000
let pollTimer: number | undefined
let isUnreadRefreshing = false
let isListRefreshing = false

const badgeValue = computed(() => {
  if (unreadCount.value > 99) {
    return '99+'
  }
  return unreadCount.value
})

async function refreshUnreadCount() {
  if (isUnreadRefreshing || document.visibilityState === 'hidden') {
    return
  }
  isUnreadRefreshing = true
  try {
    const res = await getUnreadMessageCount()
    unreadCount.value = Number(res.data || 0)
  } finally {
    isUnreadRefreshing = false
  }
}

async function refreshMessageList() {
  if (isListRefreshing) {
    return
  }
  isListRefreshing = true
  loading.value = true
  try {
    const [countRes, listRes] = await Promise.all([
      getUnreadMessageCount(),
      getMessagePage({ page: 1, size: 5 }),
    ])
    unreadCount.value = Number(countRes.data || 0)
    messages.value = listRes.data.records || []
  } finally {
    isListRefreshing = false
    loading.value = false
  }
}

async function handleVisibleChange(nextVisible: boolean) {
  visible.value = nextVisible
  if (nextVisible) {
    await refreshMessageList()
  }
}

async function handleMessageClick(message: SysMessageVo) {
  if (message.readStatus === MESSAGE_READ_STATUS_UNREAD) {
    await markMessageRead(message.id)
  }
  await refreshMessageList()
  visible.value = false
  if (message.targetUrl) {
    router.push(message.targetUrl)
    return
  }
  router.push('/system/message')
}

function goMessageCenter() {
  visible.value = false
  router.push('/system/message')
}

function startPolling() {
  if (pollTimer || document.visibilityState === 'hidden') {
    return
  }
  refreshUnreadCount()
  pollTimer = window.setInterval(refreshUnreadCount, pollIntervalMs)
}

function stopPolling() {
  if (pollTimer) {
    window.clearInterval(pollTimer)
    pollTimer = undefined
  }
}

function handlePageVisibilityChange() {
  if (document.visibilityState === 'hidden') {
    stopPolling()
    return
  }
  startPolling()
}

onMounted(() => {
  startPolling()
  document.addEventListener('visibilitychange', handlePageVisibilityChange)
})

onBeforeUnmount(() => {
  stopPolling()
  document.removeEventListener('visibilitychange', handlePageVisibilityChange)
})
</script>

<template>
  <el-popover
    :visible="visible"
    placement="bottom-end"
    trigger="click"
    width="360"
    popper-class="notification-popover"
    @update:visible="handleVisibleChange"
  >
    <template #reference>
      <button class="notification-trigger" type="button" aria-label="消息通知">
        <el-badge :value="badgeValue" :hidden="unreadCount === 0" :max="99">
          <el-icon><Bell /></el-icon>
        </el-badge>
      </button>
    </template>

    <div class="notification-panel">
      <div class="notification-header">
        <div>
          <div class="notification-title">消息通知</div>
          <div class="notification-subtitle">{{ unreadCount }} 条未读</div>
        </div>
        <el-button text type="primary" :icon="ArrowRight" @click="goMessageCenter">消息中心</el-button>
      </div>

      <div v-loading="loading" class="notification-list">
        <button
          v-for="message in messages"
          :key="message.id"
          class="notification-item"
          :class="{ unread: message.readStatus === MESSAGE_READ_STATUS_UNREAD }"
          type="button"
          @click="handleMessageClick(message)"
        >
          <span class="notification-dot"></span>
          <span class="notification-body">
            <span class="notification-item-title">{{ message.title }}</span>
            <span class="notification-content">{{ message.content }}</span>
            <span class="notification-time">{{ message.createTime }}</span>
          </span>
          <el-icon v-if="message.readStatus !== MESSAGE_READ_STATUS_UNREAD" class="notification-read-icon">
            <Check />
          </el-icon>
        </button>

        <el-empty v-if="!loading && messages.length === 0" description="暂无消息" :image-size="76" />
      </div>
    </div>
  </el-popover>
</template>

<style lang="scss" scoped>
.notification-trigger {
  width: 38px;
  height: 38px;
  border: 1px solid transparent;
  border-radius: 12px;
  background: transparent;
  color: var(--text-muted-foreground);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition:
    background-color 0.2s ease,
    border-color 0.2s ease,
    color 0.2s ease;

  &:hover {
    color: var(--color-primary);
    background: hsl(var(--accent));
    border-color: hsl(var(--border));
  }
}

.notification-panel {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.notification-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.notification-title {
  color: var(--text-foreground);
  font-size: 15px;
  font-weight: 700;
  line-height: 1.4;
}

.notification-subtitle {
  color: var(--text-muted-foreground);
  font-size: 12px;
  line-height: 1.4;
}

.notification-list {
  min-height: 120px;
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.notification-item {
  width: 100%;
  min-height: 74px;
  border: 1px solid hsl(var(--border));
  border-radius: 8px;
  padding: 10px 10px 10px 12px;
  display: grid;
  grid-template-columns: 8px minmax(0, 1fr) 18px;
  gap: 10px;
  align-items: flex-start;
  background: hsl(var(--card));
  color: var(--text-foreground);
  text-align: left;
  cursor: pointer;

  &:hover {
    border-color: hsl(var(--primary) / 0.28);
    background: hsl(var(--accent));
  }

  &.unread {
    background: hsl(var(--primary) / 0.06);
  }
}

.notification-dot {
  width: 7px;
  height: 7px;
  margin-top: 7px;
  border-radius: 999px;
  background: transparent;
}

.notification-item.unread .notification-dot {
  background: hsl(var(--primary));
}

.notification-body {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.notification-item-title {
  overflow: hidden;
  color: var(--text-foreground);
  font-size: 14px;
  font-weight: 600;
  line-height: 1.4;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.notification-content {
  display: -webkit-box;
  overflow: hidden;
  color: var(--text-muted-foreground);
  font-size: 12px;
  line-height: 1.45;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.notification-time {
  color: var(--text-muted-foreground);
  font-size: 11px;
  line-height: 1.3;
}

.notification-read-icon {
  margin-top: 3px;
  color: var(--text-muted-foreground);
}
</style>

<script setup lang="ts">
import { computed, onMounted, reactive, shallowRef } from 'vue'
import { useRouter } from 'vue-router'
import { Check, Refresh, View } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import {
  MESSAGE_READ_STATUS_READ,
  MESSAGE_READ_STATUS_UNREAD,
  getMessagePage,
  getUnreadMessageCount,
  markAllMessagesRead,
  markMessageRead,
} from '@/api/system/message'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import type { MessageReadStatus, SysMessageQuery, SysMessageVo } from '@/types/system'
import { normalizePageTotal } from '@/utils/pagination'

defineOptions({ name: 'SystemMessageCenter' })

type MessageTab = 'all' | MessageReadStatus

const router = useRouter()
const loading = shallowRef(false)
const allReadLoading = shallowRef(false)
const total = shallowRef(0)
const unreadCount = shallowRef(0)
const activeTab = shallowRef<MessageTab>('all')
const tableData = shallowRef<SysMessageVo[]>([])

const queryParams = reactive<SysMessageQuery>({
  page: 1,
  size: 20,
})

const hasUnread = computed(() => unreadCount.value > 0)

function statusTagType(readStatus: MessageReadStatus) {
  return readStatus === MESSAGE_READ_STATUS_UNREAD ? 'warning' : 'info'
}

function levelTagType(level: string) {
  return level === 'WARNING' ? 'warning' : 'info'
}

function levelLabel(level: string) {
  return level === 'WARNING' ? '警告' : '普通'
}

function statusLabel(readStatus: MessageReadStatus) {
  return readStatus === MESSAGE_READ_STATUS_UNREAD ? '未读' : '已读'
}

async function refreshUnreadCount() {
  const res = await getUnreadMessageCount()
  unreadCount.value = Number(res.data || 0)
}

async function handleQuery() {
  loading.value = true
  try {
    const res = await getMessagePage(queryParams)
    tableData.value = res.data.records || []
    total.value = normalizePageTotal(res.data.total)
    await refreshUnreadCount()
  } finally {
    loading.value = false
  }
}

function handleTabChange() {
  queryParams.page = 1
  if (activeTab.value === 'all') {
    delete queryParams.readStatus
  } else {
    queryParams.readStatus = activeTab.value
  }
  handleQuery()
}

async function handleRead(row: SysMessageVo) {
  if (row.readStatus === MESSAGE_READ_STATUS_UNREAD) {
    await markMessageRead(row.id)
    ElMessage.success('已标记为已读')
  }
  if (row.targetUrl) {
    router.push(row.targetUrl)
    return
  }
  await handleQuery()
}

async function handleReadAll() {
  allReadLoading.value = true
  try {
    await markAllMessagesRead()
    ElMessage.success('全部消息已标记为已读')
    await handleQuery()
  } finally {
    allReadLoading.value = false
  }
}

onMounted(() => {
  handleQuery()
})
</script>

<template>
  <div class="app-container list-page message-center-page">
    <div class="message-toolbar">
      <el-tabs v-model="activeTab" class="message-tabs" @tab-change="handleTabChange">
        <el-tab-pane label="全部消息" name="all" />
        <el-tab-pane label="未读消息" :name="MESSAGE_READ_STATUS_UNREAD" />
        <el-tab-pane label="已读消息" :name="MESSAGE_READ_STATUS_READ" />
      </el-tabs>

      <div class="message-actions">
        <el-button :icon="Refresh" @click="handleQuery">刷新</el-button>
        <el-button
          type="primary"
          :icon="Check"
          :loading="allReadLoading"
          :disabled="!hasUnread"
          @click="handleReadAll"
        >
          全部已读
        </el-button>
      </div>
    </div>

    <div class="table-section">
      <el-table v-loading="loading" :data="tableData" border height="100%" row-key="id">
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.readStatus)" effect="light">{{ statusLabel(row.readStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="160" show-overflow-tooltip />
        <el-table-column prop="content" label="内容" min-width="260" show-overflow-tooltip />
        <el-table-column label="级别" width="90">
          <template #default="{ row }">
            <el-tag :type="levelTagType(row.messageLevel)" effect="plain">{{ levelLabel(row.messageLevel) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="messageType" label="类型" min-width="140" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" min-width="170" />
        <el-table-column prop="readTime" label="读取时间" min-width="170" />
        <el-table-column label="操作" class-name="table-action-column" fixed="right" min-width="180">
          <template #default="{ row }">
            <TableActionGroup
              :actions="[
                {
                  label: row.readStatus === MESSAGE_READ_STATUS_UNREAD ? '已读' : '查看',
                  type: 'primary',
                  icon: row.readStatus === MESSAGE_READ_STATUS_UNREAD ? Check : View,
                  disabled: row.readStatus !== MESSAGE_READ_STATUS_UNREAD && !row.targetUrl,
                  onClick: () => handleRead(row),
                },
              ]"
            />
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          class="pagination"
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.app-container.list-page {
  padding: 20px;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.message-center-page {
  gap: 16px;
}

.message-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.message-tabs {
  flex: 1;
  min-width: 0;

  :deep(.el-tabs__header) {
    margin-bottom: 0;
  }
}

.message-actions {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-shrink: 0;
}

.table-section {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.pagination-container {
  margin-top: 16px;
  flex-shrink: 0;
}

.pagination {
  justify-content: flex-end;
}

@media (max-width: 768px) {
  .message-toolbar {
    align-items: stretch;
    flex-direction: column;
  }

  .message-actions {
    justify-content: flex-end;
  }
}
</style>

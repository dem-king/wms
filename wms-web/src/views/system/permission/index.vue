<template>
  <div class="app-container list-page">
    <el-form :model="queryParams" :inline="true" class="search-form">
      <el-form-item label="权限名称">
        <el-input v-model="queryParams.permName" placeholder="请输入权限名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="权限编码">
        <el-input v-model="queryParams.permCode" placeholder="请输入权限编码" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-section">
      <el-table v-loading="loading" :data="tableData" border height="100%">
        <el-table-column prop="permName" label="权限名称" min-width="120" />
        <el-table-column prop="permCode" label="权限编码" min-width="150" />
        <el-table-column prop="permType" label="权限类型" min-width="100">
          <template #default="{ row }">
            <el-tag :type="getPermTypeTagType(row.permType)">{{ getPermTypeLabel(row.permType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="menuName" label="关联菜单" min-width="120" />
        <el-table-column prop="status" label="状态" min-width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusTagType(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" class-name="table-action-column" fixed="right" min-width="180">
          <template #default="{ row }">
            <TableActionGroup
              :actions="[
                { label: '查看详情', type: 'primary', icon: View, onClick: () => handleViewDetail(row) },
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

    <el-dialog v-model="detailVisible" title="权限详情" width="560px" @close="handleDetailClose">
      <el-descriptions v-if="currentPermission" :column="1" border>
        <el-descriptions-item label="权限名称">{{ currentPermission.permName }}</el-descriptions-item>
        <el-descriptions-item label="权限编码">{{ currentPermission.permCode }}</el-descriptions-item>
        <el-descriptions-item label="权限类型">
          <el-tag :type="getPermTypeTagType(currentPermission.permType)">
            {{ getPermTypeLabel(currentPermission.permType) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="关联菜单">{{ currentPermission.menuName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="菜单ID">{{ currentPermission.menuId ?? '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusTagType(currentPermission.status)">
            {{ getStatusLabel(currentPermission.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ currentPermission.createTime || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button type="primary" @click="handleDetailClose">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh, View } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import { getPermissionList } from '@/api/system/permission'
import type { SysPermissionVo } from '@/types/system'
import { normalizePageTotal } from '@/utils/pagination'

const loading = ref(false)
const tableData = ref<SysPermissionVo[]>([])
const total = ref(0)
const detailVisible = ref(false)
const currentPermission = ref<SysPermissionVo | null>(null)

const queryParams = reactive({
  page: 1,
  size: 20,
  permName: '',
  permCode: '',
})

const permTypeMap: Record<number, { label: string; tagType: 'success' | 'warning' | 'info' }> = {
  1: { label: '菜单', tagType: 'warning' },
  2: { label: '按钮', tagType: 'success' },
  3: { label: '数据', tagType: 'info' },
}

async function handleQuery() {
  loading.value = true
  try {
    const res = await getPermissionList(queryParams)
    tableData.value = res.data.records
    total.value = normalizePageTotal(res.data.total)
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryParams.permName = ''
  queryParams.permCode = ''
  queryParams.page = 1
  handleQuery()
}

function getPermTypeLabel(permType: number) {
  return permTypeMap[permType]?.label || '未知'
}

function getPermTypeTagType(permType: number) {
  return permTypeMap[permType]?.tagType || 'info'
}

function getStatusLabel(status: number) {
  return status === 1 ? '启用' : '禁用'
}

function getStatusTagType(status: number) {
  return status === 1 ? 'success' : 'danger'
}

function handleViewDetail(row: SysPermissionVo) {
  currentPermission.value = row
  detailVisible.value = true
}

function handleDetailClose() {
  detailVisible.value = false
  currentPermission.value = null
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped lang="scss">
.app-container.list-page {
  padding: 20px;
  display: flex;
  flex-direction: column;
  min-height: 0;
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

.search-form {
  margin-bottom: 16px;
}

.pagination {
  justify-content: flex-end;
}
</style>

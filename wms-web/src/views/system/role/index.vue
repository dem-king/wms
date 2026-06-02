<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Menu, Lock } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import { getRoleList, deleteRole } from '@/api/system/role'
import { useUserStore } from '@/store/modules/user'
import type { EntityId, SysRoleVo } from '@/types/system'
import { normalizePageTotal } from '@/utils/pagination'
import RoleForm from './components/RoleForm.vue'
import RoleMenu from './components/RoleMenu.vue'
import RolePermission from './components/RolePermission.vue'

const dataScopeMap: Record<number, string> = {
  1: '全部数据',
  2: '自定义数据',
  3: '本部门数据',
  4: '本部门及以下数据',
  5: '仅本人数据'
}

const loading = ref(false)
const userStore = useUserStore()
const tableData = ref<SysRoleVo[]>([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 20,
  roleName: '',
  roleCode: '',
  status: undefined as number | undefined
})

const formVisible = ref(false)
const menuVisible = ref(false)
const permissionVisible = ref(false)
const isEdit = ref(false)
const currentRow = ref<SysRoleVo | null>(null)

async function handleQuery() {
  loading.value = true
  try {
    const res = await getRoleList(queryParams)
    const roles = Array.isArray(res.data) ? res.data : res.data.records
    tableData.value = roles
    total.value = Array.isArray(res.data) ? roles.length : normalizePageTotal(res.data.total)
  } finally {
    loading.value = false
  }
}

function handleReset() {
  Object.assign(queryParams, { page: 1, roleName: '', roleCode: '', status: undefined })
  handleQuery()
}

function handleAdd() {
  isEdit.value = false
  currentRow.value = null
  formVisible.value = true
}

function handleEdit(row: SysRoleVo) {
  isEdit.value = true
  currentRow.value = row
  formVisible.value = true
}

function handleAssignMenu(row: SysRoleVo) {
  currentRow.value = row
  menuVisible.value = true
}

function handleAssignPermission(row: SysRoleVo) {
  currentRow.value = row
  permissionVisible.value = true
}

async function handleDelete(id: EntityId) {
  await deleteRole(id)
  ElMessage.success('删除成功')
  await handleQuery()
}

onMounted(handleQuery)
</script>

<template>
  <div class="app-container list-page">
    <el-form :model="queryParams" :inline="true" class="search-form">
      <el-form-item label="角色名称">
        <el-input v-model="queryParams.roleName" placeholder="请输入角色名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="角色编码">
        <el-input v-model="queryParams.roleCode" placeholder="请输入角色编码" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button v-if="userStore.hasPermission('system:role:add')" type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
    </el-row>

    <div class="table-section">
      <el-table v-loading="loading" :data="tableData" border height="100%">
        <el-table-column prop="roleName" label="角色名称" min-width="120" />
        <el-table-column prop="roleCode" label="角色编码" min-width="120" />
        <el-table-column prop="roleDesc" label="角色描述" min-width="150" show-overflow-tooltip />
        <el-table-column prop="dataScope" label="数据范围" min-width="160">
          <template #default="{ row }">
            {{ dataScopeMap[row.dataScope] || '未知' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" min-width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" class-name="table-action-column" fixed="right" min-width="240">
          <template #default="{ row }">
            <TableActionGroup
              :actions="[
                { label: '编辑', type: 'primary', icon: Edit, permission: 'system:role:edit', onClick: () => handleEdit(row) },
                { label: '菜单', type: 'success', icon: Menu, permission: 'system:role:edit', onClick: () => handleAssignMenu(row) },
                { label: '权限', type: 'warning', icon: Lock, permission: 'system:role:edit', onClick: () => handleAssignPermission(row) },
                { label: '删除', type: 'danger', icon: Delete, permission: 'system:role:delete', confirmText: '确认删除该角色？', onClick: () => handleDelete(row.id) }
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

    <RoleForm v-model:visible="formVisible" :is-edit="isEdit" :form-data="currentRow" @success="handleQuery" />
    <RoleMenu v-model:visible="menuVisible" :role-id="currentRow?.id" :role-name="currentRow?.roleName" @success="handleQuery" />
    <RolePermission
      v-model:visible="permissionVisible"
      :role-id="currentRow?.id"
      :role-name="currentRow?.roleName"
      @success="handleQuery"
    />
  </div>
</template>

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

.mb8 {
  margin-bottom: 8px;
}

.pagination {
  justify-content: flex-end;
}
</style>

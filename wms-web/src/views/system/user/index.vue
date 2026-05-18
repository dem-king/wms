<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true" class="search-form">
      <el-form-item label="用户名">
        <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable @keyup.enter="handleQuery" />
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
        <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="tableData" border>
      <el-table-column prop="username" label="用户名" min-width="100" />
      <el-table-column prop="realName" label="真实姓名" min-width="100" />
      <el-table-column prop="deptName" label="部门" min-width="100" />
      <el-table-column prop="phone" label="手机号" min-width="120" />
      <el-table-column prop="email" label="邮箱" min-width="150" />
      <el-table-column prop="status" label="状态" min-width="80">
        <template #default="{ row }">
          <el-switch :model-value="row.status === 1" @change="val => handleStatusChange(row, Boolean(val))" />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="160" />
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" text :icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-button type="warning" text :icon="Key" @click="handleResetPwd(row)">重置密码</el-button>
          <el-popconfirm title="确定删除该用户吗？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button type="danger" text :icon="Delete">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

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

    <UserForm v-model:visible="formVisible" :is-edit="isEdit" :form-data="currentRow" @success="handleQuery" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, Key } from '@element-plus/icons-vue'
import { getUserList, deleteUser, resetUserPwd, changeUserStatus } from '@/api/system/user'
import type { SysUserVo } from '@/types/system'
import UserForm from './components/UserForm.vue'

const loading = ref(false)
const tableData = ref<SysUserVo[]>([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 20,
  username: '',
  phone: '',
  status: undefined as number | undefined,
})

const formVisible = ref(false)
const isEdit = ref(false)
const currentRow = ref<SysUserVo | null>(null)

async function handleQuery() {
  loading.value = true
  try {
    const res = await getUserList(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryParams.username = ''
  queryParams.phone = ''
  queryParams.status = undefined
  queryParams.page = 1
  handleQuery()
}

function handleAdd() {
  isEdit.value = false
  currentRow.value = null
  formVisible.value = true
}

function handleEdit(row: SysUserVo) {
  isEdit.value = true
  currentRow.value = { ...row }
  formVisible.value = true
}

async function handleDelete(id: number) {
  await deleteUser(id)
  ElMessage.success('删除成功')
  handleQuery()
}

async function handleResetPwd(row: SysUserVo) {
  await ElMessageBox.confirm(`确定重置用户"${row.username}"的密码吗？`, '提示', { type: 'warning' })
  await resetUserPwd(row.id)
  ElMessage.success('密码重置成功')
}

async function handleStatusChange(row: SysUserVo, val: boolean) {
  const newStatus = val ? 1 : 0
  await changeUserStatus(row.id, newStatus)
  ElMessage.success('状态修改成功')
  handleQuery()
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped lang="scss">
.app-container {
  padding: 20px;
}

.search-form {
  margin-bottom: 16px;
}

.mb8 {
  margin-bottom: 8px;
}

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
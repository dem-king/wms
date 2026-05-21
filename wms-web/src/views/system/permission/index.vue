<template>
  <div class="app-container">
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

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="tableData" border>
      <el-table-column prop="permName" label="权限名称" min-width="120" />
      <el-table-column prop="permCode" label="权限编码" min-width="150" />
      <el-table-column prop="permType" label="权限类型" min-width="100">
        <template #default="{ row }">
          <el-tag v-if="row.permType === 1" type="warning">菜单</el-tag>
          <el-tag v-else-if="row.permType === 2" type="success">按钮</el-tag>
          <el-tag v-else type="info">数据</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="menuName" label="关联菜单" min-width="120" />
      <el-table-column prop="status" label="状态" min-width="80">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" class-name="table-action-column" fixed="right">
        <template #default="{ row }">
          <TableActionGroup
            :actions="[
              { label: '编辑', type: 'primary', icon: Edit, onClick: () => handleEdit(row) },
              { label: '删除', type: 'danger', icon: Delete, confirmText: '确定删除该权限吗？', onClick: () => handleDelete(row.id) },
            ]"
          />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑权限' : '新增权限'" width="500px" @close="handleClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="权限名称" prop="permName">
          <el-input v-model="form.permName" placeholder="请输入权限名称" />
        </el-form-item>
        <el-form-item label="权限编码" prop="permCode">
          <el-input v-model="form.permCode" placeholder="请输入权限编码" />
        </el-form-item>
        <el-form-item label="权限类型" prop="permType">
          <el-select v-model="form.permType" placeholder="请选择权限类型">
            <el-option label="菜单" :value="1" />
            <el-option label="按钮" :value="2" />
            <el-option label="数据" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联菜单" prop="menuId">
          <el-tree-select
            v-model="form.menuId"
            :data="menuTreeForSelect"
            :props="{ label: 'menuName', children: 'children' }"
            value-key="id"
            placeholder="请选择关联菜单"
            check-strictly
            default-expand-all
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleClose">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import { getPermissionList, addPermission, updatePermission, deletePermission } from '@/api/system/permission'
import { getMenuTree } from '@/api/system/menu'
import type { SysPermissionVo } from '@/types/system'
import type { MenuTreeNode } from '@/types/auth'

const loading = ref(false)
const tableData = ref<SysPermissionVo[]>([])
const total = ref(0)
const menuTreeForSelect = ref<MenuTreeNode[]>([])

const queryParams = reactive({
  page: 1,
  size: 20,
  permName: '',
  permCode: '',
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const form = reactive({
  id: undefined as number | undefined,
  permName: '',
  permCode: '',
  permType: 2,
  menuId: undefined as number | undefined,
  status: 1,
})

const rules: FormRules = {
  permName: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  permCode: [{ required: true, message: '请输入权限编码', trigger: 'blur' }],
  permType: [{ required: true, message: '请选择权限类型', trigger: 'change' }],
  menuId: [{ required: true, message: '请选择关联菜单', trigger: 'change' }],
}

async function handleQuery() {
  loading.value = true
  try {
    const res = await getPermissionList(queryParams)
    const permissions = Array.isArray(res.data) ? res.data : res.data.records
    tableData.value = permissions
    total.value = Array.isArray(res.data) ? permissions.length : res.data.total
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

function handleAdd() {
  isEdit.value = false
  dialogVisible.value = true
}

function handleEdit(row: SysPermissionVo) {
  isEdit.value = true
  Object.assign(form, { id: row.id, permName: row.permName, permCode: row.permCode, permType: row.permType, menuId: row.menuId, status: row.status })
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (isEdit.value && form.id) {
      await updatePermission(form.id, { permName: form.permName, permCode: form.permCode, permType: form.permType, menuId: form.menuId!, status: form.status })
      ElMessage.success('编辑成功')
    } else {
      await addPermission({ permName: form.permName, permCode: form.permCode, permType: form.permType, menuId: form.menuId!, status: form.status })
      ElMessage.success('新增成功')
    }
    handleClose()
    handleQuery()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(id: number) {
  await deletePermission(id)
  ElMessage.success('删除成功')
  handleQuery()
}

function handleClose() {
  dialogVisible.value = false
  formRef.value?.resetFields()
  Object.assign(form, { id: undefined, permName: '', permCode: '', permType: 2, menuId: undefined, status: 1 })
}

async function loadMenuTree() {
  const res = await getMenuTree()
  menuTreeForSelect.value = res.data
}

onMounted(() => {
  handleQuery()
  loadMenuTree()
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

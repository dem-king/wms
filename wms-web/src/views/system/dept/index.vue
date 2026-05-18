<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd()">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button :icon="Sort" plain @click="toggleExpand">展开/折叠</el-button>
      </el-col>
    </el-row>

    <el-table
      v-loading="loading"
      :data="deptTree"
      row-key="id"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      :default-expand-all="isExpandAll"
      border
    >
      <el-table-column prop="deptName" label="部门名称" min-width="180" />
      <el-table-column prop="deptCode" label="部门编码" min-width="120" />
      <el-table-column prop="leader" label="负责人" min-width="100" />
      <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
      <el-table-column prop="status" label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="220" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" text :icon="Edit" @click="handleEdit(row)">编辑</el-button>
          <el-button type="success" text :icon="Plus" @click="handleAdd(row.id)">新增</el-button>
          <el-popconfirm title="确定删除该部门吗？" @confirm="handleDelete(row.id)">
            <template #reference>
              <el-button type="danger" text :icon="Delete">删除</el-button>
            </template>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑部门' : '新增部门'" width="500px" @close="handleClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级部门">
          <el-tree-select
            v-model="form.parentId"
            :data="deptTreeForSelect"
            :props="{ label: 'deptName', children: 'children' }"
            value-key="id"
            placeholder="请选择上级部门"
            check-strictly
            default-expand-all
          />
        </el-form-item>
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="form.deptName" placeholder="请输入部门名称" />
        </el-form-item>
        <el-form-item label="部门编码" prop="deptCode">
          <el-input v-model="form.deptCode" placeholder="请输入部门编码" />
        </el-form-item>
        <el-form-item label="负责人" prop="leader">
          <el-input v-model="form.leader" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="排序号" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
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
import { Plus, Edit, Delete, Sort } from '@element-plus/icons-vue'
import { getDeptList, getDeptTree, addDept, updateDept, deleteDept } from '@/api/system/dept'
import type { SysDeptVo } from '@/types/system'

const loading = ref(false)
const deptTree = ref<SysDeptVo[]>([])
const deptTreeForSelect = ref<SysDeptVo[]>([])
const isExpandAll = ref(true)

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const form = reactive({
  id: undefined as number | undefined,
  parentId: 0,
  deptName: '',
  deptCode: '',
  leader: '',
  sortOrder: 0,
  status: 1,
})

const rules: FormRules = {
  deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
  deptCode: [{ required: true, message: '请输入部门编码', trigger: 'blur' }],
}

async function loadDeptTree() {
  loading.value = true
  try {
    const [listRes, treeRes] = await Promise.all([getDeptList(), getDeptTree()])
    deptTree.value = listRes.data
    deptTreeForSelect.value = [{ id: 0, deptName: '根部门', deptCode: '', parentId: 0, leader: '', sortOrder: 0, status: 1, createTime: '', children: treeRes.data || [] } as SysDeptVo]
  } finally {
    loading.value = false
  }
}

function toggleExpand() {
  isExpandAll.value = !isExpandAll.value
  loadDeptTree()
}

function handleAdd(parentId?: number) {
  isEdit.value = false
  form.parentId = parentId ?? 0
  dialogVisible.value = true
}

function handleEdit(row: SysDeptVo) {
  isEdit.value = true
  Object.assign(form, { id: row.id, parentId: row.parentId, deptName: row.deptName, deptCode: row.deptCode, leader: row.leader, sortOrder: row.sortOrder, status: row.status })
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (isEdit.value && form.id) {
      await updateDept(form.id, { ...form })
      ElMessage.success('编辑成功')
    } else {
      await addDept({ ...form })
      ElMessage.success('新增成功')
    }
    handleClose()
    loadDeptTree()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(id: number) {
  await deleteDept(id)
  ElMessage.success('删除成功')
  loadDeptTree()
}

function handleClose() {
  dialogVisible.value = false
  formRef.value?.resetFields()
  Object.assign(form, { id: undefined, parentId: 0, deptName: '', deptCode: '', leader: '', sortOrder: 0, status: 1 })
}

onMounted(() => {
  loadDeptTree()
})
</script>

<style scoped lang="scss">
.app-container {
  padding: 20px;
}

.mb8 {
  margin-bottom: 8px;
}
</style>
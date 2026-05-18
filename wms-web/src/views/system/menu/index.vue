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
      :data="menuTree"
      row-key="id"
      :tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
      :default-expand-all="isExpandAll"
      border
    >
      <el-table-column prop="menuName" label="菜单名称" min-width="180" />
      <el-table-column prop="icon" label="图标" width="80" align="center">
        <template #default="{ row }">
          <el-icon v-if="row.icon"><component :is="row.icon" /></el-icon>
        </template>
      </el-table-column>
      <el-table-column prop="menuType" label="菜单类型" width="100" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.menuType === 1" type="warning">目录</el-tag>
          <el-tag v-else-if="row.menuType === 2" type="success">菜单</el-tag>
          <el-tag v-else type="info">按钮</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="path" label="路由路径" min-width="150" />
      <el-table-column prop="component" label="组件路径" min-width="150" />
      <el-table-column prop="sortOrder" label="排序" width="80" align="center" />
      <el-table-column prop="visible" label="状态" width="80" align="center">
        <template #default="{ row }">
          <el-tag :type="row.visible === 1 ? 'success' : 'danger'">{{ row.visible === 1 ? '显示' : '隐藏' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" class-name="table-action-column" fixed="right">
        <template #default="{ row }">
          <TableActionGroup
            :actions="[
              { label: '编辑', type: 'primary', icon: Edit, onClick: () => handleEdit(row) },
              { label: '新增', type: 'success', icon: Plus, visible: row.menuType !== 3, onClick: () => handleAdd(row.id) },
              { label: '删除', type: 'danger', icon: Delete, confirmText: '确定删除该菜单吗？', onClick: () => handleDelete(row.id) },
            ]"
          />
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑菜单' : '新增菜单'" width="600px" @close="handleClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="form.parentId"
            :data="menuTreeForSelect"
            :props="{ label: 'menuName', children: 'children' }"
            value-key="id"
            placeholder="请选择上级菜单"
            check-strictly
            default-expand-all
          />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="form.menuType">
            <el-radio :value="1">目录</el-radio>
            <el-radio :value="2">菜单</el-radio>
            <el-radio :value="3">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item v-if="form.menuType !== 3" label="图标" prop="icon">
          <el-input v-model="form.icon" placeholder="请输入图标名称" />
        </el-form-item>
        <el-form-item v-if="form.menuType !== 3" label="路由路径" prop="path">
          <el-input v-model="form.path" placeholder="请输入路由路径" />
        </el-form-item>
        <el-form-item v-if="form.menuType === 2" label="组件路径" prop="component">
          <el-input v-model="form.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item v-if="form.menuType === 3" label="权限编码" prop="permCode">
          <el-input v-model="form.permCode" placeholder="请输入权限编码" />
        </el-form-item>
        <el-form-item label="排序号" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item v-if="form.menuType !== 3" label="是否可见" prop="visible">
          <el-radio-group v-model="form.visible">
            <el-radio :value="1">显示</el-radio>
            <el-radio :value="0">隐藏</el-radio>
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
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import { getMenuList, addMenu, updateMenu, deleteMenu, getMenuTree } from '@/api/system/menu'
import type { MenuTreeNode } from '@/types/auth'

const loading = ref(false)
const menuTree = ref<MenuTreeNode[]>([])
const menuTreeForSelect = ref<MenuTreeNode[]>([])
const isExpandAll = ref(true)

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const form = reactive({
  id: undefined as number | undefined,
  parentId: 0,
  menuType: 1,
  menuName: '',
  icon: '',
  path: '',
  component: '',
  permCode: '',
  sortOrder: 0,
  visible: 1,
})

const rules: FormRules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }],
}

async function loadMenuTree() {
  loading.value = true
  try {
    const res = await getMenuList()
    menuTree.value = res.data
    const treeRes = await getMenuTree()
    menuTreeForSelect.value = [{ id: 0, menuName: '根目录', menuCode: '', parentId: 0, menuType: 1, path: '', component: '', redirect: '', icon: '', isExternal: 0, isCache: 0, visible: 1, sortOrder: 0, permCode: '', children: treeRes.data || [] } as MenuTreeNode]
  } finally {
    loading.value = false
  }
}

function toggleExpand() {
  isExpandAll.value = !isExpandAll.value
  loadMenuTree()
}

function handleAdd(parentId?: number) {
  isEdit.value = false
  form.parentId = parentId ?? 0
  form.menuType = parentId ? 2 : 1
  dialogVisible.value = true
}

function handleEdit(row: MenuTreeNode) {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    parentId: row.parentId,
    menuType: row.menuType,
    menuName: row.menuName,
    icon: row.icon,
    path: row.path,
    component: row.component,
    permCode: row.permCode,
    sortOrder: row.sortOrder,
    visible: row.visible,
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (isEdit.value && form.id) {
      await updateMenu(form.id, { ...form })
      ElMessage.success('编辑成功')
    } else {
      await addMenu({ ...form })
      ElMessage.success('新增成功')
    }
    handleClose()
    loadMenuTree()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(id: number) {
  await deleteMenu(id)
  ElMessage.success('删除成功')
  loadMenuTree()
}

function handleClose() {
  dialogVisible.value = false
  formRef.value?.resetFields()
  Object.assign(form, { id: undefined, parentId: 0, menuType: 1, menuName: '', icon: '', path: '', component: '', permCode: '', sortOrder: 0, visible: 1 })
}

onMounted(() => {
  loadMenuTree()
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
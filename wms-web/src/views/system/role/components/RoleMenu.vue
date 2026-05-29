<script setup lang="ts">
import { nextTick, ref, watch } from 'vue'
import type { ElTree } from 'element-plus'
import { ElMessage } from 'element-plus'
import { getRoleMenus, assignRoleMenus, getMenuTreeForRole } from '@/api/system/role'
import type { EntityId, MenuTreeNode } from '@/types/auth'

const props = defineProps<{
  visible: boolean
  roleId: EntityId | undefined
  roleName: string | undefined
}>()

const emit = defineEmits<{
  'update:visible': [val: boolean]
  success: []
}>()

const dialogVisible = ref(false)
const treeRef = ref<InstanceType<typeof ElTree>>()
const submitLoading = ref(false)
const menuTree = ref<MenuTreeNode[]>([])

watch(() => props.visible, async (val) => {
  dialogVisible.value = val
  if (val && props.roleId) {
    const [treeRes, menuRes] = await Promise.all([getMenuTreeForRole(), getRoleMenus(props.roleId)])
    menuTree.value = treeRes.data
    await nextTick()
    treeRef.value?.setCheckedKeys(menuRes.data)
  }
})

watch(dialogVisible, (val) => { emit('update:visible', val) })

async function handleSubmit() {
  if (!props.roleId) {
    return
  }
  submitLoading.value = true
  try {
    const menuIds = getSelectedMenuIds()
    await assignRoleMenus(props.roleId, menuIds)
    ElMessage.success('菜单分配成功')
    emit('success')
    handleClose()
  } finally {
    submitLoading.value = false
  }
}

function getSelectedMenuIds() {
  const checkedKeys = (treeRef.value?.getCheckedKeys(false) ?? []) as EntityId[]
  const halfCheckedKeys = (treeRef.value?.getHalfCheckedKeys() ?? []) as EntityId[]
  return Array.from(new Set([...checkedKeys, ...halfCheckedKeys]))
}

function handleClose() {
  dialogVisible.value = false
  menuTree.value = []
}
</script>

<template>
  <el-dialog v-model="dialogVisible" :title="`分配菜单 - ${roleName}`" width="500px" @close="handleClose">
    <el-tree
      ref="treeRef"
      :data="menuTree"
      :props="{ label: 'menuName', children: 'children' }"
      node-key="id"
      show-checkbox
      check-on-click-node
      default-expand-all
    />
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

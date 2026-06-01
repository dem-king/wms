<script setup lang="ts">
import { computed, nextTick, ref, watch } from 'vue'
import type { ElTree } from 'element-plus'
import { ElMessage } from 'element-plus'
import { assignRolePermissions, getRolePermissions } from '@/api/system/role'
import { getPermissionList } from '@/api/system/permission'
import type { EntityId, SysPermissionVo } from '@/types/system'

interface PermissionTreeNode {
  id: EntityId
  label: string
  children?: PermissionTreeNode[]
}

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
const permissionTree = ref<PermissionTreeNode[]>([])
const permissions = ref<SysPermissionVo[]>([])

const permissionIdSet = computed(() => new Set(permissions.value.map((permission) => String(permission.id))))

watch(() => props.visible, async (val) => {
  dialogVisible.value = val
  if (val && props.roleId) {
    const [permissionRes, rolePermissionRes] = await Promise.all([
      getPermissionList({ page: 1, size: 100 }),
      getRolePermissions(props.roleId),
    ])
    permissions.value = permissionRes.data.records
    permissionTree.value = buildPermissionTree(permissionRes.data.records)
    await nextTick()
    treeRef.value?.setCheckedKeys(rolePermissionRes.data)
  }
})

watch(dialogVisible, (val) => { emit('update:visible', val) })

async function handleSubmit() {
  if (!props.roleId) {
    return
  }
  submitLoading.value = true
  try {
    await assignRolePermissions(props.roleId, getSelectedPermissionIds())
    ElMessage.success('权限分配成功')
    emit('success')
    handleClose()
  } finally {
    submitLoading.value = false
  }
}

function buildPermissionTree(records: SysPermissionVo[]) {
  const grouped = new Map<string, PermissionTreeNode[]>()
  for (const permission of records) {
    const groupName = permission.menuName || '未分组'
    const children = grouped.get(groupName) ?? []
    children.push({
      id: permission.id,
      label: `${permission.permName} (${permission.permCode})`,
    })
    grouped.set(groupName, children)
  }
  return Array.from(grouped.entries()).map(([menuName, children]) => ({
    id: `menu-${menuName}`,
    label: menuName,
    children,
  }))
}

function getSelectedPermissionIds() {
  const checkedKeys = (treeRef.value?.getCheckedKeys(false) ?? []) as EntityId[]
  return checkedKeys.filter((key) => permissionIdSet.value.has(String(key)))
}

function handleClose() {
  dialogVisible.value = false
  permissionTree.value = []
  permissions.value = []
}
</script>

<template>
  <el-dialog v-model="dialogVisible" :title="`分配权限 - ${roleName}`" width="560px" @close="handleClose">
    <div class="permission-tree-wrap">
      <el-tree
        ref="treeRef"
        :data="permissionTree"
        :props="{ label: 'label', children: 'children' }"
        node-key="id"
        show-checkbox
        check-on-click-node
        default-expand-all
      />
    </div>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.permission-tree-wrap {
  max-height: 460px;
  overflow: auto;
}
</style>

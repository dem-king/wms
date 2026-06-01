<script setup lang="ts">
import { computed } from 'vue'
import { ElMessageBox } from 'element-plus'
import { useUserStore } from '@/store/modules/user'
import type { TableActionItem } from './types'

const props = defineProps<{
  actions: TableActionItem[]
}>()

const userStore = useUserStore()

const visibleActions = computed(() =>
  props.actions.filter((action) => action.visible !== false && hasActionPermission(action)),
)

function hasActionPermission(action: TableActionItem) {
  if (!action.permission) {
    return true
  }
  const permissions = Array.isArray(action.permission) ? action.permission : [action.permission]
  return permissions.some((permission) => userStore.hasPermission(permission))
}

function getActionKey(action: TableActionItem, index: number) {
  return action.key ?? `${action.label}-${index}`
}

async function handleAction(action: TableActionItem) {
  if (action.disabled) {
    return
  }

  if (action.confirmText) {
    await ElMessageBox.confirm(
      action.confirmText,
      action.confirmTitle ?? '提示',
      {
        type: action.confirmType ?? 'warning',
        autofocus: false,
      },
    )
  }

  await action.onClick()
}
</script>

<template>
  <div class="table-action-group">
    <el-button
      v-for="(action, index) in visibleActions"
      :key="getActionKey(action, index)"
      :type="action.type ?? 'primary'"
      :icon="action.icon"
      :disabled="action.disabled"
      text
      @click.stop="handleAction(action)"
    >
      {{ action.label }}
    </el-button>
  </div>
</template>

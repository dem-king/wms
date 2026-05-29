<script setup lang="ts">
import { computed } from 'vue'
import { ElMessageBox } from 'element-plus'
import type { TableActionItem } from './types'

const props = withDefaults(defineProps<{
  actions: TableActionItem[]
  maxInlineActions?: number
}>(), {
  maxInlineActions: 2,
})

const visibleActions = computed(() => props.actions.filter((action) => action.visible !== false))
const inlineActions = computed(() => visibleActions.value.slice(0, props.maxInlineActions))
const overflowActions = computed(() => visibleActions.value.slice(props.maxInlineActions))

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
      v-for="(action, index) in inlineActions"
      :key="getActionKey(action, index)"
      :type="action.type ?? 'primary'"
      :icon="action.icon"
      :disabled="action.disabled"
      text
      @click.stop="handleAction(action)"
    >
      {{ action.label }}
    </el-button>

    <el-dropdown v-if="overflowActions.length > 0" trigger="click" @command="handleAction">
      <el-button class="table-action-group__more" text type="primary" @click.stop>
        更多
      </el-button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item
            v-for="(action, index) in overflowActions"
            :key="getActionKey(action, index + maxInlineActions)"
            :command="action"
            :disabled="action.disabled"
          >
            <div class="table-action-group__dropdown-item">
              <el-icon v-if="action.icon" class="table-action-group__dropdown-icon">
                <component :is="action.icon" />
              </el-icon>
              <span>{{ action.label }}</span>
            </div>
          </el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

import { ArrowDown, Crop, FullScreen } from '@element-plus/icons-vue'

defineOptions({ name: 'TagsViewActions' })

const props = withDefaults(
  defineProps<{
    canMaximize?: boolean
  }>(),
  {
    canMaximize: true,
  },
)

const emit = defineEmits<{
  closeAll: []
  closeOthers: []
}>()

const isFullscreen = ref(false)

const fullscreenIcon = computed(() => (isFullscreen.value ? Crop : FullScreen))

async function toggleFullscreen() {
  if (!props.canMaximize) return

  if (document.fullscreenElement) {
    await document.exitFullscreen()
    isFullscreen.value = false
    return
  }

  await document.documentElement.requestFullscreen()
  isFullscreen.value = true
}
</script>

<template>
  <div class="tags-view-actions">
    <el-dropdown trigger="click">
      <button class="action-button" type="button">
        更多
        <el-icon><ArrowDown /></el-icon>
      </button>
      <template #dropdown>
        <el-dropdown-menu>
          <el-dropdown-item @click="emit('closeOthers')">关闭其他</el-dropdown-item>
          <el-dropdown-item @click="emit('closeAll')">关闭全部</el-dropdown-item>
        </el-dropdown-menu>
      </template>
    </el-dropdown>

    <button v-if="canMaximize" class="icon-button" type="button" @click="toggleFullscreen">
      <el-icon><component :is="fullscreenIcon" /></el-icon>
    </button>
  </div>
</template>

<style lang="scss" scoped>
.tags-view-actions {
  display: flex;
  align-items: center;
  gap: 8px;
}

.action-button,
.icon-button {
  border: 1px solid hsl(var(--border));
  background: var(--bg-card);
  color: var(--text-muted-foreground);
  border-radius: 12px;
  cursor: pointer;
  transition:
    color 0.2s ease,
    border-color 0.2s ease,
    background-color 0.2s ease,
    box-shadow 0.2s ease;

  &:hover {
    color: var(--color-primary);
    background: hsl(var(--accent));
    border-color: hsl(var(--primary) / 0.16);
    box-shadow: 0 8px 20px hsl(var(--primary) / 0.08);
  }
}

.action-button {
  height: 32px;
  padding: 0 12px;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  font-weight: 600;
}

.icon-button {
  width: 32px;
  height: 32px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
</style>

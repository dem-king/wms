<script setup lang="ts">
defineOptions({ name: 'HeaderActionButton' })

withDefaults(
  defineProps<{
    active?: boolean
    tooltip?: string
  }>(),
  {
    active: false,
    tooltip: '',
  },
)

defineEmits<{
  click: []
}>()
</script>

<template>
  <el-tooltip v-if="tooltip" :content="tooltip" placement="bottom">
    <button class="header-action-button" :class="{ active }" type="button" @click="$emit('click')">
      <slot />
    </button>
  </el-tooltip>

  <button v-else class="header-action-button" :class="{ active }" type="button" @click="$emit('click')">
    <slot />
  </button>
</template>

<style lang="scss" scoped>
.header-action-button {
  width: 38px;
  height: 38px;
  border: 1px solid transparent;
  border-radius: 12px;
  background: transparent;
  color: var(--text-muted-foreground);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition:
    background-color 0.2s ease,
    border-color 0.2s ease,
    color 0.2s ease,
    box-shadow 0.2s ease;

  &:hover {
    color: var(--color-primary);
    background: hsl(var(--accent));
    border-color: hsl(var(--border));
  }

  &.active {
    color: var(--color-primary);
    background: hsl(var(--primary) / 0.12);
    border-color: hsl(var(--primary) / 0.18);
    box-shadow: 0 8px 20px hsl(var(--primary) / 0.12);
  }
}
</style>

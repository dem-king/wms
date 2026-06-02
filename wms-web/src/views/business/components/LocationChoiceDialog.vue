<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import type { ItemLocationVo } from '@/types/item'

const props = defineProps<{
  visible: boolean
  locations: ItemLocationVo[]
}>()

const emit = defineEmits<{
  'update:visible': [visible: boolean]
  select: [location: ItemLocationVo]
}>()

const selectedBinId = ref<string | number>()

const dialogVisible = computed({
  get: () => props.visible,
  set: value => emit('update:visible', value),
})

watch(() => props.visible, (visible) => {
  if (visible) {
    selectedBinId.value = props.locations[0]?.binId
  }
})

function formatLocation(location: ItemLocationVo) {
  return location.locationText
    || [location.warehouseName, location.areaName, location.cabinetName, location.binCode].filter(Boolean).join(' / ')
    || String(location.binId)
}

function handleConfirm() {
  const location = props.locations.find(item => item.binId === selectedBinId.value)
  if (!location) {
    return
  }
  emit('select', location)
  emit('update:visible', false)
}
</script>

<template>
  <el-dialog v-model="dialogVisible" title="选择库位" width="560px">
    <el-radio-group v-model="selectedBinId" class="location-options">
      <el-radio
        v-for="location in locations"
        :key="location.binId"
        :label="location.binId"
        class="location-option"
      >
        {{ formatLocation(location) }}
      </el-radio>
    </el-radio-group>
    <template #footer>
      <el-button @click="dialogVisible = false">取消</el-button>
      <el-button type="primary" :disabled="!selectedBinId" @click="handleConfirm">确定</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.location-options {
  display: flex;
  flex-direction: column;
  align-items: stretch;
  gap: 8px;
}

.location-option {
  min-height: 36px;
  margin-right: 0;
  padding: 6px 8px;
  border: 1px solid var(--el-border-color);
  border-radius: 4px;
}
</style>

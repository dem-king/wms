<script setup lang="ts">
import { computed } from 'vue'
import type {
  WarehouseVisualAreaNode,
  WarehouseVisualBinNode,
  WarehouseVisualCabinetNode,
} from '../visual-layout'

const props = defineProps<{
  modelValue: boolean
  selectedArea: WarehouseVisualAreaNode | null
  selectedCabinet: WarehouseVisualCabinetNode | null
  selectedBins: WarehouseVisualBinNode[]
}>()

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
}>()

const enabledBinCount = computed(() => props.selectedBins.filter(bin => bin.status === 1).length)
const disabledBinCount = computed(() => props.selectedBins.filter(bin => bin.status === 0).length)
</script>

<template>
  <el-dialog
    :model-value="modelValue"
    width="640px"
    title="存放柜详情"
    @update:model-value="emit('update:modelValue', $event)"
  >
    <div v-if="selectedCabinet" class="detail-grid">
      <div class="detail-item">
        <div class="detail-label">所属区域</div>
        <div class="detail-value">{{ selectedArea?.areaName ?? '-' }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-label">区域编码</div>
        <div class="detail-value">{{ selectedArea?.areaCode ?? '-' }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-label">存放柜名称</div>
        <div class="detail-value">{{ selectedCabinet.cabinetName }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-label">存放柜编码</div>
        <div class="detail-value">{{ selectedCabinet.cabinetCode }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-label">网格规格</div>
        <div class="detail-value">{{ selectedCabinet.gridRowCount }} 行 × {{ selectedCabinet.gridColCount }} 列</div>
      </div>
      <div class="detail-item">
        <div class="detail-label">库位总数</div>
        <div class="detail-value">{{ selectedBins.length }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-label">启用库位</div>
        <div class="detail-value">{{ enabledBinCount }}</div>
      </div>
      <div class="detail-item">
        <div class="detail-label">禁用库位</div>
        <div class="detail-value">{{ disabledBinCount }}</div>
      </div>
    </div>

    <el-table
      v-if="selectedCabinet"
      :data="selectedBins"
      size="small"
      border
      style="margin-top: 16px"
    >
      <el-table-column prop="binCode" label="库位编码" min-width="140" />
      <el-table-column label="位置" min-width="120">
        <template #default="{ row }">
          第 {{ row.row }} 行 / 第 {{ row.col }} 列
        </template>
      </el-table-column>
      <el-table-column label="状态" min-width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
    </el-table>

    <template #footer>
      <el-button @click="emit('update:modelValue', false)">关闭</el-button>
    </template>
  </el-dialog>
</template>

<style scoped lang="scss">
.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
}

.detail-item {
  padding: 10px 12px;
  border-radius: 8px;
  background: #f8faff;
}

.detail-label {
  color: #909399;
  font-size: 12px;
}

.detail-value {
  margin-top: 6px;
  color: #303133;
  font-weight: 600;
}
</style>

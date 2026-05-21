<script setup lang="ts">
import { computed } from 'vue'
import type {
  WarehouseVisualAreaNode,
  WarehouseVisualBinNode,
  WarehouseVisualCabinetNode,
  WarehouseVisualModel,
} from '../visual-layout'

const props = defineProps<{
  warehouseName: string
  selectedArea: WarehouseVisualAreaNode | null
  selectedCabinet: WarehouseVisualCabinetNode | null
  selectedBin: WarehouseVisualBinNode | null
  visualModel: WarehouseVisualModel | null
  pendingLayoutCount: number
  isEditMode: boolean
}>()

defineEmits<{
  (e: 'open-cabinet-detail'): void
}>()

const cabinetBinCount = computed(() => props.selectedCabinet?.binIds.length ?? 0)
</script>

<template>
  <el-card class="summary-card" shadow="never">
    <template #header>
      <div class="card-header">
        <div class="header-title">联动摘要</div>
        <el-tag v-if="visualModel" type="success">已加载</el-tag>
      </div>
    </template>

    <div class="summary-section">
      <div class="summary-label">当前库房</div>
      <div class="summary-value">{{ warehouseName }}</div>
    </div>
    <div class="summary-section">
      <div class="summary-label">当前区域</div>
      <div class="summary-value">{{ selectedArea?.areaName ?? '未选择' }}</div>
      <div class="summary-extra">{{ selectedArea?.areaCode ?? '-' }}</div>
    </div>
    <div class="summary-section">
      <div class="summary-label">当前存放柜</div>
      <div class="summary-value">{{ selectedCabinet?.cabinetName ?? '未选择' }}</div>
      <div class="summary-extra">{{ selectedCabinet?.cabinetCode ?? '-' }}</div>
    </div>
    <div class="summary-section">
      <div class="summary-label">布局编辑</div>
      <div class="summary-value">{{ props.isEditMode ? '编辑中' : '浏览中' }}</div>
      <div class="summary-extra">待保存 {{ props.pendingLayoutCount }} 个存放柜</div>
    </div>
    <div class="summary-section">
      <div class="summary-label">当前库位</div>
      <div class="summary-value">{{ selectedBin?.binCode ?? '未选择' }}</div>
      <div class="summary-extra">
        {{ selectedBin ? `第 ${selectedBin.row} 行 / 第 ${selectedBin.col} 列` : '-' }}
      </div>
    </div>

    <div class="summary-actions">
      <el-button
        type="primary"
        plain
        :disabled="!selectedCabinet"
        @click="$emit('open-cabinet-detail')"
      >
        查看存放柜详情
      </el-button>
    </div>

    <el-divider />

    <div v-if="visualModel" class="summary-grid">
      <div class="metric-card">
        <div class="metric-label">区域数</div>
        <div class="metric-value">{{ visualModel.summary.totalAreas }}</div>
      </div>
      <div class="metric-card">
        <div class="metric-label">存放柜数</div>
        <div class="metric-value">{{ visualModel.summary.totalCabinets }}</div>
      </div>
      <div class="metric-card">
        <div class="metric-label">库位数</div>
        <div class="metric-value">{{ visualModel.summary.totalBins }}</div>
      </div>
      <div class="metric-card">
        <div class="metric-label">禁用库位</div>
        <div class="metric-value">{{ visualModel.summary.disabledBins }}</div>
      </div>
    </div>

    <el-alert
      v-if="selectedCabinet"
      type="info"
      :closable="false"
      :title="`当前存放柜共 ${cabinetBinCount} 个库位`"
      :description="cabinetBinCount === 0 ? '可在库位管理中维护库位后再次查看。' : '可打开详情弹窗查看库位明细。'"
    />
  </el-card>
</template>

<style scoped lang="scss">
.summary-card {
  margin-bottom: 16px;
}

.card-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-title,
.summary-value,
.metric-value {
  color: #303133;
  font-weight: 600;
}

.summary-section {
  margin-bottom: 16px;
}

.summary-label,
.metric-label,
.summary-extra {
  color: #909399;
}

.summary-label {
  margin-bottom: 6px;
  font-size: 13px;
}

.summary-value {
  min-height: 24px;
  font-size: 16px;
}

.summary-extra {
  margin-top: 4px;
  font-size: 13px;
}

.summary-actions {
  margin-bottom: 16px;
}

.summary-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.metric-card {
  padding: 12px;
  border: 1px solid #ebeef5;
  border-radius: 12px;
  background: #fafcff;
}

.metric-value {
  margin-top: 4px;
  font-size: 20px;
}
</style>

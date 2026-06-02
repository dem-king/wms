<script setup lang="ts">
import { computed } from 'vue'
import type {
  WarehouseVisualAreaNode,
  WarehouseVisualBinNode,
  WarehouseVisualCabinetNode,
  WarehouseVisualModel,
} from '../visual-layout'
import type { LayoutElementVo, ElementType } from '../types/layout-element'
import { ELEMENT_TYPES } from '../types/layout-element'
import LayoutElementEditorPanel from './LayoutElementEditorPanel.vue'

const props = defineProps<{
  warehouseName: string
  selectedArea: WarehouseVisualAreaNode | null
  selectedCabinet: WarehouseVisualCabinetNode | null
  selectedBin: WarehouseVisualBinNode | null
  visualModel: WarehouseVisualModel | null
  pendingLayoutCount: number
  isEditMode: boolean
  /** 选中的布局元素 */
  selectedElement: LayoutElementVo | null
}>()

const emit = defineEmits<{
  (e: 'open-cabinet-detail'): void
  (e: 'update-element-property', payload: { field: string; value: unknown }): void
  (e: 'update-element-style', payload: Record<string, unknown>): void
  (e: 'delete-element', elementId: string): void
}>()

const cabinetBinCount = computed(() => props.selectedCabinet?.binIds.length ?? 0)

/** 布局元素统计 */
const elementStats = computed(() => {
  if (!props.visualModel?.layoutElements) {
    return {}
  }
  const stats: Record<string, number> = {}
  for (const element of props.visualModel.layoutElements) {
    stats[element.elementType] = (stats[element.elementType] ?? 0) + 1
  }
  return stats
})
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

    <!-- 布局元素属性面板 -->
    <div v-if="selectedElement" class="element-panel-section">
      <LayoutElementEditorPanel
        :element="selectedElement"
        @update-property="emit('update-element-property', $event)"
        @update-style="emit('update-element-style', $event)"
        @delete="emit('delete-element', $event)"
      />
    </div>

    <el-divider v-if="visualModel" />

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

    <!-- 布局元素统计 -->
    <div v-if="visualModel?.layoutElements.length" class="element-stats">
      <div class="stats-title">布局元素</div>
      <div class="stats-grid">
        <div
          v-for="(count, type) in elementStats"
          :key="type"
          class="stats-item"
        >
          <span class="stats-label">{{ ELEMENT_TYPES[type as ElementType] ?? type }}</span>
          <span class="stats-count">{{ count }}</span>
        </div>
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

.element-panel-section {
  margin-bottom: 16px;
}

.element-stats {
  margin-top: 12px;
}

.stats-title {
  margin-bottom: 8px;
  color: #909399;
  font-size: 13px;
}

.stats-grid {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.stats-item {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 4px 8px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  background: #fafcff;
  font-size: 12px;
}

.stats-label {
  color: #909399;
}

.stats-count {
  color: #303133;
  font-weight: 600;
}
</style>

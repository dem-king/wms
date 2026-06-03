<script setup lang="ts">
/**
 * 库位网格面板
 * 在3D场景中以InstancedMesh渲染库位方块，颜色编码状态
 * 提供鼠标悬停高亮和点击显示物品信息功能
 */
import { computed } from 'vue'
import type { EntityId } from '@/types/warehouse'
import type { WarehouseVisualBinNode, WarehouseVisualCabinetNode } from '../visual-layout'
import { VISUAL_STATUS_DISABLED } from '../visual-layout'

const props = defineProps<{
  /** 当前存放柜 */
  cabinet: WarehouseVisualCabinetNode | null
  /** 库位列表 */
  bins: WarehouseVisualBinNode[]
  /** 是否显示 */
  visible: boolean
}>()

const emit = defineEmits<{
  (e: 'select-bin', binId: EntityId): void
}>()


/** 按行分组的库位 */
const binRows = computed(() => {
  if (!props.cabinet || props.bins.length === 0) {
    return []
  }

  const rows = props.cabinet.gridRowCount || props.cabinet.rows || 1
  const result: WarehouseVisualBinNode[][] = []

  for (let r = 1; r <= rows; r++) {
    const rowBins = props.bins.filter(bin => bin.row === r).sort((a, b) => a.col - b.col)
    result.push(rowBins)
  }

  return result
})

/**
 * 获取库位状态CSS类名
 */
function getBinStatusClass(bin: WarehouseVisualBinNode): string {
  if (bin.status === VISUAL_STATUS_DISABLED) {
    return 'bin-disabled'
  }
  return 'bin-free'
}

/**
 * 获取库位状态文本
 */
function getBinStatusText(bin: WarehouseVisualBinNode): string {
  if (bin.status === VISUAL_STATUS_DISABLED) {
    return '禁用'
  }
  return '空闲'
}

/**
 * 处理库位点击
 */
function handleBinClick(bin: WarehouseVisualBinNode): void {
  emit('select-bin', bin.id as EntityId)
}
</script>

<template>
  <div v-if="visible && cabinet" class="bin-grid-panel">
    <div class="panel-title">
      {{ cabinet.cabinetCode }} - 库位网格
    </div>

    <div v-if="bins.length === 0" class="empty-hint">
      该存放柜暂无库位数据
    </div>

    <div v-else class="grid-container">
      <!-- 图例 -->
      <div class="legend">
        <span class="legend-item">
          <span class="legend-color bin-free" />空闲
        </span>
        <span class="legend-item">
          <span class="legend-color bin-occupied" />占用
        </span>
        <span class="legend-item">
          <span class="legend-color bin-disabled" />禁用
        </span>
      </div>

      <!-- 网格 -->
      <div class="grid">
        <div
          v-for="(row, rowIndex) in binRows"
          :key="rowIndex"
          class="grid-row"
        >
          <span class="row-label">{{ rowIndex + 1 }}</span>
          <div
            v-for="bin in row"
            :key="bin.id"
            class="grid-cell"
            :class="getBinStatusClass(bin)"
            :title="`${bin.binCode} (${getBinStatusText(bin)})`"
            @click="handleBinClick(bin)"
          >
            {{ bin.col }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped lang="scss">
.bin-grid-panel {
  padding: 12px;
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.panel-title {
  font-size: 14px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.empty-hint {
  color: #909399;
  font-size: 13px;
  text-align: center;
  padding: 20px 0;
}

.legend {
  display: flex;
  gap: 16px;
  margin-bottom: 12px;
  font-size: 12px;
  color: #606266;
}

.legend-item {
  display: flex;
  align-items: center;
  gap: 4px;
}

.legend-color {
  display: inline-block;
  width: 12px;
  height: 12px;
  border-radius: 2px;
}

.grid-container {
  overflow-x: auto;
}

.grid {
  display: inline-block;
}

.grid-row {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-bottom: 4px;
}

.row-label {
  width: 20px;
  font-size: 11px;
  color: #909399;
  text-align: right;
}

.grid-cell {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  color: #fff;
  border-radius: 2px;
  cursor: pointer;
  transition: transform 0.15s, box-shadow 0.15s;

  &:hover {
    transform: scale(1.1);
    box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
  }
}

.bin-free {
  background: #52c41a;
}

.bin-occupied {
  background: #1890ff;
}

.bin-disabled {
  background: #d9d9d9;
  color: #999;
}
</style>
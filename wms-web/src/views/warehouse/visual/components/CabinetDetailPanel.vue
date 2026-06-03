<script setup lang="ts">
/**
 * 柜子详情侧面板
 * 替代旧CabinetDetailDialog.vue（弹窗改为侧面板）
 * 显示存放柜编码、名称、所属区域、行列数和库位状态统计
 */
import { computed } from 'vue'

import type { WarehouseVisualCabinetNode, WarehouseVisualBinNode, WarehouseVisualAreaNode, WarehouseVisualModel } from '../visual-layout'
import { VISUAL_STATUS_ENABLED, VISUAL_STATUS_DISABLED } from '../visual-layout'

const props = defineProps<{
  /** 选中的存放柜 */
  cabinet: WarehouseVisualCabinetNode | null
  /** 存放柜的库位列表 */
  bins: WarehouseVisualBinNode[]
  /** 存放柜所属区域 */
  area: WarehouseVisualAreaNode | null
  /** 可视化模型（用于摘要统计） */
  visualModel: WarehouseVisualModel | null
  /** 是否显示面板 */
  visible: boolean
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

/** 空闲库位数 */
const freeBinCount = computed(() => {
  return props.bins.filter(bin => bin.status === VISUAL_STATUS_ENABLED).length
})

/** 占用库位数（简化：启用但非空闲视为占用） */
const occupiedBinCount = computed(() => {
  // 简化处理：当前数据模型中无法直接区分占用和空闲
  // 实际应从库存数据判断，此处暂按启用-空闲=占用
  return 0
})

/** 禁用库位数 */
const disabledBinCount = computed(() => {
  return props.bins.filter(bin => bin.status === VISUAL_STATUS_DISABLED).length
})

/** 库位总数 */
const totalBinCount = computed(() => props.bins.length)

/** 库位利用率 */
const utilizationRate = computed(() => {
  if (totalBinCount.value === 0) {
    return '0%'
  }
  return `${Math.round((occupiedBinCount.value / totalBinCount.value) * 100)}%`
})
</script>

<template>
  <transition name="slide-right">
    <div v-if="visible && cabinet" class="cabinet-detail-panel">
      <div class="panel-header">
        <span class="panel-title">存放柜详情</span>
        <el-button text @click="emit('close')">
          <el-icon><Close /></el-icon>
        </el-button>
      </div>

      <div class="panel-body">
        <!-- 基本信息 -->
        <div class="info-section">
          <h4>基本信息</h4>
          <div class="info-row">
            <span class="info-label">编码</span>
            <span class="info-value">{{ cabinet.cabinetCode }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">名称</span>
            <span class="info-value">{{ cabinet.cabinetName }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">所属区域</span>
            <span class="info-value">{{ area?.areaName ?? '-' }}</span>
          </div>
          <div class="info-row">
            <span class="info-label">行列数</span>
            <span class="info-value">{{ cabinet.rows }} × {{ cabinet.cols }}</span>
          </div>
        </div>

        <!-- 库位状态统计 -->
        <div class="info-section">
          <h4>库位状态</h4>
          <div class="bin-stats">
            <div class="stat-item stat-free">
              <span class="stat-count">{{ freeBinCount }}</span>
              <span class="stat-label">空闲</span>
            </div>
            <div class="stat-item stat-occupied">
              <span class="stat-count">{{ occupiedBinCount }}</span>
              <span class="stat-label">占用</span>
            </div>
            <div class="stat-item stat-disabled">
              <span class="stat-count">{{ disabledBinCount }}</span>
              <span class="stat-label">禁用</span>
            </div>
            <div class="stat-item stat-total">
              <span class="stat-count">{{ totalBinCount }}</span>
              <span class="stat-label">总计</span>
            </div>
          </div>
          <div class="utilization-bar">
            <span class="info-label">利用率</span>
            <el-progress
              :percentage="Number(utilizationRate.replace('%', ''))"
              :stroke-width="12"
              :format="() => utilizationRate"
            />
          </div>
        </div>

        <!-- 库位网格预览 -->
        <div v-if="bins.length > 0" class="info-section">
          <h4>库位网格</h4>
          <div class="bin-grid">
            <div
              v-for="bin in bins"
              :key="bin.id"
              class="bin-cell"
              :class="{
                'bin-free': bin.status === VISUAL_STATUS_ENABLED,
                'bin-disabled': bin.status === VISUAL_STATUS_DISABLED,
              }"
              :title="`${bin.binCode} (${bin.status === VISUAL_STATUS_ENABLED ? '空闲' : '禁用'})`"
            />
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script lang="ts">
import { Close } from '@element-plus/icons-vue'
export default {
  components: { Close },
}
</script>

<style scoped lang="scss">
.cabinet-detail-panel {
  position: fixed;
  top: 0;
  right: 0;
  width: 320px;
  height: 100vh;
  background: #fff;
  border-left: 1px solid #e4e7ed;
  box-shadow: -4px 0 12px rgba(0, 0, 0, 0.08);
  z-index: 1000;
  overflow-y: auto;
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;
}

.panel-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.panel-body {
  padding: 16px 20px;
}

.info-section {
  margin-bottom: 20px;

  h4 {
    margin: 0 0 12px;
    font-size: 14px;
    color: #303133;
  }
}

.info-row {
  display: flex;
  justify-content: space-between;
  padding: 6px 0;
  font-size: 13px;
}

.info-label {
  color: #909399;
}

.info-value {
  color: #303133;
  font-weight: 500;
}

.bin-stats {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
}

.stat-item {
  flex: 1;
  text-align: center;
  padding: 8px 4px;
  border-radius: 4px;
}

.stat-count {
  display: block;
  font-size: 20px;
  font-weight: 600;
}

.stat-label {
  display: block;
  font-size: 12px;
  margin-top: 4px;
}

.stat-free {
  background: #f6ffed;
  color: #52c41a;
}

.stat-occupied {
  background: #e6f7ff;
  color: #1890ff;
}

.stat-disabled {
  background: #f5f5f5;
  color: #d9d9d9;
}

.stat-total {
  background: #fafafa;
  color: #333333;
}

.utilization-bar {
  display: flex;
  align-items: center;
  gap: 12px;
}

.bin-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(24px, 1fr));
  gap: 4px;
}

.bin-cell {
  width: 24px;
  height: 24px;
  border-radius: 2px;
  cursor: pointer;
  transition: transform 0.15s;

  &:hover {
    transform: scale(1.15);
  }
}

.bin-free {
  background: #52c41a;
}

.bin-disabled {
  background: #d9d9d9;
}

// 侧面板滑入动画
.slide-right-enter-active,
.slide-right-leave-active {
  transition: transform 0.3s ease;
}

.slide-right-enter-from,
.slide-right-leave-to {
  transform: translateX(100%);
}
</style>
<script setup lang="ts">
/**
 * 库房可视化顶栏
 * 库房选择、编码快速定位、关键统计指标
 */
import { computed } from 'vue'
import type { EntityId, WmsWarehouseVo } from '@/types/warehouse'

interface Stats {
  totalAreas: number
  totalCabinets: number
  totalBins: number
  disabledBins: number
}

const props = defineProps<{
  warehouseList: WmsWarehouseVo[]
  selectedWarehouseId?: EntityId
  warehouseName: string
  loading: boolean
  keyword: string
  feedback: string
  stats: Stats
  /** 是否正在快速定位中（用于光标反馈） */
  locating?: boolean
}>()

const emit = defineEmits<{
  (e: 'change-warehouse', value: EntityId): void
  (e: 'update:keyword', value: string): void
  (e: 'locate'): void
  (e: 'refresh'): void
}>()

const utilization = computed(() => {
  if (props.stats.totalBins === 0) return 0
  return Math.round(((props.stats.totalBins - props.stats.disabledBins) / props.stats.totalBins) * 100)
})
</script>

<template>
  <header class="visual-header">
    <div class="header-left">
      <div class="brand">
        <span class="brand-mark" aria-hidden="true">
          <svg viewBox="0 0 32 32" width="22" height="22">
            <rect x="3" y="6" width="26" height="20" rx="2" fill="none" stroke="currentColor" stroke-width="1.4" />
            <line x1="3" y1="12" x2="29" y2="12" stroke="currentColor" stroke-width="1" />
            <line x1="11" y1="12" x2="11" y2="26" stroke="currentColor" stroke-width="1" />
            <line x1="19" y1="12" x2="19" y2="26" stroke="currentColor" stroke-width="1" />
            <line x1="3" y1="19" x2="29" y2="19" stroke="currentColor" stroke-width="1" />
          </svg>
        </span>
        <div class="brand-text">
          <h1 class="brand-title">库房平面图</h1>
          <p class="brand-sub">WAREHOUSE · FLOOR PLAN</p>
        </div>
      </div>

      <div class="divider" />

      <div class="warehouse-pick">
        <label class="field-label">库房</label>
        <el-select
          :model-value="props.selectedWarehouseId"
          placeholder="选择库房"
          filterable
          size="default"
          class="warehouse-select"
          @change="(v: EntityId | undefined) => v && emit('change-warehouse', v)"
        >
          <el-option
            v-for="item in props.warehouseList"
            :key="item.id"
            :label="item.warehouseName"
            :value="item.id"
          >
            <div class="warehouse-option">
              <span class="warehouse-option-name">{{ item.warehouseName }}</span>
              <span class="warehouse-option-code">{{ item.warehouseCode }}</span>
            </div>
          </el-option>
        </el-select>
        <span class="warehouse-tag">{{ props.warehouseName || '未选择' }}</span>
      </div>
    </div>

    <div class="header-stats">
      <div class="stat">
        <span class="stat-label">区域</span>
        <span class="stat-value">{{ props.stats.totalAreas }}</span>
        <span class="stat-unit">个</span>
      </div>
      <div class="stat">
        <span class="stat-label">存放柜</span>
        <span class="stat-value">{{ props.stats.totalCabinets }}</span>
        <span class="stat-unit">组</span>
      </div>
      <div class="stat">
        <span class="stat-label">库位总数</span>
        <span class="stat-value">{{ props.stats.totalBins }}</span>
        <span class="stat-unit">位</span>
      </div>
      <div class="stat stat-accent">
        <span class="stat-label">可用率</span>
        <span class="stat-value">{{ utilization }}</span>
        <span class="stat-unit">%</span>
        <div class="stat-bar">
          <div class="stat-bar-fill" :style="{ width: utilization + '%' }" />
        </div>
      </div>
    </div>

    <div class="header-right">
      <div class="locator" :class="{ 'is-locating': props.locating }">
        <span class="locator-prefix">⌕</span>
        <input
          :value="props.keyword"
          class="locator-input"
          placeholder="输入区域 / 柜 / 库位编码快速定位"
          @input="emit('update:keyword', ($event.target as HTMLInputElement).value)"
          @keyup.enter="emit('locate')"
        />
        <button class="locator-button" @click="emit('locate')">定位</button>
      </div>
      <p v-if="props.feedback" class="locator-feedback">{{ props.feedback }}</p>

      <el-button :loading="props.loading" plain class="refresh-btn" @click="emit('refresh')">
        <span class="refresh-dot" />
        刷新
      </el-button>
    </div>
  </header>
</template>

<style scoped lang="scss">
.visual-header {
  display: grid;
  grid-template-columns: minmax(360px, 1fr) auto minmax(360px, 1fr);
  gap: 32px;
  align-items: center;
  padding: 22px 28px;
  background: var(--bg-card);
  border-bottom: 1px solid var(--color-border);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 24px;
  min-width: 0;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  color: var(--color-primary);
}

.brand-mark {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 38px;
  height: 38px;
  border-radius: 8px;
  background: linear-gradient(135deg, rgba(14, 165, 233, 0.12), rgba(14, 165, 233, 0.04));
  border: 1px solid rgba(14, 165, 233, 0.2);
}

.brand-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brand-title {
  margin: 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-foreground);
  letter-spacing: 0.02em;
}

.brand-sub {
  margin: 0;
  font-size: 10px;
  font-weight: 500;
  letter-spacing: 0.18em;
  color: var(--text-muted-foreground);
  font-family: 'JetBrains Mono', 'SF Mono', Menlo, monospace;
}

.divider {
  width: 1px;
  height: 32px;
  background: var(--color-border);
}

.warehouse-pick {
  display: flex;
  align-items: center;
  gap: 10px;
  min-width: 0;
}

.field-label {
  font-size: 11px;
  letter-spacing: 0.16em;
  color: var(--text-muted-foreground);
  text-transform: uppercase;
  font-family: 'JetBrains Mono', 'SF Mono', Menlo, monospace;
}

.warehouse-select {
  width: 220px;
}

.warehouse-tag {
  font-size: 12px;
  color: var(--text-muted-foreground);
  padding: 4px 10px;
  background: var(--bg-accent);
  border-radius: 999px;
  border: 1px solid var(--color-border);
  white-space: nowrap;
}

.warehouse-option {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}
.warehouse-option-name {
  font-weight: 500;
}
.warehouse-option-code {
  font-family: 'JetBrains Mono', 'SF Mono', Menlo, monospace;
  font-size: 11px;
  color: var(--text-muted-foreground);
  letter-spacing: 0.05em;
}

.header-stats {
  display: flex;
  align-items: stretch;
  gap: 28px;
  padding: 8px 24px;
  background: var(--bg-accent);
  border: 1px solid var(--color-border);
  border-radius: 14px;
}

.stat {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 2px;
  min-width: 60px;
  position: relative;
}

.stat-label {
  font-size: 10px;
  letter-spacing: 0.18em;
  color: var(--text-muted-foreground);
  text-transform: uppercase;
  font-family: 'JetBrains Mono', 'SF Mono', Menlo, monospace;
}

.stat-value {
  font-size: 22px;
  font-weight: 600;
  color: var(--text-foreground);
  line-height: 1.1;
  font-variant-numeric: tabular-nums;
}

.stat-unit {
  font-size: 11px;
  color: var(--text-muted-foreground);
  margin-left: 2px;
}

.stat-accent {
  position: relative;
  padding-right: 8px;
}
.stat-accent .stat-value {
  color: var(--color-primary);
}

.stat-bar {
  position: absolute;
  bottom: -2px;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--color-border);
  border-radius: 1px;
  overflow: hidden;
}

.stat-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, var(--color-primary), #14b8a6);
  border-radius: 1px;
  transition: width 0.4s ease;
}

.header-right {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 6px;
  min-width: 0;
}

.locator {
  display: flex;
  align-items: stretch;
  width: 360px;
  height: 38px;
  border: 1px solid var(--color-border);
  border-radius: 10px;
  background: var(--bg-card);
  overflow: hidden;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;

  &:focus-within {
    border-color: var(--color-primary);
    box-shadow: 0 0 0 3px rgba(14, 165, 233, 0.12);
  }

  &.is-locating {
    border-color: var(--color-primary);
    animation: locatorPulse 1.2s ease-in-out;
  }
}

@keyframes locatorPulse {
  0%, 100% { box-shadow: 0 0 0 0 rgba(14, 165, 233, 0.0); }
  50% { box-shadow: 0 0 0 6px rgba(14, 165, 233, 0.18); }
}

.locator-prefix {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  font-size: 16px;
  color: var(--text-muted-foreground);
  background: var(--bg-accent);
}

.locator-input {
  flex: 1;
  border: none;
  outline: none;
  background: transparent;
  padding: 0 12px;
  font-size: 13px;
  color: var(--text-foreground);
  font-family: 'JetBrains Mono', 'SF Mono', Menlo, monospace;
  letter-spacing: 0.02em;

  &::placeholder {
    color: var(--text-muted-foreground);
    font-family: inherit;
  }
}

.locator-button {
  border: none;
  outline: none;
  background: var(--text-foreground);
  color: var(--bg-card);
  padding: 0 16px;
  font-size: 12px;
  font-weight: 500;
  letter-spacing: 0.04em;
  cursor: pointer;
  transition: background 0.2s ease;

  &:hover {
    background: var(--color-primary);
  }
}

.locator-feedback {
  margin: 0;
  font-size: 11px;
  color: var(--text-muted-foreground);
  letter-spacing: 0.02em;
  max-width: 360px;
  text-align: right;
}

.refresh-btn {
  position: relative;
}

.refresh-btn :deep(.refresh-dot) {
  display: inline-block;
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: var(--color-primary);
  margin-right: 6px;
  vertical-align: middle;
  animation: refreshPulse 1.6s ease-in-out infinite;
}

@keyframes refreshPulse {
  0%, 100% { opacity: 0.4; transform: scale(0.85); }
  50% { opacity: 1; transform: scale(1.15); }
}
</style>

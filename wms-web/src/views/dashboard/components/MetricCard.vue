<template>
  <el-card shadow="never" class="metric-card" :body-style="{ padding: '20px' }">
    <div class="metric-header">
      <span class="metric-title">{{ item.name }}</span>
      <el-icon class="metric-icon" :style="{ color: item.color, backgroundColor: `${item.color}14` }">
        <component :is="icons[item.icon as keyof typeof icons]" />
      </el-icon>
    </div>
    <div class="metric-body">
      <div class="metric-value">
        <span class="value">{{ formattedValue }}</span>
        <span class="unit" v-if="item.unit">{{ item.unit }}</span>
      </div>
      <div class="metric-footer">
        <span :class="['trend-indicator', item.trend]">
          <el-icon v-if="item.trend === 'up'"><Top /></el-icon>
          <el-icon v-else-if="item.trend === 'down'"><Bottom /></el-icon>
          <el-icon v-else><Minus /></el-icon>
          {{ Math.abs(item.changeRate) }}%
        </span>
        <span class="trend-label">较上月</span>
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import type { MetricItem } from '@/types/dashboard'
import { Top, Bottom, Minus, Box, Money, Download, Upload, Document, Warning } from '@element-plus/icons-vue'

const icons = { Top, Bottom, Minus, Box, Money, Download, Upload, Document, Warning }

const props = defineProps<{
  item: MetricItem
}>()

const formattedValue = computed(() => {
  return new Intl.NumberFormat('en-US').format(props.item.value)
})
</script>

<style lang="scss" scoped>
.metric-card {
  height: 100%;
  border: none;
  border-radius: 12px;
  background: hsl(var(--card));
  box-shadow: 0 10px 30px hsl(var(--foreground) / 6%);
  transition: transform 0.24s ease, box-shadow 0.24s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 18px 40px hsl(var(--foreground) / 10%);
  }

  :deep(.el-card__body) {
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    height: 100%;
    min-height: 140px;
  }

  :deep(.el-card__header) {
    border-bottom: none;
  }

  .metric-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    .metric-title {
      font-size: 14px;
      color: hsl(var(--muted-foreground));
    }

    .metric-icon {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 40px;
      height: 40px;
      font-size: 20px;
      border-radius: 12px;
    }
  }

  .metric-body {
    .metric-value {
      display: flex;
      align-items: baseline;
      margin-bottom: 12px;

      .value {
        font-size: 30px;
        font-weight: 600;
        color: hsl(var(--card-foreground));
        line-height: 1;
      }

      .unit {
        font-size: 12px;
        color: hsl(var(--muted-foreground));
        margin-left: 4px;
      }
    }

    .metric-footer {
      font-size: 12px;
      display: flex;
      align-items: center;
      gap: 8px;

      .trend-indicator {
        display: flex;
        align-items: center;
        gap: 2px;
        font-weight: 500;

        &.up {
          color: var(--el-color-danger);
        }

        &.down {
          color: var(--el-color-success);
        }

        &.flat {
          color: hsl(var(--muted-foreground));
        }
      }

      .trend-label {
        color: hsl(var(--muted-foreground));
      }
    }
  }
}
</style>

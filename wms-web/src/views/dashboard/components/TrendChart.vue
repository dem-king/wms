<template>
  <el-card class="chart-card" shadow="never">
    <template #header>
      <div class="card-header">
        <span class="title">库存趋势</span>
        <el-radio-group v-model="timeRange" size="small" @change="handleRangeChange">
          <el-radio-button label="week">本周</el-radio-button>
          <el-radio-button label="month">本月</el-radio-button>
          <el-radio-button label="year">本年</el-radio-button>
        </el-radio-group>
      </div>
    </template>
    <Chart :option="chartOption" height="380px" />
  </el-card>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import Chart from '@/components/Chart/index.vue'

const props = defineProps<{
  data: { date: string; quantity: number; amount: number }[]
}>()

const timeRange = ref('month')

const emit = defineEmits<{
  (e: 'rangeChange', range: string): void
}>()

const handleRangeChange = (val: string | number | boolean | undefined) => {
  emit('rangeChange', String(val))
}

/** 检测当前是否为暗黑模式 */
const isDark = computed(() => document.documentElement.classList.contains('dark'))

/** 暗黑模式下 ECharts 文字颜色 */
const textColor = computed(() => isDark.value ? '#e2e8f0' : '#333')
const axisLineColor = computed(() => isDark.value ? 'rgba(255,255,255,0.1)' : '#eee')
const tooltipBg = computed(() => isDark.value ? '#1e293b' : '#fff')

const chartOption = computed(() => {
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' },
      backgroundColor: tooltipBg.value,
        borderColor: axisLineColor.value,
      textStyle: { color: textColor.value }
    },
    legend: {
      data: ['库存数量', '库存金额'],
      bottom: 0,
      textStyle: { color: textColor.value }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '10%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: props.data.map(d => d.date),
      axisLine: { lineStyle: { color: axisLineColor.value } },
      axisLabel: { color: textColor.value },
      splitLine: { show: false }
    },
    yAxis: [
      {
        type: 'value',
        name: '数量',
        position: 'left',
        nameTextStyle: { color: textColor.value },
        splitLine: { lineStyle: { color: axisLineColor.value, type: 'dashed' } },
        axisLabel: { color: textColor.value }
      },
      {
        type: 'value',
        name: '金额',
        position: 'right',
        nameTextStyle: { color: textColor.value },
        splitLine: { show: false },
        axisLabel: { color: textColor.value }
      }
    ],
    series: [
      {
        name: '库存数量',
        type: 'line',
        smooth: true,
        itemStyle: { color: '#409EFF' },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0, y: 0, x2: 0, y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(64,158,255,0.3)' },
              { offset: 1, color: 'rgba(64,158,255,0.05)' }
            ]
          }
        },
        data: props.data.map(d => d.quantity)
      },
      {
        name: '库存金额',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        itemStyle: { color: '#67C23A' },
        data: props.data.map(d => d.amount)
      }
    ]
  }
})
</script>

<style lang="scss" scoped>
.chart-card {
  height: 100%;
  border: none;
  border-radius: 12px;
  box-shadow: 0 10px 30px hsl(var(--foreground) / 6%);
  transition: transform 0.24s ease, box-shadow 0.24s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 18px 40px hsl(var(--foreground) / 10%);
  }

  :deep(.el-card__header) {
    border-bottom: none;
    padding-bottom: 12px;
  }

  :deep(.el-card__body) {
    height: calc(100% - 57px);
    padding-top: 0;
  }
  
  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 12px;
    
    .title {
      font-weight: 500;
    }
  }
}

@media (max-width: 767px) {
  .chart-card {
    .card-header {
      flex-wrap: wrap;
    }
  }
}
</style>

<template>
  <el-card class="chart-card" shadow="never">
    <template #header>
      <div class="card-header">
        <span class="title">出入库对比</span>
      </div>
    </template>
    <Chart :option="chartOption" height="300px" />
  </el-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import Chart from '@/components/Chart/index.vue'

const props = defineProps<{
  data: { date: string; inbound: number; outbound: number }[]
}>()

/** 检测当前是否为暗黑模式 */
const isDark = computed(() => document.documentElement.classList.contains('dark'))

const textColor = computed(() => isDark.value ? '#e2e8f0' : '#333')
const axisLineColor = computed(() => isDark.value ? 'rgba(255,255,255,0.1)' : '#eee')

const chartOption = computed(() => {
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'shadow' },
      backgroundColor: isDark.value ? '#1e293b' : '#fff',
      borderColor: axisLineColor.value,
      textStyle: { color: textColor.value }
    },
    legend: {
      data: ['入库', '出库'],
      bottom: 0,
      textStyle: { color: textColor.value }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '12%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      data: props.data.map(d => d.date),
      axisLine: { lineStyle: { color: axisLineColor.value } },
      axisLabel: { color: textColor.value }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: axisLineColor.value, type: 'dashed' } },
      axisLabel: { color: textColor.value }
    },
    series: [
      {
        name: '入库',
        type: 'bar',
        barWidth: '30%',
        itemStyle: { color: '#409EFF', borderRadius: [4, 4, 0, 0] },
        data: props.data.map(d => d.inbound)
      },
      {
        name: '出库',
        type: 'bar',
        barWidth: '30%',
        itemStyle: { color: '#E6A23C', borderRadius: [4, 4, 0, 0] },
        data: props.data.map(d => d.outbound)
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
    padding-bottom: 8px;
  }

  :deep(.el-card__body) {
    height: calc(100% - 57px);
    padding-top: 0;
  }
  
  .card-header {
    .title {
      font-weight: 500;
    }
  }
}
</style>

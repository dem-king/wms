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

const chartOption = computed(() => {
  return {
    tooltip: {
      trigger: 'axis',
      axisPointer: { type: 'cross' }
    },
    legend: {
      data: ['库存数量', '库存金额'],
      bottom: 0
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
      data: props.data.map(d => d.date)
    },
    yAxis: [
      {
        type: 'value',
        name: '数量',
        position: 'left',
        splitLine: { lineStyle: { type: 'dashed' } }
      },
      {
        type: 'value',
        name: '金额',
        position: 'right',
        splitLine: { show: false }
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
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.06);
  transition: transform 0.24s ease, box-shadow 0.24s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 18px 40px rgba(15, 23, 42, 0.1);
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

<template>
  <el-card class="chart-card" shadow="never">
    <template #header>
      <div class="card-header">
        <span class="title">分类分布</span>
      </div>
    </template>
    <Chart :option="chartOption" height="300px" />
  </el-card>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import Chart from '@/components/Chart/index.vue'

const props = defineProps<{
  data: { name: string; value: number }[]
}>()

const chartOption = computed(() => {
  return {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center'
    },
    series: [
      {
        name: '分类分布',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 4,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false,
          position: 'center'
        },
        emphasis: {
          label: {
            show: true,
            fontSize: '14',
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: props.data
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

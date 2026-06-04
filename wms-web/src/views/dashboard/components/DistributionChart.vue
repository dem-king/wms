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

/** 检测当前是否为暗黑模式 */
const isDark = computed(() => document.documentElement.classList.contains('dark'))

const textColor = computed(() => isDark.value ? '#e2e8f0' : '#333')
/** 饼图扇区边框色：暗黑模式下用深色，亮色模式用白色 */
const pieBorderColor = computed(() => isDark.value ? '#1e293b' : '#fff')

const chartOption = computed(() => {
  return {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)',
      backgroundColor: isDark.value ? '#1e293b' : '#fff',
      borderColor: isDark.value ? 'rgba(255,255,255,0.1)' : '#eee',
      textStyle: { color: textColor.value }
    },
    legend: {
      orient: 'vertical',
      right: 10,
      top: 'center',
      textStyle: { color: textColor.value }
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
          borderColor: pieBorderColor.value,
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
            fontWeight: 'bold',
            color: textColor.value
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

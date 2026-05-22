<script setup lang="ts">
import { ref, watch, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'

interface ChartProps {
  chartType?: 'line' | 'bar' | 'pie'
  title?: string
  option?: Record<string, any>
  height?: string
  loading?: boolean
}

const props = withDefaults(defineProps<ChartProps>(), {
  chartType: 'line',
  title: '',
  option: () => ({}),
  height: '350px',
  loading: false
})

const chartRef = ref<HTMLDivElement>()
let chartInstance: echarts.ECharts | null = null

function initChart() {
  if (!chartRef.value) return
  chartInstance = echarts.init(chartRef.value)
  renderChart()
}

function renderChart() {
  if (!chartInstance) return
  if (props.option && Object.keys(props.option).length > 0) {
    chartInstance.setOption(props.option, true)
    return
  }
}

function handleResize() {
  chartInstance?.resize()
}

watch(() => props.option, () => {
  nextTick(() => renderChart())
}, { deep: true })

watch(() => props.loading, (val) => {
  if (!chartInstance) return
  val ? chartInstance.showLoading() : chartInstance.hideLoading()
})

onMounted(() => {
  initChart()
  window.addEventListener('resize', handleResize)
})

onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  chartInstance?.dispose()
  chartInstance = null
})
</script>

<template>
  <div ref="chartRef" :style="{ height }" class="chart-container" />
</template>

<style scoped lang="scss">
.chart-container {
  width: 100%;
  min-height: 200px;
}
</style>

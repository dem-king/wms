<template>
  <div class="dashboard">
    <el-row v-loading="loading" :gutter="16">
      <el-col :span="6" v-for="card in statCards" :key="card.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ card.value }}</div>
          <div class="stat-title">{{ card.title }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card>
          <template #header>库存概览</template>
          <Chart :option="stockChartOption" height="300px" />
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>近期预警</template>
          <el-empty v-if="!alertRows.length" description="暂无预警" />
          <el-table v-else :data="alertRows" size="small">
            <el-table-column prop="alertTypeName" label="预警类型" min-width="120" />
            <el-table-column prop="triggerCount" label="触发次数" min-width="100" />
            <el-table-column prop="affectedItemCount" label="涉及物品数" min-width="120" />
            <el-table-column prop="resolvedCount" label="已处理" min-width="90" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import Chart from '@/components/Chart/index.vue'
import { getAlertSummary, getReturnSummary, getStockSummary, getStockTrend } from '@/api/report'
import type { AlertSummaryItem, AlertSummaryVo, ReturnSummaryVo, StockSummaryVo, StockTrendVo } from '@/types/report'

const loading = ref(false)
const stockSummary = ref<StockSummaryVo | null>(null)
const returnSummary = ref<ReturnSummaryVo | null>(null)
const alertSummary = ref<AlertSummaryVo | null>(null)
const stockChartOption = ref<Record<string, unknown>>({})

const statCards = computed(() => [
  { title: '库存总数量', value: stockSummary.value?.totalQuantity ?? 0 },
  { title: '库存总金额', value: stockSummary.value?.totalAmount ?? 0 },
  { title: '借还单数', value: returnSummary.value?.orderCount ?? 0 },
  { title: '预警触发次数', value: alertSummary.value?.totalTriggerCount ?? 0 },
])

const alertRows = computed<AlertSummaryItem[]>(() => alertSummary.value?.alertSummaryList || [])

function getDefaultDateRange() {
  const now = new Date()
  const start = new Date(now.getFullYear(), now.getMonth() - 1, 1)
  const formatDate = (date: Date) =>
    `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`

  return {
    startDate: formatDate(start),
    endDate: formatDate(now),
  }
}

function buildStockChart(data: StockTrendVo) {
  stockChartOption.value = {
    title: { text: '最近一个月库存趋势', left: 'center' },
    tooltip: { trigger: 'axis' },
    legend: { data: ['数量', '金额'], bottom: 0 },
    xAxis: {
      type: 'category',
      data: data.trendList.map(item => item.date),
    },
    yAxis: [
      { type: 'value', name: '数量' },
      { type: 'value', name: '金额' },
    ],
    series: [
      {
        name: '数量',
        type: 'line',
        smooth: true,
        data: data.trendList.map(item => item.quantity),
      },
      {
        name: '金额',
        type: 'line',
        smooth: true,
        yAxisIndex: 1,
        data: data.trendList.map(item => item.amount),
      },
    ],
  }
}

async function loadDashboard() {
  loading.value = true
  try {
    const params = getDefaultDateRange()
    const [stockRes, returnRes, alertRes, trendRes] = await Promise.all([
      getStockSummary(params),
      getReturnSummary(params),
      getAlertSummary(params),
      getStockTrend({ ...params, trendType: 'DAILY' }),
    ])
    stockSummary.value = stockRes.data
    returnSummary.value = returnRes.data
    alertSummary.value = alertRes.data
    buildStockChart(trendRes.data)
  } catch {
    ElMessage.error('首页数据加载失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadDashboard()
})
</script>

<style lang="scss" scoped>
.stat-card {
  text-align: center;
  .stat-value { font-size: 28px; font-weight: bold; color: #409eff; }
  .stat-title { font-size: 14px; color: #909399; margin-top: 8px; }
}
</style>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import Chart from '@/components/Chart/index.vue'
import { getCostSummary, getCostConfig, updateCostConfig } from '@/api/report'
import type { CostAccountVo, CostAccountConfigDto } from '@/types/report'

const loading = ref(false)
const costData = ref<CostAccountVo | null>(null)
const configEnabled = ref(false)
const configLoading = ref(false)

const currentYear = new Date().getFullYear()
const queryParams = reactive({
  year: currentYear,
  period: 'MONTHLY' as string
})

const costChartOption = ref<Record<string, any>>({})
const costPieOption = ref<Record<string, any>>({})

const periodOptions = [
  { label: '月度', value: 'MONTHLY' },
  { label: '季度', value: 'QUARTERLY' },
  { label: '年度', value: 'YEARLY' }
]

const yearOptions = Array.from({ length: 10 }, (_, i) => currentYear - i)

async function handleQuery() {
  loading.value = true
  try {
    const res = await getCostSummary({ year: queryParams.year })
    costData.value = res.data
    buildCharts(res.data)
  } finally { loading.value = false }
}

function buildCharts(data: CostAccountVo) {
  const labels = data.periodList.map(item => item.periodLabel)
  const purchaseCosts = data.periodList.map(item => item.purchaseCost)
  const consumeCosts = data.periodList.map(item => item.consumeCost)
  const scrapCosts = data.periodList.map(item => item.scrapCost)
  const transferCosts = data.periodList.map(item => item.transferCost)
  costChartOption.value = {
    title: { text: '费用趋势', left: 'center' },
    tooltip: { trigger: 'axis' },
    legend: { data: ['采购入库', '消耗出库', '报废损失', '调拨费用'], bottom: 0 },
    xAxis: { type: 'category', data: labels },
    yAxis: { type: 'value', name: '费用' },
    series: [
      { name: '采购入库', type: 'bar', data: purchaseCosts },
      { name: '消耗出库', type: 'bar', data: consumeCosts },
      { name: '报废损失', type: 'bar', data: scrapCosts },
      { name: '调拨费用', type: 'bar', data: transferCosts }
    ]
  }

  const totalPurchase = data.periodList.reduce((sum, item) => sum + Number(item.purchaseCost || 0), 0)
  const totalConsume = data.periodList.reduce((sum, item) => sum + Number(item.consumeCost || 0), 0)
  const totalScrap = data.periodList.reduce((sum, item) => sum + Number(item.scrapCost || 0), 0)
  const totalTransfer = data.periodList.reduce((sum, item) => sum + Number(item.transferCost || 0), 0)
  costPieOption.value = {
    title: { text: '费用构成', left: 'center' },
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left', top: 'middle' },
    series: [{
      type: 'pie', radius: ['40%', '70%'], center: ['60%', '50%'],
      data: [
        { name: '采购入库', value: totalPurchase },
        { name: '消耗出库', value: totalConsume },
        { name: '报废损失', value: totalScrap },
        { name: '调拨费用', value: totalTransfer }
      ],
      emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.5)' } }
    }]
  }
}

async function loadConfig() {
  try {
    const res = await getCostConfig()
    const data = res.data as any
    if (data) {
      configEnabled.value = data.enabled
      queryParams.period = data.period || 'MONTHLY'
    }
  } catch { /* 首次可能无配置 */ }
}

async function handleToggleEnabled() {
  configLoading.value = true
  try {
    const data: CostAccountConfigDto = {
      enabled: configEnabled.value,
      year: queryParams.year,
      period: queryParams.period
    }
    await updateCostConfig(data)
    ElMessage.success(configEnabled.value ? '已启用费用核算' : '已停用费用核算')
    if (configEnabled.value) handleQuery()
  } catch { ElMessage.error('配置更新失败') } finally { configLoading.value = false }
}

onMounted(() => {
  loadConfig()
  handleQuery()
})
</script>

<template>
  <div class="app-container">
    <el-form :inline="true" class="search-form">
      <el-form-item label="核算年度">
        <el-select v-model="queryParams.year" style="width: 120px" @change="handleQuery">
          <el-option v-for="year in yearOptions" :key="year" :label="`${year}年`" :value="year" />
        </el-select>
      </el-form-item>
      <el-form-item label="核算周期">
        <el-select v-model="queryParams.period" style="width: 120px" @change="handleQuery">
          <el-option v-for="opt in periodOptions" :key="opt.value" :label="opt.label" :value="opt.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="启用核算">
        <el-switch v-model="configEnabled" :loading="configLoading" @change="handleToggleEnabled" />
      </el-form-item>
    </el-form>

    <el-row :gutter="20" class="summary-cards" v-loading="loading">
      <el-col :span="16">
        <el-card shadow="hover">
          <div class="card-value">{{ costData?.totalCost ?? '-' }}</div>
          <div class="card-label">年度总费用</div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="14"><el-card shadow="never"><Chart :option="costChartOption" height="350px" /></el-card></el-col>
      <el-col :span="10"><el-card shadow="never"><Chart :option="costPieOption" height="350px" /></el-card></el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 20px" v-if="costData?.periodList?.length">
      <template #header><span>各周期费用明细</span></template>
      <el-table :data="costData?.periodList || []" border show-summary>
        <el-table-column prop="periodLabel" label="周期" min-width="100" />
        <el-table-column prop="purchaseCost" label="采购入库费用" min-width="130" />
        <el-table-column prop="consumeCost" label="消耗出库费用" min-width="130" />
        <el-table-column prop="scrapCost" label="报废损失费用" min-width="130" />
        <el-table-column prop="transferCost" label="调拨费用" min-width="130" />
        <el-table-column prop="periodTotal" label="周期合计" min-width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.app-container { padding: 20px; }
.search-form { margin-bottom: 16px; }
.summary-cards {
  .card-value { font-size: 28px; font-weight: 600; color: var(--el-color-primary); text-align: center; }
  .card-label { font-size: 14px; color: #909399; text-align: center; margin-top: 8px; }
}
</style>

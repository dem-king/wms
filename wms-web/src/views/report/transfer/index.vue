<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Download } from '@element-plus/icons-vue'
import Chart from '@/components/Chart/index.vue'
import { getTransferSummary, getTransferTrend, getTransferCategoryDistribution, exportReportExcel, exportReportPdf } from '@/api/report'
import { downloadBlob, getExportFilename } from '@/utils/export'
import type { TransferSummaryVo, TransferTrendVo, TransferDistributionVo } from '@/types/report'

const loading = ref(false)
const summary = ref<TransferSummaryVo | null>(null)
const trendData = ref<TransferTrendVo | null>(null)
const distributionData = ref<TransferDistributionVo | null>(null)

const queryParams = reactive({
  startDate: '',
  endDate: '',
  fromWarehouseId: undefined as number | undefined,
  toWarehouseId: undefined as number | undefined
})

const dateRange = ref<[string, string]>(['', ''])
const trendChartOption = ref<Record<string, any>>({})
const distributionChartOption = ref<Record<string, any>>({})
const exporting = ref(false)

function buildParams() {
  return {
    startDate: dateRange.value[0] || '',
    endDate: dateRange.value[1] || '',
    fromWarehouseId: queryParams.fromWarehouseId,
    toWarehouseId: queryParams.toWarehouseId
  }
}

async function handleQuery() {
  if (!dateRange.value[0] || !dateRange.value[1]) { ElMessage.warning('请选择时间范围'); return }
  loading.value = true
  try {
    const params = buildParams()
    const [summaryRes, trendRes, distRes] = await Promise.all([
      getTransferSummary(params),
      getTransferTrend({ ...params, trendType: 'DAILY' }),
      getTransferCategoryDistribution(params)
    ])
    summary.value = summaryRes.data
    trendData.value = trendRes.data
    distributionData.value = distRes.data
    buildTrendChart(trendRes.data)
    buildDistributionChart(distRes.data)
  } finally { loading.value = false }
}

function buildTrendChart(data: TransferTrendVo) {
  const dates = data.trendList.map(item => item.date)
  const quantities = data.trendList.map(item => item.quantity)
  const amounts = data.trendList.map(item => item.amount)
  trendChartOption.value = {
    title: { text: '调拨趋势', left: 'center' },
    tooltip: { trigger: 'axis' },
    legend: { data: ['数量', '金额'], bottom: 0 },
    xAxis: { type: 'category', data: dates },
    yAxis: [{ type: 'value', name: '数量' }, { type: 'value', name: '金额' }],
    series: [
      { name: '数量', type: 'line', data: quantities, smooth: true },
      { name: '金额', type: 'line', yAxisIndex: 1, data: amounts, smooth: true }
    ]
  }
}

function buildDistributionChart(data: TransferDistributionVo) {
  const pieData = data.distributionList.map(item => ({ name: item.categoryName, value: item.quantity }))
  distributionChartOption.value = {
    title: { text: '调拨分类分布', left: 'center' },
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left', top: 'middle' },
    series: [{
      type: 'pie', radius: ['40%', '70%'], center: ['60%', '50%'], data: pieData,
      emphasis: { itemStyle: { shadowBlur: 10, shadowOffsetX: 0, shadowColor: 'rgba(0,0,0,0.5)' } }
    }]
  }
}

function handleReset() {
  dateRange.value = ['', '']
  queryParams.fromWarehouseId = undefined
  queryParams.toWarehouseId = undefined
  summary.value = null
  trendData.value = null
  distributionData.value = null
  trendChartOption.value = {}
  distributionChartOption.value = {}
}

async function handleExport(exportType: string) {
  if (!dateRange.value[0] || !dateRange.value[1]) { ElMessage.warning('请选择时间范围'); return }
  exporting.value = true
  try {
    const params = buildParams()
    const data = { ...params, reportType: 'transfer', exportType }
    const res = exportType === 'EXCEL' ? await exportReportExcel(data) : await exportReportPdf(data)
    const ext = exportType === 'EXCEL' ? 'xlsx' : 'pdf'
    downloadBlob(res as unknown as Blob, getExportFilename('transfer', ext))
    ElMessage.success('导出成功')
  } catch { ElMessage.error('导出失败') } finally { exporting.value = false }
}

onMounted(() => {
  const now = new Date()
  const start = new Date(now.getFullYear(), now.getMonth() - 1, 1)
  dateRange.value = [
    `${start.getFullYear()}-${String(start.getMonth() + 1).padStart(2, '0')}-${String(start.getDate()).padStart(2, '0')}`,
    `${now.getFullYear()}-${String(now.getMonth() + 1).padStart(2, '0')}-${String(now.getDate()).padStart(2, '0')}`
  ]
  handleQuery()
})
</script>

<template>
  <div class="app-container">
    <el-form :inline="true" class="search-form">
      <el-form-item label="时间范围">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item label="调出库房">
        <el-select v-model="queryParams.fromWarehouseId" placeholder="全部" clearable style="width: 180px" />
      </el-form-item>
      <el-form-item label="调入库房">
        <el-select v-model="queryParams.toWarehouseId" placeholder="全部" clearable style="width: 180px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5"><el-button type="success" plain :icon="Download" :loading="exporting" @click="handleExport('EXCEL')">导出Excel</el-button></el-col>
      <el-col :span="1.5"><el-button type="warning" plain :icon="Download" :loading="exporting" @click="handleExport('PDF')">导出PDF</el-button></el-col>
    </el-row>

    <el-row :gutter="20" class="summary-cards" v-loading="loading">
      <el-col :span="8"><el-card shadow="hover"><div class="card-value">{{ summary?.totalQuantity ?? '-' }}</div><div class="card-label">调拨总数量</div></el-card></el-col>
      <el-col :span="8"><el-card shadow="hover"><div class="card-value">{{ summary?.totalAmount ?? '-' }}</div><div class="card-label">调拨总金额</div></el-card></el-col>
      <el-col :span="8"><el-card shadow="hover"><div class="card-value">{{ summary?.orderCount ?? '-' }}</div><div class="card-label">调拨单据数</div></el-card></el-col>
    </el-row>

    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="14"><el-card shadow="never"><Chart :option="trendChartOption" height="350px" /></el-card></el-col>
      <el-col :span="10"><el-card shadow="never"><Chart :option="distributionChartOption" height="350px" /></el-card></el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 20px" v-if="summary?.categorySummaryList?.length">
      <template #header><span>调拨分类汇总明细</span></template>
      <el-table :data="summary?.categorySummaryList || []" border>
        <el-table-column prop="fromWarehouseName" label="调出库房" min-width="120" />
        <el-table-column prop="toWarehouseName" label="调入库房" min-width="120" />
        <el-table-column prop="categoryName" label="分类名称" min-width="120" />
        <el-table-column prop="quantity" label="数量" min-width="100" />
        <el-table-column prop="amount" label="金额" min-width="120" />
      </el-table>
    </el-card>
  </div>
</template>

<style scoped lang="scss">
.app-container { padding: 20px; }
.search-form { margin-bottom: 16px; }
.mb8 { margin-bottom: 8px; }
.summary-cards {
  .card-value { font-size: 28px; font-weight: 600; color: var(--el-color-primary); text-align: center; }
  .card-label { font-size: 14px; color: #909399; text-align: center; margin-top: 8px; }
}
</style>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import Chart from '@/components/Chart/index.vue'
import { getOverdueReturnPage } from '@/api/monitor'
import type { OverdueReturnVo } from '@/types/monitor'

const loading = ref(false)
const tableData = ref<OverdueReturnVo[]>([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 20,
  status: undefined as string | undefined,
  alertLevel: undefined as string | undefined
})

const trendChartOption = ref<Record<string, any>>({})

async function handleQuery() {
  loading.value = true
  try {
    const res = await getOverdueReturnPage(queryParams)
    const data = res.data as any
    tableData.value = Array.isArray(data) ? data : (data?.records || [])
    total.value = Array.isArray(data) ? data.length : (data?.total || 0)
    buildTrendChart()
  } finally { loading.value = false }
}

function buildTrendChart() {
  const sortedByDays = [...tableData.value].sort((a, b) => a.overdueDays - b.overdueDays)
  const dayMap = new Map<number, number>()
  sortedByDays.forEach(item => {
    dayMap.set(item.overdueDays, (dayMap.get(item.overdueDays) || 0) + 1)
  })
  const days = Array.from(dayMap.keys()).sort((a, b) => a - b)
  const counts = days.map(d => dayMap.get(d) || 0)
  trendChartOption.value = {
    title: { text: '逾期天数分布', left: 'center' },
    tooltip: { trigger: 'axis' },
    xAxis: { type: 'category', data: days.map(d => `${d}天`), name: '逾期天数' },
    yAxis: { type: 'value', name: '记录数' },
    series: [{ type: 'bar', data: counts, itemStyle: { color: '#e6a23c' } }]
  }
}

function handleReset() {
  queryParams.page = 1
  queryParams.status = undefined
  queryParams.alertLevel = undefined
  handleQuery()
}

function handlePageChange() {
  handleQuery()
}

function getAlertLevelTag(level: string) {
  if (level === 'URGENT') return 'danger'
  if (level === 'IMPORTANT') return 'warning'
  return 'primary'
}

function getAlertLevelLabel(level: string) {
  if (level === 'URGENT') return '紧急'
  if (level === 'IMPORTANT') return '重要'
  return '普通'
}

function getStatusLabel(status: string) {
  if (status === 'PENDING') return '待处理'
  if (status === 'RESOLVED') return '已归还'
  return status
}

function getStatusTag(status: string) {
  if (status === 'PENDING') return 'danger'
  if (status === 'RESOLVED') return 'success'
  return 'info'
}

onMounted(() => { handleQuery() })
</script>

<template>
  <div class="app-container">
    <el-form :inline="true" class="search-form">
      <el-form-item label="提醒级别">
        <el-select v-model="queryParams.alertLevel" placeholder="全部" clearable style="width: 160px">
          <el-option label="普通" value="NORMAL" />
          <el-option label="重要" value="IMPORTANT" />
          <el-option label="紧急" value="URGENT" />
        </el-select>
      </el-form-item>
      <el-form-item label="处理状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 160px">
          <el-option label="待处理" value="PENDING" />
          <el-option label="已归还" value="RESOLVED" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="20">
      <el-col :span="16">
        <el-table v-loading="loading" :data="tableData" border>
          <el-table-column prop="borrowerName" label="借用人" min-width="100" />
          <el-table-column prop="itemName" label="物品名称" min-width="120" />
          <el-table-column prop="borrowQuantity" label="借出数量" min-width="80" />
          <el-table-column prop="borrowTime" label="借出时间" min-width="160" />
          <el-table-column prop="expectedReturnDate" label="预计归还" min-width="120" />
          <el-table-column prop="overdueDays" label="逾期天数" min-width="80" sortable />
          <el-table-column prop="alertLevel" label="提醒级别" min-width="80">
            <template #default="{ row }">
              <el-tag :type="getAlertLevelTag(row.alertLevel)">{{ getAlertLevelLabel(row.alertLevel) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" min-width="80">
            <template #default="{ row }">
              <el-tag :type="getStatusTag(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
            </template>
          </el-table-column>
        </el-table>

        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          class="pagination"
          @change="handlePageChange"
        />
      </el-col>
      <el-col :span="8">
        <el-card shadow="never">
          <Chart :option="trendChartOption" height="400px" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<style scoped lang="scss">
.app-container { padding: 20px; }
.search-form { margin-bottom: 16px; }
.pagination { margin-top: 16px; justify-content: flex-end; }
</style>

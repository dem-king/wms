<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import { getStockAlertPage, resolveStockAlert } from '@/api/monitor'
import type { StockAlertVo } from '@/types/monitor'

const loading = ref(false)
const tableData = ref<StockAlertVo[]>([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 20,
  status: undefined as string | undefined,
  alertType: undefined as string | undefined
})

const resolveLoading = ref(false)

async function handleQuery() {
  loading.value = true
  try {
    const res = await getStockAlertPage(queryParams)
    const data = res.data as any
    tableData.value = Array.isArray(data) ? data : (data?.records || [])
    total.value = Array.isArray(data) ? data.length : (data?.total || 0)
  } finally { loading.value = false }
}

function handleReset() {
  queryParams.page = 1
  queryParams.status = undefined
  queryParams.alertType = undefined
  handleQuery()
}

function handlePageChange() {
  handleQuery()
}

async function handleResolve(row: StockAlertVo) {
  try {
    await ElMessageBox.confirm('确认处理该预警记录？', '提示', { type: 'warning' })
    resolveLoading.value = true
    await resolveStockAlert(row.id)
    ElMessage.success('处理成功')
    handleQuery()
  } catch { /* 取消 */ } finally { resolveLoading.value = false }
}

function getAlertTypeTag(type: string) {
  return type === 'STOCK_LOW' ? 'danger' : 'warning'
}

function getAlertTypeLabel(type: string) {
  return type === 'STOCK_LOW' ? '库存不足' : '库存超储'
}

function getStatusTag(status: string) {
  if (status === 'PENDING') return 'danger'
  if (status === 'RESOLVED') return 'success'
  return 'info'
}

function getStatusLabel(status: string) {
  if (status === 'PENDING') return '待处理'
  if (status === 'RESOLVED') return '已处理'
  return status
}

onMounted(() => { handleQuery() })
</script>

<template>
  <div class="app-container">
    <el-form :inline="true" class="search-form">
      <el-form-item label="预警类型">
        <el-select v-model="queryParams.alertType" placeholder="全部" clearable style="width: 160px">
          <el-option label="库存不足" value="STOCK_LOW" />
          <el-option label="库存超储" value="STOCK_HIGH" />
        </el-select>
      </el-form-item>
      <el-form-item label="处理状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 160px">
          <el-option label="待处理" value="PENDING" />
          <el-option label="已处理" value="RESOLVED" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="tableData" border>
      <el-table-column prop="itemName" label="物品名称" min-width="140" />
      <el-table-column prop="itemCode" label="物品编码" min-width="120" />
      <el-table-column prop="warehouseName" label="库房" min-width="120" />
      <el-table-column prop="currentQuantity" label="当前库存" min-width="100" />
      <el-table-column prop="thresholdValue" label="触发阈值" min-width="100" />
      <el-table-column prop="alertType" label="预警类型" min-width="100">
        <template #default="{ row }">
          <el-tag :type="getAlertTypeTag(row.alertType)">{{ getAlertTypeLabel(row.alertType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="处理状态" min-width="100">
        <template #default="{ row }">
          <el-tag :type="getStatusTag(row.status)">{{ getStatusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="triggerTime" label="触发时间" min-width="160" />
      <el-table-column label="操作" class-name="table-action-column" fixed="right" min-width="180">
        <template #default="{ row }">
          <TableActionGroup
            :actions="[
              { label: '处理', visible: row.status === 'PENDING', onClick: () => handleResolve(row) },
            ]"
          />
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
  </div>
</template>

<style scoped lang="scss">
.app-container { padding: 20px; }
.search-form { margin-bottom: 16px; }
.pagination { margin-top: 16px; justify-content: flex-end; }
</style>

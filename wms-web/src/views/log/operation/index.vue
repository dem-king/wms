<script setup lang="ts">
import { ref, reactive, onMounted, onActivated } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getOperLogPage } from '@/api/system/log'
import type { SysOperLogVo } from '@/types/system-log'
import { normalizePageTotal } from '@/utils/pagination'

const loading = ref(false)
const tableData = ref<SysOperLogVo[]>([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 20,
  module: undefined as string | undefined,
  type: undefined as string | undefined,
  operatorName: undefined as string | undefined,
  startDate: undefined as string | undefined,
  endDate: undefined as string | undefined
})

const dateRange = ref<[string, string]>(['', ''])
const detailVisible = ref(false)
const currentRow = ref<SysOperLogVo | null>(null)

async function handleQuery() {
  loading.value = true
  try {
    const params = {
      ...queryParams,
      startDate: dateRange.value[0] || undefined,
      endDate: dateRange.value[1] || undefined
    }
    const res = await getOperLogPage(params)
    const data = res.data as any
    tableData.value = Array.isArray(data) ? data : (data?.records || [])
    total.value = normalizePageTotal(Array.isArray(data) ? data.length : data?.total)
  } finally { loading.value = false }
}

function handleReset() {
  queryParams.page = 1
  queryParams.module = undefined
  queryParams.type = undefined
  queryParams.operatorName = undefined
  dateRange.value = ['', '']
  handleQuery()
}

function handlePageChange() {
  handleQuery()
}

function handleDetail(row: SysOperLogVo) {
  currentRow.value = row
  detailVisible.value = true
}

function getStatusTag(status: string) {
  if (status === 'SUCCESS') return 'success'
  if (status === 'FAIL') return 'danger'
  return 'info'
}

function getStatusLabel(status: string) {
  if (status === 'SUCCESS') return '成功'
  if (status === 'FAIL') return '失败'
  return status
}

onMounted(() => { handleQuery() })
onActivated(() => { handleQuery() })
</script>

<template>
  <div class="app-container list-page">
    <el-form :inline="true" class="search-form">
      <el-form-item label="操作模块">
        <el-input v-model="queryParams.module" placeholder="请输入模块" clearable />
      </el-form-item>
      <el-form-item label="操作类型">
        <el-input v-model="queryParams.type" placeholder="请输入类型" clearable />
      </el-form-item>
      <el-form-item label="操作人">
        <el-input v-model="queryParams.operatorName" placeholder="请输入操作人" clearable />
      </el-form-item>
      <el-form-item label="时间范围">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-section">
      <el-table v-loading="loading" :data="tableData" border height="100%">
      <el-table-column prop="operatorName" label="操作人" min-width="90" />
      <el-table-column prop="module" label="操作模块" min-width="100" />
      <el-table-column prop="type" label="操作类型" min-width="80" />
      <el-table-column prop="desc" label="操作描述" min-width="140" show-overflow-tooltip />
      <el-table-column prop="requestMethod" label="请求方法" min-width="80" />
      <el-table-column prop="requestUrl" label="请求URL" min-width="180" show-overflow-tooltip />
      <el-table-column prop="costTime" label="耗时(ms)" min-width="80" />
      <el-table-column prop="status" label="状态" min-width="70">
        <template #default="{ row }">
          <el-tag :type="getStatusTag(row.status)" size="small">{{ getStatusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="operTime" label="操作时间" min-width="160" />
      <el-table-column label="操作" class-name="table-action-column" fixed="right" min-width="80">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handleDetail(row)">详情</el-button>
        </template>
      </el-table-column>
    </el-table>

      <div class="pagination-container">
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
    </div>

    <el-dialog v-model="detailVisible" title="操作日志详情" width="700px">
      <el-descriptions :column="2" border v-if="currentRow">
        <el-descriptions-item label="操作人">{{ currentRow.operatorName }}</el-descriptions-item>
        <el-descriptions-item label="操作模块">{{ currentRow.module }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">{{ currentRow.type }}</el-descriptions-item>
        <el-descriptions-item label="操作描述">{{ currentRow.desc }}</el-descriptions-item>
        <el-descriptions-item label="请求方法">{{ currentRow.requestMethod }}</el-descriptions-item>
        <el-descriptions-item label="请求URL" :span="2">{{ currentRow.requestUrl }}</el-descriptions-item>
        <el-descriptions-item label="操作IP">{{ currentRow.operIp }}</el-descriptions-item>
        <el-descriptions-item label="耗时">{{ currentRow.costTime }}ms</el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <pre class="json-pre">{{ currentRow.requestParams }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="响应结果" :span="2">
          <pre class="json-pre">{{ currentRow.responseResult }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="异常信息" :span="2" v-if="currentRow.errorMsg">
          <pre class="json-pre error-text">{{ currentRow.errorMsg }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.app-container.list-page {
  padding: 20px;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.table-section {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.pagination-container {
  margin-top: 16px;
  flex-shrink: 0;
}
.search-form { margin-bottom: 16px; }
.pagination {
  justify-content: flex-end;
}
.json-pre { max-height: 200px; overflow: auto; white-space: pre-wrap; word-break: break-all; font-size: 12px; margin: 0; }
.error-text { color: var(--el-color-danger); }
</style>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getLoginLogPage } from '@/api/system/log'
import type { SysLoginLogVo } from '@/types/system-log'
import { normalizePageTotal } from '@/utils/pagination'

const loading = ref(false)
const tableData = ref<SysLoginLogVo[]>([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 20,
  username: undefined as string | undefined,
  status: undefined as string | undefined,
  startDate: undefined as string | undefined,
  endDate: undefined as string | undefined
})

const dateRange = ref<[string, string]>(['', ''])

async function handleQuery() {
  loading.value = true
  try {
    const params = {
      ...queryParams,
      startDate: dateRange.value[0] || undefined,
      endDate: dateRange.value[1] || undefined
    }
    const res = await getLoginLogPage(params)
    const data = res.data as any
    tableData.value = Array.isArray(data) ? data : (data?.records || [])
    total.value = normalizePageTotal(Array.isArray(data) ? data.length : data?.total)
  } finally { loading.value = false }
}

function handleReset() {
  queryParams.page = 1
  queryParams.username = undefined
  queryParams.status = undefined
  dateRange.value = ['', '']
  handleQuery()
}

function handlePageChange() {
  handleQuery()
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
</script>

<template>
  <div class="app-container list-page">
    <el-form :inline="true" class="search-form">
      <el-form-item label="用户名">
        <el-input v-model="queryParams.username" placeholder="请输入用户名" clearable />
      </el-form-item>
      <el-form-item label="登录结果">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 120px">
          <el-option label="成功" value="SUCCESS" />
          <el-option label="失败" value="FAIL" />
        </el-select>
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
      <el-table-column prop="username" label="用户名" min-width="100" />
      <el-table-column prop="loginIp" label="登录IP" min-width="130" />
      <el-table-column prop="loginLocation" label="登录地点" min-width="140" />
      <el-table-column prop="browser" label="浏览器" min-width="120" />
      <el-table-column prop="os" label="操作系统" min-width="120" />
      <el-table-column prop="status" label="登录结果" min-width="80">
        <template #default="{ row }">
          <el-tag :type="getStatusTag(row.status)" size="small">{{ getStatusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="failReason" label="失败原因" min-width="140" show-overflow-tooltip />
      <el-table-column prop="loginTime" label="登录时间" min-width="160" />
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
</style>

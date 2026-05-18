<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true" class="search-form">
      <el-form-item label="物品名称">
        <el-input v-model="queryParams.itemName" placeholder="请输入物品名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="库房">
        <el-select v-model="queryParams.warehouseId" placeholder="请选择库房" clearable>
          <el-option v-for="w in warehouseList" :key="w.id" :label="w.warehouseName" :value="w.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="预警筛选">
        <el-select v-model="queryParams.isAlert" placeholder="全部" clearable>
          <el-option label="仅预警" :value="true" />
          <el-option label="非预警" :value="false" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="tableData" border :row-class-name="tableRowClassName">
      <el-table-column prop="itemCode" label="物品编码" min-width="120" />
      <el-table-column prop="itemName" label="物品名称" min-width="150" />
      <el-table-column prop="warehouseName" label="库房" min-width="120" />
      <el-table-column prop="binCode" label="库位" min-width="100" />
      <el-table-column prop="quantity" label="数量" min-width="80" />
      <el-table-column prop="safetyStock" label="安全库存" min-width="100" />
      <el-table-column label="状态" min-width="80">
        <template #default="{ row }">
          <el-tag :type="row.isAlert ? 'danger' : 'success'">{{ row.isAlert ? '预警' : '正常' }}</el-tag>
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
      @size-change="handleQuery"
      @current-change="handleQuery"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Search, Refresh } from '@element-plus/icons-vue'
import { getStockList } from '@/api/item/stock'
import { getWarehouseList } from '@/api/warehouse/warehouse'
import type { WmsStockVo } from '@/types/item'
import type { WmsWarehouseVo } from '@/types/warehouse'

const loading = ref(false)
const tableData = ref<WmsStockVo[]>([])
const total = ref(0)
const warehouseList = ref<WmsWarehouseVo[]>([])

const queryParams = reactive({
  page: 1,
  size: 20,
  itemName: '',
  warehouseId: undefined as number | undefined,
  isAlert: undefined as boolean | undefined
})

async function handleQuery() {
  loading.value = true
  try {
    const res = await getStockList(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryParams.itemName = ''
  queryParams.warehouseId = undefined
  queryParams.isAlert = undefined
  queryParams.page = 1
  handleQuery()
}

function tableRowClassName({ row }: { row: WmsStockVo }) {
  return row.isAlert ? 'alert-row' : ''
}

onMounted(async () => {
  const res = await getWarehouseList()
  warehouseList.value = res.data
  handleQuery()
})
</script>

<style scoped lang="scss">
.app-container {
  padding: 20px;
}

.search-form {
  margin-bottom: 16px;
}

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}

:deep(.alert-row) {
  background-color: #fef0f0 !important;
}
</style>

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
      <el-table-column prop="stockLowerLimit" label="安全库存" min-width="100" />
      <el-table-column prop="stockUpperLimit" label="最大库存" min-width="100" />
      <el-table-column label="状态" min-width="80">
        <template #default="{ row }">
          <el-tag :type="row.alert ? 'danger' : 'success'">{{ row.alert ? '预警' : '正常' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" class-name="table-action-column" fixed="right" min-width="180">
        <template #default="{ row }">
          <TableActionGroup
            :actions="[
              { label: '阈值设置', type: 'primary', permission: 'item:stock:edit', onClick: () => handleEditThreshold(row) },
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
      @size-change="handleQuery"
      @current-change="handleQuery"
    />

    <el-dialog v-model="thresholdDialogVisible" title="预警阈值设置" width="450px" @close="resetThresholdForm">
      <el-form ref="thresholdFormRef" :model="thresholdForm" label-width="100px">
        <el-form-item label="物品">
          <span>{{ thresholdForm.itemName }} ({{ thresholdForm.itemCode }})</span>
        </el-form-item>
        <el-form-item label="安全库存(下限)" prop="stockLowerLimit">
          <el-input-number v-model="thresholdForm.stockLowerLimit" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="最大库存(上限)" prop="stockUpperLimit">
          <el-input-number v-model="thresholdForm.stockUpperLimit" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="补货阈值" prop="replenishThreshold">
          <el-input-number v-model="thresholdForm.replenishThreshold" :min="0" controls-position="right" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="thresholdDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="thresholdLoading" @click="handleThresholdSubmit">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance } from 'element-plus'
import { ElMessage } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import { getStockList, updateThreshold } from '@/api/item/stock'
import { getWarehouseList } from '@/api/warehouse/warehouse'
import type { EntityId, WmsStockVo } from '@/types/item'
import type { WmsWarehouseVo } from '@/types/warehouse'

const loading = ref(false)
const tableData = ref<WmsStockVo[]>([])
const total = ref(0)
const warehouseList = ref<WmsWarehouseVo[]>([])

const queryParams = reactive({
  page: 1,
  size: 20,
  itemName: '',
  warehouseId: undefined as EntityId | undefined,
  isAlert: undefined as boolean | undefined
})

const thresholdDialogVisible = ref(false)
const thresholdLoading = ref(false)
const thresholdFormRef = ref<FormInstance>()
const thresholdForm = reactive({
  itemId: '',
  itemCode: '',
  itemName: '',
  stockLowerLimit: 0,
  stockUpperLimit: 0,
  replenishThreshold: 0
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
  return row.alert ? 'alert-row' : ''
}

function handleEditThreshold(row: WmsStockVo) {
  Object.assign(thresholdForm, {
    itemId: row.itemId,
    itemCode: row.itemCode,
    itemName: row.itemName,
    stockLowerLimit: row.stockLowerLimit ?? 0,
    stockUpperLimit: row.stockUpperLimit ?? 0,
    replenishThreshold: 0
  })
  thresholdDialogVisible.value = true
}

async function handleThresholdSubmit() {
  thresholdLoading.value = true
  try {
    await updateThreshold(thresholdForm.itemId, {
      stockLowerLimit: thresholdForm.stockLowerLimit,
      stockUpperLimit: thresholdForm.stockUpperLimit,
      replenishThreshold: thresholdForm.replenishThreshold
    })
    ElMessage.success('阈值设置成功')
    thresholdDialogVisible.value = false
    handleQuery()
  } finally {
    thresholdLoading.value = false
  }
}

function resetThresholdForm() {
  thresholdFormRef.value?.resetFields()
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

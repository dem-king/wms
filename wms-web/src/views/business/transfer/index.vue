<template>
  <div class="app-container list-page">
    <el-form :model="queryParams" :inline="true" class="search-form">
      <el-form-item label="单号">
        <el-input v-model="queryParams.orderNo" placeholder="请输入单号" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="s in statusOptions" :key="s.value" :label="s.label" :value="s.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button v-if="userStore.hasPermission('business:transfer:add')" type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
    </el-row>

    <div class="table-section">
      <el-table v-loading="loading" :data="tableData" border height="100%">
      <el-table-column prop="orderNo" label="调拨单号" min-width="160" />
      <el-table-column prop="fromWarehouseName" label="调出库房" min-width="120" />
      <el-table-column prop="toWarehouseName" label="调入库房" min-width="120" />
      <el-table-column prop="status" label="状态" min-width="100">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="160" />
      <el-table-column label="操作" class-name="table-action-column" fixed="right" min-width="180">
        <template #default="{ row }">
          <TableActionGroup
            :actions="[
              { label: '查看', type: 'primary', icon: View, onClick: () => handleView(row) },
              { label: '编辑', type: 'primary', icon: Edit, permission: 'business:transfer:edit', visible: row.status === 'DRAFT', onClick: () => handleEdit(row) },
              { label: '提交', type: 'warning', permission: 'business:transfer:submit', visible: row.status === 'DRAFT', onClick: () => handleSubmitOrder(row) },
              { label: '删除', type: 'danger', icon: Delete, permission: 'business:transfer:delete', visible: row.status === 'DRAFT', confirmText: '确定删除该调拨单吗？', onClick: () => handleDelete(row.id) },
            ]"
          />
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
      @size-change="handleQuery"
      @current-change="handleQuery"
    />
      </div>
    </div>

    <TransferForm v-model:visible="formVisible" :is-edit="isEdit" :form-data="currentRow" @success="handleQuery" />

    <el-dialog v-model="detailVisible" title="调拨单详情" width="700px">
      <el-descriptions :column="2" border v-if="viewRow">
        <el-descriptions-item label="调拨单号">{{ viewRow.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="调出库房">{{ viewRow.fromWarehouseName }}</el-descriptions-item>
        <el-descriptions-item label="调入库房">{{ viewRow.toWarehouseName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(viewRow.status)">{{ statusLabel(viewRow.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewRow.remark }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="viewRow?.details || []" border style="margin-top: 16px">
        <el-table-column prop="itemCode" label="物品编码" min-width="120" />
        <el-table-column prop="itemName" label="物品名称" min-width="150" />
        <el-table-column prop="quantity" label="调拨数量" min-width="100" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete, View } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import { useUserStore } from '@/store/modules/user'
import { getTransferOrders, getTransferOrder, submitTransferOrder, deleteTransferOrder } from '@/api/business/transfer'
import type { TransferOrderVo, OrderStatus } from '@/types/business'
import TransferForm from './components/TransferForm.vue'
import { normalizePageTotal } from '@/utils/pagination'

const statusOptions = [
  { label: '草稿', value: 'DRAFT' },
  { label: '待审核', value: 'PENDING_REVIEW' },
  { label: '已审核', value: 'APPROVED' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '已驳回', value: 'REJECTED' },
]

const userStore = useUserStore()

const loading = ref(false)
const tableData = ref<TransferOrderVo[]>([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 20,
  orderNo: '',
  status: undefined as string | undefined,
})

const formVisible = ref(false)
const isEdit = ref(false)
const currentRow = ref<TransferOrderVo | null>(null)
const detailVisible = ref(false)
const viewRow = ref<TransferOrderVo | null>(null)

type TagType = 'info' | 'warning' | 'success' | 'danger'

function statusTagType(status: OrderStatus): TagType {
  const map: Record<OrderStatus, TagType> = { DRAFT: 'info', PENDING_REVIEW: 'warning', APPROVED: 'success', COMPLETED: 'success', REJECTED: 'danger' }
  return map[status]
}

function statusLabel(status: OrderStatus) {
  const map: Record<string, string> = { DRAFT: '草稿', PENDING_REVIEW: '待审核', APPROVED: '已审核', COMPLETED: '已完成', REJECTED: '已驳回' }
  return map[status] || status
}

async function handleQuery() {
  loading.value = true
  try {
    const res = await getTransferOrders(queryParams)
    tableData.value = res.data.records
    total.value = normalizePageTotal(res.data.total)
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryParams.orderNo = ''
  queryParams.status = undefined
  queryParams.page = 1
  handleQuery()
}

function handleAdd() {
  isEdit.value = false
  currentRow.value = null
  formVisible.value = true
}

function handleEdit(row: TransferOrderVo) {
  isEdit.value = true
  currentRow.value = { ...row }
  formVisible.value = true
}

async function handleView(row: TransferOrderVo) {
  const res = await getTransferOrder(row.id)
  viewRow.value = res.data
  detailVisible.value = true
}

async function handleSubmitOrder(row: TransferOrderVo) {
  await ElMessageBox.confirm('确定提交该调拨单吗？', '提示', { type: 'warning' })
  await submitTransferOrder(row.id)
  ElMessage.success('提交成功')
  handleQuery()
}

async function handleDelete(id: TransferOrderVo['id']) {
  await deleteTransferOrder(id)
  ElMessage.success('删除成功')
  handleQuery()
}

onMounted(() => {
  handleQuery()
})
</script>

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

.search-form {
  margin-bottom: 16px;
}

.mb8 {
  margin-bottom: 8px;
}

.pagination {
  justify-content: flex-end;
}
</style>

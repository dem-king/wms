<template>
  <div class="app-container">
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
        <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="tableData" border>
      <el-table-column prop="orderNo" label="归还单号" min-width="160" />
      <el-table-column prop="outboundOrderNo" label="关联出库单" min-width="160" />
      <el-table-column prop="recipient" label="领用人" min-width="100" />
      <el-table-column prop="status" label="状态" min-width="100">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="160" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" text :icon="View" @click="handleView(row)">查看</el-button>
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

    <el-dialog v-model="formVisible" title="新增归还单" width="800px" @close="handleClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="关联出库单" prop="outboundOrderId">
          <el-select v-model="form.outboundOrderId" placeholder="请选择出库单" style="width: 100%" @change="handleOutboundChange">
            <el-option v-for="o in outboundList" :key="o.id" :label="`${o.orderNo} - ${o.recipient}`" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>

      <el-divider content-position="left">归还明细</el-divider>
      <el-row class="mb8">
        <el-button type="primary" plain :icon="Plus" @click="addDetailRow">新增行</el-button>
      </el-row>
      <el-table :data="form.details" border>
        <el-table-column label="物品" min-width="200">
          <template #default="{ row }">
            <el-select v-model="row.itemId" placeholder="请选择物品" filterable>
              <el-option v-for="item in outboundItems" :key="item.itemId" :label="`${item.itemCode} - ${item.itemName}`" :value="item.itemId" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="归还数量" min-width="150">
          <template #default="{ row }">
            <el-input-number v-model="row.quantity" :min="1" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="80" fixed="right">
          <template #default="{ $index }">
            <el-button type="danger" text :icon="Delete" @click="form.details.splice($index, 1)" />
          </template>
        </el-table-column>
      </el-table>

      <template #footer>
        <el-button @click="handleClose">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="detailVisible" title="归还单详情" width="700px">
      <el-descriptions :column="2" border v-if="viewRow">
        <el-descriptions-item label="归还单号">{{ viewRow.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="关联出库单">{{ viewRow.outboundOrderNo }}</el-descriptions-item>
        <el-descriptions-item label="领用人">{{ viewRow.recipient }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(viewRow.status)">{{ statusLabel(viewRow.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewRow.remark }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="viewRow?.details || []" border style="margin-top: 16px">
        <el-table-column prop="itemCode" label="物品编码" min-width="120" />
        <el-table-column prop="itemName" label="物品名称" min-width="150" />
        <el-table-column prop="quantity" label="归还数量" min-width="100" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Plus, Delete, View } from '@element-plus/icons-vue'
import { getReturnOrders, getReturnOrder, addReturnOrder } from '@/api/business/return'
import { getOutboundOrders } from '@/api/business/outbound'
import type { ReturnOrderVo, ReturnDetailDto, OrderStatus } from '@/types/business'
import type { OutboundOrderVo, OutboundDetailVo } from '@/types/business'

const statusOptions = [
  { label: '草稿', value: 'DRAFT' },
  { label: '待审核', value: 'PENDING_REVIEW' },
  { label: '已审核', value: 'APPROVED' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '已驳回', value: 'REJECTED' },
]

const loading = ref(false)
const tableData = ref<ReturnOrderVo[]>([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 20,
  orderNo: '',
  status: undefined as string | undefined,
})

const formVisible = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const outboundList = ref<OutboundOrderVo[]>([])
const outboundItems = ref<OutboundDetailVo[]>([])

const form = reactive<{
  outboundOrderId: number | undefined
  remark: string
  details: ReturnDetailDto[]
}>({
  outboundOrderId: undefined,
  remark: '',
  details: [],
})

const rules: FormRules = {
  outboundOrderId: [{ required: true, message: '请选择出库单', trigger: 'change' }],
}

const detailVisible = ref(false)
const viewRow = ref<ReturnOrderVo | null>(null)

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
    const res = await getReturnOrders(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
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

async function handleAdd() {
  const res = await getOutboundOrders({ page: 1, size: 1000, status: 'COMPLETED' })
  outboundList.value = res.data.records
  formVisible.value = true
}

async function handleOutboundChange(orderId: number) {
  const order = outboundList.value.find(o => o.id === orderId)
  outboundItems.value = order?.details || []
  form.details = outboundItems.value.map(d => ({ itemId: d.itemId, quantity: d.quantity }))
}

function addDetailRow() {
  form.details.push({ itemId: undefined as unknown as number, quantity: 1 })
}

async function handleSubmit() {
  await formRef.value?.validate()
  if (form.details.length === 0) {
    ElMessage.warning('请添加归还明细')
    return
  }
  submitLoading.value = true
  try {
    await addReturnOrder({ outboundOrderId: form.outboundOrderId!, remark: form.remark, details: form.details })
    ElMessage.success('新增成功')
    handleClose()
    handleQuery()
  } finally {
    submitLoading.value = false
  }
}

async function handleView(row: ReturnOrderVo) {
  const res = await getReturnOrder(row.id)
  viewRow.value = res.data
  detailVisible.value = true
}

function handleClose() {
  formVisible.value = false
  formRef.value?.resetFields()
  Object.assign(form, { outboundOrderId: undefined, remark: '', details: [] })
  outboundItems.value = []
}

onMounted(() => {
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

.mb8 {
  margin-bottom: 8px;
}

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
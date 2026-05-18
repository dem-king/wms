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
      <el-table-column prop="orderNo" label="报废单号" min-width="160" />
      <el-table-column prop="warehouseName" label="库房" min-width="120" />
      <el-table-column prop="scrapReason" label="报废原因" min-width="150" show-overflow-tooltip />
      <el-table-column prop="status" label="状态" min-width="100">
        <template #default="{ row }">
          <el-tag :type="statusTagType(row.status)">{{ statusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="160" />
      <el-table-column label="操作" width="240" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" text :icon="View" @click="handleView(row)">查看</el-button>
          <el-button v-if="row.status === 'DRAFT'" type="warning" text @click="handleSubmitOrder(row)">提交</el-button>
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

    <el-dialog v-model="formVisible" title="新增报废单" width="800px" @close="handleClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="库房" prop="warehouseId">
          <el-select v-model="form.warehouseId" placeholder="请选择库房" style="width: 100%">
            <el-option v-for="w in warehouseList" :key="w.id" :label="w.warehouseName" :value="w.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="报废原因" prop="scrapReason">
          <el-input v-model="form.scrapReason" type="textarea" :rows="2" placeholder="请输入报废原因" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>

      <el-divider content-position="left">报废明细</el-divider>
      <el-row class="mb8">
        <el-button type="primary" plain :icon="Plus" @click="addDetailRow">新增行</el-button>
      </el-row>
      <el-table :data="form.details" border>
        <el-table-column label="物品" min-width="200">
          <template #default="{ row }">
            <el-select v-model="row.itemId" placeholder="请选择物品" filterable>
              <el-option v-for="item in itemList" :key="item.id" :label="`${item.itemCode} - ${item.itemName}`" :value="item.id" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="报废数量" min-width="150">
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

    <el-dialog v-model="detailVisible" title="报废单详情" width="700px">
      <el-descriptions :column="2" border v-if="viewRow">
        <el-descriptions-item label="报废单号">{{ viewRow.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="库房">{{ viewRow.warehouseName }}</el-descriptions-item>
        <el-descriptions-item label="报废原因" :span="2">{{ viewRow.scrapReason }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(viewRow.status)">{{ statusLabel(viewRow.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ viewRow.remark }}</el-descriptions-item>
      </el-descriptions>
      <el-table :data="viewRow?.details || []" border style="margin-top: 16px">
        <el-table-column prop="itemCode" label="物品编码" min-width="120" />
        <el-table-column prop="itemName" label="物品名称" min-width="150" />
        <el-table-column prop="quantity" label="报废数量" min-width="100" />
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, View } from '@element-plus/icons-vue'
import { getScrapOrders, getScrapOrder, addScrapOrder, submitScrapOrder } from '@/api/business/scrap'
import { getWarehouseList } from '@/api/warehouse/warehouse'
import { getItemList } from '@/api/item/item'
import type { ScrapOrderVo, ScrapDetailDto, OrderStatus } from '@/types/business'
import type { WmsWarehouseVo } from '@/types/warehouse'
import type { WmsItemVo } from '@/types/item'

const statusOptions = [
  { label: '草稿', value: 'DRAFT' },
  { label: '待审核', value: 'PENDING_REVIEW' },
  { label: '已审核', value: 'APPROVED' },
  { label: '已完成', value: 'COMPLETED' },
  { label: '已驳回', value: 'REJECTED' },
]

const loading = ref(false)
const tableData = ref<ScrapOrderVo[]>([])
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
const warehouseList = ref<WmsWarehouseVo[]>([])
const itemList = ref<WmsItemVo[]>([])

const form = reactive<{
  warehouseId: number | undefined
  scrapReason: string
  remark: string
  details: ScrapDetailDto[]
}>({
  warehouseId: undefined,
  scrapReason: '',
  remark: '',
  details: [],
})

const rules: FormRules = {
  warehouseId: [{ required: true, message: '请选择库房', trigger: 'change' }],
  scrapReason: [{ required: true, message: '请输入报废原因', trigger: 'blur' }],
}

const detailVisible = ref(false)
const viewRow = ref<ScrapOrderVo | null>(null)

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
    const res = await getScrapOrders(queryParams)
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
  const [whRes, iRes] = await Promise.all([getWarehouseList(), getItemList({ page: 1, size: 1000, status: 1 })])
  warehouseList.value = whRes.data
  itemList.value = iRes.data.records
  formVisible.value = true
}

function addDetailRow() {
  form.details.push({ itemId: undefined as unknown as number, quantity: 1 })
}

async function handleSubmit() {
  await formRef.value?.validate()
  if (form.details.length === 0) {
    ElMessage.warning('请添加报废明细')
    return
  }
  submitLoading.value = true
  try {
    await addScrapOrder({ warehouseId: form.warehouseId!, scrapReason: form.scrapReason, remark: form.remark, details: form.details })
    ElMessage.success('新增成功')
    handleClose()
    handleQuery()
  } finally {
    submitLoading.value = false
  }
}

async function handleView(row: ScrapOrderVo) {
  const res = await getScrapOrder(row.id)
  viewRow.value = res.data
  detailVisible.value = true
}

async function handleSubmitOrder(row: ScrapOrderVo) {
  await ElMessageBox.confirm('确定提交该报废单吗？', '提示', { type: 'warning' })
  await submitScrapOrder(row.id)
  ElMessage.success('提交成功')
  handleQuery()
}

function handleClose() {
  formVisible.value = false
  formRef.value?.resetFields()
  Object.assign(form, { warehouseId: undefined, scrapReason: '', remark: '', details: [] })
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
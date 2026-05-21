<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true" class="search-form">
      <el-form-item label="业务类型">
        <el-select v-model="queryParams.bizType" placeholder="请选择" clearable>
          <el-option v-for="bt in bizTypeOptions" :key="bt.value" :label="bt.label" :value="bt.value" />
        </el-select>
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

    <el-table v-loading="loading" :data="tableData" border>
      <el-table-column prop="approvalNo" label="审批单号" min-width="160" />
      <el-table-column prop="bizType" label="业务类型" min-width="100">
        <template #default="{ row }">
          <el-tag>{{ bizTypeMap[row.bizType] || row.bizType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="bizNo" label="业务单号" min-width="160" />
      <el-table-column prop="currentNodeName" label="当前节点" min-width="120" />
      <el-table-column prop="status" label="审批状态" min-width="100">
        <template #default="{ row }">
          <el-tag :type="approvalStatusTagType(row.status)">{{ approvalStatusLabel(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="applicant" label="申请人" min-width="100" />
      <el-table-column prop="createTime" label="创建时间" min-width="160" />
      <el-table-column label="操作" class-name="table-action-column" fixed="right">
        <template #default="{ row }">
          <TableActionGroup
            :actions="[
              { label: '查看', type: 'primary', icon: View, onClick: () => handleView(row) },
              { label: '通过', type: 'success', visible: row.status === 'APPROVING', onClick: () => handleApprove(row) },
              { label: '驳回', type: 'warning', visible: row.status === 'APPROVING', onClick: () => handleReject(row) },
              { label: '撤回', type: 'info', visible: canRevoke(row), onClick: () => handleRevoke(row) },
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

    <el-dialog v-model="detailVisible" title="审批详情" width="700px">
      <el-descriptions :column="2" border v-if="viewRow">
        <el-descriptions-item label="审批单号">{{ viewRow.approvalNo }}</el-descriptions-item>
        <el-descriptions-item label="业务类型">{{ bizTypeMap[viewRow.bizType] || viewRow.bizType }}</el-descriptions-item>
        <el-descriptions-item label="业务单号">{{ viewRow.bizNo }}</el-descriptions-item>
        <el-descriptions-item label="当前节点">{{ viewRow.currentNodeName }}</el-descriptions-item>
        <el-descriptions-item label="审批状态">
          <el-tag :type="approvalStatusTagType(viewRow.status)">{{ approvalStatusLabel(viewRow.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="申请人">{{ viewRow.applicant }}</el-descriptions-item>
      </el-descriptions>
      <el-divider content-position="left">审批记录</el-divider>
      <el-timeline v-if="viewRow?.records?.length">
        <el-timeline-item
          v-for="record in viewRow.records"
          :key="record.id"
          :type="record.action === 'APPROVE' ? 'success' : 'danger'"
          :timestamp="record.actionTime"
          placement="top"
        >
          <p>{{ record.nodeName }} - {{ record.assigneeName }}</p>
          <p v-if="record.opinion">意见: {{ record.opinion }}</p>
        </el-timeline-item>
      </el-timeline>
      <el-empty v-else description="暂无审批记录" />
    </el-dialog>

    <el-dialog v-model="actionVisible" :title="actionTitle" width="400px" @close="handleActionClose">
      <el-form ref="actionFormRef" :model="actionForm" label-width="80px">
        <el-form-item label="审批意见">
          <el-input v-model="actionForm.opinion" type="textarea" :rows="3" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleActionClose">取 消</el-button>
        <el-button type="primary" :loading="actionLoading" @click="handleActionSubmit">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, View } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import { getApprovalOrders, getApprovalOrder, approveOrder, rejectOrder, revokeOrder } from '@/api/approval'
import type { ApprovalOrderVo, ApprovalStatus, ApprovalActionDto } from '@/types/business'

const bizTypeOptions = [
  { label: '入库', value: 'INBOUND' },
  { label: '出库', value: 'OUTBOUND' },
  { label: '归还', value: 'RETURN' },
  { label: '报废', value: 'SCRAP' },
  { label: '调拨', value: 'TRANSFER' },
]

const statusOptions = [
  { label: '待审批', value: 'PENDING' },
  { label: '审批中', value: 'APPROVING' },
  { label: '已通过', value: 'APPROVED' },
  { label: '已驳回', value: 'REJECTED' },
  { label: '已撤回', value: 'REVOKED' },
]

const bizTypeMap: Record<string, string> = {
  INBOUND: '入库',
  OUTBOUND: '出库',
  RETURN: '归还',
  SCRAP: '报废',
  TRANSFER: '调拨',
}

const loading = ref(false)
const tableData = ref<ApprovalOrderVo[]>([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 20,
  bizType: undefined as string | undefined,
  status: undefined as string | undefined,
})

const detailVisible = ref(false)
const viewRow = ref<ApprovalOrderVo | null>(null)

const actionVisible = ref(false)
const actionTitle = ref('')
const actionType = ref<'approve' | 'reject'>('approve')
const actionRow = ref<ApprovalOrderVo | null>(null)
const actionLoading = ref(false)
const actionFormRef = ref()
const actionForm = reactive<ApprovalActionDto>({ opinion: '' })

type TagType = 'info' | 'warning' | 'success' | 'danger'

function approvalStatusTagType(status: ApprovalStatus): TagType {
  const map: Record<ApprovalStatus, TagType> = { PENDING: 'info', APPROVING: 'warning', APPROVED: 'success', REJECTED: 'danger', REVOKED: 'info' }
  return map[status]
}

function approvalStatusLabel(status: ApprovalStatus) {
  const map: Record<string, string> = { PENDING: '待审批', APPROVING: '审批中', APPROVED: '已通过', REJECTED: '已驳回', REVOKED: '已撤回' }
  return map[status] || status
}

function canRevoke(row: ApprovalOrderVo) {
  return row.status === 'PENDING' || row.status === 'APPROVING'
}

async function handleQuery() {
  loading.value = true
  try {
    const res = await getApprovalOrders(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryParams.bizType = undefined
  queryParams.status = undefined
  queryParams.page = 1
  handleQuery()
}

async function handleView(row: ApprovalOrderVo) {
  const res = await getApprovalOrder(row.id)
  viewRow.value = res.data
  detailVisible.value = true
}

function handleApprove(row: ApprovalOrderVo) {
  actionType.value = 'approve'
  actionTitle.value = '审批通过'
  actionRow.value = row
  actionForm.opinion = ''
  actionVisible.value = true
}

function handleReject(row: ApprovalOrderVo) {
  actionType.value = 'reject'
  actionTitle.value = '审批驳回'
  actionRow.value = row
  actionForm.opinion = ''
  actionVisible.value = true
}

async function handleRevoke(row: ApprovalOrderVo) {
  await ElMessageBox.confirm('确定撤回该审批单吗？', '提示', { type: 'warning' })
  await revokeOrder(row.id)
  ElMessage.success('撤回成功')
  handleQuery()
}

async function handleActionSubmit() {
  actionLoading.value = true
  try {
    const data: ApprovalActionDto = { opinion: actionForm.opinion }
    if (actionType.value === 'approve') {
      await approveOrder(actionRow.value!.id, data)
      ElMessage.success('审批通过')
    } else {
      await rejectOrder(actionRow.value!.id, data)
      ElMessage.success('审批驳回')
    }
    handleActionClose()
    handleQuery()
  } finally {
    actionLoading.value = false
  }
}

function handleActionClose() {
  actionVisible.value = false
  actionForm.opinion = ''
  actionRow.value = null
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

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>

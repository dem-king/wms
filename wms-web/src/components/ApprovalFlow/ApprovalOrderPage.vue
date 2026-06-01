<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Refresh, Search, View } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import ApprovalBizOrderDetail from '@/components/ApprovalFlow/ApprovalBizOrderDetail.vue'
import { useUserStore } from '@/store/modules/user'
import { approveOrder, getApprovalOrder, getApprovalOrders, rejectOrder, revokeOrder } from '@/api/approval'
import {
  APPROVAL_RESULT,
  APPROVAL_STATUS,
  APPROVAL_STATUS_OPTIONS,
  getApprovalResultLabel,
  getApprovalStatusLabel,
  getApprovalStatusTagType,
  BIZ_TYPE_OPTIONS,
  getBizTypeLabel,
} from '@/constants/approval'
import type { ApprovalActionDto, ApprovalOrderQuery, ApprovalOrderVo } from '@/types/business'

interface Props {
  mode: 'pending' | 'history'
  approvePermission?: string
  rejectPermission?: string
}

const props = defineProps<Props>()

const userStore = useUserStore()
const loading = ref(false)
const tableData = ref<ApprovalOrderVo[]>([])
const total = ref(0)
const detailVisible = ref(false)
const detailLoading = ref(false)
const viewRow = ref<ApprovalOrderVo | null>(null)
const actionVisible = ref(false)
const actionLoading = ref(false)
const actionTitle = ref('')
const actionType = ref<'approve' | 'reject'>('approve')
const actionRow = ref<ApprovalOrderVo | null>(null)
const actionForm = reactive<ApprovalActionDto>({
  opinion: '',
})

const queryParams = reactive<ApprovalOrderQuery>({
  page: 1,
  size: 20,
  bizType: undefined,
  status: undefined,
})

const pageTitle = computed(() => (props.mode === 'pending' ? '待审批' : '审批记录'))
const statusOptions = computed(() => {
  if (props.mode === 'pending') {
    return APPROVAL_STATUS_OPTIONS.filter((item) => {
      return item.value === APPROVAL_STATUS.PENDING || item.value === APPROVAL_STATUS.APPROVING
    })
  }
  return APPROVAL_STATUS_OPTIONS.filter((item) => {
    return item.value === APPROVAL_STATUS.APPROVED
      || item.value === APPROVAL_STATUS.REJECTED
      || item.value === APPROVAL_STATUS.REVOKED
  })
})
const currentUserId = computed(() => userStore.userInfo?.userId ?? 0)

function isPendingRow(row: ApprovalOrderVo) {
  return row.status === APPROVAL_STATUS.PENDING || row.status === APPROVAL_STATUS.APPROVING
}

function canApprove(row: ApprovalOrderVo) {
  return props.mode === 'pending' && isPendingRow(row)
}

function canRevoke(row: ApprovalOrderVo) {
  return isPendingRow(row) && row.applicantId === currentUserId.value
}

async function loadTable() {
  loading.value = true
  try {
    const res = await getApprovalOrders({ ...queryParams })
    tableData.value = res.data.records || []
    total.value = Number(res.data.total || 0)
  } finally {
    loading.value = false
  }
}

function handleQuery() {
  queryParams.page = 1
  loadTable()
}

function handleReset() {
  queryParams.page = 1
  queryParams.size = 20
  queryParams.bizType = undefined
  queryParams.status = undefined
  loadTable()
}

async function handleView(row: ApprovalOrderVo) {
  detailLoading.value = true
  detailVisible.value = true
  try {
    const res = await getApprovalOrder(row.id)
    viewRow.value = res.data
  } finally {
    detailLoading.value = false
  }
}

function openAction(row: ApprovalOrderVo, type: 'approve' | 'reject') {
  actionType.value = type
  actionTitle.value = type === 'approve' ? '审批通过' : '审批驳回'
  actionRow.value = row
  actionForm.opinion = ''
  actionVisible.value = true
}

async function handleRevoke(row: ApprovalOrderVo) {
  await ElMessageBox.confirm(`确定撤回审批单“${row.approvalNo}”吗？`, '提示', { type: 'warning' })
  await revokeOrder(row.id)
  ElMessage.success('撤回成功')
  loadTable()
}

async function handleActionSubmit() {
  if (!actionRow.value) {
    return
  }
  actionLoading.value = true
  try {
    if (actionType.value === 'approve') {
      await approveOrder(actionRow.value.id, { opinion: actionForm.opinion })
      ElMessage.success('审批通过')
    } else {
      await rejectOrder(actionRow.value.id, { opinion: actionForm.opinion })
      ElMessage.success('审批驳回')
    }
    handleActionClose()
    loadTable()
  } finally {
    actionLoading.value = false
  }
}

function handleActionClose() {
  actionVisible.value = false
  actionForm.opinion = ''
  actionRow.value = null
}

function handleDetailClose() {
  detailVisible.value = false
  viewRow.value = null
}

onMounted(() => {
  loadTable()
})
</script>

<template>
  <div class="app-container list-page approval-page">
    <el-alert
      :title="pageTitle"
      :description="props.mode === 'pending' ? '查看待处理审批并执行通过、驳回或本人撤回操作。' : '查看审批流转记录与处理结果。'"
      type="info"
      :closable="false"
      class="page-alert"
    />

    <el-form :model="queryParams" :inline="true" class="search-form">
      <el-form-item label="业务类型">
        <el-select v-model="queryParams.bizType" placeholder="全部业务类型" clearable style="width: 180px">
          <el-option v-for="item in BIZ_TYPE_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="审批状态">
        <el-select v-model="queryParams.status" placeholder="全部审批状态" clearable style="width: 180px">
          <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-section">
      <el-table v-loading="loading" :data="tableData" border height="100%">
        <el-table-column prop="approvalNo" label="审批单号" min-width="170" />
        <el-table-column label="业务类型" min-width="110">
          <template #default="{ row }">
            <el-tag>{{ getBizTypeLabel(row.bizType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="bizNo" label="业务单号" min-width="170" />
        <el-table-column prop="currentNodeName" label="当前节点" min-width="140" />
        <el-table-column label="审批状态" min-width="110">
          <template #default="{ row }">
            <el-tag :type="getApprovalStatusTagType(row.status)">{{ getApprovalStatusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="applicantName" label="申请人" min-width="120" />
        <el-table-column prop="createTime" label="创建时间" min-width="180" />
        <el-table-column label="操作" class-name="table-action-column" fixed="right" min-width="220">
          <template #default="{ row }">
            <TableActionGroup
              :actions="[
                { label: '查看', type: 'primary', icon: View, onClick: () => handleView(row) },
                { label: '通过', type: 'success', permission: props.approvePermission, visible: canApprove(row), onClick: () => openAction(row, 'approve') },
                { label: '驳回', type: 'warning', permission: props.rejectPermission, visible: canApprove(row), onClick: () => openAction(row, 'reject') },
                { label: '撤回', type: 'info', visible: canRevoke(row), onClick: () => handleRevoke(row) },
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
          @size-change="loadTable"
          @current-change="loadTable"
        />
      </div>
    </div>

    <el-dialog v-model="detailVisible" title="审批详情" width="960px" @close="handleDetailClose">
      <div v-loading="detailLoading">
        <el-descriptions v-if="viewRow" :column="2" border>
          <el-descriptions-item label="审批单号">{{ viewRow.approvalNo }}</el-descriptions-item>
          <el-descriptions-item label="业务类型">{{ getBizTypeLabel(viewRow.bizType) }}</el-descriptions-item>
          <el-descriptions-item label="业务单号">{{ viewRow.bizNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="申请人">{{ viewRow.applicantName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="当前节点">{{ viewRow.currentNodeName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="审批状态">
            <el-tag :type="getApprovalStatusTagType(viewRow.status)">{{ getApprovalStatusLabel(viewRow.status) }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="审批进度">{{ `${viewRow.currentStep || 0} / ${viewRow.totalSteps || 0}` }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ viewRow.createTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ viewRow.remark || '-' }}</el-descriptions-item>
        </el-descriptions>
        <ApprovalBizOrderDetail v-if="viewRow" :biz-type="viewRow.bizType" :biz-id="viewRow.bizId" />
        <el-divider content-position="left">审批记录</el-divider>
        <el-timeline v-if="viewRow?.records?.length">
          <el-timeline-item
            v-for="record in viewRow.records"
            :key="record.id"
            :type="record.result === APPROVAL_RESULT.APPROVED ? 'success' : 'danger'"
            :timestamp="record.approveTime || '-'"
            placement="top"
          >
            <p>{{ record.nodeName || '-' }} / {{ record.approverName || '-' }}</p>
            <p>处理结果：{{ getApprovalResultLabel(record.result) }}</p>
            <p v-if="record.opinion">审批意见：{{ record.opinion }}</p>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-else description="暂无审批记录" />
      </div>
    </el-dialog>

    <el-dialog v-model="actionVisible" :title="actionTitle" width="420px" @close="handleActionClose">
      <el-form :model="actionForm" label-width="80px">
        <el-form-item label="审批意见">
          <el-input v-model="actionForm.opinion" type="textarea" :rows="3" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleActionClose">取消</el-button>
        <el-button type="primary" :loading="actionLoading" @click="handleActionSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped lang="scss">
.approval-page {
  padding: 20px;
  display: flex;
  flex-direction: column;
  min-height: 0;

  .page-alert {
    margin-bottom: 16px;
  }

  .search-form {
    margin-bottom: 16px;
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

  .pagination {
    justify-content: flex-end;
  }
}
</style>

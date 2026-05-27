<script setup lang="ts">
import { computed } from 'vue'
import {
  APPROVAL_RESULT,
  getApprovalResultLabel,
  getApprovalStatusLabel,
  getApprovalStatusTagType,
  getBizTypeLabel,
} from '@/constants/approval'
import type { ApprovalOrderVo } from '@/types/business'

interface Props {
  visible: boolean
  approval: ApprovalOrderVo | null
}

const props = defineProps<Props>()

const emit = defineEmits<{
  (event: 'update:visible', value: boolean): void
}>()

const dialogVisible = computed({
  get: () => props.visible,
  set: (value: boolean) => emit('update:visible', value),
})
</script>

<template>
  <el-dialog v-model="dialogVisible" title="审批进度" width="760px">
    <el-descriptions v-if="approval" :column="2" border>
      <el-descriptions-item label="审批单号">{{ approval.approvalNo }}</el-descriptions-item>
      <el-descriptions-item label="业务类型">{{ getBizTypeLabel(approval.bizType) }}</el-descriptions-item>
      <el-descriptions-item label="业务单号">{{ approval.bizNo || '-' }}</el-descriptions-item>
      <el-descriptions-item label="申请人">{{ approval.applicantName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="当前节点">{{ approval.currentNodeName || '-' }}</el-descriptions-item>
      <el-descriptions-item label="审批状态">
        <el-tag :type="getApprovalStatusTagType(approval.status)">{{ getApprovalStatusLabel(approval.status) }}</el-tag>
      </el-descriptions-item>
      <el-descriptions-item label="审批进度">{{ `${approval.currentStep || 0} / ${approval.totalSteps || 0}` }}</el-descriptions-item>
      <el-descriptions-item label="创建时间">{{ approval.createTime || '-' }}</el-descriptions-item>
      <el-descriptions-item label="备注" :span="2">{{ approval.remark || '-' }}</el-descriptions-item>
    </el-descriptions>
    <el-divider content-position="left">审批记录</el-divider>
    <el-timeline v-if="approval?.records?.length">
      <el-timeline-item
        v-for="record in approval.records"
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
  </el-dialog>
</template>

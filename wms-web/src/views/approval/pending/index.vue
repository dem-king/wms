<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import ApprovalOrderPage from '@/components/ApprovalFlow/ApprovalOrderPage.vue'

const route = useRoute()

const focusApprovalId = computed(() => {
  const rawApprovalId = route.query.approvalId
  const approvalIdValue = Array.isArray(rawApprovalId) ? rawApprovalId[0] : rawApprovalId
  const approvalId = Number(approvalIdValue)

  return Number.isFinite(approvalId) && approvalId > 0 ? approvalId : undefined
})
</script>

<template>
  <div class="app-container">
    <ApprovalOrderPage
      mode="pending"
      approve-permission="approval:pending:approve"
      reject-permission="approval:pending:reject"
      :focus-approval-id="focusApprovalId"
    />
  </div>
</template>

<script setup lang="ts">
import ApprovalOrderPage from '@/components/ApprovalFlow/ApprovalOrderPage.vue'
</script>

<template>
  <div class="app-container">
    <ApprovalOrderPage mode="pending" approve-permission="approval:pending:approve" reject-permission="approval:pending:reject" />
  </div>
</template>

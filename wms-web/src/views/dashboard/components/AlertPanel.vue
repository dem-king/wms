<template>
  <el-card class="alert-panel-card" shadow="never">
    <template #header>
      <div class="card-header">
        <span class="title">预警提醒</span>
        <el-tag size="small" type="danger" round v-if="alerts.length">{{ alerts.length }}</el-tag>
      </div>
    </template>
    
    <el-empty v-if="!alerts.length" description="暂无预警提醒" :image-size="80" />
    
    <div v-else class="alert-list">
      <el-alert
        v-for="alert in alerts"
        :key="alert.id"
        :title="alert.title"
        :type="alert.level === 'danger' ? 'error' : alert.level"
        :description="alert.content"
        show-icon
        :closable="true"
        @close="$emit('dismiss', alert.id)"
        class="alert-item"
      />
    </div>
  </el-card>
</template>

<script setup lang="ts">
import type { AlertItem } from '@/types/dashboard'

defineProps<{
  alerts: AlertItem[]
}>()

defineEmits<{
  (e: 'dismiss', id: string): void
}>()
</script>

<style lang="scss" scoped>
.alert-panel-card {
  height: 100%;
  border: none;
  border-radius: 12px;
  box-shadow: 0 10px 30px rgba(15, 23, 42, 0.06);
  transition: transform 0.24s ease, box-shadow 0.24s ease;

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 18px 40px rgba(15, 23, 42, 0.1);
  }

  :deep(.el-card__header) {
    border-bottom: none;
    padding-bottom: 8px;
  }

  :deep(.el-card__body) {
    display: flex;
    flex-direction: column;
    height: calc(100% - 57px);
    min-height: 0;
    padding-top: 0;
  }
  
  .card-header {
    display: flex;
    align-items: center;
    gap: 8px;
    
    .title {
      font-weight: 500;
    }
  }

  .alert-list {
    flex: 1;
    min-height: 0;
    overflow-y: auto;
    
    .alert-item {
      margin-bottom: 12px;
      border-radius: 10px;
      
      &:last-child {
        margin-bottom: 0;
      }
    }
  }
}
</style>

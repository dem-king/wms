<template>
  <el-card class="task-list-card" shadow="never">
    <template #header>
      <div class="card-header">
        <span class="title">待办任务</span>
        <el-tag size="small" type="primary" round>{{ tasks.length }}</el-tag>
      </div>
    </template>
    
    <el-empty v-if="!tasks.length" description="暂无待办任务" :image-size="80" />
    
    <div v-else class="task-list">
      <div 
        v-for="task in tasks" 
        :key="task.id" 
        class="task-item"
      >
        <div class="task-icon">
          <el-icon :size="20" :color="getPriorityColor(task.priority)">
            <WarningFilled v-if="task.priority === 'high'" />
            <InfoFilled v-else />
          </el-icon>
        </div>
        <div class="task-content">
          <div class="task-title">{{ task.title }}</div>
          <div class="task-desc">{{ task.description }}</div>
        </div>
        <div class="task-actions">
          <el-button link type="primary" size="small" @click="handleAction(task)">
            去处理
          </el-button>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import type { TaskItem } from '@/types/dashboard'
import { WarningFilled, InfoFilled } from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

defineProps<{
  tasks: TaskItem[]
}>()

const router = useRouter()

const getPriorityColor = (priority: string) => {
  switch (priority) {
    case 'high': return '#F56C6C'
    case 'medium': return '#E6A23C'
    case 'low': return '#909399'
    default: return '#409EFF'
  }
}

const handleAction = (task: TaskItem) => {
  router.push(task.actionUrl)
}
</script>

<style lang="scss" scoped>
.task-list-card {
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

  .task-list {
    flex: 1;
    min-height: 0;
    overflow-y: auto;
    
    .task-item {
      display: flex;
      align-items: flex-start;
      padding: 14px 0;
      border-bottom: 1px solid #eef2f7;
      
      &:last-child {
        border-bottom: none;
        padding-bottom: 0;
      }
      
      .task-icon {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 36px;
        height: 36px;
        margin-right: 12px;
        margin-top: 2px;
        border-radius: 10px;
        background: #f8fafc;
      }
      
      .task-content {
        flex: 1;
        min-width: 0;
        
        .task-title {
          font-size: 14px;
          color: #1f2937;
          margin-bottom: 6px;
          font-weight: 500;
        }
        
        .task-desc {
          font-size: 12px;
          line-height: 1.6;
          color: #6b7280;
        }
      }
      
      .task-actions {
        margin-left: 12px;
        padding-top: 4px;
      }
    }
  }
}
</style>

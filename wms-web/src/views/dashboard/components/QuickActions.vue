<template>
  <el-card class="quick-actions-card" shadow="never">
    <template #header>
      <div class="card-header">
        <span class="title">快捷入口</span>
      </div>
    </template>
    
    <div class="action-grid">
      <div
        v-for="action in actions"
        :key="action.id"
        class="action-grid__item"
      >
        <div class="action-btn" @click="handleNavigate(action.url)">
          <div class="action-icon-wrap" :style="{ backgroundColor: `${getActionColor(action.color)}15` }">
            <el-icon :color="getActionColor(action.color)" :size="24">
              <component :is="icons[action.icon as keyof typeof icons]" />
            </el-icon>
          </div>
          <span class="action-name">{{ action.name }}</span>
        </div>
      </div>
    </div>
  </el-card>
</template>

<script setup lang="ts">
import type { QuickActionItem } from '@/types/dashboard'
import { useRouter } from 'vue-router'
import { Download, Upload, RefreshLeft, Delete, Switch, Search, DataBoard, PriceTag } from '@element-plus/icons-vue'

const icons = { Download, Upload, RefreshLeft, Delete, Switch, Search, DataBoard, PriceTag }

defineProps<{
  actions: QuickActionItem[]
}>()

const router = useRouter()

const handleNavigate = (url: string) => {
  router.push(url)
}

const getActionColor = (color?: string) => color ?? '#409EFF'
</script>

<style lang="scss" scoped>
.quick-actions-card {
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
    justify-content: center;
    height: calc(100% - 57px);
    padding-top: 0;
  }
  
  .card-header {
    .title {
      font-weight: 500;
    }
  }

  .action-grid {
    display: grid;
    grid-template-columns: repeat(4, minmax(0, 1fr));
    gap: 12px;

    &__item {
      min-width: 0;
    }
  }

  .action-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    width: 100%;
    min-height: 104px;
    padding: 14px 10px;
    cursor: pointer;
    border-radius: 12px;
    background: #f8fafc;
    transition: transform 0.24s ease, box-shadow 0.24s ease, background-color 0.24s ease;
    
    &:hover {
      background-color: #eff6ff;
      box-shadow: 0 12px 24px rgba(59, 130, 246, 0.12);
      
      .action-icon-wrap {
        transform: translateY(-2px);
      }
    }
    
    .action-icon-wrap {
      width: 48px;
      height: 48px;
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      margin-bottom: 8px;
      transition: transform 0.3s;
    }
    
    .action-name {
      font-size: 13px;
      line-height: 1.4;
      color: #4b5563;
      text-align: center;
    }
  }
}

@media (max-width: 767px) {
  .quick-actions-card {
    .action-grid {
      grid-template-columns: repeat(3, minmax(0, 1fr));
      gap: 10px;
    }

    .action-btn {
      min-height: 96px;
    }
  }
}
</style>

<template>
  <div class="dashboard">
    <el-row :gutter="16">
      <el-col :span="6" v-for="card in statCards" :key="card.title">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ card.value }}</div>
          <div class="stat-title">{{ card.title }}</div>
        </el-card>
      </el-col>
    </el-row>
    <el-row :gutter="16" style="margin-top: 16px">
      <el-col :span="12">
        <el-card>
          <template #header>库存概览</template>
          <div ref="stockChartRef" style="height: 300px"></div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card>
          <template #header>近期预警</template>
          <el-empty description="暂无预警" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'

const stockChartRef = ref<HTMLElement>()

const statCards = reactive([
  { title: '物品总数', value: 0 },
  { title: '在库数量', value: 0 },
  { title: '领用中', value: 0 },
  { title: '预警数量', value: 0 }
])

onMounted(() => {
})
</script>

<style lang="scss" scoped>
.stat-card {
  text-align: center;
  .stat-value { font-size: 28px; font-weight: bold; color: #409eff; }
  .stat-title { font-size: 14px; color: #909399; margin-top: 8px; }
}
</style>

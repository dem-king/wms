<template>
  <div class="dashboard-container">
    <div class="dashboard-header">
      <div class="title">
        <h2>智能工作台</h2>
        <p>欢迎回来，今日库存动态、待办事项与风险提醒都已为你整理好。</p>
      </div>
      <div class="weather-card">
        <div class="weather-card__header">
          <span class="weather-card__tag">实时天气</span>
          <el-button link type="primary" @click="loadLocationWeather">重新获取</el-button>
        </div>
        <h3 class="weather-card__location">{{ locationWeather.locationText }}</h3>
        <p class="weather-card__weather">{{ locationWeather.weatherText }}</p>
        <p class="weather-card__detail">{{ locationWeather.detailText }}</p>
      </div>
    </div>

    <div v-loading="loading">
      <div v-if="dashboardData" class="dashboard-shell">
        <MetricCarousel :metrics="displayMetrics" />

        <div class="dashboard-bento">
          <section class="bento-panel bento-panel--tasks">
            <TaskList :tasks="dashboardData.tasks" />
          </section>

          <section class="bento-panel bento-panel--compare">
            <CompareChart :data="dashboardData.charts.compare" />
          </section>

          <section class="bento-panel bento-panel--distribution">
            <DistributionChart :data="dashboardData.charts.distribution" />
          </section>

          <section class="bento-panel bento-panel--trend">
            <TrendChart
              :data="dashboardData.charts.trend"
              @rangeChange="handleChartRangeChange"
            />
          </section>

          <section class="bento-panel bento-panel--side">
            <div class="side-stack">
              <div class="side-stack__item">
                <QuickActions :actions="displayActions" />
              </div>
              <div class="side-stack__item">
                <AlertPanel
                  :alerts="dashboardData.alerts"
                  @dismiss="handleDismissAlert"
                />
              </div>
            </div>
          </section>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { getDashboardData, dismissAlert } from '@/api/dashboard'
import type { DashboardData } from '@/types/dashboard'
import { ElMessage } from 'element-plus'
import { useDashboardLocationWeather } from './useDashboardLocationWeather'

import MetricCarousel from './components/MetricCarousel.vue'
import TaskList from './components/TaskList.vue'
import AlertPanel from './components/AlertPanel.vue'
import QuickActions from './components/QuickActions.vue'
import TrendChart from './components/TrendChart.vue'
import CompareChart from './components/CompareChart.vue'
import DistributionChart from './components/DistributionChart.vue'

const loading = ref(false)
const dashboardData = ref<DashboardData | null>(null)
const { locationWeather, loadLocationWeather } = useDashboardLocationWeather()

// 全量显示指标
const displayMetrics = computed(() => {
  if (!dashboardData.value) return []
  return dashboardData.value.metrics
})

// 全量显示快捷入口
const displayActions = computed(() => {
  if (!dashboardData.value) return []
  return dashboardData.value.quickActions
})

const loadData = async () => {
  loading.value = true
  try {
    const dataRes = await getDashboardData()
    dashboardData.value = dataRes.data
  } catch (error) {
    ElMessage.error('获取首页数据失败')
  } finally {
    loading.value = false
  }
}

const handleChartRangeChange = (range: string) => {
  // 模拟图表数据刷新
  ElMessage.success(`已切换到${range}视图`)
}

const handleDismissAlert = async (id: string) => {
  try {
    await dismissAlert(id)
    if (dashboardData.value) {
      dashboardData.value.alerts = dashboardData.value.alerts.filter(a => a.id !== id)
    }
    ElMessage.success('预警已忽略')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadData()
  loadLocationWeather()
})
</script>

<style lang="scss" scoped>
.dashboard-container {
  padding: 0;
  
  .dashboard-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 24px;
    padding: 20px 24px;
    background: linear-gradient(90deg, #ffffff 0%, #f0f7ff 50%, #e0f2fe 100%);
    border-radius: 12px;
    border: 1px solid #dbeafe;
    
    .title {
      margin-left: 24px;

      h2 {
        margin: 0;
        font-size: 24px;
        color: #1f2937;
        font-weight: 600;
      }

      p {
        margin: 8px 0 0;
        font-size: 14px;
        line-height: 1.6;
        color: #6b7280;
      }
    }
    
    .actions {
      display: flex;
      align-items: center;
      gap: 16px;
    }

    .weather-card {
      min-width: 320px;
      max-width: 360px;
      padding: 16px 18px;
      border-radius: 14px;
      background: rgba(255, 255, 255, 0.82);
      box-shadow: 0 12px 30px rgba(59, 130, 246, 0.12);
      backdrop-filter: blur(10px);

      &__header {
        display: flex;
        align-items: center;
        justify-content: space-between;
        gap: 12px;
      }

      &__tag {
        display: inline-flex;
        align-items: center;
        justify-content: center;
        padding: 4px 10px;
        border-radius: 999px;
        background: #dbeafe;
        color: #2563eb;
        font-size: 12px;
        font-weight: 600;
      }

      &__location {
        margin: 14px 0 8px;
        font-size: 24px;
        line-height: 1.3;
        color: #111827;
      }

      &__weather {
        margin: 0;
        font-size: 16px;
        color: #1f2937;
        font-weight: 600;
      }

      &__detail {
        margin: 10px 0 0;
        font-size: 13px;
        line-height: 1.6;
        color: #6b7280;
      }
    }
  }

  .dashboard-shell {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .dashboard-bento {
    display: grid;
    grid-template-columns: repeat(12, minmax(0, 1fr));
    grid-auto-rows: minmax(260px, auto);
    gap: 20px;
    align-items: stretch;
  }

  .bento-panel {
    min-width: 0;
    min-height: 0;
    height: 100%;
  }

  .bento-panel--tasks,
  .bento-panel--compare,
  .bento-panel--distribution {
    grid-column: span 4;
  }

  .bento-panel--trend {
    grid-column: span 8;
    grid-row: span 2;
  }

  .bento-panel--side {
    grid-column: span 4;
    grid-row: span 2;
  }

  .side-stack {
    display: grid;
    grid-template-rows: repeat(2, minmax(0, 1fr));
    gap: 20px;
    height: 100%;

    &__item {
      min-height: 0;
    }
  }
}

@media (max-width: 1199px) {
  .dashboard-container {
    .dashboard-bento {
      grid-template-columns: repeat(6, minmax(0, 1fr));
    }

    .dashboard-header {
      flex-direction: column;
      gap: 16px;

      .weather-card {
        width: 100%;
        max-width: none;
        min-width: 0;
      }
    }

    .bento-panel--tasks,
    .bento-panel--compare,
    .bento-panel--distribution,
    .bento-panel--trend,
    .bento-panel--side {
      grid-column: span 3;
      grid-row: span 1;
    }
  }
}

@media (max-width: 767px) {
  .dashboard-container {
    .dashboard-header {
      flex-direction: column;
      gap: 12px;
      margin-bottom: 20px;
    }

    .dashboard-bento {
      grid-template-columns: 1fr;
      grid-auto-rows: minmax(220px, auto);
      gap: 16px;
    }

    .bento-panel--tasks,
    .bento-panel--compare,
    .bento-panel--distribution,
    .bento-panel--trend,
    .bento-panel--side {
      grid-column: span 1;
      grid-row: span 1;
    }

    .side-stack {
      grid-template-rows: repeat(2, minmax(220px, auto));
      gap: 16px;
    }
  }
}

@media (max-width: 767px) {
  .dashboard-container {
    .dashboard-header {
      padding: 16px;

      .title {
        margin-left: 0;
      }
    }

    .dashboard-bento {
      grid-template-columns: 1fr;
    }

    .bento-panel--tasks,
    .bento-panel--compare,
    .bento-panel--distribution,
    .bento-panel--trend,
    .bento-panel--side {
      grid-column: span 1;
    }
  }
}
</style>

<template>
  <div class="metric-carousel">
    <div class="metric-grid">
      <div
        v-for="metric in metrics"
        :key="metric.key"
        class="metric-grid__item"
      >
        <MetricCard :item="metric" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import type { MetricItem } from '@/types/dashboard'
import MetricCard from './MetricCard.vue'

defineProps<{
  metrics: MetricItem[]
}>()
</script>

<style lang="scss" scoped>
.metric-carousel {
  width: 100%;

  .metric-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
    gap: 20px;
    align-items: stretch;

    &__item {
      min-width: 0;
      height: 100%;
    }
  }
}

@media (max-width: 767px) {
  .metric-carousel {
    .metric-grid {
      gap: 16px;
      grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
    }
  }
}
</style>

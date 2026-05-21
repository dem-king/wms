<template>
  <div class="tags-view">
    <div class="tags-view-scroll">
      <button
        v-for="tag in visitedViews"
        :key="tag.path"
        class="tag-item"
        :class="{ active: tag.path === route.path }"
        type="button"
        @click="router.push(tag.path)"
      >
        <span class="tag-label">{{ tag.meta?.title || tag.name }}</span>
        <span
          v-if="!tag.meta?.affix"
          class="tag-close"
          @click.stop="closeTag(tag)"
        >
          ×
        </span>
      </button>
    </div>

    <TagsViewActions
      v-if="preferences.tabbar.showMore"
      @close-all="closeAllTags"
      @close-others="closeOtherTags"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { preferences } from '@/utils/preferences'
import TagsViewActions from './TagsViewActions.vue'

const route = useRoute()
const router = useRouter()

interface TagView {
  path: string
  name?: string
  meta?: any
}

const visitedViews = ref<TagView[]>([
  { path: '/dashboard', name: 'Dashboard', meta: { title: '首页', affix: true } }
])

watch(
  () => route.path,
  (newPath) => {
    if (newPath && !visitedViews.value.some(v => v.path === newPath)) {
      if (newPath === '/login' || newPath === '/404' || newPath === '/403') {
        return
      }
      visitedViews.value.push({
        path: newPath,
        name: route.name as string,
        meta: { ...route.meta }
      })
    }
  },
  { immediate: true }
)

function closeTag(tag: TagView) {
  const index = visitedViews.value.findIndex(v => v.path === tag.path)
  if (index > -1) {
    visitedViews.value.splice(index, 1)
    if (tag.path === route.path) {
      const last = visitedViews.value[visitedViews.value.length - 1]
      if (last) router.push(last.path)
    }
  }
}

function closeOtherTags() {
  visitedViews.value = visitedViews.value.filter((tag) => tag.meta?.affix || tag.path === route.path)
}

function closeAllTags() {
  visitedViews.value = visitedViews.value.filter((tag) => tag.meta?.affix)
  router.push('/dashboard')
}
</script>

<style lang="scss" scoped>
.tags-view {
  height: v-bind('`${preferences.tabbar.height}px`');
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 12px;
  padding: 8px 16px 0;
  background: transparent;
  border-bottom: 1px solid transparent;
}

.tags-view-scroll {
  flex: 1;
  min-width: 0;
  display: flex;
  align-items: flex-end;
  gap: 4px;
  overflow-x: auto;
  scrollbar-width: none;
}

.tag-item {
  flex-shrink: 0;
  height: 32px;
  padding: 0 10px 0 12px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  border: 1px solid transparent;
  border-radius: 8px 8px 0 0;
  margin-bottom: -1px;
  background: transparent;
  color: var(--text-muted-foreground);
  cursor: pointer;
  position: relative;
  transition: all 0.2s ease;

  &:hover {
    color: var(--text-foreground);
    background: hsl(var(--accent) / 0.5);
  }

  &.active {
    color: var(--color-primary);
    background: transparent;
    border-color: transparent;
    border-bottom-color: transparent;
    z-index: 1;

    &::before {
      content: '';
      position: absolute;
      bottom: -1px;
      left: 0;
      right: 0;
      height: 2px;
      background-color: var(--color-primary);
      border-radius: 0;
    }
  }
}

.tag-label {
  font-size: 12px;
  font-weight: 600;
  line-height: 1;
}

.tag-close {
  width: 18px;
  height: 18px;
  border-radius: 999px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: inherit;
  font-size: 12px;
  line-height: 1;

  &:hover {
    background: hsl(var(--background) / 0.7);
  }
}
</style>

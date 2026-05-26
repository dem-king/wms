<template>
  <div class="image-preview">
    <el-image
      v-for="(img, index) in images"
      :key="img.id || index"
      :src="resolveUrl(img.imageUrl)"
      :preview-src-list="previewList"
      :initial-index="index"
      fit="cover"
      class="image-preview-item"
      :preview-teleported="true"
    >
      <template #error>
        <div class="image-preview-error">
          <el-icon :size="16"><Picture /></el-icon>
        </div>
      </template>
    </el-image>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Picture } from '@element-plus/icons-vue'

export interface ImageItem {
  id?: number
  imageUrl: string
  imageName?: string
}

const props = defineProps<{
  images: ImageItem[]
}>()

/**
 * 解析图片URL
 * 如果是相对路径（以/开头），拼接API base URL
 * 如果是完整URL（http/https开头），直接使用
 */
function resolveUrl(url: string): string {
  if (!url) return ''
  if (url.startsWith('http://') || url.startsWith('https://')) {
    return url
  }
  // 相对路径，通过Vite proxy或后端访问
  return url
}

const previewList = computed(() => props.images.map(img => resolveUrl(img.imageUrl)))
</script>

<style scoped>
.image-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}
.image-preview-item {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  border: 1px solid var(--el-border-color-lighter);
}
.image-preview-error {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  background: var(--el-fill-color-light);
  color: var(--el-text-color-placeholder);
}
</style>

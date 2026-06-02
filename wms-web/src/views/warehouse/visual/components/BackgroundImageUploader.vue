<script setup lang="ts">
/**
 * 底图上传组件
 * 支持上传JPG/PNG/SVG格式底图，限制10MB
 */
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import type { EntityId } from '@/types/warehouse'
import { uploadWarehouseBackground, deleteWarehouseBackground } from '@/api/warehouse/layout-element'

/** 允许的文件类型 */
const ALLOWED_TYPES = ['image/jpeg', 'image/png', 'image/svg+xml']
/** 文件大小限制(10MB) */
const MAX_FILE_SIZE = 10 * 1024 * 1024

const props = defineProps<{
  warehouseId: EntityId
  hasBackground: boolean
}>()

const emit = defineEmits<{
  (e: 'uploaded'): void
  (e: 'deleted'): void
}>()

const uploading = ref(false)
const uploadProgress = ref(0)

/**
 * 处理文件选择
 */
async function handleFileChange(event: Event) {
  const input = event.target as HTMLInputElement
  const file = input.files?.[0]
  if (!file) {
    return
  }

  // 校验文件类型
  if (!ALLOWED_TYPES.includes(file.type)) {
    ElMessage.error('仅支持JPG/PNG/SVG格式')
    input.value = ''
    return
  }

  // 校验文件大小
  if (file.size > MAX_FILE_SIZE) {
    ElMessage.error('文件大小超过10MB限制')
    input.value = ''
    return
  }

  uploading.value = true
  uploadProgress.value = 0

  try {
    // 模拟上传进度
    const progressTimer = setInterval(() => {
      uploadProgress.value = Math.min(uploadProgress.value + 10, 90)
    }, 200)

    await uploadWarehouseBackground(props.warehouseId, file)

    clearInterval(progressTimer)
    uploadProgress.value = 100

    ElMessage.success('底图上传成功')
    emit('uploaded')
  } catch (error) {
    ElMessage.error('底图上传失败，请重试')
  } finally {
    uploading.value = false
    uploadProgress.value = 0
    input.value = ''
  }
}

/**
 * 删除底图
 */
async function handleDeleteBackground() {
  try {
    await deleteWarehouseBackground(props.warehouseId)
    ElMessage.success('底图已删除')
    emit('deleted')
  } catch (error) {
    ElMessage.error('底图删除失败')
  }
}
</script>

<template>
  <div class="background-uploader">
    <div class="uploader-actions">
      <label class="upload-button" :class="{ disabled: uploading }">
        <input
          type="file"
          accept=".jpg,.jpeg,.png,.svg"
          :disabled="uploading"
          @change="handleFileChange"
        />
        <el-button :loading="uploading" size="small" type="primary" plain>
          {{ uploading ? '上传中...' : '上传底图' }}
        </el-button>
      </label>
      <el-button
        v-if="hasBackground"
        size="small"
        type="danger"
        plain
        @click="handleDeleteBackground"
      >
        删除底图
      </el-button>
    </div>
    <el-progress
      v-if="uploading"
      :percentage="uploadProgress"
      :stroke-width="4"
      style="margin-top: 8px;"
    />
  </div>
</template>

<style scoped lang="scss">
.background-uploader {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.uploader-actions {
  display: flex;
  gap: 8px;
  align-items: center;
}

.upload-button {
  cursor: pointer;

  &.disabled {
    cursor: not-allowed;
    opacity: 0.6;
  }

  input[type="file"] {
    display: none;
  }
}
</style>
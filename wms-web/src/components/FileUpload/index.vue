<template>
  <div class="image-upload">
    <el-upload
      :action="''"
      :auto-upload="false"
      :show-file-list="false"
      :accept="accept"
      :on-change="handleChange"
      :disabled="uploading"
    >
      <slot :uploading="uploading">
        <div class="image-upload-btn">
          <el-icon :size="24"><Plus /></el-icon>
          <span>上传图片</span>
        </div>
      </slot>
    </el-upload>
    <el-progress
      v-if="uploading && uploadProgress != null && uploadProgress > 0"
      :percentage="uploadProgress ?? 0"
      :stroke-width="2"
      style="margin-top: 4px"
    />
  </div>
</template>

<script setup lang="ts">
import { Plus } from '@element-plus/icons-vue'
import { useFileUpload } from '@/hooks/useFileUpload'
import type { UploadFile } from 'element-plus'
import type { UploadResultVo } from '@/types/storage'

const props = withDefaults(defineProps<{
  /** 逻辑桶名 */
  bucket: string
  /** 上传模式 */
  mode?: 'server' | 'presign'
  /** 允许的文件类型 */
  accept?: string
  /** 上传前校验 */
  beforeUpload?: (file: File) => boolean | Promise<boolean>
}>(), {
  mode: 'server',
  accept: 'image/*'
})

const emit = defineEmits<{
  (e: 'success', result: UploadResultVo): void
  (e: 'error', error: Error): void
}>()

const { upload, uploading, uploadProgress } = useFileUpload({
  bucket: props.bucket,
  mode: props.mode,
  beforeUpload: props.beforeUpload
})

async function handleChange(uploadFile: UploadFile) {
  if (!uploadFile.raw) return
  try {
    const result = await upload(uploadFile.raw)
    emit('success', result)
  } catch (error) {
    emit('error', error as Error)
  }
}
</script>

<style scoped>
.image-upload-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 80px;
  height: 80px;
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  color: var(--el-text-color-secondary);
  font-size: 12px;
  gap: 4px;
  transition: border-color 0.2s;
}
.image-upload-btn:hover {
  border-color: var(--el-color-primary);
}
</style>

import { ref } from 'vue'
import { uploadFile, getPresignedUploadUrl } from '@/api/common/storage'
import type { UploadResultVo, PresignedUploadResult } from '@/types/storage'

/** 上传模式 */
export type UploadMode = 'server' | 'presign'

export interface UseFileUploadOptions {
  /** 逻辑桶名 */
  bucket: string
  /** 上传模式: server=服务端中转, presign=前端直传MinIO */
  mode?: UploadMode
  /** 上传前校验，返回false取消上传 */
  beforeUpload?: (file: File) => boolean | Promise<boolean>
}

export interface UseFileUploadReturn {
  /** 上传文件 */
  upload: (file: File) => Promise<UploadResultVo>
  /** 上传中状态 */
  uploading: ReturnType<typeof ref<boolean>>
  /** 上传进度(0-100)，仅presign模式支持 */
  uploadProgress: ReturnType<typeof ref<number>>
}

/**
 * 文件上传composable
 * 支持服务端中转和前端直传MinIO两种模式
 */
export function useFileUpload(options: UseFileUploadOptions): UseFileUploadReturn {
  const { bucket, mode = 'server', beforeUpload } = options
  const uploading = ref(false)
  const uploadProgress = ref(0)

  async function upload(file: File): Promise<UploadResultVo> {
    // 上传前校验
    if (beforeUpload) {
      const allowed = await beforeUpload(file)
      if (!allowed) {
        throw new Error('上传被取消')
      }
    }

    uploading.value = true
    uploadProgress.value = 0

    try {
      if (mode === 'presign') {
        return await presignUpload(file)
      }
      return await serverUpload(file)
    } finally {
      uploading.value = false
    }
  }

  /** 服务端中转上传 */
  async function serverUpload(file: File): Promise<UploadResultVo> {
    const res = await uploadFile(bucket, file)
    uploadProgress.value = 100
    return res.data
  }

  /** 前端直传MinIO（预签名URL） */
  async function presignUpload(file: File): Promise<UploadResultVo> {
    // 1. 请求后端获取预签名URL
    const presignRes = await getPresignedUploadUrl(bucket, {
      fileName: file.name,
      contentType: file.type
    })
    const presignResult: PresignedUploadResult = presignRes.data

    // 2. 直传MinIO（PUT请求）
    await new Promise<void>((resolve, reject) => {
      const xhr = new XMLHttpRequest()
      xhr.open('PUT', presignResult.uploadUrl, true)
      xhr.setRequestHeader('Content-Type', file.type)

      // 上传进度
      xhr.upload.onprogress = (event) => {
        if (event.lengthComputable) {
          uploadProgress.value = Math.round((event.loaded / event.total) * 100)
        }
      }

      xhr.onload = () => {
        if (xhr.status >= 200 && xhr.status < 300) {
          resolve()
        } else {
          reject(new Error(`上传失败: HTTP ${xhr.status}`))
        }
      }
      xhr.onerror = () => reject(new Error('网络错误'))
      xhr.send(file)
    })

    // 3. 返回访问URL
    return {
      url: presignResult.accessUrl,
      objectName: presignResult.objectName
    }
  }

  return { upload, uploading, uploadProgress }
}

import { post, del } from '../request'
import type { UploadResultVo, PresignRequestDto, PresignedUploadResult } from '@/types/storage'

/**
 * 上传文件到指定业务桶
 */
export function uploadFile(bucket: string, file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return post<UploadResultVo>(`/storage/upload/${bucket}`, formData)
}

/**
 * 获取预签名上传URL（前端直传MinIO场景）
 */
export function getPresignedUploadUrl(bucket: string, data: PresignRequestDto) {
  return post<PresignedUploadResult>(`/storage/presign/${bucket}`, data)
}

/**
 * 删除文件
 */
export function deleteFile(bucket: string, objectName: string) {
  return del<void>(`/storage/${bucket}/${objectName}`)
}

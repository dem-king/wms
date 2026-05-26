/** 文件存储相关类型 */

/** 文件上传结果 */
export interface UploadResultVo {
  /** 文件访问URL */
  url: string
  /** 对象存储路径 */
  objectName: string
}

/** 预签名上传请求 */
export interface PresignRequestDto {
  /** 文件名 */
  fileName: string
  /** MIME类型 */
  contentType?: string
}

/** 预签名上传结果 */
export interface PresignedUploadResult {
  /** 预签名上传URL */
  uploadUrl: string
  /** 对象存储路径 */
  objectName: string
  /** 上传完成后的文件访问URL */
  accessUrl: string
}

/**
 * 验证码 API 接口（PDA 版本）
 * 适配 PDA 的 uni.request 封装，API 路径前缀为 /api/auth/code
 */
import { get, request } from '@/api/request'

/** 验证码请求参数 */
export interface VerifyParams {
  captchaType?: string
  pointJson?: string
  token?: string
}

/** 验证码响应结构（anji-plus SDK 标准返回） */
export interface VerifyResult {
  repCode: string
  repData: any
  repMsg: string
  success: boolean
}

/**
 * 申请一张验证码
 * 调用 GET /api/auth/code/get 获取验证码图片
 * PDA 的 request 封装已自动解包 ApiResponse，直接返回 data 字段
 *
 * @param data 请求参数
 * @returns VerifyResult
 */
export function reqGet(data: VerifyParams): Promise<VerifyResult> {
  return get<VerifyResult>('/api/auth/code/get', data as unknown as Record<string, unknown>)
}

/**
 * 校验验证码坐标
 * 调用 POST /api/auth/code/check 校验用户拖动/点击结果
 * PDA 的 post 方法直接传对象，Content-Type 默认 application/json
 *
 * @param data 请求参数
 * @returns VerifyResult
 */
export function reqCheck(data: VerifyParams): Promise<VerifyResult> {
  const body = Object.entries(data)
    .filter(([, value]) => value !== undefined && value !== null)
    .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(String(value))}`)
    .join('&')

  return request<VerifyResult>({
    url: '/api/auth/code/check',
    method: 'POST',
    data: body,
    header: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    needToken: false,
  })
}

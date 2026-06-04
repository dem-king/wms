import { get, post } from '@/api/request'

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
 * 调用 GET /auth/code/get 获取验证码图片
 * 返回值已解包：直接返回 VerifyResult（{ repCode, repData, repMsg, success }）
 */
export async function reqGet(data: VerifyParams): Promise<VerifyResult> {
  const res = await get<VerifyResult>('/auth/code/get', data as unknown as Record<string, unknown>)
  return res.data
}

/**
 * 校验验证码坐标
 * 调用 POST /auth/code/check 校验用户拖动/点击结果
 * Content-Type: application/x-www-form-urlencoded
 * 返回值已解包：直接返回 VerifyResult（{ repCode, repData, repMsg, success }）
 */
export async function reqCheck(data: VerifyParams): Promise<VerifyResult> {
  const body = new URLSearchParams()
  Object.entries(data).forEach(([key, value]) => {
    if (value !== undefined && value !== null) {
      body.set(key, String(value))
    }
  })

  const res = await post<VerifyResult>('/auth/code/check', body, {
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
  })
  return res.data
}

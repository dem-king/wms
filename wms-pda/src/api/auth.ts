/**
 * WMS-PDA 认证接口
 * 封装认证相关的API请求
 */
import type { LoginDto, LoginResultVo, RefreshTokenDto, RefreshTokenResultVo, AuthProfileVo } from '@/utils/constants'
import { RequestTimeout } from '@/utils/constants'
import { post, get, request } from '@/api/request'

/**
 * 登录
 * POST /api/auth/login
 *
 * @param data 登录请求参数
 * @returns 登录结果（含双Token）
 */
export function loginApi(data: LoginDto): Promise<LoginResultVo> {
  return post<LoginResultVo>('/api/auth/login', data, RequestTimeout.DEFAULT)
}

/**
 * 登出
 * POST /api/auth/logout
 */
export function logoutApi(): Promise<void> {
  return post<void>('/api/auth/logout')
}

/**
 * 刷新Token
 * POST /api/auth/token/refresh
 *
 * @param data Token刷新请求参数
 * @returns Token刷新结果
 */
export function refreshTokenApi(data: RefreshTokenDto): Promise<RefreshTokenResultVo> {
  return post<RefreshTokenResultVo>('/api/auth/token/refresh', data, RequestTimeout.DEFAULT)
}

/**
 * 获取用户档案
 * GET /api/auth/profile
 *
 * @returns 用户档案信息
 */
export function getProfileApi(): Promise<AuthProfileVo> {
  return get<AuthProfileVo>('/api/auth/profile')
}
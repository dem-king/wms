/**
 * WMS-PDA 认证接口。
 */
import type {
  AuthProfileVo,
  LoginDto,
  LoginResultVo,
  RefreshTokenDto,
  RefreshTokenResultVo,
  RsaKeyPairResp
} from '@/utils/constants'
import { RequestTimeout } from '@/utils/constants'
import { get, post } from '@/api/request'

/**
 * 登录。
 * POST /api/auth/login
 */
export function loginApi(data: LoginDto): Promise<LoginResultVo> {
  return post<LoginResultVo>('/api/auth/login', {
    username: data.username,
    encryptedPassword: data.encryptedPassword,
    code: data.code,
    randomStr: data.randomStr,
  }, RequestTimeout.DEFAULT)
}

/**
 * 登出。
 * POST /api/auth/logout
 */
export function logoutApi(): Promise<void> {
  return post<void>('/api/auth/logout')
}

/**
 * 获取 RSA 登录公钥。
 * GET /api/auth/crypto/rsa-public-key
 */
export function getRsaPublicKey(): Promise<RsaKeyPairResp> {
  return get<RsaKeyPairResp>('/api/auth/crypto/rsa-public-key')
}

/**
 * 刷新 Token。
 * POST /api/auth/token/refresh
 */
export function refreshTokenApi(data: RefreshTokenDto): Promise<RefreshTokenResultVo> {
  return post<RefreshTokenResultVo>('/api/auth/token/refresh', data, RequestTimeout.DEFAULT)
}

/**
 * 获取用户档案。
 * GET /api/auth/profile
 */
export function getProfileApi(): Promise<AuthProfileVo> {
  return get<AuthProfileVo>('/api/auth/profile')
}

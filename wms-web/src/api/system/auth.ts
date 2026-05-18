import { get, post, put } from '../request'
import type { LoginReq, LoginResp, RsaKeyPairResp, CaptchaResp, RefreshTokenReq, TokenResp, PasswordReq } from '@/types/auth'

export function login(data: LoginReq) {
  return post<LoginResp>('/auth/login', data)
}

export function logout() {
  return post<void>('/auth/logout')
}

export function refreshToken(data: RefreshTokenReq) {
  return post<TokenResp>('/auth/token/refresh', data)
}

export function getRsaPublicKey() {
  return get<RsaKeyPairResp>('/auth/crypto/rsa-public-key')
}

export function getCaptchaImage() {
  return get<CaptchaResp>('/auth/captcha/image')
}

export function changePassword(data: PasswordReq) {
  return put<void>('/auth/password', data)
}

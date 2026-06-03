import { get, post, put } from '../request'
import type {
  LoginReq,
  LoginResp,
  RsaKeyPairResp,
  CaptchaImageResp,
  RefreshTokenReq,
  TokenResp,
  PasswordReq,
  ProfileResp,
  UpdateProfileReq,
  UploadAvatarResp,
} from '@/types/auth'

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

/**
 * 获取滑块拼图验证码
 * 返回背景图 / 拼图块 (Base64)、缺口 Y 坐标、Token 与过期秒数
 */
export function getSliderCaptcha() {
  return get<CaptchaImageResp>('/auth/captcha/slider')
}

export function changePassword(data: PasswordReq) {
  return put<void>('/auth/password', data)
}

export function getProfile() {
  return get<ProfileResp>('/auth/profile')
}

export function updateProfile(data: UpdateProfileReq) {
  return put<ProfileResp>('/auth/profile', data)
}

export function uploadAvatar(data: FormData) {
  return post<UploadAvatarResp>('/auth/profile/avatar', data)
}

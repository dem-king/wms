import type { UserInfoVO } from '@/types/auth'

const TOKEN_KEY = 'wms_token'
const REFRESH_TOKEN_KEY = 'wms_refresh_token'
const TOKEN_EXPIRES_AT_KEY = 'wms_access_token_expires_at'
const USER_INFO_KEY = 'wms_user_info'
const PERMISSIONS_KEY = 'wms_permissions'
const ROLES_KEY = 'wms_roles'

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token: string) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function removeToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export function getRefreshToken(): string | null {
  return localStorage.getItem(REFRESH_TOKEN_KEY)
}

export function setRefreshToken(token: string) {
  localStorage.setItem(REFRESH_TOKEN_KEY, token)
}

export function removeRefreshToken() {
  localStorage.removeItem(REFRESH_TOKEN_KEY)
}

export function getTokenExpiresAt(): number | null {
  const value = localStorage.getItem(TOKEN_EXPIRES_AT_KEY)
  if (!value) {
    return null
  }

  const expiresAt = Number(value)
  if (Number.isNaN(expiresAt) || expiresAt <= 0) {
    localStorage.removeItem(TOKEN_EXPIRES_AT_KEY)
    return null
  }

  return expiresAt
}

export function setTokenExpiresAt(expiresAt: number) {
  localStorage.setItem(TOKEN_EXPIRES_AT_KEY, String(expiresAt))
}

export function removeTokenExpiresAt() {
  localStorage.removeItem(TOKEN_EXPIRES_AT_KEY)
}

export function getStoredUserInfo(): UserInfoVO | null {
  return getStoredValue<UserInfoVO>(USER_INFO_KEY)
}

export function setStoredUserInfo(userInfo: UserInfoVO | null) {
  setStoredValue(USER_INFO_KEY, userInfo)
}

export function getStoredPermissions(): string[] {
  return getStoredValue<string[]>(PERMISSIONS_KEY) || []
}

export function setStoredPermissions(permissions: string[]) {
  setStoredValue(PERMISSIONS_KEY, permissions)
}

export function getStoredRoles(): string[] {
  return getStoredValue<string[]>(ROLES_KEY) || []
}

export function setStoredRoles(roles: string[]) {
  setStoredValue(ROLES_KEY, roles)
}

export function clearAuth() {
  removeToken()
  removeRefreshToken()
  removeTokenExpiresAt()
  localStorage.removeItem(USER_INFO_KEY)
  localStorage.removeItem(PERMISSIONS_KEY)
  localStorage.removeItem(ROLES_KEY)
}

function setStoredValue(key: string, value: unknown) {
  if (value == null) {
    localStorage.removeItem(key)
    return
  }
  localStorage.setItem(key, JSON.stringify(value))
}

function getStoredValue<T>(key: string): T | null {
  const value = localStorage.getItem(key)
  if (!value) {
    return null
  }
  try {
    return JSON.parse(value) as T
  } catch {
    localStorage.removeItem(key)
    return null
  }
}

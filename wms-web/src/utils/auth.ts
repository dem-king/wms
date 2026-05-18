const TOKEN_KEY = 'wms_token'
const REFRESH_TOKEN_KEY = 'wms_refresh_token'

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

export function clearAuth() {
  removeToken()
  removeRefreshToken()
  localStorage.removeItem('wms_user_info')
  localStorage.removeItem('wms_permissions')
  localStorage.removeItem('wms_roles')
}

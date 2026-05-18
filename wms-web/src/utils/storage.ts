export function setLocal(key: string, value: any) {
  localStorage.setItem(key, JSON.stringify(value))
}

export function getLocal<T = any>(key: string): T | null {
  const val = localStorage.getItem(key)
  return val ? JSON.parse(val) : null
}

export function removeLocal(key: string) {
  localStorage.removeItem(key)
}

export function setSession(key: string, value: any) {
  sessionStorage.setItem(key, JSON.stringify(value))
}

export function getSession<T = any>(key: string): T | null {
  const val = sessionStorage.getItem(key)
  return val ? JSON.parse(val) : null
}

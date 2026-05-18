/** 存储类型 */
type StorageType = 'localStorage' | 'sessionStorage'

/** 存储管理器选项 */
interface StorageManagerOptions {
  prefix?: string
  storageType?: StorageType
}

/** 存储项（支持TTL过期） */
interface StorageItem<T> {
  expiry?: number
  value: T
}

/**
 * 存储管理器
 * 支持前缀隔离、TTL过期、localStorage/sessionStorage切换
 */
class StorageManager {
  private prefix: string
  private storage: Storage

  constructor({
    prefix = '',
    storageType = 'localStorage',
  }: StorageManagerOptions = {}) {
    this.prefix = prefix
    this.storage =
      storageType === 'localStorage'
        ? window.localStorage
        : window.sessionStorage
  }

  /** 清除所有带前缀的缓存 */
  clear(): void {
    const keysToRemove: string[] = []
    for (let i = 0; i < this.storage.length; i++) {
      const key = this.storage.key(i)
      if (key && key.startsWith(this.prefix)) {
        keysToRemove.push(key)
      }
    }
    keysToRemove.forEach((key) => this.storage.removeItem(key))
  }

  /** 获取缓存项 */
  getItem<T>(key: string, defaultValue: null | T = null): null | T {
    const fullKey = this.getFullKey(key)
    const itemStr = this.storage.getItem(fullKey)
    if (!itemStr) {
      return defaultValue
    }

    try {
      const item: StorageItem<T> = JSON.parse(itemStr)
      if (item.expiry && Date.now() > item.expiry) {
        this.storage.removeItem(fullKey)
        return defaultValue
      }
      return item.value
    } catch (error) {
      console.error(`Error parsing item with key "${fullKey}":`, error)
      this.storage.removeItem(fullKey)
      return defaultValue
    }
  }

  /** 移除缓存项 */
  removeItem(key: string): void {
    const fullKey = this.getFullKey(key)
    this.storage.removeItem(fullKey)
  }

  /** 设置缓存项，支持TTL过期时间 */
  setItem<T>(key: string, value: T, ttl?: number): void {
    const fullKey = this.getFullKey(key)
    const expiry = ttl ? Date.now() + ttl : undefined
    const item: StorageItem<T> = { expiry, value }
    try {
      this.storage.setItem(fullKey, JSON.stringify(item))
    } catch (error) {
      console.error(`Error setting item with key "${fullKey}":`, error)
    }
  }

  /** 生成带前缀的完整key */
  private getFullKey(key: string): string {
    return `${this.prefix}-${key}`
  }
}

export { StorageManager }

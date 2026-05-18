import type { DeepPartial, InitialOptions, Preferences } from '@/types/preferences'

import { markRaw, reactive, readonly, watch } from 'vue'
import { useBreakpoints, useDebounceFn } from '@vueuse/core'

import { defaultPreferences } from './config'
import { StorageManager } from './storage-manager'
import { merge } from './merge'
import { updateCSSVariables } from './css-variables-updater'

const STORAGE_KEY = 'preferences'
const STORAGE_KEY_LOCALE = `${STORAGE_KEY}-locale`
const STORAGE_KEY_THEME = `${STORAGE_KEY}-theme`

/**
 * 偏好设置管理器（单例模式）
 * 负责偏好设置的状态管理、缓存持久化、CSS变量同步
 * 核心设计：内部state是reactive，对外暴露readonly代理
 * 所有修改必须通过updatePreferences()方法，不能直接赋值
 */
class PreferenceManager {
  private cache: null | StorageManager = null
  private initialPreferences: Preferences = defaultPreferences
  private isInitialized: boolean = false
  private savePreferences: (preference: Preferences) => void
  private state: Preferences = reactive<Preferences>({
    ...this.loadPreferences(),
  })

  constructor() {
    this.cache = new StorageManager()
    this.savePreferences = useDebounceFn(
      (preference: Preferences) => this._savePreferences(preference),
      150,
    )
  }

  /** 清除偏好设置缓存 */
  clearCache(): void {
    [STORAGE_KEY, STORAGE_KEY_LOCALE, STORAGE_KEY_THEME].forEach((key) => {
      this.cache?.removeItem(key)
    })
  }

  /** 获取初始偏好设置 */
  getInitialPreferences(): Preferences {
    return this.initialPreferences
  }

  /** 获取当前偏好设置（只读代理，外部不可直接修改） */
  getPreferences(): Readonly<Preferences> {
    return readonly(this.state)
  }

  /**
   * 初始化偏好设置
   * 合并缓存值、默认值和覆盖值，设置监听器
   */
  async initPreferences({ namespace, overrides }: InitialOptions): Promise<void> {
    if (this.isInitialized) return

    this.cache = new StorageManager({ prefix: namespace })
    this.initialPreferences = merge({}, overrides, defaultPreferences)

    const mergedPreference = merge(
      {},
      this.loadCachedPreferences() || {},
      this.initialPreferences,
    )

    this.updatePreferences(mergedPreference)
    this.setupWatcher()
    this.initPlatform()
    this.isInitialized = true
  }

  /** 重置偏好设置为初始值 */
  resetPreferences(): void {
    Object.assign(this.state, this.initialPreferences)
    this.savePreferences(this.state)
    ;[STORAGE_KEY, STORAGE_KEY_THEME, STORAGE_KEY_LOCALE].forEach((key) => {
      this.cache?.removeItem(key)
    })
    this.updatePreferences(this.state)
  }

  /**
   * 更新偏好设置（唯一修改入口）
   * 合并更新值到当前state，触发CSS变量同步，防抖保存缓存
   */
  updatePreferences(updates: DeepPartial<Preferences>): void {
    const mergedState = merge({}, updates, markRaw(this.state))
    Object.assign(this.state, mergedState)
    this.handleUpdates(updates)
    this.savePreferences(this.state)
  }

  /** 保存偏好设置到缓存（防抖后执行） */
  private _savePreferences(preference: Preferences): void {
    this.cache?.setItem(STORAGE_KEY, preference)
    this.cache?.setItem(STORAGE_KEY_LOCALE, preference.app.locale)
    this.cache?.setItem(STORAGE_KEY_THEME, preference.theme.mode)
  }

  /** 处理偏好设置更新，触发CSS变量同步和颜色模式切换 */
  private handleUpdates(updates: DeepPartial<Preferences>): void {
    const themeUpdates = updates.theme || {}
    const appUpdates = updates.app || {}

    if (themeUpdates && Object.keys(themeUpdates).length > 0) {
      updateCSSVariables(this.state)
    }

    if (
      Reflect.has(appUpdates, 'colorGrayMode') ||
      Reflect.has(appUpdates, 'colorWeakMode')
    ) {
      this.updateColorMode(this.state)
    }
  }

  /** 初始化平台标识 */
  private initPlatform(): void {
    const dom = document.documentElement
    const isMac = navigator.platform.toUpperCase().indexOf('MAC') >= 0
    dom.dataset.platform = isMac ? 'macOs' : 'window'
  }

  /** 从缓存加载偏好设置 */
  private loadCachedPreferences(): Preferences | null {
    return this.cache?.getItem<Preferences>(STORAGE_KEY) ?? null
  }

  /** 加载偏好设置（优先从缓存，否则用默认值） */
  private loadPreferences(): Preferences {
    return this.loadCachedPreferences() || { ...defaultPreferences } as Preferences
  }

  /** 设置响应式监听器 */
  private setupWatcher(): void {
    if (this.isInitialized) return

    /* 监听断点变化（移动端自适应） */
    const breakpoints = useBreakpoints({ md: 768 })
    const isMobile = breakpoints.smaller('md')
    watch(
      () => isMobile.value,
      (val) => {
        this.updatePreferences({
          app: { isMobile: val },
        })
      },
      { immediate: true },
    )

    /* 监听系统主题变化 */
    window
      .matchMedia('(prefers-color-scheme: dark)')
      .addEventListener('change', ({ matches: isDark }) => {
        if (this.state.theme.mode === 'auto') {
          this.updatePreferences({
            theme: { mode: isDark ? 'dark' : 'light' },
          })
          this.updatePreferences({
            theme: { mode: 'auto' },
          })
        }
      })
  }

  /** 更新颜色模式（灰度/色弱） */
  private updateColorMode(preference: Preferences): void {
    if (preference.app) {
      const { colorGrayMode, colorWeakMode } = preference.app
      const dom = document.documentElement
      const COLOR_WEAK = 'invert-mode'
      const COLOR_GRAY = 'grayscale-mode'
      colorWeakMode
        ? dom.classList.add(COLOR_WEAK)
        : dom.classList.remove(COLOR_WEAK)
      colorGrayMode
        ? dom.classList.add(COLOR_GRAY)
        : dom.classList.remove(COLOR_GRAY)
    }
  }
}

const preferencesManager = new PreferenceManager()

export { PreferenceManager, preferencesManager }

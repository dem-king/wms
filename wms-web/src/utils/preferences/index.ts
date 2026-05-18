/** 偏好设置模块统一导出 */
import { preferencesManager } from './preferences'

export const preferences = preferencesManager.getPreferences()
export const updatePreferences = preferencesManager.updatePreferences.bind(preferencesManager)
export const resetPreferences = preferencesManager.resetPreferences.bind(preferencesManager)
export const clearPreferencesCache = preferencesManager.clearCache.bind(preferencesManager)
export const initPreferences = preferencesManager.initPreferences.bind(preferencesManager)
export { preferencesManager }
export * from './use-preferences'
export * from './config'
export * from './constants'
export * from './css-variables-updater'

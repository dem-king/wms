import type { Preferences } from '@/types/preferences'

import { BUILT_IN_THEME_PRESETS } from './constants'
import { generatorColorVariables } from './color-generator'
import { updateCSSVariables as injectCSSVariables } from './inject-css-variables'

/**
 * 判断是否为暗色主题
 * @param theme 主题模式（'auto' | 'dark' | 'light'）
 */
export function isDarkTheme(theme: string): boolean {
  let dark = theme === 'dark'
  if (theme === 'auto') {
    dark = window.matchMedia('(prefers-color-scheme: dark)').matches
  }
  return dark
}

/**
 * 根据偏好设置更新CSS变量
 * 包括暗色模式切换、主题色更新、圆角更新
 * @param preferences 完整偏好设置
 */
export function updateCSSVariables(preferences: Preferences): void {
  const root = document.documentElement
  if (!root) return

  const theme = preferences?.theme ?? {}
  const { builtinType, mode, radius } = theme

  /* 切换 dark/light 类 */
  if (Reflect.has(theme, 'mode')) {
    const dark = isDarkTheme(mode)
    root.classList.toggle('dark', dark)
  }

  /* 设置 data-theme 属性 */
  if (Reflect.has(theme, 'builtinType')) {
    const rootTheme = root.dataset.theme
    if (rootTheme !== builtinType) {
      root.dataset.theme = builtinType
    }
  }

  /* 获取当前内置主题的主色调 */
  const currentBuiltType = BUILT_IN_THEME_PRESETS.find(
    (item) => item.type === builtinType,
  )

  let builtinTypeColorPrimary: string | undefined = ''

  if (currentBuiltType) {
    const isDark = isDarkTheme(preferences.theme.mode)
    const color = isDark
      ? currentBuiltType.darkPrimaryColor || currentBuiltType.primaryColor
      : currentBuiltType.primaryColor
    builtinTypeColorPrimary = color || currentBuiltType.color
  }

  /* 更新主色调相关CSS变量 */
  if (
    builtinTypeColorPrimary ||
    Reflect.has(theme, 'colorPrimary') ||
    Reflect.has(theme, 'colorDestructive') ||
    Reflect.has(theme, 'colorSuccess') ||
    Reflect.has(theme, 'colorWarning')
  ) {
    updateMainColorVariables(preferences)
  }

  /* 更新圆角 */
  if (Reflect.has(theme, 'radius')) {
    document.documentElement.style.setProperty('--radius', `${radius}rem`)
  }
}

/**
 * 更新主色、成功色、警告色、危险色相关CSS变量
 */
function updateMainColorVariables(preference: Preferences): void {
  if (!preference.theme) return

  const { colorDestructive, colorPrimary, colorSuccess, colorWarning } =
    preference.theme

  const colorVariables = generatorColorVariables([
    { color: colorPrimary, name: 'primary' },
    { alias: 'warning', color: colorWarning, name: 'yellow' },
    { alias: 'success', color: colorSuccess, name: 'green' },
    { alias: 'destructive', color: colorDestructive, name: 'red' },
  ])

  const colorMappings: Record<string, string> = {
    '--green-500': '--success',
    '--primary-500': '--primary',
    '--red-500': '--destructive',
    '--yellow-500': '--warning',
  }

  Object.entries(colorMappings).forEach(([sourceVar, targetVar]) => {
    const colorValue = colorVariables[sourceVar]
    if (colorValue) {
      document.documentElement.style.setProperty(targetVar, colorValue)
    }
  })

  injectCSSVariables(colorVariables)
}

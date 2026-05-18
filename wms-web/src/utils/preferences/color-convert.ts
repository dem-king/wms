import { TinyColor } from '@ctrl/tinycolor'

/**
 * 将颜色值转换为HSL格式字符串
 * @param color 颜色值（支持hex/rgb/hsl等格式）
 * @returns HSL格式字符串，如 "212 100% 45%"
 */
export function convertToHsl(color: string): string {
  const { a, h, l, s } = new TinyColor(color).toHsl()
  const hsl = `${Math.round(h)} ${Math.round(s * 100)}% ${Math.round(l * 100)}%`
  return a < 1 ? `${hsl} / ${a}` : hsl
}

/**
 * 将颜色值转换为HSL格式的CSS变量值
 * @param color 颜色值
 * @returns HSL格式CSS变量值
 */
export function convertToHslCssVar(color: string): string {
  const { a, h, l, s } = new TinyColor(color).toHsl()
  const hsl = `${Math.round(h)} ${Math.round(s * 100)}% ${Math.round(l * 100)}%`
  return a < 1 ? `${hsl} / ${a}` : hsl
}

export { TinyColor }

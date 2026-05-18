import { getColors } from 'theme-colors'
import { TinyColor } from '@ctrl/tinycolor'
import { convertToHslCssVar } from './color-convert'

/** 颜色项配置 */
interface ColorItem {
  alias?: string
  color: string
  name: string
}

/**
 * 根据主色生成完整的色阶变量映射
 * 生成从50到950的色阶，并映射为CSS变量名
 * @param colorItems 颜色项列表
 * @returns CSS变量名到颜色值的映射
 */
export function generatorColorVariables(colorItems: ColorItem[]): Record<string, string> {
  const colorVariables: Record<string, string> = {}

  colorItems.forEach(({ alias, color, name }) => {
    if (color) {
      const colorsMap = getColors(new TinyColor(color).toHexString())
      let mainColor = colorsMap['500']

      Object.keys(colorsMap).forEach((key) => {
        const colorValue = colorsMap[key]
        if (colorValue) {
          const hslColor = convertToHslCssVar(colorValue)
          colorVariables[`--${name}-${key}`] = hslColor
          if (alias) {
            colorVariables[`--${alias}-${key}`] = hslColor
          }
          if (key === '500') {
            mainColor = hslColor
          }
        }
      })
      if (alias && mainColor) {
        colorVariables[`--${alias}`] = mainColor
      }
    }
  })
  return colorVariables
}

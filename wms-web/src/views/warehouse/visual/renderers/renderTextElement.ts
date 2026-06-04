/**
 * 文字标注渲染器
 * 在指定坐标渲染Konva Text节点
 */

import type { LayoutElementVo, RenderContext } from '../types/layout-element'
import { parseStyleData, mergeStyle } from '../types/layout-element'
import type { LayoutElementRenderer, ElementRenderResult } from './index'

/**
 * 文字标注渲染器实现
 * elementType='text'；在指定坐标渲染文字
 */
export const renderTextElement: LayoutElementRenderer = {
  elementType: 'text',

  /**
   * 渲染文字标注元素
   * @param element 布局元素数据
   * @param context 渲染上下文
   * @returns 渲染结果
   */
  render(element: LayoutElementVo, context: RenderContext): ElementRenderResult {
    const customStyle = parseStyleData(element.styleData)
    const style = mergeStyle('text', customStyle)

    const text = element.labelText || element.elementName || ''
    const fontSize = style.fontSize ?? 14
    const fontColor = context.isSelected ? '#409eff' : context.isHighlighted ? '#1d7df2' : style.fontColor ?? '#333333'

    return {
      type: 'text',
      config: {
        x: element.positionX,
        y: element.positionY,
        text,
        fontSize,
        fill: fontColor,
        fontStyle: context.isSelected ? 'bold' : 'normal',
        rotation: element.rotation ?? 0,
        // 选中时添加背景衬底
        ...(context.isSelected ? {
          textDecoration: 'underline',
        } : {}),
      },
    }
  },
}
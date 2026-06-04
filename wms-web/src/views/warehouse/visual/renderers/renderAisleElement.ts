/**
 * 通道元素渲染器
 * 渲染矩形通道区域，浅色填充和虚线边框
 */

import type { LayoutElementVo, RenderContext } from '../types/layout-element'
import { parseStyleData, mergeStyle } from '../types/layout-element'
import type { LayoutElementRenderer, ElementRenderResult } from './index'

/**
 * 通道元素渲染器实现
 * elementType='aisle'；渲染矩形通道区域，虚线边框
 */
export const renderAisleElement: LayoutElementRenderer = {
  elementType: 'aisle',

  /**
   * 渲染通道元素
   * @param element 布局元素数据
   * @param context 渲染上下文
   * @returns 渲染结果（group包含通道矩形+方向标注文字）
   */
  render(element: LayoutElementVo, context: RenderContext): ElementRenderResult {
    const customStyle = parseStyleData(element.styleData)
    const style = mergeStyle('aisle', customStyle)

    const strokeWidth = context.isSelected
      ? (style.strokeWidth ?? 1) + 2
      : context.isHighlighted
        ? (style.strokeWidth ?? 1) + 1
        : style.strokeWidth ?? 1
    const strokeColor = context.isSelected ? '#409eff' : context.isHighlighted ? '#1d7df2' : style.strokeColor ?? '#91d5ff'

    const children: ElementRenderResult[] = []

    // 通道矩形区域
    children.push({
      type: 'rect',
      config: {
        x: 0,
        y: 0,
        width: element.layoutWidth,
        height: element.layoutHeight,
        fill: style.fillColor ?? '#f0f9ff',
        stroke: strokeColor,
        strokeWidth,
        dash: style.dashPattern ?? [10, 5],
        cornerRadius: 4,
        rotation: element.rotation ?? 0,
      },
    })

    // 通道方向标注（显示元素名称）
    if (element.labelText || element.elementName) {
      const text = element.labelText || element.elementName
      children.push({
        type: 'text',
        config: {
          x: element.layoutWidth / 2,
          y: element.layoutHeight / 2,
          text,
          fontSize: style.fontSize ?? 12,
          fill: style.fontColor ?? '#91d5ff',
          fontStyle: 'italic',
          align: 'center',
          verticalAlign: 'middle',
          width: element.layoutWidth - 20,
          offsetX: (element.layoutWidth - 20) / 2,
          offsetY: 6,
        },
      })
    }

    return {
      type: 'group',
      config: {
        x: element.positionX,
        y: element.positionY,
      },
      children,
    }
  },
}
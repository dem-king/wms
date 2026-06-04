/**
 * 墙体元素渲染器
 * 渲染线段或矩形墙体，深色填充和边框
 */

import type { LayoutElementVo, RenderContext, StyleData, PointData } from '../types/layout-element'
import { parseStyleData, parsePointData, mergeStyle } from '../types/layout-element'
import type { LayoutElementRenderer, ElementRenderResult } from './index'

/**
 * 墙体元素渲染器实现
 * elementType='wall'；根据shapeType渲染线段或矩形
 */
export const renderWallElement: LayoutElementRenderer = {
  elementType: 'wall',

  /**
   * 渲染墙体元素
   * @param element 布局元素数据
   * @param context 渲染上下文
   * @returns 渲染结果
   */
  render(element: LayoutElementVo, context: RenderContext): ElementRenderResult {
    const customStyle = parseStyleData(element.styleData)
    const style = mergeStyle('wall', customStyle)

    // 选中/高亮时描边加粗
    const strokeWidth = context.isSelected
      ? (style.strokeWidth ?? 2) + 2
      : context.isHighlighted
        ? (style.strokeWidth ?? 2) + 1
        : style.strokeWidth ?? 2
    const strokeColor = context.isSelected ? '#409eff' : context.isHighlighted ? '#1d7df2' : style.strokeColor ?? '#333333'

    if (element.shapeType === 'line') {
      return renderWallLine(element, style, strokeWidth, strokeColor)
    }

    // 默认渲染矩形墙体
    return renderWallRect(element, style, strokeWidth, strokeColor)
  },

  /**
   * 2.5D模式：渲染墙体顶部和侧面阴影
   */
  render25D(element: LayoutElementVo, _context: RenderContext): ElementRenderResult[] {
    const shadowOffset = 8
    const results: ElementRenderResult[] = []

    // 顶部阴影
    results.push({
      type: 'rect',
      config: {
        x: element.positionX + 4,
        y: element.positionY - shadowOffset,
        width: Math.max(element.layoutWidth - 8, 20),
        height: shadowOffset,
        fill: '#d9d9d9',
        cornerRadius: 4,
        opacity: 0.6,
      },
    })

    // 右侧阴影
    results.push({
      type: 'rect',
      config: {
        x: element.positionX + element.layoutWidth - 4,
        y: element.positionY + 4,
        width: shadowOffset,
        height: Math.max(element.layoutHeight - 8, 20),
        fill: '#bfbfbf',
        cornerRadius: 4,
        opacity: 0.6,
      },
    })

    return results
  },
}

/**
 * 渲染矩形墙体
 */
function renderWallRect(
  element: LayoutElementVo,
  style: StyleData,
  strokeWidth: number,
  strokeColor: string,
): ElementRenderResult {
  return {
    type: 'rect',
    config: {
      x: element.positionX,
      y: element.positionY,
      width: element.layoutWidth,
      height: element.layoutHeight,
      fill: style.fillColor ?? '#4a4a4a',
      stroke: strokeColor,
      strokeWidth,
      rotation: element.rotation ?? 0,
      cornerRadius: 2,
    },
  }
}

/**
 * 渲染线段墙体
 */
function renderWallLine(
  element: LayoutElementVo,
  style: StyleData,
  strokeWidth: number,
  strokeColor: string,
): ElementRenderResult {
  const pointData = parsePointData(element.pointData) as PointData | null
  const points = pointData?.points

  if (!points || points.length < 2) {
    // 点位数据不足，降级为矩形
    console.warn('[renderWallElement] 线段墙体点位不足，降级为矩形:', element.elementName)
    return renderWallRect(element, style, strokeWidth, strokeColor)
  }

  // Konva Line的points格式为[x1,y1,x2,y2,...]
  const flatPoints = points.flatMap(p => [p.x, p.y])

  return {
    type: 'line',
    config: {
      x: element.positionX,
      y: element.positionY,
      points: flatPoints,
      stroke: strokeColor,
      strokeWidth: Math.max(strokeWidth, 4), // 墙体线段至少4px宽
      lineCap: 'round',
      lineJoin: 'round',
    },
  }
}
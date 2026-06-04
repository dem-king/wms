/**
 * 预留区元素渲染器
 * 渲染矩形预留区，斜线填充图案和警示色边框
 */

import type { LayoutElementVo, RenderContext } from '../types/layout-element'
import { parseStyleData, mergeStyle } from '../types/layout-element'
import type { LayoutElementRenderer, ElementRenderResult } from './index'

/** 斜线填充间距 */
const HATCH_SPACING = 12
/** 斜线填充线宽 */
const HATCH_STROKE_WIDTH = 1

/**
 * 预留区元素渲染器实现
 * elementType='reserved'；渲染矩形预留区，斜线填充图案
 */
export const renderReservedElement: LayoutElementRenderer = {
  elementType: 'reserved',

  /**
   * 渲染预留区元素
   * @param element 布局元素数据
   * @param context 渲染上下文
   * @returns 渲染结果（group包含矩形+斜线填充+标注文字）
   */
  render(element: LayoutElementVo, context: RenderContext): ElementRenderResult {
    const customStyle = parseStyleData(element.styleData)
    const style = mergeStyle('reserved', customStyle)

    const strokeWidth = context.isSelected
      ? (style.strokeWidth ?? 2) + 2
      : context.isHighlighted
        ? (style.strokeWidth ?? 2) + 1
        : style.strokeWidth ?? 2
    const strokeColor = context.isSelected ? '#409eff' : context.isHighlighted ? '#1d7df2' : style.strokeColor ?? '#fa8c16'

    const children: ElementRenderResult[] = []

    // 预留区矩形背景
    children.push({
      type: 'rect',
      config: {
        x: 0,
        y: 0,
        width: element.layoutWidth,
        height: element.layoutHeight,
        fill: style.fillColor ?? '#fff7e6',
        stroke: strokeColor,
        strokeWidth,
        cornerRadius: 4,
        rotation: element.rotation ?? 0,
      },
    })

    // 斜线填充图案（使用多条line模拟hatch pattern）
    const hatchLines = generateHatchLines(element.layoutWidth, element.layoutHeight)
    for (const line of hatchLines) {
      children.push({
        type: 'line',
        config: {
          x: line.x,
          y: line.y,
          points: line.points,
          stroke: strokeColor,
          strokeWidth: HATCH_STROKE_WIDTH,
          opacity: 0.3,
          listening: false,
        },
      })
    }

    // 预留区标注文字
    if (element.labelText || element.elementName) {
      const text = element.labelText || element.elementName
      children.push({
        type: 'text',
        config: {
          x: element.layoutWidth / 2,
          y: element.layoutHeight / 2,
          text,
          fontSize: style.fontSize ?? 12,
          fill: style.fontColor ?? '#fa8c16',
          fontStyle: 'bold',
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

/**
 * 生成斜线填充线条
 * @param width 区域宽度
 * @param height 区域高度
 * @returns 线条配置数组
 */
function generateHatchLines(width: number, height: number): Array<{ x: number; y: number; points: number[] }> {
  const lines: Array<{ x: number; y: number; points: number[] }> = []
  const totalLength = width + height

  // 从左下到右上方向的斜线
  for (let offset = 0; offset < totalLength; offset += HATCH_SPACING) {
    // 计算斜线与矩形边界的交点
    const x1 = Math.max(0, offset - height)
    const y1 = Math.min(height, offset)
    const x2 = Math.min(width, offset)
    const y2 = Math.max(0, offset - width)

    // 跳过无效线段
    if (x1 >= x2 && y1 <= y2) {
      continue
    }

    lines.push({
      x: 0,
      y: 0,
      points: [x1, y1, x2, y2],
    })
  }

  return lines
}
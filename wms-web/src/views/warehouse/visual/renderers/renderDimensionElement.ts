/**
 * 尺寸标注渲染器
 * 渲染建筑制图标准的标注线（端点标记+距离文本）
 */

import type {
  LayoutElementVo,
  RenderContext,

  DimensionPointData,
} from '../types/layout-element'
import {
  parseStyleData,
  parsePointData,
  mergeStyle,
  calculateDimensionText,
  DIMENSION_END_MARK_LENGTH,
  DIMENSION_MIN_FONT_SIZE,
} from '../types/layout-element'
import type { LayoutElementRenderer, ElementRenderResult } from './index'

/**
 * 尺寸标注渲染器实现
 * elementType='dimension'；渲染主标注线+端点标记+标注文本
 */
export const renderDimensionElement: LayoutElementRenderer = {
  elementType: 'dimension',

  /**
   * 渲染尺寸标注元素
   * @param element 布局元素数据
   * @param context 渲染上下文
   * @returns 渲染结果（group包含标注线+端点标记+文本）
   */
  render(element: LayoutElementVo, context: RenderContext): ElementRenderResult {
    const customStyle = parseStyleData(element.styleData)
    const style = mergeStyle('dimension', customStyle)

    const strokeWidth = context.isSelected
      ? (style.strokeWidth ?? 1) + 1
      : style.strokeWidth ?? 1
    const strokeColor = context.isSelected ? '#409eff' : style.strokeColor ?? '#333333'

    // 解析起止点
    const pointData = parsePointData(element.pointData) as DimensionPointData | null
    if (!pointData?.start || !pointData?.end) {
      console.warn('[renderDimensionElement] 尺寸标注缺少起止点数据:', element.elementName)
      return { type: 'group', config: {}, children: [] }
    }

    const { start, end } = pointData

    // 起止点重合时不渲染
    if (start.x === end.x && start.y === end.y) {
      console.warn('[renderDimensionElement] 尺寸标注起止点重合:', element.elementName)
      return { type: 'group', config: {}, children: [] }
    }

    const children: ElementRenderResult[] = []

    // 1. 主标注线
    children.push({
      type: 'line',
      config: {
        x: 0,
        y: 0,
        points: [start.x, start.y, end.x, end.y],
        stroke: strokeColor,
        strokeWidth,
      },
    })

    // 2. 端点标记线（垂直于标注线方向的短标记）
    const angle = Math.atan2(end.y - start.y, end.x - start.x)
    const perpAngle = angle + Math.PI / 2
    const markLen = DIMENSION_END_MARK_LENGTH

    // 起点标记
    const startMarkDx = Math.cos(perpAngle) * markLen
    const startMarkDy = Math.sin(perpAngle) * markLen
    children.push({
      type: 'line',
      config: {
        x: 0,
        y: 0,
        points: [
          start.x - startMarkDx, start.y - startMarkDy,
          start.x + startMarkDx, start.y + startMarkDy,
        ],
        stroke: strokeColor,
        strokeWidth,
      },
    })

    // 终点标记
    children.push({
      type: 'line',
      config: {
        x: 0,
        y: 0,
        points: [
          end.x - startMarkDx, end.y - startMarkDy,
          end.x + startMarkDx, end.y + startMarkDy,
        ],
        stroke: strokeColor,
        strokeWidth,
      },
    })

    // 3. 标注文本
    const midX = (start.x + end.x) / 2
    const midY = (start.y + end.y) / 2

    // 计算标注文本（优先使用labelText，否则自动计算）
    let dimensionText = element.labelText
    if (!dimensionText) {
      // 使用比例尺自动计算（layoutScale通过context传入，这里暂用null）
      dimensionText = calculateDimensionText(start.x, start.y, end.x, end.y, null)
    }

    // 缩放时文本字号保底
    const baseFontSize = style.fontSize ?? 12
    const scaledFontSize = Math.max(baseFontSize / context.scale, DIMENSION_MIN_FONT_SIZE)

    // 倾斜标注线文本旋转角度（度数）
    const textRotation = (angle * 180) / Math.PI
    // 文本偏移量（沿垂直方向偏移，避免与标注线重叠）
    const textOffset = scaledFontSize + 4

    children.push({
      type: 'text',
      config: {
        x: midX,
        y: midY - textOffset,
        text: dimensionText,
        fontSize: scaledFontSize,
        fill: style.fontColor ?? '#333333',
        rotation: textRotation,
        align: 'center',
        // 白色背景衬底（通过padding模拟）
        padding: 2,
      },
    })

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
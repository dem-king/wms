/**
 * 设备元素渲染器
 * 根据shapeType渲染对应图标形状
 */

import type { LayoutElementVo, RenderContext, StyleData, PointData } from '../types/layout-element'
import { parseStyleData, parsePointData, mergeStyle } from '../types/layout-element'
import type { LayoutElementRenderer, ElementRenderResult } from './index'

/**
 * 设备元素渲染器实现
 * elementType='device'；根据shapeType渲染对应图标形状
 */
export const renderDeviceElement: LayoutElementRenderer = {
  elementType: 'device',

  /**
   * 渲染设备元素
   * @param element 布局元素数据
   * @param context 渲染上下文
   * @returns 渲染结果
   */
  render(element: LayoutElementVo, context: RenderContext): ElementRenderResult {
    const customStyle = parseStyleData(element.styleData)
    const style = mergeStyle('device', customStyle)

    const strokeWidth = context.isSelected
      ? (style.strokeWidth ?? 1) + 2
      : context.isHighlighted
        ? (style.strokeWidth ?? 1) + 1
        : style.strokeWidth ?? 1
    const strokeColor = context.isSelected ? '#409eff' : context.isHighlighted ? '#1d7df2' : style.strokeColor ?? '#1890ff'

    const children: ElementRenderResult[] = []

    // 根据形状类型渲染
    switch (element.shapeType) {
      case 'circle':
        children.push({
          type: 'circle',
          config: {
            x: element.layoutWidth / 2,
            y: element.layoutHeight / 2,
            radius: Math.min(element.layoutWidth, element.layoutHeight) / 2,
            fill: style.fillColor ?? '#e6f7ff',
            stroke: strokeColor,
            strokeWidth,
          },
        })
        break
      case 'polygon':
        children.push(renderDevicePolygon(element, style, strokeWidth, strokeColor))
        break
      case 'line':
        children.push(renderDeviceLine(element, style, strokeWidth, strokeColor))
        break
      default:
        // 默认矩形
        children.push({
          type: 'rect',
          config: {
            x: 0,
            y: 0,
            width: element.layoutWidth,
            height: element.layoutHeight,
            fill: style.fillColor ?? '#e6f7ff',
            stroke: strokeColor,
            strokeWidth,
            cornerRadius: 4,
            rotation: element.rotation ?? 0,
          },
        })
    }

    // 设备名称标注
    if (element.labelText || element.elementName) {
      const text = element.labelText || element.elementName
      children.push({
        type: 'text',
        config: {
          x: element.layoutWidth / 2,
          y: element.layoutHeight + 4,
          text,
          fontSize: style.fontSize ?? 11,
          fill: style.fontColor ?? '#1890ff',
          align: 'center',
          width: element.layoutWidth,
          offsetX: element.layoutWidth / 2,
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

  /**
   * 2.5D模式：渲染设备顶部和侧面阴影
   */
  render25D(element: LayoutElementVo, _context: RenderContext): ElementRenderResult[] {
    const shadowOffset = 6
    return [
      {
        type: 'rect',
        config: {
          x: element.positionX + 3,
          y: element.positionY - shadowOffset,
          width: Math.max(element.layoutWidth - 6, 10),
          height: shadowOffset,
          fill: '#d9d9d9',
          cornerRadius: 3,
          opacity: 0.5,
        },
      },
      {
        type: 'rect',
        config: {
          x: element.positionX + element.layoutWidth - 3,
          y: element.positionY + 3,
          width: shadowOffset,
          height: Math.max(element.layoutHeight - 6, 10),
          fill: '#bfbfbf',
          cornerRadius: 3,
          opacity: 0.5,
        },
      },
    ]
  },
}

/**
 * 渲染多边形设备
 */
function renderDevicePolygon(
  element: LayoutElementVo,
  style: StyleData,
  strokeWidth: number,
  strokeColor: string,
): ElementRenderResult {
  const pointData = parsePointData(element.pointData) as PointData | null
  const points = pointData?.points

  if (!points || points.length < 3) {
    // 顶点不足，降级为矩形
    console.warn('[renderDeviceElement] 多边形设备顶点不足，降级为矩形:', element.elementName)
    return {
      type: 'rect',
      config: {
        x: 0,
        y: 0,
        width: element.layoutWidth,
        height: element.layoutHeight,
        fill: style.fillColor ?? '#e6f7ff',
        stroke: strokeColor,
        strokeWidth,
        cornerRadius: 4,
      },
    }
  }

  const flatPoints = points.flatMap(p => [p.x, p.y])
  return {
    type: 'line',
    config: {
      x: 0,
      y: 0,
      points: flatPoints,
      fill: style.fillColor ?? '#e6f7ff',
      stroke: strokeColor,
      strokeWidth,
      closed: true,
    },
  }
}

/**
 * 渲染线段设备
 */
function renderDeviceLine(
  element: LayoutElementVo,
  style: StyleData,
  strokeWidth: number,
  strokeColor: string,
): ElementRenderResult {
  const pointData = parsePointData(element.pointData) as PointData | null
  const points = pointData?.points

  if (!points || points.length < 2) {
    console.warn('[renderDeviceElement] 线段设备点位不足，降级为矩形:', element.elementName)
    return {
      type: 'rect',
      config: {
        x: 0,
        y: 0,
        width: element.layoutWidth,
        height: element.layoutHeight,
        fill: style.fillColor ?? '#e6f7ff',
        stroke: strokeColor,
        strokeWidth,
        cornerRadius: 4,
      },
    }
  }

  const flatPoints = points.flatMap(p => [p.x, p.y])
  return {
    type: 'line',
    config: {
      x: 0,
      y: 0,
      points: flatPoints,
      stroke: strokeColor,
      strokeWidth: Math.max(strokeWidth, 2),
      lineCap: 'round',
    },
  }
}
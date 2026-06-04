/**
 * 多边形区域渲染器
 * 支持shapeType=polygon的区域渲染，使用Konva Line组件
 */

import type { Point } from '../types/layout-element'

/** 多边形最小顶点数 */
const MIN_POLYGON_POINTS = 3

/**
 * 解析多边形顶点数据
 * @param polygonPointsStr 多边形顶点JSON字符串
 * @returns 顶点坐标数组，解析失败或顶点不足返回null
 */
export function parsePolygonPoints(polygonPointsStr: string | null | undefined): Point[] | null {
  if (!polygonPointsStr) {
    return null
  }

  try {
    const points = JSON.parse(polygonPointsStr)
    if (!Array.isArray(points) || points.length < MIN_POLYGON_POINTS) {
      console.warn('[renderPolygonArea] 多边形顶点不足，需要至少3个顶点:', polygonPointsStr)
      return null
    }

    // 校验每个顶点格式
    const validPoints = points.filter(
      (p: unknown): p is Point =>
        typeof p === 'object' && p !== null &&
        typeof (p as Point).x === 'number' && typeof (p as Point).y === 'number',
    )

    if (validPoints.length < MIN_POLYGON_POINTS) {
      console.warn('[renderPolygonArea] 有效多边形顶点不足，降级为矩形渲染')
      return null
    }

    return validPoints
  } catch {
    console.warn('[renderPolygonArea] 多边形顶点数据JSON解析失败:', polygonPointsStr)
    return null
  }
}

/**
 * 将顶点数组转换为Konva Line的flat points格式 [x1,y1,x2,y2,...]
 * @param points 顶点数组
 * @returns flat points数组
 */
export function toFlatPoints(points: Point[]): number[] {
  return points.flatMap(p => [p.x, p.y])
}

/**
 * 计算多边形包围盒
 * @param points 顶点数组
 * @returns 包围盒 {x, y, width, height}
 */
export function calculatePolygonBounds(points: Point[]): { x: number; y: number; width: number; height: number } {
  const xs = points.map(p => p.x)
  const ys = points.map(p => p.y)
  const minX = xs.reduce((a, b) => Math.min(a, b))
  const minY = ys.reduce((a, b) => Math.min(a, b))
  const maxX = xs.reduce((a, b) => Math.max(a, b))
  const maxY = ys.reduce((a, b) => Math.max(a, b))
  return {
    x: minX,
    y: minY,
    width: maxX - minX,
    height: maxY - minY,
  }
}

/**
 * 计算多边形区域标题位置
 * 优先使用labelX/labelY，否则取包围盒顶部居中
 * @param bounds 多边形包围盒
 * @param labelX 标题相对X坐标
 * @param labelY 标题相对Y坐标
 * @returns 标题绝对坐标
 */
export function resolveLabelPosition(
  bounds: { x: number; y: number; width: number; height: number },
  labelX?: number | null,
  labelY?: number | null,
): { x: number; y: number } {
  if (labelX != null && labelY != null) {
    // labelX/labelY为区域内的相对坐标
    return { x: bounds.x + labelX, y: bounds.y + labelY }
  }
  // 默认：包围盒顶部居中
  return {
    x: bounds.x + bounds.width / 2,
    y: bounds.y + 16,
  }
}

/**
 * 检测两个区域是否重叠（基于包围盒）
 * @param a 区域A包围盒
 * @param b 区域B包围盒
 * @returns 是否重叠
 */
export function detectAreaOverlap(
  a: { x: number; y: number; width: number; height: number },
  b: { x: number; y: number; width: number; height: number },
): boolean {
  return !(
    a.x + a.width <= b.x ||
    b.x + b.width <= a.x ||
    a.y + a.height <= b.y ||
    b.y + b.height <= a.y
  )
}

/**
 * 生成多边形区域的Konva渲染配置
 * @param points 顶点数组
 * @param isSelected 是否选中
 * @param isHighlighted 是否高亮
 * @param isDisabled 是否禁用
 * @returns Konva Line配置
 */
export function getPolygonAreaConfig(
  points: Point[],
  isSelected: boolean,
  isHighlighted: boolean,
  isDisabled: boolean,
) {
  const flatPoints = toFlatPoints(points)

  return {
    points: flatPoints,
    fill: isDisabled ? '#f4f4f5' : isHighlighted ? '#d7ebff' : isSelected ? '#e8f3ff' : '#f7fbff',
    stroke: isHighlighted ? '#1d7df2' : isSelected ? '#409eff' : '#cdddf5',
    strokeWidth: isHighlighted ? 3 : isSelected ? 2 : 1,
    closed: true,
    draggable: false,
  }
}
/**
 * 布局元素渲染器注册表
 * 采用策略模式按元素类型解耦渲染逻辑
 */

import type { LayoutElementVo, RenderContext } from '../types/layout-element'

/**
 * 渲染器接口
 * 新增元素类型只需实现此接口并注册
 */
export interface LayoutElementRenderer {
  /** 支持的元素类型 */
  elementType: string
  /**
   * 渲染元素到指定Konva Layer
   * 返回Konva节点配置对象（用于vue-konva声明式渲染）
   */
  render(element: LayoutElementVo, context: RenderContext): ElementRenderResult
  /**
   * 2.5D模式下的额外渲染配置（阴影等）
   */
  render25D?(element: LayoutElementVo, context: RenderContext): ElementRenderResult[]
}

/**
 * 元素渲染结果
 * 描述一个Konva节点的渲染配置
 */
export interface ElementRenderResult {
  /** 节点类型（rect/line/text/circle/group） */
  type: 'rect' | 'line' | 'text' | 'circle' | 'group'
  /** Konva节点配置 */
  config: Record<string, unknown>
  /** 子节点（group类型时使用） */
  children?: ElementRenderResult[]
}

/** 渲染器注册表 */
const rendererRegistry = new Map<string, LayoutElementRenderer>()

/**
 * 注册渲染器
 * @param renderer 渲染器实例
 */
export function registerRenderer(renderer: LayoutElementRenderer): void {
  rendererRegistry.set(renderer.elementType, renderer)
}

/**
 * 获取渲染器
 * @param elementType 元素类型
 * @returns 渲染器实例，未找到返回undefined
 */
export function getRenderer(elementType: string): LayoutElementRenderer | undefined {
  return rendererRegistry.get(elementType)
}

/**
 * 渲染布局元素（统一入口）
 * @param element 布局元素
 * @param context 渲染上下文
 * @returns 渲染结果，未找到渲染器时返回null
 */
export function renderLayoutElement(
  element: LayoutElementVo,
  context: RenderContext,
): ElementRenderResult | null {
  const renderer = getRenderer(element.elementType)
  if (!renderer) {
    console.warn(`[renderers] 未注册的元素类型: ${element.elementType}`)
    return null
  }
  return renderer.render(element, context)
}

/**
 * 渲染布局元素2.5D效果
 * @param element 布局元素
 * @param context 渲染上下文
 * @returns 2.5D渲染结果数组
 */
export function renderLayoutElement25D(
  element: LayoutElementVo,
  context: RenderContext,
): ElementRenderResult[] {
  const renderer = getRenderer(element.elementType)
  if (!renderer?.render25D) {
    return []
  }
  return renderer.render25D(element, context)
}

// ==================== 注册所有渲染器 ====================

import { renderWallElement } from './renderWallElement'
import { renderAisleElement } from './renderAisleElement'
import { renderReservedElement } from './renderReservedElement'
import { renderDeviceElement } from './renderDeviceElement'
import { renderTextElement } from './renderTextElement'
import { renderDimensionElement } from './renderDimensionElement'

// 注册6种元素渲染器
registerRenderer(renderWallElement)
registerRenderer(renderAisleElement)
registerRenderer(renderReservedElement)
registerRenderer(renderDeviceElement)
registerRenderer(renderTextElement)
registerRenderer(renderDimensionElement)
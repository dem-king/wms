import { describe, expect, it } from 'vitest'
import {
  registerRenderer,
  getRenderer,
  renderLayoutElement,
  renderLayoutElement25D,
  type LayoutElementRenderer,
  type ElementRenderResult,
} from './index'
import type { LayoutElementVo, RenderContext } from '../types/layout-element'

function createElement(overrides: Partial<LayoutElementVo> = {}): LayoutElementVo {
  return {
    id: '1',
    warehouseId: '1',
    areaId: null,
    elementCode: 'LE001',
    elementName: '测试元素',
    elementType: 'wall',
    shapeType: 'rect',
    positionX: 100,
    positionY: 200,
    layoutWidth: 300,
    layoutHeight: 50,
    rotation: 0,
    pointData: '',
    styleData: '',
    labelText: '',
    sortOrder: 0,
    status: 1,
    createTime: '2026-05-21 09:00:00',
    ...overrides,
  }
}

const defaultContext: RenderContext = {
  viewMode: '2d',
  isSelected: false,
  isHighlighted: false,
  isEditMode: false,
  scale: 1,
}

describe('layout element renderer registry', () => {
  it('should have all 6 element type renderers registered', () => {
    const types = ['wall', 'aisle', 'reserved', 'device', 'text', 'dimension']
    for (const type of types) {
      const renderer = getRenderer(type)
      expect(renderer, `渲染器 ${type} 应已注册`).toBeDefined()
      expect(renderer!.elementType).toBe(type)
    }
  })

  it('should return undefined for unregistered element type', () => {
    const renderer = getRenderer('unknown-type')
    expect(renderer).toBeUndefined()
  })

  it('should render wall element as rect by default', () => {
    const element = createElement({ elementType: 'wall', shapeType: 'rect' })
    const result = renderLayoutElement(element, defaultContext)

    expect(result).not.toBeNull()
    expect(result!.type).toBe('rect')
    expect(result!.config.x).toBe(100)
    expect(result!.config.y).toBe(200)
    expect(result!.config.width).toBe(300)
    expect(result!.config.height).toBe(50)
  })

  it('should render wall element as line when shapeType is line', () => {
    const element = createElement({
      elementType: 'wall',
      shapeType: 'line',
      pointData: JSON.stringify({ points: [{ x: 0, y: 0 }, { x: 300, y: 0 }] }),
    })
    const result = renderLayoutElement(element, defaultContext)

    expect(result).not.toBeNull()
    expect(result!.type).toBe('line')
  })

  it('should render aisle element as group with rect and text', () => {
    const element = createElement({
      elementType: 'aisle',
      shapeType: 'rect',
      elementName: '主通道',
    })
    const result = renderLayoutElement(element, defaultContext)

    expect(result).not.toBeNull()
    expect(result!.type).toBe('group')
    expect(result!.children).toBeDefined()
    expect(result!.children!.length).toBeGreaterThanOrEqual(1)
  })

  it('should render reserved element as group with hatch pattern', () => {
    const element = createElement({
      elementType: 'reserved',
      shapeType: 'rect',
      elementName: '预留区A',
    })
    const result = renderLayoutElement(element, defaultContext)

    expect(result).not.toBeNull()
    expect(result!.type).toBe('group')
    // Should have rect + hatch lines + text
    expect(result!.children!.length).toBeGreaterThanOrEqual(2)
  })

  it('should render device element as group', () => {
    const element = createElement({
      elementType: 'device',
      shapeType: 'rect',
      elementName: '叉车',
    })
    const result = renderLayoutElement(element, defaultContext)

    expect(result).not.toBeNull()
    expect(result!.type).toBe('group')
  })

  it('should render device element as circle when shapeType is circle', () => {
    const element = createElement({
      elementType: 'device',
      shapeType: 'circle',
    })
    const result = renderLayoutElement(element, defaultContext)

    expect(result).not.toBeNull()
    expect(result!.type).toBe('group')
    const circleChild = result!.children!.find(c => c.type === 'circle')
    expect(circleChild).toBeDefined()
  })

  it('should render text element as text node', () => {
    const element = createElement({
      elementType: 'text',
      shapeType: 'text',
      labelText: '仓库入口',
    })
    const result = renderLayoutElement(element, defaultContext)

    expect(result).not.toBeNull()
    expect(result!.type).toBe('text')
    expect(result!.config.text).toBe('仓库入口')
  })

  it('should render dimension element as group with lines and text', () => {
    const element = createElement({
      elementType: 'dimension',
      shapeType: 'line',
      pointData: JSON.stringify({ start: { x: 0, y: 0 }, end: { x: 500, y: 0 } }),
    })
    const result = renderLayoutElement(element, defaultContext)

    expect(result).not.toBeNull()
    expect(result!.type).toBe('group')
    // Should have main line + 2 end marks + text
    expect(result!.children!.length).toBeGreaterThanOrEqual(3)
  })

  it('should return null for unregistered element type in renderLayoutElement', () => {
    const element = createElement({ elementType: 'unknown-type' as any })
    const result = renderLayoutElement(element, defaultContext)

    expect(result).toBeNull()
  })

  it('should render wall 2.5D effect', () => {
    const element = createElement({ elementType: 'wall', shapeType: 'rect' })
    const result = renderLayoutElement25D(element, defaultContext)

    expect(result).toHaveLength(2) // top shadow + right shadow
    expect(result[0].type).toBe('rect')
    expect(result[1].type).toBe('rect')
  })

  it('should render device 2.5D effect', () => {
    const element = createElement({ elementType: 'device', shapeType: 'rect' })
    const result = renderLayoutElement25D(element, defaultContext)

    expect(result).toHaveLength(2)
  })

  it('should return empty array for 2.5D when renderer has no render25D', () => {
    const element = createElement({ elementType: 'text', shapeType: 'text' })
    const result = renderLayoutElement25D(element, defaultContext)

    expect(result).toEqual([])
  })

  it('should apply selection styling to wall element', () => {
    const element = createElement({ elementType: 'wall', shapeType: 'rect' })
    const selectedContext: RenderContext = { ...defaultContext, isSelected: true }
    const result = renderLayoutElement(element, selectedContext)

    expect(result).not.toBeNull()
    expect(result!.config.stroke).toBe('#409eff')
  })

  it('should apply highlight styling to wall element', () => {
    const element = createElement({ elementType: 'wall', shapeType: 'rect' })
    const highlightedContext: RenderContext = { ...defaultContext, isHighlighted: true }
    const result = renderLayoutElement(element, highlightedContext)

    expect(result).not.toBeNull()
    expect(result!.config.stroke).toBe('#1d7df2')
  })
})

describe('renderer custom registration', () => {
  it('should allow registering a custom renderer', () => {
    const customRenderer: LayoutElementRenderer = {
      elementType: 'custom-type',
      render(_element: LayoutElementVo, _context: RenderContext): ElementRenderResult {
        return { type: 'rect', config: { x: 0, y: 0, width: 100, height: 100 } }
      },
    }

    registerRenderer(customRenderer)
    const retrieved = getRenderer('custom-type')
    expect(retrieved).toBe(customRenderer)
  })
})
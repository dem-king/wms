import { describe, expect, it, vi } from 'vitest'
import { useCanvasViewport } from './useCanvasViewport'

// Mock Vue reactivity for unit testing
vi.mock('vue', () => ({
  ref: (val: any) => ({ value: val }),
  computed: (fn: () => any) => ({ get value() { return fn() } }),
}))

describe('useCanvasViewport', () => {
  const containerWidth = 960
  const containerHeight = 640

  it('should initialize with default scale 1 and offset 0', () => {
    const viewport = useCanvasViewport(containerWidth, containerHeight)

    expect(viewport.state.value.scaleX).toBe(1)
    expect(viewport.state.value.scaleY).toBe(1)
    expect(viewport.state.value.offsetX).toBe(0)
    expect(viewport.state.value.offsetY).toBe(0)
  })

  it('should report scale percent correctly', () => {
    const viewport = useCanvasViewport(containerWidth, containerHeight)
    expect(viewport.scalePercent.value).toBe('100%')
  })

  it('should clamp scale within MIN_SCALE and MAX_SCALE', () => {
    const viewport = useCanvasViewport(containerWidth, containerHeight)

    expect(viewport.clampScale(0.05)).toBe(0.1)   // MIN_SCALE = 0.1
    expect(viewport.clampScale(6.0)).toBe(5.0)    // MAX_SCALE = 5.0
    expect(viewport.clampScale(1.0)).toBe(1.0)    // within range
    expect(viewport.clampScale(0.5)).toBe(0.5)    // within range
  })

  it('should reset zoom to 100%', () => {
    const viewport = useCanvasViewport(containerWidth, containerHeight)

    // Zoom in first
    viewport.zoomIn()
    expect(viewport.state.value.scaleX).toBeGreaterThan(1)

    // Reset
    viewport.resetZoom()
    expect(viewport.state.value.scaleX).toBe(1)
    expect(viewport.state.value.scaleY).toBe(1)
    expect(viewport.state.value.offsetX).toBe(0)
    expect(viewport.state.value.offsetY).toBe(0)
  })

  it('should zoom in at center point', () => {
    const viewport = useCanvasViewport(containerWidth, containerHeight)
    viewport.zoomIn()

    expect(viewport.state.value.scaleX).toBeGreaterThan(1)
    expect(viewport.state.value.scaleY).toBeGreaterThan(1)
  })

  it('should zoom out at center point', () => {
    const viewport = useCanvasViewport(containerWidth, containerHeight)
    viewport.zoomOut()

    expect(viewport.state.value.scaleX).toBeLessThan(1)
    expect(viewport.state.value.scaleY).toBeLessThan(1)
  })

  it('should not zoom below MIN_SCALE', () => {
    const viewport = useCanvasViewport(containerWidth, containerHeight)

    // Repeatedly zoom out
    for (let i = 0; i < 100; i++) {
      viewport.zoomOut()
    }

    expect(viewport.state.value.scaleX).toBeGreaterThanOrEqual(0.1)
  })

  it('should not zoom above MAX_SCALE', () => {
    const viewport = useCanvasViewport(containerWidth, containerHeight)

    // Repeatedly zoom in
    for (let i = 0; i < 100; i++) {
      viewport.zoomIn()
    }

    expect(viewport.state.value.scaleX).toBeLessThanOrEqual(5.0)
  })

  it('should pan the canvas by specified offset', () => {
    const viewport = useCanvasViewport(containerWidth, containerHeight)
    viewport.pan(50, 30)

    expect(viewport.state.value.offsetX).toBe(50)
    expect(viewport.state.value.offsetY).toBe(30)
  })

  it('should accumulate pan offsets', () => {
    const viewport = useCanvasViewport(containerWidth, containerHeight)
    viewport.pan(50, 30)
    viewport.pan(20, 10)

    expect(viewport.state.value.offsetX).toBe(70)
    expect(viewport.state.value.offsetY).toBe(40)
  })

  it('should fit to content and center the view', () => {
    const viewport = useCanvasViewport(containerWidth, containerHeight)
    viewport.fitToContent(800, 600)

    // After fit, scale should be calculated to fit content
    expect(viewport.state.value.scaleX).toBeGreaterThan(0)
    expect(viewport.state.value.scaleY).toBeGreaterThan(0)
    // Should be centered (offsetX > 0 when content is smaller than container)
    expect(viewport.state.value.offsetX).toBeGreaterThanOrEqual(0)
  })

  it('should not fit to content when dimensions are zero or negative', () => {
    const viewport = useCanvasViewport(containerWidth, containerHeight)
    const initialScale = viewport.state.value.scaleX

    viewport.fitToContent(0, 0)
    expect(viewport.state.value.scaleX).toBe(initialScale)

    viewport.fitToContent(-100, -100)
    expect(viewport.state.value.scaleX).toBe(initialScale)
  })

  it('should zoom at a specific point adjusting offset to keep point stable', () => {
    const viewport = useCanvasViewport(containerWidth, containerHeight)
    const point = { x: 400, y: 300 }

    viewport.zoomAtPoint(point, 1)

    expect(viewport.state.value.scaleX).toBeGreaterThan(1)
    // The point should remain at the same screen position
    // offsetX = point.x - ratio * (point.x - oldOffsetX)
    expect(viewport.state.value.offsetX).not.toBe(0)
  })

  it('should generate correct stageConfig', () => {
    const viewport = useCanvasViewport(containerWidth, containerHeight)
    const config = viewport.stageConfig.value

    expect(config.width).toBe(containerWidth)
    expect(config.height).toBe(containerHeight)
    expect(config.scaleX).toBe(1)
    expect(config.scaleY).toBe(1)
    expect(config.x).toBe(0)
    expect(config.y).toBe(0)
    expect(config.draggable).toBe(false)
  })
})
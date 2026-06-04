import { describe, expect, it } from 'vitest'
import {
  parsePolygonPoints,
  toFlatPoints,
  calculatePolygonBounds,
  resolveLabelPosition,
  detectAreaOverlap,
  getPolygonAreaConfig,
} from './renderPolygonArea'

describe('parsePolygonPoints', () => {
  it('should parse valid polygon points JSON', () => {
    const json = JSON.stringify([{ x: 0, y: 0 }, { x: 100, y: 0 }, { x: 50, y: 80 }])
    const result = parsePolygonPoints(json)

    expect(result).toHaveLength(3)
    expect(result![0]).toEqual({ x: 0, y: 0 })
    expect(result![2]).toEqual({ x: 50, y: 80 })
  })

  it('should return null for null input', () => {
    expect(parsePolygonPoints(null)).toBeNull()
  })

  it('should return null for undefined input', () => {
    expect(parsePolygonPoints(undefined)).toBeNull()
  })

  it('should return null for empty string', () => {
    expect(parsePolygonPoints('')).toBeNull()
  })

  it('should return null for invalid JSON', () => {
    expect(parsePolygonPoints('not-json')).toBeNull()
  })

  it('should return null when fewer than 3 points', () => {
    const json = JSON.stringify([{ x: 0, y: 0 }, { x: 100, y: 0 }])
    expect(parsePolygonPoints(json)).toBeNull()
  })

  it('should filter out invalid point objects and return null if not enough valid', () => {
    const json = JSON.stringify([{ x: 0, y: 0 }, { foo: 'bar' }, { x: 100 }])
    expect(parsePolygonPoints(json)).toBeNull()
  })

  it('should filter out invalid points but keep valid ones if enough', () => {
    const json = JSON.stringify([
      { x: 0, y: 0 },
      { foo: 'bar' },  // invalid
      { x: 100, y: 0 },
      { x: 50, y: 80 },
    ])
    const result = parsePolygonPoints(json)
    expect(result).toHaveLength(3)
  })
})

describe('toFlatPoints', () => {
  it('should convert points to flat array [x1,y1,x2,y2,...]', () => {
    const points = [{ x: 10, y: 20 }, { x: 30, y: 40 }, { x: 50, y: 60 }]
    expect(toFlatPoints(points)).toEqual([10, 20, 30, 40, 50, 60])
  })

  it('should return empty array for empty points', () => {
    expect(toFlatPoints([])).toEqual([])
  })
})

describe('calculatePolygonBounds', () => {
  it('should calculate bounding box of polygon points', () => {
    const points = [{ x: 10, y: 20 }, { x: 100, y: 20 }, { x: 50, y: 80 }]
    const bounds = calculatePolygonBounds(points)

    expect(bounds.x).toBe(10)
    expect(bounds.y).toBe(20)
    expect(bounds.width).toBe(90)
    expect(bounds.height).toBe(60)
  })

  it('should handle single point', () => {
    const points = [{ x: 50, y: 50 }]
    const bounds = calculatePolygonBounds(points)

    expect(bounds.x).toBe(50)
    expect(bounds.y).toBe(50)
    expect(bounds.width).toBe(0)
    expect(bounds.height).toBe(0)
  })
})

describe('resolveLabelPosition', () => {
  const bounds = { x: 100, y: 200, width: 300, height: 150 }

  it('should use labelX/labelY when provided', () => {
    const pos = resolveLabelPosition(bounds, 50, 30)
    expect(pos.x).toBe(150)  // bounds.x + labelX
    expect(pos.y).toBe(230)  // bounds.y + labelY
  })

  it('should default to top center of bounds when labelX/labelY not provided', () => {
    const pos = resolveLabelPosition(bounds)
    expect(pos.x).toBe(250)  // bounds.x + bounds.width / 2
    expect(pos.y).toBe(216)  // bounds.y + 16
  })

  it('should default when labelX is null', () => {
    const pos = resolveLabelPosition(bounds, null, 30)
    expect(pos.x).toBe(250)
    expect(pos.y).toBe(216)
  })
})

describe('detectAreaOverlap', () => {
  it('should detect overlapping areas', () => {
    const a = { x: 0, y: 0, width: 200, height: 100 }
    const b = { x: 100, y: 0, width: 200, height: 100 }
    expect(detectAreaOverlap(a, b)).toBe(true)
  })

  it('should detect non-overlapping areas', () => {
    const a = { x: 0, y: 0, width: 100, height: 100 }
    const b = { x: 200, y: 0, width: 100, height: 100 }
    expect(detectAreaOverlap(a, b)).toBe(false)
  })

  it('should detect vertically non-overlapping areas', () => {
    const a = { x: 0, y: 0, width: 200, height: 100 }
    const b = { x: 0, y: 200, width: 200, height: 100 }
    expect(detectAreaOverlap(a, b)).toBe(false)
  })

  it('should detect touching edges as non-overlapping', () => {
    const a = { x: 0, y: 0, width: 100, height: 100 }
    const b = { x: 100, y: 0, width: 100, height: 100 }
    expect(detectAreaOverlap(a, b)).toBe(false)
  })
})

describe('getPolygonAreaConfig', () => {
  it('should return config with closed=true and flat points', () => {
    const points = [{ x: 0, y: 0 }, { x: 100, y: 0 }, { x: 50, y: 80 }]
    const config = getPolygonAreaConfig(points, false, false, false)

    expect(config.closed).toBe(true)
    expect(config.points).toEqual([0, 0, 100, 0, 50, 80])
    expect(config.draggable).toBe(false)
  })

  it('should apply selected styling', () => {
    const points = [{ x: 0, y: 0 }, { x: 100, y: 0 }, { x: 50, y: 80 }]
    const config = getPolygonAreaConfig(points, true, false, false)

    expect(config.fill).toBe('#e8f3ff')
    expect(config.stroke).toBe('#409eff')
    expect(config.strokeWidth).toBe(2)
  })

  it('should apply highlighted styling', () => {
    const points = [{ x: 0, y: 0 }, { x: 100, y: 0 }, { x: 50, y: 80 }]
    const config = getPolygonAreaConfig(points, false, true, false)

    expect(config.fill).toBe('#d7ebff')
    expect(config.stroke).toBe('#1d7df2')
    expect(config.strokeWidth).toBe(3)
  })

  it('should apply disabled styling', () => {
    const points = [{ x: 0, y: 0 }, { x: 100, y: 0 }, { x: 50, y: 80 }]
    const config = getPolygonAreaConfig(points, false, false, true)

    expect(config.fill).toBe('#f4f4f5')
  })

  it('should apply default styling when no special state', () => {
    const points = [{ x: 0, y: 0 }, { x: 100, y: 0 }, { x: 50, y: 80 }]
    const config = getPolygonAreaConfig(points, false, false, false)

    expect(config.fill).toBe('#f7fbff')
    expect(config.stroke).toBe('#cdddf5')
    expect(config.strokeWidth).toBe(1)
  })
})
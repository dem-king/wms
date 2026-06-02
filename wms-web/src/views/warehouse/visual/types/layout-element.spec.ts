import { describe, expect, it } from 'vitest'
import {
  parsePointData,
  parseStyleData,
  mergeStyle,
  calculateDimensionText,
  DEFAULT_ELEMENT_STYLES,
  ELEMENT_TYPES,
  SHAPE_TYPES,
} from './layout-element'

describe('parsePointData', () => {
  it('should parse valid point data JSON', () => {
    const json = JSON.stringify({ points: [{ x: 0, y: 0 }, { x: 100, y: 0 }] })
    const result = parsePointData(json)
    expect(result).not.toBeNull()
    expect((result as any).points).toHaveLength(2)
  })

  it('should parse dimension point data JSON', () => {
    const json = JSON.stringify({ start: { x: 0, y: 0 }, end: { x: 500, y: 0 } })
    const result = parsePointData(json)
    expect(result).not.toBeNull()
    expect((result as any).start.x).toBe(0)
    expect((result as any).end.x).toBe(500)
  })

  it('should return null for empty string', () => {
    expect(parsePointData('')).toBeNull()
  })

  it('should return null for invalid JSON', () => {
    expect(parsePointData('not-json')).toBeNull()
  })
})

describe('parseStyleData', () => {
  it('should parse valid style data JSON', () => {
    const json = JSON.stringify({ fillColor: '#4a4a4a', strokeColor: '#333', strokeWidth: 2 })
    const result = parseStyleData(json)
    expect(result.fillColor).toBe('#4a4a4a')
    expect(result.strokeWidth).toBe(2)
  })

  it('should return empty object for empty string', () => {
    expect(parseStyleData('')).toEqual({})
  })

  it('should return empty object for invalid JSON', () => {
    expect(parseStyleData('bad')).toEqual({})
  })
})

describe('mergeStyle', () => {
  it('should merge custom style over default style', () => {
    const customStyle = { fillColor: '#ff0000' }
    const result = mergeStyle('wall', customStyle)

    expect(result.fillColor).toBe('#ff0000')  // custom overrides
    expect(result.strokeColor).toBe(DEFAULT_ELEMENT_STYLES.wall.strokeColor)  // default kept
  })

  it('should return default style when custom is empty', () => {
    const result = mergeStyle('aisle', {})
    expect(result).toEqual(DEFAULT_ELEMENT_STYLES.aisle)
  })

  it('should merge all element types correctly', () => {
    const types = ['wall', 'aisle', 'reserved', 'device', 'text', 'dimension'] as const
    for (const type of types) {
      const result = mergeStyle(type, {})
      expect(result).toEqual(DEFAULT_ELEMENT_STYLES[type])
    }
  })
})

describe('calculateDimensionText', () => {
  it('should calculate pixel distance when no layoutScale', () => {
    const text = calculateDimensionText(0, 0, 300, 0, null)
    expect(text).toBe('300px')
  })

  it('should calculate pixel distance when layoutScale is 0', () => {
    const text = calculateDimensionText(0, 0, 300, 0, 0)
    expect(text).toBe('300px')
  })

  it('should calculate physical distance with layoutScale', () => {
    const text = calculateDimensionText(0, 0, 300, 0, 0.01)
    // 300px * 0.01 m/px = 3.0m
    expect(text).toBe('3.0m')
  })

  it('should calculate diagonal distance correctly', () => {
    // 3-4-5 triangle: distance = 500
    const text = calculateDimensionText(0, 0, 300, 400, null)
    expect(text).toBe('500px')
  })

  it('should calculate diagonal physical distance', () => {
    const text = calculateDimensionText(0, 0, 300, 400, 0.01)
    // 500px * 0.01 = 5.0m
    expect(text).toBe('5.0m')
  })
})

describe('ELEMENT_TYPES constant', () => {
  it('should define all 6 element types', () => {
    expect(Object.keys(ELEMENT_TYPES)).toEqual(['wall', 'aisle', 'reserved', 'device', 'text', 'dimension'])
  })
})

describe('SHAPE_TYPES constant', () => {
  it('should define all 5 shape types', () => {
    expect(Object.keys(SHAPE_TYPES)).toEqual(['line', 'rect', 'polygon', 'circle', 'text'])
  })
})
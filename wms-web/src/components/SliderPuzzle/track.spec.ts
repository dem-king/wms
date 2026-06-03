import { describe, expect, it } from 'vitest'
import { buildSliderTrackJson, isSliderTrackMeaningful } from './track'

describe('slider track builder', () => {
  it('在空事件时输出带 stop=true 的空轨迹', () => {
    const json = buildSliderTrackJson('tok-empty', [])
    const parsed = JSON.parse(json)

    expect(parsed.id).toBe('tok-empty')
    expect(parsed.type).toBe('SLIDER')
    expect(parsed.stop).toBe(true)
    expect(parsed.data).toEqual([])
  })

  it('在仅 UP 事件时自动补首条 DOWN', () => {
    const json = buildSliderTrackJson('tok', [
      { x: 100, y: 0, t: 100, type: 'UP' },
    ])
    const parsed = JSON.parse(json)

    expect(parsed.data).toEqual([
      { x: 0, y: 0, t: 0, type: 'DOWN' },
      { x: 100, y: 0, t: 100, type: 'UP' },
    ])
  })

  it('在仅 DOWN 事件时自动补末条 UP (时间+50ms)', () => {
    const json = buildSliderTrackJson('tok', [
      { x: 0, y: 0, t: 0, type: 'DOWN' },
    ])
    const parsed = JSON.parse(json)

    expect(parsed.data).toHaveLength(2)
    expect(parsed.data[1].type).toBe('UP')
    expect(parsed.data[1].t).toBe(50)
  })

  it('保留完整 DOWN-MOVE-UP 序列, 不重复补点', () => {
    const events = [
      { x: 0, y: 0, t: 0, type: 'DOWN' as const },
      { x: 50, y: 0, t: 50, type: 'MOVE' as const },
      { x: 100, y: 0, t: 100, type: 'MOVE' as const },
      { x: 150, y: 0, t: 150, type: 'UP' as const },
    ]
    const json = buildSliderTrackJson('tok', events)
    const parsed = JSON.parse(json)

    expect(parsed.data).toHaveLength(4)
    expect(parsed.data[0].type).toBe('DOWN')
    expect(parsed.data[3].type).toBe('UP')
  })

  it('校验有效拖动: 至少 2 个事件 + 最大 X 偏移 >= 阈值', () => {
    expect(isSliderTrackMeaningful([])).toBe(false)
    expect(isSliderTrackMeaningful([
      { x: 0, y: 0, t: 0, type: 'DOWN' },
    ])).toBe(false)
    expect(isSliderTrackMeaningful([
      { x: 0, y: 0, t: 0, type: 'DOWN' },
      { x: 2, y: 0, t: 50, type: 'UP' },
    ])).toBe(false)
    expect(isSliderTrackMeaningful([
      { x: 0, y: 0, t: 0, type: 'DOWN' },
      { x: 50, y: 0, t: 50, type: 'UP' },
    ])).toBe(true)
  })

  it('校验有效拖动: 阈值可配置', () => {
    const events = [
      { x: 0, y: 0, t: 0, type: 'DOWN' as const },
      { x: 10, y: 0, t: 50, type: 'UP' as const },
    ]
    expect(isSliderTrackMeaningful(events, 5)).toBe(true)
    expect(isSliderTrackMeaningful(events, 20)).toBe(false)
  })
})

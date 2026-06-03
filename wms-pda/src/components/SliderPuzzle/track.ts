/**
 * 滑块拼图轨迹数据构造工具 (PDA 端, 与 wms-web 同源)
 */
export interface RawSliderEvent {
  x: number
  y: number
  t: number
  type: 'MOVE' | 'DOWN' | 'UP'
}

export interface SliderTrackPoint {
  x: number
  y: number
  t: number
  type: 'MOVE' | 'DOWN' | 'UP'
}

export interface SliderTrackPayload {
  id: string
  type: 'SLIDER'
  data: SliderTrackPoint[]
  stop: boolean
}

export function buildSliderTrackJson(captchaToken: string, events: RawSliderEvent[]): string {
  const points: SliderTrackPoint[] = []

  if (events.length === 0) {
    const payload: SliderTrackPayload = {
      id: captchaToken,
      type: 'SLIDER',
      data: points,
      stop: true,
    }
    return JSON.stringify(payload)
  }

  const firstType = events[0].type
  if (firstType !== 'DOWN') {
    points.push({ x: 0, y: 0, t: 0, type: 'DOWN' })
  }

  for (const e of events) {
    points.push({ x: e.x, y: e.y, t: e.t, type: e.type })
  }

  const last = points[points.length - 1]
  if (last.type !== 'UP') {
    points.push({ x: last.x, y: last.y, t: last.t + 50, type: 'UP' })
  }

  const payload: SliderTrackPayload = {
    id: captchaToken,
    type: 'SLIDER',
    data: points,
    stop: true,
  }
  return JSON.stringify(payload)
}

export function isSliderTrackMeaningful(events: RawSliderEvent[], minMovePx = 5): boolean {
  if (events.length < 2) {
    return false
  }
  const maxX = events.reduce((m, e) => Math.max(m, Math.abs(e.x)), 0)
  return maxX >= minMovePx
}

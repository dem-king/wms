/**
 * 滑块拼图轨迹数据构造工具
 * 负责把"拖动过程中的事件流"序列化为后端 tianai-captcha 期望的 JSON 字符串
 */
import type { SliderTrackPayload, SliderTrackPoint } from '@/types/auth'

/** 拖动事件原始数据 */
export interface RawSliderEvent {
  /** 相对拖动起点的 X 偏移 (px) */
  x: number
  /** 相对拖动起点的 Y 偏移 (px) */
  y: number
  /** 事件发生的时间偏移 (ms，相对拖动开始) */
  t: number
  /** 动作类型 */
  type: 'MOVE' | 'DOWN' | 'UP'
}

/**
 * 将原始事件流构造为完整轨迹 payload
 * 自动在首尾补齐 DOWN / UP 事件（若调用方未提供），并设置 stop=true
 *
 * @param captchaToken 验证码 Token
 * @param events  拖动期间采集的事件列表（按时间顺序）
 * @returns 序列化后的 JSON 字符串
 */
export function buildSliderTrackJson(
  captchaToken: string,
  events: RawSliderEvent[],
): string {
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

  // 起始事件: 若调用方未提供 DOWN, 自动补一条 (0, 0, 0, 'DOWN')
  const firstType = events[0].type
  if (firstType !== 'DOWN') {
    points.push({ x: 0, y: 0, t: 0, type: 'DOWN' })
  }

  for (const e of events) {
    points.push({ x: e.x, y: e.y, t: e.t, type: e.type })
  }

  // 结束事件: 若最后一条不是 UP, 自动补一条 (last.x, last.y, last.t+50, 'UP')
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

/**
 * 校验用户是否完成了一次有效拖动
 * 用于避免"未拖动 / 仅点击一下"就提交导致轨迹被拒
 *
 * @param events  拖动期间采集的事件列表
 * @param minMovePx 允许的最大位移阈值，默认 5px
 * @returns 是否有效
 */
export function isSliderTrackMeaningful(
  events: RawSliderEvent[],
  minMovePx = 5,
): boolean {
  if (events.length < 2) {
    return false
  }
  const maxX = events.reduce((m, e) => Math.max(m, Math.abs(e.x)), 0)
  return maxX >= minMovePx
}

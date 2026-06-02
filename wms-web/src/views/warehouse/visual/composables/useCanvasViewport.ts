/**
 * 画布视口Composable
 * 管理画布缩放/平移状态，与业务数据分离
 */

import { computed, ref } from 'vue'

/** 缩放范围常量 */
const MIN_SCALE = 0.1   // 10%
const MAX_SCALE = 5.0   // 500%
/** 缩放步进（每次滚轮变化10%） */
const ZOOM_STEP = 0.1
/** 平移边界最小可见像素 */
const MIN_VISIBLE_PX = 50

/** 画布视口状态 */
export interface CanvasViewportState {
  /** X轴缩放比例 */
  scaleX: number
  /** Y轴缩放比例 */
  scaleY: number
  /** X轴偏移量 */
  offsetX: number
  /** Y轴偏移量 */
  offsetY: number
}

/**
 * 画布视口Composable
 * 提供缩放、平移、适应画布、重置等功能
 * @param containerWidth 画布容器宽度
 * @param containerHeight 画布容器高度
 * @returns 视口状态和操作方法
 */
export function useCanvasViewport(containerWidth: number, containerHeight: number) {
  const state = ref<CanvasViewportState>({
    scaleX: 1,
    scaleY: 1,
    offsetX: 0,
    offsetY: 0,
  })

  /** 当前缩放百分比文本 */
  const scalePercent = computed(() => `${Math.round(state.value.scaleX * 100)}%`)

  /** 当前缩放比例（统一缩放，scaleX === scaleY） */
  const scale = computed(() => state.value.scaleX)

  /**
   * 限制缩放值在合法范围内
   * @param value 目标缩放值
   * @returns 限制后的缩放值
   */
  function clampScale(value: number): number {
    return Math.min(Math.max(value, MIN_SCALE), MAX_SCALE)
  }

  /**
   * 以指定点为中心缩放画布
   * @param point 缩放中心点（画布坐标）
   * @param delta 缩放增量（正数放大，负数缩小）
   */
  function zoomAtPoint(point: { x: number; y: number }, delta: number) {
    const oldScale = state.value.scaleX
    const newScale = clampScale(oldScale + delta * ZOOM_STEP * oldScale)
    if (newScale === oldScale) {
      return
    }

    // 以鼠标指针为中心缩放：调整偏移使指针位置不变
    const ratio = newScale / oldScale
    state.value = {
      scaleX: newScale,
      scaleY: newScale,
      offsetX: point.x - ratio * (point.x - state.value.offsetX),
      offsetY: point.y - ratio * (point.y - state.value.offsetY),
    }
  }

  /**
   * 鼠标滚轮缩放处理
   * @param event 鼠标滚轮事件
   */
  function handleWheelZoom(event: WheelEvent) {
    event.preventDefault()
    // 向上滚动(deltaY<0)放大，向下滚动(deltaY>0)缩小
    const delta = event.deltaY < 0 ? 1 : -1
    zoomAtPoint({ x: event.offsetX, y: event.offsetY }, delta)
  }

  /**
   * 平移画布
   * @param dx X方向偏移量
   * @param dy Y方向偏移量
   * @param contentWidth 内容宽度（用于边界限制）
   * @param contentHeight 内容高度（用于边界限制）
   */
  function pan(dx: number, dy: number, contentWidth?: number, contentHeight?: number) {
    let newOffsetX = state.value.offsetX + dx
    let newOffsetY = state.value.offsetY + dy

    // 边界限制：至少MIN_VISIBLE_PX留在视口内
    if (contentWidth !== undefined && contentHeight !== undefined) {
      const scaledWidth = contentWidth * state.value.scaleX
      const scaledHeight = contentHeight * state.value.scaleY
      // 右边界：内容右边缘至少有MIN_VISIBLE_PX在视口内
      const maxOffsetX = MIN_VISIBLE_PX
      const minOffsetX = containerWidth - scaledWidth + MIN_VISIBLE_PX
      // 下边界
      const maxOffsetY = MIN_VISIBLE_PX
      const minOffsetY = containerHeight - scaledHeight + MIN_VISIBLE_PX

      newOffsetX = Math.min(Math.max(newOffsetX, minOffsetX), maxOffsetX)
      newOffsetY = Math.min(Math.max(newOffsetY, minOffsetY), maxOffsetY)
    }

    state.value = {
      ...state.value,
      offsetX: newOffsetX,
      offsetY: newOffsetY,
    }
  }

  /**
   * 适应画布：自动计算缩放和偏移使所有内容可见
   * @param contentWidth 内容宽度
   * @param contentHeight 内容高度
   * @param padding 内边距（默认40px）
   */
  function fitToContent(contentWidth: number, contentHeight: number, padding = 40) {
    if (contentWidth <= 0 || contentHeight <= 0) {
      return
    }

    const availableWidth = containerWidth - padding * 2
    const availableHeight = containerHeight - padding * 2
    const newScale = clampScale(
      Math.min(availableWidth / contentWidth, availableHeight / contentHeight),
    )

    state.value = {
      scaleX: newScale,
      scaleY: newScale,
      // 居中偏移
      offsetX: (containerWidth - contentWidth * newScale) / 2,
      offsetY: (containerHeight - contentHeight * newScale) / 2,
    }
  }

  /**
   * 重置为100%缩放，偏移归零
   */
  function resetZoom() {
    state.value = {
      scaleX: 1,
      scaleY: 1,
      offsetX: 0,
      offsetY: 0,
    }
  }

  /**
   * 放大一步
   */
  function zoomIn() {
    const center = { x: containerWidth / 2, y: containerHeight / 2 }
    zoomAtPoint(center, 1)
  }

  /**
   * 缩小一步
   */
  function zoomOut() {
    const center = { x: containerWidth / 2, y: containerHeight / 2 }
    zoomAtPoint(center, -1)
  }

  /** Konva Stage配置 */
  const stageConfig = computed(() => ({
    width: containerWidth,
    height: containerHeight,
    scaleX: state.value.scaleX,
    scaleY: state.value.scaleY,
    x: state.value.offsetX,
    y: state.value.offsetY,
    draggable: false,
  }))

  return {
    state,
    scale,
    scalePercent,
    stageConfig,
    zoomAtPoint,
    handleWheelZoom,
    pan,
    fitToContent,
    resetZoom,
    zoomIn,
    zoomOut,
    clampScale,
  }
}
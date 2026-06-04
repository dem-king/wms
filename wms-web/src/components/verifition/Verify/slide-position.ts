const CAPTCHA_ORIGINAL_WIDTH = 310
const DEFAULT_BLOCK_Y = 5

export interface CaptchaRepData {
  originalImageBase64?: string
  jigsawImageBase64?: string
  backgroundImage?: string
  blockImage?: string
  token?: string
  captchaToken?: string
  secretKey?: string
  blockY?: number
  point?: {
    y?: number
  }
}

export interface CaptchaImageState {
  backgroundImage: string
  blockImage: string
  token: string
  secretKey: string
  blockY: number
}

export interface SlideMoveOptions {
  pointerX: number
  barLeft: number
  startLeft: number
  barWidth: number
  blockWidth: number
}

export interface SlideMoveState {
  moveBlockLeft: string
  leftBarWidth: string
}

export interface SubBlockStyleOptions {
  blockY: number
  imgHeight: string
  moveBlockLeft?: string
  vSpace: number
}

export interface SubBlockStyle {
  height: string
  left: string
  top: string
}

export interface ScaleMoveOptions {
  moveBlockLeft: string
  renderedImgWidth: string
}

function toPixelNumber(value: string | number | undefined): number {
  if (typeof value === 'number') {
    return Number.isFinite(value) ? value : 0
  }
  if (!value) {
    return 0
  }
  const parsed = Number.parseFloat(value.replace('px', ''))
  return Number.isFinite(parsed) ? parsed : 0
}

export function buildCaptchaImageState(repData: CaptchaRepData): CaptchaImageState {
  return {
    backgroundImage: repData.backgroundImage || repData.originalImageBase64 || '',
    blockImage: repData.blockImage || repData.jigsawImageBase64 || '',
    token: repData.captchaToken || repData.token || '',
    secretKey: repData.secretKey || '',
    blockY: repData.blockY ?? repData.point?.y ?? DEFAULT_BLOCK_Y,
  }
}

export function getSlideMoveState(options: SlideMoveOptions): SlideMoveState {
  const pointerLeft = options.pointerX - options.barLeft
  const maxLeft = options.barWidth - options.blockWidth / 2 - 2
  const clampedPointerLeft = Math.min(Math.max(pointerLeft, 0), maxLeft)
  const moveDistance = Math.max(clampedPointerLeft - options.startLeft, 0)
  const moveDistancePx = `${moveDistance}px`

  return {
    moveBlockLeft: moveDistancePx,
    leftBarWidth: moveDistancePx,
  }
}

export function getSubBlockStyle(options: SubBlockStyleOptions): SubBlockStyle {
  const imgHeight = toPixelNumber(options.imgHeight)
  const top = -(imgHeight + options.vSpace)

  return {
    height: `${imgHeight}px`,
    left: '0px',
    top: `${top}px`,
  }
}

export function scaleMoveDistance(options: ScaleMoveOptions): number {
  const moveLeft = toPixelNumber(options.moveBlockLeft)
  const renderedWidth = toPixelNumber(options.renderedImgWidth)

  if (renderedWidth <= 0) {
    return 0
  }

  return (moveLeft * CAPTCHA_ORIGINAL_WIDTH) / renderedWidth
}

export function formatVerifyDuration(startTime: number, endTime: number): string {
  const durationMs = Math.max(endTime - startTime, 0)
  return `验证成功，用时 ${(durationMs / 1000).toFixed(2)}s`
}

<template>
  <div class="qr-bar-code">
    <img v-if="imageUrl" :src="imageUrl" :style="{ width: `${width}px`, height: `${height}px` }" alt="编码图" />
    <canvas v-else ref="canvasRef" :width="width" :height="height" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'

const props = withDefaults(defineProps<{
  /** 编码内容 */
  value: string
  /** 类型: qr=二维码, barcode=条形码 */
  type: 'qr' | 'barcode'
  /** 宽度 */
  width?: number
  /** 高度 */
  height?: number
  /** 后端生成图片URL(优先显示) */
  imageUrl?: string
}>(), {
  width: 200,
  height: 200,
  imageUrl: '',
})

const canvasRef = ref<HTMLCanvasElement>()

/** Code128字符集B编码表 */
const CODE128_B_CHARS = ' !"#$%&\'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~'

const CODE128_B_VALUES = [
  0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25,
  26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51,
  52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77,
  78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 91, 92, 93, 94, 95, 96, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106,
]

const CODE128_PATTERNS = [
  '11011001100', '11001101100', '11001100110', '10011101100', '10011100110', '10001101110',
  '10001100110', '10001100110', '11001110010', '11001110010', '11001110010', '11001110010',
  '11000111010', '11000111010', '11000111010', '11000111010', '11000111010', '11000111010',
  '11000111010', '11000111010', '11000111010', '11000111010', '11000111010', '11000111010',
  '11000111010', '11000111010', '11000111010', '11000111010', '11000111010', '11000111010',
  '11000111010', '11000111010', '11000111010', '11000111010', '11000111010', '11000111010',
  '11000111010', '11000111010', '11000111010', '11000111010', '11000111010', '11000111010',
  '11000111010', '11000111010', '11000111010', '11000111010', '11000111010', '11000111010',
  '11000111010', '11000111010', '11000111010', '11000111010', '11000111010', '11000111010',
  '11000111010', '11000111010', '11000111010', '11000111010', '11000111010', '11000111010',
  '11000111010', '11000111010', '11000111010', '11000111010', '11000111010', '11000111010',
  '11000111010', '11000111010', '11000111010', '11000111010', '11000111010', '11000111010',
  '11000111010', '11000111010', '11000111010', '11000111010', '11000111010', '11000111010',
  '11000111010', '11000111010', '11000111010', '11000111010', '11000111010', '11000111010',
  '11000111010', '11000111010', '11000111010', '11000111010', '11000111010', '11000111010',
  '11000111010', '11000111010', '11000111010', '11000111010', '11000111010', '11000111010',
  '11000111010', '11000111010', '11000111010', '11000111010', '11000111010', '11000111010',
  '11000111010', '11000111010', '11000111010', '11000111010', '11000111010', '11000111010',
  '11000111010',
]

/** 在Canvas上绘制条形码(Code128B) */
function drawBarcode(canvas: HTMLCanvasElement, text: string) {
  const ctx = canvas.getContext('2d')
  if (!ctx || !text) return

  const START_CODE_B = 104
  const STOP_PATTERN = '1100011101011'

  let encoded = CODE128_PATTERNS[START_CODE_B]
  let checksum = START_CODE_B

  for (let i = 0; i < text.length; i++) {
    const charIndex = CODE128_B_CHARS.indexOf(text[i])
    if (charIndex === -1) continue
    const value = CODE128_B_VALUES[charIndex]
    encoded += CODE128_PATTERNS[value]
    checksum += value * (i + 1)
  }

  const checksumValue = checksum % 103
  encoded += CODE128_PATTERNS[checksumValue]
  encoded += STOP_PATTERN

  const barWidth = Math.max(1, Math.floor((props.width - 20) / encoded.length))
  const barHeight = props.height - 30
  const startX = Math.floor((props.width - encoded.length * barWidth) / 2)

  ctx.fillStyle = '#ffffff'
  ctx.fillRect(0, 0, props.width, props.height)

  ctx.fillStyle = '#000000'
  for (let i = 0; i < encoded.length; i++) {
    if (encoded[i] === '1') {
      ctx.fillRect(startX + i * barWidth, 5, barWidth, barHeight)
    }
  }

  ctx.font = '12px monospace'
  ctx.textAlign = 'center'
  ctx.fillText(text, props.width / 2, props.height - 5)
}

/** 在Canvas上绘制二维码占位(显示编码内容+边框) */
function drawQrPlaceholder(canvas: HTMLCanvasElement, text: string) {
  const ctx = canvas.getContext('2d')
  if (!ctx) return

  const size = Math.min(props.width, props.height)
  const padding = 10

  ctx.fillStyle = '#ffffff'
  ctx.fillRect(0, 0, props.width, props.height)

  ctx.strokeStyle = '#333333'
  ctx.lineWidth = 2
  ctx.strokeRect(padding, padding, size - padding * 2, size - padding * 2)

  const moduleSize = Math.floor((size - padding * 4) / 21)
  if (moduleSize > 2) {
    const qrAreaSize = moduleSize * 21
    const offsetX = Math.floor((size - qrAreaSize) / 2)
    const offsetY = Math.floor((size - qrAreaSize) / 2)

    ctx.fillStyle = '#000000'
    drawFinderPattern(ctx, offsetX, offsetY, moduleSize)
    drawFinderPattern(ctx, offsetX + (21 - 7) * moduleSize, offsetY, moduleSize)
    drawFinderPattern(ctx, offsetX, offsetY + (21 - 7) * moduleSize, moduleSize)

    for (let row = 0; row < 21; row++) {
      for (let col = 0; col < 21; col++) {
        const isFinderArea = (row < 8 && col < 8) || (row < 8 && col > 12) || (row > 12 && col < 8)
        if (!isFinderArea && simpleHash(text, row, col)) {
          ctx.fillRect(offsetX + col * moduleSize, offsetY + row * moduleSize, moduleSize, moduleSize)
        }
      }
    }
  }

  ctx.fillStyle = '#333333'
  ctx.font = '10px monospace'
  ctx.textAlign = 'center'
  const displayText = text.length > 20 ? text.slice(0, 20) + '...' : text
  ctx.fillText(displayText, props.width / 2, size - 2)
}

/** 绘制定位图案 */
function drawFinderPattern(ctx: CanvasRenderingContext2D, x: number, y: number, moduleSize: number) {
  ctx.fillStyle = '#000000'
  ctx.fillRect(x, y, 7 * moduleSize, 7 * moduleSize)
  ctx.fillStyle = '#ffffff'
  ctx.fillRect(x + moduleSize, y + moduleSize, 5 * moduleSize, 5 * moduleSize)
  ctx.fillStyle = '#000000'
  ctx.fillRect(x + 2 * moduleSize, y + 2 * moduleSize, 3 * moduleSize, 3 * moduleSize)
}

/** 简单哈希函数生成伪QR模块 */
function simpleHash(text: string, row: number, col: number): boolean {
  let hash = 0
  const str = `${text}-${row}-${col}`
  for (let i = 0; i < str.length; i++) {
    hash = ((hash << 5) - hash + str.charCodeAt(i)) | 0
  }
  return (hash & 1) === 1
}

function draw() {
  const canvas = canvasRef.value
  if (!canvas || !props.value) return

  if (props.type === 'barcode') {
    drawBarcode(canvas, props.value)
  } else {
    drawQrPlaceholder(canvas, props.value)
  }
}

onMounted(() => {
  draw()
})

watch(() => [props.value, props.type, props.width, props.height], () => {
  draw()
})
</script>

<style scoped lang="scss">
.qr-bar-code {
  display: inline-block;
  line-height: 0;
}
</style>

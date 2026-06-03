<template>
  <div ref="containerRef" class="slider-puzzle" @selectstart.prevent>
    <div class="canvas-wrapper">
      <img v-if="data.backgroundImage" :src="data.backgroundImage" class="bg-image" draggable="false" />
      <!-- 目标位置: 拼图缺口阴影 -->
      <div
        v-if="data.blockImage && data.blockY !== null"
        class="block-slot"
        :style="{ top: data.blockY + 'px', left: slotX + 'px' }"
      >
        <div class="block-slot-glow" />
      </div>
      <!-- 拼图块: 拖动中跟随鼠标 -->
      <div
        v-if="data.blockImage"
        ref="blockRef"
        class="block"
        :style="{ top: data.blockY + 'px', left: dragX + 'px', cursor: dragState === 'dragging' ? 'grabbing' : 'grab' }"
        :class="{ 'block--dragging': dragState === 'dragging' }"
        @mousedown="handleDown"
        @touchstart="handleTouchStart"
      >
        <img :src="data.blockImage" class="block-image" draggable="false" />
      </div>
      <!-- 拖动条: 底部滑轨 -->
      <div class="slider-rail">
        <div
          class="slider-thumb"
          :class="{ 'slider-thumb--dragging': dragState === 'dragging', 'slider-thumb--success': status === 'success' }"
          :style="{ left: thumbX + 'px' }"
          @mousedown="handleDown"
          @touchstart="handleTouchStart"
        >
          <span class="slider-arrow">»</span>
        </div>
        <div
          class="slider-progress"
          :class="{ 'slider-progress--success': status === 'success' }"
          :style="{ width: thumbX + 'px' }"
        />
        <span class="slider-hint">{{ hintText }}</span>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onBeforeUnmount, ref } from 'vue'
import type { CaptchaImageResp } from '@/types/auth'
import { buildSliderTrackJson, isSliderTrackMeaningful, type RawSliderEvent } from './track'

interface Props {
  /** 滑块拼图数据（背景图、拼图块、缺口 Y、Token） */
  data: CaptchaImageResp
  /** 拼图块宽高 (px)，默认 50 */
  blockSize?: number
  /** 状态: 'idle' 待拖动 / 'success' 已通过（显示绿色） / 'failed' 失败（显示红色, 由父组件触发 refresh） */
  status?: 'idle' | 'success' | 'failed'
}

const props = withDefaults(defineProps<Props>(), {
  blockSize: 50,
  status: 'idle',
})

const emit = defineEmits<{
  /** 拖动完成且轨迹有效 */
  (e: 'change', payload: { token: string, trackJson: string }): void
  /** 拖动距离不足, 未触发提交 */
  (e: 'reset'): void
}>()

const containerRef = ref<HTMLDivElement>()
const blockRef = ref<HTMLDivElement>()
const dragState = ref<'idle' | 'dragging'>('idle')
const dragX = ref(0)
const thumbX = ref(0)
const events = ref<RawSliderEvent[]>([])
const dragStartClientX = ref(0)
const startTimestamp = ref(0)

/** 拼图块拖动有效范围: [0, containerWidth - blockSize] */
const dragMaxX = computed(() => {
  const w = containerRef.value?.clientWidth ?? 0
  return Math.max(0, w - props.blockSize)
})

/** 目标 X (本次滑动的"正确位置"), 由父组件外部传入或采用合理默认:
 *  居中偏右, 模拟真实缺口位置.
 *  实际生产应通过后端返回的 templateImageX 字段下发, 1.5.2 SDK
 *  CaptchaImageVO 未直接暴露此字段, 这里取容器宽度的 50% 作为占位.
 *  即使位置不准确, tianai 后端仍按服务端真实坐标校验, 不会放过错位提交.
 */
const slotX = computed(() => Math.round(dragMaxX.value * 0.5))

const hintText = computed(() => {
  if (props.status === 'success') return '验证通过'
  if (props.status === 'failed') return '验证失败, 请重试'
  if (dragState.value === 'dragging') return '拖动中...'
  return '按住滑块向右拖动完成拼图'
})

function handleDown(e: MouseEvent) {
  if (props.status === 'success') return
  e.preventDefault()
  startDrag(e.clientX)
}

function handleTouchStart(e: TouchEvent) {
  if (props.status === 'success') return
  e.preventDefault()
  const touch = e.touches[0]
  if (!touch) return
  startDrag(touch.clientX)
}

function startDrag(clientX: number) {
  dragState.value = 'dragging'
  dragStartClientX.value = clientX
  dragX.value = 0
  thumbX.value = 0
  startTimestamp.value = Date.now()
  events.value = [{ x: 0, y: 0, t: 0, type: 'DOWN' }]
  window.addEventListener('mousemove', handleMouseMove)
  window.addEventListener('mouseup', handleUp)
  window.addEventListener('touchmove', handleTouchMove, { passive: false })
  window.addEventListener('touchend', handleTouchEnd)
}

function handleMouseMove(e: MouseEvent) {
  updateDrag(e.clientX)
}

function handleTouchMove(e: TouchEvent) {
  e.preventDefault()
  const touch = e.touches[0]
  if (!touch) return
  updateDrag(touch.clientX)
}

function updateDrag(clientX: number) {
  if (dragState.value !== 'dragging') return
  const delta = clientX - dragStartClientX.value
  const clamped = Math.max(0, Math.min(dragMaxX.value, delta))
  dragX.value = clamped
  thumbX.value = clamped
  events.value.push({
    x: clamped,
    y: 0,
    t: Date.now() - startTimestamp.value,
    type: 'MOVE',
  })
}

function handleUp() {
  finishDrag()
}

function handleTouchEnd() {
  finishDrag()
}

function finishDrag() {
  if (dragState.value !== 'dragging') return
  dragState.value = 'idle'
  window.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('mouseup', handleUp)
  window.removeEventListener('touchmove', handleTouchMove)
  window.removeEventListener('touchend', handleTouchEnd)

  // 末尾补一条 UP
  events.value.push({
    x: dragX.value,
    y: 0,
    t: Date.now() - startTimestamp.value,
    type: 'UP',
  })

  if (!isSliderTrackMeaningful(events.value)) {
    // 拖动距离不足, 重置并通知父组件
    dragX.value = 0
    thumbX.value = 0
    events.value = []
    emit('reset')
    return
  }

  const trackJson = buildSliderTrackJson(props.data.captchaToken, events.value)
  emit('change', { token: props.data.captchaToken, trackJson })
}

onBeforeUnmount(() => {
  window.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('mouseup', handleUp)
  window.removeEventListener('touchmove', handleTouchMove)
  window.removeEventListener('touchend', handleTouchEnd)
})
</script>

<style lang="scss" scoped>
.slider-puzzle {
  width: 100%;
  user-select: none;
  -webkit-user-select: none;

  .canvas-wrapper {
    position: relative;
    width: 100%;
    aspect-ratio: 300 / 130;
    border-radius: 8px;
    overflow: hidden;
    background: #f1f5f9;
  }

  .bg-image {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
    object-fit: cover;
    pointer-events: none;
  }

  .block-slot {
    position: absolute;
    width: 50px;
    height: 50px;
    border: 2px dashed rgba(255, 255, 255, 0.85);
    border-radius: 6px;
    pointer-events: none;
    box-sizing: border-box;
  }

  .block-slot-glow {
    position: absolute;
    inset: 0;
    background: rgba(255, 255, 255, 0.18);
    border-radius: 4px;
    animation: pulse 1.6s ease-in-out infinite;
  }

  @keyframes pulse {
    0%, 100% { opacity: 0.55; }
    50% { opacity: 0.95; }
  }

  .block {
    position: absolute;
    width: 50px;
    height: 50px;
    transition: none;
    pointer-events: auto;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.25);
  }

  .block--dragging {
    box-shadow: 0 6px 14px rgba(37, 99, 235, 0.35);
  }

  .block-image {
    width: 100%;
    height: 100%;
    object-fit: fill;
    pointer-events: none;
  }

  .slider-rail {
    position: absolute;
    left: 0;
    right: 0;
    bottom: 0;
    height: 36px;
    background: rgba(248, 250, 252, 0.92);
    backdrop-filter: blur(4px);
    border-top: 1px solid rgba(226, 232, 240, 0.6);
    display: flex;
    align-items: center;
    padding: 0 4px;
  }

  .slider-thumb {
    position: absolute;
    left: 4px;
    top: 50%;
    transform: translateY(-50%);
    width: 36px;
    height: 28px;
    background: #fff;
    border: 1.5px solid #cbd5e1;
    border-radius: 4px;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: grab;
    user-select: none;
    z-index: 2;
    transition: background 0.2s;

    &--dragging {
      background: #eff6ff;
      border-color: #2563eb;
    }

    &--success {
      background: #dcfce7;
      border-color: #16a34a;
    }
  }

  .slider-arrow {
    font-size: 16px;
    font-weight: 700;
    color: #2563eb;
    line-height: 1;
  }

  .slider-progress {
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 0;
    background: linear-gradient(90deg, rgba(37, 99, 235, 0.12), rgba(37, 99, 235, 0.22));
    transition: width 0.05s linear;
    z-index: 1;

    &--success {
      background: linear-gradient(90deg, rgba(22, 163, 74, 0.18), rgba(22, 163, 74, 0.3));
    }
  }

  .slider-hint {
    position: absolute;
    left: 50%;
    top: 50%;
    transform: translate(-50%, -50%);
    font-size: 12px;
    color: #64748b;
    pointer-events: none;
    z-index: 1;
    white-space: nowrap;
  }
}
</style>

<template>
  <view class="slider-puzzle" @touchstart.stop="noop">
    <view class="canvas-wrapper">
      <image
        v-if="data.backgroundImage"
        :src="data.backgroundImage"
        class="bg-image"
        :draggable="false"
        :show-menu-by-longpress="false"
      />
      <!-- 目标位置: 拼图缺口阴影 -->
      <view
        v-if="data.blockImage && data.blockY !== null"
        class="block-slot"
        :style="{ top: data.blockY + 'px', left: slotX + 'px' }"
      >
        <view class="block-slot-glow" />
      </view>
      <!-- 拼图块: 拖动中跟随手指 -->
      <view
        v-if="data.blockImage"
        class="block"
        :class="{ 'block--dragging': dragState === 'dragging' }"
        :style="{ top: data.blockY + 'px', left: dragX + 'px' }"
        @touchstart="handleTouchStart"
        @touchmove.stop="handleTouchMove"
        @touchend="handleTouchEnd"
        @touchcancel="handleTouchEnd"
      >
        <image
          :src="data.blockImage"
          class="block-image"
          :draggable="false"
          :show-menu-by-longpress="false"
        />
      </view>
      <!-- 拖动条: 底部滑轨 -->
      <view class="slider-rail">
        <view
          class="slider-thumb"
          :class="{
            'slider-thumb--dragging': dragState === 'dragging',
            'slider-thumb--success': status === 'success',
          }"
          :style="{ left: thumbX + 'px' }"
          @touchstart="handleTouchStart"
          @touchmove.stop="handleTouchMove"
          @touchend="handleTouchEnd"
          @touchcancel="handleTouchEnd"
        >
          <text class="slider-arrow">»</text>
        </view>
        <view
          class="slider-progress"
          :class="{ 'slider-progress--success': status === 'success' }"
          :style="{ width: thumbX + 'px' }"
        />
        <text class="slider-hint">{{ hintText }}</text>
      </view>
    </view>
  </view>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import type { CaptchaImageResp } from '@/utils/constants'
import { buildSliderTrackJson, isSliderTrackMeaningful, type RawSliderEvent } from './track'

interface Props {
  /** 滑块拼图数据 */
  data: CaptchaImageResp
  /** 拼图块宽高 (px)，默认 50 */
  blockSize?: number
  /** 状态: 'idle' / 'success' / 'failed' */
  status?: 'idle' | 'success' | 'failed'
}

const props = withDefaults(defineProps<Props>(), {
  blockSize: 50,
  status: 'idle',
})

const emit = defineEmits<{
  (e: 'change', payload: { token: string, trackJson: string }): void
  (e: 'reset'): void
}>()

const dragState = ref<'idle' | 'dragging'>('idle')
const dragX = ref(0)
const thumbX = ref(0)
const events = ref<RawSliderEvent[]>([])
const dragStartClientX = ref(0)
const startTimestamp = ref(0)

/** 容器宽度估算: PDA 端常见宽度 320px - 40 (左右边距) = 280 */
const containerWidth = ref(280)

const dragMaxX = computed(() => Math.max(0, containerWidth.value - props.blockSize))
const slotX = computed(() => Math.round(dragMaxX.value * 0.5))

const hintText = computed(() => {
  if (props.status === 'success') return '验证通过'
  if (props.status === 'failed') return '验证失败, 请重试'
  if (dragState.value === 'dragging') return '拖动中...'
  return '按住滑块向右拖动完成拼图'
})

function noop() {
  // 阻止父级滚动
}

function handleTouchStart(e: TouchEvent) {
  if (props.status === 'success') return
  // uni-app 的 TouchEvent 不一定提供 touches; 使用第一根手指
  const touch = (e as any).touches?.[0] || (e.mp && e.mp.touches && e.mp.touches[0])
  if (!touch) return
  dragState.value = 'dragging'
  dragStartClientX.value = touch.clientX || touch.x || 0
  dragX.value = 0
  thumbX.value = 0
  startTimestamp.value = Date.now()
  events.value = [{ x: 0, y: 0, t: 0, type: 'DOWN' }]
}

function handleTouchMove(e: TouchEvent) {
  if (dragState.value !== 'dragging') return
  const touch = (e as any).touches?.[0] || (e.mp && e.mp.touches && e.mp.touches[0])
  if (!touch) return
  const clientX = touch.clientX || touch.x || 0
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

function handleTouchEnd() {
  if (dragState.value !== 'dragging') return
  dragState.value = 'idle'
  events.value.push({
    x: dragX.value,
    y: 0,
    t: Date.now() - startTimestamp.value,
    type: 'UP',
  })
  if (!isSliderTrackMeaningful(events.value)) {
    dragX.value = 0
    thumbX.value = 0
    events.value = []
    emit('reset')
    return
  }
  const trackJson = buildSliderTrackJson(props.data.captchaToken, events.value)
  emit('change', { token: props.data.captchaToken, trackJson })
}
</script>

<style lang="scss" scoped>
.slider-puzzle {
  width: 100%;
  user-select: none;

  .canvas-wrapper {
    position: relative;
    width: 100%;
    height: 130px;
    border-radius: 8px;
    overflow: hidden;
    background: #f1f5f9;
  }

  .bg-image {
    position: absolute;
    inset: 0;
    width: 100%;
    height: 100%;
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
    pointer-events: auto;
    box-shadow: 0 4px 10px rgba(0, 0, 0, 0.25);
  }

  .block--dragging {
    box-shadow: 0 6px 14px rgba(37, 99, 235, 0.35);
  }

  .block-image {
    width: 100%;
    height: 100%;
    pointer-events: none;
  }

  .slider-rail {
    position: absolute;
    left: 0;
    right: 0;
    bottom: 0;
    height: 36px;
    background: rgba(248, 250, 252, 0.92);
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
    z-index: 1;
  }

  .slider-progress--success {
    background: linear-gradient(90deg, rgba(22, 163, 74, 0.18), rgba(22, 163, 74, 0.3));
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

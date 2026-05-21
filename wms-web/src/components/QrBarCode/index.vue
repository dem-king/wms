<template>
  <div class="qr-bar-code" :style="{ width: `${width}px` }">
    <img
      v-if="imageUrl"
      :src="imageUrl"
      :style="{ width: `${width}px`, height: `${height}px` }"
      class="qr-bar-code__image"
      alt="编码图"
    />
    <template v-else-if="!currentError">
      <img
        v-if="renderState.type === 'qr' && qrDataUrl"
        :src="qrDataUrl"
        :style="{ width: `${width}px`, height: `${height}px` }"
        class="qr-bar-code__image"
        alt="二维码"
      />
      <svg
        v-else-if="renderState.type === 'barcode'"
        ref="barcodeRef"
        :width="width"
        :height="height"
        class="qr-bar-code__barcode"
        role="img"
        aria-label="条形码"
      />
    </template>
    <el-empty
      v-if="currentError"
      :description="currentError"
      :image-size="52"
      class="qr-bar-code__empty"
    />
    <div
      v-if="showReadableText"
      class="qr-bar-code__text"
    >
      {{ renderState.value }}
    </div>
  </div>
</template>

<script setup lang="ts">
import JsBarcode from 'jsbarcode'
import QRCode from 'qrcode'
import { computed, nextTick, ref, watch } from 'vue'
import { buildRenderableCode } from './rendering'

const DEFAULT_RENDER_ERROR_MESSAGE = '编码渲染失败，请检查内容或稍后重试'

const props = withDefaults(defineProps<{
  /** 编码内容 */
  value?: string
  /** 类型: qr=二维码, barcode=条形码 */
  type: 'qr' | 'barcode'
  /** 宽度 */
  width?: number
  /** 高度 */
  height?: number
  /** 后端生成图片URL(优先显示) */
  imageUrl?: string
  /** 空内容提示 */
  emptyMessage?: string
  /** 渲染失败提示 */
  renderErrorMessage?: string
}>(), {
  value: '',
  width: 200,
  height: 200,
  imageUrl: '',
  emptyMessage: '',
  renderErrorMessage: DEFAULT_RENDER_ERROR_MESSAGE,
})

const barcodeRef = ref<SVGSVGElement>()
const qrDataUrl = ref('')
const runtimeError = ref('')

const renderState = computed(() => {
  const result = buildRenderableCode({
    type: props.type,
    value: props.value,
  })

  return {
    ...result,
    errorMessage: result.errorMessage || props.emptyMessage,
  }
})

const currentError = computed(() => renderState.value.errorMessage || runtimeError.value)

const showReadableText = computed(() => {
  if (!renderState.value.showReadableText || !renderState.value.value) {
    return false
  }

  return Boolean(props.imageUrl || (!currentError.value && renderState.value.type === 'barcode'))
})

async function renderCode() {
  runtimeError.value = ''
  qrDataUrl.value = ''

  if (props.imageUrl || renderState.value.errorMessage) {
    return
  }

  try {
    if (renderState.value.type === 'qr') {
      qrDataUrl.value = await QRCode.toDataURL(renderState.value.value, {
        width: props.width,
        margin: 1,
        errorCorrectionLevel: 'M',
      })
      return
    }

    await nextTick()
    if (!barcodeRef.value) {
      return
    }

    JsBarcode(barcodeRef.value, renderState.value.value, {
      format: 'CODE128',
      displayValue: false,
      margin: 0,
      width: 2,
      height: Math.max(props.height - 16, 40),
      background: '#ffffff',
      lineColor: '#111827',
    })
  } catch (error) {
    console.error(error)
    runtimeError.value = props.renderErrorMessage
  }
}

watch(
  () => [props.imageUrl, props.type, props.value, props.width, props.height],
  () => {
    void renderCode()
  },
  { immediate: true },
)
</script>

<style scoped lang="scss">
.qr-bar-code {
  display: inline-flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  line-height: 1.2;
}

.qr-bar-code__image,
.qr-bar-code__barcode {
  display: block;
  background: #ffffff;
}

.qr-bar-code__text {
  max-width: 100%;
  color: #606266;
  font-size: 12px;
  line-height: 1.4;
  text-align: center;
  word-break: break-all;
}

.qr-bar-code__empty {
  padding: 8px 0;
}
</style>

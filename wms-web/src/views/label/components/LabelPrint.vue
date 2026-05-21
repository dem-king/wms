<template>
  <el-dialog v-model="dialogVisible" title="标签打印预览" width="800px" top="5vh" @close="handleClose">
    <div class="print-toolbar">
      <el-button type="primary" :icon="Printer" :disabled="hasInvalidLabels" @click="handlePrint">打印</el-button>
      <span class="print-tip">共 {{ labels.length }} 个标签</span>
    </div>
    <el-alert
      v-if="hasInvalidLabels"
      :title="`有 ${invalidLabels.length} 个标签缺少可渲染编码内容，已阻止打印错误码图`"
      type="warning"
      :closable="false"
      show-icon
      class="print-warning"
    />
    <el-alert
      v-if="printFeedback"
      :title="printFeedback.message"
      :type="printFeedback.type"
      :closable="false"
      show-icon
      class="print-feedback"
    />
    <div class="print-grid" id="label-print-area">
      <div v-for="item in previewItems" :key="item.label.id" class="print-label">
        <div class="print-label__header">
          <span class="print-label__no">{{ item.label.labelNo }}</span>
          <el-tag size="small" :type="labelTypeTagType(item.label.labelType)">{{ labelTypeMap[item.label.labelType] }}</el-tag>
        </div>
        <div class="print-label__body">
          <div class="print-label__info">
            <p>物品：{{ item.label.itemName || '-' }}</p>
            <p>编码：{{ item.label.itemCode || '-' }}</p>
          </div>
          <div class="print-label__code">
            <QrBarCode
              v-if="item.display.mode === 'render' && item.display.codeType"
              :value="item.display.codeValue"
              :type="item.display.codeType"
              :width="item.display.codeType === 'qr' ? 100 : 140"
              :height="item.display.codeType === 'qr' ? 100 : 70"
            />
            <div v-else-if="item.display.mode === 'text'" class="rfid-info">
              <span class="rfid-info__label">RFID编码</span>
              <span>{{ item.display.textValue }}</span>
            </div>
            <el-alert
              v-else
              :title="item.display.errorMessage"
              type="warning"
              :closable="false"
              show-icon
              class="print-label__alert"
            />
            <div
              v-if="item.display.mode === 'error'"
              class="print-label__error-text"
            >
              {{ item.label.labelNo }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
      <el-button type="primary" :icon="Printer" :disabled="hasInvalidLabels" @click="handlePrint">打印</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Printer } from '@element-plus/icons-vue'
import QrBarCode from '@/components/QrBarCode/index.vue'
import type { ElectronicLabelVo } from '@/types/label'
import { resolveLabelCodeDisplay } from '../code-display'
import {
  createPrintBlockedFeedback,
  createPrintErrorFeedback,
  createPrintSuccessFeedback,
  type PrintFeedback,
} from '../print-feedback'

const labelTypeMap: Record<number, string> = { 1: '二维码', 2: '条形码', 3: 'RFID' }

const props = defineProps<{
  /** 标签列表 */
  labels: ElectronicLabelVo[]
  /** 弹窗可见性 */
  visible: boolean
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'printed'): void
}>()

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val),
})
const printFeedback = ref<PrintFeedback | null>(null)

const previewItems = computed(() => props.labels.map((label) => ({
  label,
  display: resolveLabelCodeDisplay(label),
})))

const invalidLabels = computed(() => previewItems.value.filter((item) => item.display.mode === 'error'))

const hasInvalidLabels = computed(() => invalidLabels.value.length > 0)

watch(() => props.visible, (visible) => {
  if (visible) {
    printFeedback.value = null
  }
})

function labelTypeTagType(labelType: number): 'success' | 'warning' | 'info' {
  const map: Record<number, 'success' | 'warning' | 'info'> = { 1: 'success', 2: 'warning', 3: 'info' }
  return map[labelType] || 'info'
}

function handlePrint() {
  if (hasInvalidLabels.value) {
    printFeedback.value = createPrintBlockedFeedback(invalidLabels.value.length)
    ElMessage.warning(printFeedback.value.message)
    return
  }

  const printArea = document.getElementById('label-print-area')
  let iframe: HTMLIFrameElement | null = null

  try {
    if (!printArea) {
      throw new Error('未找到打印区域')
    }

    iframe = document.createElement('iframe')
    iframe.style.position = 'absolute'
    iframe.style.width = '0'
    iframe.style.height = '0'
    iframe.style.border = 'none'
    document.body.appendChild(iframe)

    const doc = iframe.contentWindow?.document
    if (!doc) {
      throw new Error('无法创建打印文档')
    }

    doc.open()
    doc.write(`
      <html>
      <head>
        <style>
          * { margin: 0; padding: 0; box-sizing: border-box; }
          body { font-family: 'Microsoft YaHei', sans-serif; padding: 10px; }
          .print-label { border: 1px solid #333; padding: 10px; margin-bottom: 10px; page-break-inside: avoid; }
          .print-label__header { display: flex; justify-content: space-between; margin-bottom: 6px; font-weight: bold; }
          .print-label__body { display: flex; justify-content: space-between; }
          .print-label__info p { margin: 2px 0; font-size: 12px; }
          .print-label__code { flex-shrink: 0; }
          @media print { .print-label { page-break-inside: avoid; } }
        </style>
      </head>
      <body>${printArea.innerHTML}</body>
      </html>
    `)
    doc.close()

    iframe.contentWindow?.focus()
    iframe.contentWindow?.print()
    printFeedback.value = createPrintSuccessFeedback(props.labels.length)
    ElMessage.success(printFeedback.value.message)
    emit('printed')
  } catch {
    printFeedback.value = createPrintErrorFeedback()
    ElMessage.error(printFeedback.value.message)
  } finally {
    if (iframe?.parentNode) {
      iframe.parentNode.removeChild(iframe)
    }
  }
}

function handleClose() {
  printFeedback.value = null
  dialogVisible.value = false
}
</script>

<style scoped lang="scss">
.print-toolbar {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.print-tip {
  color: #909399;
  font-size: 13px;
}

.print-warning {
  margin-bottom: 12px;
}

.print-feedback {
  margin-bottom: 12px;
}

.print-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  max-height: 55vh;
  overflow-y: auto;
}

.print-label {
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  padding: 10px;

  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 6px;
  }

  &__no {
    font-weight: bold;
    font-size: 13px;
  }

  &__body {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
  }

  &__info {
    p {
      margin: 2px 0;
      font-size: 12px;
      color: #606266;
    }
  }

  &__code {
    flex-shrink: 0;
    margin-left: 8px;
  }

  &__alert {
    max-width: 180px;
  }

  &__error-text {
    margin-top: 6px;
    color: #909399;
    font-size: 12px;
    text-align: center;
  }
}

.rfid-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
  max-width: 180px;
  padding: 10px;
  color: #606266;
  font-size: 12px;
  text-align: center;
  word-break: break-all;
}

.rfid-info__label {
  color: #909399;
}
</style>

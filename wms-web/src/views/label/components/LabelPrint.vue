<template>
  <el-dialog v-model="dialogVisible" title="标签打印预览" width="800px" top="5vh" @close="handleClose">
    <div class="print-toolbar">
      <el-button type="primary" :icon="Printer" @click="handlePrint">打印</el-button>
      <span class="print-tip">共 {{ labels.length }} 个标签</span>
    </div>
    <div class="print-grid" id="label-print-area">
      <div v-for="label in labels" :key="label.id" class="print-label">
        <div class="print-label__header">
          <span class="print-label__no">{{ label.labelNo }}</span>
          <el-tag size="small" :type="labelTypeTagType(label.labelType)">{{ labelTypeMap[label.labelType] }}</el-tag>
        </div>
        <div class="print-label__body">
          <div class="print-label__info">
            <p>物品：{{ label.itemName }}</p>
            <p>编码：{{ label.itemCode }}</p>
          </div>
          <div class="print-label__code">
            <QrBarCode
              v-if="label.labelType === 1"
              :value="label.qrContent || label.labelNo"
              type="qr"
              :width="100"
              :height="100"
            />
            <QrBarCode
              v-else-if="label.labelType === 2"
              :value="label.barcodeContent || label.labelNo"
              type="barcode"
              :width="140"
              :height="70"
            />
            <div v-else class="rfid-info">
              RFID: {{ label.rfidCode || label.labelNo }}
            </div>
          </div>
        </div>
      </div>
    </div>
    <template #footer>
      <el-button @click="handleClose">关闭</el-button>
      <el-button type="primary" :icon="Printer" @click="handlePrint">打印</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { Printer } from '@element-plus/icons-vue'
import QrBarCode from '@/components/QrBarCode/index.vue'
import type { ElectronicLabelVo } from '@/types/label'

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

function labelTypeTagType(labelType: number): 'success' | 'warning' | 'info' {
  const map: Record<number, 'success' | 'warning' | 'info'> = { 1: 'success', 2: 'warning', 3: 'info' }
  return map[labelType] || 'info'
}

function handlePrint() {
  const printArea = document.getElementById('label-print-area')
  if (!printArea) return

  const iframe = document.createElement('iframe')
  iframe.style.position = 'absolute'
  iframe.style.width = '0'
  iframe.style.height = '0'
  iframe.style.border = 'none'
  document.body.appendChild(iframe)

  const doc = iframe.contentWindow?.document
  if (!doc) return

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
  document.body.removeChild(iframe)
  emit('printed')
}

function handleClose() {
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
}

.rfid-info {
  font-size: 12px;
  color: #909399;
  padding: 10px;
  text-align: center;
}
</style>

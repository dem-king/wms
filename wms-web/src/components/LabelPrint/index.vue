<template>
  <el-dialog v-model="dialogVisible" title="标签打印预览" width="900px" top="5vh" @close="handleClose">
    <div class="print-toolbar">
      <el-button type="primary" :icon="Printer" @click="handlePrint">打印</el-button>
      <span class="print-tip">共 {{ labels.length }} 个标签</span>
    </div>
    <div class="print-content" id="label-print-area">
      <div v-for="label in labels" :key="label.id" class="label-card">
        <div class="label-card__header">
          <span class="label-card__no">{{ label.labelNo }}</span>
          <el-tag size="small" :type="labelTypeTagType(label.labelType)">{{ labelTypeMap[label.labelType] }}</el-tag>
        </div>
        <div class="label-card__body">
          <div class="label-card__info">
            <p><strong>物品名称：</strong>{{ label.itemName }}</p>
            <p><strong>物品编码：</strong>{{ label.itemCode }}</p>
            <p v-if="label.batchNo"><strong>批次号：</strong>{{ label.batchNo }}</p>
          </div>
          <div class="label-card__code">
            <QrBarCode
              v-if="label.labelType === 1"
              :value="label.qrContent || label.labelNo"
              type="qr"
              :width="120"
              :height="120"
            />
            <QrBarCode
              v-else-if="label.labelType === 2"
              :value="label.barcodeContent || label.labelNo"
              type="barcode"
              :width="160"
              :height="80"
            />
            <div v-else-if="label.labelType === 3" class="rfid-placeholder">
              <el-icon :size="40"><Cpu /></el-icon>
              <span>RFID: {{ label.rfidCode || label.labelNo }}</span>
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
import { Printer, Cpu } from '@element-plus/icons-vue'
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

/** 使用iframe方式打印 */
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
        .label-card { border: 1px solid #333; padding: 12px; margin-bottom: 12px; page-break-inside: avoid; }
        .label-card__header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 8px; font-size: 14px; font-weight: bold; }
        .label-card__body { display: flex; justify-content: space-between; }
        .label-card__info p { margin: 4px 0; font-size: 12px; }
        .label-card__code { text-align: right; }
        @media print { .label-card { page-break-inside: avoid; } }
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

.print-content {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  max-height: 60vh;
  overflow-y: auto;
  padding: 4px;
}

.label-card {
  border: 1px solid #dcdfe6;
  border-radius: 6px;
  padding: 12px;
  page-break-inside: avoid;

  &__header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
  }

  &__no {
    font-weight: bold;
    font-size: 14px;
  }

  &__body {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
  }

  &__info {
    flex: 1;

    p {
      margin: 4px 0;
      font-size: 13px;
      color: #606266;
    }
  }

  &__code {
    flex-shrink: 0;
    margin-left: 12px;
  }
}

.rfid-placeholder {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  color: #909399;
  font-size: 12px;
}
</style>

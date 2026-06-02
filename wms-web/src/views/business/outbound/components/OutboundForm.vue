<template>
  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑出库单' : '新增出库单'" width="900px" @close="handleClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="库房" prop="warehouseId">
            <el-select v-model="form.warehouseId" placeholder="请选择库房" style="width: 100%">
              <el-option v-for="w in warehouseList" :key="w.id" :label="w.warehouseName" :value="w.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="出库类型" prop="outboundType">
            <el-select v-model="form.outboundType" placeholder="请选择出库类型" style="width: 100%">
              <el-option label="领用出库" value="BORROW" />
              <el-option label="报废出库" value="SCRAP" />
              <el-option label="调拨出库" value="TRANSFER" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="领用人" prop="recipient">
            <el-input v-model="form.recipient" placeholder="请输入领用人" />
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="用途" prop="purpose">
            <el-input v-model="form.purpose" placeholder="请输入用途" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="归还日期" prop="returnDate">
            <el-date-picker v-model="form.returnDate" type="date" placeholder="请选择归还日期" value-format="YYYY-MM-DD" style="width: 100%" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
      </el-form-item>
    </el-form>

    <el-divider content-position="left">出库明细</el-divider>
    <div class="detail-toolbar">
      <el-button type="primary" plain :icon="Plus" @click="addDetailRow">新增行</el-button>
      <el-input
        v-model="scanCode"
        class="scan-input"
        clearable
        placeholder="扫码枪输入标签编码后回车"
        @keyup.enter="handleScan"
      >
        <template #append>
          <el-button :loading="scanLoading" @click="handleScan">扫码识别</el-button>
        </template>
      </el-input>
    </div>
    <el-alert
      v-if="scanFeedback.message"
      :title="scanFeedback.message"
      :type="scanFeedback.type"
      :closable="false"
      show-icon
      class="scan-feedback"
    />
    <el-table :data="form.details" border>
      <el-table-column label="物品" min-width="200">
        <template #default="{ row }">
          <div class="item-cell">
            <el-select v-model="row.itemId" placeholder="请选择物品" filterable @change="(val: EntityId) => handleItemChange(row, val)">
              <el-option v-for="item in itemList" :key="item.id" :label="`${item.itemCode} - ${item.itemName}`" :value="item.id" />
            </el-select>
            <div v-if="row.scannedLabels?.length" class="scan-tags">
              <el-tag v-for="label in row.scannedLabels" :key="label.labelId" size="small" type="success">
                {{ label.labelNo }}
              </el-tag>
            </div>
          </div>
        </template>
      </el-table-column>
      <el-table-column prop="specModel" label="规格型号" min-width="120" />
      <el-table-column prop="unit" label="单位" min-width="80" />
      <el-table-column label="库位" min-width="150">
        <template #default="{ row }">
          <el-select v-model="row.binId" placeholder="请选择库位" filterable :disabled="!form.warehouseId">
            <el-option v-for="bin in binList" :key="bin.id" :label="bin.binCode" :value="bin.id" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="数量" min-width="120">
        <template #default="{ row }">
          <el-input-number v-model="row.quantity" :min="1" size="small" @change="calcAmount(row)" />
        </template>
      </el-table-column>
      <el-table-column label="单价" min-width="120">
        <template #default="{ row }">
          <el-input-number v-model="row.unitPrice" :min="0" :precision="2" size="small" @change="calcAmount(row)" />
        </template>
      </el-table-column>
      <el-table-column prop="amount" label="金额" min-width="100" />
      <el-table-column label="操作" class-name="table-action-column" fixed="right" min-width="180">
        <template #default="{ $index }">
          <TableActionGroup
            :actions="[
              { label: '删除', type: 'danger', icon: Delete, onClick: () => { form.details.splice($index, 1) } },
            ]"
          />
        </template>
      </el-table-column>
    </el-table>

    <template #footer>
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import { addOutboundOrder, scanOutboundOrder, updateOutboundOrder } from '@/api/business/outbound'
import { getWarehouseList } from '@/api/warehouse/warehouse'
import { getBinListByWarehouse } from '@/api/warehouse/bin'
import { getItemList } from '@/api/item/item'
import { collectScannedLabelIds, mergeScannedDetail } from '@/views/business/order-scan'
import type {
  EntityId,
  OrderScanDetailRow,
  OutboundOrderVo,
  OutboundOrderDto,
  OutboundType,
} from '@/types/business'
import type { WmsBinVo, WmsWarehouseVo } from '@/types/warehouse'
import type { WmsItemVo } from '@/types/item'

interface DetailRow extends OrderScanDetailRow {
  itemId: EntityId
  quantity: number
  unitPrice: number
  binId?: EntityId
}

interface ScanFeedback {
  type: 'success' | 'warning' | 'error'
  message: string
}

const props = defineProps<{
  visible: boolean
  isEdit: boolean
  formData: OutboundOrderVo | null
}>()
const emit = defineEmits<{
  'update:visible': [val: boolean]
  success: []
}>()

const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const warehouseList = ref<WmsWarehouseVo[]>([])
const binList = ref<WmsBinVo[]>([])
const itemList = ref<WmsItemVo[]>([])
const scanCode = ref('')
const scanLoading = ref(false)
const scanFeedback = ref<ScanFeedback>({ type: 'success', message: '' })

const form = reactive<{
  warehouseId: EntityId | undefined
  outboundType: OutboundType | ''
  recipient: string
  purpose: string
  returnDate: string
  remark: string
  details: DetailRow[]
}>({
  warehouseId: undefined,
  outboundType: '',
  recipient: '',
  purpose: '',
  returnDate: '',
  remark: '',
  details: [],
})

const rules: FormRules = {
  warehouseId: [{ required: true, message: '请选择库房', trigger: 'change' }],
  outboundType: [{ required: true, message: '请选择出库类型', trigger: 'change' }],
  recipient: [{ required: true, message: '请输入领用人', trigger: 'blur' }],
}

watch(() => props.visible, async (val) => {
  dialogVisible.value = val
  if (val) {
    await loadOptions()
    resetScanState()
    if (props.isEdit && props.formData) {
      Object.assign(form, {
        warehouseId: props.formData.warehouseId,
        outboundType: props.formData.outboundType,
        recipient: props.formData.recipient,
        purpose: props.formData.purpose,
        returnDate: props.formData.returnDate,
        remark: props.formData.remark,
        details: (props.formData.details || []).map(d => ({
          itemId: d.itemId,
          quantity: d.quantity,
          unitPrice: d.unitPrice,
          binId: d.binId,
          specModel: d.specModel,
          unit: d.unit,
          amount: d.amount,
        })),
      })
      form.details.forEach((detail) => syncDetailRowFromItem(detail))
    }
  }
})
watch(dialogVisible, (val) => { emit('update:visible', val) })
watch(() => form.warehouseId, async (warehouseId, oldWarehouseId) => {
  await loadBinsByWarehouse(warehouseId)
  if (oldWarehouseId !== undefined) {
    clearInvalidDetailBins()
  }
})

async function loadOptions() {
  const [whRes, iRes] = await Promise.all([getWarehouseList(), getItemList({ page: 1, size: 1000, status: 1 })])
  warehouseList.value = whRes.data
  itemList.value = iRes.data.records
}

async function loadBinsByWarehouse(warehouseId: EntityId | undefined) {
  if (!warehouseId) {
    binList.value = []
    return
  }
  const res = await getBinListByWarehouse(warehouseId)
  binList.value = res.data
}

function clearInvalidDetailBins() {
  const validBinIds = new Set(binList.value.map(bin => bin.id))
  form.details.forEach((detail) => {
    if (detail.binId && !validBinIds.has(detail.binId)) {
      detail.binId = undefined
    }
  })
}

function addDetailRow() {
  form.details.push({ itemId: undefined as unknown as EntityId, quantity: 1, unitPrice: 0, binId: undefined, specModel: '', unit: '', amount: 0 })
}

function syncDetailRowFromItem(row: DetailRow) {
  const item = itemList.value.find(i => i.id === row.itemId)
  if (item) {
    row.itemName = item.itemName
    row.itemCode = item.itemCode
    row.specModel = item.specModel
    row.unit = item.unit
  }
}

function handleItemChange(row: DetailRow, itemId: EntityId) {
  row.itemId = itemId
  syncDetailRowFromItem(row)
}

function calcAmount(row: DetailRow) {
  row.amount = row.quantity * row.unitPrice
}

function resetScanState() {
  scanCode.value = ''
  scanLoading.value = false
  scanFeedback.value = { type: 'success', message: '' }
}

function resolveErrorMessage(error: unknown, fallback: string) {
  return error instanceof Error && error.message ? error.message : fallback
}

function applyScannedResult(result: Awaited<ReturnType<typeof scanOutboundOrder>>['data']) {
  const mergedDetails = mergeScannedDetail(form.details, result)
  form.details.splice(0, form.details.length, ...mergedDetails.map(detail => ({ ...detail })))
  form.details.forEach((detail) => {
    syncDetailRowFromItem(detail)
    calcAmount(detail)
  })
}

async function handleScan() {
  const code = scanCode.value.trim()
  if (!code) {
    scanFeedback.value = { type: 'warning', message: '请输入标签编码后再扫码识别' }
    return
  }

  scanLoading.value = true
  try {
    const res = await scanOutboundOrder({
      code,
      currentLabelIds: collectScannedLabelIds(form.details),
    })
    applyScannedResult(res.data)
    scanFeedback.value = {
      type: 'success',
      message: `已识别标签 ${res.data.labelNo}，已回填 ${res.data.itemName || res.data.itemCode}`,
    }
    scanCode.value = ''
  } catch (error) {
    scanFeedback.value = {
      type: 'error',
      message: resolveErrorMessage(error, '出库扫码失败，请稍后重试'),
    }
  } finally {
    scanLoading.value = false
  }
}

async function handleSubmit() {
  await formRef.value?.validate()
  if (form.details.length === 0) {
    ElMessage.warning('请添加出库明细')
    return
  }
  if (form.details.some(detail => !detail.binId)) {
    ElMessage.warning('请选择明细库位')
    return
  }
  submitLoading.value = true
  try {
    const dto: OutboundOrderDto = {
      warehouseId: form.warehouseId!,
      outboundType: form.outboundType as OutboundType,
      recipient: form.recipient,
      purpose: form.purpose,
      returnDate: form.returnDate,
      remark: form.remark,
      details: form.details.map(d => ({ itemId: d.itemId, quantity: d.quantity, unitPrice: d.unitPrice, binId: d.binId! })),
    }
    if (props.isEdit && props.formData) {
      await updateOutboundOrder(props.formData.id, dto)
      ElMessage.success('编辑成功')
    } else {
      await addOutboundOrder(dto)
      ElMessage.success('新增成功')
    }
    emit('success')
    handleClose()
  } finally {
    submitLoading.value = false
  }
}

function handleClose() {
  dialogVisible.value = false
  formRef.value?.resetFields()
  Object.assign(form, { warehouseId: undefined, outboundType: '', recipient: '', purpose: '', returnDate: '', remark: '', details: [] })
  binList.value = []
  resetScanState()
}
</script>

<style scoped lang="scss">
.mb8 {
  margin-bottom: 8px;
}

.detail-toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 8px;
}

.scan-input {
  max-width: 360px;
}

.scan-feedback {
  margin-bottom: 12px;
}

.item-cell {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.scan-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
}
</style>

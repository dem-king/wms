<template>
  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑归还单' : '新增归还单'" width="900px" @close="handleClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="关联出库单" prop="outboundOrderId">
            <el-select v-model="form.outboundOrderId" placeholder="请选择出库单" style="width: 100%" @change="handleOutboundChange">
              <el-option v-for="o in outboundList" :key="o.id" :label="`${o.orderNo} - ${o.recipient}`" :value="o.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="归还人" prop="receiver">
            <el-input v-model="form.receiver" placeholder="请输入归还人" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="归还库房">
            <el-input :model-value="returnWarehouseName" disabled placeholder="选择出库单后自动带出" />
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
      </el-form-item>
    </el-form>

    <el-divider content-position="left">归还明细</el-divider>
    <div class="detail-toolbar">
      <el-button type="primary" plain :icon="Plus" @click="addDetailRow">新增行</el-button>
    </div>
    <el-table :data="form.details" border>
      <el-table-column label="物品" min-width="200">
        <template #default="{ row }">
          <el-select v-model="row.itemId" placeholder="请选择物品" filterable>
            <el-option v-for="item in outboundItems" :key="item.itemId" :label="`${item.itemCode} - ${item.itemName}`" :value="item.itemId" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="归还库位" min-width="150">
        <template #default="{ row }">
          <el-select v-model="row.binId" placeholder="请选择归还库位" filterable :disabled="!returnWarehouseId">
            <el-option v-for="bin in returnBinList" :key="bin.id" :label="bin.binCode" :value="bin.id" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="归还数量" min-width="120">
        <template #default="{ row }">
          <el-input-number v-model="row.quantity" :min="1" size="small" />
        </template>
      </el-table-column>
      <el-table-column label="物品状态" min-width="120">
        <template #default="{ row }">
          <el-select v-model="row.conditionStatus" placeholder="请选择" size="small">
            <el-option label="正常" :value="RETURN_CONDITION_NORMAL" />
            <el-option label="损坏" :value="RETURN_CONDITION_DAMAGED" />
            <el-option label="丢失" :value="RETURN_CONDITION_LOST" />
            <el-option label="数量不符" :value="RETURN_CONDITION_MISMATCH" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="实际归还数" min-width="130">
        <template #default="{ row }">
          <el-input-number
            v-if="row.conditionStatus === RETURN_CONDITION_MISMATCH"
            v-model="row.actualQuantity"
            :min="0"
            :max="row.quantity"
            size="small"
          />
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="异常说明" min-width="150">
        <template #default="{ row }">
          <el-input v-if="row.conditionStatus !== RETURN_CONDITION_NORMAL" v-model="row.abnormalRemark" placeholder="请输入异常说明" size="small" />
          <span v-else>-</span>
        </template>
      </el-table-column>
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
import { computed, ref, reactive, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import { addReturnOrder, updateReturnOrder } from '@/api/business/return'
import { getOutboundOrders } from '@/api/business/outbound'
import { getBinListByWarehouse } from '@/api/warehouse/bin'
import type {
  EntityId,
  ReturnOrderVo,
  ReturnOrderDto,
  ReturnDetailDto,
} from '@/types/business'
import type { OutboundOrderVo, OutboundDetailVo } from '@/types/business'
import type { WmsBinVo } from '@/types/warehouse'

const RETURN_CONDITION_NORMAL = 1
const RETURN_CONDITION_DAMAGED = 2
const RETURN_CONDITION_LOST = 3
const RETURN_CONDITION_MISMATCH = 4

interface DetailRow extends ReturnDetailDto {
  binId?: EntityId
  actualQuantity?: number
}

const props = defineProps<{
  visible: boolean
  isEdit: boolean
  formData: ReturnOrderVo | null
}>()
const emit = defineEmits<{
  'update:visible': [val: boolean]
  success: []
}>()

const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const outboundList = ref<OutboundOrderVo[]>([])
const outboundItems = ref<OutboundDetailVo[]>([])
const returnBinList = ref<WmsBinVo[]>([])

const form = reactive<{
  outboundOrderId: EntityId | undefined
  receiver: string
  remark: string
  details: DetailRow[]
}>({
  outboundOrderId: undefined,
  receiver: '',
  remark: '',
  details: [],
})

const selectedOutboundOrder = computed(() => outboundList.value.find(o => o.id === form.outboundOrderId))
const returnWarehouseId = computed(() => selectedOutboundOrder.value?.warehouseId)
const returnWarehouseName = computed(() => selectedOutboundOrder.value?.warehouseName || '')

const rules: FormRules = {
  outboundOrderId: [{ required: true, message: '请选择出库单', trigger: 'change' }],
  receiver: [{ required: true, message: '请输入归还人', trigger: 'blur' }],
}

watch(() => props.visible, async (val) => {
  dialogVisible.value = val
  if (val) {
    const res = await getOutboundOrders({ page: 1, size: 1000, status: 'COMPLETED' })
    outboundList.value = res.data.records
    if (props.isEdit && props.formData) {
      Object.assign(form, {
        outboundOrderId: props.formData.outboundOrderId,
        receiver: props.formData.receiver,
        remark: props.formData.remark,
        details: (props.formData.details || []).map(d => ({
          itemId: d.itemId,
          binId: d.binId,
          quantity: d.quantity,
          conditionStatus: d.conditionStatus,
          abnormalRemark: d.abnormalRemark,
          actualQuantity: d.actualQuantity,
        })),
      })
      const order = outboundList.value.find(o => o.id === props.formData!.outboundOrderId)
      outboundItems.value = order?.details || []
    }
  }
})
watch(dialogVisible, (val) => { emit('update:visible', val) })
watch(returnWarehouseId, async (warehouseId, oldWarehouseId) => {
  await loadReturnBins(warehouseId)
  if (oldWarehouseId !== undefined) {
    clearInvalidDetailBins()
  }
})

async function loadReturnBins(warehouseId: EntityId | undefined) {
  if (!warehouseId) {
    returnBinList.value = []
    return
  }
  const res = await getBinListByWarehouse(warehouseId)
  returnBinList.value = res.data
}

function clearInvalidDetailBins() {
  const validBinIds = new Set(returnBinList.value.map(bin => bin.id))
  form.details.forEach((detail) => {
    if (detail.binId && !validBinIds.has(detail.binId)) {
      detail.binId = undefined
    }
  })
}

function handleOutboundChange(orderId: EntityId) {
  const order = outboundList.value.find(o => o.id === orderId)
  outboundItems.value = order?.details || []
  form.details = outboundItems.value.map(d => ({
    itemId: d.itemId,
    binId: undefined,
    quantity: d.quantity,
    conditionStatus: RETURN_CONDITION_NORMAL,
    abnormalRemark: '',
    actualQuantity: undefined,
  }))
}

function addDetailRow() {
  form.details.push({
    itemId: undefined as unknown as EntityId,
    binId: undefined,
    quantity: 1,
    conditionStatus: RETURN_CONDITION_NORMAL,
    abnormalRemark: '',
    actualQuantity: undefined,
  })
}

function detailRequiresBin(detail: DetailRow) {
  if (detail.conditionStatus === RETURN_CONDITION_DAMAGED || detail.conditionStatus === RETURN_CONDITION_LOST) {
    return false
  }
  if (detail.conditionStatus === RETURN_CONDITION_MISMATCH) {
    return (detail.actualQuantity ?? 0) > 0
  }
  return true
}

async function handleSubmit() {
  await formRef.value?.validate()
  if (form.details.length === 0) {
    ElMessage.warning('请添加归还明细')
    return
  }
  if (form.details.some(detail => detailRequiresBin(detail) && !detail.binId)) {
    ElMessage.warning('请选择归还库位')
    return
  }
  submitLoading.value = true
  try {
    const dto: ReturnOrderDto = {
      outboundOrderId: form.outboundOrderId!,
      receiver: form.receiver,
      remark: form.remark,
      details: form.details.map(d => ({
        itemId: d.itemId,
        binId: d.binId,
        quantity: d.quantity,
        conditionStatus: d.conditionStatus,
        abnormalRemark: d.abnormalRemark,
        actualQuantity: d.actualQuantity,
      })),
    }
    if (props.isEdit && props.formData) {
      await updateReturnOrder(props.formData.id, dto)
      ElMessage.success('编辑成功')
    } else {
      await addReturnOrder(dto)
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
  Object.assign(form, { outboundOrderId: undefined, receiver: '', remark: '', details: [] })
  outboundItems.value = []
  returnBinList.value = []
}
</script>

<style scoped lang="scss">
.detail-toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 8px;
}
</style>

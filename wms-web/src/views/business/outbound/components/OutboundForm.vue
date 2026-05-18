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
    <el-row class="mb8">
      <el-button type="primary" plain :icon="Plus" @click="addDetailRow">新增行</el-button>
    </el-row>
    <el-table :data="form.details" border>
      <el-table-column label="物品" min-width="200">
        <template #default="{ row }">
          <el-select v-model="row.itemId" placeholder="请选择物品" filterable @change="(val: number) => handleItemChange(row, val)">
            <el-option v-for="item in itemList" :key="item.id" :label="`${item.itemCode} - ${item.itemName}`" :value="item.id" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column prop="specModel" label="规格型号" min-width="120" />
      <el-table-column prop="unit" label="单位" min-width="80" />
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
      <el-table-column label="操作" class-name="table-action-column" fixed="right">
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
import { addOutboundOrder, updateOutboundOrder } from '@/api/business/outbound'
import { getWarehouseList } from '@/api/warehouse/warehouse'
import { getItemList } from '@/api/item/item'
import type { OutboundOrderVo, OutboundOrderDto, OutboundDetailDto } from '@/types/business'
import type { WmsWarehouseVo } from '@/types/warehouse'
import type { WmsItemVo } from '@/types/item'

interface DetailRow extends OutboundDetailDto {
  specModel?: string
  unit?: string
  amount?: number
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
const itemList = ref<WmsItemVo[]>([])

const form = reactive<{
  warehouseId: number | undefined
  outboundType: string
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
          specModel: d.specModel,
          unit: d.unit,
          amount: d.amount,
        })),
      })
    }
  }
})
watch(dialogVisible, (val) => { emit('update:visible', val) })

async function loadOptions() {
  const [whRes, iRes] = await Promise.all([getWarehouseList(), getItemList({ page: 1, size: 1000, status: 1 })])
  warehouseList.value = whRes.data
  itemList.value = iRes.data.records
}

function addDetailRow() {
  form.details.push({ itemId: undefined as unknown as number, quantity: 1, unitPrice: 0, specModel: '', unit: '', amount: 0 })
}

function handleItemChange(row: DetailRow, itemId: number) {
  const item = itemList.value.find(i => i.id === itemId)
  if (item) {
    row.specModel = item.specModel
    row.unit = item.unit
  }
}

function calcAmount(row: DetailRow) {
  row.amount = row.quantity * row.unitPrice
}

async function handleSubmit() {
  await formRef.value?.validate()
  if (form.details.length === 0) {
    ElMessage.warning('请添加出库明细')
    return
  }
  submitLoading.value = true
  try {
    const dto: OutboundOrderDto = {
      warehouseId: form.warehouseId!,
      outboundType: form.outboundType,
      recipient: form.recipient,
      purpose: form.purpose,
      returnDate: form.returnDate,
      remark: form.remark,
      details: form.details.map(d => ({ itemId: d.itemId, quantity: d.quantity, unitPrice: d.unitPrice })),
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
}
</script>

<style scoped lang="scss">
.mb8 {
  margin-bottom: 8px;
}
</style>


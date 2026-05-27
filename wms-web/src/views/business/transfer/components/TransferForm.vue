<template>
  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑调拨单' : '新增调拨单'" width="900px" @close="handleClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-row :gutter="16">
        <el-col :span="12">
          <el-form-item label="调出库房" prop="fromWarehouseId">
            <el-select v-model="form.fromWarehouseId" placeholder="请选择调出库房" style="width: 100%">
              <el-option v-for="w in warehouseList" :key="w.id" :label="w.warehouseName" :value="w.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="调入库房" prop="toWarehouseId">
            <el-select v-model="form.toWarehouseId" placeholder="请选择调入库房" style="width: 100%">
              <el-option v-for="w in warehouseList" :key="w.id" :label="w.warehouseName" :value="w.id" />
            </el-select>
          </el-form-item>
        </el-col>
      </el-row>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
      </el-form-item>
    </el-form>

    <el-divider content-position="left">调拨明细</el-divider>
    <div class="detail-toolbar">
      <el-button type="primary" plain :icon="Plus" @click="addDetailRow">新增行</el-button>
    </div>
    <el-table :data="form.details" border>
      <el-table-column label="物品" min-width="200">
        <template #default="{ row }">
          <el-select v-model="row.itemId" placeholder="请选择物品" filterable @change="(val: number) => handleItemChange(row, val)">
            <el-option v-for="item in itemList" :key="item.id" :label="`${item.itemCode} - ${item.itemName}`" :value="item.id" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="调拨数量" min-width="120">
        <template #default="{ row }">
          <el-input-number v-model="row.quantity" :min="1" size="small" />
        </template>
      </el-table-column>
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
import { addTransferOrder, updateTransferOrder } from '@/api/business/transfer'
import { getWarehouseList } from '@/api/warehouse/warehouse'
import { getItemList } from '@/api/item/item'
import type { TransferOrderVo, TransferOrderDto, TransferDetailDto } from '@/types/business'
import type { WmsWarehouseVo } from '@/types/warehouse'
import type { WmsItemVo } from '@/types/item'

interface DetailRow extends TransferDetailDto {}

const props = defineProps<{
  visible: boolean
  isEdit: boolean
  formData: TransferOrderVo | null
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
  fromWarehouseId: number | undefined
  toWarehouseId: number | undefined
  remark: string
  details: DetailRow[]
}>({
  fromWarehouseId: undefined,
  toWarehouseId: undefined,
  remark: '',
  details: [],
})

const rules: FormRules = {
  fromWarehouseId: [{ required: true, message: '请选择调出库房', trigger: 'change' }],
  toWarehouseId: [{ required: true, message: '请选择调入库房', trigger: 'change' }],
}

watch(() => props.visible, async (val) => {
  dialogVisible.value = val
  if (val) {
    const [whRes, iRes] = await Promise.all([
      getWarehouseList(),
      getItemList({ page: 1, size: 1000, status: 1 }),
    ])
    warehouseList.value = whRes.data
    itemList.value = iRes.data.records
    if (props.isEdit && props.formData) {
      Object.assign(form, {
        fromWarehouseId: props.formData.fromWarehouseId,
        toWarehouseId: props.formData.toWarehouseId,
        remark: props.formData.remark,
        details: (props.formData.details || []).map(d => ({
          itemId: d.itemId,
          quantity: d.quantity,
        })),
      })
    }
  }
})
watch(dialogVisible, (val) => { emit('update:visible', val) })

function addDetailRow() {
  form.details.push({ itemId: undefined as unknown as number, quantity: 1 })
}

function handleItemChange(row: DetailRow, val: number) {
  const item = itemList.value.find(candidate => candidate.id === val)
  if (!item) {
    ElMessage.warning('未找到所选物品')
    row.itemId = undefined as unknown as number
    return
  }
  const hasDuplicate = form.details.some(detail => detail !== row && detail.itemId === val)
  if (hasDuplicate) {
    ElMessage.warning('同一物品无需重复添加，请直接修改数量')
    row.itemId = undefined as unknown as number
    return
  }
  if (item.currentStock <= 0) {
    ElMessage.warning(`物品“${item.itemName}”当前无可调拨库存`)
  }
  if (item.currentStock > 0 && row.quantity > item.currentStock) {
    row.quantity = item.currentStock
    ElMessage.warning(`调拨数量已调整为当前库存上限 ${item.currentStock}`)
    return
  }
  row.quantity = Math.max(row.quantity || 1, 1)
}

function validateDetails() {
  for (const detail of form.details) {
    if (!detail.itemId) {
      ElMessage.warning('请选择每一行的物品')
      return false
    }
    if (!detail.quantity || detail.quantity <= 0) {
      ElMessage.warning('调拨数量必须大于 0')
      return false
    }
    const item = itemList.value.find(candidate => candidate.id === detail.itemId)
    if (item && item.currentStock > 0 && detail.quantity > item.currentStock) {
      ElMessage.warning(`物品“${item.itemName}”的调拨数量不能超过当前库存 ${item.currentStock}`)
      return false
    }
  }
  return true
}

async function handleSubmit() {
  await formRef.value?.validate()
  if (form.fromWarehouseId === form.toWarehouseId) {
    ElMessage.warning('调出库房和调入库房不能相同')
    return
  }
  if (form.details.length === 0) {
    ElMessage.warning('请添加调拨明细')
    return
  }
  if (!validateDetails()) {
    return
  }
  submitLoading.value = true
  try {
    const dto: TransferOrderDto = {
      fromWarehouseId: form.fromWarehouseId!,
      toWarehouseId: form.toWarehouseId!,
      remark: form.remark,
      details: form.details.map(d => ({ itemId: d.itemId, quantity: d.quantity })),
    }
    if (props.isEdit && props.formData) {
      await updateTransferOrder(props.formData.id, dto)
      ElMessage.success('编辑成功')
    } else {
      await addTransferOrder(dto)
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
  Object.assign(form, { fromWarehouseId: undefined, toWarehouseId: undefined, remark: '', details: [] })
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

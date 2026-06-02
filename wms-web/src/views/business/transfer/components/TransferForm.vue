<template>
  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑调拨单' : '新增调拨单'" width="min(1280px, calc(100vw - 48px))" @close="handleClose">
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
          <el-select v-model="row.itemId" placeholder="请选择物品" filterable @change="(val: EntityId) => handleItemChange(row, val)">
            <el-option v-for="item in itemList" :key="item.id" :label="`${item.itemCode} - ${item.itemName}`" :value="item.id" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="调出库位" min-width="150">
        <template #default="{ row }">
          <el-cascader
            v-model="row.fromLocationPath"
            :options="fromLocationOptions"
            :props="locationCascaderProps"
            placeholder="请选择调出库位"
            filterable
            clearable
            :disabled="!form.fromWarehouseId"
            @change="(val) => handleLocationPathChange(row, 'from', val)"
          />
        </template>
      </el-table-column>
      <el-table-column label="调入库位" min-width="150">
        <template #default="{ row }">
          <el-cascader
            v-model="row.toLocationPath"
            :options="toLocationOptions"
            :props="locationCascaderProps"
            placeholder="请选择调入库位"
            filterable
            clearable
            :disabled="!form.toWarehouseId"
            @change="(val) => handleLocationPathChange(row, 'to', val)"
          />
        </template>
      </el-table-column>
      <el-table-column label="调拨数量" min-width="120">
        <template #default="{ row }">
          <el-input-number v-model="row.quantity" :min="1" size="small" />
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
  <LocationChoiceDialog
    v-model:visible="locationChoiceVisible"
    :locations="locationChoices"
    @select="handleLocationChoice"
  />
</template>

<script setup lang="ts">
import { computed, ref, reactive, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import LocationChoiceDialog from '@/views/business/components/LocationChoiceDialog.vue'
import { addTransferOrder, updateTransferOrder } from '@/api/business/transfer'
import { getWarehouseList } from '@/api/warehouse/warehouse'
import { getItemList } from '@/api/item/item'
import {
  getSelectableLocations,
  locationToPath,
  pathToBinId,
  resolveLocationPathByBinId,
  type LocationPath,
} from '@/views/business/default-location'
import { locationCascaderProps, toWarehouseLocationOptions } from '@/views/business/location-cascader'
import type { EntityId, TransferOrderVo, TransferOrderDto, TransferDetailDto } from '@/types/business'
import type { WmsWarehouseVo } from '@/types/warehouse'
import type { ItemLocationVo, WmsItemVo } from '@/types/item'

interface DetailRow extends Omit<TransferDetailDto, 'fromBinId' | 'toBinId'> {
  fromBinId?: EntityId
  toBinId?: EntityId
  fromLocationPath?: LocationPath
  toLocationPath?: LocationPath
}

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
const locationChoiceVisible = ref(false)
const locationChoices = ref<ItemLocationVo[]>([])
let pendingLocationApply: ((location: ItemLocationVo) => void) | undefined
const fromLocationOptions = computed(() => toWarehouseLocationOptions(warehouseList.value, form.fromWarehouseId))
const toLocationOptions = computed(() => toWarehouseLocationOptions(warehouseList.value, form.toWarehouseId))

const form = reactive<{
  fromWarehouseId: EntityId | undefined
  toWarehouseId: EntityId | undefined
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
        details: (props.formData.details || []).map(d => {
          const fromLocationPath = resolveDetailLocationPath(d.itemId, d.fromBinId, props.formData?.fromWarehouseId)
          const toLocationPath = resolveDetailLocationPath(d.itemId, d.toBinId, props.formData?.toWarehouseId)
          return {
            itemId: d.itemId,
            fromBinId: pathToBinId(fromLocationPath),
            toBinId: pathToBinId(toLocationPath),
            fromLocationPath,
            toLocationPath,
            quantity: d.quantity,
          }
        }),
      })
    }
  }
})
watch(dialogVisible, (val) => { emit('update:visible', val) })
watch(() => form.fromWarehouseId, (_warehouseId, oldWarehouseId) => {
  if (oldWarehouseId !== undefined) {
    clearInvalidTransferBins('from')
  }
})
watch(() => form.toWarehouseId, (_warehouseId, oldWarehouseId) => {
  if (oldWarehouseId !== undefined) {
    clearInvalidTransferBins('to')
  }
})

function clearInvalidTransferBins(direction: 'from' | 'to') {
  const pathField = direction === 'from' ? 'fromLocationPath' : 'toLocationPath'
  const binField = direction === 'from' ? 'fromBinId' : 'toBinId'
  const warehouseId = direction === 'from' ? form.fromWarehouseId : form.toWarehouseId
  form.details.forEach((detail) => {
    if (detail[pathField] && detail[pathField]?.[0] !== warehouseId) {
      detail[pathField] = undefined
      detail[binField] = undefined
      return
    }
    if (detail[binField] && !detail[pathField]) {
      detail[binField] = undefined
    }
  })
}

function addDetailRow() {
  form.details.push({
    itemId: undefined as unknown as EntityId,
    fromBinId: undefined,
    toBinId: undefined,
    fromLocationPath: undefined,
    toLocationPath: undefined,
    quantity: 1,
  })
}

function handleItemChange(row: DetailRow, itemId: EntityId) {
  row.itemId = itemId
  const item = itemList.value.find(i => i.id === itemId)
  const locations = getSelectableLocations(item, form.fromWarehouseId, { strictWarehouse: true })
  if (locations.length > 1) {
    applyFromLocationToRow(row, undefined)
    openLocationChoice(locations, location => applyFromLocationToRow(row, location))
    return
  }
  applyFromLocationToRow(row, locations[0])
}

function applyFromLocationToRow(row: DetailRow, location: ItemLocationVo | undefined) {
  const path = locationToPath(location)
  if (!path) {
    row.fromLocationPath = undefined
    row.fromBinId = undefined
    return
  }
  form.fromWarehouseId = path[0]
  row.fromLocationPath = path
  row.fromBinId = pathToBinId(path)
}

function openLocationChoice(locations: ItemLocationVo[], apply: (location: ItemLocationVo) => void) {
  locationChoices.value = locations
  pendingLocationApply = apply
  locationChoiceVisible.value = true
}

function handleLocationChoice(location: ItemLocationVo) {
  pendingLocationApply?.(location)
  pendingLocationApply = undefined
}

function resolveDetailLocationPath(itemId: EntityId, binId: EntityId | undefined, warehouseId: EntityId | undefined) {
  const item = itemList.value.find(i => i.id === itemId)
  return resolveLocationPathByBinId(item, binId, warehouseId)
}

function handleLocationPathChange(row: DetailRow, direction: 'from' | 'to', value: unknown) {
  const path = Array.isArray(value) && value.length === 4 ? value as LocationPath : undefined
  if (direction === 'from') {
    row.fromLocationPath = path
    row.fromBinId = pathToBinId(path)
    return
  }
  row.toLocationPath = path
  row.toBinId = pathToBinId(path)
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
  if (form.details.some(detail => !detail.fromBinId || !detail.toBinId)) {
    ElMessage.warning('请选择明细调出库位和调入库位')
    return
  }
  submitLoading.value = true
  try {
    const dto: TransferOrderDto = {
      fromWarehouseId: form.fromWarehouseId!,
      toWarehouseId: form.toWarehouseId!,
      remark: form.remark,
      details: form.details.map(d => ({ itemId: d.itemId, fromBinId: d.fromBinId!, toBinId: d.toBinId!, quantity: d.quantity })),
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

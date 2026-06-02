<template>
  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑报废单' : '新增报废单'" width="900px" @close="handleClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="库房" prop="warehouseId">
        <el-select v-model="form.warehouseId" placeholder="请选择库房" style="width: 100%">
          <el-option v-for="w in warehouseList" :key="w.id" :label="w.warehouseName" :value="w.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="报废原因" prop="scrapReason">
        <el-input v-model="form.scrapReason" type="textarea" :rows="2" placeholder="请输入报废原因" />
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
      </el-form-item>
    </el-form>

    <el-divider content-position="left">报废明细</el-divider>
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
      <el-table-column label="库位" min-width="150">
        <template #default="{ row }">
          <el-select v-model="row.binId" placeholder="请选择库位" filterable :disabled="!form.warehouseId">
            <el-option v-for="bin in binList" :key="bin.id" :label="bin.binCode" :value="bin.id" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="报废数量" min-width="120">
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
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { Plus, Delete } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import { addScrapOrder, updateScrapOrder } from '@/api/business/scrap'
import { getWarehouseList } from '@/api/warehouse/warehouse'
import { getBinListByWarehouse } from '@/api/warehouse/bin'
import { getItemList } from '@/api/item/item'
import type { EntityId, ScrapOrderVo, ScrapOrderDto, ScrapDetailDto } from '@/types/business'
import type { WmsBinVo, WmsWarehouseVo } from '@/types/warehouse'
import type { WmsItemVo } from '@/types/item'

interface DetailRow extends Omit<ScrapDetailDto, 'binId'> {
  binId?: EntityId
}

const props = defineProps<{
  visible: boolean
  isEdit: boolean
  formData: ScrapOrderVo | null
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

const form = reactive<{
  warehouseId: EntityId | undefined
  scrapReason: string
  remark: string
  details: DetailRow[]
}>({
  warehouseId: undefined,
  scrapReason: '',
  remark: '',
  details: [],
})

const rules: FormRules = {
  warehouseId: [{ required: true, message: '请选择库房', trigger: 'change' }],
  scrapReason: [{ required: true, message: '请输入报废原因', trigger: 'blur' }],
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
        warehouseId: props.formData.warehouseId,
        scrapReason: props.formData.scrapReason,
        remark: props.formData.remark,
        details: (props.formData.details || []).map(d => ({
          itemId: d.itemId,
          binId: d.binId,
          quantity: d.quantity,
        })),
      })
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
  form.details.push({ itemId: undefined as unknown as EntityId, binId: undefined, quantity: 1 })
}

function handleItemChange(_row: DetailRow, _val: EntityId) {
}

async function handleSubmit() {
  await formRef.value?.validate()
  if (form.details.length === 0) {
    ElMessage.warning('请添加报废明细')
    return
  }
  if (form.details.some(detail => !detail.binId)) {
    ElMessage.warning('请选择明细库位')
    return
  }
  submitLoading.value = true
  try {
    const dto: ScrapOrderDto = {
      warehouseId: form.warehouseId!,
      scrapReason: form.scrapReason,
      remark: form.remark,
      details: form.details.map(d => ({ itemId: d.itemId, binId: d.binId!, quantity: d.quantity })),
    }
    if (props.isEdit && props.formData) {
      await updateScrapOrder(props.formData.id, dto)
      ElMessage.success('编辑成功')
    } else {
      await addScrapOrder(dto)
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
  Object.assign(form, { warehouseId: undefined, scrapReason: '', remark: '', details: [] })
  binList.value = []
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

<template>
  <div class="app-container">
    <el-form :inline="true" class="search-form">
      <el-form-item label="存放柜">
        <el-select v-model="selectedCabinetId" placeholder="请选择存放柜" @change="handleCabinetChange">
          <el-option-group v-for="w in warehouseList" :key="w.id" :label="w.warehouseName">
            <el-option v-for="c in w.cabinets" :key="c.id" :label="c.cabinetName" :value="c.id" />
          </el-option-group>
        </el-select>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" :disabled="!selectedCabinetId" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain :icon="Grid" :disabled="!selectedCabinetId" @click="batchDialogVisible = true">批量生成</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="tableData" border>
      <el-table-column prop="binCode" label="库位编码" min-width="150" />
      <el-table-column prop="rowNum" label="行号" min-width="80" />
      <el-table-column prop="colNum" label="列号" min-width="80" />
      <el-table-column prop="binStatus" label="状态" min-width="80">
        <template #default="{ row }">
          <el-tag :type="row.binStatus === 1 ? 'success' : 'danger'">{{ row.binStatus === 1 ? '正常' : row.binStatus === 0 ? '禁用' : '满' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" class-name="table-action-column" fixed="right">
        <template #default="{ row }">
          <TableActionGroup
            :actions="[
              { label: '编辑', type: 'primary', icon: Edit, onClick: () => handleEdit(row) },
              { label: '删除', type: 'danger', icon: Delete, confirmText: '确定删除该库位吗？', onClick: () => handleDelete(row.id) },
            ]"
          />
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑库位' : '新增库位'" width="500px" @close="handleClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="库位编码" prop="binCode">
          <el-input v-model="form.binCode" placeholder="请输入库位编码" :disabled="isEdit" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="行号" prop="rowNum">
              <el-input-number v-model="form.rowNum" :min="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="列号" prop="colNum">
              <el-input-number v-model="form.colNum" :min="1" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="binStatus">
          <el-radio-group v-model="form.binStatus">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
            <el-radio :value="2">满</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleClose">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="batchDialogVisible" title="批量生成库位" width="500px" @close="resetBatchForm">
      <el-form ref="batchFormRef" :model="batchForm" :rules="batchRules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="起始行" prop="startRow">
              <el-input-number v-model="batchForm.startRow" :min="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束行" prop="endRow">
              <el-input-number v-model="batchForm.endRow" :min="1" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="起始列" prop="startCol">
              <el-input-number v-model="batchForm.startCol" :min="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束列" prop="endCol">
              <el-input-number v-model="batchForm.endCol" :min="1" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <el-button @click="batchDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="batchLoading" @click="handleBatchSubmit">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { Plus, Edit, Delete, Grid } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import { getWarehouseList } from '@/api/warehouse/warehouse'
import { getAreaList } from '@/api/warehouse/area'
import { getCabinetList } from '@/api/warehouse/cabinet'
import { getBinList, addBin, updateBin, deleteBin, batchCreateBin } from '@/api/warehouse/bin'
import type { EntityId, WmsBinVo, WmsBinDto, WmsBinBatchDto } from '@/types/warehouse'

interface CabinetOption {
  id: EntityId
  cabinetName: string
}

interface WarehouseOption {
  id: EntityId
  warehouseName: string
  cabinets: CabinetOption[]
}

const warehouseList = ref<WarehouseOption[]>([])
const selectedCabinetId = ref<EntityId>()
const loading = ref(false)
const tableData = ref<WmsBinVo[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const form = reactive<WmsBinDto & { id?: EntityId }>({
  cabinetId: '',
  binCode: '',
  rowNum: 1,
  colNum: 1,
  binStatus: 1
})

const rules: FormRules = {
  binCode: [{ required: true, message: '请输入库位编码', trigger: 'blur' }]
}

const batchDialogVisible = ref(false)
const batchFormRef = ref<FormInstance>()
const batchLoading = ref(false)
const batchForm = reactive<WmsBinBatchDto>({
  cabinetId: '',
  startRow: 1,
  endRow: 1,
  startCol: 1,
  endCol: 1
})

const batchRules: FormRules = {
  startRow: [{ required: true, message: '请输入起始行', trigger: 'blur' }],
  endRow: [{ required: true, message: '请输入结束行', trigger: 'blur' }],
  startCol: [{ required: true, message: '请输入起始列', trigger: 'blur' }],
  endCol: [{ required: true, message: '请输入结束列', trigger: 'blur' }]
}

async function handleCabinetChange(cabinetId: EntityId) {
  loading.value = true
  try {
    const res = await getBinList(cabinetId)
    tableData.value = res.data
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  isEdit.value = false
  Object.assign(form, { id: undefined, cabinetId: selectedCabinetId.value, binCode: '', rowNum: 1, colNum: 1, binStatus: 1 })
  dialogVisible.value = true
}

function handleEdit(row: WmsBinVo) {
  isEdit.value = true
  Object.assign(form, { id: row.id, cabinetId: row.cabinetId, binCode: row.binCode, rowNum: row.rowNum, colNum: row.colNum, binStatus: row.binStatus })
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    const dto: WmsBinDto = { cabinetId: form.cabinetId, binCode: form.binCode, rowNum: form.rowNum, colNum: form.colNum, binStatus: form.binStatus }
    if (isEdit.value && form.id) {
      await updateBin(form.id, dto)
      ElMessage.success('编辑成功')
    } else {
      await addBin(dto)
      ElMessage.success('新增成功')
    }
    handleClose()
    if (selectedCabinetId.value) handleCabinetChange(selectedCabinetId.value)
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(id: EntityId) {
  await deleteBin(id)
  ElMessage.success('删除成功')
  if (selectedCabinetId.value) handleCabinetChange(selectedCabinetId.value)
}

function handleClose() {
  dialogVisible.value = false
  formRef.value?.resetFields()
}

async function handleBatchSubmit() {
  await batchFormRef.value?.validate()
  if (batchForm.startRow > batchForm.endRow || batchForm.startCol > batchForm.endCol) {
    ElMessage.warning('起始值不能大于结束值')
    return
  }
  batchLoading.value = true
  try {
    await batchCreateBin({ ...batchForm, cabinetId: selectedCabinetId.value! })
    ElMessage.success('批量生成成功')
    batchDialogVisible.value = false
    if (selectedCabinetId.value) handleCabinetChange(selectedCabinetId.value)
  } finally {
    batchLoading.value = false
  }
}

function resetBatchForm() {
  batchFormRef.value?.resetFields()
  Object.assign(batchForm, { cabinetId: '', startRow: 1, endRow: 1, startCol: 1, endCol: 1 })
}

onMounted(async () => {
  const whRes = await getWarehouseList()
  const options: WarehouseOption[] = []
  for (const w of whRes.data) {
    const areaRes = await getAreaList(w.id)
    const cabinets: CabinetOption[] = []
    for (const a of areaRes.data) {
      const cabRes = await getCabinetList(a.id)
      cabinets.push(...cabRes.data.map(c => ({ id: c.id, cabinetName: `${a.areaName}-${c.cabinetName}` })))
    }
    options.push({ id: w.id, warehouseName: w.warehouseName, cabinets })
  }
  warehouseList.value = options
})
</script>

<style scoped lang="scss">
.app-container {
  padding: 20px;
}

.search-form {
  margin-bottom: 16px;
}

.mb8 {
  margin-bottom: 8px;
}
</style>

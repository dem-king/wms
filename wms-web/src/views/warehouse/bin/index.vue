<template>
  <div class="app-container list-page">
    <el-form :inline="true" class="search-form">
      <el-form-item label="库房">
        <el-select v-model="selectedWarehouseId" placeholder="请选择库房" @change="handleWarehouseChange">
          <el-option v-for="w in warehouseList" :key="w.id" :label="w.warehouseName" :value="w.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="区域">
        <el-select v-model="selectedAreaId" placeholder="请选择区域" @change="handleAreaChange">
          <el-option v-for="a in areaList" :key="a.id" :label="a.areaName" :value="a.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="存放柜">
        <el-select v-model="selectedCabinetId" placeholder="请选择存放柜" @change="handleCabinetChange">
          <el-option v-for="c in cabinetList" :key="c.id" :label="c.cabinetName" :value="c.id" />
        </el-select>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button v-if="userStore.hasPermission('warehouse:bin:add')" type="primary" plain :icon="Plus" :disabled="!selectedCabinetId" @click="handleAdd">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button v-if="userStore.hasPermission('warehouse:bin:batch')" type="success" plain :icon="Grid" :disabled="!selectedCabinetId" @click="batchDialogVisible = true">批量生成</el-button>
      </el-col>
    </el-row>

    <div class="table-section">
      <el-table v-loading="loading" :data="tableData" border height="100%">
        <el-table-column prop="binCode" label="库位编码" min-width="150" />
        <el-table-column prop="row" label="行号" min-width="80" />
        <el-table-column prop="col" label="列号" min-width="80" />
        <el-table-column prop="status" label="状态" min-width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" class-name="table-action-column" fixed="right" min-width="180">
          <template #default="{ row }">
            <TableActionGroup
              :actions="[
                { label: '编辑', type: 'primary', icon: Edit, permission: 'warehouse:bin:edit', onClick: () => handleEdit(row) },
                { label: '删除', type: 'danger', icon: Delete, permission: 'warehouse:bin:delete', confirmText: '确定删除该库位吗？', onClick: () => handleDelete(row.id) },
              ]"
            />
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.page"
          v-model:page-size="queryParams.size"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          class="pagination"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑库位' : '新增库位'" width="500px" @close="handleClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="库位编码" prop="binCode">
          <el-input v-model="form.binCode" placeholder="请输入库位编码" :disabled="isEdit" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="行号" prop="row">
              <el-input-number v-model="form.row" :min="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="列号" prop="col">
              <el-input-number v-model="form.col" :min="1" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
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
import { useUserStore } from '@/store/modules/user'
import { getWarehouseList } from '@/api/warehouse/warehouse'
import { getAreaList } from '@/api/warehouse/area'
import { getCabinetList } from '@/api/warehouse/cabinet'
import { getBinPage, addBin, updateBin, deleteBin, batchCreateBin } from '@/api/warehouse/bin'
import type { EntityId, WmsWarehouseVo, WmsAreaVo, WmsCabinetVo, WmsBinVo, WmsBinDto, WmsBinBatchDto } from '@/types/warehouse'
import { normalizePageTotal } from '@/utils/pagination'


interface BinPageRow extends WmsBinVo {
  row: number
  col: number
  status: number
}

/** 库房下拉选项列表 */
const warehouseList = ref<WmsWarehouseVo[]>([])
/** 区域下拉选项列表（按需加载） */
const areaList = ref<WmsAreaVo[]>([])
/** 存放柜下拉选项列表（按需加载） */
const cabinetList = ref<WmsCabinetVo[]>([])
/** 当前选中的库房ID */
const selectedWarehouseId = ref<EntityId>()
/** 当前选中的区域ID */
const selectedAreaId = ref<EntityId>()
/** 当前选中的存放柜ID */
const selectedCabinetId = ref<EntityId>()
const userStore = useUserStore()

const loading = ref(false)
const tableData = ref<BinPageRow[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const queryParams = reactive({
  page: 1,
  size: 20,
})

const form = reactive<WmsBinDto & { id?: EntityId }>({
  cabinetId: '',
  binCode: '',
  row: 1,
  col: 1,
  status: 1
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

/**
 * 库房切换事件处理
 * 清空区域和存放柜的选中值及选项列表，清空表格数据，加载新库房下的区域列表
 */
async function handleWarehouseChange(warehouseId: EntityId) {
  // 先清空下级状态，防止旧数据残留
  selectedAreaId.value = undefined
  areaList.value = []
  selectedCabinetId.value = undefined
  cabinetList.value = []
  tableData.value = []
  total.value = 0
  // 按需加载区域列表
  const res = await getAreaList(warehouseId)
  areaList.value = res.data
}

/**
 * 区域切换事件处理
 * 清空存放柜的选中值及选项列表，清空表格数据，加载新区域下的存放柜列表
 */
async function handleAreaChange(areaId: EntityId) {
  // 先清空下级状态，防止旧数据残留
  selectedCabinetId.value = undefined
  cabinetList.value = []
  tableData.value = []
  total.value = 0
  // 按需加载存放柜列表
  const res = await getCabinetList(areaId)
  cabinetList.value = res.data
}

/**
 * 存放柜切换事件处理
 * 重置分页到第1页，查询该存放柜下的库位列表
 */
async function handleCabinetChange(cabinetId: EntityId) {
  queryParams.page = 1
  await handleQuery(cabinetId)
}

function normalizeBinRow(row: WmsBinVo & Record<string, unknown>): BinPageRow {
  return {
    ...row,
    row: typeof row.row === 'number' ? row.row : Number(row.rowNum ?? 0),
    col: typeof row.col === 'number' ? row.col : Number(row.colNum ?? 0),
    status: typeof row.status === 'number' ? row.status : Number(row.binStatus ?? 0),
  }
}

async function handleQuery(cabinetId = selectedCabinetId.value) {
  if (!cabinetId) {
    tableData.value = []
    total.value = 0
    return
  }
  loading.value = true
  try {
    const res = await getBinPage({ cabinetId, ...queryParams })
    tableData.value = res.data.records.map(item => normalizeBinRow(item as WmsBinVo & Record<string, unknown>))
    total.value = normalizePageTotal(res.data.total)
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  isEdit.value = false
  Object.assign(form, { id: undefined, cabinetId: selectedCabinetId.value, binCode: '', row: 1, col: 1, status: 1 })
  dialogVisible.value = true
}

function handleEdit(row: BinPageRow) {
  isEdit.value = true
  Object.assign(form, { id: row.id, cabinetId: row.cabinetId, binCode: row.binCode, row: row.row, col: row.col, status: row.status })
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    const dto: WmsBinDto = { cabinetId: form.cabinetId, binCode: form.binCode, row: form.row, col: form.col, status: form.status }
    if (isEdit.value && form.id) {
      await updateBin(form.id, dto)
      ElMessage.success('编辑成功')
    } else {
      await addBin(dto)
      ElMessage.success('新增成功')
    }
    handleClose()
    await handleQuery()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(id: EntityId) {
  await deleteBin(id)
  ElMessage.success('删除成功')
  if (tableData.value.length === 1 && queryParams.page > 1) {
    queryParams.page -= 1
  }
  await handleQuery()
}

function handleSizeChange(size: number) {
  queryParams.size = size
  queryParams.page = 1
  void handleQuery()
}

function handleCurrentChange(page: number) {
  queryParams.page = page
  void handleQuery()
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
    await handleQuery()
  } finally {
    batchLoading.value = false
  }
}

function resetBatchForm() {
  batchFormRef.value?.resetFields()
  Object.assign(batchForm, { cabinetId: '', startRow: 1, endRow: 1, startCol: 1, endCol: 1 })
}

/**
 * 页面初始化：加载库房列表，并默认选中第一个库房，级联加载区域和存放柜
 */
onMounted(async () => {
  const res = await getWarehouseList()
  warehouseList.value = res.data
  if (warehouseList.value.length > 0) {
    selectedWarehouseId.value = warehouseList.value[0].id
    const areaRes = await getAreaList(selectedWarehouseId.value)
    areaList.value = areaRes.data
    if (areaList.value.length > 0) {
      selectedAreaId.value = areaList.value[0].id
      const cabinetRes = await getCabinetList(selectedAreaId.value)
      cabinetList.value = cabinetRes.data
      if (cabinetList.value.length > 0) {
        selectedCabinetId.value = cabinetList.value[0].id
        await handleQuery(selectedCabinetId.value)
      }
    }
  }
})
</script>

<style scoped lang="scss">
.app-container.list-page {
  padding: 20px;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.table-section {
  flex: 1;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.pagination-container {
  margin-top: 16px;
  flex-shrink: 0;
}

.search-form {
  margin-bottom: 16px;
}

.mb8 {
  margin-bottom: 8px;
}

.pagination {
  justify-content: flex-end;
}
</style>

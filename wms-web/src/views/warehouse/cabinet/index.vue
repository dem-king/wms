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
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" :disabled="!selectedAreaId" @click="handleAdd">新增</el-button>
      </el-col>
    </el-row>

    <div class="table-section">
      <el-table v-loading="loading" :data="tableData" border height="100%">
        <el-table-column prop="cabinetCode" label="存放柜编码" min-width="120" />
        <el-table-column prop="cabinetName" label="存放柜名称" min-width="150" />
        <el-table-column prop="rows" label="行数" min-width="80" />
        <el-table-column prop="cols" label="列数" min-width="80" />
        <el-table-column prop="sortOrder" label="排序" min-width="80" />
        <el-table-column prop="status" label="状态" min-width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" class-name="table-action-column" fixed="right" min-width="180">
          <template #default="{ row }">
            <TableActionGroup
              :actions="[
                { label: '编辑', type: 'primary', icon: Edit, onClick: () => handleEdit(row) },
                { label: '删除', type: 'danger', icon: Delete, confirmText: '确定删除该存放柜吗？', onClick: () => handleDelete(row.id) },
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑存放柜' : '新增存放柜'" width="500px" @close="handleClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="存放柜名称" prop="cabinetName">
          <el-input v-model="form.cabinetName" placeholder="请输入存放柜名称" />
        </el-form-item>
        <el-form-item label="存放柜编码" prop="cabinetCode">
          <el-input v-model="form.cabinetCode" placeholder="请输入存放柜编码" :disabled="isEdit" />
        </el-form-item>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="行数" prop="rows">
              <el-input-number v-model="form.rows" :min="1" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="列数" prop="cols">
              <el-input-number v-model="form.cols" :min="1" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="排序号" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import { getWarehouseList } from '@/api/warehouse/warehouse'
import { getAreaList } from '@/api/warehouse/area'
import { getCabinetPage, addCabinet, updateCabinet, deleteCabinet } from '@/api/warehouse/cabinet'
import type { EntityId, WmsWarehouseVo, WmsAreaVo, WmsCabinetVo, WmsCabinetDto } from '@/types/warehouse'
import { normalizePageTotal } from '@/utils/pagination'

const warehouseList = ref<WmsWarehouseVo[]>([])
const areaList = ref<WmsAreaVo[]>([])
const selectedWarehouseId = ref<EntityId>()
const selectedAreaId = ref<EntityId>()
const loading = ref(false)
const tableData = ref<WmsCabinetVo[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const queryParams = reactive({
  page: 1,
  size: 20,
})

const form = reactive<WmsCabinetDto & { id?: EntityId }>({
  areaId: '',
  cabinetName: '',
  cabinetCode: '',
  rows: 1,
  cols: 1,
  sortOrder: 0,
  status: 1
})

const rules: FormRules = {
  cabinetName: [{ required: true, message: '请输入存放柜名称', trigger: 'blur' }],
  cabinetCode: [{ required: true, message: '请输入存放柜编码', trigger: 'blur' }]
}

async function handleWarehouseChange(warehouseId: EntityId) {
  selectedAreaId.value = undefined
  areaList.value = []
  tableData.value = []
  total.value = 0
  const res = await getAreaList(warehouseId)
  areaList.value = res.data
}

async function handleAreaChange(areaId: EntityId) {
  queryParams.page = 1
  await handleQuery(areaId)
}

async function handleQuery(areaId = selectedAreaId.value) {
  if (!areaId) {
    tableData.value = []
    total.value = 0
    return
  }
  loading.value = true
  try {
    const res = await getCabinetPage({ areaId, ...queryParams })
    tableData.value = res.data.records
    total.value = normalizePageTotal(res.data.total)
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  isEdit.value = false
  Object.assign(form, { id: undefined, areaId: selectedAreaId.value, cabinetName: '', cabinetCode: '', rows: 1, cols: 1, sortOrder: 0, status: 1 })
  dialogVisible.value = true
}

function handleEdit(row: WmsCabinetVo) {
  isEdit.value = true
  Object.assign(form, { id: row.id, areaId: row.areaId, cabinetName: row.cabinetName, cabinetCode: row.cabinetCode, rows: row.rows, cols: row.cols, sortOrder: row.sortOrder, status: row.status })
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    const dto: WmsCabinetDto = { areaId: form.areaId, cabinetName: form.cabinetName, cabinetCode: form.cabinetCode, rows: form.rows, cols: form.cols, sortOrder: form.sortOrder, status: form.status }
    if (isEdit.value && form.id) {
      await updateCabinet(form.id, dto)
      ElMessage.success('编辑成功')
    } else {
      await addCabinet(dto)
      ElMessage.success('新增成功')
    }
    handleClose()
    await handleQuery()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(id: EntityId) {
  await deleteCabinet(id)
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

onMounted(async () => {
  const res = await getWarehouseList()
  warehouseList.value = res.data
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

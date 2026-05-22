<template>
  <div class="app-container">
    <el-row :gutter="16">
      <el-col :span="6">
        <el-card>
          <template #header><span>选择库房</span></template>
          <el-select v-model="selectedWarehouseId" placeholder="请选择库房" style="width: 100%" @change="handleWarehouseChange">
            <el-option v-for="w in warehouseList" :key="w.id" :label="w.warehouseName" :value="w.id" />
          </el-select>
        </el-card>
      </el-col>
      <el-col :span="18">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>区域列表</span>
              <el-button v-if="selectedWarehouseId" type="primary" text :icon="Plus" @click="handleAdd">新增</el-button>
            </div>
          </template>
          <el-table v-loading="loading" :data="tableData" border>
            <el-table-column prop="areaCode" label="区域编码" min-width="120" />
            <el-table-column prop="areaName" label="区域名称" min-width="150" />
            <el-table-column prop="sortOrder" label="排序" min-width="80" />
            <el-table-column prop="status" label="状态" min-width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" class-name="table-action-column" fixed="right">
              <template #default="{ row }">
                <TableActionGroup
                  :actions="[
                    { label: '编辑', type: 'primary', icon: Edit, onClick: () => handleEdit(row) },
                    { label: '删除', type: 'danger', icon: Delete, confirmText: '确定删除该区域吗？', onClick: () => handleDelete(row.id) },
                  ]"
                />
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑区域' : '新增区域'" width="500px" @close="handleClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="区域名称" prop="areaName">
          <el-input v-model="form.areaName" placeholder="请输入区域名称" />
        </el-form-item>
        <el-form-item label="区域编码" prop="areaCode">
          <el-input v-model="form.areaCode" placeholder="请输入区域编码" :disabled="isEdit" />
        </el-form-item>
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
import { getAreaList, addArea, updateArea, deleteArea } from '@/api/warehouse/area'
import type { EntityId, WmsWarehouseVo, WmsAreaVo, WmsAreaDto } from '@/types/warehouse'

const warehouseList = ref<WmsWarehouseVo[]>([])
const selectedWarehouseId = ref<EntityId>()
const loading = ref(false)
const tableData = ref<WmsAreaVo[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const form = reactive<WmsAreaDto & { id?: EntityId }>({
  warehouseId: '',
  areaName: '',
  areaCode: '',
  sortOrder: 0,
  status: 1
})

const rules: FormRules = {
  areaName: [{ required: true, message: '请输入区域名称', trigger: 'blur' }],
  areaCode: [{ required: true, message: '请输入区域编码', trigger: 'blur' }]
}

async function handleWarehouseChange(warehouseId: EntityId) {
  loading.value = true
  try {
    const res = await getAreaList(warehouseId)
    tableData.value = res.data
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  isEdit.value = false
  Object.assign(form, { id: undefined, warehouseId: selectedWarehouseId.value, areaName: '', areaCode: '', sortOrder: 0, status: 1 })
  dialogVisible.value = true
}

function handleEdit(row: WmsAreaVo) {
  isEdit.value = true
  Object.assign(form, { id: row.id, warehouseId: row.warehouseId, areaName: row.areaName, areaCode: row.areaCode, sortOrder: row.sortOrder, status: row.status })
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    const dto: WmsAreaDto = { warehouseId: form.warehouseId, areaName: form.areaName, areaCode: form.areaCode, sortOrder: form.sortOrder, status: form.status }
    if (isEdit.value && form.id) {
      await updateArea(form.id, dto)
      ElMessage.success('编辑成功')
    } else {
      await addArea(dto)
      ElMessage.success('新增成功')
    }
    handleClose()
    if (selectedWarehouseId.value) handleWarehouseChange(selectedWarehouseId.value)
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(id: EntityId) {
  await deleteArea(id)
  ElMessage.success('删除成功')
  if (selectedWarehouseId.value) handleWarehouseChange(selectedWarehouseId.value)
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
.app-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>

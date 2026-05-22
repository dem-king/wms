<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="tableData" border>
      <el-table-column prop="warehouseName" label="名称" min-width="150" />
      <el-table-column prop="warehouseCode" label="编码" min-width="120" />
      <el-table-column prop="address" label="地址" min-width="200" show-overflow-tooltip />
      <el-table-column prop="manager" label="负责人" min-width="100" />
      <el-table-column prop="area" label="面积(㎡)" min-width="100" />
      <el-table-column prop="status" label="状态" min-width="80">
        <template #default="{ row }">
          <el-switch :model-value="row.status === 1" @change="val => handleStatusChange(row, Boolean(val))" />
        </template>
      </el-table-column>
      <el-table-column label="操作" class-name="table-action-column" fixed="right">
        <template #default="{ row }">
          <TableActionGroup
            :actions="[
              { label: '可视化', type: 'success', icon: DataAnalysis, onClick: () => handleVisualize(row) },
              { label: '编辑', type: 'primary', icon: Edit, onClick: () => handleEdit(row) },
              { label: '删除', type: 'danger', icon: Delete, confirmText: '确定删除该库房吗？', onClick: () => handleDelete(row.id) },
            ]"
          />
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑库房' : '新增库房'" width="600px" @close="handleClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="库房名称" prop="warehouseName">
          <el-input v-model="form.warehouseName" placeholder="请输入库房名称" />
        </el-form-item>
        <el-form-item v-if="isEdit" label="库房编码" prop="warehouseCode">
          <el-input v-model="form.warehouseCode" placeholder="请输入库房编码" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="地址" prop="address">
          <el-input v-model="form.address" type="textarea" :rows="2" placeholder="请输入地址" />
        </el-form-item>
        <el-form-item label="负责人" prop="manager">
          <el-input v-model="form.manager" placeholder="请输入负责人" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="面积(㎡)" prop="area">
          <el-input-number v-model="form.area" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
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
import { computed, ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { Plus, Edit, Delete, DataAnalysis } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import { getWarehouseList, addWarehouse, updateWarehouse, deleteWarehouse } from '@/api/warehouse/warehouse'
import type { EntityId, WmsWarehouseVo, WmsWarehouseDto } from '@/types/warehouse'

const router = useRouter()
const loading = ref(false)
const tableData = ref<WmsWarehouseVo[]>([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const form = reactive<WmsWarehouseDto & { id?: EntityId }>({
  warehouseName: '',
  warehouseCode: '',
  address: '',
  manager: '',
  phone: '',
  area: 0,
  status: 1,
  remark: '',
})

const rules = computed<FormRules>(() => ({
  warehouseName: [{ required: true, message: '请输入库房名称', trigger: 'blur' }],
  ...(isEdit.value ? { warehouseCode: [{ required: true, message: '请输入库房编码', trigger: 'blur' }] } : {}),
}))

async function handleQuery() {
  loading.value = true
  try {
    const res = await getWarehouseList()
    tableData.value = res.data
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  isEdit.value = false
  Object.assign(form, { id: undefined, warehouseName: '', warehouseCode: '', address: '', manager: '', phone: '', area: 0, status: 1, remark: '' })
  dialogVisible.value = true
}

function handleEdit(row: WmsWarehouseVo) {
  isEdit.value = true
  Object.assign(form, { id: row.id, warehouseName: row.warehouseName, warehouseCode: row.warehouseCode, address: row.address, manager: row.manager, phone: row.phone ?? '', area: row.area, status: row.status, remark: row.remark ?? '' })
  dialogVisible.value = true
}

function handleVisualize(row: WmsWarehouseVo) {
  router.push({
    path: '/warehouse/visual',
    query: {
      warehouseId: String(row.id),
    },
  })
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    const dto: WmsWarehouseDto = {
      warehouseName: form.warehouseName,
      address: form.address,
      manager: form.manager,
      phone: form.phone,
      area: form.area,
      status: form.status,
      remark: form.remark,
      ...(isEdit.value ? { warehouseCode: form.warehouseCode } : {}),
    }
    if (isEdit.value && form.id) {
      await updateWarehouse(form.id, dto)
      ElMessage.success('编辑成功')
    } else {
      await addWarehouse(dto)
      ElMessage.success('新增成功')
    }
    handleClose()
    handleQuery()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(id: EntityId) {
  await deleteWarehouse(id)
  ElMessage.success('删除成功')
  handleQuery()
}

async function handleStatusChange(row: WmsWarehouseVo, val: boolean) {
  const newStatus = val ? 1 : 0
  await updateWarehouse(row.id, { warehouseName: row.warehouseName, warehouseCode: row.warehouseCode, address: row.address, manager: row.manager, phone: row.phone, area: row.area, status: newStatus, remark: row.remark })
  ElMessage.success('状态修改成功')
  handleQuery()
}

function handleClose() {
  dialogVisible.value = false
  formRef.value?.resetFields()
}

onMounted(() => {
  handleQuery()
})
</script>

<style scoped lang="scss">
.app-container {
  padding: 20px;
}

.mb8 {
  margin-bottom: 8px;
}
</style>

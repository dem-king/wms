<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="tableData" border>
      <el-table-column prop="machineName" label="机器名称" min-width="160" />
      <el-table-column prop="machineCode" label="机器编号" min-width="140" />
      <el-table-column prop="itemName" label="备件物品名称" min-width="160" />
      <el-table-column prop="quantity" label="数量" min-width="100" />
      <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
      <el-table-column label="操作" class-name="table-action-column" fixed="right">
        <template #default="{ row }">
          <TableActionGroup
            :actions="[
              { label: '编辑', type: 'primary', icon: Edit, onClick: () => handleEdit(row) },
              { label: '删除', type: 'danger', icon: Delete, confirmText: '确定删除该关联记录吗？', onClick: () => handleDelete(row.id) },
            ]"
          />
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="queryParams.page"
      v-model:page-size="queryParams.size"
      :total="total"
      :page-sizes="[10, 20, 50, 100]"
      layout="total, sizes, prev, pager, next, jumper"
      class="pagination"
      @size-change="handleQuery"
      @current-change="handleQuery"
    />

    <el-dialog v-model="formVisible" :title="isEdit ? '编辑机器-备件关联' : '新增机器-备件关联'" width="600px" @close="handleClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="机器名称" prop="machineName">
          <el-input v-model="form.machineName" placeholder="请输入机器名称" />
        </el-form-item>
        <el-form-item label="机器编号" prop="machineCode">
          <el-input v-model="form.machineCode" placeholder="请输入机器编号" />
        </el-form-item>
        <el-form-item label="备件物品" prop="itemId">
          <el-select v-model="form.itemId" placeholder="请选择备件物品" filterable style="width: 100%">
            <el-option v-for="item in itemList" :key="item.id" :label="`${item.itemCode} - ${item.itemName}`" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量" prop="quantity">
          <el-input-number v-model="form.quantity" :min="1" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import { getMachineSpareList, addMachineSpare, updateMachineSpare, deleteMachineSpare } from '@/api/item/machineSpare'
import { getItemList } from '@/api/item/item'
import type { EntityId, MachineSpareVo, MachineSpareDto } from '@/types/business'
import type { WmsItemVo } from '@/types/item'

const loading = ref(false)
const tableData = ref<MachineSpareVo[]>([])
const total = ref(0)
const itemList = ref<WmsItemVo[]>([])

const queryParams = reactive({
  page: 1,
  size: 20,
})

const formVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const editingId = ref<EntityId | null>(null)

const form = reactive<{
  machineName: string
  machineCode: string
  itemId: EntityId | undefined
  quantity: number
  remark: string
}>({
  machineName: '',
  machineCode: '',
  itemId: undefined,
  quantity: 1,
  remark: '',
})

const rules: FormRules = {
  machineName: [{ required: true, message: '请输入机器名称', trigger: 'blur' }],
  machineCode: [{ required: true, message: '请输入机器编号', trigger: 'blur' }],
  itemId: [{ required: true, message: '请选择备件物品', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }],
}

async function handleQuery() {
  loading.value = true
  try {
    const res = await getMachineSpareList(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function handleAdd() {
  isEdit.value = false
  editingId.value = null
  Object.assign(form, { machineName: '', machineCode: '', itemId: undefined, quantity: 1, remark: '' })
  const iRes = await getItemList({ page: 1, size: 1000, status: 1 })
  itemList.value = iRes.data.records
  formVisible.value = true
}

async function handleEdit(row: MachineSpareVo) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(form, {
    machineName: row.machineName,
    machineCode: row.machineCode,
    itemId: row.itemId,
    quantity: row.quantity,
    remark: row.remark,
  })
  const iRes = await getItemList({ page: 1, size: 1000, status: 1 })
  itemList.value = iRes.data.records
  formVisible.value = true
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    const dto: MachineSpareDto = {
      machineName: form.machineName,
      machineCode: form.machineCode,
      itemId: form.itemId!,
      quantity: form.quantity,
      remark: form.remark,
    }
    if (isEdit.value && editingId.value) {
      await updateMachineSpare(editingId.value, dto)
      ElMessage.success('编辑成功')
    } else {
      await addMachineSpare(dto)
      ElMessage.success('新增成功')
    }
    handleClose()
    handleQuery()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(id: EntityId) {
  await ElMessageBox.confirm('确定删除该关联记录吗？', '提示', { type: 'warning' })
  await deleteMachineSpare(id)
  ElMessage.success('删除成功')
  handleQuery()
}

function handleClose() {
  formVisible.value = false
  formRef.value?.resetFields()
  Object.assign(form, { machineName: '', machineCode: '', itemId: undefined, quantity: 1, remark: '' })
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

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>

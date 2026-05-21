<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="tableData" border>
      <el-table-column prop="configName" label="配置名称" min-width="160" />
      <el-table-column prop="bizType" label="业务类型" min-width="120">
        <template #default="{ row }">
          <el-tag>{{ bizTypeMap[row.bizType] || row.bizType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="是否启用" min-width="100">
        <template #default="{ row }">
          <el-tag :type="row.enabled ? 'success' : 'info'">{{ row.enabled ? '启用' : '禁用' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="是否免审" min-width="100">
        <template #default="{ row }">
          <el-tag :type="row.autoApproved ? 'warning' : 'info'">{{ row.autoApproved ? '免审' : '需审批' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="160" />
      <el-table-column label="操作" class-name="table-action-column" fixed="right">
        <template #default="{ row }">
          <TableActionGroup
            :actions="[
              { label: '编辑', type: 'primary', icon: Edit, onClick: () => handleEdit(row) },
              { label: '删除', type: 'danger', icon: Delete, confirmText: '确定删除该审批配置吗？', onClick: () => handleDelete(row.id) },
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

    <el-dialog v-model="formVisible" :title="isEdit ? '编辑审批配置' : '新增审批配置'" width="700px" @close="handleClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="配置名称" prop="configName">
          <el-input v-model="form.configName" placeholder="请输入配置名称" />
        </el-form-item>
        <el-form-item label="业务类型" prop="bizType">
          <el-select v-model="form.bizType" placeholder="请选择业务类型" style="width: 100%">
            <el-option v-for="bt in bizTypeOptions" :key="bt.value" :label="bt.label" :value="bt.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否启用" prop="enabled">
          <el-switch v-model="form.enabled" />
        </el-form-item>
        <el-form-item label="是否免审" prop="autoApproved">
          <el-switch v-model="form.autoApproved" />
        </el-form-item>
      </el-form>

      <el-divider content-position="left">审批节点</el-divider>
      <div class="detail-toolbar">
        <el-button type="primary" plain :icon="Plus" @click="addNode">新增节点</el-button>
      </div>
      <el-table :data="form.nodes" border>
        <el-table-column label="节点顺序" min-width="100">
          <template #default="{ row }">
            <el-input-number v-model="row.nodeOrder" :min="1" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="节点名称" min-width="150">
          <template #default="{ row }">
            <el-input v-model="row.nodeName" placeholder="请输入节点名称" size="small" />
          </template>
        </el-table-column>
        <el-table-column label="审批人类型" min-width="140">
          <template #default="{ row }">
            <el-select v-model="row.assigneeType" placeholder="请选择" size="small">
              <el-option label="指定角色" :value="1" />
              <el-option label="指定用户" :value="2" />
              <el-option label="库房管理员" :value="3" />
            </el-select>
          </template>
        </el-table-column>
        <el-table-column label="审批人ID" min-width="120">
          <template #default="{ row }">
            <el-input-number v-model="row.assigneeId" :min="1" size="small" v-if="row.assigneeType !== 3" />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" class-name="table-action-column" fixed="right">
          <template #default="{ $index }">
            <TableActionGroup
              :actions="[
                { label: '删除', type: 'danger', icon: Delete, onClick: () => { form.nodes.splice($index, 1) } },
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
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import { getApprovalConfigs, addApprovalConfig, updateApprovalConfig, deleteApprovalConfig } from '@/api/approval'
import type { ApprovalConfigVo, ApprovalConfigDto, ApprovalNodeVo, BizType } from '@/types/business'

const bizTypeOptions = [
  { label: '入库', value: 'INBOUND' },
  { label: '出库', value: 'OUTBOUND' },
  { label: '归还', value: 'RETURN' },
  { label: '报废', value: 'SCRAP' },
  { label: '调拨', value: 'TRANSFER' },
]

const bizTypeMap: Record<string, string> = {
  INBOUND: '入库',
  OUTBOUND: '出库',
  RETURN: '归还',
  SCRAP: '报废',
  TRANSFER: '调拨',
}

const loading = ref(false)
const tableData = ref<ApprovalConfigVo[]>([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 20,
})

const formVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const editingId = ref<number | null>(null)

const form = reactive<{
  configName: string
  bizType: BizType | ''
  enabled: boolean
  autoApproved: boolean
  nodes: ApprovalNodeVo[]
}>({
  configName: '',
  bizType: '',
  enabled: true,
  autoApproved: false,
  nodes: [],
})

const rules: FormRules = {
  configName: [{ required: true, message: '请输入配置名称', trigger: 'blur' }],
  bizType: [{ required: true, message: '请选择业务类型', trigger: 'change' }],
}

async function handleQuery() {
  loading.value = true
  try {
    const res = await getApprovalConfigs(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  isEdit.value = false
  editingId.value = null
  Object.assign(form, { configName: '', bizType: '', enabled: true, autoApproved: false, nodes: [] })
  formVisible.value = true
}

function handleEdit(row: ApprovalConfigVo) {
  isEdit.value = true
  editingId.value = row.id
  Object.assign(form, {
    configName: row.configName,
    bizType: row.bizType,
    enabled: row.enabled,
    autoApproved: row.autoApproved,
    nodes: (row.nodes || []).map(n => ({ ...n })),
  })
  formVisible.value = true
}

function addNode() {
  form.nodes.push({ id: 0, configId: 0, nodeOrder: form.nodes.length + 1, nodeName: '', assigneeType: 2, assigneeId: 0 })
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    const dto: ApprovalConfigDto = {
      configName: form.configName,
      bizType: form.bizType as BizType,
      enabled: form.enabled,
      autoApproved: form.autoApproved,
      nodes: form.nodes,
    }
    if (isEdit.value && editingId.value) {
      await updateApprovalConfig(editingId.value, dto)
      ElMessage.success('编辑成功')
    } else {
      await addApprovalConfig(dto)
      ElMessage.success('新增成功')
    }
    handleClose()
    handleQuery()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('确定删除该审批配置吗？', '提示', { type: 'warning' })
  await deleteApprovalConfig(id)
  ElMessage.success('删除成功')
  handleQuery()
}

function handleClose() {
  formVisible.value = false
  formRef.value?.resetFields()
  Object.assign(form, { configName: '', bizType: '', enabled: true, autoApproved: false, nodes: [] })
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

.detail-toolbar {
  display: flex;
  gap: 12px;
  align-items: center;
  margin-bottom: 8px;
}
</style>

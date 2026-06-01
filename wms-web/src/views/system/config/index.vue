<template>
  <div class="app-container list-page">
    <el-form :model="queryParams" :inline="true" class="search-form">
      <el-form-item label="配置键">
        <el-input v-model="queryParams.configKey" placeholder="请输入配置键" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="配置组">
        <el-input v-model="queryParams.configGroup" placeholder="请输入配置组" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button v-if="false" type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
    </el-row>

    <div class="table-section">
      <el-table v-loading="loading" :data="tableData" border height="100%">
        <el-table-column prop="configName" label="配置名称" min-width="120" />
        <el-table-column prop="configKey" label="配置键" min-width="150" />
        <el-table-column prop="configValue" label="配置值" min-width="150" show-overflow-tooltip />
        <el-table-column prop="configGroup" label="配置组" min-width="100" />
        <el-table-column prop="description" label="描述" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" class-name="table-action-column" fixed="right" min-width="180">
          <template #default="{ row }">
            <TableActionGroup
              :actions="[
                { label: '编辑', type: 'primary', icon: Edit, permission: 'system:config:edit', onClick: () => handleEdit(row) },
                { label: '删除', type: 'danger', icon: Delete, visible: false, confirmText: '确定删除该配置吗？', onClick: () => handleDelete(row.id) },
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
          @size-change="handleQuery"
          @current-change="handleQuery"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑配置' : '新增配置'" width="500px" @close="handleClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="配置名称" prop="configName">
          <el-input v-model="form.configName" placeholder="请输入配置名称" />
        </el-form-item>
        <el-form-item label="配置键" prop="configKey">
          <el-input v-model="form.configKey" placeholder="请输入配置键" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="配置值" prop="configValue">
          <el-input v-model="form.configValue" type="textarea" :rows="3" placeholder="请输入配置值" />
        </el-form-item>
        <el-form-item label="配置组" prop="configGroup">
          <el-input v-model="form.configGroup" placeholder="请输入配置组" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="请输入描述" />
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
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import { getConfigList, addConfig, updateConfig, deleteConfig } from '@/api/system/config'
import type { EntityId, SysConfigVo } from '@/types/system'
import { normalizePageTotal } from '@/utils/pagination'

const loading = ref(false)
const tableData = ref<SysConfigVo[]>([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  size: 20,
  configKey: '',
  configGroup: ''
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const form = reactive({
  id: undefined as EntityId | undefined,
  configName: '',
  configKey: '',
  configValue: '',
  configGroup: '',
  description: ''
})

const rules: FormRules = {
  configName: [{ required: true, message: '请输入配置名称', trigger: 'blur' }],
  configKey: [{ required: true, message: '请输入配置键', trigger: 'blur' }],
  configValue: [{ required: true, message: '请输入配置值', trigger: 'blur' }]
}

async function handleQuery() {
  loading.value = true
  try {
    const res = await getConfigList(queryParams)
    const configs = Array.isArray(res.data) ? res.data : res.data.records
    tableData.value = configs
    total.value = Array.isArray(res.data) ? configs.length : normalizePageTotal(res.data.total)
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryParams.configKey = ''
  queryParams.configGroup = ''
  queryParams.page = 1
  handleQuery()
}

function handleAdd() {
  isEdit.value = false
  dialogVisible.value = true
}

function handleEdit(row: SysConfigVo) {
  isEdit.value = true
  Object.assign(form, { id: row.id, configName: row.configName, configKey: row.configKey, configValue: row.configValue, configGroup: row.configGroup, description: row.description })
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (isEdit.value && form.id) {
      await updateConfig(form.id, { configName: form.configName, configKey: form.configKey, configValue: form.configValue, configGroup: form.configGroup, description: form.description })
      ElMessage.success('编辑成功')
    } else {
      await addConfig({ configName: form.configName, configKey: form.configKey, configValue: form.configValue, configGroup: form.configGroup, description: form.description })
      ElMessage.success('新增成功')
    }
    handleClose()
    handleQuery()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(id: EntityId) {
  await deleteConfig(id)
  ElMessage.success('删除成功')
  handleQuery()
}

function handleClose() {
  dialogVisible.value = false
  formRef.value?.resetFields()
  Object.assign(form, { id: undefined, configName: '', configKey: '', configValue: '', configGroup: '', description: '' })
}

onMounted(() => {
  handleQuery()
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

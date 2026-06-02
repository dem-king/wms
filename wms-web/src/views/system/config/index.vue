<template>
  <div class="app-container list-page">
    <el-tabs v-model="activeGroup" class="config-tabs" @tab-change="handleGroupChange">
      <el-tab-pane v-for="group in configGroups" :key="group.value" :label="group.label" :name="group.value" />
    </el-tabs>

    <el-form :model="queryParams" :inline="true" class="search-form">
      <el-form-item label="配置键">
        <el-input v-model="queryParams.configKey" placeholder="请输入配置键" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button v-if="userStore.hasPermission('system:config:add')" type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
    </el-row>

    <div class="table-section">
      <el-table v-loading="loading" :data="tableData" border height="100%">
        <el-table-column prop="configName" label="配置名称" min-width="120" />
        <el-table-column prop="configKey" label="配置键" min-width="180" />
        <el-table-column prop="configValue" label="配置值" min-width="150" show-overflow-tooltip />
        <el-table-column v-if="!activeGroup" prop="configGroup" label="配置组" min-width="100" />
        <el-table-column prop="configDesc" label="描述" min-width="150" show-overflow-tooltip />
        <el-table-column label="操作" class-name="table-action-column" fixed="right" min-width="180">
          <template #default="{ row }">
            <TableActionGroup
              :actions="[
                { label: '编辑', type: 'primary', icon: Edit, permission: 'system:config:edit', onClick: () => handleEdit(row) },
                { label: '删除', type: 'danger', icon: Delete, permission: 'system:config:delete', confirmText: '确定删除该配置吗？', onClick: () => handleDelete(row.id) },
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
          <el-switch
            v-if="valueEditorType === 'switch'"
            v-model="switchValue"
            active-value="true"
            inactive-value="false"
          />
          <el-input-number
            v-else-if="valueEditorType === 'number'"
            v-model="numberValue"
            :min="0"
            controls-position="right"
            style="width: 100%"
          />
          <el-input
            v-else-if="valueEditorType === 'prefix'"
            v-model="form.configValue"
            placeholder="请输入配置值"
            maxlength="10"
          />
          <el-input
            v-else
            v-model="form.configValue"
            type="textarea"
            :rows="3"
            placeholder="请输入配置值"
          />
        </el-form-item>
        <el-form-item label="配置组" prop="configGroup">
          <el-select v-model="form.configGroup" placeholder="请选择配置组" clearable>
            <el-option v-for="group in configGroups.filter(g => g.value)" :key="group.value" :label="group.label" :value="group.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="configDesc">
          <el-input v-model="form.configDesc" type="textarea" :rows="2" placeholder="请输入描述" />
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
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import { useUserStore } from '@/store/modules/user'
import { getConfigPage, addConfig, updateConfig, deleteConfig } from '@/api/system/config'
import type { EntityId, SysConfigVo } from '@/types/system'
import { normalizePageTotal } from '@/utils/pagination'

const userStore = useUserStore()

const configGroups = [
  { label: '全部', value: '' },
  { label: '安全策略', value: 'security' },
  { label: '认证策略', value: 'auth' },
  { label: '系统参数', value: 'system' },
  { label: '文件存储', value: 'storage' },
  { label: '业务参数', value: 'business' },
  { label: '标签打印', value: 'label' },
  { label: '报表统计', value: 'report' },
  { label: '系统信息', value: 'info' },
]

const activeGroup = ref('')
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
  configDesc: ''
})

const rules: FormRules = {
  configName: [{ required: true, message: '请输入配置名称', trigger: 'blur' }],
  configKey: [{ required: true, message: '请输入配置键', trigger: 'blur' }],
  configValue: [{ required: true, message: '请输入配置值', trigger: 'blur' }]
}

const switchValue = computed({
  get: () => form.configValue,
  set: (val: string | number | boolean) => { form.configValue = String(val) }
})

const numberValue = computed({
  get: () => Number(form.configValue) || 0,
  set: (val: number) => { form.configValue = String(val) }
})

function getValueEditorType(configKey: string): 'switch' | 'number' | 'prefix' | 'text' {
  if (/\.enabled$|\.switch$|captcha\.enabled/.test(configKey)) return 'switch'
  if (/\.limit$|\.max$|\.size$|\.days$|\.timeout$|\.threshold$|\.expiry/.test(configKey)) return 'number'
  if (/\.prefix$/.test(configKey)) return 'prefix'
  return 'text'
}

const valueEditorType = computed(() => getValueEditorType(form.configKey))

async function handleQuery() {
  loading.value = true
  try {
    const res = await getConfigPage(queryParams)
    const configs = Array.isArray(res.data) ? res.data : res.data.records
    tableData.value = configs
    total.value = Array.isArray(res.data) ? configs.length : normalizePageTotal(res.data.total)
  } finally {
    loading.value = false
  }
}

function handleGroupChange() {
  queryParams.configGroup = activeGroup.value
  queryParams.page = 1
  handleQuery()
}

function handleReset() {
  queryParams.configKey = ''
  queryParams.page = 1
  handleQuery()
}

function handleAdd() {
  isEdit.value = false
  Object.assign(form, { id: undefined, configName: '', configKey: '', configValue: '', configGroup: activeGroup.value || '', configDesc: '' })
  dialogVisible.value = true
}

function handleEdit(row: SysConfigVo) {
  isEdit.value = true
  Object.assign(form, { id: row.id, configName: row.configName, configKey: row.configKey, configValue: row.configValue, configGroup: row.configGroup, configDesc: row.configDesc })
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    const dto = {
      configKey: form.configKey,
      configValue: form.configValue,
      configName: form.configName,
      configGroup: form.configGroup,
      configDesc: form.configDesc
    }
    if (isEdit.value && form.id) {
      await updateConfig(form.id, dto)
      ElMessage.success('编辑成功')
    } else {
      await addConfig(dto)
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
  if (tableData.value.length === 1 && queryParams.page > 1) {
    queryParams.page -= 1
  }
  handleQuery()
}

function handleClose() {
  dialogVisible.value = false
  formRef.value?.resetFields()
  Object.assign(form, { id: undefined, configName: '', configKey: '', configValue: '', configGroup: '', configDesc: '' })
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

.config-tabs {
  margin-bottom: 16px;
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

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Delete, Edit, Plus, Refresh, Search } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import { useUserStore } from '@/store/modules/user'
import {
  addApprovalConfig,
  deleteApprovalConfig,
  getApprovalConfig,
  getApprovalConfigs,
  updateApprovalConfig,
} from '@/api/approval'
import { getAllRoles } from '@/api/system/role'
import { getUserByUsernameExact } from '@/api/system/user'
import {
  APPROVER_TYPE,
  APPROVER_TYPE_OPTIONS,
  BIZ_TYPE_OPTIONS,
  DEFAULT_TIMEOUT_HOURS,
  getBizTypeLabel,
  SWITCH_STATUS,
  TIMEOUT_ACTION,
  TIMEOUT_ACTION_OPTIONS,
} from '@/constants/approval'
import type {
  ApprovalConfigDto,
  ApprovalConfigQuery,
  ApprovalConfigVo,
  ApprovalNodeDto,
  ApprovalNodeVo,
  BizType,
} from '@/types/business'
import type { SysRoleVo } from '@/types/system'

interface ApprovalNodeForm extends ApprovalNodeDto {
  approverUsername?: string
  approverName?: string
  userCheckStatus?: 'idle' | 'checking' | 'valid' | 'invalid'
  userCheckMessage?: string
  userCheckSeq?: number
  userCheckTimer?: number
}

interface ConfigFormModel {
  configName: string
  bizType?: BizType
  enabled: number
  autoApprove: number
  remark: string
  timeoutHours: number
  timeoutAction: number
  nodes: ApprovalNodeForm[]
}

const userStore = useUserStore()

const loading = ref(false)
const tableData = ref<ApprovalConfigVo[]>([])
const roleOptions = ref<SysRoleVo[]>([])
const total = ref(0)
const formVisible = ref(false)
const submitLoading = ref(false)
const isEdit = ref(false)
const editingId = ref<number | null>(null)
const formRef = ref<FormInstance>()

const queryParams = reactive<ApprovalConfigQuery>({
  page: 1,
  size: 20,
  bizType: undefined,
})

const form = reactive<ConfigFormModel>(createDefaultForm())

const showNodes = computed(() => form.autoApprove !== SWITCH_STATUS.ENABLED)

const rules: FormRules<ConfigFormModel> = {
  configName: [{ required: true, message: '请输入配置名称', trigger: 'blur' }],
  bizType: [{ required: true, message: '请选择业务类型', trigger: 'change' }],
  timeoutHours: [{ required: true, message: '请输入超时阈值', trigger: 'blur' }],
  timeoutAction: [{ required: true, message: '请选择超时处理方式', trigger: 'change' }],
}

function createDefaultNode(stepOrder: number): ApprovalNodeForm {
  return {
    stepOrder,
    nodeName: '',
    approverType: APPROVER_TYPE.USER,
    approverId: undefined,
    approverUsername: '',
    approverName: '',
    userCheckStatus: 'idle',
    userCheckMessage: '',
    userCheckSeq: 0,
    userCheckTimer: undefined,
  }
}

function createDefaultForm(): ConfigFormModel {
  return {
    configName: '',
    bizType: undefined,
    enabled: SWITCH_STATUS.ENABLED,
    autoApprove: SWITCH_STATUS.DISABLED,
    remark: '',
    timeoutHours: DEFAULT_TIMEOUT_HOURS,
    timeoutAction: TIMEOUT_ACTION.REMIND,
    nodes: [createDefaultNode(1)],
  }
}

function resetFormModel() {
  Object.assign(form, createDefaultForm())
}

function mapNodes(nodes?: ApprovalNodeVo[]): ApprovalNodeForm[] {
  if (!nodes?.length) {
    return [createDefaultNode(1)]
  }
  return nodes.map((node) => ({
    stepOrder: node.stepOrder,
    nodeName: node.nodeName,
    approverType: node.approverType,
    approverId: node.approverId,
    approverUsername: node.approverUsername || '',
    approverName: node.approverName || '',
    userCheckStatus: node.approverType === APPROVER_TYPE.USER ? 'valid' : 'idle',
    userCheckMessage: '',
    userCheckSeq: 0,
    userCheckTimer: undefined,
  }))
}

async function loadRoles() {
  const res = await getAllRoles()
  roleOptions.value = res.data || []
}

function resetUserCheck(node: ApprovalNodeForm) {
  if (node.userCheckTimer) {
    window.clearTimeout(node.userCheckTimer)
    node.userCheckTimer = undefined
  }
  node.userCheckStatus = 'idle'
  node.userCheckMessage = ''
  node.approverName = ''
}

function handleApproverTypeChange(node: ApprovalNodeForm) {
  node.approverId = undefined
  node.approverUsername = ''
  resetUserCheck(node)
}

function scheduleUsernameCheck(node: ApprovalNodeForm) {
  node.approverId = undefined
  resetUserCheck(node)
  node.userCheckTimer = window.setTimeout(() => {
    node.userCheckTimer = undefined
    validateUsername(node)
  }, 300)
}

async function validateUsername(node: ApprovalNodeForm) {
  if (node.userCheckTimer) {
    window.clearTimeout(node.userCheckTimer)
    node.userCheckTimer = undefined
  }
  if (node.approverType !== APPROVER_TYPE.USER) {
    return
  }
  const username = node.approverUsername?.trim()
  if (!username) {
    node.userCheckStatus = 'invalid'
    node.userCheckMessage = '请输入用户名'
    return
  }
  const seq = (node.userCheckSeq || 0) + 1
  node.userCheckSeq = seq
  node.userCheckStatus = 'checking'
  node.userCheckMessage = '正在校验'
  let res
  try {
    res = await getUserByUsernameExact(username)
  } catch {
    if (node.userCheckSeq !== seq) {
      return
    }
    node.userCheckStatus = 'invalid'
    node.userCheckMessage = '用户校验失败'
    return
  }
  if (node.userCheckSeq !== seq) {
    return
  }
  const user = res.data
  if (!user) {
    node.userCheckStatus = 'invalid'
    node.userCheckMessage = '用户不存在'
    return
  }
  if (user.status !== SWITCH_STATUS.ENABLED) {
    node.userCheckStatus = 'invalid'
    node.userCheckMessage = '用户已禁用'
    return
  }
  if (!user.hasApprovalPermission) {
    node.userCheckStatus = 'invalid'
    node.userCheckMessage = '用户缺少审批权限'
    return
  }
  node.approverId = Number(user.id)
  node.approverUsername = user.username
  node.approverName = user.realName || user.username
  node.userCheckStatus = 'valid'
  node.userCheckMessage = node.approverName ? `已匹配：${node.approverName}` : '已匹配'
}

async function loadTable() {
  loading.value = true
  try {
    const res = await getApprovalConfigs({ ...queryParams })
    tableData.value = res.data.records || []
    total.value = Number(res.data.total || 0)
  } finally {
    loading.value = false
  }
}

function handleQuery() {
  queryParams.page = 1
  loadTable()
}

function handleReset() {
  queryParams.page = 1
  queryParams.size = 20
  queryParams.bizType = undefined
  loadTable()
}

function handleAdd() {
  isEdit.value = false
  editingId.value = null
  resetFormModel()
  formVisible.value = true
}

async function handleEdit(row: ApprovalConfigVo) {
  isEdit.value = true
  editingId.value = row.id
  const res = await getApprovalConfig(row.id)
  const detail = res.data
  Object.assign(form, {
    configName: detail.configName,
    bizType: detail.bizType,
    enabled: detail.enabled,
    autoApprove: detail.autoApprove,
    remark: detail.remark || '',
    timeoutHours: detail.timeoutHours || DEFAULT_TIMEOUT_HOURS,
    timeoutAction: detail.timeoutAction || TIMEOUT_ACTION.REMIND,
    nodes: mapNodes(detail.nodes),
  })
  formVisible.value = true
}

function addNode() {
  form.nodes.push(createDefaultNode(form.nodes.length + 1))
}

function removeNode(index: number) {
  form.nodes.splice(index, 1)
  form.nodes.forEach((node, nodeIndex) => {
    node.stepOrder = nodeIndex + 1
  })
}

async function ensureUserNodesValid(nodes: ApprovalNodeForm[]) {
  for (const node of nodes) {
    if (node.approverType === APPROVER_TYPE.USER) {
      await validateUsername(node)
      if (node.userCheckStatus !== 'valid' || !node.approverId) {
        throw new Error(node.userCheckMessage || '请指定有效审批用户')
      }
    }
  }
}

function normalizeNodes(nodes: ApprovalNodeForm[]): ApprovalNodeDto[] {
  return [...nodes]
    .map((node) => ({
      stepOrder: node.stepOrder,
      nodeName: node.nodeName.trim(),
      approverType: node.approverType,
      approverId: node.approverId,
    }))
    .sort((a, b) => a.stepOrder - b.stepOrder)
}

function validateNodes(nodes: ApprovalNodeDto[]) {
  if (form.autoApprove === SWITCH_STATUS.ENABLED) {
    return
  }
  if (!nodes.length) {
    throw new Error('请至少配置一个审批节点')
  }
  const invalidNode = nodes.find((node) => !node.nodeName || !node.approverId)
  if (invalidNode) {
    throw new Error('请完善审批节点名称和审批人配置')
  }
}

async function handleSubmit() {
  await formRef.value?.validate()
  await ensureUserNodesValid(form.nodes)
  const nodes = normalizeNodes(form.nodes)
  validateNodes(nodes)
  submitLoading.value = true
  try {
    const dto: ApprovalConfigDto = {
      configName: form.configName.trim(),
      bizType: form.bizType as BizType,
      enabled: form.enabled,
      autoApprove: form.autoApprove,
      remark: form.remark.trim() || undefined,
      timeoutHours: form.timeoutHours,
      timeoutAction: form.timeoutAction,
      nodes: form.autoApprove === SWITCH_STATUS.ENABLED ? [] : nodes,
    }
    if (isEdit.value && editingId.value) {
      await updateApprovalConfig(editingId.value, dto)
      ElMessage.success('编辑成功')
    } else {
      await addApprovalConfig(dto)
      ElMessage.success('新增成功')
    }
    handleClose()
    loadTable()
  } catch (error) {
    if (error instanceof Error) {
      ElMessage.warning(error.message)
      return
    }
    throw error
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(id: number) {
  await ElMessageBox.confirm('确定删除该审批配置吗？', '提示', { type: 'warning' })
  await deleteApprovalConfig(id)
  ElMessage.success('删除成功')
  loadTable()
}

function handleClose() {
  formVisible.value = false
  formRef.value?.clearValidate()
  resetFormModel()
}

onMounted(() => {
  loadTable()
  loadRoles()
})
</script>

<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true" class="search-form">
      <el-form-item label="业务类型">
        <el-select v-model="queryParams.bizType" placeholder="全部业务类型" clearable style="width: 180px">
          <el-option v-for="item in BIZ_TYPE_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button v-if="userStore.hasPermission('approval:config:add')" type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="tableData" border>
      <el-table-column prop="configName" label="配置名称" min-width="160" />
      <el-table-column label="业务类型" min-width="110">
        <template #default="{ row }">
          <el-tag>{{ getBizTypeLabel(row.bizType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="启用状态" min-width="100">
        <template #default="{ row }">
          <el-tag :type="row.enabled === SWITCH_STATUS.ENABLED ? 'success' : 'info'">
            {{ row.enabled === SWITCH_STATUS.ENABLED ? '启用' : '禁用' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="审批模式" min-width="100">
        <template #default="{ row }">
          <el-tag :type="row.autoApprove === SWITCH_STATUS.ENABLED ? 'warning' : 'info'">
            {{ row.autoApprove === SWITCH_STATUS.ENABLED ? '免审' : '需审批' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="timeoutHours" label="超时阈值(小时)" min-width="130" />
      <el-table-column label="超时处理" min-width="120">
        <template #default="{ row }">
          {{ TIMEOUT_ACTION_OPTIONS.find((item) => item.value === row.timeoutAction)?.label || '-' }}
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="180" />
      <el-table-column label="操作" class-name="table-action-column" fixed="right" min-width="180">
        <template #default="{ row }">
          <TableActionGroup
            :actions="[
              { label: '编辑', type: 'primary', icon: Edit, permission: 'approval:config:edit', onClick: () => handleEdit(row) },
              { label: '删除', type: 'danger', icon: Delete, permission: 'approval:config:delete', confirmText: '确定删除该审批配置吗？', onClick: () => handleDelete(row.id) },
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
      @size-change="loadTable"
      @current-change="loadTable"
    />

    <el-dialog v-model="formVisible" :title="isEdit ? '编辑审批配置' : '新增审批配置'" width="820px" @close="handleClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="配置名称" prop="configName">
              <el-input v-model="form.configName" placeholder="请输入配置名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业务类型" prop="bizType">
              <el-select v-model="form.bizType" placeholder="请选择业务类型" style="width: 100%">
                <el-option v-for="item in BIZ_TYPE_OPTIONS" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否启用" prop="enabled">
              <el-switch
                v-model="form.enabled"
                :active-value="SWITCH_STATUS.ENABLED"
                :inactive-value="SWITCH_STATUS.DISABLED"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="是否免审" prop="autoApprove">
              <el-switch
                v-model="form.autoApprove"
                :active-value="SWITCH_STATUS.ENABLED"
                :inactive-value="SWITCH_STATUS.DISABLED"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="超时阈值" prop="timeoutHours">
              <el-input-number v-model="form.timeoutHours" :min="1" :max="720" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="超时处理" prop="timeoutAction">
              <el-select v-model="form.timeoutAction" placeholder="请选择超时处理方式" style="width: 100%">
                <el-option
                  v-for="item in TIMEOUT_ACTION_OPTIONS"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>

      <el-divider content-position="left">审批节点</el-divider>
      <div v-if="showNodes">
        <div class="detail-toolbar">
          <el-button v-if="userStore.hasPermission('approval:config:add')" type="primary" plain :icon="Plus" @click="addNode">新增节点</el-button>
        </div>
        <el-table :data="form.nodes" border>
          <el-table-column label="节点顺序" min-width="100">
            <template #default="{ row }">
              <el-input-number v-model="row.stepOrder" :min="1" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="节点名称" min-width="150">
            <template #default="{ row }">
              <el-input v-model="row.nodeName" placeholder="请输入节点名称" size="small" />
            </template>
          </el-table-column>
          <el-table-column label="审批人类型" min-width="140">
            <template #default="{ row }">
              <el-select v-model="row.approverType" placeholder="请选择" size="small" @change="handleApproverTypeChange(row)">
                <el-option
                  v-for="item in APPROVER_TYPE_OPTIONS"
                  :key="item.value"
                  :label="item.label"
                  :value="item.value"
                />
              </el-select>
            </template>
          </el-table-column>
          <el-table-column label="审批人/角色" min-width="220">
            <template #default="{ row }">
              <el-select
                v-if="row.approverType === APPROVER_TYPE.ROLE"
                v-model="row.approverId"
                placeholder="请选择角色"
                size="small"
                filterable
                style="width: 100%"
              >
                <el-option
                  v-for="role in roleOptions"
                  :key="role.id"
                  :label="role.roleName"
                  :value="Number(role.id)"
                />
              </el-select>
              <div v-else class="user-check-cell">
                <el-input
                  v-model="row.approverUsername"
                  placeholder="请输入用户名"
                  size="small"
                  @input="scheduleUsernameCheck(row)"
                  @blur="validateUsername(row)"
                />
                <span
                  v-if="row.userCheckMessage"
                  :class="['user-check-message', row.userCheckStatus === 'valid' ? 'is-valid' : 'is-invalid']"
                >
                  {{ row.userCheckMessage }}
                </span>
              </div>
            </template>
          </el-table-column>
          <el-table-column label="操作" class-name="table-action-column" fixed="right" min-width="180">
            <template #default="{ $index }">
              <TableActionGroup
                :actions="[
                  { label: '删除', type: 'danger', icon: Delete, onClick: () => removeNode($index) },
                ]"
              />
            </template>
          </el-table-column>
        </el-table>
      </div>
      <el-empty v-else description="当前配置为免审，提交后将直接通过审批。" />

      <template #footer>
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

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

.user-check-cell {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.user-check-message {
  font-size: 12px;
  line-height: 16px;
}

.user-check-message.is-valid {
  color: var(--el-color-success);
}

.user-check-message.is-invalid {
  color: var(--el-color-danger);
}
</style>

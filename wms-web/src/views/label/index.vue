<template>
  <div class="app-container">
    <!-- 搜索栏 -->
    <el-form :model="queryParams" :inline="true" class="search-form">
      <el-form-item label="标签类型">
        <el-select v-model="queryParams.labelType" placeholder="全部" clearable>
          <el-option v-for="(label, value) in labelTypeMap" :key="value" :label="label" :value="Number(value)" />
        </el-select>
      </el-form-item>
      <el-form-item label="标签状态">
        <el-select v-model="queryParams.labelStatus" placeholder="全部" clearable>
          <el-option v-for="(label, value) in labelStatusMap" :key="value" :label="label" :value="Number(value)" />
        </el-select>
      </el-form-item>
      <el-form-item label="物品">
        <el-select
          v-model="queryParams.itemId"
          filterable
          remote
          reserve-keyword
          clearable
          placeholder="搜索物品"
          :remote-method="handleItemSearch"
          :loading="itemSearchLoading"
        >
          <el-option v-for="item in itemOptions" :key="item.id" :label="`${item.itemName}(${item.itemCode})`" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 操作按钮 -->
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" @click="handleGenerate">批量生成</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain :icon="Clock" @click="handleIdleLabels">闲置标签</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain :icon="Printer" :disabled="selectedIds.length === 0" @click="handleBatchPrint">批量打印</el-button>
      </el-col>
    </el-row>

    <!-- 表格 -->
    <el-table v-loading="loading" :data="tableData" border @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column prop="labelNo" label="标签编号" min-width="150" />
      <el-table-column prop="labelType" label="标签类型" min-width="100">
        <template #default="{ row }">
          <el-tag :type="labelTypeTagType(row.labelType)">{{ labelTypeMap[row.labelType] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="itemName" label="物品名称" min-width="140" show-overflow-tooltip />
      <el-table-column prop="itemCode" label="物品编码" min-width="120" />
      <el-table-column prop="bindType" label="绑定类型" min-width="100">
        <template #default="{ row }">
          {{ bindTypeMap[row.bindType] }}
        </template>
      </el-table-column>
      <el-table-column prop="labelStatus" label="标签状态" min-width="100">
        <template #default="{ row }">
          <el-tag :type="labelStatusTagType(row.labelStatus)">{{ labelStatusMap[row.labelStatus] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="printStatus" label="打印状态" min-width="90">
        <template #default="{ row }">
          <el-tag :type="row.printStatus === 1 ? 'success' : 'info'" size="small">{{ printStatusMap[row.printStatus] }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" min-width="160" />
      <el-table-column label="操作" class-name="table-action-column" fixed="right" min-width="180">
        <template #default="{ row }">
          <TableActionGroup
            :actions="[
              { label: '绑定', type: 'primary', visible: !row.itemId, onClick: () => handleBind(row) },
              { label: '状态', type: 'warning', onClick: () => handleStatusChange(row) },
              { label: '详情', type: 'primary', icon: View, onClick: () => handleView(row) },
            ]"
          />
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
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

    <!-- 批量生成弹窗 -->
    <el-dialog v-model="generateVisible" title="批量生成标签" width="520px" @close="handleGenerateClose">
      <el-form ref="generateFormRef" :model="generateForm" :rules="generateRules" label-width="100px">
        <el-form-item label="物品" prop="itemId">
          <el-select
            v-model="generateForm.itemId"
            filterable
            remote
            reserve-keyword
            placeholder="请搜索物品"
            :remote-method="handleGenerateItemSearch"
            :loading="generateItemLoading"
            style="width: 100%"
          >
            <el-option v-for="item in generateItemOptions" :key="item.id" :label="`${item.itemName}(${item.itemCode})`" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="生成数量" prop="count">
          <el-input-number v-model="generateForm.count" :min="1" :max="100" style="width: 100%" />
        </el-form-item>
        <el-form-item label="标签类型" prop="labelType">
          <el-radio-group v-model="generateForm.labelType">
            <el-radio :value="1">二维码</el-radio>
            <el-radio :value="2">条形码</el-radio>
            <el-radio :value="3">RFID</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="绑定类型" prop="bindType">
          <el-radio-group v-model="generateForm.bindType">
            <el-radio :value="1">单品对应</el-radio>
            <el-radio :value="2">批次对应</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="handleGenerateClose">取消</el-button>
        <el-button type="primary" :loading="generateLoading" @click="handleGenerateSubmit">生成</el-button>
      </template>
    </el-dialog>

    <!-- 状态修改弹窗 -->
    <el-dialog v-model="statusVisible" title="修改标签状态" width="450px" @close="handleStatusClose">
      <el-form ref="statusFormRef" :model="statusForm" :rules="statusRules" label-width="100px">
        <el-form-item label="当前状态">
          <el-tag :type="labelStatusTagType(currentLabel?.labelStatus ?? 0)">{{ labelStatusMap[currentLabel?.labelStatus ?? 0] }}</el-tag>
        </el-form-item>
        <el-form-item label="新状态" prop="labelStatus">
          <el-select v-model="statusForm.labelStatus" placeholder="请选择新状态" style="width: 100%">
            <el-option v-for="(label, value) in labelStatusMap" :key="value" :label="label" :value="Number(value)" />
          </el-select>
        </el-form-item>
        <el-alert v-if="statusTip" :title="statusTip" type="warning" :closable="false" show-icon style="margin-top: 8px" />
      </el-form>
      <template #footer>
        <el-button @click="handleStatusClose">取消</el-button>
        <el-button type="primary" :loading="statusLoading" @click="handleStatusSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="标签详情" width="650px">
      <template v-if="detailData">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="标签编号">{{ detailData.labelNo }}</el-descriptions-item>
          <el-descriptions-item label="标签类型">
            <el-tag :type="labelTypeTagType(detailData.labelType)">{{ labelTypeMap[detailData.labelType] }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="物品名称">{{ detailData.itemName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="物品编码">{{ detailData.itemCode || '-' }}</el-descriptions-item>
          <el-descriptions-item label="批次号">{{ detailData.batchNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="绑定类型">{{ bindTypeMap[detailData.bindType] }}</el-descriptions-item>
          <el-descriptions-item label="标签状态">
            <el-tag :type="labelStatusTagType(detailData.labelStatus)">{{ labelStatusMap[detailData.labelStatus] }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="打印状态">
            <el-tag :type="detailData.printStatus === 1 ? 'success' : 'info'" size="small">{{ printStatusMap[detailData.printStatus] }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="RFID编码" v-if="detailData.rfidCode">{{ detailData.rfidCode }}</el-descriptions-item>
          <el-descriptions-item label="借用时间">{{ detailData.borrowTime || '-' }}</el-descriptions-item>
          <el-descriptions-item label="预计归还">{{ detailData.expectedReturn || '-' }}</el-descriptions-item>
          <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        </el-descriptions>
        <div class="detail-code" v-if="detailData.labelType === 1 || detailData.labelType === 2">
          <h4>编码预览</h4>
          <QrBarCode
            v-if="detailData.labelType === 1"
            :value="detailData.qrContent || detailData.labelNo"
            type="qr"
            :width="180"
            :height="180"
          />
          <QrBarCode
            v-else
            :value="detailData.barcodeContent || detailData.labelNo"
            type="barcode"
            :width="240"
            :height="100"
          />
        </div>
      </template>
    </el-dialog>

    <!-- 绑定弹窗 -->
    <LabelBind v-model:visible="bindVisible" :label-id="bindLabelId" @success="handleQuery" />

    <!-- 闲置标签弹窗 -->
    <el-dialog v-model="idleVisible" title="长期闲置标签" width="900px">
      <el-table v-loading="idleLoading" :data="idleLabels" border max-height="400">
        <el-table-column prop="labelNo" label="标签编号" min-width="150" />
        <el-table-column prop="labelType" label="标签类型" min-width="100">
          <template #default="{ row }">
            <el-tag :type="labelTypeTagType(row.labelType)">{{ labelTypeMap[row.labelType] }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="itemName" label="物品名称" min-width="140" />
        <el-table-column prop="createTime" label="创建时间" min-width="160" />
        <el-table-column label="操作" class-name="table-action-column" min-width="100">
          <template #default="{ row }">
            <TableActionGroup
              :actions="[
                {
                  label: '修改状态',
                  type: 'primary',
                  onClick: () => {
                    handleStatusChange(row)
                    idleVisible = false
                  },
                },
              ]"
            />
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 批量打印弹窗 -->
    <LabelPrintComponent v-model:visible="printVisible" :labels="selectedLabels" @printed="handlePrinted" />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Plus, Printer, View, Clock } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import QrBarCode from '@/components/QrBarCode/index.vue'
import LabelBind from './components/LabelBind.vue'
import LabelPrintComponent from './components/LabelPrint.vue'
import { getLabelList, getLabel, generateLabels, updateLabelStatus, batchPrintLabels, getIdleLabels } from '@/api/item/label'
import { searchItem } from '@/api/item/item'
import type { ElectronicLabelVo, LabelGenerateDto, LabelStatusDto } from '@/types/label'
import type { WmsItemVo } from '@/types/item'

/** 状态映射 */
const labelTypeMap: Record<number, string> = { 1: '二维码', 2: '条形码', 3: 'RFID' }
const labelStatusMap: Record<number, string> = { 1: '在库', 2: '正在使用', 3: '已归还', 4: '报废', 5: '闲置' }
const bindTypeMap: Record<number, string> = { 1: '单品对应', 2: '批次对应' }
const printStatusMap: Record<number, string> = { 0: '未打印', 1: '已打印' }

/** 状态流转规则提示 */
const statusFlowTips: Record<number, string> = {
  1: '在库标签可流转为：正在使用、报废、闲置',
  2: '正在使用的标签可流转为：已归还、报废',
  3: '已归还标签可流转为：在库、报废',
  4: '已报废标签不可再变更状态',
  5: '闲置标签可流转为：在库、报废',
}

type TagType = 'success' | 'warning' | 'danger' | 'info' | 'primary'

function labelTypeTagType(labelType: number): TagType {
  const map: Record<number, TagType> = { 1: 'success', 2: 'warning', 3: 'info' }
  return map[labelType] || 'info'
}

function labelStatusTagType(status: number): TagType {
  const map: Record<number, TagType> = { 1: 'success', 2: 'warning', 3: 'info', 4: 'danger', 5: 'info' }
  return map[status] || 'info'
}

/** 搜索栏 */
const loading = ref(false)
const tableData = ref<ElectronicLabelVo[]>([])
const total = ref(0)
const itemSearchLoading = ref(false)
const itemOptions = ref<WmsItemVo[]>([])

const queryParams = reactive({
  page: 1,
  size: 20,
  labelType: undefined as number | undefined,
  labelStatus: undefined as number | undefined,
  itemId: undefined as number | undefined,
})

/** 远程搜索物品(搜索栏) */
async function handleItemSearch(keyword: string) {
  if (!keyword) return
  itemSearchLoading.value = true
  try {
    const res = await searchItem(keyword)
    itemOptions.value = res.data
  } finally {
    itemSearchLoading.value = false
  }
}

async function handleQuery() {
  loading.value = true
  try {
    const res = await getLabelList(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryParams.labelType = undefined
  queryParams.labelStatus = undefined
  queryParams.itemId = undefined
  queryParams.page = 1
  handleQuery()
}

/** 多选 */
const selectedIds = ref<number[]>([])
const selectedLabels = ref<ElectronicLabelVo[]>([])

function handleSelectionChange(rows: ElectronicLabelVo[]) {
  selectedIds.value = rows.map((r) => r.id)
  selectedLabels.value = rows
}

/** 批量生成弹窗 */
const generateVisible = ref(false)
const generateLoading = ref(false)
const generateFormRef = ref<FormInstance>()
const generateItemLoading = ref(false)
const generateItemOptions = ref<WmsItemVo[]>([])

const generateForm = reactive<LabelGenerateDto>({
  itemId: undefined as unknown as number,
  count: 1,
  labelType: 1,
  bindType: 1,
})

const generateRules: FormRules = {
  itemId: [{ required: true, message: '请选择物品', trigger: 'change' }],
  count: [{ required: true, message: '请输入生成数量', trigger: 'blur' }],
  labelType: [{ required: true, message: '请选择标签类型', trigger: 'change' }],
  bindType: [{ required: true, message: '请选择绑定类型', trigger: 'change' }],
}

async function handleGenerateItemSearch(keyword: string) {
  if (!keyword) return
  generateItemLoading.value = true
  try {
    const res = await searchItem(keyword)
    generateItemOptions.value = res.data
  } finally {
    generateItemLoading.value = false
  }
}

function handleGenerate() {
  Object.assign(generateForm, { itemId: undefined, count: 1, labelType: 1, bindType: 1 })
  generateVisible.value = true
}

async function handleGenerateSubmit() {
  await generateFormRef.value?.validate()
  generateLoading.value = true
  try {
    await generateLabels(generateForm)
    ElMessage.success('标签生成成功')
    handleGenerateClose()
    handleQuery()
  } finally {
    generateLoading.value = false
  }
}

function handleGenerateClose() {
  generateVisible.value = false
  generateFormRef.value?.resetFields()
}

/** 绑定弹窗 */
const bindVisible = ref(false)
const bindLabelId = ref(0)

function handleBind(row: ElectronicLabelVo) {
  bindLabelId.value = row.id
  bindVisible.value = true
}

/** 状态修改弹窗 */
const statusVisible = ref(false)
const statusLoading = ref(false)
const statusFormRef = ref<FormInstance>()
const currentLabel = ref<ElectronicLabelVo | null>(null)

const statusForm = reactive<LabelStatusDto>({
  labelStatus: undefined as unknown as number,
})

const statusRules: FormRules = {
  labelStatus: [{ required: true, message: '请选择新状态', trigger: 'change' }],
}

const statusTip = computed(() => {
  if (!currentLabel.value) return ''
  return statusFlowTips[currentLabel.value.labelStatus] || ''
})

function handleStatusChange(row: ElectronicLabelVo) {
  currentLabel.value = row
  statusForm.labelStatus = undefined as unknown as number
  statusVisible.value = true
}

async function handleStatusSubmit() {
  await statusFormRef.value?.validate()
  if (!currentLabel.value) return

  statusLoading.value = true
  try {
    await updateLabelStatus(currentLabel.value.id, { labelStatus: statusForm.labelStatus })
    ElMessage.success('状态修改成功')
    handleStatusClose()
    handleQuery()
  } finally {
    statusLoading.value = false
  }
}

function handleStatusClose() {
  statusVisible.value = false
  statusFormRef.value?.resetFields()
  currentLabel.value = null
}

/** 详情弹窗 */
const detailVisible = ref(false)
const detailData = ref<ElectronicLabelVo | null>(null)

async function handleView(row: ElectronicLabelVo) {
  const res = await getLabel(row.id)
  detailData.value = res.data
  detailVisible.value = true
}

/** 闲置标签 */
const idleVisible = ref(false)
const idleLoading = ref(false)
const idleLabels = ref<ElectronicLabelVo[]>([])

async function handleIdleLabels() {
  idleVisible.value = true
  idleLoading.value = true
  try {
    const res = await getIdleLabels()
    idleLabels.value = res.data
  } finally {
    idleLoading.value = false
  }
}

/** 批量打印 */
const printVisible = ref(false)

function handleBatchPrint() {
  if (selectedIds.value.length === 0) {
    ElMessage.warning('请先选择要打印的标签')
    return
  }
  printVisible.value = true
}

async function handlePrinted() {
  try {
    await batchPrintLabels({ labelIds: selectedIds.value })
    ElMessage.success('打印状态已更新')
    handleQuery()
  } catch {
    // 打印状态更新失败不影响用户
  }
}

onMounted(() => {
  handleQuery()
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

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}

.detail-code {
  margin-top: 16px;
  text-align: center;

  h4 {
    margin-bottom: 8px;
    color: #606266;
  }
}
</style>

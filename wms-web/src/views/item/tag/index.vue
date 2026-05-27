<template>
  <div class="app-container list-page">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
    </el-row>

    <div class="table-section">
      <el-table v-loading="loading" :data="tableData" border height="100%">
        <el-table-column prop="tagName" label="标签名称" min-width="160" />
        <el-table-column prop="tagColor" label="颜色" min-width="140">
          <template #default="{ row }">
            <div class="color-cell">
              <span class="color-dot" :style="{ backgroundColor: row.tagColor || '#dcdfe6' }"></span>
              <span>{{ row.tagColor || '-' }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="tagDesc" label="描述" min-width="220" show-overflow-tooltip />
        <el-table-column label="作用范围" min-width="140">
          <template #default="{ row }">
            {{ scopeTypeLabel(row.scopeType) }}
          </template>
        </el-table-column>
        <el-table-column prop="scopeId" label="范围对象ID" min-width="140" />
        <el-table-column prop="createTime" label="创建时间" min-width="180" />
        <el-table-column label="操作" class-name="table-action-column" fixed="right">
          <template #default="{ row }">
            <TableActionGroup
              :actions="[
                { label: '编辑', type: 'primary', icon: Edit, onClick: () => handleEdit(row) },
                { label: '删除', type: 'danger', icon: Delete, confirmText: '确定删除该标签吗？', onClick: () => handleDelete(row.id) },
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑标签' : '新增标签'" width="520px" @close="handleClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标签名称" prop="tagName">
          <el-input v-model="form.tagName" placeholder="请输入标签名称" />
        </el-form-item>
        <el-form-item label="标签颜色" prop="tagColor">
          <el-color-picker v-model="form.tagColor" />
        </el-form-item>
        <el-form-item label="标签描述" prop="tagDesc">
          <el-input v-model="form.tagDesc" type="textarea" :rows="2" placeholder="请输入标签描述" />
        </el-form-item>
        <el-form-item label="作用范围" prop="scopeType">
          <el-select v-model="form.scopeType" placeholder="请选择作用范围">
            <el-option label="全局" :value="0" />
            <el-option label="主类目" :value="1" />
            <el-option label="细分类目" :value="2" />
            <el-option label="具体物品" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="范围对象ID" prop="scopeId">
          <el-input-number v-model="form.scopeId" :min="1" controls-position="right" placeholder="为空表示全局" />
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
import { onMounted, reactive, ref } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import { addTag, deleteTag, getTagPage, updateTag } from '@/api/item/tag'
import type { WmsTagDto, WmsTagVo } from '@/types/item'
import { normalizePageTotal } from '@/utils/pagination'

const DEFAULT_TAG_COLOR = '#409EFF'
const DEFAULT_SCOPE_TYPE = 0

const loading = ref(false)
const tableData = ref<WmsTagVo[]>([])
const total = ref(0)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const queryParams = reactive({
  page: 1,
  size: 20
})

const form = reactive<WmsTagDto & { id?: number }>({
  tagName: '',
  tagColor: DEFAULT_TAG_COLOR,
  tagDesc: '',
  scopeType: DEFAULT_SCOPE_TYPE,
  scopeId: undefined
})

const rules: FormRules = {
  tagName: [{ required: true, message: '请输入标签名称', trigger: 'blur' }]
}

function scopeTypeLabel(scopeType?: number) {
  const scopeMap: Record<number, string> = {
    0: '全局',
    1: '主类目',
    2: '细分类目',
    3: '具体物品'
  }
  return scopeType === undefined ? '全局' : (scopeMap[scopeType] || '未知')
}

function resetForm() {
  Object.assign(form, {
    id: undefined,
    tagName: '',
    tagColor: DEFAULT_TAG_COLOR,
    tagDesc: '',
    scopeType: DEFAULT_SCOPE_TYPE,
    scopeId: undefined
  })
}

async function handleQuery() {
  loading.value = true
  try {
    const res = await getTagPage(queryParams)
    tableData.value = res.data.records
    total.value = normalizePageTotal(res.data.total)
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row: WmsTagVo) {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    tagName: row.tagName,
    tagColor: row.tagColor || DEFAULT_TAG_COLOR,
    tagDesc: row.tagDesc || '',
    scopeType: row.scopeType ?? DEFAULT_SCOPE_TYPE,
    scopeId: row.scopeId
  })
  dialogVisible.value = true
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    const dto: WmsTagDto = {
      tagName: form.tagName,
      tagColor: form.tagColor,
      tagDesc: form.tagDesc,
      scopeType: form.scopeType,
      scopeId: form.scopeId
    }
    if (isEdit.value && form.id) {
      await updateTag(form.id, dto)
      ElMessage.success('编辑成功')
    } else {
      await addTag(dto)
      ElMessage.success('新增成功')
    }
    handleClose()
    handleQuery()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(id: number) {
  await deleteTag(id)
  ElMessage.success('删除成功')
  handleQuery()
}

function handleClose() {
  dialogVisible.value = false
  formRef.value?.clearValidate()
  resetForm()
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

.mb8 {
  margin-bottom: 8px;
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

.pagination {
  justify-content: flex-end;
}

.color-cell {
  display: flex;
  align-items: center;
  gap: 8px;

  .color-dot {
    width: 16px;
    height: 16px;
    border-radius: 4px;
    display: inline-block;
    border: 1px solid #dcdfe6;
  }
}
</style>

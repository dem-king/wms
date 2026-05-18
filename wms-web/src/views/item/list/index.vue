<template>
  <div class="app-container">
    <el-form :model="queryParams" :inline="true" class="search-form">
      <el-form-item label="物品编码">
        <el-input v-model="queryParams.itemCode" placeholder="请输入物品编码" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="物品名称">
        <el-input v-model="queryParams.itemName" placeholder="请输入物品名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="类目">
        <el-select v-model="queryParams.categoryId" placeholder="请选择类目" clearable>
          <el-option v-for="cat in categoryList" :key="cat.id" :label="cat.categoryName" :value="cat.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="启用" :value="1" />
          <el-option label="禁用" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" :icon="Search" @click="handleQuery">搜索</el-button>
        <el-button :icon="Refresh" @click="handleReset">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain :icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="tableData" border>
      <el-table-column prop="itemCode" label="编码" min-width="120" />
      <el-table-column prop="itemName" label="名称" min-width="150" />
      <el-table-column prop="specModel" label="规格型号" min-width="120" />
      <el-table-column prop="unit" label="单位" min-width="80" />
      <el-table-column prop="categoryName" label="类目" min-width="100" />
      <el-table-column prop="currentStock" label="数量" min-width="80" />
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
              { label: '删除', type: 'danger', icon: Delete, confirmText: '确定删除该物品吗？', onClick: () => handleDelete(row.id) },
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑物品' : '新增物品'" width="700px" @close="handleClose">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="物品编码" prop="itemCode">
              <el-input v-model="form.itemCode" placeholder="请输入物品编码" :disabled="isEdit" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="物品名称" prop="itemName">
              <el-input v-model="form.itemName" placeholder="请输入物品名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="规格型号" prop="specModel">
              <el-input v-model="form.specModel" placeholder="请输入规格型号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计量单位" prop="unit">
              <el-input v-model="form.unit" placeholder="请输入计量单位" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="主类目" prop="categoryId">
              <el-select v-model="form.categoryId" placeholder="请选择主类目" @change="handleCategoryChange">
                <el-option v-for="cat in categoryList" :key="cat.id" :label="cat.categoryName" :value="cat.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="细分类目" prop="subCategoryId">
              <el-select v-model="form.subCategoryId" placeholder="请选择细分类目">
                <el-option v-for="sub in subCategoryOptions" :key="sub.id" :label="sub.subCategoryName" :value="sub.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="标签" prop="tagIds">
              <el-select v-model="form.tagIds" multiple placeholder="请选择标签">
                <el-option v-for="tag in tagList" :key="tag.id" :label="tag.tagName" :value="tag.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="供应商" prop="supplierId">
              <el-select v-model="form.supplierId" placeholder="请选择供应商">
                <el-option v-for="s in supplierList" :key="s.id" :label="s.supplierName" :value="s.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="安全库存" prop="safetyStock">
              <el-input-number v-model="form.safetyStock" :min="0" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">启用</el-radio>
                <el-radio :value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
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
import { getItemList, addItem, updateItem, deleteItem } from '@/api/item/item'
import { getCategoryList } from '@/api/item/category'
import { getSubCategories } from '@/api/item/category'
import { getTagList } from '@/api/item/tag'
import { getSupplierList } from '@/api/system/supplier'
import type { WmsItemVo, WmsItemDto, WmsCategoryVo, WmsSubCategoryVo, WmsTagVo } from '@/types/item'
import type { SysSupplierVo } from '@/types/system'

const loading = ref(false)
const tableData = ref<WmsItemVo[]>([])
const total = ref(0)
const categoryList = ref<WmsCategoryVo[]>([])
const subCategoryOptions = ref<WmsSubCategoryVo[]>([])
const tagList = ref<WmsTagVo[]>([])
const supplierList = ref<SysSupplierVo[]>([])

const queryParams = reactive({
  page: 1,
  size: 20,
  itemCode: '',
  itemName: '',
  categoryId: undefined as number | undefined,
  status: undefined as number | undefined
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const form = reactive<WmsItemDto & { id?: number }>({
  itemCode: '',
  itemName: '',
  specModel: '',
  unit: '',
  categoryId: undefined as unknown as number,
  subCategoryId: undefined as unknown as number,
  tagIds: [],
  supplierId: undefined as unknown as number,
  safetyStock: 0,
  status: 1
})

const rules: FormRules = {
  itemCode: [{ required: true, message: '请输入物品编码', trigger: 'blur' }],
  itemName: [{ required: true, message: '请输入物品名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择主类目', trigger: 'change' }],
  supplierId: [{ required: true, message: '请选择供应商', trigger: 'change' }]
}

async function handleQuery() {
  loading.value = true
  try {
    const res = await getItemList(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleReset() {
  queryParams.itemCode = ''
  queryParams.itemName = ''
  queryParams.categoryId = undefined
  queryParams.status = undefined
  queryParams.page = 1
  handleQuery()
}

function handleAdd() {
  isEdit.value = false
  Object.assign(form, { id: undefined, itemCode: '', itemName: '', specModel: '', unit: '', categoryId: undefined, subCategoryId: undefined, tagIds: [], supplierId: undefined, safetyStock: 0, status: 1 })
  dialogVisible.value = true
}

function handleEdit(row: WmsItemVo) {
  isEdit.value = true
  Object.assign(form, { id: row.id, itemCode: row.itemCode, itemName: row.itemName, specModel: row.specModel, unit: row.unit, categoryId: row.categoryId, subCategoryId: row.subCategoryId, tagIds: row.tagIds || [], supplierId: row.supplierId, safetyStock: row.safetyStock, status: row.status })
  if (row.categoryId) handleCategoryChange(row.categoryId)
  dialogVisible.value = true
}

async function handleCategoryChange(categoryId: number) {
  form.subCategoryId = undefined as unknown as number
  const res = await getSubCategories(categoryId)
  subCategoryOptions.value = res.data
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    const dto: WmsItemDto = { itemCode: form.itemCode, itemName: form.itemName, specModel: form.specModel, unit: form.unit, categoryId: form.categoryId, subCategoryId: form.subCategoryId, tagIds: form.tagIds, supplierId: form.supplierId, safetyStock: form.safetyStock, status: form.status }
    if (isEdit.value && form.id) {
      await updateItem(form.id, dto)
      ElMessage.success('编辑成功')
    } else {
      await addItem(dto)
      ElMessage.success('新增成功')
    }
    handleClose()
    handleQuery()
  } finally {
    submitLoading.value = false
  }
}

async function handleDelete(id: number) {
  await deleteItem(id)
  ElMessage.success('删除成功')
  handleQuery()
}

function handleClose() {
  dialogVisible.value = false
  formRef.value?.resetFields()
  subCategoryOptions.value = []
}

async function loadOptions() {
  const [catRes, tagRes, supplierRes] = await Promise.all([getCategoryList(), getTagList(), getSupplierList({ page: 1, size: 1000 })])
  categoryList.value = catRes.data
  tagList.value = tagRes.data
  supplierList.value = supplierRes.data.records
}

onMounted(() => {
  handleQuery()
  loadOptions()
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
</style>

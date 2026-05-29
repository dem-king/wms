<template>
  <div class="app-container list-page">
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
      <el-col :span="6">
        <el-input
          v-model="quickSearchKeyword"
          placeholder="快速搜索：输入编码/名称/拼音首字母"
          clearable
          :prefix-icon="Search"
          @keyup.enter="handleQuickSearch"
          @clear="handleQuery"
        />
      </el-col>
    </el-row>

    <div class="table-section">
      <el-table v-loading="loading" :data="tableData" border height="100%">
      <el-table-column label="图片" width="70">
        <template #default="{ row }">
          <el-image
            v-if="row.images && row.images.length > 0"
            :src="row.images[0].imageUrl"
            :preview-src-list="row.images.map((img: any) => img.imageUrl)"
            fit="cover"
            style="width: 40px; height: 40px; border-radius: 4px"
          />
          <span v-else style="color: #c0c4cc; font-size: 12px">无图</span>
        </template>
      </el-table-column>
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑物品' : '新增物品'" width="750px" @close="handleClose">
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
          <el-col :span="8">
            <el-form-item label="安全库存" prop="stockLowerLimit">
              <el-input-number v-model="form.stockLowerLimit" :min="0" controls-position="right" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="最大库存" prop="stockUpperLimit">
              <el-input-number v-model="form.stockUpperLimit" :min="0" controls-position="right" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="补货阈值" prop="replenishThreshold">
              <el-input-number v-model="form.replenishThreshold" :min="0" controls-position="right" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="16">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio :value="1">启用</el-radio>
                <el-radio :value="0">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="物品图片">
          <div class="image-upload-area">
            <div v-for="(img, index) in form.imageList" :key="img.id || index" class="image-item">
              <el-image :src="img.imageUrl" fit="cover" style="width: 100px; height: 100px; border-radius: 4px" />
              <div class="image-actions">
                <el-button type="danger" :icon="Delete" circle size="small" @click="handleRemoveImage(index)" />
              </div>
            </div>
            <el-upload
              :action="''"
              :auto-upload="false"
              :show-file-list="false"
              accept="image/*"
              @change="handleImageChange"
            >
              <div class="image-upload-btn">
                <el-icon :size="24"><Plus /></el-icon>
                <span>上传图片</span>
              </div>
            </el-upload>
          </div>
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
import type { FormInstance, FormRules, UploadFile } from 'element-plus'
import { ElMessage } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import TableActionGroup from '@/components/TableActionGroup/TableActionGroup.vue'
import { getItemList, addItem, updateItem, deleteItem, searchItem, deleteItemImage } from '@/api/item/item'
import { getCategoryList } from '@/api/item/category'
import { getSubCategories } from '@/api/item/category'
import { getTagList } from '@/api/item/tag'
import { getSupplierList } from '@/api/system/supplier'
import type { EntityId, WmsItemVo, WmsItemDto, WmsCategoryVo, WmsSubCategoryVo, WmsTagVo, ItemImageVo } from '@/types/item'
import type { SysSupplierVo } from '@/types/system'
import { useFileUpload } from '@/hooks/useFileUpload'
import { normalizePageTotal } from '@/utils/pagination'

const loading = ref(false)
const tableData = ref<WmsItemVo[]>([])
const total = ref(0)
const categoryList = ref<WmsCategoryVo[]>([])
const subCategoryOptions = ref<WmsSubCategoryVo[]>([])
const tagList = ref<WmsTagVo[]>([])
const supplierList = ref<SysSupplierVo[]>([])
const quickSearchKeyword = ref('')

const queryParams = reactive({
  page: 1,
  size: 20,
  itemCode: '',
  itemName: '',
  categoryId: undefined as EntityId | undefined,
  status: undefined as number | undefined
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const form = reactive<WmsItemDto & { id?: EntityId; imageList: ItemImageVo[] }>({
  itemCode: '',
  itemName: '',
  specModel: '',
  unit: '',
  categoryId: undefined as unknown as EntityId,
  subCategoryId: undefined as unknown as EntityId,
  tagIds: [],
  supplierId: undefined as unknown as EntityId,
  stockLowerLimit: 0,
  stockUpperLimit: 0,
  replenishThreshold: 0,
  status: 1,
  imageList: []
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
    total.value = normalizePageTotal(res.data.total)
  } finally {
    loading.value = false
  }
}

async function handleQuickSearch() {
  if (!quickSearchKeyword.value.trim()) return handleQuery()
  loading.value = true
  try {
    const res = await searchItem(quickSearchKeyword.value.trim())
    tableData.value = res.data
    total.value = normalizePageTotal(res.data.length)
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
  quickSearchKeyword.value = ''
  handleQuery()
}

function handleAdd() {
  isEdit.value = false
  Object.assign(form, {
    id: undefined, itemCode: '', itemName: '', specModel: '', unit: '',
    categoryId: undefined, subCategoryId: undefined, tagIds: [], supplierId: undefined,
    stockLowerLimit: 0, stockUpperLimit: 0, replenishThreshold: 0, status: 1, imageList: []
  })
  dialogVisible.value = true
}

function handleEdit(row: WmsItemVo) {
  isEdit.value = true
  Object.assign(form, {
    id: row.id, itemCode: row.itemCode, itemName: row.itemName, specModel: row.specModel, unit: row.unit,
    categoryId: row.categoryId, subCategoryId: row.subCategoryId, tagIds: row.tagIds || [],
    supplierId: row.supplierId, stockLowerLimit: row.stockLowerLimit ?? 0,
    stockUpperLimit: row.stockUpperLimit ?? 0, replenishThreshold: row.replenishThreshold ?? 0,
    status: row.status, imageList: row.images || []
  })
  if (row.categoryId) handleCategoryChange(row.categoryId)
  dialogVisible.value = true
}

async function handleCategoryChange(categoryId: EntityId) {
  form.subCategoryId = undefined as unknown as EntityId
  const res = await getSubCategories(categoryId)
  subCategoryOptions.value = res.data
}

const { upload: uploadItemImageFile } = useFileUpload({ bucket: 'items' })

async function handleImageChange(uploadFile: UploadFile) {
  if (!uploadFile.raw || !form.id) {
    ElMessage.warning('请先保存物品后再上传图片')
    return
  }
  try {
    // 通过useFileUpload统一上传，自动适配本地/MinIO存储
    const result = await uploadItemImageFile(uploadFile.raw)
    form.imageList.push({
      imageUrl: result.url,
      imageName: uploadFile.raw.name,
      sortOrder: form.imageList.length + 1
    } as ItemImageVo)
    ElMessage.success('图片上传成功')
  } catch {
    ElMessage.error('图片上传失败')
  }
}

async function handleRemoveImage(index: number) {
  const img = form.imageList[index]
  if (form.id && img.id) {
    await deleteItemImage(form.id, img.id)
  }
  form.imageList.splice(index, 1)
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    const dto: WmsItemDto = {
      itemCode: form.itemCode, itemName: form.itemName, specModel: form.specModel, unit: form.unit,
      categoryId: form.categoryId, subCategoryId: form.subCategoryId, tagIds: form.tagIds,
      supplierId: form.supplierId, stockLowerLimit: form.stockLowerLimit,
      stockUpperLimit: form.stockUpperLimit, replenishThreshold: form.replenishThreshold, status: form.status
    }
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

async function handleDelete(id: EntityId) {
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

.image-upload-area {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.image-item {
  position: relative;
  width: 100px;
  height: 100px;

  .image-actions {
    position: absolute;
    top: 0;
    right: 0;
    opacity: 0;
    transition: opacity 0.2s;
  }

  &:hover .image-actions {
    opacity: 1;
  }
}

.image-upload-btn {
  width: 100px;
  height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: #8c939d;
  font-size: 12px;

  &:hover {
    border-color: #409eff;
    color: #409eff;
  }
}
</style>

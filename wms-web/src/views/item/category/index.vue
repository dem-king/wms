<template>
  <div class="app-container list-page">
    <div class="category-page-body">
      <el-card class="category-sidebar">
        <template #header>
          <div class="card-header">
            <span>主类目</span>
            <el-button type="primary" text :icon="Plus" @click="handleAddCategory">新增</el-button>
          </div>
        </template>
        <div class="category-tree-container">
          <el-tree
            :data="categoryTree"
            :props="{ label: 'categoryName' }"
            :current-node-key="currentCategory?.id"
            node-key="id"
            highlight-current
            default-expand-all
            @node-click="handleNodeClick"
          >
            <template #default="{ node, data }">
              <div class="tree-node">
                <span>{{ node.label }}</span>
                <span class="tree-actions">
                  <el-button text :icon="Edit" size="small" @click.stop="handleEditCategory(data)" />
                  <el-button text :icon="Delete" size="small" type="danger" @click.stop="handleDeleteCategory(data)" />
                </span>
              </div>
            </template>
          </el-tree>
        </div>
        <div class="pagination-container">
          <el-pagination
            v-model:current-page="categoryQuery.page"
            v-model:page-size="categoryQuery.size"
            :total="categoryTotal"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next"
            class="pagination"
            @size-change="loadCategoryTree"
            @current-change="loadCategoryTree"
          />
        </div>
      </el-card>

      <el-card class="category-detail-panel">
        <template #header>
          <div class="card-header">
            <span>{{ currentCategory ? `${currentCategory.categoryName} - 细分类目` : '请选择主类目' }}</span>
            <el-button v-if="currentCategory" type="primary" text :icon="Plus" @click="handleAddSub">新增</el-button>
          </div>
        </template>

        <div class="table-section" v-loading="subLoading">
          <el-empty v-if="!currentCategory" description="请选择主类目后查看细分类目" />
          <template v-else>
            <el-table :data="subCategoryList" border height="100%">
              <el-table-column prop="subCategoryCode" label="编码" min-width="140" />
              <el-table-column prop="subCategoryName" label="名称" min-width="180" />
              <el-table-column prop="sortOrder" label="排序" min-width="100" />
              <el-table-column prop="createTime" label="创建时间" min-width="180" />
              <el-table-column label="操作" class-name="table-action-column" fixed="right">
                <template #default="{ row }">
                  <TableActionGroup
                    :actions="[
                      { label: '编辑', type: 'primary', icon: Edit, onClick: () => handleEditSub(row) },
                      { label: '删除', type: 'danger', icon: Delete, confirmText: '确定删除该细分类目吗？', onClick: () => handleDeleteSub(row.id) },
                    ]"
                  />
                </template>
              </el-table-column>
            </el-table>

            <div class="pagination-container">
              <el-pagination
                v-model:current-page="subQuery.page"
                v-model:page-size="subQuery.size"
                :total="subTotal"
                :page-sizes="[10, 20, 50, 100]"
                layout="total, sizes, prev, pager, next, jumper"
                class="pagination"
                @size-change="loadSubCategoryPage"
                @current-change="loadSubCategoryPage"
              />
            </div>
          </template>
        </div>
      </el-card>
    </div>

    <el-dialog v-model="categoryDialogVisible" :title="isCategoryEdit ? '编辑主类目' : '新增主类目'" width="520px" @close="resetCategoryForm">
      <el-form ref="categoryFormRef" :model="categoryForm" :rules="categoryRules" label-width="100px">
        <el-form-item label="类目名称" prop="categoryName">
          <el-input v-model="categoryForm.categoryName" placeholder="请输入类目名称" />
        </el-form-item>
        <el-form-item label="类目编码" prop="categoryCode">
          <el-input v-model="categoryForm.categoryCode" placeholder="请输入类目编码" />
        </el-form-item>
        <el-form-item label="标识颜色" prop="categoryColor">
          <el-color-picker v-model="categoryForm.categoryColor" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <el-input v-model="categoryForm.icon" placeholder="请输入图标名称" />
        </el-form-item>
        <el-form-item label="排序号" prop="sortOrder">
          <el-input-number v-model="categoryForm.sortOrder" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="是否消耗品" prop="isConsumable">
          <el-radio-group v-model="categoryForm.isConsumable">
            <el-radio :value="0">否</el-radio>
            <el-radio :value="1">是</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="categoryDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleCategorySubmit">确 定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="subDialogVisible" :title="isSubEdit ? '编辑细分类目' : '新增细分类目'" width="500px" @close="resetSubForm">
      <el-form ref="subFormRef" :model="subForm" :rules="subRules" label-width="100px">
        <el-form-item label="名称" prop="subCategoryName">
          <el-input v-model="subForm.subCategoryName" placeholder="请输入细分类目名称" />
        </el-form-item>
        <el-form-item label="编码" prop="subCategoryCode">
          <el-input v-model="subForm.subCategoryCode" placeholder="请输入细分类目编码" />
        </el-form-item>
        <el-form-item label="排序号" prop="sortOrder">
          <el-input-number v-model="subForm.sortOrder" :min="0" controls-position="right" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="subDialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubSubmit">确 定</el-button>
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
import {
  addCategory,
  addSubCategory,
  deleteCategory,
  deleteSubCategory,
  getCategoryPage,
  getSubCategoryPage,
  updateCategory,
  updateSubCategory
} from '@/api/item/category'
import type { EntityId, WmsCategoryDto, WmsCategoryVo, WmsSubCategoryDto, WmsSubCategoryVo } from '@/types/item'
import { normalizePageTotal } from '@/utils/pagination'

const DEFAULT_CATEGORY_COLOR = '#409EFF'

const categoryTree = ref<WmsCategoryVo[]>([])
const categoryTotal = ref(0)
const currentCategory = ref<WmsCategoryVo | null>(null)
const subCategoryList = ref<WmsSubCategoryVo[]>([])
const subTotal = ref(0)
const subLoading = ref(false)
const submitLoading = ref(false)

const categoryQuery = reactive({
  page: 1,
  size: 10
})

const subQuery = reactive({
  page: 1,
  size: 10
})

const categoryDialogVisible = ref(false)
const isCategoryEdit = ref(false)
const categoryFormRef = ref<FormInstance>()
const categoryForm = reactive<WmsCategoryDto & { id?: EntityId }>({
  categoryName: '',
  categoryCode: '',
  categoryColor: DEFAULT_CATEGORY_COLOR,
  icon: '',
  sortOrder: 0,
  isConsumable: 0
})
const categoryRules: FormRules = {
  categoryName: [{ required: true, message: '请输入类目名称', trigger: 'blur' }],
  categoryCode: [{ required: true, message: '请输入类目编码', trigger: 'blur' }]
}

const subDialogVisible = ref(false)
const isSubEdit = ref(false)
const subFormRef = ref<FormInstance>()
const subForm = reactive<WmsSubCategoryDto & { id?: EntityId; categoryId: EntityId }>({
  categoryId: '',
  subCategoryName: '',
  subCategoryCode: '',
  sortOrder: 0
})
const subRules: FormRules = {
  subCategoryName: [{ required: true, message: '请输入细分类目名称', trigger: 'blur' }],
  subCategoryCode: [{ required: true, message: '请输入细分类目编码', trigger: 'blur' }]
}

function resetCategoryData() {
  Object.assign(categoryForm, {
    id: undefined,
    categoryName: '',
    categoryCode: '',
    categoryColor: DEFAULT_CATEGORY_COLOR,
    icon: '',
    sortOrder: 0,
    isConsumable: 0
  })
}

function resetSubData() {
  Object.assign(subForm, {
    id: undefined,
    categoryId: currentCategory.value?.id || '',
    subCategoryName: '',
    subCategoryCode: '',
    sortOrder: 0
  })
}

async function loadSubCategoryPage() {
  if (!currentCategory.value) {
    subCategoryList.value = []
    subTotal.value = 0
    return
  }

  subLoading.value = true
  try {
    const res = await getSubCategoryPage(currentCategory.value.id, subQuery)
    subCategoryList.value = res.data.records
    subTotal.value = normalizePageTotal(res.data.total)
  } finally {
    subLoading.value = false
  }
}

async function loadCategoryTree() {
  const res = await getCategoryPage(categoryQuery)
  categoryTree.value = res.data.records
  categoryTotal.value = normalizePageTotal(res.data.total)

  if (categoryTree.value.length === 0) {
    currentCategory.value = null
    subCategoryList.value = []
    subTotal.value = 0
    return
  }

  const selectedCategory = categoryTree.value.find((item) => item.id === currentCategory.value?.id) || categoryTree.value[0]
  currentCategory.value = selectedCategory
  await loadSubCategoryPage()
}

async function handleNodeClick(data: WmsCategoryVo) {
  currentCategory.value = data
  subQuery.page = 1
  await loadSubCategoryPage()
}

function handleAddCategory() {
  isCategoryEdit.value = false
  resetCategoryData()
  categoryDialogVisible.value = true
}

function handleEditCategory(data: WmsCategoryVo) {
  isCategoryEdit.value = true
  Object.assign(categoryForm, {
    id: data.id,
    categoryName: data.categoryName,
    categoryCode: data.categoryCode,
    categoryColor: data.categoryColor || DEFAULT_CATEGORY_COLOR,
    icon: data.icon || '',
    sortOrder: data.sortOrder,
    isConsumable: data.isConsumable ?? 0
  })
  categoryDialogVisible.value = true
}

async function handleDeleteCategory(data: WmsCategoryVo) {
  await deleteCategory(data.id)
  ElMessage.success('删除成功')
  if (currentCategory.value?.id === data.id) {
    currentCategory.value = null
  }
  await loadCategoryTree()
}

async function handleCategorySubmit() {
  await categoryFormRef.value?.validate()
  submitLoading.value = true
  try {
    const dto: WmsCategoryDto = {
      categoryName: categoryForm.categoryName,
      categoryCode: categoryForm.categoryCode,
      categoryColor: categoryForm.categoryColor,
      icon: categoryForm.icon,
      sortOrder: categoryForm.sortOrder,
      isConsumable: categoryForm.isConsumable
    }
    if (isCategoryEdit.value && categoryForm.id) {
      await updateCategory(categoryForm.id, dto)
      ElMessage.success('编辑成功')
    } else {
      await addCategory(dto)
      ElMessage.success('新增成功')
    }
    categoryDialogVisible.value = false
    await loadCategoryTree()
  } finally {
    submitLoading.value = false
  }
}

function resetCategoryForm() {
  categoryFormRef.value?.clearValidate()
  resetCategoryData()
}

function handleAddSub() {
  if (!currentCategory.value) return
  isSubEdit.value = false
  resetSubData()
  subDialogVisible.value = true
}

function handleEditSub(row: WmsSubCategoryVo) {
  isSubEdit.value = true
  Object.assign(subForm, {
    id: row.id,
    categoryId: row.categoryId,
    subCategoryName: row.subCategoryName,
    subCategoryCode: row.subCategoryCode,
    sortOrder: row.sortOrder
  })
  subDialogVisible.value = true
}

async function handleDeleteSub(id: EntityId) {
  await deleteSubCategory(id)
  ElMessage.success('删除成功')
  await loadSubCategoryPage()
}

async function handleSubSubmit() {
  await subFormRef.value?.validate()
  submitLoading.value = true
  try {
    const dto: WmsSubCategoryDto = {
      subCategoryName: subForm.subCategoryName,
      subCategoryCode: subForm.subCategoryCode,
      sortOrder: subForm.sortOrder
    }
    if (isSubEdit.value && subForm.id) {
      await updateSubCategory(subForm.id, dto)
      ElMessage.success('编辑成功')
    } else {
      await addSubCategory(subForm.categoryId, dto)
      ElMessage.success('新增成功')
    }
    subDialogVisible.value = false
    await loadSubCategoryPage()
  } finally {
    submitLoading.value = false
  }
}

function resetSubForm() {
  subFormRef.value?.clearValidate()
  resetSubData()
}

onMounted(() => {
  loadCategoryTree()
})
</script>

<style scoped lang="scss">
.app-container.list-page {
  padding: 20px;
  display: flex;
  flex-direction: column;
  min-height: 0;
}

.category-page-body {
  flex: 1;
  min-height: 0;
  display: flex;
  gap: 16px;
}

.category-sidebar {
  width: 320px;
  min-width: 320px;
  min-height: 0;
}

.category-detail-panel {
  flex: 1;
  min-height: 0;
}

.category-sidebar :deep(.el-card__body),
.category-detail-panel :deep(.el-card__body) {
  height: 100%;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.category-tree-container {
  flex: 1;
  min-height: 0;
  overflow: auto;
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

.tree-node {
  flex: 1;
  display: flex;
  justify-content: space-between;
  align-items: center;

  .tree-actions {
    display: none;
  }

  &:hover .tree-actions {
    display: inline-flex;
  }
}
</style>

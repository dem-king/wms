<template>
  <div class="app-container">
    <el-row :gutter="16">
      <el-col :span="6">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>主类目</span>
              <el-button type="primary" text :icon="Plus" @click="handleAddCategory">新增</el-button>
            </div>
          </template>
          <el-tree
            ref="treeRef"
            :data="categoryTree"
            :props="{ label: 'categoryName', children: 'children' }"
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
        </el-card>
      </el-col>
      <el-col :span="18">
        <el-card>
          <template #header>
            <div class="card-header">
              <span>{{ currentCategory ? `${currentCategory.categoryName} - 细分类目` : '请选择主类目' }}</span>
              <el-button v-if="currentCategory" type="primary" text :icon="Plus" @click="handleAddSub">新增</el-button>
            </div>
          </template>
          <el-table v-loading="subLoading" :data="subCategoryList" border>
            <el-table-column prop="subCategoryCode" label="编码" min-width="120" />
            <el-table-column prop="subCategoryName" label="名称" min-width="150" />
            <el-table-column prop="sortOrder" label="排序" min-width="80" />
            <el-table-column prop="status" label="状态" min-width="80">
              <template #default="{ row }">
                <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="createTime" label="创建时间" min-width="160" />
            <el-table-column label="操作" width="180" fixed="right">
              <template #default="{ row }">
                <el-button type="primary" text :icon="Edit" @click="handleEditSub(row)">编辑</el-button>
                <el-popconfirm title="确定删除该细分类目吗？" @confirm="handleDeleteSub(row.id)">
                  <template #reference>
                    <el-button type="danger" text :icon="Delete">删除</el-button>
                  </template>
                </el-popconfirm>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <el-dialog v-model="categoryDialogVisible" :title="isCategoryEdit ? '编辑主类目' : '新增主类目'" width="500px" @close="resetCategoryForm">
      <el-form ref="categoryFormRef" :model="categoryForm" :rules="categoryRules" label-width="100px">
        <el-form-item label="类目名称" prop="categoryName">
          <el-input v-model="categoryForm.categoryName" placeholder="请输入类目名称" />
        </el-form-item>
        <el-form-item label="类目编码" prop="categoryCode">
          <el-input v-model="categoryForm.categoryCode" placeholder="请输入类目编码" />
        </el-form-item>
        <el-form-item label="排序号" prop="sortOrder">
          <el-input-number v-model="categoryForm.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="categoryForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
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
          <el-input-number v-model="subForm.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="subForm.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
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
import { ref, reactive, onMounted } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { Plus, Edit, Delete } from '@element-plus/icons-vue'
import { getCategoryList, addCategory, updateCategory, deleteCategory, getSubCategories, addSubCategory, updateSubCategory, deleteSubCategory } from '@/api/item/category'
import type { WmsCategoryVo, WmsCategoryDto, WmsSubCategoryVo, WmsSubCategoryDto } from '@/types/item'

const treeRef = ref<InstanceType<typeof import('element-plus')['ElTree']>>()
const categoryTree = ref<WmsCategoryVo[]>([])
const currentCategory = ref<WmsCategoryVo | null>(null)
const subCategoryList = ref<WmsSubCategoryVo[]>([])
const subLoading = ref(false)
const submitLoading = ref(false)

const categoryDialogVisible = ref(false)
const isCategoryEdit = ref(false)
const categoryFormRef = ref<FormInstance>()
const categoryForm = reactive<WmsCategoryDto & { id?: number }>({
  categoryName: '',
  categoryCode: '',
  sortOrder: 0,
  status: 1
})
const categoryRules: FormRules = {
  categoryName: [{ required: true, message: '请输入类目名称', trigger: 'blur' }],
  categoryCode: [{ required: true, message: '请输入类目编码', trigger: 'blur' }]
}

const subDialogVisible = ref(false)
const isSubEdit = ref(false)
const subFormRef = ref<FormInstance>()
const subForm = reactive<WmsSubCategoryDto & { id?: number }>({
  categoryId: 0,
  subCategoryName: '',
  subCategoryCode: '',
  sortOrder: 0,
  status: 1
})
const subRules: FormRules = {
  subCategoryName: [{ required: true, message: '请输入细分类目名称', trigger: 'blur' }],
  subCategoryCode: [{ required: true, message: '请输入细分类目编码', trigger: 'blur' }]
}

async function loadCategoryTree() {
  const res = await getCategoryList()
  categoryTree.value = res.data
}

async function handleNodeClick(data: WmsCategoryVo) {
  currentCategory.value = data
  subLoading.value = true
  try {
    const res = await getSubCategories(data.id)
    subCategoryList.value = res.data
  } finally {
    subLoading.value = false
  }
}

function handleAddCategory() {
  isCategoryEdit.value = false
  Object.assign(categoryForm, { id: undefined, categoryName: '', categoryCode: '', sortOrder: 0, status: 1 })
  categoryDialogVisible.value = true
}

function handleEditCategory(data: WmsCategoryVo) {
  isCategoryEdit.value = true
  Object.assign(categoryForm, { id: data.id, categoryName: data.categoryName, categoryCode: data.categoryCode, sortOrder: data.sortOrder, status: data.status })
  categoryDialogVisible.value = true
}

async function handleDeleteCategory(data: WmsCategoryVo) {
  await deleteCategory(data.id)
  ElMessage.success('删除成功')
  if (currentCategory.value?.id === data.id) {
    currentCategory.value = null
    subCategoryList.value = []
  }
  loadCategoryTree()
}

async function handleCategorySubmit() {
  await categoryFormRef.value?.validate()
  submitLoading.value = true
  try {
    const dto: WmsCategoryDto = { categoryName: categoryForm.categoryName, categoryCode: categoryForm.categoryCode, sortOrder: categoryForm.sortOrder, status: categoryForm.status }
    if (isCategoryEdit.value && categoryForm.id) {
      await updateCategory(categoryForm.id, dto)
      ElMessage.success('编辑成功')
    } else {
      await addCategory(dto)
      ElMessage.success('新增成功')
    }
    categoryDialogVisible.value = false
    loadCategoryTree()
  } finally {
    submitLoading.value = false
  }
}

function resetCategoryForm() {
  categoryFormRef.value?.resetFields()
}

function handleAddSub() {
  if (!currentCategory.value) return
  isSubEdit.value = false
  Object.assign(subForm, { id: undefined, categoryId: currentCategory.value.id, subCategoryName: '', subCategoryCode: '', sortOrder: 0, status: 1 })
  subDialogVisible.value = true
}

function handleEditSub(row: WmsSubCategoryVo) {
  isSubEdit.value = true
  Object.assign(subForm, { id: row.id, categoryId: row.categoryId, subCategoryName: row.subCategoryName, subCategoryCode: row.subCategoryCode, sortOrder: row.sortOrder, status: row.status })
  subDialogVisible.value = true
}

async function handleDeleteSub(id: number) {
  await deleteSubCategory(id)
  ElMessage.success('删除成功')
  if (currentCategory.value) handleNodeClick(currentCategory.value)
}

async function handleSubSubmit() {
  await subFormRef.value?.validate()
  submitLoading.value = true
  try {
    const dto: WmsSubCategoryDto = { categoryId: subForm.categoryId, subCategoryName: subForm.subCategoryName, subCategoryCode: subForm.subCategoryCode, sortOrder: subForm.sortOrder, status: subForm.status }
    if (isSubEdit.value && subForm.id) {
      await updateSubCategory(subForm.id, dto)
      ElMessage.success('编辑成功')
    } else {
      await addSubCategory(dto)
      ElMessage.success('新增成功')
    }
    subDialogVisible.value = false
    if (currentCategory.value) handleNodeClick(currentCategory.value)
  } finally {
    submitLoading.value = false
  }
}

function resetSubForm() {
  subFormRef.value?.resetFields()
}

onMounted(() => {
  loadCategoryTree()
})
</script>

<style scoped lang="scss">
.app-container {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

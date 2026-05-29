<script setup lang="ts">
import { computed, reactive, shallowRef, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { addRole, updateRole } from '@/api/system/role'
import { getDeptTree } from '@/api/system/dept'
import type { EntityId, SysDeptVo, SysRoleVo } from '@/types/system'

const DATA_SCOPE_CUSTOM = 2

const dataScopeOptions: Record<number, string> = {
  1: '全部数据',
  2: '自定义数据',
  3: '本部门数据',
  4: '本部门及以下数据',
  5: '仅本人数据'
}

const props = defineProps<{
  visible: boolean
  isEdit: boolean
  formData: SysRoleVo | null
}>()

const emit = defineEmits<{
  'update:visible': [val: boolean]
  success: []
}>()

const dialogVisible = shallowRef(false)
const formRef = shallowRef<FormInstance>()
const submitLoading = shallowRef(false)
const deptTree = shallowRef<SysDeptVo[]>([])

const form = reactive({
  roleName: '',
  roleCode: '',
  roleDesc: '',
  dataScope: 1,
  deptIds: [] as EntityId[],
  status: 1
})

const isCustomDataScope = computed(() => form.dataScope === DATA_SCOPE_CUSTOM)

const rules: FormRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  dataScope: [{ required: true, message: '请选择数据范围', trigger: 'change' }],
  deptIds: [{
    validator: (_rule, value: EntityId[], callback) => {
      if (isCustomDataScope.value && (!value || value.length === 0)) {
        callback(new Error('请选择部门'))
        return
      }
      callback()
    },
    trigger: 'change'
  }]
}

watch(() => props.visible, async (val) => {
  dialogVisible.value = val
  if (!val) {
    return
  }
  await loadDeptTree()
  if (props.isEdit && props.formData) {
    Object.assign(form, {
      roleName: props.formData.roleName,
      roleCode: props.formData.roleCode,
      roleDesc: props.formData.roleDesc,
      dataScope: props.formData.dataScope,
      deptIds: props.formData.deptIds ?? [],
      status: props.formData.status
    })
  }
})

watch(() => form.dataScope, () => {
  if (!isCustomDataScope.value) {
    form.deptIds = []
  }
})

watch(dialogVisible, (val) => { emit('update:visible', val) })

async function loadDeptTree() {
  if (deptTree.value.length > 0) {
    return
  }
  const res = await getDeptTree()
  deptTree.value = res.data
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  const payload = {
    ...form,
    deptIds: isCustomDataScope.value ? form.deptIds : []
  }
  try {
    if (props.isEdit && props.formData) {
      await updateRole(props.formData.id, payload)
      ElMessage.success('更新成功')
    } else {
      await addRole(payload)
      ElMessage.success('新增成功')
    }
    emit('success')
    handleClose()
  } finally {
    submitLoading.value = false
  }
}

function handleClose() {
  dialogVisible.value = false
  formRef.value?.resetFields()
  Object.assign(form, { roleName: '', roleCode: '', roleDesc: '', dataScope: 1, deptIds: [], status: 1 })
}
</script>

<template>
  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="520px" @close="handleClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="108px">
      <el-form-item label="角色名称" prop="roleName">
        <el-input v-model="form.roleName" placeholder="请输入角色名称" />
      </el-form-item>
      <el-form-item label="角色编码" prop="roleCode">
        <el-input v-model="form.roleCode" placeholder="请输入角色编码" :disabled="isEdit" />
      </el-form-item>
      <el-form-item label="角色描述" prop="roleDesc">
        <el-input v-model="form.roleDesc" type="textarea" placeholder="请输入角色描述" :rows="3" />
      </el-form-item>
      <el-form-item label="数据范围" prop="dataScope">
        <el-select v-model="form.dataScope" placeholder="请选择数据范围">
          <el-option v-for="(label, value) in dataScopeOptions" :key="value" :label="label" :value="Number(value)" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="isCustomDataScope" label="授权部门" prop="deptIds">
        <el-tree-select
          v-model="form.deptIds"
          class="role-form-tree"
          :data="deptTree"
          multiple
          collapse-tags
          collapse-tags-tooltip
          check-strictly
          show-checkbox
          node-key="id"
          value-key="id"
          :props="{ label: 'deptName', children: 'children' }"
          placeholder="请选择授权部门"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">启用</el-radio>
          <el-radio :value="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<style scoped>
.role-form-tree {
  width: 100%;
}
</style>

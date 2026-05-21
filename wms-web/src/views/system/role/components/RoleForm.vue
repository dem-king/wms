<template>
  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑角色' : '新增角色'" width="500px" @close="handleClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="角色名称" prop="roleName">
        <el-input v-model="form.roleName" placeholder="请输入角色名称" />
      </el-form-item>
      <el-form-item label="角色编码" prop="roleCode">
        <el-input v-model="form.roleCode" placeholder="请输入角色编码" :disabled="isEdit" />
      </el-form-item>
      <el-form-item label="描述" prop="roleDesc">
        <el-input v-model="form.roleDesc" type="textarea" placeholder="请输入描述" :rows="3" />
      </el-form-item>
      <el-form-item label="数据范围" prop="dataScope">
        <el-select v-model="form.dataScope" placeholder="请选择数据范围">
          <el-option v-for="(label, value) in dataScopeOptions" :key="value" :label="label" :value="Number(value)" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">启用</el-radio>
          <el-radio :value="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确 定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { addRole, updateRole } from '@/api/system/role'
import type { SysRoleVo } from '@/types/system'

const dataScopeOptions: Record<number, string> = {
  1: '全部数据',
  2: '自定义',
  3: '本部门',
  4: '本部门及以下',
  5: '仅本人'
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

const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)

const form = reactive({
  roleName: '',
  roleCode: '',
  roleDesc: '',
  dataScope: 1,
  status: 1
})

const rules: FormRules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleCode: [{ required: true, message: '请输入角色编码', trigger: 'blur' }],
  dataScope: [{ required: true, message: '请选择数据范围', trigger: 'change' }]
}

watch(() => props.visible, (val) => {
  dialogVisible.value = val
  if (val && props.isEdit && props.formData) {
    Object.assign(form, {
      roleName: props.formData.roleName,
      roleCode: props.formData.roleCode,
      roleDesc: props.formData.roleDesc,
      dataScope: props.formData.dataScope,
      status: props.formData.status
    })
  }
})
watch(dialogVisible, (val) => { emit('update:visible', val) })

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    if (props.isEdit && props.formData) {
      await updateRole(props.formData.id, { ...form })
      ElMessage.success('编辑成功')
    } else {
      await addRole({ ...form })
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
  Object.assign(form, { roleName: '', roleCode: '', roleDesc: '', dataScope: 1, status: 1 })
}
</script>

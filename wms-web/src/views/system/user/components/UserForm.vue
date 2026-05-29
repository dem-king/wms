<template>
  <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="600px" @close="handleClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="用户名" prop="username">
        <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit" />
      </el-form-item>
      <el-form-item v-if="!isEdit" label="密码" prop="password">
        <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
      </el-form-item>
      <el-form-item label="真实姓名" prop="realName">
        <el-input v-model="form.realName" placeholder="请输入真实姓名" />
      </el-form-item>
      <el-form-item label="部门" prop="deptId">
        <el-tree-select
          v-model="form.deptId"
          :data="deptTree"
          :props="{ label: 'deptName', children: 'children' }"
          value-key="id"
          placeholder="请选择部门"
          check-strictly
          default-expand-all
        />
      </el-form-item>
      <el-form-item label="手机号" prop="phone">
        <el-input v-model="form.phone" placeholder="请输入手机号" />
      </el-form-item>
      <el-form-item label="邮箱" prop="email">
        <el-input v-model="form.email" placeholder="请输入邮箱" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-radio-group v-model="form.status">
          <el-radio :value="1">启用</el-radio>
          <el-radio :value="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="角色" prop="roleId">
        <el-select v-model="form.roleId" placeholder="请选择角色">
          <el-option v-for="role in roleList" :key="role.id" :label="role.roleName" :value="role.id" />
        </el-select>
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
import { addUser, updateUser } from '@/api/system/user'
import { getDeptTree } from '@/api/system/dept'
import { getAllRoles } from '@/api/system/role'
import type { EntityId, SysUserDto, SysUserVo, SysDeptVo, SysRoleVo } from '@/types/system'

const props = defineProps<{
  visible: boolean
  isEdit: boolean
  formData: SysUserVo | null
}>()
const emit = defineEmits<{
  'update:visible': [val: boolean]
  success: []
}>()

const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const deptTree = ref<SysDeptVo[]>([])
const roleList = ref<SysRoleVo[]>([])

const form = reactive({
  username: '',
  password: '',
  realName: '',
  deptId: undefined as EntityId | undefined,
  phone: '',
  email: '',
  status: 1,
  roleId: undefined as EntityId | undefined,
})

const rules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  deptId: [{ required: true, message: '请选择部门', trigger: 'change' }],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }],
  email: [{ type: 'email', message: '请输入正确的邮箱', trigger: 'blur' }],
}

watch(() => props.visible, (val) => {
  dialogVisible.value = val
  if (val) {
    loadDeptTree()
    loadRoleList()
    if (props.isEdit && props.formData) {
      Object.assign(form, {
        username: props.formData.username,
        password: '',
        realName: props.formData.realName,
        deptId: props.formData.deptId,
        phone: props.formData.phone,
        email: props.formData.email,
        status: props.formData.status,
        roleId: props.formData.roleIds?.[0],
      })
    }
  }
})
watch(dialogVisible, (val) => { emit('update:visible', val) })

async function loadDeptTree() {
  const res = await getDeptTree()
  deptTree.value = res.data
}

async function loadRoleList() {
  const res = await getAllRoles()
  roleList.value = res.data
}

async function handleSubmit() {
  await formRef.value?.validate()
  submitLoading.value = true
  try {
    const dto: SysUserDto = {
      username: form.username,
      password: form.password,
      realName: form.realName,
      deptId: form.deptId!,
      phone: form.phone,
      email: form.email,
      status: form.status,
      roleIds: form.roleId !== undefined ? [form.roleId] : [],
    }
    if (props.isEdit && props.formData) {
      await updateUser(props.formData.id, dto)
      ElMessage.success('编辑成功')
    } else {
      await addUser(dto)
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
  Object.assign(form, { username: '', password: '', realName: '', deptId: undefined, phone: '', email: '', status: 1, roleId: undefined })
}
</script>

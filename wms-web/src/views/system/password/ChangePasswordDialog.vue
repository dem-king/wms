<template>
  <el-dialog v-model="dialogVisible" title="修改密码" width="460px" @close="handleClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="原密码" prop="oldPassword">
        <el-input v-model="form.oldPassword" type="password" placeholder="请输入原密码" show-password />
      </el-form-item>
      <el-form-item label="新密码" prop="newPassword">
        <el-input v-model="form.newPassword" type="password" placeholder="请输入新密码" show-password />
      </el-form-item>
      <el-form-item label="确认新密码" prop="confirmPassword">
        <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取 消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确 定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { changePassword, getRsaPublicKey } from '@/api/system/auth'
import { rsaEncrypt } from '@/utils/crypto'
import { clearAuth } from '@/utils/auth'
import router from '@/router'

const props = defineProps<{ visible: boolean }>()
const emit = defineEmits<{ 'update:visible': [val: boolean]; success: [] }>()

const dialogVisible = ref(false)
const formRef = ref<FormInstance>()
const loading = ref(false)

const form = reactive({ oldPassword: '', newPassword: '', confirmPassword: '' })

const validateConfirm = (_rule: unknown, value: string, callback: (err?: Error) => void) => {
  if (value !== form.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules: FormRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).{8,20}$/, message: '密码长度8-20位，需包含大小写字母和数字', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

watch(() => props.visible, (val) => { dialogVisible.value = val })
watch(dialogVisible, (val) => { emit('update:visible', val) })

async function handleSubmit() {
  await formRef.value?.validate()
  loading.value = true
  try {
    const rsaRes = await getRsaPublicKey()
    const publicKey = rsaRes.data.publicKey
    const encryptedOldPassword = rsaEncrypt(form.oldPassword, publicKey)
    const encryptedNewPassword = rsaEncrypt(form.newPassword, publicKey)
    await changePassword({ encryptedOldPassword, encryptedNewPassword })
    ElMessage.success('密码修改成功，请重新登录')
    clearAuth()
    router.push('/login')
    emit('success')
  } catch (e: unknown) {
    const err = e as { message?: string }
    const msg = err.message || ''
    if (msg.includes('原密码错误') || msg.includes('OLD_INCORRECT')) {
      ElMessage.error('原密码错误')
      form.oldPassword = ''
    } else if (msg.includes('强度') || msg.includes('STRENGTH')) {
      ElMessage.error('新密码不符合强度要求')
    } else if (msg.includes('相同') || msg.includes('SAME')) {
      ElMessage.error('新密码不能与原密码相同')
    }
  } finally {
    loading.value = false
  }
}

function handleClose() {
  dialogVisible.value = false
  formRef.value?.resetFields()
}
</script>

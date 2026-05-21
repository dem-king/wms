<template>
  <el-dialog v-model="dialogVisible" title="绑定物品" width="500px" @close="handleClose">
    <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="物品" prop="itemId">
        <el-select
          v-model="form.itemId"
          filterable
          remote
          reserve-keyword
          placeholder="请搜索物品"
          :remote-method="handleItemSearch"
          :loading="itemSearchLoading"
          style="width: 100%"
        >
          <el-option v-for="item in itemOptions" :key="item.id" :label="`${item.itemName}(${item.itemCode})`" :value="item.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="绑定类型" prop="bindType">
        <el-radio-group v-model="form.bindType">
          <el-radio :value="1">单品对应</el-radio>
          <el-radio :value="2">批次对应</el-radio>
        </el-radio-group>
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { ref, reactive, computed } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { searchItem } from '@/api/item/item'
import type { WmsItemVo } from '@/types/item'
import { bindLabel } from '@/api/item/label'

const props = defineProps<{
  /** 标签ID */
  labelId: number
  /** 弹窗可见性 */
  visible: boolean
}>()

const emit = defineEmits<{
  (e: 'update:visible', value: boolean): void
  (e: 'success'): void
}>()

const dialogVisible = computed({
  get: () => props.visible,
  set: (val) => emit('update:visible', val),
})

const formRef = ref<FormInstance>()
const submitLoading = ref(false)
const itemSearchLoading = ref(false)
const itemOptions = ref<WmsItemVo[]>([])

const form = reactive({
  itemId: undefined as number | undefined,
  bindType: 1,
})

const rules: FormRules = {
  itemId: [{ required: true, message: '请选择物品', trigger: 'change' }],
  bindType: [{ required: true, message: '请选择绑定类型', trigger: 'change' }],
}

/** 远程搜索物品 */
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

async function handleSubmit() {
  await formRef.value?.validate()
  if (!form.itemId) return

  submitLoading.value = true
  try {
    await bindLabel(props.labelId, { itemId: form.itemId, bindType: form.bindType })
    ElMessage.success('绑定成功')
    handleClose()
    emit('success')
  } finally {
    submitLoading.value = false
  }
}

function handleClose() {
  dialogVisible.value = false
  formRef.value?.resetFields()
  Object.assign(form, { itemId: undefined, bindType: 1 })
}
</script>

<style scoped lang="scss"></style>

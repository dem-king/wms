<script setup lang="ts">
/**
 * 布局元素属性编辑面板
 * 显示选中元素的位置、尺寸、颜色、文字等可编辑属性
 */
import { computed, watch, ref } from 'vue'
import type { EntityId } from '@/types/warehouse'
import type { LayoutElementVo, StyleData, ElementType } from '../types/layout-element'
import { parseStyleData, mergeStyle, ELEMENT_TYPES } from '../types/layout-element'

const props = defineProps<{
  element: LayoutElementVo | null
}>()

const emit = defineEmits<{
  (e: 'update-property', payload: { field: string; value: unknown }): void
  (e: 'update-style', payload: Partial<StyleData>): void
  (e: 'delete', elementId: EntityId): void
}>()

/** 当前编辑的样式数据 */
const currentStyle = ref<StyleData>({})

// 监听元素变化，更新样式
watch(
  () => props.element,
  (element) => {
    if (element) {
      currentStyle.value = mergeStyle(element.elementType, parseStyleData(element.styleData))
    }
  },
  { immediate: true },
)

/** 元素类型中文名 */
const elementTypeName = computed(() => {
  if (!props.element) return ''
  return ELEMENT_TYPES[props.element.elementType as ElementType] ?? props.element.elementType
})

/**
 * 更新位置属性
 */
function handlePositionChange(field: 'positionX' | 'positionY', value: number | undefined) {
  if (value !== undefined) {
    emit('update-property', { field, value })
  }
}

/**
 * 更新尺寸属性
 */
function handleSizeChange(field: 'layoutWidth' | 'layoutHeight', value: number | undefined) {
  if (value !== undefined) {
    emit('update-property', { field, value })
  }
}

/**
 * 更新旋转角度
 */
function handleRotationChange(value: number | undefined) {
  if (value !== undefined) {
    emit('update-property', { field: 'rotation', value })
  }
}

/**
 * 更新标注文本
 */
function handleLabelTextChange(value: string | null) {
  if (value !== null) {
    emit('update-property', { field: 'labelText', value })
  }
}

/**
 * 更新填充颜色
 */
function handleFillColorChange(value: string | null) {
  if (value !== null) {
    emit('update-style', { fillColor: value })
  }
}

/**
 * 更新描边颜色
 */
function handleStrokeColorChange(value: string | null) {
  if (value !== null) {
    emit('update-style', { strokeColor: value })
  }
}

/**
 * 删除元素
 */
function handleDelete() {
  if (props.element) {
    emit('delete', props.element.id)
  }
}
</script>

<template>
  <div v-if="element" class="element-property-panel">
    <div class="panel-header">
      <span class="panel-title">元素属性</span>
      <el-tag size="small">{{ elementTypeName }}</el-tag>
    </div>

    <el-divider />

    <!-- 基本信息 -->
    <div class="property-section">
      <div class="section-title">基本信息</div>
      <el-form label-width="70px" size="small">
        <el-form-item label="名称">
          <el-input :model-value="element.elementName" disabled />
        </el-form-item>
        <el-form-item label="编码">
          <el-input :model-value="element.elementCode" disabled />
        </el-form-item>
        <el-form-item v-if="element.labelText !== undefined" label="标注文本">
          <el-input
            :model-value="element.labelText"
            @update:model-value="handleLabelTextChange"
          />
        </el-form-item>
      </el-form>
    </div>

    <!-- 位置与尺寸 -->
    <div class="property-section">
      <div class="section-title">位置与尺寸</div>
      <el-form label-width="70px" size="small">
        <el-form-item label="X坐标">
          <el-input-number
            :model-value="element.positionX"
            :min="0"
            :max="10000"
            controls-position="right"
            @update:model-value="(v?: number) => handlePositionChange('positionX', v)"
          />
        </el-form-item>
        <el-form-item label="Y坐标">
          <el-input-number
            :model-value="element.positionY"
            :min="0"
            :max="10000"
            controls-position="right"
            @update:model-value="(v?: number) => handlePositionChange('positionY', v)"
          />
        </el-form-item>
        <el-form-item label="宽度">
          <el-input-number
            :model-value="element.layoutWidth"
            :min="1"
            :max="5000"
            controls-position="right"
            @update:model-value="(v?: number) => handleSizeChange('layoutWidth', v)"
          />
        </el-form-item>
        <el-form-item label="高度">
          <el-input-number
            :model-value="element.layoutHeight"
            :min="1"
            :max="5000"
            controls-position="right"
            @update:model-value="(v?: number) => handleSizeChange('layoutHeight', v)"
          />
        </el-form-item>
        <el-form-item label="旋转">
          <el-input-number
            :model-value="element.rotation"
            :min="0"
            :max="360"
            controls-position="right"
            @update:model-value="handleRotationChange"
          />
        </el-form-item>
      </el-form>
    </div>

    <!-- 样式 -->
    <div class="property-section">
      <div class="section-title">样式</div>
      <el-form label-width="70px" size="small">
        <el-form-item v-if="currentStyle.fillColor" label="填充色">
          <el-color-picker
            :model-value="currentStyle.fillColor"
            @update:model-value="handleFillColorChange"
          />
        </el-form-item>
        <el-form-item v-if="currentStyle.strokeColor" label="描边色">
          <el-color-picker
            :model-value="currentStyle.strokeColor"
            @update:model-value="handleStrokeColorChange"
          />
        </el-form-item>
      </el-form>
    </div>

    <!-- 操作 -->
    <div class="property-actions">
      <el-button type="danger" size="small" @click="handleDelete">删除元素</el-button>
    </div>
  </div>

  <div v-else class="element-property-panel empty">
    <el-empty description="请选中一个布局元素" :image-size="60" />
  </div>
</template>

<style scoped lang="scss">
.element-property-panel {
  padding: 12px;

  &.empty {
    padding: 24px 12px;
  }
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.panel-title {
  font-weight: 600;
  color: #303133;
}

.property-section {
  margin-bottom: 12px;
}

.section-title {
  margin-bottom: 8px;
  color: #909399;
  font-size: 13px;
}

.property-actions {
  margin-top: 16px;
  display: flex;
  justify-content: flex-end;
}
</style>
<script setup lang="ts">
/**
 * 2D俯视图组件
 * 使用Canvas 2D API渲染库房俯视图，支持编辑模式拖拽存放柜
 */
import { ref, watch, onMounted, onUnmounted } from 'vue'
import type { EntityId } from '@/types/warehouse'
import type { WarehouseVisualModel, WarehouseVisualCabinetNode } from '../visual-layout'
import type { LayoutElementVo } from '../types/layout-element'
import type { VisualSelectionState } from '../visual-state'

const props = defineProps<{
  /** 库房可视化模型 */
  visualModel: WarehouseVisualModel | null
  /** 选中状态 */
  selection: VisualSelectionState
  /** 是否编辑模式 */
  isEditMode: boolean
  /** 布局辅助元素列表 */
  layoutElements: LayoutElementVo[]
}>()

const emit = defineEmits<{
  (e: 'select-area', areaId: EntityId): void
  (e: 'select-cabinet', cabinetId: EntityId): void
  (e: 'update-cabinet-position', payload: { cabinetId: EntityId; positionX: number; positionY: number }): void
}>()

/** Canvas元素引用 */
const canvasRef = ref<HTMLCanvasElement | null>(null)
/** Canvas 2D上下文 */
let ctx: CanvasRenderingContext2D | null = null

/** 编辑模式拖拽状态 */
const dragState = ref<{
  isDragging: boolean
  cabinetId: EntityId | null
  offsetX: number
  offsetY: number
}>({
  isDragging: false,
  cabinetId: null,
  offsetX: 0,
  offsetY: 0,
})

/** 缩放比例 */
const scale = ref(1)

/** 区域颜色列表 */
const AREA_COLORS = ['#e6f7ff', '#f6ffed', '#fff7e6', '#f9f0ff', '#fff1f0', '#e6fffb']

/**
 * 绘制2D俯视图
 */
function draw(): void {
  if (!ctx || !props.visualModel) {
    return
  }

  const model = props.visualModel
  const canvas = canvasRef.value!
  const width = canvas.width
  const height = canvas.height

  // 清空画布
  ctx.clearRect(0, 0, width, height)

  // 绘制背景
  ctx.fillStyle = '#f5f5f5'
  ctx.fillRect(0, 0, width, height)

  // 计算缩放以适应画布
  const contentWidth = model.layoutWidth || model.viewport.width
  const contentHeight = model.layoutHeight || model.viewport.height
  scale.value = Math.min(
    (width - 40) / contentWidth,
    (height - 40) / contentHeight,
    1,
  )

  ctx.save()
  ctx.translate(20, 20)
  ctx.scale(scale.value, scale.value)

  // 绘制库房边界
  ctx.strokeStyle = '#d9d9d9'
  ctx.lineWidth = 1 / scale.value
  ctx.strokeRect(0, 0, contentWidth, contentHeight)

  // 绘制区域
  for (let i = 0; i < model.areas.length; i++) {
    const area = model.areas[i]
    const color = AREA_COLORS[i % AREA_COLORS.length]
    const isSelected = props.selection.selectedAreaId === area.id

    // 区域填充
    ctx.fillStyle = isSelected ? color : color
    ctx.globalAlpha = isSelected ? 0.6 : 0.3
    ctx.fillRect(area.x, area.y, area.width, area.height)
    ctx.globalAlpha = 1

    // 区域边框
    ctx.strokeStyle = isSelected ? '#1890ff' : '#91d5ff'
    ctx.lineWidth = isSelected ? 2 / scale.value : 1 / scale.value
    ctx.strokeRect(area.x, area.y, area.width, area.height)

    // 区域名称
    ctx.fillStyle = '#333333'
    ctx.font = `bold ${14 / scale.value}px sans-serif`
    ctx.fillText(area.areaName, area.x + 8, area.y + 20)
  }

  // 绘制存放柜
  const cabinets = Object.values(model.cabinets)
  for (const cabinet of cabinets) {
    const isSelected = props.selection.selectedCabinetId === cabinet.id
    drawCabinet(cabinet, isSelected)
  }

  // 绘制布局辅助元素（通道、标注等）
  drawLayoutElements()

  ctx.restore()
}

/**
 * 绘制存放柜矩形
 */
function drawCabinet(cabinet: WarehouseVisualCabinetNode, isSelected: boolean): void {
  if (!ctx) {
    return
  }

  // 存放柜填充
  ctx.fillStyle = isSelected ? '#ffa940' : '#1890ff'
  ctx.fillRect(cabinet.x, cabinet.y, cabinet.width, cabinet.height)

  // 存放柜边框
  ctx.strokeStyle = isSelected ? '#fa8c16' : '#096dd9'
  ctx.lineWidth = isSelected ? 2 / scale.value : 1 / scale.value
  ctx.strokeRect(cabinet.x, cabinet.y, cabinet.width, cabinet.height)

  // 存放柜编码
  ctx.fillStyle = '#ffffff'
  ctx.font = `${10 / scale.value}px sans-serif`
  ctx.textAlign = 'center'
  ctx.textBaseline = 'middle'
  ctx.fillText(
    cabinet.cabinetCode,
    cabinet.x + cabinet.width / 2,
    cabinet.y + cabinet.height / 2,
  )
  ctx.textAlign = 'left'
  ctx.textBaseline = 'alphabetic'
}

/**
 * 绘制布局辅助元素
 */
function drawLayoutElements(): void {
  if (!ctx) {
    return
  }

  for (const element of props.layoutElements) {
    switch (element.elementType) {
      case 'aisle':
        // 通道：虚线矩形
        ctx.save()
        ctx.setLineDash([6 / scale.value, 4 / scale.value])
        ctx.strokeStyle = '#91d5ff'
        ctx.lineWidth = 1 / scale.value
        ctx.strokeRect(element.positionX, element.positionY, element.layoutWidth, element.layoutHeight)
        ctx.setLineDash([])
        ctx.restore()

        // 通道名称
        if (element.labelText || element.elementName) {
          ctx.fillStyle = '#666666'
          ctx.font = `${10 / scale.value}px sans-serif`
          ctx.fillText(
            element.labelText || element.elementName,
            element.positionX + 4,
            element.positionY + 14,
          )
        }
        break

      case 'wall':
        // 墙壁：深色矩形
        ctx.fillStyle = '#4a4a4a'
        ctx.fillRect(element.positionX, element.positionY, element.layoutWidth, element.layoutHeight || 4)
        break

      case 'text':
        // 文字标注
        if (element.labelText || element.elementName) {
          ctx.fillStyle = '#333333'
          ctx.font = `${12 / scale.value}px sans-serif`
          ctx.fillText(
            element.labelText || element.elementName,
            element.positionX,
            element.positionY,
          )
        }
        break

      default:
        break
    }
  }
}

/**
 * 处理Canvas点击事件
 */
function handleCanvasClick(event: MouseEvent): void {
  if (!props.visualModel || !ctx) {
    return
  }

  const canvas = canvasRef.value!
  const rect = canvas.getBoundingClientRect()
  const x = (event.clientX - rect.left - 20) / scale.value
  const y = (event.clientY - rect.top - 20) / scale.value

  // 检测点击的存放柜
  const cabinets = Object.values(props.visualModel.cabinets)
  for (const cabinet of cabinets) {
    if (
      x >= cabinet.x && x <= cabinet.x + cabinet.width &&
      y >= cabinet.y && y <= cabinet.y + cabinet.height
    ) {
      emit('select-cabinet', cabinet.id as EntityId)
      return
    }
  }

  // 检测点击的区域
  for (const area of props.visualModel.areas) {
    if (
      x >= area.x && x <= area.x + area.width &&
      y >= area.y && y <= area.y + area.height
    ) {
      emit('select-area', area.id as EntityId)
      return
    }
  }
}

/**
 * 处理鼠标按下（编辑模式拖拽开始）
 */
function handleMouseDown(event: MouseEvent): void {
  if (!props.isEditMode || !props.visualModel) {
    return
  }

  const canvas = canvasRef.value!
  const rect = canvas.getBoundingClientRect()
  const x = (event.clientX - rect.left - 20) / scale.value
  const y = (event.clientY - rect.top - 20) / scale.value

  // 检测拖拽的存放柜
  const cabinets = Object.values(props.visualModel.cabinets)
  for (const cabinet of cabinets) {
    if (
      x >= cabinet.x && x <= cabinet.x + cabinet.width &&
      y >= cabinet.y && y <= cabinet.y + cabinet.height
    ) {
      dragState.value = {
        isDragging: true,
        cabinetId: cabinet.id as EntityId,
        offsetX: x - cabinet.x,
        offsetY: y - cabinet.y,
      }
      return
    }
  }
}

/**
 * 处理鼠标移动（编辑模式拖拽中）
 */
function handleMouseMove(event: MouseEvent): void {
  if (!dragState.value.isDragging || !props.visualModel) {
    return
  }

  const canvas = canvasRef.value!
  const rect = canvas.getBoundingClientRect()
  const x = (event.clientX - rect.left - 20) / scale.value
  const y = (event.clientY - rect.top - 20) / scale.value

  const newPositionX = x - dragState.value.offsetX
  const newPositionY = y - dragState.value.offsetY

  emit('update-cabinet-position', {
    cabinetId: dragState.value.cabinetId!,
    positionX: newPositionX,
    positionY: newPositionY,
  })
}

/**
 * 处理鼠标松开（编辑模式拖拽结束）
 */
function handleMouseUp(): void {
  dragState.value = {
    isDragging: false,
    cabinetId: null,
    offsetX: 0,
    offsetY: 0,
  }
}

// 监听模型和选中状态变化，重绘
watch(
  () => [props.visualModel, props.selection, props.layoutElements],
  () => {
    draw()
  },
  { deep: true },
)

onMounted(() => {
  const canvas = canvasRef.value
  if (canvas) {
    ctx = canvas.getContext('2d')
    // 设置Canvas尺寸
    resizeCanvas()
    draw()
  }

  window.addEventListener('mousemove', handleMouseMove)
  window.addEventListener('mouseup', handleMouseUp)
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('mousemove', handleMouseMove)
  window.removeEventListener('mouseup', handleMouseUp)
  window.removeEventListener('resize', handleResize)
})

/**
 * 处理窗口resize，重设Canvas尺寸并重绘
 */
function handleResize(): void {
  resizeCanvas()
  draw()
}

/**
 * 根据父容器尺寸设置Canvas像素尺寸
 */
function resizeCanvas(): void {
  const canvas = canvasRef.value
  if (!canvas) {
    return
  }
  const parent = canvas.parentElement
  if (parent) {
    canvas.width = parent.clientWidth
    canvas.height = parent.clientHeight
  }
}
</script>

<template>
  <div class="warehouse-2d-map">
    <canvas
      ref="canvasRef"
      class="map-canvas"
      @click="handleCanvasClick"
      @mousedown="handleMouseDown"
    />
    <div v-if="isEditMode" class="edit-mode-badge">
      编辑模式
    </div>
  </div>
</template>

<style scoped lang="scss">
.warehouse-2d-map {
  position: relative;
  width: 100%;
  height: 600px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
  background: #f5f5f5;
}

.map-canvas {
  width: 100%;
  height: 100%;
  cursor: crosshair;
}

.edit-mode-badge {
  position: absolute;
  top: 8px;
  right: 8px;
  padding: 4px 8px;
  background: #fa8c16;
  color: #fff;
  font-size: 12px;
  border-radius: 4px;
}
</style>
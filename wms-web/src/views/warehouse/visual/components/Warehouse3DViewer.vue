<script setup lang="ts">
/**
 * 3D库房视图主组件
 * 替代旧VisualStage.vue，使用Three.js渲染3D库房场景
 * 提供交互式3D导航、点击选中、双击进入柜子详情等功能
 */
import { ref, watch, onUnmounted, toRef } from 'vue'
import * as THREE from 'three'
import type { EntityId } from '@/types/warehouse'
import type { WarehouseVisualModel } from '../visual-layout'
import type { LayoutElementVo } from '../types/layout-element'
import type { VisualSelectionState } from '../visual-state'
import { isWebGL2Available } from '../types/three-visual'
import { useThreeScene } from '../composables/useThreeScene'
import { useRaycaster } from '../composables/useRaycaster'
import { useCameraAnimation } from '../composables/useCameraAnimation'

const props = defineProps<{
  /** 库房可视化模型 */
  visualModel: WarehouseVisualModel | null
  /** 选中状态 */
  selection: VisualSelectionState
  /** 是否编辑模式 */
  isEditMode: boolean
  /** 布局辅助元素列表 */
  layoutElements: LayoutElementVo[]
  /** 底图URL */
  backgroundUrl?: string
}>()

const emit = defineEmits<{
  (e: 'select-area', areaId: EntityId): void
  (e: 'select-cabinet', cabinetId: EntityId): void
  (e: 'select-bin', payload: { cabinetId: EntityId; binId: EntityId }): void
  (e: 'enter-cabinet-detail', cabinetId: EntityId): void
  (e: 'exit-cabinet-detail'): void
}>()

/** 3D渲染容器DOM引用 */
const containerRef = ref<HTMLElement | null>(null)

/** WebGL是否可用 */
const webglAvailable = ref(isWebGL2Available())

/** 保存推近前的相机位置，用于退回全景 */
const savedCameraPosition = ref<{ position: { x: number; y: number; z: number }; target: { x: number; y: number; z: number } } | null>(null)

// 初始化Three.js场景管理
const {
  camera,
  controls,
  sceneResult,
  isReady,
  highlightCabinetById,
  highlightAreaById,
  showCabinetBinGrid,
  hideCabinetBinGrid,
} = useThreeScene({
  containerRef,
  visualModel: toRef(props, 'visualModel'),
  layoutElements: toRef(props, 'layoutElements'),
  backgroundUrl: toRef(props, 'backgroundUrl'),
})

// 初始化射线拾取
const {
  pickCabinet,
  pickBin,
  pickArea,
  pickGround,
  createClickHandlers,
} = useRaycaster()

// 相机动画：延迟初始化，在camera就绪后才创建
// useCameraAnimation内部注册了onUnmounted清理GSAP动画
const cameraAnimation = ref<ReturnType<typeof useCameraAnimation> | null>(null)

// 当场景就绪后，创建相机动画实例
watch(isReady, (ready) => {
  if (ready && camera.value) {
    cameraAnimation.value = useCameraAnimation(camera.value)
  }
}, { immediate: true })

/**
 * 处理鼠标单击事件
 * 射线拾取3D场景中的对象，触发选中事件
 */
function handleSingleClick(event: MouseEvent): void {
  if (!sceneResult.value || !isReady.value || !camera.value || (cameraAnimation.value?.isAnimating ?? false)) {
    return
  }

  const container = containerRef.value
  if (!container) {
    return
  }

  // 优先检测库位（柜子详情视图时）
  const binResult = pickBin(event, sceneResult.value, camera.value, container)
  if (binResult) {
    emit('select-bin', binResult)
    return
  }

  // 检测存放柜
  const cabinetId = pickCabinet(event, sceneResult.value, camera.value, container)
  if (cabinetId) {
    emit('select-cabinet', cabinetId)
    return
  }

  // 检测区域
  const areaId = pickArea(event, sceneResult.value, camera.value, container)
  if (areaId) {
    emit('select-area', areaId)
    return
  }

  // 检测地面空白处 → 取消选中
  const isGround = pickGround(event, sceneResult.value, camera.value, container)
  if (isGround) {
    // 如果在柜子详情视图，先退出
    if (props.selection.viewMode === 'detail') {
      emit('exit-cabinet-detail')
    }
  }
}

/**
 * 处理鼠标双击事件
 * 双击存放柜时进入柜子详情视图
 */
function handleDoubleClick(event: MouseEvent): void {
  if (!sceneResult.value || !isReady.value || !camera.value || !controls.value || (cameraAnimation.value?.isAnimating ?? false)) {
    return
  }

  const container = containerRef.value
  if (!container) {
    return
  }

  // 检测存放柜
  const cabinetId = pickCabinet(event, sceneResult.value, camera.value, container)
  if (cabinetId) {
    // 保存当前相机位置，用于退回全景
    savedCameraPosition.value = {
      position: { x: camera.value.position.x, y: camera.value.position.y, z: camera.value.position.z },
      target: { x: controls.value.target.x, y: controls.value.target.y, z: controls.value.target.z },
    }

    // 触发进入柜子详情视图
    emit('enter-cabinet-detail', cabinetId)
  }
}

// 创建单击/双击事件处理器
const { handleClick, handleDblClick, cleanup: cleanupClickHandlers } = createClickHandlers({
  onClick: handleSingleClick,
  onDblClick: handleDoubleClick,
})

/**
 * 切换到预设视角
 * @param preset 预设视角名称
 */
function setPresetView(preset: 'bird-eye' | 'front' | 'side' | 'isometric'): void {
  if (!props.visualModel || !isReady.value || !cameraAnimation.value || !controls.value) {
    return
  }
  cameraAnimation.value.animateToPreset(
    preset,
    props.visualModel.layoutWidth,
    props.visualModel.layoutHeight,
    controls.value,
  )
}

/**
 * 退回全景视角
 */
function returnToOverview(): void {
  if (!savedCameraPosition.value || !isReady.value || !cameraAnimation.value || !controls.value) {
    return
  }

  const saved = savedCameraPosition.value
  const savedPos = new THREE.Vector3(saved.position.x, saved.position.y, saved.position.z)
  const savedTarget = new THREE.Vector3(saved.target.x, saved.target.y, saved.target.z)
  cameraAnimation.value.animateToOverview(savedPos, savedTarget, controls.value)

  hideCabinetBinGrid()
  savedCameraPosition.value = null
}

// 监听选中状态变化，更新高亮
watch(() => props.selection.selectedCabinetId, (newCabinetId) => {
  highlightCabinetById(newCabinetId ?? null)
})

watch(() => props.selection.selectedAreaId, (newAreaId) => {
  highlightAreaById(newAreaId ?? null)
})

// 监听detailCabinetId变化，显示/隐藏库位网格
watch(() => props.selection.detailCabinetId, (newDetailId) => {
  if (newDetailId) {
    showCabinetBinGrid(newDetailId)
  } else {
    hideCabinetBinGrid()
  }
})

// 暴露方法给父组件
defineExpose({
  setPresetView,
  returnToOverview,
  isReady,
})

onUnmounted(() => {
  cleanupClickHandlers()
})
</script>

<template>
  <div class="warehouse-3d-viewer">
    <!-- WebGL不可用提示 -->
    <div v-if="!webglAvailable" class="webgl-fallback">
      <el-empty description="您的浏览器不支持3D渲染，请使用Chrome 90+或Edge 90+等现代浏览器">
        <template #image>
          <el-icon :size="64"><Monitor /></el-icon>
        </template>
      </el-empty>
    </div>

    <!-- 3D渲染容器 -->
    <div
      v-else
      ref="containerRef"
      class="three-container"
      @click="handleClick"
      @dblclick="handleDblClick"
    />

    <!-- 场景未就绪提示 -->
    <div v-if="webglAvailable && !isReady && visualModel" class="loading-overlay">
      <el-icon class="is-loading" :size="32"><Loading /></el-icon>
      <span>正在加载3D场景...</span>
    </div>
  </div>
</template>

<script lang="ts">
import { Monitor, Loading } from '@element-plus/icons-vue'
export default {
  components: { Monitor, Loading },
}
</script>

<style scoped lang="scss">
.warehouse-3d-viewer {
  position: relative;
  width: 100%;
  height: 600px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  overflow: hidden;
  background: #f5f5f5;
}

.three-container {
  width: 100%;
  height: 100%;
  cursor: grab;

  &:active {
    cursor: grabbing;
  }
}

.webgl-fallback {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  background: rgba(255, 255, 255, 0.8);
  color: #909399;
  font-size: 14px;
}
</style>

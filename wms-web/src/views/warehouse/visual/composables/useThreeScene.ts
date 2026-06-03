/**
 * Three.js场景生命周期管理composable
 * 负责初始化/销毁/resize/渲染循环/场景重建
 * 使用ShallowRef包裹Three.js对象，避免Vue深度响应式代理
 */

import { onMounted, onUnmounted, ref, shallowRef, type Ref, type ShallowRef, watch, type MaybeRefOrGetter, toValue } from 'vue'
import * as THREE from 'three'
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js'
import type { EntityId } from '@/types/warehouse'
import type { WarehouseVisualModel } from '../visual-layout'
import type { LayoutElementVo } from '../types/layout-element'
import type { WarehouseSceneResult, SceneConfig } from '../types/three-visual'
import { DEFAULT_SCENE_CONFIG, isWebGL2Available } from '../types/three-visual'
import {
  buildWarehouseScene,
  buildCabinetBinGrid,
  computeInitialCameraPosition,
  disposeGroup,
} from '../scene/buildWarehouseScene'
import { highlightCabinet } from '../scene/createCabinets'
import { highlightArea } from '../scene/createAreas'
import { showBinGrid, hideBinGrid } from '../scene/createBins'

/** 相机FOV（度） */
const CAMERA_FOV = 45

/** 相机近裁面 */
const CAMERA_NEAR = 0.1

/** 渲染器像素比上限（避免超高分屏性能问题） */
const MAX_PIXEL_RATIO = 2

/**
 * useThreeScene选项
 */
export interface UseThreeSceneOptions {
  /** 容器DOM元素引用 */
  containerRef: Ref<HTMLElement | null>
  /** 库房可视化模型 */
  visualModel: MaybeRefOrGetter<WarehouseVisualModel | null>
  /** 布局辅助元素列表 */
  layoutElements: MaybeRefOrGetter<LayoutElementVo[]>
  /** 场景配置 */
  sceneConfig?: SceneConfig
  /** 底图URL */
  backgroundUrl?: MaybeRefOrGetter<string | undefined>
}

/**
 * useThreeScene返回接口
 */
export interface UseThreeSceneReturn {
  /** Three.js Scene */
  scene: ShallowRef<THREE.Scene | null>
  /** Three.js PerspectiveCamera */
  camera: ShallowRef<THREE.PerspectiveCamera | null>
  /** Three.js WebGLRenderer */
  renderer: ShallowRef<THREE.WebGLRenderer | null>
  /** OrbitControls */
  controls: ShallowRef<OrbitControls | null>
  /** 场景构建结果 */
  sceneResult: ShallowRef<WarehouseSceneResult | null>
  /** WebGL是否可用 */
  isReady: Ref<boolean>
  /** 重建场景 */
  rebuildScene: () => void
  /** 高亮存放柜 */
  highlightCabinetById: (cabinetId: EntityId | null) => void
  /** 高亮区域 */
  highlightAreaById: (areaId: EntityId | null) => void
  /** 显示库位网格 */
  showCabinetBinGrid: (cabinetId: EntityId) => void
  /** 隐藏库位网格 */
  hideCabinetBinGrid: () => void
  /** 释放所有资源 */
  dispose: () => void
}

/**
 * Three.js场景生命周期管理composable
 * @param options 选项
 * @returns 场景管理接口
 */
export function useThreeScene(options: UseThreeSceneOptions): UseThreeSceneReturn {
  const { containerRef, sceneConfig = DEFAULT_SCENE_CONFIG } = options

  // 使用ShallowRef避免Vue深度响应式代理
  // 延迟创建实际对象，init()中才真正实例化，避免创建后立即被替换导致泄漏
  const scene = shallowRef<THREE.Scene | null>(null)
  const camera = shallowRef<THREE.PerspectiveCamera | null>(null)
  const renderer = shallowRef<THREE.WebGLRenderer | null>(null)
  const controls = shallowRef<OrbitControls | null>(null)
  const sceneResult = shallowRef<WarehouseSceneResult | null>(null)

  const isReady = ref(false)
  let animationFrameId: number | null = null
  let needsRender = true

  /**
   * 初始化Three.js场景
   */
  function init(): boolean {
    // WebGL2兼容性检测
    if (!isWebGL2Available()) {
      console.warn('[useThreeScene] 当前浏览器不支持WebGL2')
      isReady.value = false
      return false
    }

    const container = containerRef.value
    if (!container) {
      return false
    }

    const model = toValue(options.visualModel)
    if (!model) {
      return false
    }

    const { layoutWidth, layoutHeight } = model

    // 创建Scene
    scene.value = new THREE.Scene()
    // 设置场景背景色
    scene.value.background = new THREE.Color(0xf5f5f5)

    // 创建PerspectiveCamera
    const far = Math.max(layoutWidth, layoutHeight) * 5
    const { position: camPos, lookAt: camLookAt } = computeInitialCameraPosition(layoutWidth, layoutHeight)
    camera.value = new THREE.PerspectiveCamera(CAMERA_FOV, container.clientWidth / container.clientHeight, CAMERA_NEAR, far)
    camera.value.position.copy(camPos)
    camera.value.lookAt(camLookAt)

    // 创建WebGLRenderer
    renderer.value = new THREE.WebGLRenderer({
      antialias: true,
      alpha: false,
    })
    renderer.value.setSize(container.clientWidth, container.clientHeight)
    renderer.value.setPixelRatio(Math.min(window.devicePixelRatio, MAX_PIXEL_RATIO))
    renderer.value.shadowMap.enabled = true
    renderer.value.shadowMap.type = THREE.PCFSoftShadowMap
    renderer.value.outputColorSpace = THREE.SRGBColorSpace
    container.appendChild(renderer.value.domElement)

    // 创建OrbitControls
    controls.value = new OrbitControls(camera.value, renderer.value.domElement)
    controls.value.target.copy(camLookAt)
    controls.value.enableDamping = true
    controls.value.dampingFactor = 0.08
    controls.value.minDistance = 5
    controls.value.maxDistance = Math.max(layoutWidth, layoutHeight) * 2
    controls.value.minPolarAngle = 0
    controls.value.maxPolarAngle = Math.PI / 2
    controls.value.update()

    // 构建场景内容
    const elements = toValue(options.layoutElements)
    const bgUrl = toValue(options.backgroundUrl)
    sceneResult.value = buildWarehouseScene(scene.value, model, elements, sceneConfig, bgUrl)

    // 标记就绪
    isReady.value = true
    needsRender = true

    // 启动渲染循环
    startRenderLoop()

    return true
  }

  /**
   * 渲染循环
   */
  function startRenderLoop(): void {
    function animate(): void {
      animationFrameId = requestAnimationFrame(animate)

      // OrbitControls阻尼需要持续更新
      if (controls.value?.enableDamping) {
        controls.value.update()
        needsRender = true
      }

      if (!needsRender || !renderer.value || !scene.value || !camera.value) {
        return
      }

      renderer.value.render(scene.value, camera.value)
      needsRender = false
    }

    animate()
  }

  /**
   * 处理窗口resize
   */
  function handleResize(): void {
    const container = containerRef.value
    if (!container || !isReady.value || !camera.value || !renderer.value) {
      return
    }

    const width = container.clientWidth
    const height = container.clientHeight

    camera.value.aspect = width / height
    camera.value.updateProjectionMatrix()
    renderer.value.setSize(width, height)
    needsRender = true
  }

  /**
   * 重建场景（数据变化时调用）
   */
  function rebuildScene(): void {
    const model = toValue(options.visualModel)
    if (!model || !isReady.value || !scene.value) {
      return
    }

    const elements = toValue(options.layoutElements)
    const bgUrl = toValue(options.backgroundUrl)
    sceneResult.value = buildWarehouseScene(scene.value, model, elements, sceneConfig, bgUrl)
    needsRender = true
  }

  /**
   * 高亮存放柜
   */
  function highlightCabinetById(cabinetId: EntityId | null): void {
    if (!sceneResult.value) {
      return
    }
    highlightCabinet(
      sceneResult.value.cabinetInstancedMeshes,
      sceneResult.value.cabinetInstanceMaps,
      cabinetId,
    )
    needsRender = true
  }

  /**
   * 高亮区域
   */
  function highlightAreaById(areaId: EntityId | null): void {
    if (!scene.value) {
      return
    }
    const areasObject = scene.value.getObjectByName('areas')
    if (!areasObject || !(areasObject instanceof THREE.Group)) {
      return
    }
    highlightArea(areasObject, areaId, !!areaId)
    needsRender = true
  }

  /**
   * 显示指定柜子的库位网格
   */
  function showCabinetBinGrid(cabinetId: EntityId): void {
    const model = toValue(options.visualModel)
    if (!model || !sceneResult.value || !scene.value) {
      return
    }

    const result = buildCabinetBinGrid(
      scene.value,
      cabinetId,
      model,
      model.layoutWidth,
      model.layoutHeight,
      sceneConfig,
    )

    if (result) {
      sceneResult.value = {
        ...sceneResult.value,
        binInstanceMap: result.binInstanceMap,
        binInstancedMesh: result.binInstancedMesh,
      }
      showBinGrid(result.binInstancedMesh)
    }

    needsRender = true
  }

  /**
   * 隐藏库位网格
   */
  function hideCabinetBinGrid(): void {
    if (!sceneResult.value) {
      return
    }
    hideBinGrid(sceneResult.value.binInstancedMesh)
    needsRender = true
  }

  /**
   * 释放所有Three.js资源
   */
  function dispose(): void {
    // 停止渲染循环
    if (animationFrameId !== null) {
      cancelAnimationFrame(animationFrameId)
      animationFrameId = null
    }

    // 释放OrbitControls
    if (controls.value && typeof controls.value.dispose === 'function') {
      controls.value.dispose()
    }

    // 释放场景资源
    if (scene.value) {
      disposeGroup(scene.value)
    }

    // 释放Renderer
    if (renderer.value) {
      renderer.value.dispose()
      // 从DOM移除canvas
      const container = containerRef.value
      if (container && renderer.value.domElement.parentNode === container) {
        container.removeChild(renderer.value.domElement)
      }
    }

    isReady.value = false
    sceneResult.value = null
    scene.value = null
    camera.value = null
    renderer.value = null
    controls.value = null
  }

  // 监听visualModel变化，重建场景
  watch(() => toValue(options.visualModel), (newModel) => {
    if (newModel && isReady.value) {
      // 重新初始化（因为布局尺寸可能变化，需要重建相机等）
      dispose()
      init()
    }
  })

  // 监听layoutElements变化
  watch(() => toValue(options.layoutElements), () => {
    if (isReady.value) {
      rebuildScene()
    }
  })

  // 生命周期
  onMounted(() => {
    window.addEventListener('resize', handleResize)
    init()
  })

  onUnmounted(() => {
    window.removeEventListener('resize', handleResize)
    dispose()
  })

  return {
    scene,
    camera,
    renderer,
    controls,
    sceneResult,
    isReady,
    rebuildScene,
    highlightCabinetById,
    highlightAreaById,
    showCabinetBinGrid,
    hideCabinetBinGrid,
    dispose,
  }
}

/**
 * 3D库房场景构建主入口
 * 协调调用各create函数，创建光照，将所有Group添加到Scene
 */

import * as THREE from 'three'
import type { EntityId } from '@/types/warehouse'
import type { WarehouseVisualModel } from '../visual-layout'
import type { LayoutElementVo } from '../types/layout-element'
import type { WarehouseSceneResult, SceneConfig } from '../types/three-visual'
import { DEFAULT_SCENE_CONFIG } from '../types/three-visual'
import { createGround } from './createGround'
import { createWalls } from './createWalls'
import { createAreas } from './createAreas'
import { createCabinets } from './createCabinets'
import { createBins } from './createBins'
import { createAisles } from './createAisles'
import { createAnnotations } from './createAnnotations'

/** 环境光强度 */
const AMBIENT_LIGHT_INTENSITY = 0.4

/** 方向光强度 */
const DIRECTIONAL_LIGHT_INTENSITY = 0.8

/** 阴影贴图尺寸 */
const SHADOW_MAP_SIZE = 2048

/**
 * 构建3D库房场景
 * @param scene Three.js Scene对象
 * @param visualModel 库房可视化模型
 * @param layoutElements 布局辅助元素列表
 * @param sceneConfig 场景配置（可选，使用默认值）
 * @param backgroundUrl 底图URL（可选）
 * @returns 场景构建结果（包含各种映射和引用）
 */
export function buildWarehouseScene(
  scene: THREE.Scene,
  visualModel: WarehouseVisualModel,
  layoutElements: LayoutElementVo[],
  sceneConfig: SceneConfig = DEFAULT_SCENE_CONFIG,
  backgroundUrl?: string,
): WarehouseSceneResult {
  // 清空场景（保留环境，重新构建内容）
  clearScene(scene)

  const { layoutWidth, layoutHeight } = visualModel

  // 创建光照
  setupLighting(scene, layoutWidth, layoutHeight)

  // 创建地面
  const groundGroup = createGround(layoutWidth, layoutHeight, sceneConfig.gridDivisions, backgroundUrl)
  scene.add(groundGroup)

  // 获取地面Mesh引用
  const groundMesh = groundGroup.getObjectByName('groundPlane') as THREE.Mesh | null

  // 创建墙壁
  const wallsGroup = createWalls(layoutElements, layoutWidth, layoutHeight, sceneConfig)
  scene.add(wallsGroup)

  // 创建区域标记
  const { group: areasGroup, areaMeshes } = createAreas(visualModel.areas, layoutWidth, layoutHeight)
  scene.add(areasGroup)

  // 创建存放柜（InstancedMesh批量渲染）
  const {
    group: cabinetsGroup,
    cabinetInstanceMaps,
    cabinetNodeMap,
    cabinetInstancedMeshes,
  } = createCabinets(visualModel.cabinets, layoutWidth, layoutHeight, sceneConfig)
  scene.add(cabinetsGroup)

  // 创建库位（默认隐藏，柜子详情视图时显示）
  // 初始时无选中柜子，创建空的库位组
  const binsGroup = new THREE.Group()
  binsGroup.name = 'bins'
  scene.add(binsGroup)

  // 创建通道
  const aislesGroup = createAisles(layoutElements, layoutWidth, layoutHeight)
  scene.add(aislesGroup)

  // 创建标注
  const annotationsGroup = createAnnotations(layoutElements, layoutWidth, layoutHeight)
  scene.add(annotationsGroup)

  return {
    cabinetInstanceMaps,
    cabinetNodeMap,
    binInstanceMap: new Map(),
    areaMeshes,
    cabinetInstancedMeshes,
    binInstancedMesh: null,
    groundMesh,
  }
}

/**
 * 为指定存放柜创建库位网格
 * @param scene Three.js Scene对象
 * @param cabinetId 存放柜ID
 * @param visualModel 库房可视化模型
 * @param layoutWidth 库房布局宽度
 * @param layoutHeight 库房布局高度
 * @param sceneConfig 场景配置
 * @returns 库位构建结果
 */
export function buildCabinetBinGrid(
  scene: THREE.Scene,
  cabinetId: EntityId,
  visualModel: WarehouseVisualModel,
  layoutWidth: number,
  layoutHeight: number,
  sceneConfig: SceneConfig = DEFAULT_SCENE_CONFIG,
): { binInstanceMap: Map<number, { cabinetId: EntityId; binId: EntityId }>; binInstancedMesh: THREE.InstancedMesh | null } | null {
  const cabinet = visualModel.cabinets[cabinetId]
  if (!cabinet) {
    return null
  }

  // 移除旧的库位组
  const oldBinsGroup = scene.getObjectByName('bins')
  if (oldBinsGroup) {
    disposeGroup(oldBinsGroup)
    scene.remove(oldBinsGroup)
  }

  // 获取该存放柜的库位列表
  const bins = cabinet.binIds
    .map(binId => visualModel.bins[binId])
    .filter(Boolean)

  // 创建新的库位组
  const { group: binsGroup, binInstanceMap, binInstancedMesh } = createBins(
    cabinet,
    bins,
    layoutWidth,
    layoutHeight,
    sceneConfig,
  )
  scene.add(binsGroup)

  return { binInstanceMap, binInstancedMesh }
}

/**
 * 创建光照
 * @param scene 场景
 * @param layoutWidth 库房布局宽度
 * @param layoutHeight 库房布局高度
 */
function setupLighting(scene: THREE.Scene, layoutWidth: number, layoutHeight: number): void {
  // 环境光：基础照明，避免阴影面全黑
  const ambientLight = new THREE.AmbientLight(0xffffff, AMBIENT_LIGHT_INTENSITY)
  ambientLight.name = 'ambientLight'
  scene.add(ambientLight)

  // 方向光：模拟日光，产生阴影增强空间感
  const directionalLight = new THREE.DirectionalLight(0xffffff, DIRECTIONAL_LIGHT_INTENSITY)
  directionalLight.name = 'directionalLight'
  directionalLight.position.set(
    layoutWidth / 2,
    layoutHeight * 2,
    layoutHeight / 2,
  )
  directionalLight.castShadow = true

  // 配置阴影参数
  directionalLight.shadow.mapSize.width = SHADOW_MAP_SIZE
  directionalLight.shadow.mapSize.height = SHADOW_MAP_SIZE
  // 阴影相机覆盖场景范围
  const shadowRange = Math.max(layoutWidth, layoutHeight) * 1.5
  directionalLight.shadow.camera.left = -shadowRange
  directionalLight.shadow.camera.right = shadowRange
  directionalLight.shadow.camera.top = shadowRange
  directionalLight.shadow.camera.bottom = -shadowRange
  directionalLight.shadow.camera.near = 0.1
  directionalLight.shadow.camera.far = shadowRange * 3

  scene.add(directionalLight)
}

/**
 * 清空场景内容（移除所有子对象并dispose资源）
 * @param scene 场景
 */
function clearScene(scene: THREE.Scene): void {
  while (scene.children.length > 0) {
    const child = scene.children[0]
    if (child instanceof THREE.Group || child instanceof THREE.Mesh || child instanceof THREE.InstancedMesh) {
      disposeGroup(child)
    }
    scene.remove(child)
  }
}

/**
 * 递归释放Group及其子对象的资源
 * @param object 3D对象
 */
export function disposeGroup(object: THREE.Object3D): void {
  object.traverse((child) => {
    if (child instanceof THREE.Mesh || child instanceof THREE.InstancedMesh) {
      child.geometry?.dispose()
      const material = child.material
      if (Array.isArray(material)) {
        material.forEach(m => m.dispose())
      } else if (material) {
        material.dispose()
        // 释放材质的纹理
        if ('map' in material && material.map) {
          (material.map as THREE.Texture).dispose()
        }
      }
    }
    if (child instanceof THREE.LineSegments || child instanceof THREE.Line) {
      child.geometry?.dispose()
      const material = child.material
      if (Array.isArray(material)) {
        material.forEach(m => m.dispose())
      } else if (material) {
        material.dispose()
      }
    }
    if (child instanceof THREE.Sprite) {
      const material = child.material
      if (material && 'map' in material && material.map) {
        (material.map as THREE.Texture).dispose()
      }
      material?.dispose()
    }
  })
}

/**
 * 计算相机初始位置（鸟瞰45度角）
 * @param layoutWidth 库房布局宽度
 * @param layoutHeight 库房布局高度
 * @returns 相机位置和lookAt目标
 */
export function computeInitialCameraPosition(
  _layoutWidth: number,
  layoutHeight: number,
): { position: THREE.Vector3; lookAt: THREE.Vector3 } {
  return {
    position: new THREE.Vector3(0, layoutHeight * 0.8, layoutHeight * 0.6),
    lookAt: new THREE.Vector3(0, 0, 0),
  }
}

/**
 * 计算预设视角位置
 * @param preset 预设视角名称
 * @param layoutWidth 库房布局宽度
 * @param layoutHeight 库房布局高度
 * @returns 相机位置和lookAt目标
 */
export function computePresetCameraPosition(
  preset: 'bird-eye' | 'front' | 'side' | 'isometric',
  layoutWidth: number,
  layoutHeight: number,
): { position: THREE.Vector3; lookAt: THREE.Vector3 } {
  const W = layoutWidth
  const H = layoutHeight

  switch (preset) {
    case 'bird-eye':
      // 鸟瞰：正上方俯视
      return {
        position: new THREE.Vector3(0, H * 1.2, 0.01),
        lookAt: new THREE.Vector3(0, 0, 0),
      }
    case 'front':
      // 正面：正前方平视
      return {
        position: new THREE.Vector3(0, H * 0.3, H * 0.8),
        lookAt: new THREE.Vector3(0, 0, 0),
      }
    case 'side':
      // 侧面：右侧平视
      return {
        position: new THREE.Vector3(W * 0.8, H * 0.3, 0),
        lookAt: new THREE.Vector3(0, 0, 0),
      }
    case 'isometric':
      // 等轴测：45度等轴测
      return {
        position: new THREE.Vector3(W * 0.5, H * 0.8, H * 0.5),
        lookAt: new THREE.Vector3(0, 0, 0),
      }
    default:
      return computeInitialCameraPosition(W, H)
  }
}
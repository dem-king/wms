/**
 * 创建3D场景存放柜（核心性能优化：InstancedMesh批量渲染）
 * 所有存放柜使用InstancedMesh批量渲染，大幅减少draw call
 */

import * as THREE from 'three'
import type { EntityId } from '@/types/warehouse'
import type { WarehouseVisualCabinetNode } from '../visual-layout'
import type { CabinetNode3D, SceneConfig } from '../types/three-visual'
import { CABINET_DEFAULT_COLOR, toWorld3D } from '../types/three-visual'

/** 存放柜标签默认字号 */
const CABINET_LABEL_FONT_SIZE = 32

/** 存放柜标签颜色 */
const CABINET_LABEL_COLOR = '#ffffff'

/**
 * 创建存放柜组（InstancedMesh批量渲染）
 * @param cabinets 存放柜节点映射
 * @param layoutWidth 库房布局宽度
 * @param layoutHeight 库房布局高度
 * @param sceneConfig 场景配置
 * @returns 存放柜Group、实例映射、节点映射、InstancedMesh列表
 */
export function createCabinets(
  cabinets: Record<string, WarehouseVisualCabinetNode>,
  layoutWidth: number,
  layoutHeight: number,
  sceneConfig: SceneConfig,
): {
  group: THREE.Group
  /** 每个InstancedMesh的局部索引 → 存放柜ID映射（按InstancedMesh顺序存储） */
  cabinetInstanceMaps: Map<number, EntityId>[]
  cabinetNodeMap: Map<string, CabinetNode3D>
  cabinetInstancedMeshes: THREE.InstancedMesh[]
} {
  const cabinetsGroup = new THREE.Group()
  cabinetsGroup.name = 'cabinets'

  /** 每个InstancedMesh各自的局部索引→存放柜ID映射 */
  const cabinetInstanceMaps: Map<number, EntityId>[] = []
  const cabinetNodeMap = new Map<string, CabinetNode3D>()
  const cabinetInstancedMeshes: THREE.InstancedMesh[] = []

  const cabinetEntries = Object.values(cabinets)
  if (cabinetEntries.length === 0) {
    return { group: cabinetsGroup, cabinetInstanceMaps, cabinetNodeMap, cabinetInstancedMeshes }
  }

  // 按尺寸分组：相同尺寸的存放柜共享一个InstancedMesh
  const sizeGroups = groupBySize(cabinetEntries, sceneConfig)

  // 旋转轴向量（复用，避免循环内重复创建）
  const yAxis = new THREE.Vector3(0, 1, 0)

  for (const [sizeKey, groupCabinets] of sizeGroups) {
    const [cabWidth, cabHeight, cabDepth] = parseSizeKey(sizeKey)

    // 创建共享几何体和材质
    const geometry = new THREE.BoxGeometry(cabWidth, cabHeight, cabDepth)
    const material = new THREE.MeshStandardMaterial({
      color: CABINET_DEFAULT_COLOR,
      roughness: 0.5,
      metalness: 0.1,
    })

    // 创建InstancedMesh
    const instancedMesh = new THREE.InstancedMesh(geometry, material, groupCabinets.length)
    instancedMesh.name = `cabinetInstances_${sizeKey}`
    instancedMesh.castShadow = true
    instancedMesh.receiveShadow = true

    const matrix = new THREE.Matrix4()
    const position = new THREE.Vector3()
    const quaternion = new THREE.Quaternion()
    const scaleVec = new THREE.Vector3(1, 1, 1)
    const defaultColor = new THREE.Color(CABINET_DEFAULT_COLOR)

    // 当前InstancedMesh的局部索引映射
    const localInstanceMap = new Map<number, EntityId>()

    for (let i = 0; i < groupCabinets.length; i++) {
      const cabinet = groupCabinets[i]

      // 坐标映射：2D布局坐标 → 3D世界坐标
      const cabinetX = cabinet.x + cabinet.width / 2
      const cabinetY = cabinet.y + cabinet.height / 2
      const [x3d, z3d] = toWorld3D(cabinetX, cabinetY, layoutWidth, layoutHeight)
      const y3d = cabHeight / 2 // 底部贴地面，中心在半高处

      position.set(x3d, y3d, z3d)
      quaternion.setFromAxisAngle(yAxis, 0)
      matrix.compose(position, quaternion, scaleVec)

      // 设置实例变换矩阵
      instancedMesh.setMatrixAt(i, matrix)
      // 设置实例颜色
      instancedMesh.setColorAt(i, defaultColor)

      // 构建局部索引映射关系
      const cabinetId = cabinet.id as EntityId
      localInstanceMap.set(i, cabinetId)

      // 记录3D节点信息
      cabinetNodeMap.set(cabinet.id, {
        id: cabinetId,
        position: position.clone(),
        width: cabWidth,
        height: cabHeight,
        depth: cabDepth,
        rotation: 0,
        instanceIndex: i,
      })
    }

    // 标记需要更新
    instancedMesh.instanceMatrix.needsUpdate = true
    if (instancedMesh.instanceColor) {
      instancedMesh.instanceColor.needsUpdate = true
    }

    cabinetsGroup.add(instancedMesh)
    cabinetInstancedMeshes.push(instancedMesh)
    cabinetInstanceMaps.push(localInstanceMap)
  }

  // 创建存放柜编码标签组
  const labelsGroup = createCabinetLabels(cabinetEntries, layoutWidth, layoutHeight, sceneConfig)
  cabinetsGroup.add(labelsGroup)

  return { group: cabinetsGroup, cabinetInstanceMaps, cabinetNodeMap, cabinetInstancedMeshes }
}

/**
 * 按存放柜尺寸分组
 * 大多数场景下存放柜尺寸统一，仅需1个InstancedMesh
 */
function groupBySize(
  cabinets: WarehouseVisualCabinetNode[],
  sceneConfig: SceneConfig,
): Map<string, WarehouseVisualCabinetNode[]> {
  const groups = new Map<string, WarehouseVisualCabinetNode[]>()

  for (const cabinet of cabinets) {
    // 使用存放柜的布局宽高作为3D尺寸，无数据时使用默认值
    const width = cabinet.width / 60 || sceneConfig.cabinetDepth // 像素转场景单位
    const height = sceneConfig.cabinetHeight
    const depth = cabinet.height / 60 || sceneConfig.cabinetDepth
    const key = buildSizeKey(width, height, depth)

    if (!groups.has(key)) {
      groups.set(key, [])
    }
    groups.get(key)!.push(cabinet)
  }

  return groups
}

/**
 * 构建尺寸分组Key
 */
function buildSizeKey(width: number, height: number, depth: number): string {
  return `${width.toFixed(2)}_${height.toFixed(2)}_${depth.toFixed(2)}`
}

/**
 * 解析尺寸分组Key
 */
function parseSizeKey(key: string): [number, number, number] {
  const parts = key.split('_').map(Number)
  return [parts[0], parts[1], parts[2]]
}

/**
 * 创建存放柜编码标签组
 */
function createCabinetLabels(
  cabinets: WarehouseVisualCabinetNode[],
  layoutWidth: number,
  layoutHeight: number,
  sceneConfig: SceneConfig,
): THREE.Group {
  const labelsGroup = new THREE.Group()
  labelsGroup.name = 'cabinetLabels'

  for (const cabinet of cabinets) {
    if (!cabinet.cabinetCode) {
      continue
    }

    const sprite = createTextSprite(cabinet.cabinetCode, CABINET_LABEL_FONT_SIZE, CABINET_LABEL_COLOR)
    if (!sprite) {
      continue
    }

    sprite.name = `cabinetLabel_${cabinet.id}`
    sprite.scale.set(1.5, 0.4, 1)

    // 标签位于存放柜顶部
    const cabinetX = cabinet.x + cabinet.width / 2
    const cabinetY = cabinet.y + cabinet.height / 2
    const [x3d, z3d] = toWorld3D(cabinetX, cabinetY, layoutWidth, layoutHeight)
    sprite.position.set(x3d, sceneConfig.cabinetHeight + 0.3, z3d)

    labelsGroup.add(sprite)
  }

  return labelsGroup
}

/**
 * 创建文字Sprite
 */
function createTextSprite(text: string, fontSize: number, color: string): THREE.Sprite | null {
  const canvas = document.createElement('canvas')
  canvas.width = 128
  canvas.height = 32
  const ctx = canvas.getContext('2d')
  if (!ctx) {
    return null
  }

  ctx.fillStyle = color
  ctx.font = `bold ${fontSize / 2}px sans-serif`
  ctx.textAlign = 'center'
  ctx.textBaseline = 'middle'
  ctx.fillText(text, 64, 16)

  const texture = new THREE.CanvasTexture(canvas)
  texture.colorSpace = THREE.SRGBColorSpace

  const material = new THREE.SpriteMaterial({
    map: texture,
    transparent: true,
    depthTest: false,
  })

  return new THREE.Sprite(material)
}

/**
 * 高亮/取消高亮存放柜
 * @param cabinetInstancedMeshes 存放柜InstancedMesh列表
 * @param cabinetInstanceMaps 每个InstancedMesh各自的局部索引→存放柜ID映射
 * @param cabinetId 要高亮的存放柜ID，null则恢复所有为默认颜色
 * @param highlightColor 高亮颜色
 */
export function highlightCabinet(
  cabinetInstancedMeshes: THREE.InstancedMesh[],
  cabinetInstanceMaps: Map<number, EntityId>[],
  cabinetId: EntityId | null,
  highlightColor: number = 0xffa940,
): void {
  const defaultColor = new THREE.Color(CABINET_DEFAULT_COLOR)
  const highlight = new THREE.Color(highlightColor)

  for (let meshIdx = 0; meshIdx < cabinetInstancedMeshes.length; meshIdx++) {
    const instancedMesh = cabinetInstancedMeshes[meshIdx]
    const localMap = cabinetInstanceMaps[meshIdx]
    const count = instancedMesh.count

    for (let i = 0; i < count; i++) {
      const id = localMap.get(i)
      if (cabinetId && id === cabinetId) {
        instancedMesh.setColorAt(i, highlight)
      } else {
        instancedMesh.setColorAt(i, defaultColor)
      }
    }
    if (instancedMesh.instanceColor) {
      instancedMesh.instanceColor.needsUpdate = true
    }
  }
}

/**
 * 创建3D场景库位（柜子详情视图专用）
 * 使用InstancedMesh批量渲染当前选中柜子的所有库位
 * 颜色编码状态：空闲=绿色、占用=蓝色、禁用=灰色
 */

import * as THREE from 'three'
import type { EntityId } from '@/types/warehouse'
import type { WarehouseVisualBinNode, WarehouseVisualCabinetNode } from '../visual-layout'
import { VISUAL_STATUS_DISABLED } from '../visual-layout'
import type { SceneConfig } from '../types/three-visual'
import { BIN_STATUS_COLORS, toWorld3D } from '../types/three-visual'

/**
 * 创建库位组（InstancedMesh批量渲染）
 * @param cabinet 当前选中存放柜
 * @param bins 该存放柜的库位列表
 * @param layoutWidth 库房布局宽度
 * @param layoutHeight 库房布局高度
 * @param sceneConfig 场景配置
 * @returns 库位Group、实例映射、InstancedMesh
 */
export function createBins(
  cabinet: WarehouseVisualCabinetNode,
  bins: WarehouseVisualBinNode[],
  layoutWidth: number,
  layoutHeight: number,
  sceneConfig: SceneConfig,
): {
  group: THREE.Group
  binInstanceMap: Map<number, { cabinetId: EntityId; binId: EntityId }>
  binInstancedMesh: THREE.InstancedMesh | null
} {
  const binsGroup = new THREE.Group()
  binsGroup.name = 'bins'

  const binInstanceMap = new Map<number, { cabinetId: EntityId; binId: EntityId }>()

  if (bins.length === 0) {
    return { group: binsGroup, binInstanceMap, binInstancedMesh: null }
  }

  // 库位方块几何体和材质
  const { binSize, binGap } = sceneConfig
  const geometry = new THREE.BoxGeometry(binSize, binSize, binSize * 0.5)
  const material = new THREE.MeshStandardMaterial({
    roughness: 0.3,
    metalness: 0.1,
  })

  // 创建InstancedMesh
  const instancedMesh = new THREE.InstancedMesh(geometry, material, bins.length)
  instancedMesh.name = 'binInstances'
  instancedMesh.castShadow = true
  // 默认隐藏，仅在柜子详情视图时显示
  instancedMesh.visible = false

  const matrix = new THREE.Matrix4()
  const position = new THREE.Vector3()
  const quaternion = new THREE.Quaternion()
  const scale = new THREE.Vector3(1, 1, 1)

  // 计算存放柜在3D场景中的位置
  const cabinetX = cabinet.x + cabinet.width / 2
  const cabinetY = cabinet.y + cabinet.height / 2
  const [cabinetX3d, cabinetZ3d] = toWorld3D(cabinetX, cabinetY, layoutWidth, layoutHeight)

  // 库位网格排列在柜子正面
  const gridRowCount = cabinet.gridRowCount || cabinet.rows || 1
  const gridColCount = cabinet.gridColCount || cabinet.cols || 1

  // 网格总宽高
  const gridTotalWidth = gridColCount * (binSize + binGap) - binGap
  const gridTotalHeight = gridRowCount * (binSize + binGap) - binGap

  for (let i = 0; i < bins.length; i++) {
    const bin = bins[i]

    // 按row/col排列，行从上到下，列从左到右
    const col = (bin.col - 1) * (binSize + binGap) - gridTotalWidth / 2 + binSize / 2
    const row = -((bin.row - 1) * (binSize + binGap) - gridTotalHeight / 2 + binSize / 2)

    // 库位位于存放柜正面
    const x = cabinetX3d + col
    const y = sceneConfig.cabinetHeight / 2 + row + gridTotalHeight / 2
    const z = cabinetZ3d - sceneConfig.cabinetDepth / 2 - binSize * 0.5 - 0.1

    position.set(x, y, z)
    matrix.compose(position, quaternion, scale)

    instancedMesh.setMatrixAt(i, matrix)

    // 根据库位状态设置颜色
    const binColor = resolveBinColor(bin.status)
    instancedMesh.setColorAt(i, new THREE.Color(binColor))

    // 构建映射
    binInstanceMap.set(i, {
      cabinetId: cabinet.id as EntityId,
      binId: bin.id as EntityId,
    })
  }

  instancedMesh.instanceMatrix.needsUpdate = true
  if (instancedMesh.instanceColor) {
    instancedMesh.instanceColor.needsUpdate = true
  }

  binsGroup.add(instancedMesh)

  return { group: binsGroup, binInstanceMap, binInstancedMesh: instancedMesh }
}

/**
 * 根据库位状态值解析颜色
 * @param status 库位状态（1=启用/空闲，0=禁用）
 * @returns 颜色值
 */
function resolveBinColor(status: number): number {
  // status=1 表示启用（空闲），status=0 表示禁用
  // 占用状态需要额外字段判断，此处简化处理
  if (status === VISUAL_STATUS_DISABLED) {
    return BIN_STATUS_COLORS.disabled
  }
  // 启用状态默认为空闲（占用信息需从库存数据获取）
  return BIN_STATUS_COLORS.free
}

/** 库位高亮颜色（金色） */
const BIN_HIGHLIGHT_COLOR = 0xffd700

/**
 * 显示库位网格
 * @param binInstancedMesh 库位InstancedMesh
 */
export function showBinGrid(binInstancedMesh: THREE.InstancedMesh | null): void {
  if (binInstancedMesh) {
    binInstancedMesh.visible = true
  }
}

/**
 * 隐藏库位网格
 * @param binInstancedMesh 库位InstancedMesh
 */
export function hideBinGrid(binInstancedMesh: THREE.InstancedMesh | null): void {
  if (binInstancedMesh) {
    binInstancedMesh.visible = false
  }
}

/**
 * 高亮指定库位
 * @param binInstancedMesh 库位InstancedMesh
 * @param binInstanceMap 库位实例映射
 * @param binId 要高亮的库位ID，null则恢复所有默认颜色
 */
export function highlightBin(
  binInstancedMesh: THREE.InstancedMesh | null,
  binInstanceMap: Map<number, { cabinetId: EntityId; binId: EntityId }>,
  binId: EntityId | null,
): void {
  if (!binInstancedMesh) {
    return
  }

  const count = binInstancedMesh.count
  for (let i = 0; i < count; i++) {
    const info = binInstanceMap.get(i)
    if (!info) {
      continue
    }

    // 查找原始库位状态以恢复默认颜色
    const defaultColor = new THREE.Color(BIN_STATUS_COLORS.free)

    if (binId && info.binId === binId) {
      // 高亮：使用更亮的颜色
      binInstancedMesh.setColorAt(i, new THREE.Color(BIN_HIGHLIGHT_COLOR))
    } else {
      binInstancedMesh.setColorAt(i, defaultColor)
    }
  }

  if (binInstancedMesh.instanceColor) {
    binInstancedMesh.instanceColor.needsUpdate = true
  }
}
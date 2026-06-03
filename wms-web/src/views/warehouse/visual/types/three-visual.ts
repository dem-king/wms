/**
 * 3D库房可视化专用类型定义
 * 包含3D场景构建、交互、渲染所需的全部类型
 */

import type { EntityId } from '@/types/warehouse'
import type { Vector3 } from 'three'
import type { InstancedMesh, Mesh } from 'three'

// ==================== 预设视角 ====================

/** 预设视角枚举 */
export type PresetView = 'bird-eye' | 'front' | 'side' | 'isometric'

// ==================== 3D节点类型 ====================

/** 存放柜3D节点 */
export interface CabinetNode3D {
  /** 存放柜ID */
  id: EntityId
  /** 3D场景位置 */
  position: Vector3
  /** 存放柜宽度（3D场景单位） */
  width: number
  /** 存放柜高度（3D场景单位） */
  height: number
  /** 存放柜深度（3D场景单位） */
  depth: number
  /** 旋转角度（弧度） */
  rotation: number
  /** InstancedMesh中的实例索引 */
  instanceIndex: number
}

/** 库位3D节点 */
export interface BinNode3D {
  /** 库位ID */
  id: EntityId
  /** 存放柜ID */
  cabinetId: EntityId
  /** 3D场景位置 */
  position: Vector3
  /** 库位状态：空闲/占用/禁用 */
  status: 'free' | 'occupied' | 'disabled'
  /** InstancedMesh中的实例索引 */
  instanceIndex: number
}

// ==================== 场景构建结果 ====================

/** 场景构建结果 */
export interface WarehouseSceneResult {
  /** 每个InstancedMesh的局部索引 → 存放柜ID映射（按InstancedMesh顺序存储） */
  cabinetInstanceMaps: Map<number, EntityId>[]
  /** 存放柜ID → 3D节点映射 */
  cabinetNodeMap: Map<string, CabinetNode3D>
  /** InstancedMesh实例索引 → 库位信息映射 */
  binInstanceMap: Map<number, { cabinetId: EntityId; binId: EntityId }>
  /** 区域地面Mesh列表（用于射线拾取） */
  areaMeshes: Mesh[]
  /** 存放柜InstancedMesh引用（可能有多个，按尺寸分组） */
  cabinetInstancedMeshes: InstancedMesh[]
  /** 库位InstancedMesh引用 */
  binInstancedMesh: InstancedMesh | null
  /** 地面Mesh引用（用于射线拾取判断点击空白处） */
  groundMesh: Mesh | null
}

// ==================== 场景配置 ====================

/** 3D场景配置常量 */
export interface SceneConfig {
  /** 存放柜默认高度（场景单位，约2米） */
  cabinetHeight: number
  /** 存放柜默认深度（场景单位，约0.8米） */
  cabinetDepth: number
  /** 墙壁高度（场景单位，约3米） */
  wallHeight: number
  /** 库位方块尺寸（场景单位） */
  binSize: number
  /** 库位间距（场景单位） */
  binGap: number
  /** 地面网格分割数 */
  gridDivisions: number
  /** LOD距离阈值（超过此距离不渲染库位细节） */
  lodDistance: number
}

/** 默认场景配置 */
export const DEFAULT_SCENE_CONFIG: SceneConfig = {
  cabinetHeight: 2.0,
  cabinetDepth: 0.8,
  wallHeight: 3.0,
  binSize: 0.3,
  binGap: 0.05,
  gridDivisions: 50,
  lodDistance: 20,
}

// ==================== 颜色常量 ====================

/** 库位状态颜色常量 */
export const BIN_STATUS_COLORS = {
  /** 空闲：绿色 */
  free: 0x52c41a,
  /** 占用：蓝色 */
  occupied: 0x1890ff,
  /** 禁用：灰色 */
  disabled: 0xd9d9d9,
} as const

/** 存放柜高亮颜色（橙色） */
export const CABINET_HIGHLIGHT_COLOR = 0xffa940

/** 存放柜默认颜色（蓝色） */
export const CABINET_DEFAULT_COLOR = 0x1890ff

/** 区域选中透明度 */
export const AREA_SELECTED_OPACITY = 0.6

/** 区域默认透明度 */
export const AREA_DEFAULT_OPACITY = 0.3

/** 地面默认颜色（浅灰） */
export const GROUND_DEFAULT_COLOR = 0xe0e0e0

/** 墙壁默认颜色（深灰） */
export const WALL_DEFAULT_COLOR = 0x4a4a4a

/** 通道地面颜色 */
export const AISLE_GROUND_COLOR = 0xf0f9ff

/** 通道边框颜色 */
export const AISLE_BORDER_COLOR = 0x91d5ff

// ==================== OrbitControls配置 ====================

/** OrbitControls配置接口 */
export interface OrbitControlsConfig {
  /** 最小缩放距离 */
  minDistance: number
  /** 最大缩放距离 */
  maxDistance: number
  /** 是否启用阻尼（惯性） */
  enableDamping: boolean
  /** 阻尼系数 */
  dampingFactor: number
  /** 最小极角（弧度） */
  minPolarAngle: number
  /** 最大极角（弧度） */
  maxPolarAngle: number
}

// ==================== 相机预设位置 ====================

/** 相机预设位置配置 */
export interface CameraPresetPosition {
  /** 相机位置 */
  position: Vector3
  /** 相机注视目标 */
  lookAt: Vector3
}

// ==================== WebGL兼容性 ====================

/**
 * 检测浏览器是否支持WebGL2
 * @returns 是否支持WebGL2
 */
export function isWebGL2Available(): boolean {
  try {
    const canvas = document.createElement('canvas')
    return !!(canvas.getContext('webgl2') || canvas.getContext('experimental-webgl2'))
  } catch {
    return false
  }
}

// ==================== 坐标映射工具 ====================

/**
 * 2D布局坐标转换为3D世界坐标
 * 后端坐标系：左上角原点，X右Y下
 * Three.js坐标系：中心原点，X右Z前Y上
 *
 * @param x2d 2D布局X坐标
 * @param y2d 2D布局Y坐标
 * @param layoutWidth 库房布局宽度
 * @param layoutHeight 库房布局高度
 * @returns 3D世界坐标 [x, z]
 */
export function toWorld3D(
  x2d: number,
  y2d: number,
  layoutWidth: number,
  layoutHeight: number,
): [number, number] {
  const x3d = x2d - layoutWidth / 2
  const z3d = y2d - layoutHeight / 2
  return [x3d, z3d]
}
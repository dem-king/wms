/**
 * 创建3D场景区域标记
 * 包含区域地面颜色标记、边框线和名称标签
 */

import * as THREE from 'three'
import type { EntityId } from '@/types/warehouse'
import type { WarehouseVisualAreaNode } from '../visual-layout'
import { AREA_DEFAULT_OPACITY, AREA_SELECTED_OPACITY, toWorld3D } from '../types/three-visual'

/** 区域地面略高于地面的偏移量，避免z-fighting */
const AREA_Y_OFFSET = 0.02

/** 区域标签默认字号 */
const AREA_LABEL_FONT_SIZE = 64

/** 区域标签默认颜色 */
const AREA_LABEL_COLOR = '#333333'

/** 区域默认边框颜色 */
const AREA_BORDER_COLOR = 0x1890ff

/** 区域颜色列表（循环使用） */
const AREA_COLORS = [
  0xe6f7ff, // 浅蓝
  0xf6ffed, // 浅绿
  0xfff7e6, // 浅黄
  0xf9f0ff, // 浅紫
  0xfff1f0, // 浅红
  0xe6fffb, // 浅青
] as const

/**
 * 创建区域组
 * @param areas 区域节点列表
 * @param layoutWidth 库房布局宽度
 * @param layoutHeight 库房布局高度
 * @returns 区域Group和区域Mesh列表（用于射线拾取）
 */
export function createAreas(
  areas: WarehouseVisualAreaNode[],
  layoutWidth: number,
  layoutHeight: number,
): { group: THREE.Group; areaMeshes: THREE.Mesh[] } {
  const areasGroup = new THREE.Group()
  areasGroup.name = 'areas'
  const areaMeshes: THREE.Mesh[] = []

  for (let i = 0; i < areas.length; i++) {
    const area = areas[i]
    const areaColor = AREA_COLORS[i % AREA_COLORS.length]

    const areaGroup = new THREE.Group()
    areaGroup.name = `area_${area.id}`

    // 创建区域地面标记
    const groundMesh = createAreaGround(area, layoutWidth, layoutHeight, areaColor)
    areaGroup.add(groundMesh)
    areaMeshes.push(groundMesh)

    // 创建区域边框
    const borderLine = createAreaBorder(area, layoutWidth, layoutHeight)
    if (borderLine) {
      areaGroup.add(borderLine)
    }

    // 创建区域名称标签
    const label = createAreaLabel(area, layoutWidth, layoutHeight)
    if (label) {
      areaGroup.add(label)
    }

    areasGroup.add(areaGroup)
  }

  return { group: areasGroup, areaMeshes }
}

/**
 * 创建区域地面标记Mesh
 * @param area 区域节点
 * @param layoutWidth 库房布局宽度
 * @param layoutHeight 库房布局高度
 * @param color 区域颜色
 * @returns 区域地面Mesh
 */
function createAreaGround(
  area: WarehouseVisualAreaNode,
  layoutWidth: number,
  layoutHeight: number,
  color: number,
): THREE.Mesh {
  const geometry = new THREE.PlaneGeometry(area.width, area.height)
  geometry.rotateX(-Math.PI / 2)

  const material = new THREE.MeshStandardMaterial({
    color,
    transparent: true,
    opacity: AREA_DEFAULT_OPACITY,
    side: THREE.DoubleSide,
    depthWrite: false,
  })

  const mesh = new THREE.Mesh(geometry, material)
  mesh.name = `areaGround_${area.id}`

  // 坐标映射
  const [x3d, z3d] = toWorld3D(area.x + area.width / 2, area.y + area.height / 2, layoutWidth, layoutHeight)
  mesh.position.set(x3d, AREA_Y_OFFSET, z3d)

  // 存储区域ID用于射线拾取
  mesh.userData.areaId = area.id as EntityId
  mesh.userData.isAreaGround = true

  return mesh
}

/**
 * 创建区域边框线
 * @param area 区域节点
 * @param layoutWidth 库房布局宽度
 * @param layoutHeight 库房布局高度
 * @returns 边框LineSegments
 */
function createAreaBorder(
  area: WarehouseVisualAreaNode,
  layoutWidth: number,
  layoutHeight: number,
): THREE.LineSegments | null {
  // 矩形区域边框：4条边
  const halfW = area.width / 2
  const halfH = area.height / 2
  const vertices = new Float32Array([
    -halfW, 0, -halfH,  halfW, 0, -halfH,
    halfW, 0, -halfH,   halfW, 0, halfH,
    halfW, 0, halfH,    -halfW, 0, halfH,
    -halfW, 0, halfH,   -halfW, 0, -halfH,
  ])

  const geometry = new THREE.BufferGeometry()
  geometry.setAttribute('position', new THREE.BufferAttribute(vertices, 3))

  const material = new THREE.LineBasicMaterial({
    color: AREA_BORDER_COLOR,
    linewidth: 1,
  })

  const line = new THREE.LineSegments(geometry, material)
  line.name = `areaBorder_${area.id}`

  // 坐标映射（与区域地面中心对齐）
  const [x3d, z3d] = toWorld3D(area.x + area.width / 2, area.y + area.height / 2, layoutWidth, layoutHeight)
  line.position.set(x3d, AREA_Y_OFFSET + 0.01, z3d)

  return line
}

/**
 * 创建区域名称标签Sprite
 * @param area 区域节点
 * @param layoutWidth 库房布局宽度
 * @param layoutHeight 库房布局高度
 * @returns 标签Sprite
 */
function createAreaLabel(
  area: WarehouseVisualAreaNode,
  layoutWidth: number,
  layoutHeight: number,
): THREE.Sprite | null {
  if (!area.areaName) {
    return null
  }

  // 使用Canvas生成文字纹理
  const canvas = document.createElement('canvas')
  canvas.width = 256
  canvas.height = 64
  const ctx = canvas.getContext('2d')
  if (!ctx) {
    return null
  }

  ctx.fillStyle = AREA_LABEL_COLOR
  ctx.font = `bold ${AREA_LABEL_FONT_SIZE / 2}px sans-serif`
  ctx.textAlign = 'center'
  ctx.textBaseline = 'middle'
  ctx.fillText(area.areaName, 128, 32)

  const texture = new THREE.CanvasTexture(canvas)
  texture.colorSpace = THREE.SRGBColorSpace

  const material = new THREE.SpriteMaterial({
    map: texture,
    transparent: true,
    depthTest: false,
  })

  const sprite = new THREE.Sprite(material)
  sprite.name = `areaLabel_${area.id}`
  // 标签缩放
  sprite.scale.set(3, 0.75, 1)

  // 坐标映射：标签放在区域顶部
  const labelX = area.labelX != null ? area.x + area.labelX : area.x + area.width / 2
  const labelY = area.labelY != null ? area.y + area.labelY : area.y + 20
  const [x3d, z3d] = toWorld3D(labelX, labelY, layoutWidth, layoutHeight)
  sprite.position.set(x3d, AREA_Y_OFFSET + 0.5, z3d)

  return sprite
}

/**
 * 高亮/取消高亮区域
 * @param areasGroup 区域组
 * @param areaId 要高亮的区域ID，null则恢复所有区域默认状态
 * @param isSelected 是否选中
 */
export function highlightArea(
  areasGroup: THREE.Group,
  areaId: EntityId | null,
  isSelected: boolean,
): void {
  areasGroup.traverse((object) => {
    if (object instanceof THREE.Mesh && object.userData.isAreaGround) {
      const material = object.material as THREE.MeshStandardMaterial
      if (areaId && object.userData.areaId === areaId && isSelected) {
        // 高亮：提升透明度
        material.opacity = AREA_SELECTED_OPACITY
      } else {
        // 恢复默认
        material.opacity = AREA_DEFAULT_OPACITY
      }
      material.needsUpdate = true
    }
  })
}
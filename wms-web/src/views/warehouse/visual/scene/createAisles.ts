/**
 * 创建3D场景通道
 * 从布局辅助元素中筛选aisle类型，生成通道地面和虚线边框
 */

import * as THREE from 'three'
import type { LayoutElementVo } from '../types/layout-element'
import { parseStyleData, DEFAULT_ELEMENT_STYLES } from '../types/layout-element'
import { AISLE_GROUND_COLOR, AISLE_BORDER_COLOR, toWorld3D } from '../types/three-visual'

/** 通道地面略高于地面的偏移量 */
const AISLE_Y_OFFSET = 0.03

/** 虚线dashSize（场景单位） */
const DASH_SIZE = 0.5

/** 虚线gapSize（场景单位） */
const GAP_SIZE = 0.3

/**
 * 创建通道组
 * @param layoutElements 布局辅助元素列表
 * @param layoutWidth 库房布局宽度
 * @param layoutHeight 库房布局高度
 * @returns 通道Group
 */
export function createAisles(
  layoutElements: LayoutElementVo[],
  layoutWidth: number,
  layoutHeight: number,
): THREE.Group {
  const aislesGroup = new THREE.Group()
  aislesGroup.name = 'aisles'

  // 筛选aisle类型的元素
  const aisleElements = layoutElements.filter(element => element.elementType === 'aisle')

  for (let i = 0; i < aisleElements.length; i++) {
    const element = aisleElements[i]
    const aisleGroup = new THREE.Group()
    aisleGroup.name = `aisle_${element.id}`

    // 创建通道地面
    const groundMesh = createAisleGround(element, layoutWidth, layoutHeight)
    aisleGroup.add(groundMesh)

    // 创建通道虚线边框
    const borderLine = createAisleBorder(element, layoutWidth, layoutHeight)
    if (borderLine) {
      aisleGroup.add(borderLine)
    }

    // 创建通道名称标签
    const label = createAisleLabel(element, layoutWidth, layoutHeight)
    if (label) {
      aisleGroup.add(label)
    }

    aislesGroup.add(aisleGroup)
  }

  return aislesGroup
}

/**
 * 创建通道地面Mesh
 */
function createAisleGround(
  element: LayoutElementVo,
  layoutWidth: number,
  layoutHeight: number,
): THREE.Mesh {
  const aisleWidth = element.layoutWidth || 2
  const aisleHeight = element.layoutHeight || 6

  const geometry = new THREE.PlaneGeometry(aisleWidth, aisleHeight)
  geometry.rotateX(-Math.PI / 2)

  // 解析自定义样式
  const customStyle = parseStyleData(element.styleData)
  const defaultStyle = DEFAULT_ELEMENT_STYLES.aisle
  const fillColor = customStyle.fillColor ?? defaultStyle.fillColor

  const material = new THREE.MeshStandardMaterial({
    color: fillColor || AISLE_GROUND_COLOR,
    transparent: true,
    opacity: 0.5,
    side: THREE.DoubleSide,
    depthWrite: false,
  })

  const mesh = new THREE.Mesh(geometry, material)
  mesh.name = `aisleGround_${element.id}`

  // 坐标映射
  const [x3d, z3d] = toWorld3D(
    element.positionX + aisleWidth / 2,
    element.positionY + aisleHeight / 2,
    layoutWidth,
    layoutHeight,
  )
  mesh.position.set(x3d, AISLE_Y_OFFSET, z3d)

  return mesh
}

/**
 * 创建通道虚线边框
 */
function createAisleBorder(
  element: LayoutElementVo,
  layoutWidth: number,
  layoutHeight: number,
): THREE.LineSegments | null {
  const aisleWidth = element.layoutWidth || 2
  const aisleHeight = element.layoutHeight || 6
  const halfW = aisleWidth / 2
  const halfH = aisleHeight / 2

  const vertices = new Float32Array([
    -halfW, 0, -halfH,  halfW, 0, -halfH,
    halfW, 0, -halfH,   halfW, 0, halfH,
    halfW, 0, halfH,    -halfW, 0, halfH,
    -halfW, 0, halfH,   -halfW, 0, -halfH,
  ])

  const geometry = new THREE.BufferGeometry()
  geometry.setAttribute('position', new THREE.BufferAttribute(vertices, 3))

  // 解析自定义样式
  const customStyle = parseStyleData(element.styleData)
  const defaultStyle = DEFAULT_ELEMENT_STYLES.aisle
  const strokeColor = customStyle.strokeColor ?? defaultStyle.strokeColor

  const material = new THREE.LineDashedMaterial({
    color: strokeColor || AISLE_BORDER_COLOR,
    dashSize: DASH_SIZE,
    gapSize: GAP_SIZE,
  })

  const line = new THREE.LineSegments(geometry, material)
  line.name = `aisleBorder_${element.id}`

  // 计算线段总长度以使虚线生效
  line.computeLineDistances()

  // 坐标映射
  const [x3d, z3d] = toWorld3D(
    element.positionX + aisleWidth / 2,
    element.positionY + aisleHeight / 2,
    layoutWidth,
    layoutHeight,
  )
  line.position.set(x3d, AISLE_Y_OFFSET + 0.01, z3d)

  return line
}

/**
 * 创建通道名称标签
 */
function createAisleLabel(
  element: LayoutElementVo,
  layoutWidth: number,
  layoutHeight: number,
): THREE.Sprite | null {
  const text = element.labelText || element.elementName
  if (!text) {
    return null
  }

  const canvas = document.createElement('canvas')
  canvas.width = 128
  canvas.height = 32
  const ctx = canvas.getContext('2d')
  if (!ctx) {
    return null
  }

  ctx.fillStyle = '#666666'
  ctx.font = 'bold 16px sans-serif'
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

  const sprite = new THREE.Sprite(material)
  sprite.name = `aisleLabel_${element.id}`
  sprite.scale.set(2, 0.5, 1)

  const aisleWidth = element.layoutWidth || 2
  const aisleHeight = element.layoutHeight || 6
  const [x3d, z3d] = toWorld3D(
    element.positionX + aisleWidth / 2,
    element.positionY + aisleHeight / 2,
    layoutWidth,
    layoutHeight,
  )
  sprite.position.set(x3d, AISLE_Y_OFFSET + 0.3, z3d)

  return sprite
}
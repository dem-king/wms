/**
 * 创建3D场景标注
 * 处理text/dimension/reserved/device类型的布局辅助元素
 */

import * as THREE from 'three'
import type { LayoutElementVo } from '../types/layout-element'
import { parseStyleData, parsePointData, DEFAULT_ELEMENT_STYLES, DIMENSION_END_MARK_LENGTH } from '../types/layout-element'
import { toWorld3D } from '../types/three-visual'

/** 标注略高于地面的偏移量 */
const ANNOTATION_Y_OFFSET = 0.05

/** 保留区默认颜色 */
const RESERVED_FILL_COLOR = 0xfff7e6

/** 保留区边框颜色 */
const RESERVED_BORDER_COLOR = 0xfa8c16

/** 设备默认颜色 */
const DEVICE_COLOR = 0xe6f7ff

/** 设备方块尺寸 */
const DEVICE_SIZE = 0.5

/**
 * 创建标注组
 * @param layoutElements 布局辅助元素列表
 * @param layoutWidth 库房布局宽度
 * @param layoutHeight 库房布局高度
 * @returns 标注Group
 */
export function createAnnotations(
  layoutElements: LayoutElementVo[],
  layoutWidth: number,
  layoutHeight: number,
): THREE.Group {
  const annotationsGroup = new THREE.Group()
  annotationsGroup.name = 'annotations'

  // 筛选标注类型的元素（排除wall和aisle，它们由专门的函数处理）
  const annotationElements = layoutElements.filter(
    element => ['text', 'dimension', 'reserved', 'device'].includes(element.elementType),
  )

  for (const element of annotationElements) {
    const annotation = createAnnotationByType(element, layoutWidth, layoutHeight)
    if (annotation) {
      annotationsGroup.add(annotation)
    }
  }

  return annotationsGroup
}

/**
 * 根据元素类型创建对应的标注对象
 */
function createAnnotationByType(
  element: LayoutElementVo,
  layoutWidth: number,
  layoutHeight: number,
): THREE.Object3D | null {
  switch (element.elementType) {
    case 'text':
      return createTextAnnotation(element, layoutWidth, layoutHeight)
    case 'dimension':
      return createDimensionAnnotation(element, layoutWidth, layoutHeight)
    case 'reserved':
      return createReservedAnnotation(element, layoutWidth, layoutHeight)
    case 'device':
      return createDeviceAnnotation(element, layoutWidth, layoutHeight)
    default:
      return null
  }
}

/**
 * 创建文字标注（Sprite + Canvas文字纹理）
 */
function createTextAnnotation(
  element: LayoutElementVo,
  layoutWidth: number,
  layoutHeight: number,
): THREE.Sprite | null {
  const text = element.labelText || element.elementName
  if (!text) {
    return null
  }

  const customStyle = parseStyleData(element.styleData)
  const defaultStyle = DEFAULT_ELEMENT_STYLES.text
  const fontColor = customStyle.fontColor ?? defaultStyle.fontColor ?? '#333333'
  const fontSize = customStyle.fontSize ?? defaultStyle.fontSize ?? 14

  const canvas = document.createElement('canvas')
  canvas.width = 256
  canvas.height = 64
  const ctx = canvas.getContext('2d')
  if (!ctx) {
    return null
  }

  ctx.fillStyle = fontColor
  ctx.font = `bold ${Math.min(fontSize * 2, 48)}px sans-serif`
  ctx.textAlign = 'center'
  ctx.textBaseline = 'middle'
  ctx.fillText(text, 128, 32)

  const texture = new THREE.CanvasTexture(canvas)
  texture.colorSpace = THREE.SRGBColorSpace

  const material = new THREE.SpriteMaterial({
    map: texture,
    transparent: true,
    depthTest: false,
  })

  const sprite = new THREE.Sprite(material)
  sprite.name = `textAnnotation_${element.id}`
  sprite.scale.set(3, 0.75, 1)

  // 坐标映射
  const [x3d, z3d] = toWorld3D(element.positionX, element.positionY, layoutWidth, layoutHeight)
  sprite.position.set(x3d, ANNOTATION_Y_OFFSET + 1.5, z3d)

  return sprite
}

/**
 * 创建尺寸标注（LineSegments + 数值标签 + 端点标记线）
 */
function createDimensionAnnotation(
  element: LayoutElementVo,
  layoutWidth: number,
  layoutHeight: number,
): THREE.Group | null {
  const pointData = parsePointData(element.pointData)
  if (!pointData || !('start' in pointData) || !('end' in pointData)) {
    return null
  }

  const dimData = pointData as { start: { x: number; y: number }; end: { x: number; y: number } }
  const group = new THREE.Group()
  group.name = `dimensionAnnotation_${element.id}`

  // 起终点3D坐标
  const [startX, startZ] = toWorld3D(dimData.start.x, dimData.start.y, layoutWidth, layoutHeight)
  const [endX, endZ] = toWorld3D(dimData.end.x, dimData.end.y, layoutWidth, layoutHeight)
  const y = ANNOTATION_Y_OFFSET + 0.5

  // 尺寸线
  const lineVertices = new Float32Array([startX, y, startZ, endX, y, endZ])
  const lineGeometry = new THREE.BufferGeometry()
  lineGeometry.setAttribute('position', new THREE.BufferAttribute(lineVertices, 3))

  const customStyle = parseStyleData(element.styleData)
  const defaultStyle = DEFAULT_ELEMENT_STYLES.dimension
  const strokeColor = customStyle.strokeColor ?? defaultStyle.strokeColor ?? '#333333'

  const lineMaterial = new THREE.LineBasicMaterial({ color: strokeColor })
  const line = new THREE.LineSegments(lineGeometry, lineMaterial)
  group.add(line)

  // 端点标记线（短垂直线）
  const markLen = DIMENSION_END_MARK_LENGTH / 60 // 像素转场景单位
  const markVertices = new Float32Array([
    startX, y - markLen, startZ, startX, y + markLen, startZ,
    endX, y - markLen, endZ, endX, y + markLen, endZ,
  ])
  const markGeometry = new THREE.BufferGeometry()
  markGeometry.setAttribute('position', new THREE.BufferAttribute(markVertices, 3))
  const markLine = new THREE.LineSegments(markGeometry, lineMaterial)
  group.add(markLine)

  // 数值标签
  const distance = Math.sqrt((endX - startX) ** 2 + (endZ - startZ) ** 2)
  const labelText = element.labelText || `${distance.toFixed(1)}m`
  const label = createSimpleLabel(labelText, 12)
  if (label) {
    label.position.set(
      (startX + endX) / 2,
      y + 0.3,
      (startZ + endZ) / 2,
    )
    group.add(label)
  }

  return group
}

/**
 * 创建保留区标注（半透明黄色区域 + 边框）
 */
function createReservedAnnotation(
  element: LayoutElementVo,
  layoutWidth: number,
  layoutHeight: number,
): THREE.Group | null {
  const group = new THREE.Group()
  group.name = `reservedAnnotation_${element.id}`

  const resWidth = element.layoutWidth || 2
  const resHeight = element.layoutHeight || 2

  // 半透明黄色区域
  const geometry = new THREE.PlaneGeometry(resWidth, resHeight)
  geometry.rotateX(-Math.PI / 2)

  const customStyle = parseStyleData(element.styleData)
  const defaultStyle = DEFAULT_ELEMENT_STYLES.reserved
  const fillColor = customStyle.fillColor ?? defaultStyle.fillColor

  const material = new THREE.MeshStandardMaterial({
    color: fillColor || RESERVED_FILL_COLOR,
    transparent: true,
    opacity: 0.3,
    side: THREE.DoubleSide,
    depthWrite: false,
  })

  const mesh = new THREE.Mesh(geometry, material)
  const [x3d, z3d] = toWorld3D(
    element.positionX + resWidth / 2,
    element.positionY + resHeight / 2,
    layoutWidth,
    layoutHeight,
  )
  mesh.position.set(x3d, ANNOTATION_Y_OFFSET, z3d)
  group.add(mesh)

  // 边框
  const halfW = resWidth / 2
  const halfH = resHeight / 2
  const borderVertices = new Float32Array([
    -halfW, 0, -halfH,  halfW, 0, -halfH,
    halfW, 0, -halfH,   halfW, 0, halfH,
    halfW, 0, halfH,    -halfW, 0, halfH,
    -halfW, 0, halfH,   -halfW, 0, -halfH,
  ])
  const borderGeometry = new THREE.BufferGeometry()
  borderGeometry.setAttribute('position', new THREE.BufferAttribute(borderVertices, 3))

  const strokeColor = customStyle.strokeColor ?? defaultStyle.strokeColor
  const borderMaterial = new THREE.LineBasicMaterial({ color: strokeColor || RESERVED_BORDER_COLOR })
  const borderLine = new THREE.LineSegments(borderGeometry, borderMaterial)
  borderLine.position.set(x3d, ANNOTATION_Y_OFFSET + 0.01, z3d)
  group.add(borderLine)

  return group
}

/**
 * 创建设备标注（小型3D方块 + 设备名称标签）
 */
function createDeviceAnnotation(
  element: LayoutElementVo,
  layoutWidth: number,
  layoutHeight: number,
): THREE.Group | null {
  const group = new THREE.Group()
  group.name = `deviceAnnotation_${element.id}`

  // 设备方块
  const geometry = new THREE.BoxGeometry(DEVICE_SIZE, DEVICE_SIZE, DEVICE_SIZE)
  const customStyle = parseStyleData(element.styleData)
  const defaultStyle = DEFAULT_ELEMENT_STYLES.device
  const fillColor = customStyle.fillColor ?? defaultStyle.fillColor

  const material = new THREE.MeshStandardMaterial({
    color: fillColor || DEVICE_COLOR,
    roughness: 0.5,
    metalness: 0.2,
  })

  const mesh = new THREE.Mesh(geometry, material)
  mesh.castShadow = true

  const [x3d, z3d] = toWorld3D(element.positionX, element.positionY, layoutWidth, layoutHeight)
  mesh.position.set(x3d, DEVICE_SIZE / 2, z3d)
  group.add(mesh)

  // 设备名称标签
  const text = element.labelText || element.elementName
  if (text) {
    const label = createSimpleLabel(text, 10)
    if (label) {
      label.position.set(x3d, DEVICE_SIZE + 0.3, z3d)
      group.add(label)
    }
  }

  return group
}

/**
 * 创建简单文字标签
 */
function createSimpleLabel(text: string, fontSize: number): THREE.Sprite | null {
  const canvas = document.createElement('canvas')
  canvas.width = 128
  canvas.height = 32
  const ctx = canvas.getContext('2d')
  if (!ctx) {
    return null
  }

  ctx.fillStyle = '#333333'
  ctx.font = `bold ${fontSize}px sans-serif`
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
  sprite.scale.set(1.5, 0.4, 1)
  return sprite
}
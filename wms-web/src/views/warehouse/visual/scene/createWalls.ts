/**
 * 创建3D场景墙壁
 * 从布局辅助元素中筛选wall类型，生成3D长方体墙壁
 */

import * as THREE from 'three'
import type { LayoutElementVo } from '../types/layout-element'
import { parseStyleData, DEFAULT_ELEMENT_STYLES } from '../types/layout-element'
import { WALL_DEFAULT_COLOR, toWorld3D } from '../types/three-visual'
import type { SceneConfig } from '../types/three-visual'

/**
 * 创建墙壁组
 * @param layoutElements 布局辅助元素列表
 * @param layoutWidth 库房布局宽度
 * @param layoutHeight 库房布局高度
 * @param sceneConfig 场景配置
 * @returns 墙壁Group
 */
export function createWalls(
  layoutElements: LayoutElementVo[],
  layoutWidth: number,
  layoutHeight: number,
  sceneConfig: SceneConfig,
): THREE.Group {
  const wallsGroup = new THREE.Group()
  wallsGroup.name = 'walls'

  // 筛选wall类型的元素
  const wallElements = layoutElements.filter(element => element.elementType === 'wall')

  if (wallElements.length === 0) {
    return wallsGroup
  }

  // 共享几何体和材质（所有墙壁使用相同材质）
  const wallMaterial = new THREE.MeshStandardMaterial({
    color: WALL_DEFAULT_COLOR,
    roughness: 0.8,
    metalness: 0.0,
  })

  for (let i = 0; i < wallElements.length; i++) {
    const element = wallElements[i]
    const wallMesh = createWallMesh(element, layoutWidth, layoutHeight, sceneConfig, wallMaterial)
    wallMesh.name = `wall_${i}`
    wallsGroup.add(wallMesh)
  }

  return wallsGroup
}

/**
 * 创建单个墙壁Mesh
 * @param element 墙壁布局元素
 * @param layoutWidth 库房布局宽度
 * @param layoutHeight 库房布局高度
 * @param sceneConfig 场景配置
 * @param sharedMaterial 共享材质
 * @returns 墙壁Mesh
 */
function createWallMesh(
  element: LayoutElementVo,
  layoutWidth: number,
  layoutHeight: number,
  sceneConfig: SceneConfig,
  sharedMaterial: THREE.MeshStandardMaterial,
): THREE.Mesh {
  // 墙壁尺寸：使用元素布局尺寸，无尺寸时使用默认值
  const wallWidth = element.layoutWidth || layoutWidth
  const wallHeight = sceneConfig.wallHeight
  const wallDepth = element.layoutHeight || 0.3

  const geometry = new THREE.BoxGeometry(wallWidth, wallHeight, wallDepth)

  // 解析自定义样式颜色
  const customStyle = parseStyleData(element.styleData)
  const defaultStyle = DEFAULT_ELEMENT_STYLES.wall
  const fillColor = customStyle.fillColor ?? defaultStyle.fillColor

  // 如果有自定义颜色，创建独立材质实例
  let material = sharedMaterial
  if (fillColor && fillColor !== defaultStyle.fillColor) {
    material = new THREE.MeshStandardMaterial({
      color: fillColor,
      roughness: 0.8,
      metalness: 0.0,
    })
  }

  const mesh = new THREE.Mesh(geometry, material)
  mesh.castShadow = true
  mesh.receiveShadow = true

  // 坐标映射：2D布局坐标 → 3D世界坐标
  const [x3d, z3d] = toWorld3D(element.positionX, element.positionY, layoutWidth, layoutHeight)
  mesh.position.set(x3d, wallHeight / 2, z3d)

  // 旋转：元素rotation字段为角度，转换为弧度
  if (element.rotation) {
    mesh.rotation.y = THREE.MathUtils.degToRad(element.rotation)
  }

  // 存储元素ID用于射线拾取
  mesh.userData.elementId = element.id
  mesh.userData.elementType = 'wall'

  return mesh
}
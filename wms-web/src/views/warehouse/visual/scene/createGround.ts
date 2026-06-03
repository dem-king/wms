/**
 * 创建3D场景地面
 * 包含地面平面和辅助网格线，支持底图纹理加载
 */

import * as THREE from 'three'
import { GROUND_DEFAULT_COLOR } from '../types/three-visual'

/** 地面网格线略高于地面的偏移量，避免z-fighting */
const GRID_Y_OFFSET = 0.01

/** 地面网格默认分割数 */
const DEFAULT_GRID_DIVISIONS = 50

/**
 * 创建地面组
 * @param layoutWidth 库房布局宽度
 * @param layoutHeight 库房布局高度
 * @param gridDivisions 网格分割数
 * @param backgroundUrl 底图URL（可选）
 * @returns 地面Group（包含地面平面和网格辅助线）
 */
export function createGround(
  layoutWidth: number,
  layoutHeight: number,
  gridDivisions: number = DEFAULT_GRID_DIVISIONS,
  backgroundUrl?: string,
): THREE.Group {
  const groundGroup = new THREE.Group()
  groundGroup.name = 'ground'

  // 创建地面平面几何体
  const groundGeometry = new THREE.PlaneGeometry(layoutWidth, layoutHeight)
  // 旋转-90度使面朝上（PlaneGeometry默认朝向Z轴正方向）
  groundGeometry.rotateX(-Math.PI / 2)

  // 创建地面材质：有底图时使用纹理，否则使用默认灰色
  const groundMaterial = new THREE.MeshStandardMaterial({
    color: GROUND_DEFAULT_COLOR,
    roughness: 0.8,
    metalness: 0.0,
    side: THREE.DoubleSide,
  })

  const groundMesh = new THREE.Mesh(groundGeometry, groundMaterial)
  groundMesh.name = 'groundPlane'
  groundMesh.receiveShadow = true
  // 存储标识用于射线拾取时判断点击了地面空白处
  groundMesh.userData.isGround = true

  groundGroup.add(groundMesh)

  // 加载底图纹理（异步）
  if (backgroundUrl) {
    loadBackgroundTexture(backgroundUrl, groundMaterial)
  }

  // 创建辅助网格线，放置在地面略上方避免z-fighting
  const gridHelper = new THREE.GridHelper(
    Math.max(layoutWidth, layoutHeight),
    gridDivisions,
    0xcccccc, // 中心线颜色
    0xeeeeee, // 网格线颜色
  )
  gridHelper.name = 'gridHelper'
  gridHelper.position.y = GRID_Y_OFFSET
  groundGroup.add(gridHelper)

  return groundGroup
}

/**
 * 异步加载底图纹理
 * 加载成功时替换地面材质的map属性，失败时保持默认灰色
 * @param url 底图URL
 * @param material 地面材质
 */
function loadBackgroundTexture(url: string, material: THREE.MeshStandardMaterial): void {
  const textureLoader = new THREE.TextureLoader()

  textureLoader.load(
    url,
    // 加载成功回调
    (texture) => {
      texture.colorSpace = THREE.SRGBColorSpace
      // 翻转Y轴以匹配2D布局坐标系
      texture.flipY = false
      material.map = texture
      material.color.set(0xffffff)
      material.needsUpdate = true
    },
    // 加载进度回调（无需处理）
    undefined,
    // 加载失败回调：fallback到默认灰色材质
    () => {
      console.warn('[createGround] 底图加载失败，使用默认灰色地面:', url)
    },
  )
}

/**
 * 更新底图透明度
 * @param groundGroup 地面组
 * @param opacity 透明度(0~1)
 */
export function updateGroundOpacity(groundGroup: THREE.Group, opacity: number): void {
  const groundPlane = groundGroup.getObjectByName('groundPlane')
  if (groundPlane instanceof THREE.Mesh) {
    const material = groundPlane.material as THREE.MeshStandardMaterial
    material.transparent = opacity < 1
    material.opacity = opacity
    material.needsUpdate = true
  }
}
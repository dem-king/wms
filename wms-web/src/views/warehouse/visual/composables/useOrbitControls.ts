/**
 * OrbitControls封装composable
 * 提供预设视角切换、控制启用/禁用等功能
 */

import { shallowRef, type ShallowRef } from 'vue'
import * as THREE from 'three'
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js'
import type { PresetView, OrbitControlsConfig } from '../types/three-visual'
import { computePresetCameraPosition } from '../scene/buildWarehouseScene'

/**
 * useOrbitControls返回接口
 */
export interface UseOrbitControlsReturn {
  /** OrbitControls实例 */
  controls: ShallowRef<OrbitControls>
  /** 配置OrbitControls参数 */
  configure: (config: Partial<OrbitControlsConfig>) => void
  /** 切换到预设视角 */
  setPresetView: (preset: PresetView, layoutWidth: number, layoutHeight: number) => void
  /** 启用控制 */
  enableControls: () => void
  /** 禁用控制 */
  disableControls: () => void
}

/**
 * OrbitControls封装composable
 * @param camera Three.js相机
 * @param domElement 渲染器DOM元素
 * @returns OrbitControls管理接口
 */
export function useOrbitControls(
  camera: THREE.PerspectiveCamera,
  domElement: HTMLElement,
): UseOrbitControlsReturn {
  const controls = shallowRef<OrbitControls>(
    new OrbitControls(camera, domElement),
  )

  // 默认配置
  controls.value.enableDamping = true
  controls.value.dampingFactor = 0.08
  controls.value.minDistance = 5
  controls.value.maxDistance = 100
  controls.value.minPolarAngle = 0
  controls.value.maxPolarAngle = Math.PI / 2

  /**
   * 配置OrbitControls参数
   * @param config 部分配置参数
   */
  function configure(config: Partial<OrbitControlsConfig>): void {
    if (config.minDistance !== undefined) {
      controls.value.minDistance = config.minDistance
    }
    if (config.maxDistance !== undefined) {
      controls.value.maxDistance = config.maxDistance
    }
    if (config.enableDamping !== undefined) {
      controls.value.enableDamping = config.enableDamping
    }
    if (config.dampingFactor !== undefined) {
      controls.value.dampingFactor = config.dampingFactor
    }
    if (config.minPolarAngle !== undefined) {
      controls.value.minPolarAngle = config.minPolarAngle
    }
    if (config.maxPolarAngle !== undefined) {
      controls.value.maxPolarAngle = config.maxPolarAngle
    }
    controls.value.update()
  }

  /**
   * 切换到预设视角（立即跳转，动画由useCameraAnimation处理）
   * @param preset 预设视角名称
   * @param layoutWidth 库房布局宽度
   * @param layoutHeight 库房布局高度
   */
  function setPresetView(preset: PresetView, layoutWidth: number, layoutHeight: number): void {
    const { position, lookAt } = computePresetCameraPosition(preset, layoutWidth, layoutHeight)
    camera.position.copy(position)
    controls.value.target.copy(lookAt)
    controls.value.update()
  }

  /**
   * 启用控制
   */
  function enableControls(): void {
    controls.value.enabled = true
  }

  /**
   * 禁用控制
   */
  function disableControls(): void {
    controls.value.enabled = false
  }

  return {
    controls,
    configure,
    setPresetView,
    enableControls,
    disableControls,
  }
}
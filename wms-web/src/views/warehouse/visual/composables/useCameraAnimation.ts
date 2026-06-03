/**
 * 相机平滑动画composable
 * 使用GSAP实现相机推近/退回/预设视角切换动画
 */

import { ref, type Ref, onUnmounted } from 'vue'
import * as THREE from 'three'
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls.js'
import gsap from 'gsap'
import type { PresetView, CabinetNode3D } from '../types/three-visual'
import { computePresetCameraPosition } from '../scene/buildWarehouseScene'

/** 默认动画时长（秒） */
const DEFAULT_DURATION = 0.8

/** 推近动画时长（秒） */
const APPROACH_DURATION = 1.0

/** 默认缓动函数 */
const DEFAULT_EASE = 'power2.inOut'

/**
 * useCameraAnimation返回接口
 */
export interface UseCameraAnimationReturn {
  /** 相机动画到指定位置 */
  animateTo: (target: THREE.Vector3, lookAt: THREE.Vector3, duration?: number) => Promise<void>
  /** 相机推近到存放柜正面 */
  animateToCabinet: (cabinet: CabinetNode3D, controls: OrbitControls) => Promise<void>
  /** 相机退回全景视角 */
  animateToOverview: (savedPosition: THREE.Vector3, savedTarget: THREE.Vector3, controls: OrbitControls) => Promise<void>
  /** 相机切换到预设视角 */
  animateToPreset: (preset: PresetView, layoutWidth: number, layoutHeight: number, controls: OrbitControls) => Promise<void>
  /** 是否正在动画中 */
  isAnimating: Ref<boolean>
}

/**
 * 相机平滑动画composable
 * @param camera Three.js相机
 * @returns 相机动画接口
 */
export function useCameraAnimation(
  camera: THREE.PerspectiveCamera,
): UseCameraAnimationReturn {
  const isAnimating = ref(false)

  /** 追踪当前活跃的GSAP动画，组件卸载时统一kill防止泄漏 */
  const activeAnimations: (gsap.core.Tween | gsap.core.Timeline)[] = []

  /**
   * 清理所有活跃动画
   */
  function killActiveAnimations(): void {
    for (const tween of activeAnimations) {
      tween.kill()
    }
    activeAnimations.length = 0
    isAnimating.value = false
  }

  onUnmounted(() => {
    killActiveAnimations()
  })

  /**
   * 相机动画到指定位置
   * @param target 目标位置
   * @param lookAt 注视目标
   * @param duration 动画时长（秒）
   */
  async function animateTo(
    target: THREE.Vector3,
    lookAt: THREE.Vector3,
    duration: number = DEFAULT_DURATION,
  ): Promise<void> {
    if (isAnimating.value) {
      return
    }

    isAnimating.value = true

    return new Promise((resolve) => {
      // 动画相机位置
      const tween = gsap.to(camera.position, {
        x: target.x,
        y: target.y,
        z: target.z,
        duration,
        ease: DEFAULT_EASE,
        onUpdate: () => {
          camera.lookAt(lookAt)
        },
        onComplete: () => {
          // 从活跃列表移除
          const idx = activeAnimations.indexOf(tween)
          if (idx !== -1) activeAnimations.splice(idx, 1)
          isAnimating.value = false
          resolve()
        },
      })
      activeAnimations.push(tween)
    })
  }

  /**
   * 相机推近到存放柜正面
   * 计算柜子正面中心点前方位置，平滑动画移动
   * @param cabinet 存放柜3D节点
   * @param controls OrbitControls
   */
  async function animateToCabinet(
    cabinet: CabinetNode3D,
    controls: OrbitControls,
  ): Promise<void> {
    if (isAnimating.value) {
      return
    }

    isAnimating.value = true

    // 计算柜子正面中心点
    const frontCenter = new THREE.Vector3(
      cabinet.position.x,
      cabinet.height / 2,
      cabinet.position.z - cabinet.depth / 2,
    )

    // 相机在正面正前方，距离为柜子高度的1.5倍
    const cameraTarget = new THREE.Vector3(
      frontCenter.x,
      frontCenter.y + cabinet.height * 0.3,
      frontCenter.z - cabinet.height * 1.5,
    )

    return new Promise((resolve) => {
      const timeline = gsap.timeline({
        onComplete: () => {
          const idx = activeAnimations.indexOf(timeline)
          if (idx !== -1) activeAnimations.splice(idx, 1)
          isAnimating.value = false
          resolve()
        },
      })

      // 同时动画相机位置和controls.target
      timeline.to(camera.position, {
        x: cameraTarget.x,
        y: cameraTarget.y,
        z: cameraTarget.z,
        duration: APPROACH_DURATION,
        ease: DEFAULT_EASE,
      }, 0)

      timeline.to(controls.target, {
        x: frontCenter.x,
        y: frontCenter.y,
        z: frontCenter.z,
        duration: APPROACH_DURATION,
        ease: DEFAULT_EASE,
        onUpdate: () => {
          controls.update()
        },
      }, 0)

      activeAnimations.push(timeline)
    })
  }

  /**
   * 相机退回全景视角
   * @param savedPosition 保存的推近前位置
   * @param savedTarget 保存的推近前target
   * @param controls OrbitControls
   */
  async function animateToOverview(
    savedPosition: THREE.Vector3,
    savedTarget: THREE.Vector3,
    controls: OrbitControls,
  ): Promise<void> {
    if (isAnimating.value) {
      return
    }

    isAnimating.value = true

    return new Promise((resolve) => {
      const timeline = gsap.timeline({
        onComplete: () => {
          const idx = activeAnimations.indexOf(timeline)
          if (idx !== -1) activeAnimations.splice(idx, 1)
          isAnimating.value = false
          resolve()
        },
      })

      timeline.to(camera.position, {
        x: savedPosition.x,
        y: savedPosition.y,
        z: savedPosition.z,
        duration: DEFAULT_DURATION,
        ease: DEFAULT_EASE,
      }, 0)

      timeline.to(controls.target, {
        x: savedTarget.x,
        y: savedTarget.y,
        z: savedTarget.z,
        duration: DEFAULT_DURATION,
        ease: DEFAULT_EASE,
        onUpdate: () => {
          controls.update()
        },
      }, 0)

      activeAnimations.push(timeline)
    })
  }

  /**
   * 相机切换到预设视角
   * @param preset 预设视角名称
   * @param layoutWidth 库房布局宽度
   * @param layoutHeight 库房布局高度
   * @param controls OrbitControls
   */
  async function animateToPreset(
    preset: PresetView,
    layoutWidth: number,
    layoutHeight: number,
    controls: OrbitControls,
  ): Promise<void> {
    if (isAnimating.value) {
      return
    }

    const { position, lookAt } = computePresetCameraPosition(preset, layoutWidth, layoutHeight)

    isAnimating.value = true

    return new Promise((resolve) => {
      const timeline = gsap.timeline({
        onComplete: () => {
          const idx = activeAnimations.indexOf(timeline)
          if (idx !== -1) activeAnimations.splice(idx, 1)
          isAnimating.value = false
          resolve()
        },
      })

      timeline.to(camera.position, {
        x: position.x,
        y: position.y,
        z: position.z,
        duration: DEFAULT_DURATION,
        ease: DEFAULT_EASE,
      }, 0)

      timeline.to(controls.target, {
        x: lookAt.x,
        y: lookAt.y,
        z: lookAt.z,
        duration: DEFAULT_DURATION,
        ease: DEFAULT_EASE,
        onUpdate: () => {
          controls.update()
        },
      }, 0)

      activeAnimations.push(timeline)
    })
  }

  return {
    animateTo,
    animateToCabinet,
    animateToOverview,
    animateToPreset,
    isAnimating,
  }
}
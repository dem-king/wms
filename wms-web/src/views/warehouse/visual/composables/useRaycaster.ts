/**
 * Raycaster射线拾取composable
 * 处理鼠标点击/双击事件，检测3D场景中的存放柜/库位/区域
 */

import { shallowRef, type ShallowRef } from 'vue'
import * as THREE from 'three'
import type { EntityId } from '@/types/warehouse'
import type { WarehouseSceneResult } from '../types/three-visual'

/** 单击延迟时间（ms），用于区分单击和双击 */
const CLICK_DELAY = 150

/**
 * useRaycaster返回接口
 */
export interface UseRaycasterReturn {
  /** Raycaster实例 */
  raycaster: ShallowRef<THREE.Raycaster>
  /** 通用拾取 */
  pick: (event: MouseEvent, targets: THREE.Object3D[], camera: THREE.Camera, container: HTMLElement) => THREE.Intersection[]
  /** 存放柜拾取 */
  pickCabinet: (event: MouseEvent, sceneResult: WarehouseSceneResult, camera: THREE.Camera, container: HTMLElement) => EntityId | null
  /** 库位拾取 */
  pickBin: (event: MouseEvent, sceneResult: WarehouseSceneResult, camera: THREE.Camera, container: HTMLElement) => { cabinetId: EntityId; binId: EntityId } | null
  /** 区域拾取 */
  pickArea: (event: MouseEvent, sceneResult: WarehouseSceneResult, camera: THREE.Camera, container: HTMLElement) => EntityId | null
  /** 地面空白拾取 */
  pickGround: (event: MouseEvent, sceneResult: WarehouseSceneResult, camera: THREE.Camera, container: HTMLElement) => boolean
  /** 创建单击/双击事件处理器 */
  createClickHandlers: (callbacks: {
    onClick: (event: MouseEvent) => void
    onDblClick: (event: MouseEvent) => void
  }) => { handleClick: (event: MouseEvent) => void; handleDblClick: (event: MouseEvent) => void; cleanup: () => void }
}

/**
 * Raycaster射线拾取composable
 * @returns 射线拾取接口
 */
export function useRaycaster(): UseRaycasterReturn {
  const raycaster = shallowRef<THREE.Raycaster>(new THREE.Raycaster())

  // 复用NDC坐标向量，避免每次拾取创建新对象
  const _ndc = new THREE.Vector2()

  /**
   * 计算鼠标NDC坐标
   * @param event 鼠标事件
   * @param container 容器DOM元素
   * @returns NDC坐标（复用内部向量）
   */
  function computeNDC(event: MouseEvent, container: HTMLElement): THREE.Vector2 {
    const rect = container.getBoundingClientRect()
    _ndc.x = ((event.clientX - rect.left) / rect.width) * 2 - 1
    _ndc.y = -((event.clientY - rect.top) / rect.height) * 2 + 1
    return _ndc
  }

  /**
   * 通用拾取
   * @param event 鼠标事件
   * @param targets 检测目标列表
   * @param camera 相机
   * @param container 容器DOM元素
   * @returns 交叉结果列表
   */
  function pick(
    event: MouseEvent,
    targets: THREE.Object3D[],
    camera: THREE.Camera,
    container: HTMLElement,
  ): THREE.Intersection[] {
    const ndc = computeNDC(event, container)
    raycaster.value.setFromCamera(ndc, camera)
    return raycaster.value.intersectObjects(targets, true)
  }

  /**
   * 存放柜拾取
   * @param event 鼠标事件
   * @param sceneResult 场景构建结果
   * @param camera 相机
   * @param container 容器DOM元素
   * @returns 存放柜ID，未命中返回null
   */
  function pickCabinet(
    event: MouseEvent,
    sceneResult: WarehouseSceneResult,
    camera: THREE.Camera,
    container: HTMLElement,
  ): EntityId | null {
    const targets = sceneResult.cabinetInstancedMeshes
    if (targets.length === 0) {
      return null
    }

    const ndc = computeNDC(event, container)
    raycaster.value.setFromCamera(ndc, camera)

    for (let meshIdx = 0; meshIdx < targets.length; meshIdx++) {
      const instancedMesh = targets[meshIdx]
      const intersects = raycaster.value.intersectObject(instancedMesh)
      if (intersects.length > 0) {
        const instanceId = intersects[0].instanceId
        if (instanceId !== undefined) {
          // 使用对应InstancedMesh的局部映射
          const localMap = sceneResult.cabinetInstanceMaps[meshIdx]
          return localMap?.get(instanceId) ?? null
        }
      }
    }

    return null
  }

  /**
   * 库位拾取
   * @param event 鼠标事件
   * @param sceneResult 场景构建结果
   * @param camera 相机
   * @param container 容器DOM元素
   * @returns 库位信息，未命中返回null
   */
  function pickBin(
    event: MouseEvent,
    sceneResult: WarehouseSceneResult,
    camera: THREE.Camera,
    container: HTMLElement,
  ): { cabinetId: EntityId; binId: EntityId } | null {
    if (!sceneResult.binInstancedMesh || !sceneResult.binInstancedMesh.visible) {
      return null
    }

    const ndc = computeNDC(event, container)
    raycaster.value.setFromCamera(ndc, camera)

    const intersects = raycaster.value.intersectObject(sceneResult.binInstancedMesh)
    if (intersects.length > 0) {
      const instanceId = intersects[0].instanceId
      if (instanceId !== undefined) {
        return sceneResult.binInstanceMap.get(instanceId) ?? null
      }
    }

    return null
  }

  /**
   * 区域拾取
   * @param event 鼠标事件
   * @param sceneResult 场景构建结果
   * @param camera 相机
   * @param container 容器DOM元素
   * @returns 区域ID，未命中返回null
   */
  function pickArea(
    event: MouseEvent,
    sceneResult: WarehouseSceneResult,
    camera: THREE.Camera,
    container: HTMLElement,
  ): EntityId | null {
    if (sceneResult.areaMeshes.length === 0) {
      return null
    }

    const ndc = computeNDC(event, container)
    raycaster.value.setFromCamera(ndc, camera)

    const intersects = raycaster.value.intersectObjects(sceneResult.areaMeshes)
    if (intersects.length > 0) {
      const mesh = intersects[0].object
      return mesh.userData.areaId ?? null
    }

    return null
  }

  /**
   * 地面空白拾取
   * @param event 鼠标事件
   * @param sceneResult 场景构建结果
   * @param camera 相机
   * @param container 容器DOM元素
   * @returns 是否点击了地面空白处
   */
  function pickGround(
    event: MouseEvent,
    sceneResult: WarehouseSceneResult,
    camera: THREE.Camera,
    container: HTMLElement,
  ): boolean {
    if (!sceneResult.groundMesh) {
      return false
    }

    const ndc = computeNDC(event, container)
    raycaster.value.setFromCamera(ndc, camera)

    const intersects = raycaster.value.intersectObject(sceneResult.groundMesh)
    return intersects.length > 0
  }

  /**
   * 创建单击/双击事件处理器
   * 单击延迟CLICK_DELAY执行，若延迟内收到dblclick则取消单击
   * @param callbacks 回调函数
   * @returns 事件处理器
   */
  function createClickHandlers(callbacks: {
    onClick: (event: MouseEvent) => void
    onDblClick: (event: MouseEvent) => void
  }): {
    handleClick: (event: MouseEvent) => void
    handleDblClick: (event: MouseEvent) => void
    cleanup: () => void
  } {
    let clickTimer: ReturnType<typeof setTimeout> | null = null

    function handleClick(event: MouseEvent): void {
      // 延迟执行单击，等待可能的双击
      if (clickTimer) {
        clearTimeout(clickTimer)
        clickTimer = null
      }

      clickTimer = setTimeout(() => {
        clickTimer = null
        callbacks.onClick(event)
      }, CLICK_DELAY)
    }

    function handleDblClick(event: MouseEvent): void {
      // 取消待执行的单击
      if (clickTimer) {
        clearTimeout(clickTimer)
        clickTimer = null
      }
      callbacks.onDblClick(event)
    }

    function cleanup(): void {
      if (clickTimer) {
        clearTimeout(clickTimer)
        clickTimer = null
      }
    }

    return { handleClick, handleDblClick, cleanup }
  }

  return {
    raycaster,
    pick,
    pickCabinet,
    pickBin,
    pickArea,
    pickGround,
    createClickHandlers,
  }
}
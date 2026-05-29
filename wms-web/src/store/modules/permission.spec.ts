import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'

const routerAddRouteMock = vi.hoisted(() => vi.fn())
const routerRemoveRouteMock = vi.hoisted(() => vi.fn())
const routerHasRouteMock = vi.hoisted(() => vi.fn(() => true))

vi.mock('@/router', () => ({
  default: {
    addRoute: routerAddRouteMock,
    removeRoute: routerRemoveRouteMock,
    hasRoute: routerHasRouteMock,
  },
}))

import { usePermissionStore } from './permission'

describe('permission store', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    setActivePinia(createPinia())
  })

  it('removes previously added dynamic routes when resetting permission state', () => {
    const store = usePermissionStore()

    store.generateRoutes([
      {
        id: '1',
        parentId: '0',
        menuName: '系统管理',
        menuCode: 'SystemRoot',
        path: 'system',
        component: '',
        redirect: '',
        menuType: 1,
        visible: 1,
        isExternal: 0,
        isCache: 1,
        sortOrder: 1,
        permCode: '',
        icon: 'Setting',
        children: [],
      },
    ])

    store.resetPermission()

    expect(routerRemoveRouteMock).toHaveBeenCalledWith('SystemRoot')
    expect(routerRemoveRouteMock).toHaveBeenCalledWith('DynamicNotFound')
    expect(store.hasRoutes()).toBe(false)
  })

  it('cleans stale routes before generating a second dynamic route batch', () => {
    const store = usePermissionStore()

    store.generateRoutes([
      {
        id: '1',
        parentId: '0',
        menuName: '系统管理',
        menuCode: 'SystemRoot',
        path: 'system',
        component: '',
        redirect: '',
        menuType: 1,
        visible: 1,
        isExternal: 0,
        isCache: 1,
        sortOrder: 1,
        permCode: '',
        icon: 'Setting',
        children: [],
      },
    ])

    store.generateRoutes([
      {
        id: '2',
        parentId: '0',
        menuName: '业务管理',
        menuCode: 'BusinessRoot',
        path: 'business',
        component: '',
        redirect: '',
        menuType: 1,
        visible: 1,
        isExternal: 0,
        isCache: 1,
        sortOrder: 2,
        permCode: '',
        icon: 'List',
        children: [],
      },
    ])

    expect(routerRemoveRouteMock).toHaveBeenCalledWith('SystemRoot')
    expect(routerAddRouteMock).toHaveBeenCalledWith(expect.objectContaining({
      name: 'BusinessRoot',
      path: '/business',
    }))
  })
})

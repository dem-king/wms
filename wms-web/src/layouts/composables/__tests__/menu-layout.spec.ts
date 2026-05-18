import { describe, expect, it } from 'vitest'

import {
  buildSplitMenuState,
  buildSidebarPreferenceState,
  getLayoutMetrics,
  resolveHeaderMenuAlignment,
  resolveLayoutFlags,
} from '../menu-layout'

describe('resolveLayoutFlags', () => {
  it('forces sidebar-nav on mobile', () => {
    expect(resolveLayoutFlags('header-mixed-nav', true).layout).toBe('sidebar-nav')
  })

  it('marks split menu layouts correctly', () => {
    expect(resolveLayoutFlags('mixed-nav', false).isSplitMenu).toBe(true)
    expect(resolveLayoutFlags('header-sidebar-nav', false).isSplitMenu).toBe(false)
  })

  it('returns consistent flags for header-sidebar-nav', () => {
    const flags = resolveLayoutFlags('header-sidebar-nav', false)

    expect(flags.showHeader).toBe(true)
    expect(flags.showSidebar).toBe(true)
    expect(flags.showHeaderMenu).toBe(false)
  })
})

describe('buildSplitMenuState', () => {
  const tree = [
    {
      id: 1,
      menuName: '系统管理',
      menuCode: 'system',
      parentId: 0,
      menuType: 1,
      path: 'system',
      component: '',
      redirect: '',
      icon: '',
      isExternal: 0,
      isCache: 1,
      visible: 1,
      sortOrder: 1,
      permCode: '',
      children: [
        {
          id: 2,
          menuName: '用户管理',
          menuCode: 'user',
          parentId: 1,
          menuType: 2,
          path: 'user',
          component: '',
          redirect: '',
          icon: '',
          isExternal: 0,
          isCache: 1,
          visible: 1,
          sortOrder: 1,
          permCode: '',
          children: [],
        },
      ],
    },
  ]

  it('finds the active root menu from nested child routes', () => {
    const state = buildSplitMenuState(tree, '/system/user')

    expect(state.activeRootMenu?.id).toBe(1)
    expect(state.sidebarMenus).toHaveLength(1)
    expect(state.sidebarBasePath).toBe('/system')
  })

  it('keeps active root menu when clicking a root item with children', () => {
    const state = buildSplitMenuState(tree, '/system/user', 1)

    expect(state.activeRootMenu?.id).toBe(1)
    expect(state.sidebarMenus[0]?.id).toBe(2)
    expect(state.sidebarBasePath).toBe('/system')
  })
})

describe('resolveHeaderMenuAlignment', () => {
  it('maps header menu alignment to flex positions', () => {
    expect(resolveHeaderMenuAlignment('start')).toBe('flex-start')
    expect(resolveHeaderMenuAlignment('center')).toBe('center')
    expect(resolveHeaderMenuAlignment('end')).toBe('flex-end')
  })
})

describe('getLayoutMetrics', () => {
  it('computes split sidebar width using mixedWidth and sidebar width', () => {
    const metrics = getLayoutMetrics(resolveLayoutFlags('sidebar-mixed-nav', false), {
      collapseWidth: 60,
      collapsed: false,
      headerHeight: 50,
      headerVisible: true,
      mixedWidth: 80,
      sidebarVisible: true,
      sidebarWidth: 210,
      tabbarHeight: 38,
    })

    expect(metrics.sidebarWidth).toBe(290)
    expect(metrics.mainPaddingLeft).toBe(290)
    expect(metrics.mainPaddingTop).toBe(50)
  })

  it('drops sidebar width to zero on mobile fallback', () => {
    const metrics = getLayoutMetrics(resolveLayoutFlags('header-mixed-nav', true), {
      collapseWidth: 60,
      collapsed: false,
      headerHeight: 50,
      headerVisible: true,
      mixedWidth: 80,
      sidebarVisible: true,
      sidebarWidth: 210,
      tabbarHeight: 38,
    })

    expect(metrics.sidebarWidth).toBe(210)
    expect(metrics.mainPaddingTop).toBe(50)
  })
})

describe('buildSidebarPreferenceState', () => {
  it('exposes mixed width control for split layouts only', () => {
    expect(buildSidebarPreferenceState('sidebar-mixed-nav', true, false).showMixedWidth).toBe(true)
    expect(buildSidebarPreferenceState('sidebar-nav', true, false).showMixedWidth).toBe(false)
  })

  it('only allows hover expand when sidebar is enabled and collapsed', () => {
    expect(buildSidebarPreferenceState('sidebar-nav', true, true).allowHoverExpand).toBe(true)
    expect(buildSidebarPreferenceState('sidebar-nav', true, false).allowHoverExpand).toBe(false)
    expect(buildSidebarPreferenceState('sidebar-nav', false, true).allowHoverExpand).toBe(false)
  })
})

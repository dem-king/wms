import type { MenuTreeNode } from '@/types/auth'
import type { HeaderPreferences, LayoutType } from '@/types/preferences'

export interface LayoutFlags {
  isFullContent: boolean
  isHeaderMixedNav: boolean
  isHeaderNav: boolean
  isHeaderSidebarNav: boolean
  isMixedNav: boolean
  isSidebarMixedNav: boolean
  isSidebarNav: boolean
  isSplitMenu: boolean
  layout: LayoutType
  showHeader: boolean
  showHeaderMenu: boolean
  showSidebar: boolean
}

export interface SplitMenuState {
  activeRootMenu: MenuTreeNode | null
  sidebarBasePath: string
  sidebarMenus: MenuTreeNode[]
}

export interface LayoutMetricsInput {
  collapseWidth: number
  collapsed: boolean
  headerHeight: number
  headerVisible: boolean
  mixedWidth: number
  sidebarVisible: boolean
  sidebarWidth: number
  tabbarHeight: number
}

export interface LayoutMetrics {
  headerOffsetTop: number
  mainPaddingLeft: number
  mainPaddingTop: number
  sidebarInnerWidth: number
  sidebarTop: number
  sidebarWidth: number
  tabbarHeight: number
}

export interface HorizontalScrollStateInput {
  clientWidth: number
  scrollLeft: number
  scrollWidth: number
}

export interface HorizontalScrollState {
  canScrollLeft: boolean
  canScrollRight: boolean
  isOverflowing: boolean
}

export function resolveLayoutFlags(layout: LayoutType, isMobile: boolean): LayoutFlags {
  const effectiveLayout: LayoutType = isMobile ? 'sidebar-nav' : layout
  const isFullContent = effectiveLayout === 'full-content'
  const isHeaderNav = effectiveLayout === 'header-nav'
  const isMixedNav = effectiveLayout === 'mixed-nav'
  const isSidebarMixedNav = effectiveLayout === 'sidebar-mixed-nav'
  const isHeaderSidebarNav = effectiveLayout === 'header-sidebar-nav'
  const isHeaderMixedNav = effectiveLayout === 'header-mixed-nav'
  const isSidebarNav = effectiveLayout === 'sidebar-nav'
  const isSplitMenu = isSidebarMixedNav || isMixedNav || isHeaderMixedNav

  return {
    layout: effectiveLayout,
    isFullContent,
    isHeaderMixedNav,
    isHeaderNav,
    isHeaderSidebarNav,
    isMixedNav,
    isSidebarMixedNav,
    isSidebarNav,
    isSplitMenu,
    showHeader: !isFullContent,
    showHeaderMenu: isHeaderNav || isMixedNav,
    showSidebar: !isFullContent && !isHeaderNav,
  }
}

export function resolveHeaderMenuAlignment(
  align: HeaderPreferences['menuAlign'],
): 'center' | 'flex-end' | 'flex-start' {
  if (align === 'center') return 'center'
  if (align === 'end') return 'flex-end'
  return 'flex-start'
}

export function resolveHorizontalScrollState(
  input: HorizontalScrollStateInput,
): HorizontalScrollState {
  const { clientWidth, scrollLeft, scrollWidth } = input
  const isOverflowing = scrollWidth - clientWidth > 4

  if (!isOverflowing) {
    return {
      canScrollLeft: false,
      canScrollRight: false,
      isOverflowing: false,
    }
  }

  return {
    canScrollLeft: scrollLeft > 4,
    canScrollRight: scrollLeft + clientWidth < scrollWidth - 4,
    isOverflowing: true,
  }
}

export function resolveMenuPath(menu: MenuTreeNode, basePath = ''): string {
  if (menu.path.startsWith('/')) {
    return menu.path
  }

  if (basePath) {
    return `${basePath}/${menu.path}`.replace(/\/+/g, '/')
  }

  return `/${menu.path}`.replace(/\/+/g, '/')
}

export function isRootMenuHighlighted(
  menu: MenuTreeNode,
  currentPath: string,
  activeRootMenuId?: MenuTreeNode['id'] | null,
): boolean {
  if (activeRootMenuId != null && menu.id === activeRootMenuId) {
    return true
  }

  const rootPath = resolveMenuPath(menu)
  if (matchesPath(rootPath, currentPath)) {
    return true
  }

  return hasMatchingChild(menu.children ?? [], currentPath, rootPath)
}

function findActiveRootMenu(
  menuTree: MenuTreeNode[],
  currentPath: string,
  preferredRootId?: MenuTreeNode['id'] | null,
): MenuTreeNode | null {
  if (preferredRootId != null) {
    const preferred = menuTree.find((menu) => menu.id === preferredRootId) ?? null
    if (preferred) {
      return preferred
    }
  }

  return (
    menuTree.find((menu) => {
      return isRootMenuHighlighted(menu, currentPath)
    }) ?? null
  )
}

function hasMatchingChild(children: MenuTreeNode[], currentPath: string, basePath: string): boolean {
  return children.some((child) => {
    const childPath = resolveMenuPath(child, basePath)

    if (matchesPath(childPath, currentPath)) {
      return true
    }

    return hasMatchingChild(child.children ?? [], currentPath, childPath)
  })
}

function matchesPath(targetPath: string, currentPath: string): boolean {
  return currentPath === targetPath || currentPath.startsWith(`${targetPath}/`)
}

export function buildSplitMenuState(
  menuTree: MenuTreeNode[],
  currentPath: string,
  preferredRootId?: MenuTreeNode['id'] | null,
): SplitMenuState {
  if (currentPath === '/dashboard') {
    return {
      activeRootMenu: null,
      sidebarBasePath: '',
      sidebarMenus: [],
    }
  }

  const activeRootMenu = findActiveRootMenu(menuTree, currentPath, preferredRootId)

  return {
    activeRootMenu,
    sidebarBasePath: activeRootMenu ? resolveMenuPath(activeRootMenu) : '',
    sidebarMenus: activeRootMenu?.children ?? [],
  }
}

export function getLayoutMetrics(flags: LayoutFlags, input: LayoutMetricsInput): LayoutMetrics {
  const sidebarWidth = resolveSidebarWidth(flags, input)
  const headerOffsetTop =
    input.headerVisible &&
    (flags.isHeaderNav || flags.isMixedNav || flags.isHeaderMixedNav || flags.isHeaderSidebarNav)
      ? input.headerHeight
      : 0

  return {
    headerOffsetTop,
    mainPaddingLeft: input.sidebarVisible ? sidebarWidth : 0,
    mainPaddingTop: input.headerVisible ? input.headerHeight : 0,
    sidebarInnerWidth:
      flags.isSidebarMixedNav || flags.isHeaderMixedNav ? input.sidebarWidth : sidebarWidth,
    sidebarTop: headerOffsetTop,
    sidebarWidth,
    tabbarHeight: input.tabbarHeight,
  }
}

export function buildSidebarPreferenceState(
  layout: LayoutType,
  sidebarEnable: boolean,
  sidebarCollapsed: boolean,
) {
  return {
    allowHoverExpand: sidebarEnable && sidebarCollapsed,
    showMixedWidth: layout === 'sidebar-mixed-nav' || layout === 'header-mixed-nav',
  }
}

function resolveSidebarWidth(flags: LayoutFlags, input: LayoutMetricsInput): number {
  if (!input.sidebarVisible) {
    return 0
  }

  if (flags.isSidebarMixedNav || flags.isHeaderMixedNav) {
    return input.mixedWidth + (input.collapsed ? 0 : input.sidebarWidth)
  }

  if (input.collapsed) {
    return input.collapseWidth
  }

  return input.sidebarWidth
}

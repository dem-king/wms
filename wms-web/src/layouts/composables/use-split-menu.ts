import { computed, ref, watch } from 'vue'
import type { ComputedRef, Ref } from 'vue'

import { buildSplitMenuState, resolveMenuPath } from './menu-layout'
import type { LayoutFlags } from './menu-layout'
import type { MenuTreeNode } from '@/types/auth'

const sharedActiveRootMenuId = ref<MenuTreeNode['id'] | null>(null)

interface UseSplitMenuOptions {
  layoutState: ComputedRef<LayoutFlags>
  menuTree: ComputedRef<MenuTreeNode[]>
  routePath: ComputedRef<string>
}

export function useSplitMenu(options: UseSplitMenuOptions) {
  const { layoutState, menuTree, routePath } = options

  const splitMenuState = computed(() =>
    buildSplitMenuState(
      menuTree.value,
      routePath.value,
      layoutState.value.isSplitMenu ? sharedActiveRootMenuId.value : null,
    ),
  )

  watch(
    splitMenuState,
    (state) => {
      if (!layoutState.value.isSplitMenu) {
        sharedActiveRootMenuId.value = null
        return
      }

      if (state.activeRootMenu) {
        sharedActiveRootMenuId.value = state.activeRootMenu.id
      }
    },
    { immediate: true },
  )

  function setActiveRootMenu(menu: MenuTreeNode | null) {
    sharedActiveRootMenuId.value = menu?.id ?? null
  }

  function navigateToMenu(router: { push: (path: string) => unknown }, menu: MenuTreeNode) {
    setActiveRootMenu(menu)

    const targetPath = findFirstNavigablePath(menu)
    if (targetPath) {
      router.push(targetPath)
    }
  }

  return {
    activeRootMenuId: sharedActiveRootMenuId as Ref<MenuTreeNode['id'] | null>,
    activeRootMenu: computed(() => splitMenuState.value.activeRootMenu),
    navigateToMenu,
    setActiveRootMenu,
    sidebarBasePath: computed(() => splitMenuState.value.sidebarBasePath),
    sidebarMenus: computed(() => splitMenuState.value.sidebarMenus),
  }
}

function findFirstNavigablePath(menu: MenuTreeNode, basePath = ''): string | null {
  const currentPath = resolveMenuPath(menu, basePath)

  if (menu.menuType === 2 || !menu.children?.length) {
    return currentPath
  }

  for (const child of menu.children) {
    const childPath = findFirstNavigablePath(child, currentPath)
    if (childPath) {
      return childPath
    }
  }

  return currentPath
}

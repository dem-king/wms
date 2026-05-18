# Layout Preferences Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 收敛 `wms-web` 的主题与布局偏好实现，修复双栏布局、头部/侧栏切换和关键偏好项不生效的问题。

**Architecture:** 以 `usePreferences()` 为统一布局状态入口，新增轻量布局辅助模块承载布局判定与双栏菜单计算，再让 `DefaultLayout`、`Sidebar`、`Navbar` 统一消费这些结果。偏好抽屉只暴露真实可生效的配置，并与布局预览保持一致。

**Tech Stack:** Vue 3、`<script setup lang="ts">`、Vue Router、Pinia、Element Plus、Vite、Vue TSC、Vitest

---

### Task 1: 建立布局辅助测试基线

**Files:**
- Modify: `package.json`
- Create: `vitest.config.ts`
- Create: `src/layouts/composables/menu-layout.ts`
- Create: `src/layouts/composables/__tests__/menu-layout.spec.ts`

**Step 1: Write the failing test**

```ts
import { describe, expect, it } from 'vitest'
import { buildSplitMenuState, resolveLayoutFlags } from '../menu-layout'

describe('resolveLayoutFlags', () => {
  it('forces sidebar-nav on mobile', () => {
    expect(resolveLayoutFlags('header-mixed-nav', true).layout).toBe('sidebar-nav')
  })

  it('marks split menu layouts correctly', () => {
    expect(resolveLayoutFlags('mixed-nav', false).isSplitMenu).toBe(true)
  })
})

describe('buildSplitMenuState', () => {
  it('finds the active root menu from nested child routes', () => {
    const tree = [
      { id: 1, path: '/system', children: [{ id: 2, path: '/system/user' }] },
    ]
    const state = buildSplitMenuState(tree, '/system/user')
    expect(state.activeRootMenu?.id).toBe(1)
    expect(state.sidebarMenus).toHaveLength(1)
  })
})
```

**Step 2: Run test to verify it fails**

Run: `npm install`
Run: `npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts`
Expected: FAIL with missing module or failing assertions

**Step 3: Write minimal implementation**

```ts
export function resolveLayoutFlags(layout: LayoutType, isMobile: boolean) {
  const effectiveLayout = isMobile ? 'sidebar-nav' : layout
  return {
    layout: effectiveLayout,
    isSplitMenu: ['sidebar-mixed-nav', 'mixed-nav', 'header-mixed-nav'].includes(effectiveLayout),
  }
}
```

**Step 4: Run test to verify it passes**

Run: `npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts`
Expected: PASS

**Step 5: Commit**

```bash
git add package.json vitest.config.ts src/layouts/composables/menu-layout.ts src/layouts/composables/__tests__/menu-layout.spec.ts
git commit -m "test: add layout state helper coverage"
```

### Task 2: 收敛 `usePreferences()` 的布局计算态

**Files:**
- Modify: `src/utils/preferences/use-preferences.ts`
- Test: `src/layouts/composables/__tests__/menu-layout.spec.ts`

**Step 1: Write the failing test**

```ts
it('returns consistent flags for header-sidebar-nav', () => {
  const flags = resolveLayoutFlags('header-sidebar-nav', false)
  expect(flags.showHeader).toBe(true)
  expect(flags.showSidebar).toBe(true)
  expect(flags.showHeaderMenu).toBe(false)
})
```

**Step 2: Run test to verify it fails**

Run: `npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts -t "returns consistent flags for header-sidebar-nav"`
Expected: FAIL because the helper does not yet expose the required flags

**Step 3: Write minimal implementation**

```ts
const layoutState = computed(() => resolveLayoutFlags(appPreferences.value.layout, isMobile.value))

return {
  layout: computed(() => layoutState.value.layout),
  layoutState,
  isFullContent: computed(() => layoutState.value.isFullContent),
  isShowHeaderNav: computed(() => layoutState.value.showHeader),
}
```

**Step 4: Run test to verify it passes**

Run: `npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts`
Expected: PASS

**Step 5: Commit**

```bash
git add src/utils/preferences/use-preferences.ts src/layouts/composables/__tests__/menu-layout.spec.ts
git commit -m "refactor: centralize layout preference flags"
```

### Task 3: 重写 `DefaultLayout` 的宽度与偏移计算

**Files:**
- Modify: `src/layouts/DefaultLayout.vue`
- Modify: `src/layouts/components/TagsView.vue`
- Test: `src/layouts/composables/__tests__/menu-layout.spec.ts`

**Step 1: Write the failing test**

```ts
it('computes split sidebar width using mixedWidth and sidebar width', () => {
  const flags = resolveLayoutFlags('sidebar-mixed-nav', false)
  const style = getLayoutMetrics(flags, {
    sidebarWidth: 210,
    mixedWidth: 80,
    collapseWidth: 60,
    collapsed: false,
    headerHeight: 50,
    tabbarHeight: 38,
  })
  expect(style.sidebarWidth).toBe(290)
})
```

**Step 2: Run test to verify it fails**

Run: `npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts -t "computes split sidebar width"`
Expected: FAIL because `getLayoutMetrics` is not implemented

**Step 3: Write minimal implementation**

```ts
const metrics = computed(() =>
  getLayoutMetrics(layoutState.value, {
    sidebarWidth: preferences.sidebar.width,
    mixedWidth: preferences.sidebar.mixedWidth,
    collapseWidth: preferences.sidebar.collapseWidth,
    collapsed: preferences.sidebar.collapsed,
    headerHeight: preferences.header.height,
    tabbarHeight: preferences.tabbar.height,
  }),
)
```

**Step 4: Run test to verify it passes**

Run: `npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts`
Expected: PASS

**Step 5: Commit**

```bash
git add src/layouts/DefaultLayout.vue src/layouts/components/TagsView.vue src/layouts/composables/menu-layout.ts src/layouts/composables/__tests__/menu-layout.spec.ts
git commit -m "fix: normalize layout offsets and tabbar sizing"
```

### Task 4: 稳定双栏菜单根节点与子菜单计算

**Files:**
- Modify: `src/layouts/components/Sidebar.vue`
- Modify: `src/layouts/components/Navbar.vue`
- Create: `src/layouts/composables/use-split-menu.ts`
- Test: `src/layouts/composables/__tests__/menu-layout.spec.ts`

**Step 1: Write the failing test**

```ts
it('keeps active root menu when clicking a root item with children', () => {
  const tree = [
    { id: 10, path: '/warehouse', children: [{ id: 11, path: '/warehouse/area' }] },
  ]
  const state = buildSplitMenuState(tree, '/warehouse/area', 10)
  expect(state.activeRootMenu?.id).toBe(10)
  expect(state.sidebarMenus[0].id).toBe(11)
})
```

**Step 2: Run test to verify it fails**

Run: `npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts -t "keeps active root menu"`
Expected: FAIL because explicit active root state is not supported

**Step 3: Write minimal implementation**

```ts
const activeRootMenuId = ref<number | string | null>(null)
const splitMenuState = useSplitMenu({
  menuTree: computed(() => permissionStore.menuTree),
  routePath: computed(() => route.path),
  layoutState,
  activeRootMenuId,
})
```

**Step 4: Run test to verify it passes**

Run: `npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts`
Expected: PASS

**Step 5: Commit**

```bash
git add src/layouts/components/Sidebar.vue src/layouts/components/Navbar.vue src/layouts/composables/use-split-menu.ts src/layouts/composables/__tests__/menu-layout.spec.ts
git commit -m "fix: stabilize split menu root state"
```

### Task 5: 落地头部对齐、头部模式与侧栏悬停展开

**Files:**
- Modify: `src/layouts/DefaultLayout.vue`
- Modify: `src/layouts/components/Navbar.vue`
- Modify: `src/layouts/components/Sidebar.vue`
- Test: `src/layouts/composables/__tests__/menu-layout.spec.ts`

**Step 1: Write the failing test**

```ts
it('maps header menu alignment to flex positions', () => {
  expect(resolveHeaderMenuAlignment('start')).toBe('flex-start')
  expect(resolveHeaderMenuAlignment('center')).toBe('center')
  expect(resolveHeaderMenuAlignment('end')).toBe('flex-end')
})
```

**Step 2: Run test to verify it fails**

Run: `npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts -t "maps header menu alignment"`
Expected: FAIL because alignment mapping is not implemented

**Step 3: Write minimal implementation**

```ts
const headerMenuStyle = computed(() => ({
  justifyContent: resolveHeaderMenuAlignment(preferences.header.menuAlign),
}))
```

**Step 4: Run test to verify it passes**

Run: `npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts`
Expected: PASS

**Step 5: Commit**

```bash
git add src/layouts/DefaultLayout.vue src/layouts/components/Navbar.vue src/layouts/components/Sidebar.vue src/layouts/composables/menu-layout.ts src/layouts/composables/__tests__/menu-layout.spec.ts
git commit -m "feat: activate header alignment and hover expand behavior"
```

### Task 6: 校准偏好抽屉与布局预览

**Files:**
- Modify: `src/layouts/components/preferences/PreferencesDrawer.vue`
- Modify: `src/layouts/components/preferences/PreferenceLayout.vue`
- Modify: `src/layouts/components/preferences/PreferenceSidebar.vue`
- Modify: `src/layouts/components/preferences/PreferenceHeader.vue`

**Step 1: Write the failing test**

```ts
it('exposes mixed width control for split layouts only', () => {
  const config = buildSidebarPreferenceSchema('sidebar-mixed-nav')
  expect(config.showMixedWidth).toBe(true)
})
```

**Step 2: Run test to verify it fails**

Run: `npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts -t "exposes mixed width control"`
Expected: FAIL because preference schema helper is missing

**Step 3: Write minimal implementation**

```ts
const sidebarPreferenceState = computed(() => ({
  showMixedWidth: ['sidebar-mixed-nav', 'header-mixed-nav'].includes(preferences.app.layout),
  allowHoverExpand: preferences.sidebar.collapsed && preferences.sidebar.enable,
}))
```

**Step 4: Run test to verify it passes**

Run: `npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts`
Expected: PASS

**Step 5: Commit**

```bash
git add src/layouts/components/preferences/PreferencesDrawer.vue src/layouts/components/preferences/PreferenceLayout.vue src/layouts/components/preferences/PreferenceSidebar.vue src/layouts/components/preferences/PreferenceHeader.vue src/layouts/composables/menu-layout.ts src/layouts/composables/__tests__/menu-layout.spec.ts
git commit -m "fix: align preference drawer with real layout behavior"
```

### Task 7: 执行类型检查、构建与手动验收

**Files:**
- Verify: `src/layouts/DefaultLayout.vue`
- Verify: `src/layouts/components/Sidebar.vue`
- Verify: `src/layouts/components/Navbar.vue`
- Verify: `src/layouts/components/preferences/PreferencesDrawer.vue`

**Step 1: Run automated verification**

```bash
npx vitest run
npm run build
```

Expected: PASS with no type errors and no build failures

**Step 2: Run manual layout verification**

Run: `npm run dev`
Check:

```text
1. 逐个切换 7 种布局，确认头部、侧栏、标签栏、内容区不遮挡
2. 在 sidebar-mixed-nav / header-mixed-nav 下确认左根菜单与右子菜单完整显示
3. 在 mixed-nav 下确认顶部根菜单切换后左侧子菜单同步更新
4. 修改 header.menuAlign / header.mode / sidebar.mixedWidth / tabbar.height 并确认即时生效
5. 将窗口缩窄到移动端宽度，确认统一回退为 sidebar-nav
```

**Step 3: Run diagnostics**

Run: `vue-tsc --noEmit`
Expected: PASS

**Step 4: Commit**

```bash
git add src/layouts src/utils/preferences docs/plans
git commit -m "feat: complete layout preference adaptation"
```

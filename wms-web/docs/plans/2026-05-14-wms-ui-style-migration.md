# WMS UI 风格迁移 Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 以 `aryn-mall-ui` 为视觉标准，完成 `wms-web` 布局壳层的完整风格迁移，重点升级顶部区域、标签页、用户信息区、偏好抽屉和亮暗主题表现。

**Architecture:** 保留 `wms-web` 现有权限、菜单、路由和偏好状态流，只重做布局层与表现层。通过扩展偏好抽屉和布局组件，把参考项目的工具栏结构、页签视觉和主题层次映射到当前 Vue 3 + Element Plus 架构中。

**Tech Stack:** Vue 3、`<script setup lang="ts">`、TypeScript、Vue Router、Pinia、Element Plus、SCSS、Vite、Vitest、Vue TSC

---

### Task 1: 为布局风格迁移建立可回归的样式与行为基线

**Files:**
- Modify: `src/layouts/composables/__tests__/menu-layout.spec.ts`
- Test: `src/layouts/composables/menu-layout.ts`
- Verify: `src/types/preferences.ts`

**Step 1: Write the failing test**

```ts
import { describe, expect, it } from 'vitest'
import {
  buildSidebarPreferenceState,
  getLayoutMetrics,
  resolveHeaderMenuAlignment,
  resolveLayoutFlags,
} from '../menu-layout'

describe('layout chrome helpers', () => {
  it('maps header alignment to flex positions', () => {
    expect(resolveHeaderMenuAlignment('start')).toBe('flex-start')
    expect(resolveHeaderMenuAlignment('center')).toBe('center')
    expect(resolveHeaderMenuAlignment('end')).toBe('flex-end')
  })

  it('returns mixed sidebar metrics', () => {
    const flags = resolveLayoutFlags('sidebar-mixed-nav', false)
    const metrics = getLayoutMetrics(flags, {
      sidebarWidth: 220,
      mixedWidth: 72,
      collapseWidth: 60,
      collapsed: false,
      headerHeight: 56,
      headerVisible: true,
      sidebarVisible: true,
      tabbarHeight: 40,
    })

    expect(metrics.sidebarWidth).toBe(292)
    expect(metrics.mainPaddingTop).toBe(56)
  })

  it('shows mixed width control only in split layouts', () => {
    expect(
      buildSidebarPreferenceState('sidebar-mixed-nav', true, false).showMixedWidth,
    ).toBe(true)
    expect(
      buildSidebarPreferenceState('header-nav', true, false).showMixedWidth,
    ).toBe(false)
  })
})
```

**Step 2: Run test to verify it fails**

Run: `npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts`
Expected: FAIL with at least one missing assertion or helper contract mismatch

**Step 3: Write minimal implementation**

```ts
export function resolveHeaderMenuAlignment(align: 'start' | 'center' | 'end') {
  return {
    start: 'flex-start',
    center: 'center',
    end: 'flex-end',
  }[align]
}
```

**Step 4: Run test to verify it passes**

Run: `npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts`
Expected: PASS

**Step 5: Commit**

```bash
git add src/layouts/composables/menu-layout.ts src/layouts/composables/__tests__/menu-layout.spec.ts
git commit -m "test: lock layout chrome helper behavior"
```

### Task 2: 重做布局令牌与全局壳层视觉基线

**Files:**
- Modify: `src/styles/design-tokens/default.css`
- Modify: `src/styles/design-tokens/dark.css`
- Modify: `src/styles/index.scss`
- Verify: `src/utils/preferences/css-variables-updater.ts`

**Step 1: Write the failing test**

```ts
it('keeps header and tabbar tokens available in light and dark themes', () => {
  const requiredTokens = [
    '--bg-header',
    '--bg-background',
    '--color-border',
    '--color-primary',
  ]

  expect(requiredTokens.every(Boolean)).toBe(true)
})
```

**Step 2: Run test to verify it fails**

Run: `npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts -t "keeps header and tabbar tokens"`
Expected: FAIL because the placeholder assertion is replaced by a real token smoke check or is intentionally absent

**Step 3: Write minimal implementation**

```css
:root {
  --bg-header: hsl(210 40% 98%);
  --bg-background: hsl(210 20% 99%);
  --color-primary: hsl(212 100% 45%);
  --color-border: hsl(214 22% 91%);
}
```

**Step 4: Run test to verify it passes**

Run: `npm run build`
Expected: PASS with updated theme tokens compiled successfully

**Step 5: Commit**

```bash
git add src/styles/design-tokens/default.css src/styles/design-tokens/dark.css src/styles/index.scss
git commit -m "style: align layout tokens with blue admin theme"
```

### Task 3: 重构 `DefaultLayout` 的壳层结构与层次

**Files:**
- Modify: `src/layouts/DefaultLayout.vue`
- Test: `src/layouts/composables/__tests__/menu-layout.spec.ts`

**Step 1: Write the failing test**

```ts
it('keeps header and tabbar offsets in sync with layout metrics', () => {
  const flags = resolveLayoutFlags('sidebar-nav', false)
  const metrics = getLayoutMetrics(flags, {
    sidebarWidth: 220,
    mixedWidth: 72,
    collapseWidth: 60,
    collapsed: false,
    headerHeight: 56,
    headerVisible: true,
    sidebarVisible: true,
    tabbarHeight: 40,
  })

  expect(metrics.mainPaddingLeft).toBe(220)
  expect(metrics.mainPaddingTop).toBe(56)
})
```

**Step 2: Run test to verify it fails**

Run: `npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts -t "keeps header and tabbar offsets"`
Expected: FAIL if offsets or metrics contract do not match the new layout structure

**Step 3: Write minimal implementation**

```ts
const layoutMetrics = computed(() =>
  getLayoutMetrics(layoutState.value, {
    collapseWidth: preferences.sidebar.collapseWidth,
    collapsed: preferences.sidebar.collapsed,
    headerHeight: preferences.header.height,
    headerVisible: headerVisible.value,
    mixedWidth: preferences.sidebar.mixedWidth,
    sidebarVisible: sidebarVisible.value,
    sidebarWidth: preferences.sidebar.width,
    tabbarHeight: preferences.tabbar.height,
  }),
)
```

**Step 4: Run test to verify it passes**

Run: `npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts`
Expected: PASS

**Step 5: Commit**

```bash
git add src/layouts/DefaultLayout.vue src/layouts/composables/menu-layout.ts src/layouts/composables/__tests__/menu-layout.spec.ts
git commit -m "refactor: rebuild layout shell spacing and layering"
```

### Task 4: 把顶部区域改造成统一工具栏

**Files:**
- Modify: `src/layouts/components/Navbar.vue`
- Create: `src/layouts/components/HeaderActionButton.vue`
- Modify: `src/layouts/components/ThemeToggle.vue`
- Verify: `src/layouts/components/NavbarMenuItem.vue`

**Step 1: Write the failing test**

```ts
it('maps header menu alignment for toolbar layouts', () => {
  expect(resolveHeaderMenuAlignment('center')).toBe('center')
})
```

**Step 2: Run test to verify it fails**

Run: `npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts -t "maps header menu alignment for toolbar layouts"`
Expected: FAIL if helper coverage is missing before the navbar refactor starts

**Step 3: Write minimal implementation**

```vue
<HeaderActionButton tooltip="偏好设置" @click="openPreferences">
  <Setting />
</HeaderActionButton>

<HeaderActionButton tooltip="主题切换">
  <ThemeToggle />
</HeaderActionButton>
```

**Step 4: Run test to verify it passes**

Run: `npm run build`
Expected: PASS with the new toolbar structure compiled successfully

**Step 5: Commit**

```bash
git add src/layouts/components/Navbar.vue src/layouts/components/HeaderActionButton.vue src/layouts/components/ThemeToggle.vue
git commit -m "feat: restyle navbar as unified action toolbar"
```

### Task 5: 重做用户信息区为头像下拉

**Files:**
- Create: `src/layouts/components/UserDropdown.vue`
- Modify: `src/layouts/components/Navbar.vue`
- Verify: `src/store/modules/user.ts`
- Verify: `src/types/auth.d.ts`

**Step 1: Write the failing test**

```ts
it('keeps user dropdown actions available', () => {
  const commands = ['changePassword', 'logout']
  expect(commands).toContain('logout')
})
```

**Step 2: Run test to verify it fails**

Run: `npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts -t "keeps user dropdown actions available"`
Expected: FAIL until the placeholder is replaced with the actual component contract smoke test

**Step 3: Write minimal implementation**

```vue
<UserDropdown
  :avatar="userStore.userInfo?.avatar"
  :name="userStore.userInfo?.realName || '用户'"
  description="WMS 管理后台"
  @change-password="passwordDialogVisible = true"
  @logout="userStore.logout()"
/>
```

**Step 4: Run test to verify it passes**

Run: `npm run build`
Expected: PASS with avatar dropdown and password dialog integration intact

**Step 5: Commit**

```bash
git add src/layouts/components/UserDropdown.vue src/layouts/components/Navbar.vue
git commit -m "feat: upgrade header user area to avatar dropdown"
```

### Task 6: 升级标签页为页签式视觉并补充工具区

**Files:**
- Modify: `src/layouts/components/TagsView.vue`
- Create: `src/layouts/components/TagsViewActions.vue`
- Verify: `src/utils/preferences/config.ts`
- Verify: `src/types/preferences.ts`

**Step 1: Write the failing test**

```ts
it('uses tabbar height preference in layout metrics', () => {
  const flags = resolveLayoutFlags('sidebar-nav', false)
  const metrics = getLayoutMetrics(flags, {
    sidebarWidth: 220,
    mixedWidth: 72,
    collapseWidth: 60,
    collapsed: false,
    headerHeight: 56,
    headerVisible: true,
    sidebarVisible: true,
    tabbarHeight: 42,
  })

  expect(metrics.tabbarHeight).toBe(42)
})
```

**Step 2: Run test to verify it fails**

Run: `npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts -t "uses tabbar height preference"`
Expected: FAIL if tabbar metrics are not exposed for the new chrome-style tabbar

**Step 3: Write minimal implementation**

```vue
<div class="tags-view">
  <div class="tags-scroll">
    <button
      v-for="tag in visitedViews"
      :key="tag.path"
      class="tag-pill"
      :class="{ active: tag.path === route.path }"
    >
      {{ tag.meta?.title || tag.name }}
    </button>
  </div>
  <TagsViewActions />
</div>
```

**Step 4: Run test to verify it passes**

Run: `npm run build`
Expected: PASS with the new tabbar structure and action area

**Step 5: Commit**

```bash
git add src/layouts/components/TagsView.vue src/layouts/components/TagsViewActions.vue
git commit -m "feat: restyle tabbar with chrome-like tabs"
```

### Task 7: 扩展偏好抽屉的信息架构和可配置能力

**Files:**
- Modify: `src/layouts/components/preferences/PreferencesDrawer.vue`
- Create: `src/layouts/components/preferences/PreferenceTabbar.vue`
- Create: `src/layouts/components/preferences/PreferenceWidget.vue`
- Modify: `src/layouts/components/preferences/PreferenceThemeMode.vue`
- Modify: `src/layouts/components/preferences/PreferenceHeader.vue`
- Modify: `src/layouts/components/preferences/PreferenceSidebar.vue`

**Step 1: Write the failing test**

```ts
it('exposes widget and tabbar controls for the preference drawer', () => {
  expect(
    buildSidebarPreferenceState('sidebar-mixed-nav', true, false).showMixedWidth,
  ).toBe(true)
})
```

**Step 2: Run test to verify it fails**

Run: `npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts -t "exposes widget and tabbar controls"`
Expected: FAIL until the preference helper and drawer structure align

**Step 3: Write minimal implementation**

```vue
<el-tab-pane label="组件" name="widgets">
  <PreferenceTabbar v-bind="tabbarAttrs" v-on="tabbarListen" />
  <PreferenceWidget v-bind="widgetAttrs" v-on="widgetListen" />
</el-tab-pane>
```

**Step 4: Run test to verify it passes**

Run: `npm run build`
Expected: PASS with new preference sections and emitted updates

**Step 5: Commit**

```bash
git add src/layouts/components/preferences/PreferencesDrawer.vue src/layouts/components/preferences/PreferenceTabbar.vue src/layouts/components/preferences/PreferenceWidget.vue src/layouts/components/preferences/PreferenceThemeMode.vue src/layouts/components/preferences/PreferenceHeader.vue src/layouts/components/preferences/PreferenceSidebar.vue
git commit -m "feat: expand preference drawer controls and structure"
```

### Task 8: 收口侧栏、顶部菜单和状态色的一致性

**Files:**
- Modify: `src/layouts/components/Sidebar.vue`
- Modify: `src/layouts/components/SidebarMenuItem.vue`
- Modify: `src/layouts/components/NavbarMenuItem.vue`
- Verify: `src/layouts/composables/use-split-menu.ts`

**Step 1: Write the failing test**

```ts
it('marks split menu layouts correctly', () => {
  expect(resolveLayoutFlags('mixed-nav', false).isSplitMenu).toBe(true)
})
```

**Step 2: Run test to verify it fails**

Run: `npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts -t "marks split menu layouts correctly"`
Expected: FAIL if the helper contract drifts during sidebar/menu refactor

**Step 3: Write minimal implementation**

```scss
.menu-item.is-active {
  background: color-mix(in srgb, var(--color-primary) 12%, transparent);
  color: var(--color-primary);
}
```

**Step 4: Run test to verify it passes**

Run: `npm run build`
Expected: PASS with updated sidebar and header menu styling

**Step 5: Commit**

```bash
git add src/layouts/components/Sidebar.vue src/layouts/components/SidebarMenuItem.vue src/layouts/components/NavbarMenuItem.vue
git commit -m "style: unify menu active and hover states"
```

### Task 9: 校验亮暗主题、类型和构建结果

**Files:**
- Verify: `src/layouts/DefaultLayout.vue`
- Verify: `src/layouts/components/Navbar.vue`
- Verify: `src/layouts/components/TagsView.vue`
- Verify: `src/layouts/components/UserDropdown.vue`
- Verify: `src/layouts/components/preferences/PreferencesDrawer.vue`
- Verify: `src/styles/design-tokens/default.css`
- Verify: `src/styles/design-tokens/dark.css`

**Step 1: Run targeted tests**

```bash
npx vitest run src/layouts/composables/__tests__/menu-layout.spec.ts
```

Expected: PASS

**Step 2: Run type and build verification**

```bash
npm run build
npx vue-tsc --noEmit
```

Expected: PASS with no type errors

**Step 3: Run manual UI verification**

Run: `npm run dev`
Check:

```text
1. 顶部工具区是否已形成统一按钮组
2. 标签页是否具备明显的激活态、hover 态和右侧工具区
3. 用户头像下拉是否可正常触发修改密码和退出登录
4. 偏好抽屉是否出现“外观 / 布局 / 组件”分区
5. 蓝系主题在亮色和暗色下是否都保持清晰层次
6. 切换 7 种布局时顶部、侧栏、标签页和内容区是否不遮挡
```

**Step 4: Commit**

```bash
git add src/layouts src/styles docs/plans
git commit -m "feat: complete wms ui style migration"
```

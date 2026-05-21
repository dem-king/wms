# Warehouse Visual 2D Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 为 `wms-web` 增加基于 Konva 的独立库房 2D 可视化页面，并通过最小 API 修正打通区域、存放柜、库位数据。

**Architecture:** 先把接口兼容、布局推导和点击联动都拆到可测试的纯 TypeScript 模块中，使用 Vitest 按 TDD 逐步锁定行为；再由 `index.vue` 作为薄页面壳层接入 Konva 画布、空态、错误态和摘要区。页面层只负责加载与事件绑定，不承载复杂布局推导。

**Tech Stack:** Vue 3 + `<script setup lang="ts">` + TypeScript + Vitest + Vite + Element Plus + Konva + vue-konva

---

### Task 1: 修正仓库层 API 路径兼容

**Files:**
- Modify: `src/api/warehouse/area.ts`
- Modify: `src/api/warehouse/cabinet.ts`
- Modify: `src/api/warehouse/bin.ts`
- Create: `src/api/warehouse/visual-api.spec.ts`

**Step 1: Write the failing test**

```ts
it('requests backend path-variable endpoints for area, cabinet and bin queries', () => {
  getAreaList(10)
  getCabinetList(20)
  getBinList(30)
  expect(get).toHaveBeenCalledWith('/warehouse/areas/warehouse/10')
})
```

**Step 2: Run test to verify it fails**

Run: `npm test -- src/api/warehouse/visual-api.spec.ts`
Expected: FAIL because current code still uses query-string endpoints

**Step 3: Write minimal implementation**

```ts
export function getAreaList(warehouseId: number) {
  return get(`/warehouse/areas/warehouse/${warehouseId}`)
}
```

**Step 4: Run test to verify it passes**

Run: `npm test -- src/api/warehouse/visual-api.spec.ts`
Expected: PASS

### Task 2: 先写布局推导失败测试

**Files:**
- Create: `src/views/warehouse/visual/visual-layout.ts`
- Create: `src/views/warehouse/visual/visual-layout.spec.ts`

**Step 1: Write the failing test**

```ts
it('groups cabinets by area and derives stable bin grids', () => {
  expect(buildWarehouseVisualModel(...)).toEqual(...)
})
```

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/warehouse/visual/visual-layout.spec.ts`
Expected: FAIL because helper does not exist yet

**Step 3: Write minimal implementation**

```ts
export function buildWarehouseVisualModel(input) {
  return { areas: [], viewport: ..., summary: ... }
}
```

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/warehouse/visual/visual-layout.spec.ts`
Expected: PASS

### Task 3: 先写联动状态失败测试

**Files:**
- Create: `src/views/warehouse/visual/visual-state.ts`
- Create: `src/views/warehouse/visual/visual-state.spec.ts`

**Step 1: Write the failing test**

```ts
it('keeps selected area in sync when cabinet and bin are clicked', () => {
  expect(reduceVisualSelection(...)).toEqual(...)
})
```

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/warehouse/visual/visual-state.spec.ts`
Expected: FAIL because reducer does not exist yet

**Step 3: Write minimal implementation**

```ts
export function reduceVisualSelection(state, action) {
  return nextState
}
```

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/warehouse/visual/visual-state.spec.ts`
Expected: PASS

### Task 4: 先写页面壳层失败测试

**Files:**
- Create: `src/views/warehouse/visual/index.vue`
- Create: `src/views/warehouse/visual/index.spec.ts`
- Modify: `src/router/index.ts`

**Step 1: Write the failing test**

```ts
it('renders the visual page with stage, empty state and summary panel markers', () => {
  expect(source).toContain('v-stage')
})
```

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/warehouse/visual/index.spec.ts`
Expected: FAIL because the page does not exist yet

**Step 3: Write minimal implementation**

```vue
<script setup lang="ts">
// load warehouses, build visual model, bind selection
</script>
```

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/warehouse/visual/index.spec.ts`
Expected: PASS

### Task 5: 回归验证与构建

**Files:**
- Verify: `src/api/warehouse/visual-api.spec.ts`
- Verify: `src/views/warehouse/visual/visual-layout.spec.ts`
- Verify: `src/views/warehouse/visual/visual-state.spec.ts`
- Verify: `src/views/warehouse/visual/index.spec.ts`

**Step 1: Run focused tests**

Run: `npm test -- src/api/warehouse/visual-api.spec.ts src/views/warehouse/visual/visual-layout.spec.ts src/views/warehouse/visual/visual-state.spec.ts src/views/warehouse/visual/index.spec.ts`
Expected: PASS

**Step 2: Run full tests**

Run: `npm test`
Expected: PASS

**Step 3: Run build**

Run: `npm run build`
Expected: PASS

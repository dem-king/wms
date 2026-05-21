# Warehouse Code Auto Generate Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 新增库房时不再填写库房编码，并由后端自动生成；编辑库房时继续显示已有编码且保持只读。

**Architecture:** 通过在库房页面按新增/编辑场景拆分表单展示和提交流程实现。类型层将 `warehouseCode` 调整为可选，提交时新增与编辑分别组装最小请求体，并用测试约束这两个场景的行为。

**Tech Stack:** Vue 3、`<script setup lang="ts">`、Element Plus、Vitest、TypeScript

---

### Task 1: 为新增/编辑提交流程写失败测试

**Files:**
- Create: `src/views/warehouse/__tests__/warehouse-form-submit.spec.ts`
- Modify: `src/views/warehouse/warehouse/index.vue`

**Step 1: Write the failing test**

```ts
it('新增库房时不传 warehouseCode', async () => {
  // 挂载页面，填写新增必填项，提交
  // 断言 addWarehouse 收到的参数不包含 warehouseCode
})

it('编辑库房时保留 warehouseCode', async () => {
  // 准备编辑态数据，提交
  // 断言 updateWarehouse 收到的参数包含 warehouseCode
})
```

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/warehouse/__tests__/warehouse-form-submit.spec.ts`
Expected: FAIL，因为当前实现新增时仍要求并提交 `warehouseCode`

### Task 2: 用最小改动通过测试

**Files:**
- Modify: `src/views/warehouse/warehouse/index.vue`
- Modify: `src/types/warehouse.d.ts`

**Step 1: Write minimal implementation**

```ts
// 类型层：warehouseCode?: string
// 页面层：仅编辑态显示编码项；新增提交参数不带 warehouseCode；编辑提交继续带 warehouseCode
```

**Step 2: Run test to verify it passes**

Run: `npm test -- src/views/warehouse/__tests__/warehouse-form-submit.spec.ts`
Expected: PASS

### Task 3: 运行回归检查

**Files:**
- Modify: `src/views/warehouse/warehouse/index.vue`
- Modify: `src/types/warehouse.d.ts`
- Test: `src/api/warehouse/warehouse.spec.ts`

**Step 1: Run related tests**

Run: `npm test -- src/views/warehouse/__tests__/warehouse-form-submit.spec.ts src/api/warehouse/warehouse.spec.ts`
Expected: PASS

**Step 2: Run type/build verification**

Run: `npm run build`
Expected: PASS

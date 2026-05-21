# Label Code Display Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 将标签详情与打印预览中的占位码图替换为真实二维码/条形码渲染，并补齐类型与异常处理。

**Architecture:** 将编码展示的规则拆到可测试的纯 TypeScript 模块中，先用单测约束标签类型与内容解析，再让 `QrBarCode` 组件只负责真实渲染与错误回退。标签详情页和打印预览统一复用相同的解析结果，RFID 场景只展示文本信息，不再尝试图形化渲染。

**Tech Stack:** Vue 3 + `<script setup lang="ts">` + Vitest + Vite + Element Plus + 第三方二维码/条形码渲染库

---

### Task 1: 补充编码展示规则测试

**Files:**
- Create: `src/components/QrBarCode/rendering.spec.ts`
- Create: `src/views/label/label-code-display.spec.ts`
- Test: `src/components/QrBarCode/rendering.spec.ts`
- Test: `src/views/label/label-code-display.spec.ts`

**Step 1: Write the failing test**

```ts
it('uses qrContent for qr labels and refuses empty payloads', () => {
  expect(resolveCodeDisplay(...)).toEqual(...)
})
```

**Step 2: Run test to verify it fails**

Run: `npm test -- src/components/QrBarCode/rendering.spec.ts src/views/label/label-code-display.spec.ts`
Expected: FAIL with missing helper/module or missing expected source usage

**Step 3: Write minimal implementation**

```ts
export function resolveCodeDisplay(label) {
  // 根据类型返回 qr / barcode / rfid / unsupported
}
```

**Step 4: Run test to verify it passes**

Run: `npm test -- src/components/QrBarCode/rendering.spec.ts src/views/label/label-code-display.spec.ts`
Expected: PASS

**Step 5: Commit**

```bash
git add src/components/QrBarCode/rendering.spec.ts src/views/label/label-code-display.spec.ts
git commit -m "test: cover label code display rules"
```

### Task 2: 重构通用编码展示组件

**Files:**
- Modify: `src/components/QrBarCode/index.vue`
- Create: `src/components/QrBarCode/rendering.ts`
- Test: `src/components/QrBarCode/rendering.spec.ts`

**Step 1: Write the failing test**

```ts
it('maps unsupported type and missing content to explicit error state', () => {
  expect(buildRenderableCode(...)).toEqual(...)
})
```

**Step 2: Run test to verify it fails**

Run: `npm test -- src/components/QrBarCode/rendering.spec.ts`
Expected: FAIL because helper/state does not exist yet

**Step 3: Write minimal implementation**

```ts
export function buildRenderableCode(...) {
  return { kind: 'qr', value, errorMessage: '' }
}
```

**Step 4: Run test to verify it passes**

Run: `npm test -- src/components/QrBarCode/rendering.spec.ts`
Expected: PASS

**Step 5: Commit**

```bash
git add src/components/QrBarCode/index.vue src/components/QrBarCode/rendering.ts
git commit -m "feat: render real qr and barcode images"
```

### Task 3: 接入标签详情与打印预览

**Files:**
- Modify: `src/views/label/index.vue`
- Modify: `src/views/label/components/LabelPrint.vue`
- Create: `src/views/label/code-display.ts`
- Test: `src/views/label/label-code-display.spec.ts`

**Step 1: Write the failing test**

```ts
it('returns text-only output for rfid labels', () => {
  expect(resolveLabelCodeDisplay(label)).toEqual(...)
})
```

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/label/label-code-display.spec.ts`
Expected: FAIL because resolver does not exist yet

**Step 3: Write minimal implementation**

```ts
export function resolveLabelCodeDisplay(label) {
  // 统一详情与打印场景
}
```

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/label/label-code-display.spec.ts`
Expected: PASS

**Step 5: Commit**

```bash
git add src/views/label/index.vue src/views/label/components/LabelPrint.vue src/views/label/code-display.ts
git commit -m "feat: reuse label code display in detail and print preview"
```

### Task 4: 类型、异常与验证

**Files:**
- Modify: `src/types/label.d.ts`
- Modify: `src/api/item/label.ts`
- Test: `src/components/QrBarCode/rendering.spec.ts`
- Test: `src/views/label/label-code-display.spec.ts`

**Step 1: Write the failing test**

```ts
it('keeps batch print request shape stable', () => {
  batchPrintLabels({ labelIds: [1] })
  expect(post).toHaveBeenCalledWith('/labels/print', { labelIds: [1] })
})
```

**Step 2: Run test to verify it fails**

Run: `npm test -- src/api/item/label.spec.ts`
Expected: FAIL if request shape or types drift

**Step 3: Write minimal implementation**

```ts
export interface ElectronicLabelVo {
  qrContent?: string
  barcodeContent?: string
}
```

**Step 4: Run test to verify it passes**

Run: `npm test -- src/api/item/label.spec.ts && npm run build`
Expected: PASS

**Step 5: Commit**

```bash
git add src/types/label.d.ts src/api/item/label.ts
git commit -m "chore: tighten label code typing and validation"
```

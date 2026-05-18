# Action Column Width Tiers Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 将所有列表的操作列宽度改成按按钮数量分档的统一规范，去掉页面里分散的 `min-width` 数值。

**Architecture:** 保留现有 `table-action-column` 作为共享基础类，在页面层为每个操作列补充 `table-action-column--1` 到 `table-action-column--4` 的档位类。全局样式统一声明四档宽度，测试负责约束“必须有共享类、必须有档位类、不能继续写 `min-width`”。

**Tech Stack:** Vue 3、`<script setup lang="ts">`、SCSS、Vitest

---

### Task 1: 更新回归测试约束档位类

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\views\__tests__\table-action-column.spec.ts`

**Step 1: Write the failing test**

为现有测试补充一条断言：
- 每个 `label="操作"` 的 `el-table-column` 都必须带有 `table-action-column--1|2|3|4` 之一。
- 没有档位类的文件应被收集并使测试失败。

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/__tests__/table-action-column.spec.ts`
Expected: FAIL，提示现有操作列缺少合法档位类。

**Step 3: Write minimal implementation**

只修改测试文件，增加对合法档位类的校验，不提前改业务页面。

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/__tests__/table-action-column.spec.ts`
Expected: 仍然 FAIL，因为业务页面还未补档位类，这是预期的红灯阶段。

**Step 5: Commit**

```bash
git add src/views/__tests__/table-action-column.spec.ts
git commit -m "test: require action column width tiers"
```

### Task 2: 将页面操作列改成档位类

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\views\business\inbound\components\InboundForm.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\business\inbound\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\business\outbound\components\OutboundForm.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\business\outbound\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\business\return\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\business\scrap\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\business\transfer\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\item\category\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\item\list\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\item\tag\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\system\config\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\system\dept\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\system\menu\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\system\permission\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\system\role\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\system\supplier\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\system\user\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\warehouse\area\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\warehouse\bin\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\warehouse\cabinet\index.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\views\warehouse\warehouse\index.vue`

**Step 1: Write the failing test**

使用 Task 1 的失败测试作为回归基线，不新增额外测试。

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/__tests__/table-action-column.spec.ts`
Expected: FAIL，提示具体哪些页面缺少 `table-action-column--N`。

**Step 3: Write minimal implementation**

按按钮数量将每个操作列改成以下格式之一：
- `class-name="table-action-column table-action-column--1"`
- `class-name="table-action-column table-action-column--2"`
- `class-name="table-action-column table-action-column--3"`
- `class-name="table-action-column table-action-column--4"`

同时移除这些列上的 `min-width` 属性。

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/__tests__/table-action-column.spec.ts`
Expected: PASS，说明页面层声明已统一。

**Step 5: Commit**

```bash
git add src/views
git commit -m "refactor: apply action column width tiers"
```

### Task 3: 在全局样式中声明四档宽度

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\styles\index.scss`
- Test: `d:\Codes\WMS_code\wms-web\src\views\__tests__\table-action-column.spec.ts`

**Step 1: Write the failing test**

在测试中补充断言，要求全局样式包含：
- `.table-action-column--1`
- `.table-action-column--2`
- `.table-action-column--3`
- `.table-action-column--4`

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/__tests__/table-action-column.spec.ts`
Expected: FAIL，提示缺少档位样式定义。

**Step 3: Write minimal implementation**

在 `src/styles/index.scss` 中为四档类补充统一 `min-width`：
- `--1: 80px`
- `--2: 200px`
- `--3: 260px`
- `--4: 320px`

保留现有 `nowrap`、`gap`、按钮间距处理。

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/__tests__/table-action-column.spec.ts`
Expected: PASS

Run: `npm test -- src/views/__tests__/table-action-column.spec.ts`
Expected: PASS 且输出干净。

**Step 5: Commit**

```bash
git add src/styles/index.scss src/views/__tests__/table-action-column.spec.ts
git commit -m "style: define action column width tiers"
```

### Task 4: 做诊断与变更确认

**Files:**
- Verify: `d:\Codes\WMS_code\wms-web\src\views\system\user\index.vue`
- Verify: `d:\Codes\WMS_code\wms-web\src\views\business\outbound\index.vue`
- Verify: `d:\Codes\WMS_code\wms-web\src\views\__tests__\table-action-column.spec.ts`
- Verify: `d:\Codes\WMS_code\wms-web\src\styles\index.scss`

**Step 1: Write the failing test**

不新增测试，使用已有 Vitest 测试和 VS Code diagnostics 作为验证。

**Step 2: Run test to verify it fails**

此任务无红灯步骤，直接进入验证。

**Step 3: Write minimal implementation**

不改代码，只执行检查：
- 获取最近改动文件的 diagnostics
- 查看 `git diff --stat -- src/views src/styles/index.scss`

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/__tests__/table-action-column.spec.ts`
Expected: PASS

**Step 5: Commit**

```bash
git add docs/plans/2026-05-18-action-column-width-tiers-design.md docs/plans/2026-05-18-action-column-width-tiers.md
git commit -m "docs: add action column width tier plan"
```

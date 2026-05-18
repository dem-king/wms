# Action Column Overflow Adaptive Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 将所有列表页操作列改为“最多直出 3 个按钮，其余收进更多下拉”的统一自适应方案，避免多按钮时显示不全。

**Architecture:** 新增一个 `TableActionGroup` 通用组件，负责过滤可见动作、直出前 3 个动作并将剩余动作收进“更多”菜单，同时统一处理确认型动作。页面层改为声明动作配置数组并接入该组件，原有固定宽度分档测试同步迁移为组件接入约束。

**Tech Stack:** Vue 3、`<script setup lang="ts">`、TypeScript、Element Plus、SCSS、Vitest

---

### Task 1: 更新测试约束新的操作列模式

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\views\__tests__\table-action-column.spec.ts`

**Step 1: Write the failing test**

在现有测试中新增断言：

- 每个 `label="操作"` 的 `el-table-column` 都必须保留 `table-action-column`。
- 每个操作列都必须使用 `TableActionGroup` 组件。
- 不再要求 `table-action-column--1~4` 档位类。

```ts
const tableActionGroupPattern = /<TableActionGroup\b/

it('uses TableActionGroup in every action column', () => {
  const filesWithMissingGroup = collectVueFiles(viewsRoot)
    .map((filePath) => {
      const source = readFileSync(filePath, 'utf-8')
      const actionColumnTags = source.match(actionColumnTagPattern) ?? []

      if (actionColumnTags.length === 0) {
        return null
      }

      return tableActionGroupPattern.test(source) ? null : path.relative(viewsRoot, filePath)
    })
    .filter((filePath): filePath is string => Boolean(filePath))

  expect(filesWithMissingGroup).toEqual([])
})
```

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/__tests__/table-action-column.spec.ts`
Expected: FAIL，提示现有操作列页面还没有接入 `TableActionGroup`。

**Step 3: Write minimal implementation**

只修改测试文件，不提前修改生产代码。

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/__tests__/table-action-column.spec.ts`
Expected: 仍然 FAIL，因为生产代码尚未改造，这是预期红灯。

**Step 5: Commit**

```bash
git add src/views/__tests__/table-action-column.spec.ts
git commit -m "test: require shared table action group"
```

### Task 2: 新增共享操作列组件

**Files:**
- Create: `d:\Codes\WMS_code\wms-web\src\components\TableActionGroup\TableActionGroup.vue`
- Modify: `d:\Codes\WMS_code\wms-web\src\components.d.ts`

**Step 1: Write the failing test**

沿用 Task 1 的失败测试，不单独新建组件测试。

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/__tests__/table-action-column.spec.ts`
Expected: FAIL

**Step 3: Write minimal implementation**

实现 `TableActionGroup`：

- props：`actions`
- 计算 `visibleActions`
- 前 3 个渲染为文字按钮
- 剩余动作通过 `el-dropdown` + `el-dropdown-menu` 收纳
- 对 `confirmText` 动作统一包裹 `el-popconfirm`

```vue
<script setup lang="ts">
interface TableActionItem {
  label: string
  type?: 'primary' | 'success' | 'warning' | 'danger' | 'info'
  visible?: boolean
  disabled?: boolean
  confirmText?: string
  onClick: () => void | Promise<void>
}

const props = defineProps<{ actions: TableActionItem[] }>()

const visibleActions = computed(() => props.actions.filter((action) => action.visible !== false))
const inlineActions = computed(() => visibleActions.value.slice(0, 3))
const overflowActions = computed(() => visibleActions.value.slice(3))
</script>
```

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/__tests__/table-action-column.spec.ts`
Expected: 仍然 FAIL，因为页面还未接入。

**Step 5: Commit**

```bash
git add src/components/TableActionGroup/TableActionGroup.vue src/components.d.ts
git commit -m "feat: add shared table action group"
```

### Task 3: 迁移所有列表页操作列到共享组件

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

使用 Task 1 的测试作为统一回归基线。

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/__tests__/table-action-column.spec.ts`
Expected: FAIL，并列出仍在手写按钮模板的页面。

**Step 3: Write minimal implementation**

每个页面完成以下改造：

- 操作列保留 `class-name="table-action-column"`。
- 用 `<TableActionGroup :actions="buildXxxActions(row)" />` 替换现有多个按钮模板。
- 在 `<script setup lang="ts">` 中新增动作构造函数，保持原有顺序、权限和 `v-if` 逻辑。
- 对删除、提交等危险动作写入 `confirmText`。
- 移除 `table-action-column--1~4` 档位类。

示例：

```vue
<el-table-column label="操作" class-name="table-action-column" fixed="right">
  <template #default="{ row }">
    <TableActionGroup :actions="buildUserActions(row)" />
  </template>
</el-table-column>
```

```ts
function buildUserActions(row: SysUserVo) {
  return [
    { label: '编辑', type: 'primary', icon: Edit, onClick: () => handleEdit(row) },
    { label: '重置密码', type: 'warning', icon: Key, onClick: () => handleResetPwd(row) },
    { label: '删除', type: 'danger', icon: Delete, confirmText: '确定删除该用户吗？', onClick: () => handleDelete(row.id) },
  ]
}
```

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/__tests__/table-action-column.spec.ts`
Expected: PASS

**Step 5: Commit**

```bash
git add src/views
git commit -m "refactor: use shared action group in table columns"
```

### Task 4: 调整共享样式以适配新交互

**Files:**
- Modify: `d:\Codes\WMS_code\wms-web\src\styles\index.scss`
- Test: `d:\Codes\WMS_code\wms-web\src\views\__tests__\table-action-column.spec.ts`

**Step 1: Write the failing test**

在样式测试中调整断言：

- 继续要求 `.table-action-column`
- 移除对 `.table-action-column--1~4` 的要求
- 新增对共享布局类和“更多”触发器样式关键字的检查

```ts
expect(source).toContain('.table-action-column')
expect(source).toContain('.table-action-group')
expect(source).toContain('.table-action-group__more')
```

**Step 2: Run test to verify it fails**

Run: `npm test -- src/views/__tests__/table-action-column.spec.ts`
Expected: FAIL，提示共享样式尚未更新。

**Step 3: Write minimal implementation**

在 `src/styles/index.scss` 中：

- 移除或废弃 `table-action-column--1~4`
- 保留 `.cell` 的横向布局
- 增加 `.table-action-group`、`.table-action-group__more`、下拉项图标间距等样式
- 为操作列设置适合 3 个按钮加“更多”触发器的统一 `min-width`

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/__tests__/table-action-column.spec.ts`
Expected: PASS

**Step 5: Commit**

```bash
git add src/styles/index.scss src/views/__tests__/table-action-column.spec.ts
git commit -m "style: adapt table action column layout"
```

### Task 5: 完成验证与诊断检查

**Files:**
- Verify: `d:\Codes\WMS_code\wms-web\src\views\system\user\index.vue`
- Verify: `d:\Codes\WMS_code\wms-web\src\views\system\role\index.vue`
- Verify: `d:\Codes\WMS_code\wms-web\src\views\business\inbound\index.vue`
- Verify: `d:\Codes\WMS_code\wms-web\src\views\business\outbound\index.vue`
- Verify: `d:\Codes\WMS_code\wms-web\src\components\TableActionGroup\TableActionGroup.vue`
- Verify: `d:\Codes\WMS_code\wms-web\src\styles\index.scss`

**Step 1: Write the failing test**

不新增测试，使用已有 Vitest、构建和诊断检查。

**Step 2: Run test to verify it fails**

此任务无红灯步骤，直接进入验证。

**Step 3: Write minimal implementation**

不改代码，只执行检查：

- `npm test -- src/views/__tests__/table-action-column.spec.ts`
- `npm run build`
- 获取最近改动文件 diagnostics

**Step 4: Run test to verify it passes**

Run: `npm test -- src/views/__tests__/table-action-column.spec.ts`
Expected: PASS

Run: `npm run build`
Expected: PASS

**Step 5: Commit**

```bash
git add docs/plans/2026-05-18-action-column-overflow-adaptive-design.md docs/plans/2026-05-18-action-column-overflow-adaptive.md
git commit -m "docs: add adaptive action column plan"
```

# 操作列宽度分档设计

## 背景

当前列表页的操作列已经统一使用 `table-action-column` 控制横向排列，但列宽仍然分散写在各个页面的 `min-width` 属性中。这样虽然解决了按钮换行问题，但仍存在两个问题：

1. 宽度规则分散，后续新增列表页时容易继续写出新的“魔法数字”。
2. 页面无法表达“这是几个操作按钮的列”，只能直接写结果宽度，可维护性较差。

## 目标

将操作列宽度改成按按钮数量分档的统一规范：

- 1 个按钮：`table-action-column--1`
- 2 个按钮：`table-action-column--2`
- 3 个按钮：`table-action-column--3`
- 4 个按钮：`table-action-column--4`

页面层只声明档位类，不再手工写操作列的 `min-width` 数值。

## 方案

### 页面结构

保留现有 `class-name="table-action-column"`，并在其后追加档位类，例如：

- 单按钮明细表：`table-action-column table-action-column--1`
- 普通编辑/删除：`table-action-column table-action-column--2`
- 三按钮列表：`table-action-column table-action-column--3`
- 四按钮列表：`table-action-column table-action-column--4`

### 样式规则

在全局样式中统一定义四档最小宽度：

- `--1`: `80px`
- `--2`: `200px`
- `--3`: `260px`
- `--4`: `320px`

继续复用现有横向排列样式，确保按钮不换行。

### 分档规则

按模板中当前声明的操作按钮数量分档。

- `v-if` 场景按最大可能可见数量归档，优先保证不截断。
- 暂不引入运行时动态计算，避免增加组件复杂度和渲染逻辑。

## 验证

新增和更新回归测试，确保：

1. 所有操作列都带有 `table-action-column`。
2. 所有操作列都不再写 `min-width`。
3. 所有操作列都必须带一个合法的档位类。

## 预期收益

- 消除分散的宽度常量。
- 让操作列宽度规则更直观。
- 后续新增列表页时，只需根据按钮数量选择档位类。

# 标签页样式切换设计

## 1. 背景

当前偏好设置面板中的“标签页 -> 样式类型”已经提供了三种可选值：

- `chrome`
- `card`
- `plain`

但实际标签页组件 [TagsView.vue](file:///d:/Codes/WMS_code/wms-web/src/layouts/components/TagsView.vue) 只有一套固定样式，导致用户切换偏好后界面没有可见变化，配置项处于“可选不可用”状态。

## 2. 目标

- 接通偏好设置中的 `preferences.tabbar.styleType`
- 为标签页提供三种可见差异明确的视觉风格
- 保持现有标签页的行为逻辑不变，包括：
  - 路由切换
  - 激活态判断
  - 关闭标签
  - 关闭其他
  - 关闭全部

## 3. 本次不做

- 不重构标签页数据结构
- 不引入新的状态管理
- 不修改 `PreferencesDrawer.vue` 与 `PreferenceTabbar.vue` 的交互逻辑
- 不新增动画、拖拽或滚轮行为
- 不调整标签页与顶部操作区的布局结构

## 4. 方案比较

### 4.1 方案 A：在 `TagsView.vue` 中按样式类型挂载类名

做法：

- 在标签栏容器和标签项上根据 `preferences.tabbar.styleType` 生成样式类
- 在同一个 SFC 中维护三套 SCSS 分支

优点：

- 改动集中
- 不影响现有标签页逻辑
- 最适合本次“最小改动接通配置”的目标

缺点：

- 三套样式会共存在一个文件中，样式体积略增

### 4.2 方案 B：拆分子组件分别实现三种样式

做法：

- 提取 `TagItem` 子组件
- 根据偏好设置切换不同模板或不同子组件

优点：

- 组件职责更清晰
- 后续扩展更多样式更方便

缺点：

- 对当前问题属于过度设计
- 增加组件拆分和通信成本

### 4.3 方案 C：使用 CSS 变量抽象三套风格

做法：

- 通过 CSS 变量统一描述背景、边框、激活态和 hover
- 组件结构保持不变

优点：

- 样式切换机制统一
- 后续扩展成本低

缺点：

- 当前只有三种固定风格，抽象收益有限
- 首次接入成本高于直接 class 分支

## 5. 推荐方案

选择方案 A。

原因：

- 最符合“仅接通切换、最小改动”的用户诉求
- 可以直接复用当前标签页 DOM 结构
- 容易验证，风险最小

## 6. 设计细节

### 6.1 数据流

- 输入来源保持为 `preferences.tabbar.styleType`
- 组件内部新增一个计算属性或纯函数，将当前样式类型映射为类名
- 模板层只消费类名，不新增业务状态

### 6.2 组件职责

- [PreferenceTabbar.vue](file:///d:/Codes/WMS_code/wms-web/src/layouts/components/preferences/PreferenceTabbar.vue) 继续负责偏好设置输入
- [PreferencesDrawer.vue](file:///d:/Codes/WMS_code/wms-web/src/layouts/components/preferences/PreferencesDrawer.vue) 继续负责把偏好值写回全局设置
- [TagsView.vue](file:///d:/Codes/WMS_code/wms-web/src/layouts/components/TagsView.vue) 负责根据偏好设置切换视觉样式

### 6.3 视觉映射

- `chrome`
  - 保留当前“浏览器页签”风格
  - 激活态以主色强调，顶部/底部保留清晰选中标识
- `card`
  - 标签项使用更完整的卡片背景和边框
  - 激活态增加背景、边框和轻微阴影
- `plain`
  - 整体更轻量
  - 减弱边框和装饰，突出文字与主色状态

### 6.4 错误处理与兼容

- 类型已在 `TabbarPreferences.styleType` 中限制为 `chrome | card | plain`
- 默认配置已设置为 `chrome`
- 即使未来缓存中出现非法值，也可以回退到 `chrome` 样式类

## 7. 测试策略

- 新增一个纯函数测试，验证样式类型到类名的映射
- 手动检查三种切换下的页面视觉差异
- 执行前端类型构建，确保样式接入不引入 TS 或 SFC 诊断问题

## 8. 风险

- 三套样式共存于一个组件中，后续如果继续扩展样式类型，SCSS 复杂度会上升
- 当前测试覆盖的是样式类型映射，不直接校验浏览器渲染效果，因此仍需要手动检查视觉表现

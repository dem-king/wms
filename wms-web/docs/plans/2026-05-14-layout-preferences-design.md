# WMS 布局与主题偏好优化设计

## 背景

`wms-web` 已具备主题与布局偏好基础能力，但布局消费层未完全收敛，导致以下问题长期并存：

- 布局判断来源不统一，`DefaultLayout` 与 `Navbar` 对当前布局、移动端降级的处理不一致
- 双栏布局依赖当前路由反推根菜单，导致切换布局、点击根菜单、返回首页后的右侧子菜单状态不稳定
- 多个偏好项在设置面板中可配置，但布局组件未真正消费，例如 `header.menuAlign`、`header.mode`、`sidebar.expandOnHover`
- 布局预览文案与实际渲染存在偏差，尤其是 `mixed-nav`、`header-mixed-nav`、`header-sidebar-nav`
- 双栏和顶部布局切换后，菜单显示不全、宽度计算和内容偏移不稳定

## 目标

- 统一布局状态来源，确保桌面端与移动端在所有组件中使用同一套布局判定
- 修复双栏布局的主菜单、子菜单、头部操作区和内容区之间的宽高与偏移适配
- 补齐关键偏好项的真实生效，清理“面板能调但界面不变”的体验问题
- 让布局切换预览、命名和实际渲染语义一致
- 保持现有业务页面可用，不对所有业务页做一次性大规模样式重写

## 非目标

- 本轮不重写整个偏好管理系统
- 本轮不批量改造所有业务页面的 `.app-container` 内边距策略
- 本轮不新增全新布局模式，只整理并修正现有 7 种布局的实现

## 参考项目结论

`aryn-mall-ui` 中最值得复用的并不是具体布局组件，而是偏好实现的三层分离：

- 偏好管理器负责响应式状态、本地持久化与主题副作用
- `usePreferences()` 负责输出布局计算态和平台降级规则
- 设置抽屉桥接层负责把偏好状态转换为表单属性和 `update:*` 事件

`wms-web` 已经具备其中前两层的雏形，因此本次优化采用“完整收敛而非推倒重来”的策略。

## 方案选择

采用方案 B：完整收敛。

该方案在保留现有偏好模型和页面结构的前提下，统一布局状态、重构双栏菜单状态、补齐关键偏好项消费，并同步修正偏好面板展示。

## 设计概览

### 1. 布局状态统一

将当前布局的所有派生判定统一收口到 `usePreferences()`，对外提供统一布局计算结果：

- 当前生效布局
- 是否移动端
- 是否全内容布局
- 是否显示头部
- 是否显示侧栏
- 是否为顶部菜单模式
- 是否为双栏根菜单模式
- 是否为拆分菜单模式

这样 `DefaultLayout`、`Navbar`、`Sidebar` 不再分别维护一套 `computed` 逻辑，避免语义漂移。

### 2. 双栏菜单状态化

双栏布局不再只依赖当前路由反推根菜单，而是引入一个轻量的根菜单激活状态：

- 初始值根据当前路由自动推导
- 点击左侧根菜单或顶部根菜单时主动更新
- 切换布局时保留当前根菜单上下文
- 进入首页或没有子菜单时，右侧菜单区域安全降级

该状态仍围绕当前路由工作，但会比纯计算式反推更稳定，也更利于后续扩展。

### 3. 统一布局语义

对 7 种布局给出明确视觉定义：

- `sidebar-nav`: 经典左侧导航
- `sidebar-mixed-nav`: 左根菜单 + 右子菜单
- `header-nav`: 顶部水平菜单
- `header-sidebar-nav`: 顶部操作区 + 左完整菜单
- `mixed-nav`: 顶部根菜单 + 左子菜单
- `header-mixed-nav`: 顶部操作区 + 左根菜单 + 右子菜单
- `full-content`: 无头部、无侧栏，仅内容

偏好面板的预览图与文案同步按这套定义修正。

### 4. 关键偏好项落地

本轮保证以下偏好项真实影响界面：

- `header.menuAlign`: 控制顶部菜单左、中、右对齐
- `header.mode`: 统一固定、静态等头部定位策略
- `sidebar.expandOnHover`: 在折叠侧栏模式下悬停展开
- `sidebar.mixedWidth`: 双栏左根菜单宽度可配置
- `tabbar.height`: 标签栏高度跟随偏好设置

短期无法稳定落地的偏好项将隐藏或降级，避免伪配置项继续暴露给用户。

### 5. 宽度与偏移统一计算

`DefaultLayout` 负责单点计算：

- 头部高度与定位方式
- 侧栏宽度、双栏宽度、折叠宽度
- 主内容区 `padding-left`、`padding-top`
- Footer 与 TagsView 占位

布局组件仅消费结果，不再各自推导偏移，降低错位和遮挡风险。

## 文件影响范围

- `src/utils/preferences/use-preferences.ts`
- `src/layouts/DefaultLayout.vue`
- `src/layouts/components/Sidebar.vue`
- `src/layouts/components/Navbar.vue`
- `src/layouts/components/TagsView.vue`
- `src/layouts/components/preferences/PreferencesDrawer.vue`
- `src/layouts/components/preferences/PreferenceLayout.vue`
- `src/layouts/components/preferences/PreferenceSidebar.vue`
- `src/layouts/components/preferences/PreferenceHeader.vue`

如收敛逻辑过多，可新增一个轻量辅助模块，例如：

- `src/layouts/components/layout/use-layout-state.ts`
- `src/layouts/components/layout/menu-utils.ts`

## 风险与处理

### 业务页面内边距重复

多数业务页面保留了自身 `.app-container` 内边距，布局层再加强内容区 `padding` 时可能出现双重内边距。处理策略为：

- 本轮维持兼容
- 仅确保布局层不产生额外溢出和横向压缩
- 后续如要统一页面留白，再作为独立任务推进

### 路由与菜单树路径格式不一致

当前菜单项路径既有绝对路径也有相对路径，双栏菜单点击后若路径拼接不统一，可能继续造成激活异常。处理策略为：

- 抽取统一路径解析函数
- 根菜单与子菜单都走同一套路径归一化逻辑

### keep-alive 与标签栏逻辑耦合

当前 `keep-alive` 配置存在逻辑可疑点，本轮会以“避免功能错误”为主，优先修正缓存判定，不在布局重构中引入复杂缓存新策略。

## 验收标准

- 7 种布局切换时头部、侧栏、标签栏和内容区不互相遮挡
- 双栏布局下左根菜单和右子菜单始终可见，不出现截断、空白、错位
- 移动端始终回退为单侧栏，所有组件对当前布局认知一致
- 顶部菜单对齐与头部模式设置可真实生效
- 双栏宽度、标签栏高度等偏好变更后界面立即更新
- 偏好面板的布局预览、名称和实际渲染一致

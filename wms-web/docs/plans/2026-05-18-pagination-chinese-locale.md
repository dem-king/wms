# Pagination Chinese Locale Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** 为项目中的 `Element Plus` 分页组件接入全局中文文案，使所有 `el-pagination` 自动显示中文。

**Architecture:** 在应用入口统一注册 `Element Plus` 插件并传入 `zh-cn` 语言包，而不是逐页覆盖分页文案。这样只修改启动层，所有现有分页实例都自动继承中文配置，保持业务页面零侵入。

**Tech Stack:** Vue 3、TypeScript、Vite、Element Plus

---

### Task 1: 接入 Element Plus 中文语言包

**Files:**
- Modify: `src/main.ts`

**Step 1: 写一个最小化验证点**

确认 `src/main.ts` 当前只引入了 `element-plus/dist/index.css` 和图标，尚未通过 `app.use(ElementPlus, { locale })` 注册插件，因此分页文案仍可能使用默认语言。

**Step 2: 修改入口注册逻辑**

在 `src/main.ts` 中：
- 新增 `import ElementPlus from 'element-plus'`
- 新增 `import zhCn from 'element-plus/es/locale/lang/zh-cn'`
- 在 `app.mount('#app')` 前增加 `app.use(ElementPlus, { locale: zhCn })`

**Step 3: 自查影响范围**

确认不需要改动各业务页面中的 `el-pagination` 模板，包括：
- `src/views/system/user/index.vue`
- `src/views/system/role/index.vue`
- `src/views/system/config/index.vue`
- 以及其他已使用 `el-pagination` 的页面

**Step 4: 运行验证**

Run: `npm run build`
Expected: 构建通过，无新增类型错误或打包错误

**Step 5: 补充静态诊断**

使用编辑器诊断检查 `src/main.ts`，确认没有新增导入或类型问题。

**Step 6: Commit**

```bash
git add src/main.ts docs/plans/2026-05-18-pagination-chinese-locale.md
git commit -m "feat: localize pagination to Chinese"
```

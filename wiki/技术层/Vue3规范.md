# Vue 3 + Vite 前端规范

> **状态**: ⚠️ 占位（待补充）— 创建于 2026-06-02
> **负责人**: <TBD>
> **关联模块**: wms-web（含 wms-pda）
> **优先级**: 🟡 P2 — 辅助文档

## 文档目标

- Vue 3 + Composition API + TypeScript 编码约定
- 路由约定（vue-router）
- 状态管理（pinia）
- HTTP 客户端（axios 封装）
- UI 组件库
- 命名规范（与 [AGENTS.md §8.1](../AGENTS.md) 一致：小驼峰）

## 当前已知要点

- 前端路径: `wms-web/`
- 主项目: `wms-web/`（PC 端管理后台）
- 移动端: `wms-web/wms-pda/`（PDA 手持设备）
- 端口: 3000（Vite 开发服务器）
- API 代理: `/api` → `http://localhost:8080`

## 待补内容

- [ ] 组件命名规范（PascalCase）
- [ ] Props / Emits 类型定义（TypeScript interface）
- [ ] API 目录结构（api/system/user.ts 等）
- [ ] 路由 meta 配置（title / icon / permission）
- [ ] pinia store 组织
- [ ] axios 拦截器（统一错误处理、token 注入）
- [ ] 权限指令 `v-permission`
- [ ] 与后端命名一致性（[AGENTS.md §8.1](../AGENTS.md)）

## 关联规则引用

- [AGENTS.md §8.1](../AGENTS.md) — 前后端参数命名一致（小驼峰）
- [项目启动.md](../快速上手/项目启动.md) — 前端启动命令

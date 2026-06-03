# 备品备件库房管理平台 — Wiki 知识库

> **最近更新**: 2026-06-02 — 完成 5 个 P0 文档补全（出库 498/归还报废 695/PDA 497/认证鉴权 968/权限体系 488 行），Wiki 覆盖率 12 → 17/22（77%）。
> **待补文档**: [wiki/TODO.md](TODO.md)（剩余 5 个 P1 + 7 个 P2）。

本 Wiki 面向**开发人员、测试人员、AI 协作者**，按"上手 → 业务 → 治理 → 技术 → 资产"五层组织。

---

## 0. AI / 协作者必读

| 入口 | 说明 |
|------|------|
| [../AGENTS.md](../AGENTS.md) | **Workspace 强制规则**（红线，零容忍） |
| [../.harness/rules/编码约束.md](../.harness/rules/%E7%BC%96%E7%A0%81%E7%BA%A6%E6%9D%9F.md) | 可程序化验证的编码规则清单（DB-01~07、SEC-01~04、ARCH-01~04、NAME-01~05、JAVADOC-01~03、CONST-01~03、SEQ-01、PERF-01~02、INV-01~02、DDL-01~03） |
| [../.harness/rules/工程结构.md](../.harness/rules/%E5%B7%A5%E7%A8%8B%E7%BB%93%E6%9E%84.md) | 模块依赖层次、11 类包结构、9 模块特殊包说明 |
| [../.harness/rules/质量门禁.md](../.harness/rules/%E8%B4%A8%E9%87%8F%E9%97%A8%E7%A6%81.md) | 24 条 CI 阻断/警告门禁 |
| [../.harness/agents/wms-owner.md](../.harness/agents/wms-owner.md) | 9 阶段开发流程、模块职责索引、Wiki 推荐阅读路径 |

**AI / 协作者 5 分钟入门流程**：
1. Read [../AGENTS.md](../AGENTS.md) — 9 大红线
2. Read [../.harness/rules/工程结构.md](../.harness/rules/%E5%B7%A5%E7%A8%8B%E7%BB%93%E6%9E%84.md) — 模块与包结构
3. Read [../.harness/rules/编码约束.md](../.harness/rules/%E7%BC%96%E7%A0%81%E7%BA%A6%E6%9D%9F.md) — 编码红线 + 正确示例
4. 浏览本 Wiki 的 [业务开发](#2-业务开发) 与 [系统管理](#3-系统管理) — 了解业务
5. 编码前主动加载相关 Skill（详见 [wms-owner.md](../.harness/agents/wms-owner.md)）

---

## 1. 快速上手

| 文档 | 状态 | 用途 |
|------|------|------|
| [快速上手/环境搭建.md](快速上手/环境搭建.md) | ✅ 已更新（2026-06-02） | JDK/Maven/MySQL/Redis/MinIO 安装，docker-compose 启动中间件 |
| [快速上手/项目启动.md](快速上手/项目启动.md) | ✅ 已有 | 后端 + 前端启动命令，常见端口/服务对照 |

---

## 2. 业务开发

| 业务域 | 文档 | 状态 | 后端模块 |
|--------|------|------|----------|
| **库房** | [业务开发/库房管理.md](业务开发/库房管理.md) | ✅ 已有 | wms-warehouse |
| 库房布局可视化 | [业务开发/库房布局可视化.md](业务开发/库房布局可视化.md) | 🆕 占位 | wms-warehouse (LayoutElement) |
| **入库** | [业务开发/入库流程.md](业务开发/入库流程.md) | ✅ 已有 | wms-business (Inbound) |
| **出库** | [业务开发/出库流程.md](业务开发/出库流程.md) | ✅ 498 行 | wms-business (Outbound) |
| 归还 / 报废 / 调拨 | [业务开发/归还报废调拨.md](业务开发/归还报废调拨.md) | ✅ 695 行 | wms-business (Return/Scrap/Transfer) |
| 库存盘点（PDA 扫码） | [业务开发/库存盘点.md](业务开发/库存盘点.md) | 🆕 占位 | wms-business (StockCheck) |
| **物品 / 库存** | [业务开发/物品库存.md](业务开发/物品库存.md) | ✅ 已有 | wms-item |
| 电子标签 / RFID | [业务开发/电子标签.md](业务开发/电子标签.md) | 🆕 占位 | wms-item (ElectronicLabel) |
| 机器备品 | [业务开发/机器备品.md](业务开发/机器备品.md) | 🆕 占位 | wms-item (MachineSpare) |
| **PDA 移动端** | [业务开发/PDA移动端.md](业务开发/PDA移动端.md) | ✅ 497 行 | wms-pda + wms-business.controller.pda |
| 审批流程 | [业务开发/审批流程.md](业务开发/审批流程.md) | 🆕 占位 | wms-approval |
| 报表导出 | [业务开发/报表导出.md](业务开发/报表导出.md) | 🆕 占位 | wms-report |
| 监控预警 | [业务开发/监控预警.md](业务开发/监控预警.md) | 🆕 占位 | wms-monitor |

---

## 3. 系统管理

| 主题 | 文档 | 状态 | 后端模块 |
|------|------|------|----------|
| 认证鉴权（JWT / 限流） | [系统管理/认证鉴权.md](系统管理/认证鉴权.md) | 🆕 占位 | wms-auth |
| 权限体系（数据权限/菜单权限） | [系统管理/权限体系.md](系统管理/权限体系.md) | 🆕 占位 | wms-auth + wms-system |
| 用户/角色/部门/供应商 | 合并至"系统管理"上层 wiki | 🟡 待拆 | wms-system |

---

## 4. 部署运维

| 主题 | 文档 | 状态 |
|------|------|------|
| Docker 中间件编排 | [部署运维/Docker部署.md](部署运维/Docker部署.md) | 🟡 占位（参考 [快速上手/环境搭建.md](快速上手/环境搭建.md)） |
| CI 流水线 | [部署运维/CI流水线.md](部署运维/CI流水线.md) | 🟡 占位（参考 [.github/workflows/ci.yml](../.github/workflows/ci.yml)） |
| check-rules.sh 本地执行 | [部署运维/check-rules.md](部署运维/check-rules.md) | 🆕 占位 |

---

## 5. 技术层

| 主题 | 文档 | 状态 |
|------|------|------|
| Spring Boot 3.x 项目约定 | [技术层/SpringBoot规范.md](技术层/SpringBoot规范.md) | 🟡 占位 |
| **MyBatis-Plus 规范（含 DB-07 红线）** | [技术层/MyBatisPlus规范.md](技术层/MyBatisPlus规范.md) | 🆕 占位 |
| Vue 3 + Vite 前端规范 | [技术层/Vue3规范.md](技术层/Vue3规范.md) | 🟡 占位 |
| **公共常量类与工具清单（21 + 2）** | [技术层/常量与工具.md](技术层/常量与工具.md) | 🆕 占位（基于 AGENTS.md 4.3.2） |

---

## 6. 资产层

| 主题 | 文档 | 状态 |
|------|------|------|
| **代码模板（Entity / Service / Controller / Converter）** | [资产层/代码模板.md](资产层/代码模板.md) | ✅ 已更新（2026-06-02 修复 DB-07 红线） |
| 历史需求索引 | [资产层/历史需求索引.md](资产层/历史需求索引.md) | 🟡 占位 |
| **Harness 变更归档** | [../.harness/changes/](../.harness/changes/) | ✅ 已有 |
| **Wiki 待补文档跟踪** | [wiki/TODO.md](TODO.md) | 🆕 新建（2026-06-02） |

---

## 7. 索引（按角色）

### 7.1 新人入职（按顺序读 30 分钟）

1. [快速上手/环境搭建.md](快速上手/环境搭建.md)
2. [快速上手/项目启动.md](快速上手/项目启动.md)
3. [../AGENTS.md](../AGENTS.md)（重点：3.2 逻辑删除、4.1 库存异常、4.3 魔法数字、4.4 异常信息）
4. [业务开发/库房管理.md](业务开发/库房管理.md)
5. [业务开发/物品库存.md](业务开发/物品库存.md)
6. [业务开发/入库流程.md](业务开发/入库流程.md)
7. [业务开发/出库流程.md](业务开发/出库流程.md)
8. [业务开发/归还报废调拨.md](业务开发/归还报废调拨.md)
9. [业务开发/PDA移动端.md](业务开发/PDA移动端.md)
10. [系统管理/认证鉴权.md](系统管理/认证鉴权.md)
11. [系统管理/权限体系.md](系统管理/权限体系.md)
12. [资产层/代码模板.md](资产层/代码模板.md)

### 7.2 AI 协作者（每次任务前 5 分钟必读）

1. [../AGENTS.md](../AGENTS.md)
2. [../.harness/rules/工程结构.md](../.harness/rules/%E5%B7%A5%E7%A8%8B%E7%BB%93%E6%9E%84.md)
3. [../.harness/rules/编码约束.md](../.harness/rules/%E7%BC%96%E7%A0%81%E7%BA%A6%E6%9D%9F.md)
4. [../.harness/agents/wms-owner.md](../.harness/agents/wms-owner.md) 阶段 0-1
5. 本 Wiki 的相关业务文档（按任务关键词定位）

### 7.3 评审者

1. [../.harness/rules/质量门禁.md](../.harness/rules/%E8%B4%A8%E9%87%8F%E9%97%A8%E7%A6%81.md)
2. [../.harness/skills/expert-reviewer/spec.md](../.harness/skills/expert-reviewer/spec.md)
3. [资产层/代码模板.md](资产层/代码模板.md)
4. [技术层/MyBatisPlus规范.md](技术层/MyBatisPlus规范.md)

---

## 8. 维护规则

- 新增模块时，**先在 §2 业务开发表新增入口**（即使文档是占位），再补内容。
- 修改 [AGENTS.md](../AGENTS.md) 后，**同步本 Wiki 顶部"最近更新"日期**。
- check-rules.sh 升级到新版本后，**同步 [技术层/MyBatisPlus规范.md](技术层/MyBatisPlus规范.md) 引用**。
- 占位文档使用统一格式（见 [wiki/TODO.md](TODO.md) 的"占位模板"）。

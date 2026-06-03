# Wiki 索引刷新 — 变更摘要

- **变更类型**: docs
- **创建日期**: 2026-06-02
- **完成日期**: 2026-06-02

## 目标

承接 [2026-06-01 Harness Engineering 变更](docs-harness-engineering-refresh-20260601) 完成的"结构层对齐"与 [2026-06-02 规则层变更](docs-harness-engineering-refresh-20260602) 完成的"规则层对齐"，本次专注于 **Wiki 知识库的清理与同步**：
1. 清理 wiki/README.md 中 10 个死链
2. 修复 2 处红线违规（环境搭建.md 硬编码密码 / 代码模板.md 错删除模式）
3. 补全 7 个新模块（ElectronicLabel/MachineSpare/LayoutElement/PDA/StockCheck/审批/报表）的入口
4. 沉淀 17 个占位文档（按 P0/P1/P2 优先级）
5. 引入 wiki/TODO.md 跟踪待补文档

## 流程状态

| 阶段 | 状态 |
|------|------|
| 现状分析 | ✅ 完成（发现 10 死链 + 2 红线违规） |
| 索引重写 | ✅ 完成（README.md 改为分层结构） |
| 红线修复 | ✅ 完成（环境搭建.md + 代码模板.md） |
| 占位文档创建 | ✅ 完成（17 个） |
| 待补跟踪 | ✅ 完成（TODO.md） |
| 文档归档 | ✅ 完成（本次 summary.md） |

## 变更文件清单（20 个）

### 修改（3 个）

| 文件 | 变更 |
|------|------|
| [wiki/README.md](../../wiki/README.md) | 清理 10 死链 + 改为分层结构 + 引用 Harness |
| [wiki/快速上手/环境搭建.md](../../wiki/%E5%BF%AB%E9%80%9F%E4%B8%8A%E6%89%8B/%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA.md) | **修复红线**：MySQL 密码 `wqy123456` → `${MYSQL_PASSWORD}` 环境变量 |
| [wiki/资产层/代码模板.md](../../wiki/%E8%B5%84%E4%BA%A7%E5%B1%82/%E4%BB%A3%E7%A0%81%E6%A8%A1%E6%9D%BF.md) | **修复红线**：ServiceImpl 删除模板 `setDelFlag+updateById` → `LogicDeleteHelper.markDeleted` |

### 新建（17 个）

| # | 文件 | 优先级 | 状态 |
|---|------|--------|------|
| 1 | [wiki/TODO.md](../../wiki/TODO.md) | P0 | 🆕 跟踪清单 |
| 2 | [wiki/业务开发/出库流程.md](../../wiki/%E4%B8%9A%E5%8A%A1%E5%BC%80%E5%8F%91/%E5%87%BA%E5%BA%93%E6%B5%81%E7%A8%8B.md) | 🔴 P0 | 🆕 占位 |
| 3 | [wiki/业务开发/归还报废调拨.md](../../wiki/%E4%B8%9A%E5%8A%A1%E5%BC%80%E5%8F%91/%E5%BD%92%E8%BF%98%E6%8A%A1%E5%BA%9F%E8%B0%83%E6%8B%A8.md) | 🔴 P0 | 🆕 占位 |
| 4 | [wiki/业务开发/PDA移动端.md](../../wiki/%E4%B8%9A%E5%8A%A1%E5%BC%80%E5%8F%91/PDA%E7%A7%BB%E5%8A%A8%E7%AB%AF.md) | 🔴 P0 | 🆕 占位 |
| 5 | [wiki/系统管理/认证鉴权.md](../../wiki/%E7%B3%BB%E7%BB%9F%E7%AE%A1%E7%90%86/%E8%AE%A4%E8%AF%81%E9%89%B4%E6%9D%83.md) | 🔴 P0 | 🆕 占位 |
| 6 | [wiki/系统管理/权限体系.md](../../wiki/%E7%B3%BB%E7%BB%9F%E7%AE%A1%E7%90%86/%E6%9D%83%E9%99%90%E4%BD%93%E7%B3%BB.md) | 🔴 P0 | 🆕 占位 |
| 7 | [wiki/技术层/MyBatisPlus规范.md](../../wiki/%E6%8A%80%E6%9C%AF%E5%B1%82/MyBatisPlus%E8%A7%84%E8%8C%83.md) | 🟠 P1 | 🆕 含 DB-07 红线详解 |
| 8 | [wiki/技术层/常量与工具.md](../../wiki/%E6%8A%80%E6%9C%AF%E5%B1%82/%E5%B8%B8%E9%87%8F%E4%B8%8E%E5%B7%A5%E5%85%B7.md) | 🟠 P1 | 🆕 21 常量 + 2 工具表 |
| 9 | [wiki/业务开发/库房布局可视化.md](../../wiki/%E4%B8%9A%E5%8A%A1%E5%BC%80%E5%8F%91/%E5%BA%93%E6%88%BF%E5%B8%83%E5%B1%80%E5%8F%AF%E8%A7%86%E5%8C%96.md) | 🟠 P1 | 🆕 占位 |
| 10 | [wiki/业务开发/库存盘点.md](../../wiki/%E4%B8%9A%E5%8A%A1%E5%BC%80%E5%8F%91/%E5%BA%93%E5%AD%98%E7%9B%98%E7%82%B9.md) | 🟠 P1 | 🆕 占位 |
| 11 | [wiki/业务开发/电子标签.md](../../wiki/%E4%B8%9A%E5%8A%A1%E5%BC%80%E5%8F%91/%E7%94%B5%E5%AD%90%E6%A0%87%E7%AD%BE.md) | 🟠 P1 | 🆕 占位 |
| 12 | [wiki/业务开发/机器备品.md](../../wiki/%E4%B8%9A%E5%8A%A1%E5%BC%80%E5%8F%91/%E6%9C%BA%E5%99%A8%E5%A4%87%E5%93%81.md) | 🟠 P1 | 🆕 占位 |
| 13 | [wiki/业务开发/审批流程.md](../../wiki/%E4%B8%9A%E5%8A%A1%E5%BC%80%E5%8F%91/%E5%AE%A1%E6%89%B9%E6%B5%81%E7%A8%8B.md) | 🟠 P1 | 🆕 占位 |
| 14 | [wiki/业务开发/报表导出.md](../../wiki/%E4%B8%9A%E5%8A%A1%E5%BC%80%E5%8F%91/%E6%8A%A5%E8%A1%A8%E5%AF%BC%E5%87%BA.md) | 🟡 P2 | 🆕 占位 |
| 15 | [wiki/业务开发/监控预警.md](../../wiki/%E4%B8%9A%E5%8A%A1%E5%BC%80%E5%8F%91/%E7%9B%91%E6%8E%A7%E9%A2%84%E8%AD%A6.md) | 🟡 P2 | 🆕 占位 |
| 16 | [wiki/技术层/SpringBoot规范.md](../../wiki/%E6%8A%80%E6%9C%AF%E5%B1%82/SpringBoot%E8%A7%84%E8%8C%83.md) | 🟡 P2 | 🆕 占位 |
| 17 | [wiki/技术层/Vue3规范.md](../../wiki/%E6%8A%80%E6%9C%AF%E5%B1%82/Vue3%E8%A7%84%E8%8C%83.md) | 🟡 P2 | 🆕 占位 |
| 18 | [wiki/部署运维/Docker部署.md](../../wiki/%E9%83%A8%E7%BD%B2%E8%BF%90%E7%BB%B4/Docker%E9%83%A8%E7%BD%B2.md) | 🟡 P2 | 🆕 占位 |
| 19 | [wiki/部署运维/CI流水线.md](../../wiki/%E9%83%A8%E7%BD%B2%E8%BF%90%E7%BB%B4/CI%E6%B5%81%E6%B0%B4%E7%BA%BF.md) | 🟡 P2 | 🆕 占位 |
| 20 | [wiki/部署运维/check-rules.md](../../wiki/%E9%83%A8%E7%BD%B2%E8%BF%90%E7%BB%B4/check-rules.md) | 🟡 P2 | 🆕 占位 |
| 21 | [wiki/资产层/历史需求索引.md](../../wiki/%E8%B5%84%E4%BA%A7%E5%B1%82/%E5%8E%86%E5%8F%B2%E9%9C%80%E6%B1%82%E7%B4%A2%E5%BC%95.md) | 🟡 P2 | 🆕 占位 |

## 关键修复

### 🔴 红线 #1：环境搭建.md 硬编码 MySQL 密码

**之前（违规）**：
```yaml
password: wqy123456    # 硬编码，违反 AGENTS.md 1.1
```

**之后（合规）**：
```yaml
password: ${MYSQL_PASSWORD:}    # 环境变量
```

**额外加固**：补充 4 种设置环境变量的方法（PowerShell 临时 / Windows 系统级 / Linux 临时 / IDEA），并加"启动前必查"验证步骤。

### 🔴 红线 #2：代码模板.md 教错删除模式

**之前（违反 DB-07）**：
```java
Xxx update = new Xxx();
update.setId(id);
update.setDelFlag(DelFlagConstants.DELETED);
mapper.updateById(update);    // ❌ @TableLogic 字段会被跳过
```

**之后（合规）**：
```java
LogicDeleteHelper.markDeleted(mapper, Xxx.class, id);    // ✅
```

**额外加固**：
- 在文件顶部加"红线提示"警告块
- 在 ServiceImpl 模板中增加 `deleteBatch` 示例
- 在 import 区增加 `LogicDeleteHelper` 和 `Wrappers`
- 增加 2 段 ❌ 错误示例对比

## 维护规则沉淀

[wiki/README.md §8](../../wiki/README.md) 新增"维护规则"章节，强制：
- 新增模块 → 先在业务开发表新增入口（即使占位）
- 修改 AGENTS.md → 同步 Wiki 顶部"最近更新"日期
- check-rules.sh 升级 → 同步 MyBatisPlus规范 引用
- 占位文档使用统一格式（详见 wiki/TODO.md）

## 例外记录

- 占位文档为骨架级，**业务内容仍需后续 PR 补充**。本变更只解决"误导性死链"和"红线违规"，不替代业务知识沉淀。
- 5 个 🔴 P0 文档（出库/归还报废调拨/PDA/认证鉴权/权限体系）的状态、API、流程图等需由对应模块负责人补全。
- [wms-web/wms-pda/](../../wms-web/) 路径在本会话未做实际验证（LS 已确认 wms-web 存在），PDA 移动端 wiki 中的前端路径如有不符需后续校正。
- 本次未触碰业务代码，仅做文档更新。

## 后续建议

1. **本周内**：把 wiki/TODO.md 中的 5 个 🔴 P0 文档分给对应模块负责人，每周五提交
2. **下 Sprint**：完成 7 个 🟠 P1 文档（库房布局/库存盘点/电子标签/机器备品/审批/MyBatisPlus/常量）
3. **季度**：把"新增 wiki 文档"作为 PR 自查清单的"附加项"（参考 [AGENTS.md §9](../../AGENTS.md)）
4. **半年**：建立 wiki 覆盖率指标：每个模块至少 1 篇核心文档，覆盖率 ≥ 80%

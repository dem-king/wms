# 备品备件库房管理平台（WMS）

> **单一事实来源（Single Source of Truth）**：如果某个信息不在本文档中，对 AI Agent 来说它就不存在。
> 本文档是 AI 助手理解项目的第一入口，所有变更后请同步更新本文档索引。

---

## 项目概述

备品备件库房管理平台是一套面向企业备品备件管理的完整解决方案，覆盖从**库房规划 → 物品入库 → 出库消耗 → 归还报废 → 统计报表**的全链路管理。

| 维度 | 详情 |
|------|------|
| **代码规模** | ~54,000 行（Java 32,000 + Vue 16,000 + TypeScript 5,500） |
| **后端框架** | Spring Boot 3.2.5 + MyBatis-Plus 3.5.6 |
| **前端框架** | Vue 3.4 + TypeScript 5.4 + Element Plus 2.7 |
| **构建工具** | Maven（后端）+ Vite 5.2（前端） |
| **Java 版本** | 17 |
| **数据库** | MySQL 8.0 |
| **中间件** | Redis 7 / MinIO（对象存储） |
| **安全框架** | Spring Security + JWT（jjwt 0.12.5） |
| **CI/CD** | GitHub Actions |

---

## 快速导航

### 新人上手（按顺序阅读）

| 序号 | 文档 | 预计时间 |
|------|------|---------|
| 1 | [Git 工作流规范](docs/Git工作流规范.md) | 10 min |
| 2 | [环境搭建](wiki/快速上手/环境搭建.md) | 30 min |
| 3 | [项目启动](wiki/快速上手/项目启动.md) | 15 min |
| 4 | [库房管理链路](wiki/业务开发/库房管理.md) | 20 min |
| 5 | [物品库存链路](wiki/业务开发/物品库存.md) | 20 min |
| 6 | [入库流程](wiki/业务开发/入库流程.md) | 20 min |

### AI 开发必读

| 序号 | 文档 | 用途 |
|------|------|------|
| 1 | [开发规范总则](AGENTS.md) | 编码前必读，所有硬性规则 |
| 2 | [Harness 规则体系](.harness/) | AI 可控运行的约束系统 |
| 3 | [知识库索引](wiki/README.md) | 项目层/技术层/资产层知识索引 |
| 4 | [工程结构规则](.harness/rules/工程结构.md) | 架构分层与模块依赖 |
| 5 | [编码约束规则](.harness/rules/编码约束.md) | 红线规则与代码规范 |
| 6 | [质量门禁](.harness/rules/质量门禁.md) | PR 提交前自检清单 |

### 功能开发参考

| 业务场景 | 后端模块 | 前端页面 | Wiki 文档 |
|---------|---------|---------|----------|
| 库房区域货架管理 | `wms-warehouse` | `views/warehouse/` | [库房管理链路](wiki/业务开发/库房管理.md) |
| 物品分类标签管理 | `wms-item` | `views/item/` | [物品库存链路](wiki/业务开发/物品库存.md) |
| 入库 | `wms-business`/Inbound | `views/business/inbound/` | [入库流程](wiki/业务开发/入库流程.md) |
| 出库 | `wms-business`/Outbound | `views/business/outbound/` | [出库流程](wiki/业务开发/出库流程.md) |
| 归还/报废/调拨 | `wms-business` | `views/business/return\|scrap\|transfer/` | [归还报废调拨](wiki/业务开发/归还报废调拨.md) |
| 审批流 | `wms-approval` | `views/approval/` | — |
| 统计报表 | `wms-report` | `views/report/` | — |
| 库存监控 | `wms-monitor` | `views/monitor/` | — |
| 用户权限管理 | `wms-system` | `views/system/` | [认证鉴权](wiki/系统管理/认证鉴权.md) |
| 登录认证 | `wms-auth` | `views/login/` | [认证鉴权](wiki/系统管理/认证鉴权.md) |

---

## 项目结构

```
WMS_code/
├── README.md                     ← 顶层索引（本文档）
├── AGENTS.md                     ← 开发规范总则
│
├── wms-server/                   ← 后端 Maven 多模块项目
│   ├── pom.xml                   ← 父 POM（依赖管理）
│   ├── wms-app/                  ← 🚀 启动模块（Spring Boot 入口）
│   ├── wms-common/               ← 🧱 公共基础设施层
│   ├── wms-auth/                 ← 🔐 认证鉴权
│   ├── wms-system/               ← ⚙️ 系统管理
│   ├── wms-warehouse/            ← 🏭 库房管理
│   ├── wms-item/                 ← 📦 物品库存
│   ├── wms-business/             ← 📋 业务单据
│   ├── wms-approval/             ← ✅ 审批流程
│   ├── wms-report/               ← 📊 统计报表
│   └── wms-monitor/              ← 📡 监控预警
│
├── wms-web/                      ← 前端 Vue 3 应用
│   ├── src/api/                  ← API 接口层
│   ├── src/components/           ← 公共组件
│   ├── src/views/                ← 页面视图
│   ├── src/store/                ← Pinia 状态管理
│   ├── src/router/               ← 路由配置
│   └── src/layouts/              ← 布局组件
│
├── database/                     ← 数据库脚本
│   ├── wms_ddl.sql               ← DDL 建表脚本
│   └── migration/                ← 增量迁移脚本
│
├── docs/                         ← 设计文档归档
│   └── plans/                    ← 各功能设计文档
│
├── wiki/                         ← 知识库（三层结构）
│   ├── 快速上手/
│   ├── 业务开发/
│   ├── 系统管理/
│   ├── 部署运维/
│   ├── 技术层/
│   └── 资产层/
│
├── .harness/                     ← AI 驾驭工程配置
│   ├── agents/                   ← Agent 角色定义
│   ├── rules/                    ← 规则体系
│   ├── skills/                   ← 技能包
│   ├── changes/                  ← 变更记录归档
│   └── mcp/                      ← 外部工具配置
│
└── .github/workflows/            ← CI/CD 流水线
```

---

## 后端模块详解

### 模块分层架构

```
┌──────────────────────────────────────────────────┐
│  wms-app        ← 启动入口、配置、静态资源         │
├──────────────────────────────────────────────────┤
│  wms-auth       ← 认证：登录/JWT/验证码/限流       │
│  wms-system     ← 系统：用户/角色/菜单/部门/配置    │
├──────────────────────────────────────────────────┤
│  wms-warehouse  ← 库房 → 区域 → 货架 → 库位       │
│  wms-item       ← 物品 → 分类/标签/库存/二维码      │
├──────────────────────────────────────────────────┤
│  wms-business   ← 入库/出库/归还/报废/调拨          │
│  wms-approval   ← 审批流程引擎                    │
│  wms-report     ← 报表/Dashboard/导出             │
│  wms-monitor    ← 库存告警/逾期归还监控            │
├──────────────────────────────────────────────────┤
│  wms-common     ← 基础设施：BaseEntity/异常/注解/枚举│
└──────────────────────────────────────────────────┘
```

### 模块详细说明

| 模块 | 关键 Service | 关键 Controller | 关键 Converter |
|------|-------------|----------------|---------------|
| **wms-app** | — | — | — |
| **wms-auth** | `AuthService`, `TokenService`, `CaptchaService`, `CryptoService` | `AuthController`, `CaptchaController`, `CryptoController` | — |
| **wms-system** | `SysUserService`, `SysRoleService`, `SysMenuService`, `SysDeptService` | `SysUserController`, `SysRoleController`, `SysMenuController` | `SysMenuConverter`, `SysLoginLogConverter`, `SysOperLogConverter` |
| **wms-warehouse** | `WarehouseService`, `AreaService`, `CabinetService`, `BinService` | `AreaController`, `BinController` | `AreaConverter`, `BinConverter` |
| **wms-item** | `ItemService`, `CategoryService`, `StockService`, `LabelService`, `TagService` | `ItemController`, `CategoryController`, `StockController`, `LabelController` | `ItemConverter`, `CategoryConverter`, `StockConverter`, `TagConverter` |
| **wms-business** | `InboundService`, `OutboundService`, `ReturnService`, `ScrapService`, `TransferService` | `InboundController`, `OutboundController`, `ReturnController`, `ScrapController` | `InboundOrderConverter`, `OutboundOrderConverter`, `ReturnOrderConverter`, `ScrapOrderConverter` |
| **wms-approval** | `ApprovalService`, `ApprovalConfigService` | `ApprovalController`, `ApprovalConfigController` | `ApprovalOrderConverter`, `ApprovalConfigConverter` |
| **wms-report** | `DashboardService`, `ChartService`, `ReportExportService`, 及各 ReportService | `DashboardController`, `ChartController`, `ReportExportController` | `ChartOptionConverter`, `ReportConverter` |
| **wms-monitor** | `StockAlertService`, `OverdueReturnService` | `StockAlertController`, `OverdueReturnController` | `MonitorConverter` |

### 模块依赖关系

```
wms-app → wms-common
       → wms-auth → wms-system → wms-common
       → wms-system → wms-common
       → wms-warehouse → wms-common
       → wms-item → wms-warehouse → wms-common
       → wms-business → wms-item / wms-warehouse / wms-system / wms-common
       → wms-approval → wms-business / wms-system / wms-common
       → wms-report → wms-business / wms-item / wms-warehouse / wms-system / wms-common
       → wms-monitor → wms-business / wms-item / wms-warehouse / wms-common
```

---

## 前端模块详解

| 目录 | 职责 | 关键文件 |
|------|------|---------|
| `api/` | 后端 API 调用封装 | `request.ts`（Axios 封装）, `auth.ts`, `item/`, `business/` |
| `components/` | 公共 UI 组件 | `TableCrud/`, `SearchBar/`, `FormModal/`, `ApprovalFlow/`, `QrBarCode/` |
| `views/` | 业务页面 | `business/`（入库/出库/归还/报废/调拨）, `item/`, `dashboard/` |
| `store/` | Pinia 状态管理 | `user.ts`, `permission.ts`, `app.ts` |
| `router/` | 路由配置 | `index.ts`, `guard.ts`（路由守卫） |
| `layouts/` | 页面布局 | `DefaultLayout.vue`, `Sidebar.vue`, `Navbar.vue` |
| `types/` | TypeScript 类型定义 | `business.d.ts`, `item.d.ts`, `warehouse.d.ts` |
| `utils/` | 工具函数 | `auth.ts`, `storage.ts`, `crypto.ts` |

---

## Harness 驾驭工程体系

本项目已建立完整的 AI 可控运行约束体系，位于 `.harness/` 目录：

```
.harness/
├── agents/
│   └── wms-owner.md          ← 应用 Owner Agent（9 阶段工作流 + 核心约束）
├── rules/
│   ├── 工程结构.md            ← 架构分层与模块依赖约束
│   ├── 编码约束.md            ← 代码硬性规则（红线）
│   ├── 质量门禁.md            ← PR 提交前检查项
│   └── 人工审查确认点.md       ← HITL 人工介入节点
├── skills/
│   ├── request-analysis/     ← 需求分析技能包
│   ├── coding-skill/         ← 编码实现技能包
│   ├── expert-reviewer/      ← 专家评审技能包
│   ├── unit-test-write/      ← 单元测试技能包
│   └── project-analysis/     ← 项目分析技能包
├── changes/                  ← 变更记录归档目录
└── mcp/
    └── mcp-config.md         ← MySQL/Redis/Git 工具配置
```

### 9 阶段开发流水线

```
阶段 0: 项目分析 → 阶段 1: 需求分析 → 阶段 2: 需求评审
                → 阶段 3: 编码实现 → 阶段 4: 编码评审
                → 阶段 5: 测试执行 → 阶段 6: 测试评审
                → 阶段 7: CI 验证  → 阶段 8: 部署验证
                       ↑                  ↑                  ↑
                    HITL-1          HITL-2 + HITL-4        HITL-3
```

本地规则检查：

```bash
./scripts/check-rules.sh
./scripts/check-rules.sh --strict
```

Windows 可使用 PowerShell 入口代理 Git Bash 或已安装 Linux 发行版的 WSL：

```powershell
powershell -ExecutionPolicy Bypass -File scripts/check-rules.ps1
powershell -ExecutionPolicy Bypass -File scripts/check-rules.ps1 --strict
```

---

## 数据库

| 资源 | 路径 |
|------|------|
| 完整初始化脚本 | `database/wms_full_init.sql` |
| DDL 建表脚本 | `database/wms_ddl.sql` |
| 增量迁移脚本 | `database/migration/` |
| ER 图 | `database/ER_diagram.md` |

所有表必须包含的公共字段：`id`（雪花ID）、`del_flag`（逻辑删除）、`create_time`、`create_by`、`update_time`、`update_by`。

---

## 启动命令

```bash
# 后端（wms-server 目录）
mvn clean install -DskipTests
cd wms-app
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 前端（wms-web 目录）
npm install
npm run dev
```

### 环境依赖

| 服务 | 版本 | 开发默认端口 |
|------|------|-------------|
| MySQL | 8.0 | 3306 |
| Redis | 7.x | 6379 |
| MinIO | latest | 9000/9001 |

---

## 编码铁律（红线）

> 以下规则违反任何一条，代码必须退回修改。详细说明见 [AGENTS.md](AGENTS.md)。

1. 🔴 **禁止硬编码密码/密钥** — 全部使用 `${ENV_VAR:default}` 注入
2. 🔴 **所有接口加 `@PreAuthorize`** — 最小粒度 `isAuthenticated()`
3. 🔴 **查询接口加 `@DataScope`** — 实现数据隔离
4. 🔴 **写操作加 `@OperLog`** — 记录操作日志
5. 🔴 **禁止物理删除** — 统一 `setDelFlag(DelFlagConstants.DELETED)` + `updateById`
6. 🔴 **禁止返回 Entity** — Controller 和 Service 公共方法只返回 VO
7. 🔴 **禁止魔法数字** — 状态值必须使用常量类或枚举
8. 🔴 **禁止 N+1 查询** — 列表查询用 `selectBatchIds` 构建 Map
9. 🔴 **库存不足必须抛 BizException** — 禁止静默修正为 0

---

## FAQ

**Q: 新增一个数据库表需要做什么？**
A: 参考 [AGENTS.md](AGENTS.md) 第七节建表规则，确保包含公共字段、主键用雪花ID、不用 AUTO_INCREMENT。

**Q: 新增一个业务接口的完整流程？**
A: 参考 `.harness/agents/wms-owner.md` 的 9 阶段流水线：项目分析 → 需求分析 → 需求评审 → 编码 → 编码评审 → 测试 → 测试评审 → CI → 部署。

**Q: 库存不足应该怎么处理？**
A: 抛出 `BizException("库存不足: itemId=" + itemId)`，禁止 `stock.setQuantity(0)`。

**Q: 如何添加一个新的常量？**
A: 公共常量放 `wms-common/constant/`，模块常量放各模块 `domain/constant/`，常量类以 `Constants` 结尾。

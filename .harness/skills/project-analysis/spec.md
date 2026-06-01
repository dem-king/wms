# Project Analysis Skill

## 触发条件

- 首次接触 WMS 项目
- 收到需求前需要确认现状
- 新人上手引导

## 分析步骤

### Step 1: 阅读 Agent 定义

打开 `.harness/agents/wms-owner.md`，理解：
- 项目背景和技术栈
- 模块结构（10 个 Maven 模块 + 1 个前端）
- 配置中枢索引（Rules / Skills / Wiki / MCP 在哪里）

### Step 2: 阅读核心规则

按顺序阅读 `.harness/rules/` 下的文件：
1. `工程结构.md` — 理解模块划分和分层约束
2. `编码约束.md` — 理解每条可程序化验证的规则
3. `质量门禁.md` — 理解 CI 门禁和评审标准

### Step 3: 理解目录结构

关键路径速查：
- 后端入口：`wms-server/wms-app/src/main/java/com/wms/app/WmsApplication.java`
- 配置文件：`wms-server/wms-app/src/main/resources/application.yml`
- 前端入口：`wms-web/src/main.ts`
- CI 配置：`.github/workflows/ci.yml`

### Step 4: 识别业务模块

基于需求的关键词判断涉及哪些模块：

| 需求关键词 | 涉及模块 |
|-----------|---------|
| 库房/区域/货架/库位 | wms-warehouse |
| 物品/分类/标签/库存 | wms-item |
| 入库/出库 | wms-business (Inbound/Outbound) |
| 归还/报废/调拨 | wms-business (Return/Scrap/Transfer) |
| 用户/角色/菜单/权限 | wms-system + wms-auth |
| 审批 | wms-approval |
| 报表/统计/导出 | wms-report |
| 监控/预警 | wms-monitor |

### Step 5: 检查已有代码

在开始编码前：
- 检查该模块是否已有类似功能的代码可参考
- 检查已有 Entity / VO / Converter 的最新版本
- 确认 CI 最近一次运行状态（如果可用）

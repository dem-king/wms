# Harness Engineering 规则同步更新 — 变更摘要

- **变更类型**: docs
- **创建日期**: 2026-06-02
- **完成日期**: 2026-06-02

## 目标

承接 [2026-06-01 变更](docs-harness-engineering-refresh-20260601) 完成的"结构层对齐"，本次专注于"规则层 + 内容层"对齐：
1. 修复 `.harness/rules/编码约束.md` 与 [AGENTS.md](../../AGENTS.md) 的直接冲突（DB-03 逻辑删除规则红线遗漏）
2. 补全 [AGENTS.md](../../AGENTS.md) 中已存在但 Harness 未沉淀的常量类清单
3. 将新增模块/子包（ElectronicLabel、MachineSpare、LayoutElement、PDA、StockCheck、审批策略、监控任务）同步到 Harness 索引
4. 在 check-rules.sh 中新增 4 条可程序化验证的规则（DB-07、CONST-03、INV-01、NAME-01、DDL-01）

## 流程状态

| 阶段 | 状态 | 备注 |
|------|------|------|
| 现状分析 | ✅ 完成 | 已识别 14 处 P0/P1/P2 不一致项 |
| 规则层冲突修复 | ✅ 完成 | DB-03 重写 + DB-07 新增 + CONST-03 新增 |
| 结构层同步 | ✅ 完成 | 工程结构.md 子包补全 + 依赖修正 + event 错误修正 |
| 内容层同步 | ✅ 完成 | wms-owner.md 模块职责表 + project-analysis 关键词映射 |
| 自动化增强 | ✅ 完成 | check-rules.sh 新增 5 个检查函数（v1.1 → v1.2） |
| 文档归档 | ✅ 完成 | 本次 summary.md |

## 变更文件清单

| 文件路径 | 变更类型 | 说明 |
|---------|---------|------|
| .harness/rules/编码约束.md | 修改 | DB-03 重写 + DB-07 新增 + CONST-03 新增 + INV-01/NAME-01/DDL-01 规则条目补全 |
| .harness/rules/工程结构.md | 修改 | 子包清单补全（listener/manager/strategy/task/util/security/config/enums/datascope/context/storage）+ 9 个模块特殊包说明 + 1.1 节模块依赖修正（wms-monitor→system）+ event 错误修正 |
| .harness/rules/质量门禁.md | 修改 | 新增 DB-07 / CONST-03 / NAME-01 / DDL-01 / INV-01 门禁条目 |
| .harness/agents/wms-owner.md | 修改 | 10 个模块职责描述全部更新（新增 ElectronicLabel/MachineSpare/LayoutElement/PDA/StockCheck/策略/任务/事件监听 等） |
| .harness/skills/project-analysis/spec.md | 修改 | Step 4 关键词映射新增 7 项（库房布局、电子标签/RFID、机器备品、库存盘点、PDA、审批策略、日报聚合） |
| AGENTS.md | 修改 | 4.3.2 现有公共常量类清单由 10 项扩到 21 项 + 工具类 2 项（LogicDeleteHelper、SequenceGenerator） |
| scripts/check-rules.sh | 修改 | v1.1 → v1.2；新增 check_DB07 / check_CONST03 / check_INV01 / check_NAME01 / check_DDL01 共 5 个检查函数 |

## 详细规则变更

### P0 — 规则层冲突修复

#### 1. DB-03 规则重写

**之前（错误/遗漏）**：
```
DB-03 | 逻辑删除必须用 DelFlagConstants.DELETED | ... | check_DB03
```
check-rules.sh 中的 `check_DB03` 正则仅检测 `setDelFlag(数字)` 是否使用了 `DelFlagConstants` 常量，但**未禁止** `setDelFlag(DELETED) + updateById` 与 `Db.updateBatchById` 这两条已被 AGENTS.md 3.2 节明令禁止的"假删除"路径。

**之后（与 AGENTS.md 3.2 一致）**：
- DB-03 描述改为"逻辑删除必须显式写 del_flag"，并在表下补充「DB-03 / DB-07 详细说明」段落（LogicDeleteHelper / UpdateWrapper.set 正确用法 + setDelFlag+updateById / Db.updateBatchById 错误用法）
- 新增 **DB-07**：禁止 `setDelFlag + updateById` 与 `Db.updateBatchById` 删除模式（@TableLogic 字段会被 MyBatis-Plus 普通 update 路径跳过）
- 新增 `check_DB07` 函数，扫描这两种违规模式

#### 2. CONST-03 常量类位置规范

**之前**：Harness 无此规则
**之后**：
- 新增 CONST-03：公共常量必须放 `wms-common.constant`；模块常量必须放 `module.domain.constant`
- ServiceImpl/Controller 中禁止 `public static final` 业务常量
- 任意位置的 `*Constants.java` 类必须在 wms-common/constant 或 module/domain/constant 包下
- 新增 `check_CONST03` 函数，三路检查（ServiceImpl 静态、Controller 静态、类位置）

### P1 — 结构层同步

#### 3. 工程结构.md 子包补全

**之前**：1.2 节包结构只列了 7 个基础包，且 `event/` 标注"仅 wms-business"
**之后**：
- 基础包结构图扩到 11 个（增加 listener/strategy/task/manager/util + 1 个 service.support）
- controller/service 增加 `{sub}/` 二级分类说明（wms-business.controller.pda）
- 新增"特殊模块补充包"表格，覆盖 9 个模块（wms-common/wms-auth/wms-system/wms-warehouse/wms-item/wms-business/wms-approval/wms-monitor/wms-report）
- 删除 `event/` "仅 wms-business" 错误标注，修正 wms-common 也含 event
- 1.1 节模块依赖增加 wms-monitor → wms-system 边
- 1.3 节公共组件位置扩到 9 类（新增 event/datascope/context/storage + 模块级常量位置）

#### 4. wms-owner.md 模块职责表扩展

| 模块 | 新增内容 |
|------|---------|
| wms-common | 增加"事件、数据权限、存储策略" |
| wms-auth | 增加"Spring Security 配置、认证事件监听" |
| wms-system | 增加"操作/登录日志、数据权限切面、操作日志切面" |
| wms-warehouse | 增加"库房布局元素（LayoutElement，可视化编辑器）" |
| wms-item | 增加"电子标签（ElectronicLabel，含 RFID）、机器备品（MachineSpare）、闲置检测任务" |
| wms-business | 增加"库存盘点、PDA 专用接口、库存事件发布与监听、跨 Service 校验工具" |
| wms-approval | 增加"审批配置、审批记录、策略模式（Free/Single/Multi）、审批事件监听、超时任务" |
| wms-report | 增加"日报聚合任务、Excel/PDF 导出、图表服务、高德地图配置" |
| wms-monitor | 改"预警任务"为"预警任务调度" |
| wms-web | 增加"含 PDA 移动端 wms-pda" |

#### 5. project-analysis 关键词映射补全

7 个新增关键词到模块的映射：库房布局/可视化/编辑器、电子标签/RFID、机器备品/设备配件、库存盘点/扫码盘点、PDA/手持设备、审批策略/审批配置、登录认证/JWT、日报聚合/定时报表。

### P2 — 自动化增强

#### 6. check-rules.sh 新增 5 个检查（v1.1 → v1.2）

| 新规则 | 阻断/警告 | 检测目标 |
|--------|---------|---------|
| check_DB07 | 🔴 阻断 | `setDelFlag(.*)` 紧跟 `updateById` 模式 / `Db.updateBatchById` 调用 |
| check_CONST03 | 🔴 阻断 | ServiceImpl/Controller 内的 `public static final` + 错误位置的 `*Constants.java` |
| check_INV01 | 🟡 警告 | `setQuantity(0)` 紧邻 5 行内无 `throw new BizException` |
| check_NAME01 | 🔴 阻断 | DTO/VO 字段定义中使用下划线（违反 AGENTS.md 8.1） |
| check_DDL01 | 🔴 阻断 | CREATE TABLE 块缺少 del_flag/create_time/create_by/update_time/update_by |

## 例外记录

- 本次只更新 Harness Engineering 体系与 AGENTS.md 文档，不修改业务代码。
- 本机无 Git Bash 与 WSL，check-rules.sh 只能通过 CI（Ubuntu）验证；本地 PowerShell wrapper 仍按 2026-06-01 变更说明返回 exit 2。
- 9 个模块的补充包表（wms-auth/wms-system/wms-item/wms-business/wms-approval/wms-monitor/wms-report）中列出的所有包均经 `LS` 验证存在，但 PDA 子包（wms-business.controller.pda、wms-business.service.pda.impl 等）仅 wms-business 实际拥有，其他模块未做硬性约束（标注为"大型模块的二级分类"作为可选模式）。
- 4.3.2 常量清单中：`StorageConstants` 实际位于 `wms-common/storage/StorageConstants.java`，与 wms-common.constant 同级但路径深一层；标注位置以实际为准。
- `LabelConstants` 实际有 `LABEL_TYPE_RFID = 3`，原文档"RFID类型"含义正确，扩展为"LABEL_TYPE_RFID 等标签类型"以包含未来扩展。
- `OrderConstants` 新增"PD"前缀（盘点单），原文档未列。

## 后续建议

1. **下个 Sprint**：在 CI 跑通后归档一次 `feat-validate-rule-202606XX/` 变更记录，附 `mvn-output.txt` 验证新增检查未误报。
2. **季度回访**：当再有新模块/新子包出现时，按本变更的方法（先扫描代码、再补规则、再补 check 脚本）增量更新。
3. **未来增强候选**：
   - check_INV02：审批事件发布时机（需要结合 git diff 上下文，复杂度高，保留为人工审查）
   - check_ARCH05：禁止 Controller 调用 SecurityUtil（已有人工审查，自动化收益低）
   - check_NAME02：后端字段 vs 前端 API 参数一致性（需要 TypeScript AST 解析，超出 Harness 范围）

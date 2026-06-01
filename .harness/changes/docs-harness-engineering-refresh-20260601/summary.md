# Harness Engineering 更新 — 变更摘要

- **变更类型**: docs
- **创建日期**: 2026-06-01
- **完成日期**: 2026-06-01

## 目标

让 Harness Engineering 体系与当前 WMS 项目保持一致：10 个后端 Maven 模块、9 阶段工作流、实际 CI 命令、统一变更归档结构，以及 Windows 本地规则检查入口。

## 流程状态

| 阶段 | 状态 | 备注 |
|------|------|------|
| 现状分析 | ✅ 完成 | 已核对 `.harness/`、CI、POM 模块和本地运行环境 |
| 文档对齐 | ✅ 完成 | 对齐模块、阶段流、HITL、门禁、变更归档 |
| 本地入口 | ✅ 完成 | 新增 PowerShell wrapper，代理 bash/WSL |
| 验证 | ✅ 完成 | Wrapper 可运行并给出缺少 bash/可用 WSL 的明确提示；一致性扫描未发现旧描述 |

## 变更文件清单

| 文件路径 | 变更类型 | 说明 |
|---------|---------|------|
| docs/plans/2026-06-01-harness-engineering-refresh.md | 新增 | 本次更新计划 |
| .harness/agents/wms-owner.md | 修改 | 对齐当前模块数量、9 阶段流、HITL |
| .harness/rules/工程结构.md | 修改 | 对齐实际模块依赖和活跃模块 |
| .harness/rules/质量门禁.md | 修改 | 对齐 CI 实际命令和本地入口 |
| .harness/skills/project-analysis/spec.md | 修改 | 对齐当前项目现状 |
| .harness/skills/request-analysis/spec.md | 修改 | 对齐变更归档路径 |
| .harness/skills/unit-test-write/spec.md | 修改 | 对齐阶段描述和输出路径 |
| .harness/changes/README.md | 修改 | 标准化变更目录结构 |
| README.md | 修改 | 对齐 Harness 索引和本地检查命令 |
| scripts/check-rules.ps1 | 新增 | Windows 本地规则检查入口 |

## 例外记录

- 本次只更新 Harness Engineering 体系，不修改业务代码。
- `scripts/check-rules.sh` 仍作为 CI 主规则实现，PowerShell 脚本仅负责本地跨平台入口代理。
- 当前本机无 Git Bash，且 WSL 无可用发行版，因此 wrapper 验证结果为 exit 2；这是预期的环境提示路径，不代表规则检查失败。

# CI 流水线

> **状态**: ⚠️ 占位（待补充）— 创建于 2026-06-02
> **负责人**: <TBD>
> **关联模块**: `.github/workflows/ci.yml`
> **优先级**: 🟡 P2 — 辅助文档

## 文档目标

- CI 触发条件（push / pull_request / schedule）
- 任务编排（build / test / lint / deploy）
- check-rules.sh 在 CI 中的执行
- 缓存策略（Maven / npm）

## 当前已知要点

- 工作流: `.github/workflows/ci.yml`
- runner: ubuntu-latest
- 触发: push / pull_request 到 develop / master

## 待补内容

- [ ] 工作流结构
- [ ] 任务清单与依赖
- [ ] check-rules.sh 集成位置
- [ ] 缓存策略
- [ ] 失败处理

## 关联规则引用

- [check-rules.sh](../../scripts/check-rules.sh) — 24 条 CI 检查
- [质量门禁.md](../../.harness/rules/%E8%B4%A8%E9%87%8F%E9%97%A8%E7%A6%81.md) — 阻断 vs 警告清单

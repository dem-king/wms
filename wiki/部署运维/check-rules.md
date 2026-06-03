# check-rules.sh 本地执行

> **状态**: ⚠️ 占位（待补充）— 创建于 2026-06-02
> **负责人**: <TBD>
> **关联模块**: scripts/check-rules.sh
> **优先级**: 🟡 P2 — 辅助文档

## 文档目标

- 在本地执行 check-rules.sh 的步骤
- 24 个 check 函数说明
- 与 CI 的一致性
- pre-commit 集成

## 当前已知要点

- 脚本: `scripts/check-rules.sh`（Linux/macOS 用）、`scripts/check-rules.ps1`（Windows）
- 当前版本: v1.2（2026-06-02）
- 检查函数: 24 个（DB-01~07、SEC-01~04、ARCH-01~04、NAME-01/03/05、CONST-01~03、SEQ-01、PERF-01/02、JAVADOC-01/02/03、INV-01、DDL-01）

## 待补内容

- [ ] 本地执行命令
- [ ] 输出解读（PASS / WARN / FAIL）
- [ ] 与 CI 的等价性
- [ ] pre-commit 集成（Git Hook）
- [ ] Windows PowerShell 注意事项

## 关联规则引用

- [check-rules.sh](../../scripts/check-rules.sh) — 主脚本
- [编码约束.md](../../.harness/rules/%E7%BC%96%E7%A0%81%E7%BA%A6%E6%9D%9F.md) — 规则文档
- [质量门禁.md](../../.harness/rules/%E8%B4%A8%E9%87%8F%E9%97%A8%E7%A6%81.md) — 阻断级别
- [CI流水线.md](CI流水线.md) — CI 集成

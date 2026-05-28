# 需求分析 Skill

## 触发条件

收到新需求时自动加载本 Skill。

## 职责

将用户模糊的需求转化为精确、可验证的 spec.md（需求分析文档），确保编码 Agent 不会误解需求意图。

## 分析步骤

### Step 1: 需求澄清

在开始编写 spec 之前，对以下问题自我检查，如有不明确，主动向用户提问：

1. 这个需求解决什么用户痛点？
2. 涉及哪些模块和业务链路？
3. 是否有数据库变更（新表/改表）？
4. 是否有审批流程需求？
5. 是否影响库存数据？
6. 是否涉及外部系统调用？
7. 前端是否需要同步变更？
8. 是否有数据迁移需求？

### Step 2: 模块定位

基于 `.harness/skills/project-analysis/spec.md` 的模块映射表，确定涉及的模块：

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

### Step 3: 编写 spec.md

spec.md 必须包含以下章节：

```markdown
# {需求名称} — 需求分析文档

## 用户故事
- 作为 {角色}，我希望 {目标}，以便 {价值}

## 验收标准（Acceptance Criteria）
- [ ] AC-1: {可验证的条件1}
- [ ] AC-2: {可验证的条件2}
- [ ] AC-3: {可验证的条件3}

## 涉及模块
| 模块 | 职责 | 是否有已有代码可参考 |
|------|------|-------------------|
| | | |

## 数据模型变更
| 表名 | 操作 | 字段 | 说明 |
|------|------|------|------|
| | | | |

## 业务约束
- {列出该需求特有的业务规则，如库存安全、审批流、状态流转等}

## 系统约束
- {列出技术约束，如编号生成规则、并发安全、权限控制等}

## 风险评估
- {识别可能的风险点，如性能、数据一致性、权限边界等}
```

### Step 4: 编写 tasks.md

tasks.md 必须将需求拆解为原子化子任务，每个任务明确：

```markdown
# {需求名称} — 任务拆分清单

| 序号 | 任务 | 涉及模块 | 前置依赖 | 产出 | 验收标准 |
|------|------|---------|---------|------|---------|
| 1 | 建表/改表 DDL | | | SQL 文件 | 包含完整公共字段 |
| 2 | Entity 层 | | | WmsXxx.java | 主键用 ASSIGN_ID |
| 3 | DTO 层 | | | XxxDto.java | 小驼峰命名 |
| 4 | Mapper 层 | | | WmsXxxMapper.java | 无物理删除 |
| 5 | Converter 层 | | | XxxConverter.java | @Component |
| 6 | Service 接口 | | | XxxService.java | 返回 Vo |
| 7 | ServiceImpl | | | XxxServiceImpl.java | @Transactional |
| 8 | Controller | | | XxxController.java | @PreAuthorize + @DataScope |
| 9 | 前端页面 | | | views/xxx/index.vue | 参数小驼峰 |
| 10 | 前端 API | | | api/xxx/index.ts | 与后端命名一致 |
| 11 | 单元测试 | | | XxxServiceImplTest.java | 覆盖核心路径 |
```

### Step 5: 评审检查清单

在产出 spec.md 和 tasks.md 后，自我评审：

- [ ] 验收标准是否可验证（不能用"好用""方便"等模糊词）？
- [ ] 是否遗漏了任何模块（如前端同步变更）？
- [ ] 数据模型变更是否包含完整公共字段？
- [ ] 是否考虑了审批流程（如果有状态变更）？
- [ ] 是否考虑了库存操作的安全性（如果有库存变更）？
- [ ] 编号生成是否使用了 SequenceGenerator？
- [ ] 权限控制是否覆盖了所有新接口？

## 输出路径

- spec.md → `.harness/changes/{变更名}/request_analysis/spec.md`
- tasks.md → `.harness/changes/{变更名}/request_analysis/tasks.md`

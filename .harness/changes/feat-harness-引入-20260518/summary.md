# Harness Engineering 体系引入 — 变更摘要

- **变更类型**: feat
- **创建日期**: 2026-05-18
- **完成日期**: 2026-05-18

## 流程状态

| 阶段 | 状态 | 备注 |
|------|------|------|
| 阶段 0: 基础设施（CI/CD） | ✅ 完成 | GitHub Actions: backend + frontend jobs |
| 阶段 0: 基础设施（测试骨架） | ✅ 完成 | 10 个测试用例，全部通过 |
| 阶段 0: 基础设施（并发安全） | ✅ 完成 | 8 个 Service 改用 Redis INCR |
| 阶段 0: 基础设施（Converter） | ✅ 完成 | ItemConverter / StockConverter / InboundOrderConverter |
| 阶段 1: .harness/ 体系 | ✅ 完成 | agents + rules + skills + mcp |
| 阶段 2: Wiki 知识库 | ✅ 完成 | wiki/ README + 快速上手 + 业务开发 + 资产层 |

## 变更文件清单

| 文件路径 | 变更类型 | 说明 |
|---------|---------|------|
| .github/workflows/ci.yml | 新增 | GitHub Actions CI 流水线 |
| wms-common/.../SequenceGenerator.java | 新增 | Redis INCR 序列号生成器 |
| wms-common/.../BizExceptionTest.java | 新增 | BizException 单元测试 |
| wms-common/.../DelFlagConstantsTest.java | 新增 | 逻辑删除常量测试 |
| wms-common/.../BizConstantsTest.java | 新增 | 业务常量测试 |
| wms-common/.../OrderStatusEnumTest.java | 新增 | 订单状态枚举测试 |
| wms-business/.../OrderConstantsTest.java | 新增 | 订单常量测试 |
| wms-business/.../StockSyncEventHandlerTest.java | 新增 | 库存事件处理器测试 |
| wms-business/.../InboundOrderConverter.java | 新增 | 入库单转换器 |
| wms-item/.../ItemConverter.java | 新增 | 物品转换器 |
| wms-item/.../StockConverter.java | 新增 | 库存转换器 |
| wms-business/.../InboundServiceImpl.java | 修改 | 使用 Converter + SequenceGenerator |
| wms-business/.../OutboundServiceImpl.java | 修改 | 使用 SequenceGenerator |
| wms-business/.../ScrapServiceImpl.java | 修改 | 使用 SequenceGenerator |
| wms-business/.../ReturnServiceImpl.java | 修改 | 使用 SequenceGenerator |
| wms-business/.../TransferServiceImpl.java | 修改 | 使用 SequenceGenerator |
| wms-warehouse/.../WarehouseServiceImpl.java | 修改 | 使用 SequenceGenerator |
| wms-warehouse/.../AreaServiceImpl.java | 修改 | 使用 SequenceGenerator |
| wms-warehouse/.../CabinetServiceImpl.java | 修改 | 使用 SequenceGenerator |
| .harness/ | 新增 | Harness 体系目录 |
| wiki/ | 新增 | 知识库目录 |

## 验证结果

- 后端编译：✅ SUCCESS
- 后端测试：✅ 10/10 通过
- 前端构建：待 CI 验证
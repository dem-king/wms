# 库房编码自动生成设计

## 目标

- 新增库房时不再要求用户填写 `warehouseCode`
- 新增请求不主动传递 `warehouseCode`，由后端自动生成
- 编辑库房时继续展示现有 `warehouseCode`，但保持只读

## 方案

- 页面模板中仅在编辑态渲染“库房编码”表单项
- 表单校验规则改为仅编辑态要求 `warehouseCode`
- 前端类型将 `WmsWarehouseDto.warehouseCode` 调整为可选
- 提交逻辑按场景组装参数：
  - 新增：不带 `warehouseCode`
  - 编辑：继续带上已有 `warehouseCode`

## 影响范围

- `src/views/warehouse/warehouse/index.vue`
- `src/types/warehouse.d.ts`
- 相关前端单元测试

## 验证

- 增加针对提交参数组装的测试
- 运行对应测试与类型检查，确认新增/编辑流程均可通过

# Tasks
- [ ] Task 1: 梳理 19-22 的数据边界与扩展策略
  - [ ] SubTask 1.1: 核对 18 已实现的 2D 页面、布局模型、联动状态和入口结构
  - [ ] SubTask 1.2: 核对后端存放柜位置字段、排序字段和现有位置更新接口
  - [ ] SubTask 1.3: 明确本轮持久化范围以“存放柜布局” 为主，区域仍按推导布局处理

- [ ] Task 2: 补齐后端布局持久化能力
  - [ ] SubTask 2.1: 为存放柜补齐稳定排序字段或等价能力
  - [ ] SubTask 2.2: 新增批量保存布局所需 DTO、VO、Service 和 Controller 接口
  - [ ] SubTask 2.3: 确保查询接口可返回保存后的布局位置，并与现有 CRUD 兼容

- [ ] Task 3: 重构前端可视化工作台结构
  - [ ] SubTask 3.1: 将现有可视化页拆分为工具栏、画布、摘要、详情、编辑器等更清晰的组件
  - [ ] SubTask 3.2: 抽离数据加载与选中态逻辑，形成可支撑 2D/2.5D/编辑模式的统一状态模型
  - [ ] SubTask 3.3: 清理或收敛旧的仓储可视化 API 出口，避免并存的错误契约继续扩散

- [ ] Task 4: 实现 2D/2.5D 视图切换
  - [ ] SubTask 4.1: 在可视化页增加 2D / 2.5D 视图切换控件
  - [ ] SubTask 4.2: 基于同一布局模型渲染 2.5D 存放柜与库位效果
  - [ ] SubTask 4.3: 确保视图切换时选中、高亮和定位上下文保持一致

- [ ] Task 5: 实现存放柜详情弹窗
  - [ ] SubTask 5.1: 新增存放柜详情弹窗组件
  - [ ] SubTask 5.2: 展示存放柜基础信息、区域归属、网格规模、库位统计与状态
  - [ ] SubTask 5.3: 对无库位数据、禁用状态等场景提供明确提示

- [ ] Task 6: 实现快速定位与高亮
  - [ ] SubTask 6.1: 增加按区域编码、存放柜编码、库位编码定位的输入入口
  - [ ] SubTask 6.2: 定位命中后自动选中并聚焦目标对象
  - [ ] SubTask 6.3: 未命中时保留当前上下文并给出明确提示

- [ ] Task 7: 实现布局编辑器与保存流程
  - [ ] SubTask 7.1: 新增编辑模式开关和布局编辑器面板
  - [ ] SubTask 7.2: 支持在画布上调整存放柜位置，并实时更新待保存布局
  - [ ] SubTask 7.3: 调用后端布局保存接口完成持久化，并处理成功/失败反馈

- [ ] Task 8: 验证 19-22 联动工作台
  - [ ] SubTask 8.1: 为布局持久化、视图切换、定位和详情逻辑补充针对性测试
  - [ ] SubTask 8.2: 运行前后端相关测试与前端构建，确认未引入回归
  - [ ] SubTask 8.3: 手动验证 2D/2.5D 切换、柜详情、快速定位、布局编辑保存四条主路径

# Task Dependencies
- Task 2 depends on Task 1
- Task 3 depends on Task 1
- Task 4 depends on Task 3
- Task 5 depends on Task 3
- Task 6 depends on Task 3
- Task 7 depends on Task 2
- Task 7 depends on Task 3
- Task 8 depends on Task 4
- Task 8 depends on Task 5
- Task 8 depends on Task 6
- Task 8 depends on Task 7

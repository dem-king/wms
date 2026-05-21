# Tasks
- [x] Task 1: 梳理标签打印与扫码链路的现状边界
  - [x] SubTask 1.1: 核对标签打印预览、打印状态更新和异常反馈的现有实现
  - [x] SubTask 1.2: 核对入库/出库页面、表单、API 与后端服务的现有能力
  - [x] SubTask 1.3: 明确扫码模式与手工模式并存时的数据结构和提交流程

- [x] Task 2: 完善标签打印前端闭环
  - [x] SubTask 2.1: 调整 `wms-web/src/views/label/components/LabelPrint.vue` 的打印结果反馈
  - [x] SubTask 2.2: 区分打印成功、阻止打印、状态同步失败三类交互
  - [x] SubTask 2.3: 确保打印内容与标签详情展示规则保持一致

- [x] Task 3: 补齐扫码入库/出库后端能力
  - [x] SubTask 3.1: 设计并实现入库扫码、出库扫码所需 DTO、Service 和 Controller 接口
  - [x] SubTask 3.2: 复用标签扫码结果并补充业务校验，如重复扫描、标签状态校验
  - [x] SubTask 3.3: 保持现有入库/出库单创建、编辑、提交接口兼容

- [x] Task 4: 接入入库页面扫码模式
  - [x] SubTask 4.1: 在入库表单中增加扫码输入区或扫码操作入口
  - [x] SubTask 4.2: 扫码成功后自动追加或回填入库明细
  - [x] SubTask 4.3: 对重复扫描、无匹配结果等情况给出明确提示

- [x] Task 5: 接入出库页面扫码模式
  - [x] SubTask 5.1: 在出库表单中增加扫码输入区或扫码操作入口
  - [x] SubTask 5.2: 扫码成功后自动追加出库明细并校验标签状态
  - [x] SubTask 5.3: 对重复扫描、不可出库标签等情况给出明确提示

- [x] Task 6: 验证 16-17 功能闭环
  - [x] SubTask 6.1: 为新增的扫码解析与打印状态交互补充针对性测试
  - [x] SubTask 6.2: 运行前后端相关测试或构建命令，确认未引入回归
  - [x] SubTask 6.3: 手动验证标签打印、扫码入库、扫码出库三条用户路径

# Task Dependencies
- Task 2 depends on Task 1
- Task 3 depends on Task 1
- Task 4 depends on Task 3
- Task 5 depends on Task 3
- Task 6 depends on Task 2
- Task 6 depends on Task 4
- Task 6 depends on Task 5

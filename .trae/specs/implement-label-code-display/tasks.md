# Tasks
- [x] Task 1: 梳理标签编码展示边界并对齐现有入口
  - [x] SubTask 1.1: 核对 `ElectronicLabelVo` 中 `labelType`、`qrContent`、`barcodeContent`、`rfidCode` 的使用场景
  - [x] SubTask 1.2: 确认标签详情弹窗、打印预览组件、扫码链路对编码展示的一致性要求
  - [x] SubTask 1.3: 明确二维码、条形码、RFID 三类标签的展示与兜底规则

- [x] Task 2: 重构通用编码展示组件为真实渲染实现
  - [x] SubTask 2.1: 在 `wms-web/src/components/QrBarCode/index.vue` 中接入真实二维码/条形码生成方案
  - [x] SubTask 2.2: 保留统一的类型、尺寸与内容入参，补充渲染失败提示能力
  - [x] SubTask 2.3: 移除可能误导用户的占位式码图输出逻辑

- [x] Task 3: 接入标签详情与打印预览场景
  - [x] SubTask 3.1: 更新 `wms-web/src/views/label/index.vue`，让详情弹窗按标签类型显示真实编码
  - [x] SubTask 3.2: 更新 `wms-web/src/views/label/components/LabelPrint.vue`，复用统一组件输出打印预览
  - [x] SubTask 3.3: 对 RFID 场景输出文本信息而非二维码/条形码图像

- [x] Task 4: 补齐类型、交互与异常处理
  - [x] SubTask 4.1: 校正前端 API 与类型定义中和编码展示相关的字段与请求结构
  - [x] SubTask 4.2: 对空内容、未知类型、渲染异常增加页面提示
  - [x] SubTask 4.3: 确保展示逻辑不影响现有标签生成、绑定与打印状态更新流程

- [x] Task 5: 验证功能可用性
  - [x] SubTask 5.1: 运行前端类型检查或单测，验证组件改造未引入编译错误
  - [x] SubTask 5.2: 手动验证标签详情与打印预览中的二维码/条形码显示正确
  - [x] SubTask 5.3: 验证异常场景下页面提示清晰，且不会展示伪造码图

# Task Dependencies
- Task 2 depends on Task 1
- Task 3 depends on Task 2
- Task 4 depends on Task 2
- Task 5 depends on Task 3
- Task 5 depends on Task 4

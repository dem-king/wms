# Tasks
- [x] Task 1: 梳理 2D 库房视图的数据边界与页面入口
  - [x] SubTask 1.1: 核对库房、区域、存放柜、库位四类现有接口和类型定义
  - [x] SubTask 1.2: 确认 2D 页面放置位置、入口方式和与现有库房表格页的关系
  - [x] SubTask 1.3: 明确本轮只读展示与后续 19-22 功能的边界

- [x] Task 2: 设计 2D 布局数据组装与推导规则
  - [x] SubTask 2.1: 将区域、存放柜、库位数据组装为可渲染的树形或画布模型
  - [x] SubTask 2.2: 依据 `sortOrder`、`rows`、`cols`、`row`、`col` 设计稳定的布局推导规则
  - [x] SubTask 2.3: 明确禁用状态、空数据、异常数据的画布表现

- [x] Task 3: 搭建 Konva 2D 视图页面
  - [x] SubTask 3.1: 新增库房 2D 视图页面与基础筛选区
  - [x] SubTask 3.2: 绘制区域容器、存放柜块和库位网格
  - [x] SubTask 3.3: 增加缩放、自适应或基础画布滚动策略，保证常见数据量可浏览

- [x] Task 4: 接入区域-柜-库位点击联动
  - [x] SubTask 4.1: 点击区域时更新高亮与筛选上下文
  - [x] SubTask 4.2: 点击存放柜时显示其库位网格并同步选中状态
  - [x] SubTask 4.3: 点击库位时展示基础信息摘要

- [x] Task 5: 完善状态反馈与空态
  - [x] SubTask 5.1: 为禁用区域、禁用存放柜、禁用库位设计弱化样式
  - [x] SubTask 5.2: 为无区域、无存放柜、无库位数据提供明确空态
  - [x] SubTask 5.3: 为接口失败和数据装配失败提供错误提示与恢复方式

- [x] Task 6: 验证 2D 库房视图
  - [x] SubTask 6.1: 为布局组装和联动逻辑补充针对性测试
  - [x] SubTask 6.2: 运行前端测试与构建，确认未引入回归
  - [x] SubTask 6.3: 手动验证库房切换、区域点击、存放柜点击、库位点击四条路径

# Task Dependencies
- Task 2 depends on Task 1
- Task 3 depends on Task 2
- Task 4 depends on Task 3
- Task 5 depends on Task 3
- Task 6 depends on Task 4
- Task 6 depends on Task 5

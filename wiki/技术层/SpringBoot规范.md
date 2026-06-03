# Spring Boot 3.x 项目约定

> **状态**: ⚠️ 占位（待补充）— 创建于 2026-06-02
> **负责人**: <TBD>
> **关联模块**: 全局
> **优先级**: 🟡 P2 — 辅助文档

## 文档目标

- Spring Boot 3.x 的项目级配置约定
- 统一异常处理（`@ControllerAdvice` + `BizException`）
- 统一响应封装（`R<T>`）
- 启动类 `WmsApplication` 位置
- profile 配置（dev / prod）

## 当前已知要点

- 启动类: `wms-server/wms-app/src/main/java/com/wms/WmsApplication.java`
- 端口: 8080
- profile: dev（默认）/ prod
- 公共配置: `wms-common/src/main/java/com/wms/common/config/`

## 待补内容

- [ ] 启动类配置（@MapperScan、@EnableAsync、@EnableScheduling）
- [ ] 统一异常处理
- [ ] 统一响应封装
- [ ] 跨域配置（CORS）
- [ ] 异步任务线程池配置
- [ ] 定时任务线程池配置
- [ ] profile 切换注意事项（生产关闭 SQL 日志，[AGENTS.md §7.1](../AGENTS.md)）

## 关联规则引用

- [AGENTS.md §7.1](../AGENTS.md) — 生产环境禁止 SQL 日志输出
- [AGENTS.md §7.2](../AGENTS.md) — Controller 参数必须校验

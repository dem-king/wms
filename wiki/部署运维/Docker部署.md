# Docker 中间件编排

> **状态**: ⚠️ 占位（待补充）— 创建于 2026-06-02
> **负责人**: <TBD>
> **关联模块**: wms-server/docker
> **优先级**: 🟡 P2 — 辅助文档

## 文档目标

- docker-compose 启动 MySQL/Redis/MinIO 一套中间件
- 数据持久化
- 网络配置
- 启动后初始化 SQL

## 当前已知要点

- docker-compose 路径: `wms-server/docker/docker-compose.yml`
- 服务: MySQL 8.0 (3306) / Redis 7.0 (6379) / MinIO (9000/9001)
- SQL 初始化: `database/` 目录

## 待补内容

- [ ] docker-compose.yml 详解
- [ ] 数据卷挂载
- [ ] 网络配置
- [ ] 启动顺序与健康检查
- [ ] 数据库初始化流程

## 关联规则引用

- [环境搭建.md](../快速上手/环境搭建.md) — Docker 启动命令
- [AGENTS.md §1.1](../AGENTS.md) — 密码一律环境变量

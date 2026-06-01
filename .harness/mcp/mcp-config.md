# MCP 工具集成配置

## MySQL MCP

用于数据库查询验证、数据准备和验证。

```json
{
  "mcpServers": {
    "mysql": {
      "command": "mcp-mysql",
      "args": [
        "--host", "${MYSQL_HOST:localhost}",
        "--port", "${MYSQL_PORT:3306}",
        "--user", "${MYSQL_USER:root}",
        "--password", "${MYSQL_PASSWORD}",
        "--database", "wms"
      ]
    }
  }
}
```

## Redis MCP

用于缓存验证、序列号校验。

```json
{
  "redis": {
    "command": "mcp-redis",
    "args": [
      "--host", "${REDIS_HOST:localhost}",
      "--port", "${REDIS_PORT:6379}",
      "--password", "${REDIS_PASSWORD:}"
    ]
  }
}
```

## Git MCP

用于代码提交、历史查询、分支管理。

```json
{
  "git": {
    "command": "mcp-git",
    "args": [
      "--repository", "D:/Codes/WMS_code"
    ]
  }
}
```

---

## 使用策略

- L1 工具（始终可用）：Git — 用于理解代码历史和上下文
- L2 工具（按需启用）：MySQL / Redis — 仅在需要验证数据/状态时启用
- L3 工具（部署阶段）：CI/CD 平台 API — 部署时启用

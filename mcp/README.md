# MCP Server — port-manage 工具集成

将 port-manage 的端口/进程管理能力暴露为 MCP 工具，供 Claude Code (CCB) 等 MCP 客户端直接调用。

## 可用工具

| 工具 | 说明 |
|------|------|
| `scan_ports` | 扫描全量端口列表 |
| `get_port` | 查询指定端口详情 |
| `search_ports` | 关键词搜索端口/进程 |
| `kill_process` | 根据 PID 终止进程 |
| `batch_kill` | 批量终止进程 |
| `get_statistics` | 端口统计摘要 |
| `get_system_info` | 系统信息 |
| `health_check` | 健康检查 |

## 启动顺序

**1. 启动 port-manage 服务**
```bash
cd /Users/panris/Projects/port-manage
java -jar target/port-manage-web.jar
# 默认端口 9527
```

**2. 启动 MCP Server（前台测试）**
```bash
cd /Users/panris/Projects/port-manage/mcp
node dist/index.js
```

**3. 启动 CCB**
```bash
# CCB 会自动读取 ~/.claude/settings.json 中的 mcpServers
# 启动后输入 /mcp 或 /tools 查看已注册的 port-manage 工具
ccb
```

## CCB 配置

已自动注册到 `~/.claude/settings.json`：

```json
{
  "mcpServers": {
    "port-manage": {
      "command": "node",
      "args": ["/Users/panris/Projects/port-manage/mcp/dist/index.js"]
    }
  }
}
```

环境变量：
- `PORT_MANAGE_URL` — port-manage API 地址，默认 `http://localhost:9527`

## 手动测试

```bash
# 工具列表
echo '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2024-11-05","capabilities":{},"clientInfo":{"name":"test","version":"1.0"}}}
{"jsonrpc":"2.0","method":"notifications/initialized","params":{}}
{"jsonrpc":"2.0","id":2,"method":"tools/list","params":{}}' | node dist/index.js

# 调用健康检查
echo '{"jsonrpc":"2.0","id":1,"method":"initialize","params":{"protocolVersion":"2024-11-05","capabilities":{},"clientInfo":{"name":"test","version":"1.0"}}}
{"jsonrpc":"2.0","method":"notifications/initialized","params":{}}
{"jsonrpc":"2.0","id":2,"method":"tools/call","params":{"name":"health_check","arguments":{}}}' | node dist/index.js
```

## 重新编译

```bash
cd mcp
npm install
npm run build
```

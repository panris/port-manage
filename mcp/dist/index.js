import { Server } from "@modelcontextprotocol/sdk/server/index.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";
import { CallToolRequestSchema, ListToolsRequestSchema, } from "@modelcontextprotocol/sdk/types.js";
// ─── port-manage API endpoint ────────────────────────────────────────────────
const API = process.env.PORT_MANAGE_URL ?? "http://localhost:9527";
async function apiGet(path) {
    const res = await fetch(`${API}${path}`);
    if (!res.ok) {
        const text = await res.text();
        throw new Error(`port-manage API error ${res.status}: ${text}`);
    }
    return res.json();
}
// ─── Tool Definitions ──────────────────────────────────────────────────────────
const tools = [
    {
        name: "scan_ports",
        description: "扫描并返回当前机器上所有被占用的端口列表，以及对应的进程信息。用于排查端口冲突、了解系统资源占用情况。",
        inputSchema: {},
    },
    {
        name: "get_port",
        description: "查询指定端口的详细信息（端口号、协议、进程名、PID、状态）。",
        inputSchema: {
            type: "object",
            properties: {
                port: {
                    type: "number",
                    description: "要查询的端口号，如 8080、3000",
                },
            },
            required: ["port"],
        },
    },
    {
        name: "search_ports",
        description: "按关键词（端口号、进程名、PID）搜索端口列表。适用于只知道部分信息时的模糊查找。",
        inputSchema: {
            type: "object",
            properties: {
                q: {
                    type: "string",
                    description: "搜索关键词，可以是端口号、进程名或 PID",
                },
            },
            required: ["q"],
        },
    },
    {
        name: "kill_process",
        description: "根据 PID 关闭进程。慎用：会强制终止目标进程。",
        inputSchema: {
            type: "object",
            properties: {
                pid: {
                    type: "number",
                    description: "要终止的进程 PID",
                },
                permanent: {
                    type: "boolean",
                    description: "是否永久停止（阻止服务重启），默认 false",
                    default: false,
                },
            },
            required: ["pid"],
        },
    },
    {
        name: "batch_kill",
        description: "批量关闭多个进程。一次性传入 PID 列表，原子操作。",
        inputSchema: {
            type: "object",
            properties: {
                pids: {
                    type: "array",
                    items: { type: "number" },
                    description: "要终止的 PID 列表",
                },
                permanent: {
                    type: "boolean",
                    description: "是否永久停止，默认 false",
                    default: false,
                },
            },
            required: ["pids"],
        },
    },
    {
        name: "get_statistics",
        description: "获取端口扫描统计摘要（按状态、协议分类的数量）。",
        inputSchema: {},
    },
    {
        name: "get_system_info",
        description: "获取 port-manage 所在机器的操作系统信息。",
        inputSchema: {},
    },
    {
        name: "health_check",
        description: "健康检查：确认 port-manage 服务是否正常运行。",
        inputSchema: {},
    },
];
// ─── MCP Server ───────────────────────────────────────────────────────────────
const server = new Server({ name: "port-manage-mcp", version: "1.0.0" }, { capabilities: { tools: {} } });
// Declare tools
server.setRequestHandler(ListToolsRequestSchema, async () => ({
    tools: tools.map(({ name, description, inputSchema }) => ({
        name,
        description,
        inputSchema,
    })),
}));
// ─── Tool Handlers ────────────────────────────────────────────────────────────
server.setRequestHandler(CallToolRequestSchema, async (request) => {
    const { name, arguments: args } = request.params;
    try {
        switch (name) {
            case "scan_ports": {
                const data = await apiGet("/api/ports");
                if (!data.success)
                    throw new Error("扫描失败");
                const rows = data.data
                    .map((p) => `[${p.port}/${p.protocol}] ${p.status}  pid=${p.pid}  ${p.processName}${p.processPath ? " (" + p.processPath + ")" : ""}`)
                    .join("\n");
                return {
                    content: [
                        {
                            type: "text",
                            text: `共 ${data.count} 个端口\n\n${rows || "无结果"}`,
                        },
                    ],
                };
            }
            case "get_port": {
                const port = args?.port;
                const data = await apiGet(`/api/ports/${port}`);
                if (!data.success) {
                    return {
                        content: [{ type: "text", text: `端口 ${port} 未被占用或不存在` }],
                        isError: false,
                    };
                }
                const p = data.data;
                return {
                    content: [
                        {
                            type: "text",
                            text: `端口 ${p.port}/${p.protocol}  状态: ${p.status}\nPID: ${p.pid}\n进程: ${p.processName}\n路径: ${p.processPath ?? "未知"}`,
                        },
                    ],
                };
            }
            case "search_ports": {
                const q = args?.q;
                const data = await apiGet(`/api/ports/search?q=${encodeURIComponent(q)}`);
                const rows = data.data
                    .map((p) => `[${p.port}/${p.protocol}] ${p.status}  pid=${p.pid}  ${p.processName}`)
                    .join("\n");
                return {
                    content: [
                        {
                            type: "text",
                            text: `关键词 "${q}" 匹配 ${data.count} 个结果\n\n${rows || "无匹配"}`,
                        },
                    ],
                };
            }
            case "kill_process": {
                const pid = args?.pid;
                const permanent = args?.permanent ?? false;
                const res = await fetch(`${API}/api/process/${pid}?permanent=${permanent}`, {
                    method: "DELETE",
                });
                const data = await res.json();
                if (!data.success)
                    throw new Error(data.message);
                return {
                    content: [{ type: "text", text: `✅ PID ${pid} 已终止${permanent ? "（永久停止）" : ""}\n${data.message}` }],
                };
            }
            case "batch_kill": {
                const pids = args?.pids;
                const permanent = args?.permanent ?? false;
                const res = await fetch(`${API}/api/process/batch`, {
                    method: "DELETE",
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify({ pids, permanent }),
                });
                const data = await res.json();
                const lines = data.results
                    .map((r) => `${r.success ? "✅" : "❌"} PID ${r.pid}: ${r.message}`)
                    .join("\n");
                return {
                    content: [
                        {
                            type: "text",
                            text: `批量操作完成：✅ ${data.successCount}  ❌ ${data.failCount}\n${lines}`,
                        },
                    ],
                };
            }
            case "get_statistics": {
                const data = await apiGet("/api/statistics");
                const lines = Object.entries(data.data)
                    .map(([k, v]) => `  ${k}: ${v}`)
                    .join("\n");
                return {
                    content: [{ type: "text", text: `端口统计\n${lines}` }],
                };
            }
            case "get_system_info": {
                const data = await apiGet("/api/system");
                const lines = Object.entries(data.data)
                    .map(([k, v]) => `  ${k}: ${v}`)
                    .join("\n");
                return {
                    content: [{ type: "text", text: `系统信息\n${lines}` }],
                };
            }
            case "health_check": {
                const data = await apiGet("/api/health");
                const time = new Date(data.timestamp).toLocaleString("zh-CN");
                return {
                    content: [
                        {
                            type: "text",
                            text: `✅ port-manage 运行正常\n状态: ${data.status}\n时间: ${time}`,
                        },
                    ],
                };
            }
            default:
                return {
                    content: [{ type: "text", text: `未知工具: ${name}` }],
                    isError: true,
                };
        }
    }
    catch (err) {
        const msg = err instanceof Error ? err.message : String(err);
        return { content: [{ type: "text", text: `错误: ${msg}` }], isError: true };
    }
});
// ─── Start ────────────────────────────────────────────────────────────────────
const transport = new StdioServerTransport();
server.connect(transport).catch((err) => {
    console.error("MCP server error:", err);
    process.exit(1);
});

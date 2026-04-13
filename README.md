# Port Manager 🚀

<div align="center">

**功能强大的端口管理工具 | 支持 macOS & Windows**

[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Platform](https://img.shields.io/badge/platform-macOS%20%7C%20Windows-lightgrey.svg)](https://github.com/panris/port-manager)

[功能特性](#-功能特性) • [快速开始](#-快速开始) • [使用说明](#-使用说明) • [技术栈](#-技术栈) • [开发指南](#-开发指南)

</div>

---

## 📖 项目简介

Port Manager 是一个基于Web的端口管理工具，帮助开发者快速查看、管理本地端口占用情况。支持一键关闭进程、批量操作、永久停止系统服务等功能。

### 🎯 为什么选择 Port Manager？

- ✨ **零配置** - 双击启动脚本，无需任何设置
- 🚀 **全自动** - 自动检测环境、自动打开浏览器
- 🌍 **全中文** - 完整的中文界面和文档
- 🎨 **现代化UI** - 响应式设计，支持浅色/深色主题
- 🧠 **智能识别** - 自动识别进程类型（数据库、Web服务器、开发工具等）
- 💪 **功能强大** - 支持批量操作、永久停止系统服务
- 🔒 **安全可靠** - 明确的操作提示，支持优雅退出

---

## ✨ 功能特性

### 核心功能

| 功能 | 说明 |
|------|------|
| 📊 **实时监控** | 自动扫描所有端口占用情况，实时更新 |
| 🔍 **多维筛选** | 支持按端口类型、进程类型、协议、开发进程等多维度筛选 |
| ❌ **一键关闭** | 单击关闭任意进程，支持确认提示 |
| 📦 **批量操作** | 勾选多个端口，一次性批量关闭 |
| 🛑 **永久停止** | 智能识别系统服务，支持永久停止（不自动重启） |
| 🔄 **自动刷新** | 可配置自动刷新间隔，实时监控端口变化 |
| 🌓 **主题切换** | 支持浅色/深色主题，护眼舒适 |
| 🔎 **快速搜索** | 支持按端口号、进程名、PID搜索 |

### 智能识别

Port Manager 能够自动识别以下进程类型：

- **开发工具**: Node.js, Python, Java, Go, PHP等
- **数据库服务**: MySQL, Redis, PostgreSQL, MongoDB等
- **Web服务器**: Nginx, Apache, Tomcat等
- **IDE工具**: VSCode, IntelliJ IDEA, WebStorm等
- **浏览器**: Chrome, Firefox, Safari等
- **系统进程**: 自动标记系统管理的进程

### 筛选功能

- **端口类型**: 前端(3000-4999)、后端(8000-9999)、数据库(3306,5432,6379等)
- **进程类型**: Java、Node、Python、数据库、Web服务器等
- **协议类型**: TCP、UDP
- **开发进程**: 自动识别开发相关进程
- **常用端口**: 快速定位常见服务端口

---

## 🚀 快速开始

### 系统要求

- **Java**: 11 或更高版本
- **系统**: macOS 10.15+ 或 Windows 10+
- **端口**: 9527 端口可用

### 安装 Java（如果未安装）

#### macOS
```bash
brew install openjdk@11
```

#### Windows
访问 [Adoptium](https://adoptium.net/) 下载并安装

---

### 📥 下载与启动

#### 方式1: 下载发布包（推荐）

1. 从 [Releases](https://github.com/panris/port-manager/releases) 下载最新版本
2. 解压 `PortManager-v1.0.0-All.zip`
3. 根据系统选择启动方式：

**macOS 用户**
```bash
双击 PortManager.command
```

首次运行可能需要：
- 右键点击 → 选择"打开"
- 或在终端运行: `chmod +x PortManager.command && ./PortManager.command`

**Windows 用户**
```bash
双击 PortManager.bat
```

如有安全提示，点击"仍要运行"

4. ✨ 浏览器自动打开管理页面: http://localhost:9527

---

#### 方式2: 从源码运行

```bash
# 克隆项目
git clone https://github.com/panris/port-manager.git
cd port-manager/port-manager-web

# Maven构建
mvn clean package

# 启动应用
java -jar target/port-manager-web.jar
```

访问: http://localhost:9527

---

## 📱 使用说明

### 主界面功能

#### 1. 查看端口列表
启动后自动扫描并显示所有占用的端口，包括：
- 端口号
- 协议 (TCP/UDP)
- PID (进程ID)
- 进程名称
- 端口类型（前端/后端/数据库/其他）
- 进程类型（Java/Node/Python等）
- 是否为开发进程

#### 2. 筛选端口
使用顶部筛选栏快速定位：
```
端口类型 → 进程类型 → 协议 → 开发进程 → 常用端口
```

#### 3. 搜索功能
在搜索框输入：
- 端口号（如：3000）
- 进程名（如：mysql）
- PID（如：12345）

#### 4. 关闭单个进程
1. 找到要关闭的进程
2. 点击"关闭"按钮
3. 选择关闭方式：
   - **临时关闭**: 仅关闭进程（可能自动重启）
   - **永久停止**: 停止系统服务（不会重启，推荐用于数据库等）
4. 确认操作

#### 5. 批量关闭
1. 勾选要关闭的端口（可使用全选）
2. 点击"关闭选中 (N)"按钮
3. 确认端口列表
4. 选择关闭方式（临时/永久）
5. 确认批量关闭

---

## 🎯 使用场景

### 场景1: 清理开发环境
```
筛选"开发进程: 是" → 全选 → 批量关闭
```
一次性关闭所有开发服务器、构建工具

### 场景2: 停止数据库服务
```
筛选"进程类型: DATABASE" → 选择目标 → 永久停止
```
MySQL、Redis、PostgreSQL等一键永久停止

### 场景3: 释放被占用的端口
```
搜索端口号 → 查看占用进程 → 关闭
```
快速定位并释放需要的端口

### 场景4: 批量清理指定端口范围
```
手动勾选8000-9000范围端口 → 批量关闭
```
清理开发测试中的临时服务

---

## 🛠️ 技术栈

### 后端
- **框架**: Spring Boot 2.7.18
- **语言**: Java 11
- **构建工具**: Maven
- **端口扫描**: lsof (macOS) / netstat (Windows)
- **进程管理**: kill/brew services (macOS) / taskkill (Windows)

### 前端
- **语言**: 原生 JavaScript (ES6+)
- **样式**: CSS3 (CSS Custom Properties)
- **布局**: Flexbox + Grid
- **主题**: 支持浅色/深色模式

### 特性
- 响应式设计，支持移动端
- RESTful API 架构
- 自动刷新机制
- 错误处理和友好提示
- 跨平台支持

---

## 📚 API 文档

### 获取所有端口
```http
GET /api/ports
```

**响应示例**:
```json
{
  "success": true,
  "data": [
    {
      "port": 3306,
      "protocol": "tcp",
      "pid": 24883,
      "processName": "mysqld",
      "portType": "DATABASE",
      "processType": "DATABASE",
      "isDevelopmentProcess": false
    }
  ],
  "count": 1
}
```

### 关闭进程
```http
DELETE /api/process/{pid}?permanent=false
```

### 批量关闭
```http
DELETE /api/process/batch
Content-Type: application/json

{
  "pids": [24883, 21176],
  "permanent": true
}
```

### 触发扫描
```http
POST /api/scan
```

更多API详情请参考代码中的 `PortController.java`

---

## 🔧 开发指南

### 项目结构

```
port-manager-web/
├── src/
│   ├── main/
│   │   ├── java/com/portmanager/web/
│   │   │   ├── controller/         # REST API控制器
│   │   │   ├── service/            # 业务逻辑服务
│   │   │   ├── scanner/            # 端口扫描实现
│   │   │   ├── model/              # 数据模型
│   │   │   └── config/             # 配置类
│   │   └── resources/
│   │       ├── static/             # 前端资源
│   │       │   ├── index.html
│   │       │   ├── css/style.css
│   │       │   └── js/app.js
│   │       └── application.yml     # Spring Boot配置
├── release/                        # 发布包
├── scripts/                        # 启动脚本
├── docs/                          # 文档
└── pom.xml                        # Maven配置
```

### 本地开发

1. **克隆项目**
```bash
git clone https://github.com/panris/port-manager.git
cd port-manager/port-manager-web
```

2. **编译运行**
```bash
# 编译
mvn clean package

# 运行
java -jar target/port-manager-web.jar

# 或使用Maven插件
mvn spring-boot:run
```

3. **访问应用**
```
http://localhost:9527
```

### 修改端口

在 `application.yml` 中修改：
```yaml
server:
  port: 9527  # 改为你需要的端口
```

或通过命令行参数：
```bash
java -jar port-manager-web.jar --server.port=8080
```

---

## 📖 文档

项目包含完整的功能文档：

- [系统服务管理指南](SYSTEM_SERVICES.md) - launchd/Homebrew服务管理
- [永久停止功能说明](PERMANENT_STOP_GUIDE.md) - 如何永久停止系统服务
- [批量关闭功能指南](BATCH_KILL_GUIDE.md) - 批量操作详细说明

---

## 🤝 贡献指南

欢迎贡献代码、报告Bug、提出新功能建议！

### 如何贡献

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 提交 Pull Request

### 报告问题

如果你发现了Bug或有功能建议，请[创建Issue](https://github.com/panris/port-manager/issues)。

---

## 📋 待办事项

- [ ] 支持 Linux 系统
- [ ] 添加端口占用历史记录
- [ ] 支持导出端口列表
- [ ] 添加进程详细信息查看
- [ ] 支持自定义端口类型规则
- [ ] 添加键盘快捷键支持
- [ ] 国际化支持（英文界面）
- [ ] Docker镜像支持

---

## ❓ 常见问题

### Q1: 提示"未检测到Java环境"？
**A**: 请安装Java 11或更高版本。
- Mac: `brew install openjdk@11`
- Windows: 访问 https://adoptium.net/

### Q2: 端口9527被占用？
**A**: 启动脚本会提示是否关闭占用进程，或修改配置文件使用其他端口。

### Q3: 关闭进程后又重新出现？
**A**: 这是系统管理的服务（如MySQL、Redis）会自动重启。请使用"永久停止"选项。

### Q4: Mac提示"无法打开未验证的开发者"？
**A**: 右键点击启动脚本 → 选择"打开"，或在终端运行。

### Q5: Windows Defender 报毒？
**A**: 这是误报。启动脚本仅用于启动Java应用，无任何恶意代码。

更多问题请查看 [Issues](https://github.com/panris/port-manager/issues)。

---

## 📄 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件

---

## 👤 作者

**panris.pan**

- GitHub: [@panris](https://github.com/panris)

---

## 🌟 Star History

如果这个项目对你有帮助，请给它一个 ⭐️ Star！

---

## 📮 联系方式

- 提交 Issue: [GitHub Issues](https://github.com/panris/port-manager/issues)
- Pull Request: [GitHub Pull Requests](https://github.com/panris/port-manager/pulls)

---

<div align="center">

**[⬆ 回到顶部](#port-manager-)**

Made with ❤️ by [panris.pan](https://github.com/panris)

</div>

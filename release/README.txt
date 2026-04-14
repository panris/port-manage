# Port Manage - 端口管理工具

## 快速启动指南

### Mac系统 (macOS)

1. **双击运行**: `PortManager.command`
2. 如果提示"无法打开"，请执行以下操作：
   - 右键点击 `PortManager.command`
   - 选择"打开方式" → "终端"
   - 或在终端中运行: `chmod +x PortManager.command && ./PortManager.command`
3. 浏览器会自动打开管理页面: http://localhost:9527

### Windows系统

1. **双击运行**: `PortManager.bat`
2. 如果出现安全提示，点击"仍要运行"
3. 浏览器会自动打开管理页面: http://localhost:9527

---

## 系统要求

- **Java版本**: Java 11 或更高版本
- **端口要求**: 9527端口未被占用

### 如何安装Java

#### Mac系统
```bash
brew install openjdk@11
```

#### Windows系统
访问 https://adoptium.net/ 下载并安装

---

## 使用说明

### 启动应用

- **Mac**: 双击 `PortManager.command`
- **Windows**: 双击 `PortManager.bat`

启动后会自动：
1. 检测Java环境
2. 检查端口占用情况
3. 启动Port Manage应用
4. 打开浏览器访问管理页面

### 停止应用

- **Mac**: 在终端窗口中按 `Ctrl+C` 或直接关闭终端窗口
- **Windows**: 在命令窗口中按任意键或直接关闭窗口

### 访问管理页面

应用启动后访问: http://localhost:9527

---

## 功能特性

✅ 实时监控所有端口占用情况
✅ 支持多维度筛选（端口类型、进程类型、协议等）
✅ 一键关闭进程
✅ 支持永久停止系统服务
✅ 批量关闭多个端口
✅ 自动识别开发进程和数据库服务
✅ 支持浅色/深色主题

---

## 常见问题

### Q1: 提示"未检测到Java环境"？
**A**: 请先安装Java 11或更高版本。参考上方"如何安装Java"。

### Q2: 提示"端口9527已被占用"？
**A**: 启动脚本会提示是否关闭占用进程，输入 `y` 即可自动关闭。

### Q3: Mac系统提示"无法打开未验证的开发者"？
**A**:
- 方法1: 右键点击 → 选择"打开"
- 方法2: 终端运行 `chmod +x PortManager.command && ./PortManager.command`
- 方法3: 系统偏好设置 → 安全性与隐私 → 通用 → 点击"仍要打开"

### Q4: 浏览器没有自动打开？
**A**: 手动访问 http://localhost:9527

### Q5: 如何查看应用日志？
**A**: 日志会显示在启动的终端/命令窗口中。

---

## 文件说明

- `PortManager.command` - Mac启动脚本
- `PortManager.bat` - Windows启动脚本
- `port-manage-web.jar` - 应用程序主文件
- `README.txt` - 本说明文件

---

## 版本信息

- 版本: 1.0.0
- 构建日期: 2026-02-08
- 支持系统: macOS, Windows

---

## 技术支持

如有问题或建议，请查看项目文档或提交Issue。

应用地址: http://localhost:9527
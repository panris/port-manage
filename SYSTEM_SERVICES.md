# 系统管理服务说明

## 问题说明

当你尝试关闭某些进程（如 MySQL、Redis 等数据库服务）时，虽然页面显示"关闭成功"，但刷新后进程会重新出现。这是因为这些服务被系统服务管理器（如 macOS 的 launchd 或 Homebrew Services）自动管理和重启。

## 解决方案

### 1. 应用已优化的自动处理

最新版本的 Port Manage 已经优化了关闭进程的逻辑：

- **自动检测**：自动识别由 launchd 管理的服务
- **智能停止**：对于系统管理的服务，会尝试使用正确的命令停止（如 `brew services stop` 或 `launchctl stop`）
- **友好提示**：在关闭数据库、系统进程、Web服务器时会显示警告信息

### 2. 确认对话框增强

现在关闭进程时会看到不同的警告：

- **数据库服务**（MySQL、Redis、MongoDB等）：
  ```
  ⚠️ 警告：这是一个数据库服务，可能由系统服务管理器（如 launchd/Homebrew）自动启动。
  关闭后可能会自动重启。如需永久停止，请使用：
  brew services stop <service-name> 或 launchctl stop <service-name>
  ```

- **系统进程**：
  ```
  ⚠️ 警告：这是一个系统进程，关闭可能影响系统稳定性！
  ```

- **Web服务器**（nginx、apache等）：
  ```
  ⚠️ 提示：这是一个 Web 服务器，关闭可能影响网站访问。
  ```

### 3. 手动管理服务

如果需要永久停止服务，请使用以下命令：

#### Homebrew 管理的服务

```bash
# 查看所有 Homebrew 服务
brew services list

# 停止服务
brew services stop mysql
brew services stop redis
brew services stop postgresql

# 重启服务
brew services restart mysql
```

#### launchd 管理的服务

```bash
# 查看所有运行的 launchd 服务
launchctl list | grep -v "^-"

# 停止服务
launchctl stop homebrew.mxcl.mysql
launchctl stop homebrew.mxcl.redis

# 卸载服务（防止自动启动）
launchctl unload -w ~/Library/LaunchAgents/homebrew.mxcl.mysql.plist
```

## 进程类型说明

Port Manage 会自动识别以下进程类型：

| 类型 | 说明 | 示例 |
|------|------|------|
| JAVA | Java 应用 | Spring Boot、Tomcat |
| NODE | Node.js 应用 | npm、webpack、vite |
| PYTHON | Python 应用 | Django、Flask、FastAPI |
| WEB_SERVER | Web 服务器 | nginx、apache |
| DATABASE | 数据库服务 | MySQL、Redis、MongoDB |
| IDE | 开发工具 | IntelliJ IDEA、VSCode |
| BROWSER | 浏览器 | Chrome、Firefox |
| SYSTEM | 系统进程 | systemd、sshd |
| OTHER | 其他进程 | - |

## 最佳实践

1. **开发环境服务**：可以通过 Port Manage 关闭
2. **系统管理服务**：建议使用命令行工具管理（brew services、launchctl）
3. **重要服务**：关闭前确认是否会影响其他应用

## 技术实现

Port Manage 现在会：

1. 检查进程是否由 launchd 管理
2. 如果是，尝试使用 `brew services stop` 停止 Homebrew 服务
3. 如果不是 Homebrew 服务，尝试使用 `launchctl stop` 停止
4. 如果都失败，回退到 `kill -9` 强制关闭
5. 记录详细的操作日志供调试

查看日志：
```bash
tail -f app.log | grep -i "kill\|launchd\|service"
```

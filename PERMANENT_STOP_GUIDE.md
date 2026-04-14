# 永久停止服务功能使用指南

## 新功能说明

现在，当你关闭数据库、系统进程或 Web 服务器时，会出现一个选项对话框，让你选择关闭方式：

### 两种关闭方式

#### 1. 临时关闭 (Temporary)
- 仅关闭进程 (`kill -9`)
- 如果服务由系统管理器（launchd/Homebrew）管理，**可能会自动重启**
- 适用于快速测试或临时释放端口

#### 2. 永久停止 (Permanent) ⭐ **推荐**
- 使用系统服务命令停止服务 (`brew services stop` 或 `launchctl stop`)
- **不会自动重启**，直到你手动启动
- 适用于真正想停止服务的场景

## 使用步骤

### 方式一：通过 Port Manage 界面（推荐）

1. 访问 http://localhost:9527
2. 找到要停止的服务（如 MySQL、Redis）
3. 点击"关闭"按钮
4. 在弹出的对话框中会看到：
   ```
   确定要关闭进程 "redis-ser" (PID: 21176, Port: 6379) 吗？
   
   ⚠️ 警告：这是一个数据库服务，可能由系统服务管理器（如 launchd/Homebrew）自动启动。
   
   ○ 临时关闭
     仅关闭进程，服务可能会自动重启
   
   ● 永久停止（推荐）
     停止系统服务，不会自动重启
   ```
5. 选择"**永久停止**"（默认已选中）
6. 点击"确定"
7. 等待几秒，服务将被永久停止

### 方式二：通过命令行

如果需要更多控制，可以直接使用命令行：

#### Homebrew 管理的服务

```bash
# 查看所有服务状态
brew services list

# 永久停止 MySQL
brew services stop mysql

# 永久停止 Redis
brew services stop redis

# 永久停止 PostgreSQL
brew services stop postgresql

# 重新启动服务
brew services start mysql
brew services restart redis
```

#### launchd 管理的服务

```bash
# 查看所有运行的服务
launchctl list | grep -v "^-"

# 停止服务
launchctl stop homebrew.mxcl.mysql

# 卸载服务（防止开机自启）
launchctl unload ~/Library/LaunchAgents/homebrew.mxcl.mysql.plist
```

## 技术实现

### 后端逻辑

当选择"永久停止"时，系统会：

1. 检测进程是否由 launchd 管理
2. 提取服务名称（如 `homebrew.mxcl.redis`）
3. 尝试使用 `brew services stop <name>` 停止 Homebrew 服务
4. 如果不是 Homebrew 服务，使用 `launchctl stop <name>`
5. 记录详细日志

### API 端点

```
DELETE /api/process/{pid}?permanent=true
```

参数：
- `pid`: 进程ID
- `permanent`: true=永久停止，false=临时关闭（默认）

## 验证服务已停止

### 方法1：通过 Port Manage

刷新页面，检查端口是否消失

### 方法2：通过命令行

```bash
# 检查 Homebrew 服务状态
brew services list

# 检查进程是否还在运行
ps aux | grep redis
ps aux | grep mysql

# 检查端口是否还在监听
lsof -i :6379  # Redis
lsof -i :3306  # MySQL
```

## 常见问题

### Q1: 选择"永久停止"后服务还会重启吗？

**A**: 不会。永久停止会使用系统服务管理命令，服务不会自动重启，除非你手动启动或系统重启后自动启动（取决于服务配置）。

### Q2: 如何重新启动已停止的服务？

**A**: 使用以下命令：
```bash
brew services start mysql
brew services start redis
```

### Q3: 什么时候应该选择"临时关闭"？

**A**: 
- 快速测试时想释放端口
- 知道服务会自动重启且这是你想要的
- 不是系统管理的服务（普通用户进程）

### Q4: 什么时候应该选择"永久停止"？

**A**: 
- 真正想停止服务，不希望自动重启
- 数据库服务（MySQL、Redis、MongoDB等）
- Web 服务器（nginx、apache等）
- 系统服务

### Q5: 为什么我的服务没有显示选项？

**A**: 只有系统管理的服务（DATABASE、SYSTEM、WEB_SERVER 类型）才会显示选择选项。普通进程会直接关闭。

## 日志查看

查看操作日志：

```bash
# 实时查看关闭进程的日志
tail -f app.log | grep -i "kill\|service\|launchd"

# 查看最近的操作
tail -100 app.log | grep "ProcessManageService"
```

## 示例场景

### 场景1：停止开发环境的 Redis

```
1. 打开 Port Manage (http://localhost:9527)
2. 找到 Redis (端口 6379)
3. 点击"关闭"
4. 选择"永久停止"（默认）
5. 点击"确定"
6. 2秒后刷新页面，Redis 已消失

验证：
$ brew services list
Name  Status  User   File
redis stopped panris ...
```

### 场景2：临时关闭开发服务器

```
1. 找到你的 Node.js 开发服务器（端口 3000）
2. 点击"关闭"
3. 对于普通进程，会直接关闭（没有选项对话框）
4. 或者如果有选项，选择"临时关闭"即可
```

## 支持的服务类型

- ✅ **DATABASE**: MySQL, PostgreSQL, Redis, MongoDB
- ✅ **WEB_SERVER**: nginx, apache, httpd
- ✅ **SYSTEM**: systemd, sshd（谨慎操作）
- ⚪ **其他类型**: JAVA, NODE, PYTHON, IDE, BROWSER 等使用标准关闭方式

## 注意事项

1. ⚠️ 停止系统关键服务可能影响系统稳定性
2. ⚠️ 停止数据库服务前确保没有重要应用依赖
3. ✅ 建议对数据库和 Web 服务器使用"永久停止"
4. ✅ 操作会记录在日志中，便于追踪

---

**应用地址**: http://localhost:9527  
**日志文件**: app.log

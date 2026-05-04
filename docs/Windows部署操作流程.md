# Windows 部署操作流程

本文档给 Windows 电脑上的同事使用，目标是从拿到仓库文件开始，把跃鹿运动健身指导系统在本机跑起来，并完成基础验证。

## 1. 准备环境

建议使用 Windows 10/11。先安装以下软件：

- Git for Windows：https://git-scm.com/download/win
- JDK 17 或更高版本：https://adoptium.net/temurin/releases/
- Node.js 18 或更高版本：https://nodejs.org/
- 推荐编辑器：Visual Studio Code

安装后新开一个 PowerShell，确认版本：

```powershell
git --version
java -version
node -v
npm -v
```

如果 `java -version` 不是 17 或以上，需要检查 `JAVA_HOME` 和系统 `Path`。如果刚装完软件命令不生效，先关闭 PowerShell 重新打开。

## 2. 获取项目文件

如果是从 GitHub 拉取：

```powershell
cd D:\workspace
git clone https://github.com/powerpan/xiaolanshu.git
cd xiaolanshu
```

如果是拿到压缩包：

1. 解压到一个固定目录，例如 `D:\workspace\xiaolanshu`。
2. 目录路径尽量不要包含中文、空格或特殊符号。
3. 打开 PowerShell 进入项目根目录：

```powershell
cd D:\workspace\xiaolanshu
```

项目根目录应该能看到：

```text
fitnessGuidance
vue-project
docs
README.md
```

## 3. 首次本地运行

本项目是前后端分离项目，需要开两个 PowerShell 窗口：一个跑后端，一个跑前端。

### 3.1 启动后端

在第一个 PowerShell 中执行：

```powershell
cd D:\workspace\xiaolanshu\fitnessGuidance
$env:MAVEN_USER_HOME=".m2"
.\mvnw.cmd spring-boot:run
```

第一次启动会下载 Maven 依赖，时间可能较长。看到类似下面的信息表示后端启动成功：

```text
Tomcat started on port 8080
Started FitnessGuidanceApplication
```

后端默认地址：

```text
http://127.0.0.1:8080
```

后端启动后会自动创建本地 H2 数据库文件：

```text
fitnessGuidance\data
```

### 3.2 启动前端

在第二个 PowerShell 中执行：

```powershell
cd D:\workspace\xiaolanshu\vue-project
npm install
npm run dev
```

看到类似下面的信息表示前端启动成功：

```text
Local: http://localhost:5173/
```

浏览器访问：

```text
http://127.0.0.1:5173
```

如果 `5173` 被占用，Vite 可能会提示另一个端口，例如 `5174`，按终端显示的地址访问即可。

## 4. 登录验证

默认本地开发账号：

```text
管理员：admin / admin123
普通用户：demo / demo123
```

建议先用普通用户登录，确认以下页面可用：

- 首页
- 训练计划
- 动作指导
- 训练打卡
- 训练复盘
- 饮食建议
- 公告中心
- 文章广场

再用管理员登录，确认以下页面可用：

- 内容管理
- 动作库管理
- 饮食模板
- 用户管理

页面左侧菜单不应出现横向或纵向滚动条；左下角音乐光碟可以拖动，点击后播放或暂停背景音乐。

## 5. 停止服务

两个 PowerShell 窗口分别按：

```text
Ctrl + C
```

如果端口没有释放，可以查看并结束进程。

查看 8080 端口：

```powershell
netstat -ano | findstr :8080
```

查看 5173 端口：

```powershell
netstat -ano | findstr :5173
```

结束指定 PID：

```powershell
taskkill /PID <PID> /F
```

把 `<PID>` 替换成 `netstat` 输出最后一列的进程号。

## 6. 重置本地数据

如果想恢复初始化账号、公告、动作库和饮食模板：

1. 先停止后端服务。
2. 删除目录：

```text
D:\workspace\xiaolanshu\fitnessGuidance\data
```

3. 重新启动后端：

```powershell
cd D:\workspace\xiaolanshu\fitnessGuidance
$env:MAVEN_USER_HOME=".m2"
.\mvnw.cmd spring-boot:run
```

后端会重新根据 `schema.sql` 和 `data.sql` 初始化 H2 数据库。

## 7. 构建检查

前端构建：

```powershell
cd D:\workspace\xiaolanshu\vue-project
npm run build
```

成功后会生成：

```text
vue-project\dist
```

后端测试：

```powershell
cd D:\workspace\xiaolanshu\fitnessGuidance
$env:MAVEN_USER_HOME=".m2"
.\mvnw.cmd test
```

后端打包：

```powershell
cd D:\workspace\xiaolanshu\fitnessGuidance
$env:MAVEN_USER_HOME=".m2"
.\mvnw.cmd -DskipTests package
```

成功后会生成：

```text
fitnessGuidance\target\fitnessGuidance-0.0.1-SNAPSHOT.jar
```

可以用下面命令运行 jar：

```powershell
cd D:\workspace\xiaolanshu\fitnessGuidance
java -jar target\fitnessGuidance-0.0.1-SNAPSHOT.jar
```

## 8. 常见问题

### 端口 8080 被占用

后端启动失败并提示端口占用时，执行：

```powershell
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

也可以临时换端口：

```powershell
cd D:\workspace\xiaolanshu\fitnessGuidance
$env:MAVEN_USER_HOME=".m2"
.\mvnw.cmd spring-boot:run "-Dspring-boot.run.arguments=--server.port=8081"
```

如果后端端口改成 8081，前端接口地址也要同步修改 `vue-project\src\services\api.js` 里的 `baseURL`。

### npm install 很慢或失败

可以先设置 npm 镜像：

```powershell
npm config set registry https://registry.npmmirror.com
npm install
```

如果依赖装乱了，可以删除 `vue-project\node_modules` 和 `vue-project\package-lock.json` 后重新执行 `npm install`。正常协作时不要提交 `node_modules`。

### mvnw.cmd 下载依赖失败

确认网络可以访问 Maven 仓库。也可以删除后端局部缓存后重试：

```powershell
cd D:\workspace\xiaolanshu\fitnessGuidance
rmdir /s /q .m2
$env:MAVEN_USER_HOME=".m2"
.\mvnw.cmd spring-boot:run
```

### 登录后页面请求失败

确认后端还在运行，并且前端接口地址指向：

```text
http://localhost:8080
```

配置位置：

```text
vue-project\src\services\api.js
```

### 页面没有最新初始化数据

通常是旧 H2 文件库还在。停止后端，删除 `fitnessGuidance\data`，再重新启动后端。

### 背景音乐无法自动播放

浏览器默认禁止网页自动播放音频。需要用户手动点击左下角音乐光碟后才能播放。

## 9. 交付检查清单

交给同事前建议确认：

- 仓库不包含 `node_modules`、`dist`、`target`、`.m2`、`fitnessGuidance\data`。
- `vue-project\src\assets\yuelu-logo.png` 存在。
- `vue-project\src\assets\橙色制高点.mp3` 存在。
- `fitnessGuidance\uploads\exercise-guides\.gitkeep` 存在。
- `npm run build` 能通过。
- `.\mvnw.cmd test` 能通过，或已说明当前无法执行的原因。

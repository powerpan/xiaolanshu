# 小蓝书健身指导系统

这是一个前后端分离的健身指导项目。前端使用 Vue 3 + Vite + Element Plus，后端使用 Spring Boot + MyBatis，数据库使用项目内本地 H2 文件库。

## 环境要求

需要在本机安装：

- Java 17 或更高版本
- Node.js 18 或更高版本
- npm
- Git

项目不会把 `node_modules`、Maven 依赖缓存、构建产物和本地数据库文件提交到仓库。对方拉取仓库后，第一次启动时会在本机或项目目录内自动补齐这些内容。

## 拉取项目

```sh
git clone https://github.com/powerpan/xiaolanshu.git
cd xiaolanshu
```

## 启动后端

进入后端目录：

```sh
cd fitnessGuidance
```

首次运行或开发运行：

```sh
MAVEN_USER_HOME=.m2 ./mvnw spring-boot:run
```

如果 macOS/Linux 提示 `mvnw` 没有执行权限，先执行：

```sh
chmod +x mvnw
```

Windows 可以使用：

```bat
set MAVEN_USER_HOME=.m2
mvnw.cmd spring-boot:run
```

后端默认地址：

```text
http://127.0.0.1:8080
```

## 启动前端

另开一个终端，从项目根目录进入前端目录：

```sh
cd vue-project
npm install
npm run dev
```

前端默认地址：

```text
http://127.0.0.1:5173
```

浏览器访问前端地址即可使用系统。

## 默认账号

本地演示数据库会初始化以下账号：

```text
管理员：admin / admin123
普通用户：demo / demo123
```

管理员可以看到内容管理、用户管理等页面；普通用户主要使用训练计划、动作指导、饮食建议、打卡和内容阅读功能。

## 本地数据库

项目当前使用 H2 文件数据库，后端启动后会在下面目录生成本地数据文件：

```text
fitnessGuidance/data
```

数据库结构和初始化演示数据在：

```text
fitnessGuidance/src/main/resources/schema.sql
fitnessGuidance/src/main/resources/data.sql
```

如果需要重置本地演示数据，可以先停止后端服务，再删除 `fitnessGuidance/data` 目录，然后重新启动后端。

## 本地动作图片

动作指导页使用的图片已经放在项目本地：

```text
vue-project/public/exercise-guides
```

页面加载图片时走 `/exercise-guides/...` 本地静态路径，不需要每次联网加载。图片来源署名和原始来源链接仍保留在动作指导页面中。

## 常用命令

后端打包：

```sh
cd fitnessGuidance
MAVEN_USER_HOME=.m2 ./mvnw -DskipTests package
```

前端构建：

```sh
cd vue-project
npm run build
```

前端构建产物会生成在：

```text
vue-project/dist
```

后端 jar 会生成在：

```text
fitnessGuidance/target
```

## 目录说明

```text
fitnessGuidance/        后端 Spring Boot 项目
vue-project/            前端 Vue 项目
vue-project/public/     前端静态资源
fitnessGuidance/data/   本地 H2 数据库文件，运行后生成
fitnessGuidance/.m2/    项目内 Maven 依赖缓存，运行后生成
vue-project/node_modules/ 前端依赖，npm install 后生成
```

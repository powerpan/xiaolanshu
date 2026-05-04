# 小蓝书健身指导系统

这是一个前后端分离的健身指导项目。前端使用 Vue 3 + Vite + Element Plus，后端使用 Spring Boot + MyBatis，数据库使用项目内本地 H2 文件库。

## 项目文档

- [产品化升级计划](docs/产品化升级计划.md)：记录后续功能升级、前端重构和产品体验完善方向。
- [功能验收清单](docs/验收清单.md)：记录普通用户端、管理端和基础回归检查路径。
- [打卡统计与饮食推荐数据表结构](打卡统计与饮食推荐数据表结构.md)：说明当前打卡统计和饮食推荐相关数据来源。

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

本地开发数据库会初始化以下账号：

```text
管理员：admin / admin123
普通用户：demo / demo
```

管理员可以看到内容管理、动作库管理和用户管理页面；普通用户主要使用训练计划、动作指导、饮食建议、打卡和内容阅读功能。

## 功能边界

普通用户端聚焦训练执行和内容阅读：

- 首页、个人资料、健身需求、训练计划、动作指导、训练打卡、训练复盘、饮食建议、公告中心、文章广场和详情阅读。

管理端聚焦维护和审核：

- 内容管理、公告编辑、文章编辑、动作库管理、动作图片上传、用户管理和注册审核。

管理员账号不会显示普通用户训练入口，普通用户也不会看到公告、文章和动作库的编辑入口。

当前阶段三已继续补齐核心产品能力：

- 训练计划动作卡片会展示推荐理由、强度提示、进阶建议、肌群、常见错误、禁忌和替代动作。
- 训练计划支持保存当天动作完成记录，并根据本周完成率、连续打卡和记录情况给出调整建议。
- 动作库支持维护主肌群、辅助肌群、难度、禁忌、常见错误、适合人群和替代动作，并支持筛选缺图片、缺步骤、缺要点、缺常见错误和整体不完整动作。
- 饮食建议支持保存用户饮食偏好，并按训练日、休息日、食材替换和采购清单输出更细的建议。
- 饮食推荐会保存最近生成历史，用户可以回看热量、三大营养素和当时推荐摘要。

## JWT 密钥配置

后端会优先读取环境变量 `XIAOLANSHU_JWT_SECRET` 作为登录 token 签名密钥。本地开发不配置也能启动，但正式部署环境建议显式设置一个随机长字符串。

macOS/Linux 示例：

```sh
export XIAOLANSHU_JWT_SECRET="replace-with-a-long-random-secret"
cd fitnessGuidance
MAVEN_USER_HOME=.m2 ./mvnw spring-boot:run
```

也可以通过 Java 系统参数传入：

```sh
MAVEN_USER_HOME=.m2 ./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-Dxiaolanshu.jwt.secret=replace-with-a-long-random-secret"
```

## 本地数据库

项目当前使用 H2 文件数据库，后端启动后会在下面目录生成本地数据文件：

```text
fitnessGuidance/data
```

数据库结构和初始化数据在：

```text
fitnessGuidance/src/main/resources/schema.sql
fitnessGuidance/src/main/resources/data.sql
```

阶段三新增的核心数据：

- `exerciseguides`：新增肌群、难度、常见错误、禁忌、适合人群和替代动作字段。
- `plantaskrecords`：保存用户每日训练计划动作完成记录，用于计算训练计划完成率和调整建议。
- `nutritionpreferences`：保存用户饮食类型、忌口、预算、外食频率、每日餐次和口味偏好。
- `nutritionrecommendationhistories`：保存每次生成的饮食推荐摘要和核心营养目标，方便回看。

如果需要重置本地开发数据，可以先停止后端服务，再删除 `fitnessGuidance/data` 目录，然后重新启动后端。

## 切换到 openGauss

仓库内额外提供了一份 openGauss 初始化脚本：

```text
fitnessGuidance/database/opengauss-init.sql
```

这份脚本适合导入到新的 openGauss 数据库，里面会创建项目所需表结构并写入初始化数据。注意：脚本开头会删除同名表，导入前请确认目标库可以重置。

### 1. 创建数据库并导入脚本

先在 openGauss 中创建一个数据库，例如：

```sql
CREATE DATABASE xiaolanshu;
```

然后导入初始化脚本。命令示例：

```sh
gsql -h 127.0.0.1 -p 5432 -U gaussdb -d xiaolanshu -f fitnessGuidance/database/opengauss-init.sql
```

如果对方使用的是远程 openGauss，把 `127.0.0.1`、`5432`、`gaussdb`、`xiaolanshu` 替换成实际连接信息即可。

### 2. 修改 openGauss 连接配置

openGauss 配置文件在：

```text
fitnessGuidance/src/main/resources/application-opengauss.properties
```

把里面的数据库地址、用户名、密码改成对方环境：

```properties
spring.datasource.url=jdbc:opengauss://127.0.0.1:5432/xiaolanshu
spring.datasource.username=gaussdb
spring.datasource.password=gaussdb
spring.datasource.driver-class-name=org.opengauss.Driver
spring.sql.init.mode=never
```

`spring.sql.init.mode=never` 表示后端启动时不再自动执行 H2 的 `schema.sql` / `data.sql`，openGauss 数据由上面的导入脚本负责。

### 3. 使用 openGauss 配置启动后端

进入后端目录：

```sh
cd fitnessGuidance
```

使用 openGauss profile 启动：

```sh
MAVEN_USER_HOME=.m2 ./mvnw spring-boot:run -Dspring-boot.run.profiles=opengauss
```

如果使用已经打好的 jar：

```sh
java -jar target/fitnessGuidance-0.0.1-SNAPSHOT.jar --spring.profiles.active=opengauss
```

前端启动方式不变，仍然进入 `vue-project` 执行：

```sh
npm install
npm run dev
```

### 4. 切回默认 H2

如果不指定 `opengauss` profile，项目会继续使用默认 H2 文件数据库：

```sh
cd fitnessGuidance
MAVEN_USER_HOME=.m2 ./mvnw spring-boot:run
```

## 动作图片上传

动作指导页支持展示本地图片，但图片不再由前端内置，也不再使用自动绘制的 SVG。当前图片由管理员在动作库管理中手动上传，后端会保存到：

```text
fitnessGuidance/uploads/exercise-guides
```

页面加载图片时走后端静态路径，不需要每次联网加载。仓库只提交上传目录占位文件 `.gitkeep`，不会提交运行时上传的图片文件。

动作指导库支持筛选“只看未自定义图片”，方便管理员逐步补齐动作图片。没有图片的动作会显示占位，不会在训练计划中强行展示图片。

动作指导数据同时维护在两份初始化脚本里：

```text
fitnessGuidance/src/main/resources/data.sql
fitnessGuidance/database/opengauss-init.sql
```

默认 H2 开发库会读取 `data.sql`；openGauss 环境需要重新导入 `opengauss-init.sql`。如果修改动作指导、图片路径或图片来源，需要同步更新这两份文件，避免本地开发和 openGauss 部署内容不一致。

如果本地页面没有看到新图片，通常是后端正在使用旧的 H2 文件库。停止后端后删除下面目录，再重新启动后端即可重新初始化数据：

```text
fitnessGuidance/data
```

## 验证项目

后端测试：

```sh
cd fitnessGuidance
MAVEN_USER_HOME=.m2 ./mvnw test
```

前端构建：

```sh
cd vue-project
npm run build
```

人工验收路径见：

```text
docs/验收清单.md
```

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
vue-project/src/        前端源码
fitnessGuidance/uploads/ 后端运行期上传目录，只提交占位文件
fitnessGuidance/data/   本地 H2 数据库文件，运行后生成
fitnessGuidance/.m2/    项目内 Maven 依赖缓存，运行后生成
vue-project/node_modules/ 前端依赖，npm install 后生成
```

# 用户管理系统 - Vue3 + Spring Boot + MyBatis Plus

## 项目简介

一个功能完整的用户管理系统，包含用户注册、登录、密码管理、用户CRUD等核心功能。采用前后端分离架构，后端使用MyBatis Plus作为ORM框架。

## 项目结构

```
demo-codearts/
├── backend/                # Spring Boot 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/demo/
│   │   │   │   ├── controller/    # 控制器
│   │   │   │   ├── service/       # 服务层
│   │   │   │   ├── mapper/        # MyBatis Plus Mapper
│   │   │   │   ├── entity/        # 实体类
│   │   │   │   ├── dto/           # 数据传输对象
│   │   │   │   └── config/        # 配置类
│   │   │   └── resources/
│   │   │       └── application.yml  # 配置文件
│   │   └── test/
│   └── pom.xml              # Maven 配置
├── frontend/               # Vue3 前端项目
│   ├── src/
│   │   ├── views/          # 页面组件
│   │   ├── api/            # API 请求
│   │   ├── router/         # 路由配置
│   │   ├── App.vue         # 根组件
│   │   └── main.js         # 入口文件
│   ├── index.html
│   ├── vite.config.js      # Vite 配置
│   └── package.json
└── database/               # 数据库脚本
    └── init.sql            # 初始化脚本
```

## 技术栈

### 后端
- Spring Boot 3.2.0
- MyBatis Plus 3.5.5
- MySQL 8.0
- Lombok

### 前端
- Vue 3.4
- Vue Router 4
- Element Plus (UI 组件库)
- Axios (HTTP 客户端)
- Vite 5 (构建工具)

## 核心功能

### 用户认证
- 用户注册（账户唯一性校验）
- 用户登录（登录日志记录）
- 密码更新（强制更新机制）
- 路由守卫（权限控制）

### 密码管理
- 默认密码配置（!qazXSW@）
- 密码过期检查（6个月）
- 强制密码更新
- 重置密码功能

### 用户管理
- 用户列表查看
- 添加新用户
- 编辑用户信息
- 删除用户
- 重置用户密码

## 快速开始

### 1. 数据库配置

1. 安装 MySQL 8.0
2. 创建数据库：
```sql
CREATE DATABASE demo_db;
```

3. 修改后端配置文件 `backend/src/main/resources/application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/demo_db?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: your_password  # 修改为你的 MySQL 密码
```

### 2. 启动后端

```bash
cd backend
mvn spring-boot:run
```

后端服务将在 http://localhost:8080 启动

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端服务将在 http://localhost:5173 启动

### 4. 测试账户

- 账户：admin
- 密码：admin

首次登录会强制要求修改密码。

## API 接口

### 认证接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/login | 用户登录 |
| POST | /api/auth/register | 用户注册 |
| POST | /api/auth/update-password | 更新密码 |
| GET | /api/auth/check-account | 检查账户是否存在 |

### 用户管理接口
| 方法 | 路径 | 说明 |
|------|------|------|
| GET  | /api/users | 获取所有用户 |
| GET  | /api/users/{id} | 根据 ID 获取用户 |
| POST | /api/users | 创建用户 |
| PUT  | /api/users/{id} | 更新用户 |
| DELETE | /api/users/{id} | 删除用户 |
| POST | /api/users/{id}/reset-password | 重置用户密码 |

## 项目特性

### 安全特性
- 默认密码强制更新
- 密码过期自动提醒
- 登录日志完整记录
- 路由权限控制

### 技术亮点
- MyBatis Plus Lambda查询
- 自动填充时间字段
- CORS跨域配置
- 内网穿透支持

## 开发说明

### 后端开发
1. 实体类放在 `entity` 包（使用MyBatis Plus注解）
2. Mapper接口放在 `mapper` 包（继承BaseMapper）
3. Service类放在 `service` 包
4. Controller类放在 `controller` 包

### 前端开发
1. 页面组件放在 `views` 目录
2. API请求放在 `api` 目录
3. 路由配置在 `router/index.js`
4. 使用Element Plus组件库

## 配置说明

### 安全配置
```yaml
app:
  security:
    default-password: "!qazXSW@"  # 默认密码
    password-expire-months: 6      # 密码过期月数
```

### MyBatis Plus配置
```yaml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true  # 驼峰转换
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl  # SQL日志
  global-config:
    db-config:
      id-type: auto  # 主键自增
```

## 注意事项

1. 确保MySQL服务已启动
2. 确保数据库连接配置正确
3. 后端默认端口: 8080
4. 前端默认端口: 5173
5. 已配置CORS跨域支持
6. 支持内网穿透访问

## 版本历史

- v1.0.7: 用户注册功能
- v1.0.6: 重置密码功能
- v1.0.5: 密码管理与安全增强
- v1.0.4: UI优化与路由守卫
- v1.0.3: 内网穿透配置
- v1.0.2: 登录功能实现
- v1.0.1: 项目初始化
- v1.0.0: 项目创建

## License

MIT

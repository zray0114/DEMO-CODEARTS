# Vue3 + Spring Boot + MySQL 全栈项目

## 项目结构

```
demo-codearts/
├── backend/                # Spring Boot 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/demo/
│   │   │   │   ├── controller/    # 控制器
│   │   │   │   ├── service/       # 服务层
│   │   │   │   ├── repository/    # 数据访问层
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
│   │   ├── components/     # 通用组件
│   │   ├── api/            # API 请求
│   │   ├── router/         # 路由配置
│   │   ├── stores/         # Pinia 状态管理
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
- Spring Data JPA
- MySQL 8.0
- Lombok

### 前端
- Vue 3.4
- Vue Router 4
- Pinia (状态管理)
- Element Plus (UI 组件库)
- Axios (HTTP 客户端)
- Vite (构建工具)

## 快速开始

### 1. 数据库配置

1. 安装 MySQL 8.0
2. 执行数据库初始化脚本:
```bash
mysql -u root -p < database/init.sql
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

## 功能说明

### 用户管理
- 查看用户列表
- 添加新用户
- 编辑用户信息
- 删除用户

## API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET  | /api/users | 获取所有用户 |
| GET  | /api/users/{id} | 根据 ID 获取用户 |
| POST | /api/users | 创建用户 |
| PUT  | /api/users/{id} | 更新用户 |
| DELETE | /api/users/{id} | 删除用户 |

## 开发说明

### 后端开发
1. 实体类放在 `entity` 包
2. Repository 接口放在 `repository` 包
3. Service 类放在 `service` 包
4. Controller 类放在 `controller` 包

### 前端开发
1. 页面组件放在 `views` 目录
2. 通用组件放在 `components` 目录
3. API 请求放在 `api` 目录
4. 路由配置在 `router/index.js`

## 注意事项

1. 确保 MySQL 服务已启动
2. 确保数据库连接配置正确
3. 后端默认端口: 8080
4. 前端默认端口: 5173
5. 已配置 CORS 跨域支持

## 扩展建议

1. 添加登录认证功能
2. 添加权限管理
3. 添加更多业务模块
4. 优化前端 UI
5. 添加单元测试

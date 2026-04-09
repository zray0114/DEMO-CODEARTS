# 项目开发日志

## 项目基础信息

### 项目名称
demo-codearts - 全栈用户管理系统

### 技术栈

#### 后端
- **框架**: Spring Boot 3.2.0
- **语言**: Java 17
- **ORM**: Spring Data JPA (Hibernate 6.3.1)
- **数据库**: MySQL 8.0
- **构建工具**: Maven
- **端口**: 8080
- **主要依赖**:
  - Lombok (简化代码)
  - Spring Boot DevTools (开发工具)
  - Validation (数据验证)

#### 前端
- **框架**: Vue 3.4.0
- **路由**: Vue Router 4.2.5
- **状态管理**: Pinia 2.1.7
- **UI组件库**: Element Plus 2.4.4
- **HTTP客户端**: Axios 1.6.2
- **构建工具**: Vite 5.0.0
- **端口**: 5173

#### 数据库
- **MySQL 8.0**
- **数据库名**: demo_db
- **字符集**: utf8mb4

### 项目结构

```
demo-codearts/
├── backend/                    # 后端项目
│   ├── src/main/java/com/example/demo/
│   │   ├── DemoApplication.java        # 主启动类
│   │   ├── config/                     # 配置类
│   │   │   └── CorsConfig.java        # CORS跨域配置
│   │   ├── controller/                 # 控制器层
│   │   │   ├── UserController.java    # 用户管理控制器
│   │   │   └── AuthController.java    # 登录认证控制器
│   │   ├── entity/                     # 实体类
│   │   │   ├── User.java              # 用户实体
│   │   │   └── LoginLog.java          # 登录日志实体
│   │   ├── repository/                 # 数据访问层
│   │   │   ├── UserRepository.java
│   │   │   └── LoginLogRepository.java
│   │   ├── service/                    # 服务层
│   │   │   ├── UserService.java
│   │   │   └── AuthService.java       # 登录认证服务
│   │   └── dto/                        # 数据传输对象
│   │       ├── LoginRequest.java
│   │       └── LoginResponse.java
│   └── src/main/resources/
│       └── application.yml            # 应用配置
├── frontend/                   # 前端项目
│   ├── src/
│   │   ├── main.js                     # 入口文件
│   │   ├── App.vue                     # 根组件
│   │   ├── api/                        # API接口
│   │   │   ├── request.js             # Axios封装
│   │   │   ├── user.js                # 用户API
│   │   │   └── auth.js                # 登录API
│   │   ├── components/                 # 通用组件
│   │   ├── router/
│   │   │   └── index.js               # 路由配置
│   │   ├── stores/                     # Pinia状态管理
│   │   └── views/                      # 页面组件
│   │       ├── HomeView.vue           # 首页
│   │       ├── UserView.vue           # 用户管理页面
│   │       └── LoginView.vue          # 登录页面
│   └── vite.config.js                 # Vite配置
├── database/                   # 数据库脚本
│   └── init.sql                        # 初始化脚本
└── DEVELOPMENT_LOG.md          # 开发日志(本文件)
```

---

## 开发记录

### 2026-04-09 第一次开发 - 项目初始化与基础功能

#### 完成内容
1. **项目启动**
   - 安装前端依赖
   - 配置数据库连接（修正密码）
   - 创建demo_db数据库
   - 启动前后端服务

2. **数据库初始化**
   - 创建users表
   - 插入admin账户数据
     - username: admin
     - password: admin
     - email: admin@example.com
     - phone: 13800138000

3. **基础CRUD功能**
   - 用户列表查询
   - 用户创建
   - 用户编辑
   - 用户删除

#### 遇到的问题及解决方案
- **问题1**: MySQL密码错误
  - **解决**: 更新application.yml中的数据库密码为正确密码

- **问题2**: 数据库demo_db不存在
  - **解决**: 手动创建demo_db数据库

---

### 2026-04-09 第二次开发 - 登录功能实现

#### 完成内容
1. **数据库扩展**
   - users表新增account字段（账户）
   - 创建login_log登录日志表
     - id: 日志ID
     - user_id: 用户ID
     - account: 登录账户
     - login_time: 登录时间
     - login_status: 登录状态(SUCCESS/FAILED)
     - login_ip: 登录IP
     - login_message: 登录消息
     - user_agent: 用户代理

2. **后端开发**
   - User实体类添加account字段
   - 创建LoginLog实体类
   - 创建LoginLogRepository数据访问层
   - UserRepository添加按account查询方法
   - 创建LoginRequest和LoginResponse DTO
   - 创建AuthService登录服务
     - 账户密码验证
     - 登录日志记录
     - Token生成
   - 创建AuthController登录接口
     - POST /api/auth/login

3. **前端开发**
   - 创建登录API接口 (src/api/auth.js)
   - 创建登录页面组件 (src/views/LoginView.vue)
     - 美观的登录表单UI
     - 表单验证
     - 登录状态提示
   - 配置登录路由 (/login)

4. **默认账户**
   - account: admin
   - password: admin

#### 遇到的问题及解决方案
- **问题1**: API路径重复 (/api/api/auth/login)
  - **原因**: request.js的baseURL是'/api'，auth.js中URL又写了'/api/auth/login'
  - **解决**: 修改auth.js中的URL为'/auth/login'

#### 功能特性
- 完整的登录流程
- 详细的登录日志记录
- 登录成功/失败状态跟踪
- IP地址和User-Agent记录
- 用户信息本地存储

---

## API接口文档

### 用户管理接口

| 方法 | 路径 | 说明 | 请求体 | 响应 |
|------|------|------|--------|------|
| GET | /api/users | 获取所有用户 | - | User[] |
| GET | /api/users/{id} | 根据ID获取用户 | - | User |
| POST | /api/users | 创建用户 | User | User |
| PUT | /api/users/{id} | 更新用户 | User | User |
| DELETE | /api/users/{id} | 删除用户 | - | - |

### 认证接口

| 方法 | 路径 | 说明 | 请求体 | 响应 |
|------|------|------|--------|------|
| POST | /api/auth/login | 用户登录 | LoginRequest | LoginResponse |

#### LoginRequest
```json
{
  "account": "admin",
  "password": "admin"
}
```

#### LoginResponse
```json
{
  "success": true,
  "message": "登录成功",
  "userId": 1,
  "username": "admin",
  "account": "admin",
  "token": "uuid-token"
}
```

---

## 数据库表结构

### users表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| username | VARCHAR(50) | 用户名，唯一 |
| account | VARCHAR(50) | 账户，唯一 |
| password | VARCHAR(100) | 密码 |
| email | VARCHAR(100) | 邮箱 |
| phone | VARCHAR(20) | 电话 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

### login_log表
| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| user_id | BIGINT | 用户ID |
| account | VARCHAR(50) | 登录账户 |
| login_time | DATETIME | 登录时间 |
| login_status | VARCHAR(20) | 登录状态(SUCCESS/FAILED) |
| login_ip | VARCHAR(50) | 登录IP |
| login_message | VARCHAR(255) | 登录消息 |
| user_agent | VARCHAR(500) | 用户代理 |

---

## 访问地址

- **前端**: http://localhost:5173
- **登录页面**: http://localhost:5173/login
- **后端API**: http://localhost:8080
- **数据库**: localhost:3306/demo_db

---

## 默认账户信息

- **账户**: admin
- **密码**: admin
- **邮箱**: admin@example.com
- **电话**: 13800138000

---

## 待开发功能

- [ ] 用户注册功能
- [ ] 密码加密存储
- [ ] JWT Token认证
- [ ] 路由权限守卫
- [ ] 用户个人信息修改
- [ ] 登出功能
- [ ] 登录日志查询页面
- [ ] 分页功能
- [ ] 搜索功能

---

## 注意事项

1. 数据库密码已配置为：`0wfYDIG6wm1gV5`
2. 前端API请求baseURL为`/api`，会被代理到后端`http://localhost:8080`
3. 后端已配置CORS允许跨域请求
4. 登录日志会记录每次登录尝试（成功和失败）
5. 当前密码为明文存储，生产环境需要加密

---

## 更新日志

### v1.0.0 (2026-04-09)
- 初始化项目
- 实现用户CRUD基础功能
- 实现登录功能
- 实现登录日志记录

### v1.0.1 (2026-04-09) - Bug修复
#### 修复内容
1. **API路径重复问题**
   - 问题：请求URL为 `/api/api/auth/login`
   - 原因：request.js的baseURL为`/api`，auth.js中URL又写了`/api/auth/login`
   - 解决：修改auth.js中的URL为`/auth/login`

2. **CORS配置冲突问题**
   - 问题：`When allowCredentials is true, allowedOrigins cannot contain the special value "*"`
   - 原因：Controller上的`@CrossOrigin(origins = "*")`与全局CORS配置冲突
   - 解决：
     - 移除AuthController和UserController上的`@CrossOrigin`注解
     - 统一使用全局CorsConfig配置
     - 全局配置：`allowedOrigins("http://localhost:5173")` + `allowCredentials(true)`

#### 技术要点
- Spring Boot CORS配置：当`allowCredentials(true)`时，不能使用`origins = "*"`
- 应该明确指定允许的源地址，如：`http://localhost:5173`
- 推荐使用全局CORS配置，避免每个Controller单独配置

---

### v1.0.2 (2026-04-09) - 功能增强
#### 新增功能
1. **用户管理页面account字段**
   - 用户列表表格添加"账户"列显示
   - 新增用户表单添加"账户"输入框
   - 编辑用户表单添加"账户"输入框
   - 表单数据模型添加account字段

2. **首页用户信息显示**
   - 登录成功后在首页头部显示"欢迎，{用户名}"
   - 添加退出登录按钮
   - 未登录时显示"登录"按钮
   - 从localStorage读取用户信息

#### 技术实现
- 使用localStorage存储用户登录信息
- 使用Vue3的ref和onMounted进行状态管理
- 条件渲染(v-if)控制登录/未登录状态显示

---

### v1.0.3 (2026-04-09) - 内网穿透支持
#### 新增功能
1. **Vite配置支持内网穿透**
   - 添加`host: '0.0.0.0'`监听所有网络接口
   - 添加`allowedHosts: ['.natappfree.cc']`通配符模式允许花生壳域名
   - 支持通过花生壳域名访问前端

2. **后端CORS支持花生壳域名**
   - 添加花生壳域名到CORS允许列表
   - 支持HTTP和HTTPS协议
   - 解决内网穿透访问时的跨域问题

#### 配置修改
**前端 vite.config.js:**
```javascript
server: {
  host: '0.0.0.0',  // 监听所有网络接口
  allowedHosts: ['.natappfree.cc'],  // 通配符允许所有子域名
  proxy: { '/api': { target: 'http://localhost:8080' } }
}
```

**后端 CorsConfig.java:**
```java
.allowedOrigins(
  "http://localhost:5173",
  "http://v85596d4.natappfree.cc",
  "https://v85596d4.natappfree.cc"
)
```

#### 访问地址
- **本地**: http://localhost:5173
- **花生壳**: http://v85596d4.natappfree.cc

---

### v1.0.4 (2026-04-09) - UI优化与路由守卫
#### 新增功能
1. **路由守卫实现登录验证**
   - 添加全局路由守卫`router.beforeEach`
   - 为路由添加`meta.requiresAuth`标记
   - 未登录自动跳转到登录页面
   - 已登录访问登录页自动重定向到首页

2. **前端UI全面优化**
   - 采用Minimalist Modern设计风格
   - 登录页面：双栏布局、品牌展示、渐变背景、玻璃态效果
   - 首页：现代Header、用户头像、卡片式导航、技术栈展示
   - 统一配色：蓝紫渐变主题(#3B82F6 → #8B5CF6)
   - 响应式设计：适配移动端和桌面端
   - 微交互动画：悬停、聚焦、过渡效果

#### 技术实现
**路由守卫配置:**
```javascript
router.beforeEach((to, from, next) => {
  const isLoggedIn = !!localStorage.getItem('user')
  if (to.meta.requiresAuth && !isLoggedIn) {
    next('/login')  // 未登录跳转登录页
  } else if (to.path === '/login' && isLoggedIn) {
    next('/')  // 已登录重定向首页
  } else {
    next()
  }
})
```

**UI设计特点:**
- 双栏登录页面：左侧品牌展示，右侧登录表单
- 渐变主题色：#3B82F6 → #8B5CF6
- 圆角设计：统一使用16px/20px圆角
- 阴影层次：多层次阴影营造深度感
- 玻璃态效果：backdrop-filter实现毛玻璃效果

#### 后端CORS通配配置
使用`allowedOriginPatterns`实现通配：
```java
.allowedOriginPatterns(
  "http://localhost:*",
  "http://*.natappfree.cc",
  "https://*.natappfree.cc"
)
```

---

### v1.0.5 (2026-04-09) - 密码管理与安全增强
#### 新增功能
1. **默认密码配置**
   - 在application.yml中配置默认密码：`!qazXSW@`
   - 配置密码过期时间：6个月
   - 使用@ConfigurationProperties读取配置

2. **最后登录时间记录**
   - 数据库添加`last_login_time`字段
   - User实体添加lastLoginTime属性
   - 登录成功后自动更新最后登录时间

3. **密码更新强制检查**
   - 检查是否使用默认密码
   - 检查密码是否超过6个月未更新
   - 满足条件强制跳转密码更新页面

4. **密码更新功能**
   - 创建密码更新页面UpdatePasswordView.vue
   - 实现密码更新接口POST /api/auth/update-password
   - 密码更新成功后自动跳转登录页

#### 技术实现
**配置文件 application.yml:**
```yaml
app:
  security:
    default-password: "!qazXSW@"
    password-expire-months: 6
```

**密码检查逻辑:**
```java
// 检查默认密码
if (defaultPassword.equals(password)) {
    needUpdate = true;
    updateReason = "DEFAULT_PASSWORD";
}

// 检查密码过期
if (lastLoginTime.plusMonths(6).isBefore(now)) {
    needUpdate = true;
    updateReason = "PASSWORD_EXPIRED";
}
```

**前端处理流程:**
1. 登录成功后检查`needUpdatePassword`字段
2. 如需更新，跳转`/update-password`页面
3. 用户更新密码后跳转登录页
4. 取消更新也返回登录页

#### 数据库变更
```sql
ALTER TABLE users ADD COLUMN last_login_time DATETIME;
```

#### API接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/update-password | 更新密码 |

---

### v1.0.6 (2026-04-09) - 用户管理重置密码功能
#### 新增功能
1. **重置密码功能**
   - 管理员可重置用户密码为默认密码
   - 用户管理列表添加重置密码按钮
   - 重置后用户需使用默认密码登录

#### 技术实现
**后端接口:**
```java
@PostMapping("/{id}/reset-password")
public ResponseEntity<Map<String, Object>> resetPassword(@PathVariable Long id) {
    // 将用户密码重置为默认密码
    user.setPassword(securityConfig.getDefaultPassword());
}
```

**前端实现:**
- UserView.vue添加重置密码按钮
- 确认对话框防止误操作
- 成功提示用户密码已重置

#### API接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/users/{id}/reset-password | 重置用户密码 |

---

### v1.0.7 (2026-04-09) - 用户注册功能
#### 新增功能
1. **用户注册功能**
   - 登录页面添加注册表单
   - 支持账户、用户名、密码输入
   - 登录/注册模式切换

2. **账户唯一性校验**
   - 实时检查账户是否已存在
   - 输入账户后自动验证
   - 重复账户提示警告

3. **注册表单验证**
   - 账户长度：3-20个字符
   - 密码长度：至少6位
   - 确认密码一致性检查
   - 用户名可选（默认使用账户）

#### 技术实现
**后端接口:**
```java
// 注册用户
@PostMapping("/register")
public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, Object> request)

// 检查账户是否存在
@GetMapping("/check-account")
public ResponseEntity<Map<String, Object>> checkAccount(@RequestParam String account)
```

**前端实现:**
- 登录/注册模式切换按钮
- 账户失焦时自动检查唯一性
- 表单验证规则动态计算
- 注册成功后自动切换到登录

#### API接口
| 方法 | 路径 | 说明 |
|------|------|------|
| POST | /api/auth/register | 用户注册 |
| GET | /api/auth/check-account | 检查账户是否存在 |

---

### v1.0.8 (2026-04-09) - MyBatis Plus重构
#### 重构内容
1. **ORM框架替换**
   - 从Spring Data JPA迁移到MyBatis Plus 3.5.5
   - 移除JPA依赖和配置
   - 添加MyBatis Plus依赖和配置

2. **数据访问层重构**
   - Repository替换为Mapper
   - 继承BaseMapper获取基础CRUD
   - 使用LambdaQueryWrapper构建查询

3. **实体类优化**
   - 使用MyBatis Plus注解
   - @TableName指定表名
   - @TableId配置主键策略
   - @TableField自动填充配置

4. **自动填充机制**
   - 创建MyMetaObjectHandler
   - 自动填充创建时间
   - 自动填充更新时间
   - 自动填充登录时间

#### 技术优势
- **性能提升**：启动速度更快
- **SQL控制**：更灵活的SQL编写
- **查询简化**：Lambda表达式查询
- **代码简洁**：减少样板代码

#### 配置示例
```yaml
mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
    log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
  global-config:
    db-config:
      id-type: auto
```

#### 查询示例
```java
// Lambda查询
User user = userMapper.selectOne(
    new LambdaQueryWrapper<User>()
        .eq(User::getAccount, account)
);

// 计数查询
long count = userMapper.selectCount(
    new LambdaQueryWrapper<User>()
        .eq(User::getAccount, account)
);
```

---

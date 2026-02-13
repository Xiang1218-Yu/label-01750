# 宿舍管理系统

基于 Spring Boot 3 + Vue 3 + MySQL 8 的宿舍管理系统。

## How to Run

### Docker 方式（推荐）

```bash
# 启动所有服务
docker-compose up --build -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

### 手动启动

1. 启动后端（数据库表和初始数据会自动创建）：
```bash
cd backend
mvn spring-boot:run
```

2. 启动前端：
```bash
cd frontend-admin
npm install
npm run dev
```

> 注意：系统启动时会自动执行数据库初始化，无需手动执行 SQL 脚本。

## 数据库配置

系统启动时会自动创建数据库表结构和初始数据（通过 DataInitializer）。

默认配置（application.yml）：
- 主机：localhost
- 端口：3306
- 数据库：dorm_db
- 用户名：root
- 密码：root（可通过环境变量 DB_PASSWORD 覆盖）

配置数据库连接（可选，如果本地 MySQL 配置不同）：
```bash
# 设置环境变量
export DB_PASSWORD=你的密码
export DB_HOST=你的主机
```

支持的环境变量：
| 变量名 | 默认值 | 说明 |
|--------|--------|------|
| DB_HOST | localhost | 数据库主机 |
| DB_PORT | 3306 | 数据库端口 |
| DB_NAME | dorm_db | 数据库名称 |
| DB_USER | root | 数据库用户名 |
| DB_PASSWORD | root | 数据库密码 |
| JWT_SECRET | (内置默认值) | JWT签名密钥，生产环境建议自定义 |
| JWT_EXPIRATION | 86400000 | JWT过期时间（毫秒），默认24小时 |

## 安全配置说明

### JWT 密钥配置
生产环境部署时，强烈建议通过环境变量设置自定义的 JWT 密钥：

```bash
# Linux/Mac
export JWT_SECRET=your-custom-secret-key-at-least-32-characters

# Docker Compose
# 在 docker-compose.yml 的 backend 服务中添加：
environment:
  JWT_SECRET: your-custom-secret-key-at-least-32-characters
```

密钥要求：
- 长度至少 32 个字符
- 建议使用随机生成的字符串
- 不要在代码中硬编码生产环境密钥

### 接口权限控制
系统实现了基于角色的接口级别权限控制（RBAC）：
- 使用 `@RequireRole` 注解控制接口访问权限
- 角色定义：1-管理员、2-宿管、3-学生
- 未授权访问将返回 403 错误

## Services

| 服务 | 地址 | 说明 |
|------|------|------|
| 管理后台 | http://localhost:8081 | Vue 3 + Element Plus |
| 后端 API | http://localhost:8080 | Spring Boot 3 |
| MySQL | localhost:3306 | 数据库 |

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 宿管 | sg001 | 123456 |
| 学生 | 2021001 | 123456 |

> 学生账号在新增学生时自动创建，用户名为学号，默认密码 123456

## 功能模块

### 管理员功能
- 用户管理：管理系统用户（管理员、宿管）
- 楼栋管理：管理宿舍楼栋信息
- 房间管理：管理房间及床位
- 学生管理：管理学生信息、分配/退宿
- 维修管理：处理维修申请
- 访客管理：登记和管理访客记录
- 公告管理：发布系统公告
- 操作日志：查看系统操作记录

### 宿管功能
- 房间/学生管理
- 维修申请处理
- 访客记录管理
- 公告发布

### 学生功能
- 查看个人宿舍信息
- 提交维修申请
- 查看公告

## 技术栈

- **后端**: Java 17 + Spring Boot 3.2 + MyBatis-Plus 3.5
- **前端**: Vue 3 + Vite 5 + Element Plus 2.4 + Pinia
- **数据库**: MySQL 8.0
- **认证**: JWT + 角色权限控制
- **日志**: AOP 操作日志记录
- **容器化**: Docker + Docker Compose

## 项目结构

```
├── backend/                 # 后端项目
│   ├── src/main/java/com/dorm/
│   │   ├── annotation/      # 自定义注解（@OperationLog, @RequireRole）
│   │   ├── aspect/          # AOP切面（操作日志、权限校验）
│   │   ├── common/          # 通用类
│   │   ├── config/          # 配置类
│   │   ├── controller/      # 控制器
│   │   ├── entity/          # 实体类
│   │   ├── interceptor/     # 拦截器
│   │   ├── mapper/          # MyBatis Mapper
│   │   ├── service/         # 服务层
│   │   └── util/            # 工具类
│   └── src/main/resources/
│       ├── application.yml  # 配置文件
│       └── schema.sql       # 数据库脚本
├── frontend-admin/          # 前端管理后台
│   ├── src/
│   │   ├── api/             # API 接口
│   │   ├── router/          # 路由配置
│   │   ├── stores/          # Pinia 状态管理
│   │   ├── styles/          # 样式文件
│   │   └── views/           # 页面组件
│   └── Dockerfile
├── docker-compose.yml       # Docker 编排
└── README.md
```

## 单元测试

项目包含基础单元测试，位于 `backend/src/test/java/com/dorm/`：

```bash
cd backend
mvn test
```

测试覆盖：
- `JwtUtilTest`: JWT 工具类测试（生成、解析、验证）
- `AuthServiceTest`: 认证服务测试（登录逻辑）
- `ResultTest`: 统一响应类测试

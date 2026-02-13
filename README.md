# 宿舍管理系统

基于 Spring Boot 3 + Vue 3 + MySQL 8 的宿舍管理系统，支持管理员、宿管、学生三种角色。

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

1. 创建数据库并执行 `backend/src/main/resources/schema.sql`

2. 启动后端：
```bash
cd backend
mvn spring-boot:run
```

3. 启动前端：
```bash
cd frontend-admin
npm install
npm run dev
```

## Services

| 服务 | 地址 | 说明 |
|------|------|------|
| 管理后台 | http://localhost:8081 | Vue 3 + Element Plus |
| 后端 API | http://localhost:8080 | Spring Boot 3 |
| MySQL | localhost:3306 | 数据库 |

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 学生 | (学号) | 123456 |

> 学生账号在新增学生时自动创建，用户名为学号，默认密码 123456

## 题目内容

制作一个宿舍管理系统，功能帮我添加，前端使用 Vue 3，后端使用 Spring Boot，数据库使用 MySQL。

---

## 功能模块

### 管理员功能
- 用户管理：管理系统用户（管理员、宿管）
- 楼栋管理：管理宿舍楼栋信息
- 房间管理：管理房间及床位
- 学生管理：管理学生信息、分配/退宿
- 维修管理：处理维修申请
- 访客管理：查看访客记录
- 公告管理：发布系统公告

### 宿管功能
- 楼栋/房间/学生管理
- 维修申请处理
- 访客记录管理
- 公告发布

### 学生功能
- 查看个人宿舍信息
- 提交维修申请
- 登记访客
- 查看公告

## 技术栈

- **后端**: Java 17 + Spring Boot 3.2 + MyBatis-Plus 3.5
- **前端**: Vue 3 + Vite 5 + Element Plus 2.4 + Pinia
- **数据库**: MySQL 8.0
- **认证**: JWT
- **容器化**: Docker + Docker Compose

## 项目结构

```
├── backend/                 # 后端项目
│   ├── src/main/java/com/dorm/
│   │   ├── annotation/      # 自定义注解
│   │   ├── aspect/          # AOP切面
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
├── docs/                    # 文档
│   └── project_design.md    # 项目设计文档
├── docker-compose.yml       # Docker 编排
└── README.md
```

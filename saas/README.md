# 理发店SaaS平台

## 项目简介

理发店SaaS平台是一个完整的后端服务系统，为理发店提供会员管理、理发师管理、发型管理、智能匹配、订单管理等核心功能。支持多商家入驻，数据完全隔离。

## 核心功能

### 1. 商家管理
- ✅ 商家注册申请
- ✅ 管理员审核
- ✅ 商家登录认证
- ✅ 商家信息维护
- ✅ 商家上下线控制

### 2. 会员管理
- ✅ 会员录入（手机号绑定）
- ✅ 会员充值
- ✅ 会员消费
- ✅ 会员等级体系
- ✅ 等级折扣自动计算

### 3. 理发师管理
- ✅ 理发师信息维护
- ✅ 理发师级别设置
- ✅ 级别提成比例配置
- ✅ 理发师擅长发型关联
- ✅ 熟练度管理

### 4. 发型管理
- ✅ 发型类型管理（剪发、烫发、染发、护理、造型）
- ✅ 发型细节选项
- ✅ 基础价格设置
- ✅ 预计时长配置

### 5. 智能匹配
- ✅ 用户选择发型和细节
- ✅ 一键匹配适合的理发师
- ✅ 匹配评分算法（熟练度、评分、订单量）
- ✅ 价格自动计算（含会员折扣）

### 6. 订单管理
- ✅ 订单创建
- ✅ 订单状态流转
- ✅ 会员余额扣减
- ✅ 理发师提成计算
- ✅ 订单取消

### 7. 数据隔离
- ✅ 按商家完全隔离数据
- ✅ JWT认证
- ✅ 权限控制

## 技术架构

```
saas
├── src/main/java/com/saas/barbershop/
│   ├── controller/      # 控制器层
│   ├── service/         # 业务逻辑层
│   ├── repository/      # 数据访问层
│   ├── entity/          # 实体类
│   ├── dto/             # 数据传输对象
│   ├── config/          # 配置类
│   ├── security/        # 安全认证
│   ├── exception/       # 异常处理
│   └── utils/           # 工具类
├── src/main/resources/
│   └── application.yml  # 配置文件
├── src/test/            # 测试代码
├── pom.xml              # Maven配置
└── API_DOCUMENTATION.md # API文档
```

## 技术栈

- **Java**: 1.8
- **Spring Boot**: 2.7.18
- **Spring Security**: JWT认证
- **Spring Data JPA**: 数据持久化
- **MySQL**: 8.0
- **Maven**: 构建工具
- **Lombok**: 代码简化
- **Swagger/OpenAPI**: API文档

## 数据库表结构

### 核心表
- `merchants` - 商家表
- `members` - 会员表
- `member_levels` - 会员等级表
- `barbers` - 理发师表
- `barber_levels` - 理发师级别表
- `hairstyle_types` - 发型类型表
- `hairstyle_details` - 发型细节表
- `barber_hairstyles` - 理发师发型关联表
- `orders` - 订单表
- `order_items` - 订单项目表
- `order_item_details` - 订单项目细节表
- `transaction_records` - 交易记录表

## 快速开始

### 1. 环境准备

- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库配置

修改 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/barbershop_saas?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: your_username
    password: your_password
```

### 3. 创建数据库

```sql
CREATE DATABASE barbershop_saas CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 4. 编译运行

```bash
# 编译
mvn clean compile

# 运行
mvn spring-boot:run
```

### 5. 访问服务

- 服务地址: http://localhost:8080/api
- Swagger文档: http://localhost:8080/api/swagger-ui.html
- API文档: 查看 `API_DOCUMENTATION.md`

## API接口概览

### 认证接口
- `POST /auth/register` - 商家注册
- `POST /auth/login` - 商家登录
- `GET /auth/me` - 获取当前商家
- `PUT /auth/me` - 更新商家信息

### 会员管理
- `POST /members` - 创建会员
- `GET /members` - 会员列表
- `GET /members/{id}` - 会员详情
- `PUT /members/{id}` - 更新会员
- `DELETE /members/{id}` - 删除会员
- `POST /members/{id}/recharge` - 会员充值

### 会员等级
- `POST /member-levels` - 创建等级
- `GET /member-levels` - 等级列表
- `PUT /member-levels/{id}` - 更新等级
- `DELETE /member-levels/{id}` - 删除等级

### 理发师管理
- `POST /barbers` - 创建理发师
- `GET /barbers` - 理发师列表
- `GET /barbers/active` - 在职理发师
- `PUT /barbers/{id}` - 更新理发师
- `DELETE /barbers/{id}` - 删除理发师

### 理发师级别
- `POST /barber-levels` - 创建级别
- `GET /barber-levels` - 级别列表

### 发型管理
- `POST /hairstyles/types` - 创建发型类型
- `GET /hairstyles/types` - 发型列表
- `POST /hairstyles/types/{id}/details` - 创建发型细节
- `GET /hairstyles/types/{id}/details` - 细节列表

### 智能匹配
- `POST /matching/barbers` - 匹配理发师
- `POST /matching/price` - 计算价格

### 订单管理
- `POST /orders` - 创建订单
- `GET /orders` - 订单列表
- `POST /orders/{id}/start` - 开始订单
- `POST /orders/{id}/complete` - 完成订单
- `POST /orders/{id}/cancel` - 取消订单

### 商家管理（管理员）
- `GET /admin/merchants` - 商家列表
- `POST /admin/merchants/{id}/audit` - 审核商家
- `POST /admin/merchants/{id}/disable` - 禁用商家
- `POST /admin/merchants/{id}/enable` - 启用商家

## 测试

### 运行测试

```bash
# 运行所有测试
mvn test

# 运行特定测试类
mvn test -Dtest=MerchantServiceTest

# 运行特定测试方法
mvn test -Dtest=MerchantServiceTest#testRegister
```

### 测试覆盖

- ✅ 商家注册、登录、审核流程
- ✅ 会员CRUD、充值功能
- ✅ 理发师管理
- ✅ 智能匹配算法
- ✅ 订单生命周期

## 核心业务逻辑

### 1. 会员等级自动升级

会员根据累计消费金额自动升级：
- 消费达到等级最低金额要求自动升级
- 享受对应等级的折扣优惠

### 2. 智能匹配算法

匹配评分维度：
- 熟练度（40分）: EXPERT(40) > ADVANCED(30) > MEDIUM(20) > BEGINNER(10)
- 评分（30分）: 根据理发师评分计算
- 订单量（20分）: 根据历史订单量计算
- 性别偏好（10分）: 匹配用户偏好

### 3. 订单结算流程

1. 创建订单：计算总价、折扣、理发师提成
2. 开始服务：更新订单状态
3. 完成服务：
   - 扣减会员余额
   - 记录交易流水
   - 更新理发师订单数
   - 检查会员等级升级

### 4. 数据隔离

- 所有查询都带 `merchant_id` 条件
- JWT Token 中包含商家ID
- Repository 层统一数据过滤

## 项目特点

1. **完整的业务闭环**: 从商家入驻到订单完成的全流程
2. **智能匹配**: 基于多维度评分的理发师推荐
3. **灵活的等级体系**: 会员等级和理发师级别可配置
4. **完善的权限控制**: JWT认证 + 数据隔离
5. **丰富的API接口**: RESTful设计，Swagger文档
6. **完整的测试覆盖**: 单元测试 + 集成测试

## 开发规范

- 代码遵循阿里巴巴Java开发规范
- RESTful API设计规范
- 统一的响应格式
- 完善的参数校验
- 详细的日志记录

## 后续优化方向

1. 添加Redis缓存
2. 引入消息队列处理订单
3. 添加数据统计报表
4. 支持预约排班
5. 添加评价系统
6. 支持多店铺管理

## 联系方式

如有问题或建议，欢迎反馈。

## 许可证

MIT License

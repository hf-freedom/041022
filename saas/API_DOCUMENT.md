# 理发店SaaS平台 API 接口文档

## 概述

本文档描述了理发店SaaS平台后端服务的RESTful API接口。平台支持商家申请、审核、上下线管理，会员管理（充值、消费、等级折扣），理发师管理（级别、提成），发型管理以及智能匹配理发师等功能。

## 基础信息

- **基础URL**: `http://localhost:8080`
- **认证方式**: JWT Bearer Token
- **内容类型**: `application/json`

## 认证说明

除注册和登录接口外，其他接口均需要在请求头中携带JWT Token：

```
Authorization: Bearer <token>
```

---

## 1. 商家管理接口

### 1.1 商家注册申请

**POST** `/api/merchants/register`

提交商家注册申请，等待管理员审核。

**请求体**:
```json
{
    "username": "shop001",
    "password": "password123",
    "shopName": "时尚理发店",
    "address": "北京市朝阳区xxx街道",
    "phone": "13800138000",
    "contactPerson": "张经理",
    "businessLicense": "营业执照编号"
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "注册申请已提交，等待审核",
    "data": {
        "id": 1,
        "username": "shop001",
        "shopName": "时尚理发店",
        "address": "北京市朝阳区xxx街道",
        "phone": "13800138000",
        "contactPerson": "张经理",
        "status": "PENDING",
        "balance": 0,
        "createdAt": "2024-01-01T10:00:00"
    }
}
```

### 1.2 商家登录

**POST** `/api/merchants/login`

商家登录获取JWT Token。

**请求体**:
```json
{
    "username": "shop001",
    "password": "password123"
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "token": "eyJhbGciOiJIUzI1NiJ9...",
        "merchant": {
            "id": 1,
            "username": "shop001",
            "shopName": "时尚理发店",
            "status": "ONLINE"
        }
    }
}
```

### 1.3 获取待审核商家列表

**GET** `/api/merchants/pending`

获取待审核的商家列表（管理员接口）。

**查询参数**:
| 参数 | 类型 | 默认值 | 描述 |
|------|------|--------|------|
| page | int | 0 | 页码 |
| size | int | 10 | 每页数量 |

**响应示例**:
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "content": [...],
        "totalElements": 10,
        "totalPages": 1,
        "number": 0
    }
}
```

### 1.4 审核通过商家

**POST** `/api/merchants/{id}/approve`

审核通过商家申请。

**路径参数**:
| 参数 | 类型 | 描述 |
|------|------|------|
| id | Long | 商家ID |

**响应示例**:
```json
{
    "code": 200,
    "message": "审核通过",
    "data": {
        "id": 1,
        "status": "APPROVED"
    }
}
```

### 1.5 审核拒绝商家

**POST** `/api/merchants/{id}/reject`

拒绝商家申请。

**响应示例**:
```json
{
    "code": 200,
    "message": "已拒绝",
    "data": {...}
}
```

### 1.6 商家上线

**POST** `/api/merchants/{id}/online`

将商家状态设为上线。

**响应示例**:
```json
{
    "code": 200,
    "message": "已上线",
    "data": {...}
}
```

### 1.7 商家下线

**POST** `/api/merchants/{id}/offline`

将商家状态设为下线。

**响应示例**:
```json
{
    "code": 200,
    "message": "已下线",
    "data": {...}
}
```

### 1.8 获取商家详情

**GET** `/api/merchants/{id}`

根据ID获取商家详情。

### 1.9 获取所有商家列表

**GET** `/api/merchants`

分页获取所有商家列表。

---

## 2. 会员管理接口

### 2.1 创建会员

**POST** `/api/members`

录入新会员。

**请求体**:
```json
{
    "name": "张三",
    "phone": "15000150001"
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "会员创建成功",
    "data": {
        "id": 1,
        "merchantId": 1,
        "name": "张三",
        "phone": "15000150001",
        "level": "NORMAL",
        "discount": 1.0,
        "balance": 0,
        "totalConsumption": 0,
        "createdAt": "2024-01-01T10:00:00"
    }
}
```

### 2.2 获取会员列表

**GET** `/api/members`

分页获取当前商家的会员列表。

**查询参数**:
| 参数 | 类型 | 默认值 | 描述 |
|------|------|--------|------|
| page | int | 0 | 页码 |
| size | int | 10 | 每页数量 |

### 2.3 获取会员详情

**GET** `/api/members/{id}`

根据ID获取会员详情。

### 2.4 根据手机号查询会员

**GET** `/api/members/phone/{phone}`

根据手机号查询会员信息。

### 2.5 会员充值

**POST** `/api/members/recharge`

为会员充值。

**请求体**:
```json
{
    "memberId": 1,
    "amount": 100.00,
    "remark": "首次充值"
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "充值成功",
    "data": {
        "id": 1,
        "memberId": 1,
        "type": "RECHARGE",
        "amount": 100.00,
        "balanceAfter": 100.00,
        "createdAt": "2024-01-01T10:00:00"
    }
}
```

### 2.6 获取交易记录

**GET** `/api/members/transactions`

获取会员交易记录。

**查询参数**:
| 参数 | 类型 | 默认值 | 描述 |
|------|------|--------|------|
| memberId | Long | - | 会员ID（可选） |
| page | int | 0 | 页码 |
| size | int | 10 | 每页数量 |

### 2.7 更新会员等级

**PUT** `/api/members/{id}/level`

手动更新会员等级。

**查询参数**:
| 参数 | 类型 | 描述 |
|------|------|------|
| level | String | 会员等级：NORMAL/SILVER/GOLD/PLATINUM/DIAMOND |

---

## 3. 理发师管理接口

### 3.1 创建理发师

**POST** `/api/barbers`

添加新理发师。

**请求体**:
```json
{
    "name": "理发师李四",
    "phone": "15100151001",
    "level": "SENIOR",
    "commissionRate": 0.35
}
```

**理发师级别**:
- JUNIOR: 初级
- INTERMEDIATE: 中级
- SENIOR: 高级
- MASTER: 大师
- CHIEF: 首席

**响应示例**:
```json
{
    "code": 200,
    "message": "理发师创建成功",
    "data": {
        "id": 1,
        "merchantId": 1,
        "name": "理发师李四",
        "phone": "15100151001",
        "level": "SENIOR",
        "commissionRate": 0.35,
        "active": true,
        "createdAt": "2024-01-01T10:00:00"
    }
}
```

### 3.2 获取理发师列表

**GET** `/api/barbers`

分页获取理发师列表。

### 3.3 获取活跃理发师列表

**GET** `/api/barbers/active`

获取所有活跃的理发师。

### 3.4 获取理发师详情

**GET** `/api/barbers/{id}`

根据ID获取理发师详情。

### 3.5 更新理发师

**PUT** `/api/barbers/{id}`

更新理发师信息。

**请求体**:
```json
{
    "name": "理发师李四",
    "phone": "15100151001",
    "level": "MASTER",
    "commissionRate": 0.40
}
```

### 3.6 更新理发师状态

**PUT** `/api/barbers/{id}/status`

启用或禁用理发师。

**查询参数**:
| 参数 | 类型 | 描述 |
|------|------|------|
| active | Boolean | 是否活跃 |

### 3.7 删除理发师

**DELETE** `/api/barbers/{id}`

删除理发师。

---

## 4. 发型管理接口

### 4.1 创建发型类型

**POST** `/api/hairstyles/types`

添加新的发型类型。

**请求体**:
```json
{
    "name": "短发",
    "description": "清爽短发造型",
    "imageUrl": "http://example.com/short.jpg"
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "发型类型创建成功",
    "data": {
        "id": 1,
        "name": "短发",
        "description": "清爽短发造型",
        "imageUrl": "http://example.com/short.jpg",
        "active": true
    }
}
```

### 4.2 获取发型类型列表

**GET** `/api/hairstyles/types`

分页获取发型类型列表。

### 4.3 获取活跃发型类型列表

**GET** `/api/hairstyles/types/active`

获取所有活跃的发型类型。

### 4.4 获取发型类型详情

**GET** `/api/hairstyles/types/{id}`

根据ID获取发型类型详情。

### 4.5 更新发型类型

**PUT** `/api/hairstyles/types/{id}`

更新发型类型信息。

### 4.6 删除发型类型

**DELETE** `/api/hairstyles/types/{id}`

删除发型类型。

### 4.7 切换发型类型状态

**PUT** `/api/hairstyles/types/{id}/toggle`

启用或禁用发型类型。

### 4.8 创建发型选项

**POST** `/api/hairstyles/options`

添加新的发型选项。

**请求体**:
```json
{
    "name": "渐变",
    "category": "修剪方式",
    "description": "两侧渐变效果"
}
```

### 4.9 获取发型选项列表

**GET** `/api/hairstyles/options`

分页获取发型选项列表。

### 4.10 获取活跃发型选项列表

**GET** `/api/hairstyles/options/active`

获取所有活跃的发型选项。

### 4.11 按分类获取发型选项

**GET** `/api/hairstyles/options/category/{category}`

根据分类获取发型选项列表。

### 4.12 获取发型选项详情

**GET** `/api/hairstyles/options/{id}`

### 4.13 更新发型选项

**PUT** `/api/hairstyles/options/{id}`

### 4.14 删除发型选项

**DELETE** `/api/hairstyles/options/{id}`

---

## 5. 消费管理接口

### 5.1 创建消费记录

**POST** `/api/consumptions`

记录会员消费。

**请求体**:
```json
{
    "memberId": 1,
    "barberId": 1,
    "hairstyleTypeId": 1,
    "originalAmount": 100.00,
    "paymentMethod": "BALANCE",
    "hairstyleOptionIds": [1, 2, 3],
    "remark": "周末理发"
}
```

**支付方式**:
- BALANCE: 余额支付
- CASH: 现金支付
- CARD: 刷卡支付
- OTHER: 其他

**响应示例**:
```json
{
    "code": 200,
    "message": "消费记录创建成功",
    "data": {
        "id": 1,
        "memberId": 1,
        "memberName": "张三",
        "barberId": 1,
        "barberName": "理发师李四",
        "originalAmount": 100.00,
        "discountAmount": 95.00,
        "actualAmount": 95.00,
        "barberCommission": 28.50,
        "paymentMethod": "BALANCE",
        "createdAt": "2024-01-01T10:00:00"
    }
}
```

### 5.2 获取消费记录列表

**GET** `/api/consumptions`

分页获取消费记录。

**查询参数**:
| 参数 | 类型 | 默认值 | 描述 |
|------|------|--------|------|
| memberId | Long | - | 会员ID（可选） |
| page | int | 0 | 页码 |
| size | int | 10 | 每页数量 |

### 5.3 获取理发师的消费记录

**GET** `/api/consumptions/barber/{barberId}`

分页获取指定理发师的消费记录。

### 5.4 匹配理发师

**POST** `/api/consumptions/match-barber`

根据发型选择匹配最适合的理发师。

**请求体**:
```json
{
    "hairstyleTypeId": 1,
    "hairstyleOptionIds": [1, 2, 3]
}
```

**响应示例**:
```json
{
    "code": 200,
    "message": "success",
    "data": [
        {
            "id": 1,
            "name": "首席理发师",
            "phone": "15100151001",
            "level": "CHIEF",
            "commissionRate": 0.50,
            "matchScore": 5
        },
        {
            "id": 2,
            "name": "高级理发师",
            "phone": "15100151002",
            "level": "SENIOR",
            "commissionRate": 0.35,
            "matchScore": 3
        }
    ]
}
```

---

## 6. 会员等级说明

| 等级 | 累计消费 | 折扣率 |
|------|----------|--------|
| NORMAL | 0 | 1.0 (无折扣) |
| SILVER | 500 | 0.95 (95折) |
| GOLD | 2000 | 0.9 (9折) |
| PLATINUM | 5000 | 0.85 (85折) |
| DIAMOND | 10000 | 0.8 (8折) |

---

## 7. 商家状态说明

| 状态 | 描述 |
|------|------|
| PENDING | 待审核 |
| APPROVED | 已审核通过 |
| REJECTED | 审核未通过 |
| ONLINE | 已上线 |
| OFFLINE | 已下线 |

---

## 8. 错误码说明

| 错误码 | 描述 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权 |
| 403 | 无权限 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

---

## 9. 快速开始

### 9.1 启动项目

```bash
cd saas
mvn spring-boot:run
```

### 9.2 访问Swagger文档

启动后访问: http://localhost:8080/swagger-ui/index.html

### 9.3 访问H2数据库控制台

访问: http://localhost:8080/h2-console

- JDBC URL: `jdbc:h2:mem:saasdb`
- User: `sa`
- Password: (空)

---

## 10. 完整业务流程示例

### 10.1 商家入驻流程

1. 调用 `POST /api/merchants/register` 提交注册申请
2. 管理员调用 `GET /api/merchants/pending` 查看待审核列表
3. 管理员调用 `POST /api/merchants/{id}/approve` 审核通过
4. 商家调用 `POST /api/merchants/login` 登录系统
5. 管理员调用 `POST /api/merchants/{id}/online` 上线商家

### 10.2 会员消费流程

1. 调用 `POST /api/members` 创建会员
2. 调用 `POST /api/members/recharge` 会员充值
3. 调用 `POST /api/barbers` 添加理发师
4. 调用 `POST /api/hairstyles/types` 添加发型类型
5. 调用 `POST /api/hairstyles/options` 添加发型选项
6. 调用 `POST /api/consumptions/match-barber` 匹配理发师
7. 调用 `POST /api/consumptions` 创建消费记录

---

## 11. 数据隔离说明

系统采用商家级数据隔离，每个商家只能访问和管理自己的数据：
- 会员数据按merchantId隔离
- 理发师数据按merchantId隔离
- 发型数据按merchantId隔离
- 消费记录按merchantId隔离

JWT Token中包含merchantId，系统自动进行数据隔离验证。

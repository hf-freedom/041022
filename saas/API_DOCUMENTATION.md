# 理发店SaaS平台 API接口文档

## 项目概述

理发店SaaS平台后端服务，提供完整的RESTful API接口，支持多商家入驻、会员管理、理发师管理、发型管理、智能匹配等核心功能。

## 基础信息

- **Base URL**: `http://localhost:8080/api`
- **Content-Type**: `application/json`
- **认证方式**: Bearer Token (JWT)

## 认证相关接口

### 1. 商家注册

**接口**: `POST /auth/register`

**描述**: 新商家申请入驻平台

**请求参数**:
```json
{
  "shopName": "理发店名称",
  "ownerName": "负责人姓名",
  "phone": "13800138000",
  "password": "123456",
  "email": "shop@example.com",
  "address": "店铺地址",
  "businessLicense": "营业执照号",
  "description": "店铺描述"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "注册成功，等待审核",
  "data": {
    "id": 1,
    "shopName": "理发店名称",
    "ownerName": "负责人姓名",
    "phone": "13800138000",
    "email": "shop@example.com",
    "address": "店铺地址",
    "businessLicense": "营业执照号",
    "description": "店铺描述",
    "status": "PENDING",
    "createdAt": "2024-01-01 12:00:00"
  }
}
```

### 2. 商家登录

**接口**: `POST /auth/login`

**描述**: 商家使用手机号和密码登录

**请求参数**:
```json
{
  "phone": "13800138000",
  "password": "123456"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "tokenType": "Bearer",
    "expiresIn": 86400,
    "merchant": {
      "id": 1,
      "shopName": "理发店名称",
      "phone": "13800138000",
      "status": "APPROVED"
    }
  }
}
```

### 3. 获取当前商家信息

**接口**: `GET /auth/me`

**描述**: 获取当前登录商家的详细信息

**请求头**:
```
Authorization: Bearer {token}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "shopName": "理发店名称",
    "ownerName": "负责人姓名",
    "phone": "13800138000",
    "status": "APPROVED"
  }
}
```

### 4. 更新商家信息

**接口**: `PUT /auth/me`

**描述**: 更新当前登录商家的信息

**请求头**:
```
Authorization: Bearer {token}
```

**请求参数**: 同注册接口

---

## 会员管理接口

### 1. 创建会员

**接口**: `POST /members`

**描述**: 录入新会员信息

**请求头**:
```
Authorization: Bearer {token}
```

**请求参数**:
```json
{
  "name": "会员姓名",
  "phone": "13900139000",
  "gender": "MALE",
  "birthday": "1990-01-01",
  "remark": "备注信息"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "会员创建成功",
  "data": {
    "id": 1,
    "name": "会员姓名",
    "phone": "13900139000",
    "gender": "MALE",
    "birthday": "1990-01-01",
    "balance": 0,
    "totalConsumption": 0,
    "totalRecharge": 0,
    "memberLevel": {
      "id": 1,
      "name": "普通会员",
      "discountRate": 1.00
    }
  }
}
```

### 2. 获取会员列表

**接口**: `GET /members`

**描述**: 分页获取会员列表

**请求头**:
```
Authorization: Bearer {token}
```

**查询参数**:
- `page`: 页码 (默认0)
- `size`: 每页大小 (默认20)
- `sort`: 排序字段

### 3. 搜索会员

**接口**: `GET /members/search?keyword={keyword}`

**描述**: 根据姓名或手机号搜索会员

### 4. 会员充值

**接口**: `POST /members/{id}/recharge`

**描述**: 为会员账户充值

**请求参数**:
```json
{
  "amount": 100.00,
  "remark": "首次充值"
}
```

### 5. 更新会员

**接口**: `PUT /members/{id}`

**描述**: 更新会员信息

### 6. 删除会员

**接口**: `DELETE /members/{id}`

**描述**: 删除会员（逻辑删除）

---

## 会员等级管理接口

### 1. 创建会员等级

**接口**: `POST /member-levels`

**请求参数**:
```json
{
  "name": "VIP会员",
  "level": 2,
  "minAmount": 1000,
  "discountRate": 0.85,
  "description": "消费满1000元可升级",
  "isDefault": false
}
```

### 2. 获取会员等级列表

**接口**: `GET /member-levels`

### 3. 更新会员等级

**接口**: `PUT /member-levels/{id}`

### 4. 删除会员等级

**接口**: `DELETE /member-levels/{id}`

---

## 理发师管理接口

### 1. 创建理发师

**接口**: `POST /barbers`

**请求参数**:
```json
{
  "name": "王师傅",
  "phone": "13600136000",
  "gender": "MALE",
  "birthday": "1985-05-01",
  "entryDate": "2020-01-01",
  "levelId": 1,
  "specialties": "剪发、烫发",
  "introduction": "10年经验高级理发师"
}
```

### 2. 获取理发师列表

**接口**: `GET /barbers`

### 3. 获取在职理发师

**接口**: `GET /barbers/active`

### 4. 更新理发师

**接口**: `PUT /barbers/{id}`

### 5. 删除理发师

**接口**: `DELETE /barbers/{id}`

---

## 理发师级别管理接口

### 1. 创建理发师级别

**接口**: `POST /barber-levels`

**请求参数**:
```json
{
  "name": "高级理发师",
  "level": 1,
  "commissionRate": 0.30,
  "description": "提成30%",
  "isDefault": true
}
```

### 2. 获取理发师级别列表

**接口**: `GET /barber-levels`

---

## 发型管理接口

### 1. 创建发型类型

**接口**: `POST /hairstyles/types`

**请求参数**:
```json
{
  "name": "男士剪发",
  "category": "CUT",
  "basePrice": 50.00,
  "durationMinutes": 30,
  "imageUrl": "http://example.com/image.jpg",
  "description": "基础男士剪发服务"
}
```

**分类说明**:
- `CUT`: 剪发
- `PERM`: 烫发
- `COLOR`: 染发
- `CARE`: 护理
- `STYLING`: 造型

### 2. 获取发型类型列表

**接口**: `GET /hairstyles/types`

### 3. 按分类获取发型

**接口**: `GET /hairstyles/types/category/{category}`

### 4. 创建发型细节

**接口**: `POST /hairstyles/types/{typeId}/details`

**请求参数**:
```json
{
  "name": "精剪",
  "additionalPrice": 20.00,
  "additionalMinutes": 10,
  "description": "精细修剪"
}
```

### 5. 获取发型细节列表

**接口**: `GET /hairstyles/types/{typeId}/details`

---

## 智能匹配接口

### 1. 匹配理发师

**接口**: `POST /matching/barbers`

**描述**: 根据选择的发型和细节匹配适合的理发师

**请求参数**:
```json
{
  "hairstyleTypeId": 1,
  "hairstyleDetailIds": [1, 2],
  "memberId": 1,
  "preferredGender": "MALE"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "barberId": 1,
      "barberName": "王师傅",
      "barberLevel": "高级理发师",
      "rating": 4.9,
      "orderCount": 500,
      "proficiency": "EXPERT",
      "basePrice": 50.00,
      "additionalPrice": 30.00,
      "totalPrice": 80.00,
      "discountRate": 0.85,
      "finalPrice": 68.00,
      "estimatedDuration": 50,
      "matchScore": 95.5,
      "recommended": true
    }
  ]
}
```

### 2. 计算价格

**接口**: `POST /matching/price`

**描述**: 根据选择的发型和细节计算价格

**请求参数**:
```json
{
  "hairstyleTypeId": 1,
  "hairstyleDetailIds": [1],
  "memberId": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "hairstyleTypeId": 1,
    "hairstyleName": "男士剪发",
    "basePrice": 50.00,
    "additionalPrice": 10.00,
    "totalPrice": 60.00,
    "discountRate": 0.90,
    "discountAmount": 6.00,
    "finalPrice": 54.00,
    "baseDuration": 30,
    "additionalDuration": 10,
    "totalDuration": 40,
    "selectedDetails": [
      {
        "id": 1,
        "name": "洗头",
        "additionalPrice": 10.00
      }
    ]
  }
}
```

---

## 订单管理接口

### 1. 创建订单

**接口**: `POST /orders`

**请求参数**:
```json
{
  "barberId": 1,
  "memberId": 1,
  "items": [
    {
      "hairstyleTypeId": 1,
      "hairstyleDetailIds": [1],
      "quantity": 1
    }
  ],
  "appointmentTime": "2024-01-01 14:00:00",
  "remark": "预约备注"
}
```

### 2. 获取订单列表

**接口**: `GET /orders`

### 3. 按状态获取订单

**接口**: `GET /orders/status/{status}`

**状态说明**:
- `PENDING`: 待服务
- `IN_PROGRESS`: 服务中
- `COMPLETED`: 已完成
- `CANCELLED`: 已取消

### 4. 开始订单

**接口**: `POST /orders/{id}/start`

### 5. 完成订单

**接口**: `POST /orders/{id}/complete`

### 6. 取消订单

**接口**: `POST /orders/{id}/cancel?reason={reason}`

---

## 理发师发型关联接口

### 1. 添加理发师发型

**接口**: `POST /barber-hairstyles`

**请求参数**:
```json
{
  "barberId": 1,
  "hairstyleTypeId": 1
}
```

### 2. 获取理发师的发型

**接口**: `GET /barber-hairstyles/barber/{barberId}`

### 3. 更新熟练度

**接口**: `PUT /barber-hairstyles/{id}?proficiency={proficiency}`

**熟练度说明**:
- `BEGINNER`: 初级
- `MEDIUM`: 中级
- `ADVANCED`: 高级
- `EXPERT`: 专家

---

## 商家管理接口（管理员）

### 1. 获取所有商家

**接口**: `GET /admin/merchants`

### 2. 按状态获取商家

**接口**: `GET /admin/merchants/status/{status}`

### 3. 审核商家

**接口**: `POST /admin/merchants/{id}/audit`

**请求参数**:
```json
{
  "status": "APPROVED",
  "auditRemark": "审核通过"
}
```

### 4. 禁用商家

**接口**: `POST /admin/merchants/{id}/disable`

### 5. 启用商家

**接口**: `POST /admin/merchants/{id}/enable`

---

## 错误码说明

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 未授权，Token无效或过期 |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 500 | 服务器内部错误 |

## 数据模型

### 商家状态
- `PENDING`: 待审核
- `APPROVED`: 已通过
- `REJECTED`: 已拒绝
- `DISABLED`: 已禁用

### 订单状态
- `PENDING`: 待服务
- `IN_PROGRESS`: 服务中
- `COMPLETED`: 已完成
- `CANCELLED`: 已取消

### 发型分类
- `CUT`: 剪发
- `PERM`: 烫发
- `COLOR`: 染发
- `CARE`: 护理
- `STYLING`: 造型

### 熟练度
- `BEGINNER`: 初级
- `MEDIUM`: 中级
- `ADVANCED`: 高级
- `EXPERT`: 专家

---

## 测试说明

项目包含完整的单元测试和集成测试，运行测试命令：

```bash
mvn test
```

测试覆盖：
- 商家注册、登录、审核流程
- 会员CRUD、充值功能
- 理发师管理
- 智能匹配算法
- 订单生命周期

---

## 技术栈

- Java 8
- Spring Boot 2.7.18
- Spring Security + JWT
- Spring Data JPA
- MySQL 8.0
- Maven
- Lombok
- Swagger/OpenAPI

---

## 启动项目

1. 配置数据库连接（application.yml）
2. 运行命令：
```bash
mvn spring-boot:run
```
3. 访问Swagger文档：http://localhost:8080/api/swagger-ui.html

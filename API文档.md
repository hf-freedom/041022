# 理发店SaaS平台API接口文档

## 项目信息
- **项目名称**: saas
- **版本**: 1.0.0
- **基础路径**: /api
- **端口**: 8080

## 认证说明
除注册、登录接口外，其他所有接口都需要在请求头中携带token：
```
Authorization: Bearer {token}
```

## 通用响应格式
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {}
}
```

---

## 1. 商家模块

### 1.1 商家注册申请
- **接口**: POST /api/merchant/register
- **说明**: 新商家提交入驻申请
- **请求体**:
```json
{
  "shopName": "时尚剪艺",
  "contactName": "张三",
  "phone": "13800138000",
  "email": "test@example.com",
  "address": "北京市朝阳区",
  "username": "test",
  "password": "123456",
  "businessLicense": "xxx",
  "idCardFront": "xxx",
  "idCardBack": "xxx"
}
```

### 1.2 商家登录
- **接口**: POST /api/merchant/login
- **说明**: 商家登录获取token
- **请求体**:
```json
{
  "username": "admin",
  "password": "admin"
}
```
- **响应**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9...",
    "merchantId": 1,
    "shopName": "时尚剪艺旗舰店"
  }
}
```

### 1.3 获取商家信息
- **接口**: GET /api/merchant/{id}
- **说明**: 根据ID获取商家详情

---

## 2. 管理员-商家管理模块

### 2.1 获取商家列表
- **接口**: GET /api/admin/merchant/list
- **参数**:
  - status: 状态（0待审核 1通过 2拒绝）可选
- **说明**: 管理员查看所有商家申请

### 2.2 审核商家
- **接口**: POST /api/admin/merchant/audit
- **请求体**:
```json
{
  "id": 1,
  "status": 1,
  "auditRemark": "资料齐全，审核通过"
}
```
- **说明**: status=1审核通过，status=2审核拒绝

### 2.3 商家上下线
- **接口**: POST /api/admin/merchant/toggle-online
- **请求体**:
```json
{
  "id": 1,
  "online": 1
}
```
- **说明**: online=1上线，online=0下线

---

## 3. 会员模块

### 3.1 录入会员
- **接口**: POST /api/member/add
- **请求体**:
```json
{
  "name": "李四",
  "phone": "13900139000",
  "gender": "男",
  "age": 28,
  "balance": 0
}
```

### 3.2 绑定手机号
- **接口**: POST /api/member/bind-phone
- **请求体**:
```json
{
  "memberId": 1,
  "phone": "13900139001"
}
```

### 3.3 会员充值
- **接口**: POST /api/member/recharge
- **请求体**:
```json
{
  "memberId": 1,
  "amount": 500,
  "rechargeType": "微信",
  "remark": "首次充值"
}
```

### 3.4 会员消费（余额扣款）
- **接口**: POST /api/member/consume
- **请求体**:
```json
{
  "memberId": 1,
  "amount": 50
}
```

### 3.5 获取会员列表
- **接口**: GET /api/member/list
- **参数**:
  - keyword: 姓名/手机号搜索，可选
- **说明**: 支持按姓名或手机号模糊搜索

### 3.6 获取会员详情
- **接口**: GET /api/member/{id}

### 3.7 获取会员等级列表
- **接口**: GET /api/member/levels
- **说明**: 自动初始化4个等级：普通会员(不打折)、银卡(9折)、金卡(85折)、钻石(8折)

---

## 4. 理发师模块

### 4.1 添加理发师
- **接口**: POST /api/barber/add
- **请求体**:
```json
{
  "name": "王师傅",
  "phone": "13700137000",
  "avatar": "xxx",
  "levelId": 2,
  "skillTags": "剪发,烫发,染发",
  "status": 1
}
```

### 4.2 修改理发师
- **接口**: POST /api/barber/update/{id}
- **请求体**: 同添加理发师

### 4.3 匹配理发师
- **接口**: POST /api/barber/match
- **请求体**:
```json
{
  "hairstyleId": 1,
  "detailOptions": ["染发", "柔顺"]
}
```
- **说明**: 根据发型和细节选项，按技能匹配度排序推荐理发师

### 4.4 获取理发师列表
- **接口**: GET /api/barber/list
- **参数**:
  - keyword: 姓名/手机号搜索，可选

### 4.5 获取理发师详情
- **接口**: GET /api/barber/{id}

### 4.6 获取理发师级别列表
- **接口**: GET /api/barber/levels
- **说明**: 自动初始化4个级别：助理(10%)、发型师(20%)、高级发型师(30%)、技术总监(40%)

---

## 5. 发型模块

### 5.1 添加发型
- **接口**: POST /api/hairstyle/add
- **请求体**:
```json
{
  "name": "时尚短发",
  "category": "短发",
  "image": "xxx",
  "description": "清爽利落",
  "price": 68,
  "duration": 30,
  "suitableGender": "男女通用",
  "suitableAge": "18-40",
  "detailOptions": "刘海,鬓角",
  "skillTags": "剪发,精修",
  "status": 1
}
```

### 5.2 修改发型
- **接口**: POST /api/hairstyle/update/{id}
- **请求体**: 同添加发型

### 5.3 获取发型列表
- **接口**: GET /api/hairstyle/list
- **参数**:
  - category: 分类，可选
  - keyword: 名称搜索，可选

### 5.4 获取所有分类
- **接口**: GET /api/hairstyle/categories
- **说明**: 获取该商家下所有发型分类

### 5.5 获取发型详情
- **接口**: GET /api/hairstyle/{id}

---

## 6. 订单模块

### 6.1 创建订单
- **接口**: POST /api/order/create
- **请求体**:
```json
{
  "memberId": 1,
  "barberId": 1,
  "hairstyleId": 1,
  "detailOptions": ["刘海", "鬓角"],
  "payType": 1,
  "useBalance": 1,
  "remark": "老顾客"
}
```
- **说明**:
  - memberId为空则为散客，不享受会员折扣
  - useBalance=1时使用余额支付，自动扣款
  - 自动计算会员折扣
  - 自动计算理发师提成

### 6.2 获取订单列表
- **接口**: GET /api/order/list
- **参数**:
  - keyword: 搜索，可选
  - status: 状态，可选

### 6.3 获取订单详情
- **接口**: GET /api/order/{id}

---

## 业务逻辑闭环说明

### 数据隔离
- 所有业务数据通过拦截器自动加上商家ID过滤
- 商家只能看到自己的数据
- 通过JWT token中的merchantId实现

### 会员等级自动升级
- 创建会员时自动初始化等级数据
- 充值/消费后自动计算累计金额
- 根据累计消费金额自动匹配对应的会员等级和折扣

### 理发师提成自动计算
- 创建订单时，根据理发师级别自动计算提成比例
- 自动累计理发师接单数量和总提成

### 智能匹配理发师
- 根据选择的发型所需技能标签
- 对理发师进行技能匹配度打分排序
- 返回最匹配的理发师列表

### 默认数据初始化
- 商家首次使用时，自动初始化：
  - 4个会员等级及对应折扣
  - 4个理发师级别及对应提成比例

---

## 测试账号
- 用户名: admin
- 密码: admin

## 数据库表说明
1. t_merchant - 商家表
2. t_member - 会员表
3. t_member_level - 会员等级表
4. t_barber - 理发师表
5. t_barber_level - 理发师级别表
6. t_hairstyle - 发型表
7. t_recharge_record - 充值记录表
8. t_order - 订单表

---

## 测试说明

### 测试类列表
1. **MerchantServiceTest.java** - 商家模块测试
   - 商家注册测试
   - 商家登录测试
   - JWT Token生成与验证
   - 商家审核与上下线测试

2. **MemberServiceTest.java** - 会员模块测试
   - 添加会员测试
   - 会员充值测试
   - 会员消费测试
   - 会员等级自动升级测试
   - 手机号绑定测试

3. **BarberServiceTest.java** - 理发师模块测试
   - 添加/修改理发师测试
   - 技能匹配理发师测试
   - 理发师级别与提成比例测试

4. **HairstyleServiceTest.java** - 发型模块测试
   - 添加/修改发型测试
   - 发型分类管理测试

5. **OrderServiceTest.java** - 订单模块测试
   - 散客下单测试
   - 会员下单（自动折扣）测试
   - 余额支付测试
   - 理发师提成自动计算测试

6. **BusinessIntegrationTest.java** - 完整业务流程集成测试（8个步骤）
   - 步骤1：录入会员
   - 步骤2：会员充值
   - 步骤3：添加理发师
   - 步骤4：添加发型
   - 步骤5：匹配理发师
   - 步骤6：创建订单（余额支付）
   - 步骤7：验证会员等级升级
   - 步骤8：完整流程验证

### 运行测试
```bash
# 运行所有测试
mvn test

# 运行单个测试类
mvn test -Dtest="MerchantServiceTest"

# 运行集成测试
mvn test -Dtest="BusinessIntegrationTest"
```

---

## 技术栈
- SpringBoot 2.7.18
- Java 8
- MyBatis-Plus 3.5.3.1
- MySQL 8.0
- JWT认证
- Druid连接池
- JUnit 5 单元测试

CREATE DATABASE IF NOT EXISTS saas_barbershop DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE saas_barbershop;

CREATE TABLE t_merchant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '商家ID',
    shop_name VARCHAR(100) NOT NULL COMMENT '店铺名称',
    contact_name VARCHAR(50) NOT NULL COMMENT '联系人',
    phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    email VARCHAR(100) COMMENT '邮箱',
    address VARCHAR(255) COMMENT '地址',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    business_lotse VARCHAR(500) COMMENT '营业执照',
    id_card_front VARCHAR(500) COMMENT '身份证正面',
    id_card_back VARCHAR(500) COMMENT '身份证反面',
    status TINYINT DEFAULT 0 COMMENT '状态：0待审核 1审核通过 2审核拒绝',
    audit_remark VARCHAR(500) COMMENT '审核备注',
    online TINYINT DEFAULT 0 COMMENT '是否上线：0否 1是',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商家表';

CREATE TABLE t_member_level (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '等级ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    name VARCHAR(50) NOT NULL COMMENT '等级名称',
    discount DECIMAL(3,2) NOT NULL COMMENT '折扣率',
    min_amount DECIMAL(10,2) DEFAULT 0 COMMENT '最低消费金额',
    min_count INT DEFAULT 0 COMMENT '最低消费次数',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_merchant_id(merchant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员等级表';

CREATE TABLE t_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '会员ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    gender VARCHAR(10) COMMENT '性别',
    age INT COMMENT '年龄',
    balance DECIMAL(10,2) DEFAULT 0 COMMENT '余额',
    total_consume_count INT DEFAULT 0 COMMENT '累计消费次数',
    total_consume_amount DECIMAL(10,2) DEFAULT 0 COMMENT '累计消费金额',
    level_id INT COMMENT '会员等级ID',
    level_name VARCHAR(50) COMMENT '会员等级名称',
    discount DECIMAL(3,2) DEFAULT 1.00 COMMENT '折扣率',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    UNIQUE KEY uk_merchant_phone(merchant_id, phone),
    INDEX idx_merchant_id(merchant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会员表';

CREATE TABLE t_barber_level (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '级别ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    name VARCHAR(50) NOT NULL COMMENT '级别名称',
    commission_rate DECIMAL(3,2) NOT NULL COMMENT '提成比例',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_merchant_id(merchant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='理发师级别表';

CREATE TABLE t_barber (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '理发师ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    name VARCHAR(50) NOT NULL COMMENT '姓名',
    phone VARCHAR(20) NOT NULL COMMENT '手机号',
    avatar VARCHAR(500) COMMENT '头像',
    level_id INT COMMENT '级别ID',
    level_name VARCHAR(50) COMMENT '级别名称',
    commission_rate DECIMAL(3,2) COMMENT '提成比例',
    skill_tags VARCHAR(500) COMMENT '技能标签，逗号分隔',
    order_count INT DEFAULT 0 COMMENT '接单数量',
    total_commission DECIMAL(10,2) DEFAULT 0 COMMENT '累计提成',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_merchant_id(merchant_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='理发师表';

CREATE TABLE t_hairstyle (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '发型ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    name VARCHAR(100) NOT NULL COMMENT '发型名称',
    category VARCHAR(50) COMMENT '分类',
    image VARCHAR(500) COMMENT '图片',
    description VARCHAR(500) COMMENT '描述',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    duration INT COMMENT '时长(分钟)',
    suitable_gender VARCHAR(10) COMMENT '适用性别',
    suitable_age VARCHAR(20) COMMENT '适用年龄',
    detail_options VARCHAR(1000) COMMENT '细节选项，JSON格式',
    skill_tags VARCHAR(500) COMMENT '技能标签，逗号分隔',
    status TINYINT DEFAULT 1 COMMENT '状态：0禁用 1启用',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_merchant_id(merchant_id),
    INDEX idx_category(category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='发型表';

CREATE TABLE t_recharge_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    member_id BIGINT NOT NULL COMMENT '会员ID',
    member_name VARCHAR(50) COMMENT '会员姓名',
    member_phone VARCHAR(20) COMMENT '会员手机号',
    amount DECIMAL(10,2) NOT NULL COMMENT '充值金额',
    before_balance DECIMAL(10,2) COMMENT '充值前余额',
    after_balance DECIMAL(10,2) COMMENT '充值后余额',
    recharge_type VARCHAR(50) COMMENT '充值方式',
    remark VARCHAR(500) COMMENT '备注',
    operator_id BIGINT COMMENT '操作人ID',
    operator_name VARCHAR(50) COMMENT '操作人姓名',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_merchant_id(merchant_id),
    INDEX idx_member_id(member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='充值记录表';

CREATE TABLE t_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '订单ID',
    merchant_id BIGINT NOT NULL COMMENT '商家ID',
    order_no VARCHAR(50) NOT NULL UNIQUE COMMENT '订单号',
    member_id BIGINT COMMENT '会员ID',
    member_name VARCHAR(50) COMMENT '会员姓名',
    member_phone VARCHAR(20) COMMENT '会员手机号',
    barber_id BIGINT NOT NULL COMMENT '理发师ID',
    barber_name VARCHAR(50) COMMENT '理发师姓名',
    hairstyle_id BIGINT NOT NULL COMMENT '发型ID',
    hairstyle_name VARCHAR(100) COMMENT '发型名称',
    detail_options VARCHAR(500) COMMENT '细节选项',
    original_price DECIMAL(10,2) COMMENT '原价',
    discount DECIMAL(3,2) COMMENT '折扣率',
    actual_price DECIMAL(10,2) COMMENT '实付金额',
    pay_type INT COMMENT '支付方式：1现金 2微信 3支付宝 4银行卡',
    use_balance TINYINT DEFAULT 0 COMMENT '是否使用余额：0否 1是',
    balance_amount DECIMAL(10,2) DEFAULT 0 COMMENT '余额支付金额',
    status TINYINT DEFAULT 1 COMMENT '状态：1已完成 2已取消',
    commission_rate DECIMAL(3,2) COMMENT '提成比例',
    commission_amount DECIMAL(10,2) COMMENT '提成金额',
    remark VARCHAR(500) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除',
    INDEX idx_merchant_id(merchant_id),
    INDEX idx_order_no(order_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

INSERT INTO t_merchant (shop_name, contact_name, phone, address, username, password, status, audit_remark, online) 
VALUES ('时尚剪艺旗舰店', '张店长', '13800138001', '北京市朝阳区建国路88号', 'admin', '21232f297a57a5a743894a0e4a801fc3', 1, '审核通过', 1);

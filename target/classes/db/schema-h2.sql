CREATE TABLE IF NOT EXISTS t_merchant (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shop_name VARCHAR(100) NOT NULL,
    contact_name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(255),
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    business_license VARCHAR(500),
    id_card_front VARCHAR(500),
    id_card_back VARCHAR(500),
    status TINYINT DEFAULT 0,
    audit_remark VARCHAR(500),
    online TINYINT DEFAULT 0,
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS t_member_level (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    discount DECIMAL(3,2) NOT NULL,
    min_amount DECIMAL(10,2) DEFAULT 0,
    min_count INT DEFAULT 0,
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS t_member (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    gender VARCHAR(10),
    age INT,
    balance DECIMAL(10,2) DEFAULT 0,
    total_consume_count INT DEFAULT 0,
    total_consume_amount DECIMAL(10,2) DEFAULT 0,
    level_id INT,
    level_name VARCHAR(50),
    discount DECIMAL(3,2) DEFAULT 1.00,
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS t_barber_level (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    commission_rate DECIMAL(3,2) NOT NULL,
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS t_barber (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    avatar VARCHAR(500),
    level_id INT,
    level_name VARCHAR(50),
    commission_rate DECIMAL(3,2),
    skill_tags VARCHAR(500),
    order_count INT DEFAULT 0,
    total_commission DECIMAL(10,2) DEFAULT 0,
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS t_hairstyle (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    category VARCHAR(50),
    image VARCHAR(500),
    description VARCHAR(500),
    price DECIMAL(10,2) NOT NULL,
    duration INT,
    suitable_gender VARCHAR(10),
    suitable_age VARCHAR(20),
    detail_options VARCHAR(1000),
    skill_tags VARCHAR(500),
    status TINYINT DEFAULT 1,
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS t_recharge_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    member_name VARCHAR(50),
    member_phone VARCHAR(20),
    amount DECIMAL(10,2) NOT NULL,
    before_balance DECIMAL(10,2),
    after_balance DECIMAL(10,2),
    recharge_type VARCHAR(50),
    remark VARCHAR(500),
    operator_id BIGINT,
    operator_name VARCHAR(50),
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

CREATE TABLE IF NOT EXISTS t_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    merchant_id BIGINT NOT NULL,
    order_no VARCHAR(50) NOT NULL UNIQUE,
    member_id BIGINT,
    member_name VARCHAR(50),
    member_phone VARCHAR(20),
    barber_id BIGINT NOT NULL,
    barber_name VARCHAR(50),
    hairstyle_id BIGINT NOT NULL,
    hairstyle_name VARCHAR(100),
    detail_options VARCHAR(500),
    original_price DECIMAL(10,2),
    discount DECIMAL(3,2),
    actual_price DECIMAL(10,2),
    pay_type INT,
    use_balance TINYINT DEFAULT 0,
    balance_amount DECIMAL(10,2) DEFAULT 0,
    status TINYINT DEFAULT 1,
    commission_rate DECIMAL(3,2),
    commission_amount DECIMAL(10,2),
    remark VARCHAR(500),
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    deleted TINYINT DEFAULT 0
);

INSERT INTO t_merchant (shop_name, contact_name, phone, address, username, password, status, audit_remark, online) 
VALUES ('时尚剪艺旗舰店', '张店长', '13800138001', '北京市朝阳区建国路88号', 'admin', '21232f297a57a5a743894a0e4a801fc3', 1, '审核通过', 1);

INSERT INTO t_member_level (merchant_id, name, discount, min_amount) VALUES
(1, '普通会员', 1.00, 0),
(1, '银卡会员', 0.90, 500),
(1, '金卡会员', 0.85, 2000),
(1, '钻石会员', 0.80, 5000);

INSERT INTO t_barber_level (merchant_id, name, commission_rate) VALUES
(1, '助理', 0.10),
(1, '发型师', 0.20),
(1, '高级发型师', 0.30),
(1, '技术总监', 0.40);

INSERT INTO t_member (merchant_id, name, phone, gender, age, balance, total_consume_count, total_consume_amount, level_id, level_name, discount) VALUES
(1, '张三', '13900139001', '男', 28, 500.00, 5, 680.00, 2, '银卡会员', 0.90),
(1, '李四', '13900139002', '女', 25, 1200.00, 12, 1580.00, 3, '金卡会员', 0.85),
(1, '王五', '13900139003', '男', 32, 80.00, 2, 136.00, 1, '普通会员', 1.00);

INSERT INTO t_barber (merchant_id, name, phone, level_id, level_name, commission_rate, skill_tags, order_count, total_commission, status) VALUES
(1, '王师傅', '13700137001', 4, '技术总监', 0.40, '剪发,烫发,染发,精修', 128, 6580.00, 1),
(1, '李师傅', '13700137002', 3, '高级发型师', 0.30, '剪发,烫发,护理', 96, 3260.00, 1),
(1, '张师傅', '13700137003', 2, '发型师', 0.20, '剪发,刘海', 65, 1680.00, 1),
(1, '小赵', '13700137004', 1, '助理', 0.10, '洗头,护理', 32, 520.00, 1);

INSERT INTO t_hairstyle (merchant_id, name, category, price, duration, detail_options, skill_tags, status) VALUES
(1, '时尚短发', '短发', 68.00, 30, '刘海,鬓角,碎发', '剪发,精修', 1),
(1, '商务短发', '短发', 58.00, 25, '刘海,渐变', '剪发', 1),
(1, '梨花烫', '烫发', 198.00, 90, '刘海,柔顺,护理', '烫发,护理', 1),
(1, '羊毛卷', '烫发', 268.00, 120, '刘海,护理', '烫发', 1),
(1, '自然黑', '染发', 168.00, 60, '护理', '染发,护理', 1),
(1, '亚麻棕', '染发', 198.00, 60, '护理', '染发', 1),
(1, '男士碎盖', '短发', 88.00, 40, '刘海,渐变,鬓角', '剪发,精修', 1),
(1, '女士长发', '长发', 128.00, 60, '刘海,发尾,护理', '剪发,护理', 1);

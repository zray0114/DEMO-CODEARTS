-- 菜单表
CREATE TABLE IF NOT EXISTS menus (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL COMMENT '菜单名称',
    path VARCHAR(100) COMMENT '路由路径',
    icon VARCHAR(50) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    parent_id BIGINT DEFAULT 0 COMMENT '父菜单ID',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜单表';

-- 水费记录表
CREATE TABLE IF NOT EXISTS water_fees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_date DATE NOT NULL COMMENT '缴费日期',
    amount DECIMAL(10,2) NOT NULL COMMENT '缴费金额',
    images LONGBLOB COMMENT '图片（二进制）',
    remark TEXT COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='水费记录表';

-- 电费记录表
CREATE TABLE IF NOT EXISTS electricity_fees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_date DATE NOT NULL COMMENT '缴费日期',
    amount DECIMAL(10,2) NOT NULL COMMENT '缴费金额',
    images LONGBLOB COMMENT '图片（二进制）',
    remark TEXT COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='电费记录表';

-- 天然气费记录表
CREATE TABLE IF NOT EXISTS gas_fees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_date DATE NOT NULL COMMENT '缴费日期',
    amount DECIMAL(10,2) NOT NULL COMMENT '缴费金额',
    images LONGBLOB COMMENT '图片（二进制）',
    remark TEXT COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='天然气费记录表';

-- 物业费记录表
CREATE TABLE IF NOT EXISTS property_fees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    payment_date DATE NOT NULL COMMENT '缴费日期',
    amount DECIMAL(10,2) NOT NULL COMMENT '缴费金额',
    images LONGBLOB COMMENT '图片（二进制）',
    remark TEXT COMMENT '备注',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物业费记录表';

-- 租户表
CREATE TABLE IF NOT EXISTS tenants (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    check_in_date DATE NOT NULL COMMENT '入住日期',
    rent_amount DECIMAL(10,2) NOT NULL COMMENT '房租金额',
    deposit_amount DECIMAL(10,2) NOT NULL COMMENT '押金金额',
    status TINYINT DEFAULT 1 COMMENT '状态：1-在住，0-已退租',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='租户表';

-- 费用分摊表
CREATE TABLE IF NOT EXISTS fee_shares (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tenant_id BIGINT NOT NULL COMMENT '租户ID',
    share_month VARCHAR(7) NOT NULL COMMENT '分摊月份（YYYY-MM）',
    water_fee DECIMAL(10,2) DEFAULT 0 COMMENT '水费分摊',
    electricity_fee DECIMAL(10,2) DEFAULT 0 COMMENT '电费分摊',
    gas_fee DECIMAL(10,2) DEFAULT 0 COMMENT '天然气费分摊',
    property_fee DECIMAL(10,2) DEFAULT 0 COMMENT '物业费分摊',
    total_fee DECIMAL(10,2) DEFAULT 0 COMMENT '总费用',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (tenant_id) REFERENCES tenants(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用分摊表';

-- 插入默认菜单数据
INSERT INTO menus (name, path, icon, sort_order) VALUES
('用户管理', '/users', 'User', 1),
('水费管理', '/water-fee', 'Coffee', 2),
('电费管理', '/electricity-fee', 'Lightning', 3),
('天然气费管理', '/gas-fee', 'Sunrise', 4),
('物业费管理', '/property-fee', 'OfficeBuilding', 5),
('租户管理', '/tenant', 'House', 6),
('费用分摊', '/fee-share', 'Document', 7),
('数据统计', '/statistics', 'DataAnalysis', 8);

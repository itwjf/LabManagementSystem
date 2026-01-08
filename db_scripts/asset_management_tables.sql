-- 1. 实训室信息表
CREATE TABLE `lab_info` (
  `lab_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '实训室编号',
  `lab_name` VARCHAR(100) NOT NULL COMMENT '实训室名称',
  `location` VARCHAR(200) DEFAULT NULL COMMENT '地理位置',
  `capacity` INT DEFAULT NULL COMMENT '容纳人数',
  `manager_id` BIGINT DEFAULT NULL COMMENT '管理员ID（关联user.id）',
  `manager_name` VARCHAR(50) DEFAULT NULL COMMENT '冗余：管理员姓名',
  `contact` VARCHAR(20) DEFAULT NULL COMMENT '联系电话',
  `status` VARCHAR(20) NOT NULL DEFAULT '正常使用' COMMENT '实训室状态（正常使用/维护中/已停用等）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`lab_id`),
  UNIQUE KEY `uk_lab_name` (`lab_name`),
  KEY `idx_manager_id` (`manager_id`),
  CONSTRAINT `fk_lab_info_manager` FOREIGN KEY (`manager_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实训室信息表';

-- 2. 资产类型表
CREATE TABLE `asset_type` (
  `type_id` INT NOT NULL AUTO_INCREMENT COMMENT '资产类型ID',
  `type_name` VARCHAR(50) NOT NULL COMMENT '资产类型名称（设备类/家具类/耗材类等）',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '类型描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`type_id`),
  UNIQUE KEY `uk_type_name` (`type_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产类型表';

-- 3. 固定资产表
CREATE TABLE `asset` (
  `asset_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '资产编号',
  `asset_name` VARCHAR(100) NOT NULL COMMENT '资产名称',
  `type_id` INT NOT NULL COMMENT '资产类型ID',
  `spec_model` VARCHAR(100) DEFAULT NULL COMMENT '规格型号',
  `unit` VARCHAR(20) DEFAULT NULL COMMENT '单位',
  `unit_price` DECIMAL(10,2) NOT NULL COMMENT '单价',
  `total_quantity` INT NOT NULL COMMENT '总数量',
  `available_quantity` INT NOT NULL COMMENT '可用数量',
  `total_value` DECIMAL(12,2) NOT NULL COMMENT '总价值',
  `purchase_date` DATE DEFAULT NULL COMMENT '购置日期',
  `purchaser_id` BIGINT DEFAULT NULL COMMENT '采购人ID（关联user.id）',
  `purchaser_name` VARCHAR(50) DEFAULT NULL COMMENT '冗余：采购人姓名',
  `supplier` VARCHAR(100) DEFAULT NULL COMMENT '供应商',
  `status` VARCHAR(20) NOT NULL DEFAULT '正常使用' COMMENT '资产状态（正常使用/出库待处置/已报废等）',
  `lab_id` BIGINT NOT NULL COMMENT '所属实训室ID',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`asset_id`),
  KEY `idx_type_id` (`type_id`),
  KEY `idx_lab_id` (`lab_id`),
  KEY `idx_purchaser_id` (`purchaser_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_asset_type` FOREIGN KEY (`type_id`) REFERENCES `asset_type` (`type_id`),
  CONSTRAINT `fk_asset_lab` FOREIGN KEY (`lab_id`) REFERENCES `lab_info` (`lab_id`),
  CONSTRAINT `fk_asset_purchaser` FOREIGN KEY (`purchaser_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='固定资产表';

-- 4. 资产入库记录表
CREATE TABLE `asset_in_record` (
  `record_id` INT NOT NULL AUTO_INCREMENT COMMENT '入库记录ID',
  `lab_id` BIGINT NOT NULL COMMENT '实训室ID',
  `asset_id` BIGINT NOT NULL COMMENT '资产编号',
  `quantity` INT NOT NULL COMMENT '入库数量',
  `unit_price` DECIMAL(10,2) NOT NULL COMMENT '入库单价',
  `total_amount` DECIMAL(12,2) NOT NULL COMMENT '入库总金额',
  `purchase_date` DATE DEFAULT NULL COMMENT '购置日期',
  `purchaser_id` BIGINT DEFAULT NULL COMMENT '采购人ID（关联user.id）',
  `purchaser_name` VARCHAR(50) DEFAULT NULL COMMENT '冗余：采购人姓名',
  `handler_id` BIGINT NOT NULL COMMENT '入库经办人ID（关联user.id）',
  `handler_name` VARCHAR(50) NOT NULL COMMENT '冗余：入库经办人姓名',
  `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
  `in_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '入库日期',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`record_id`),
  KEY `idx_lab_id` (`lab_id`),
  KEY `idx_asset_id` (`asset_id`),
  KEY `idx_in_date` (`in_date`),
  KEY `idx_purchaser_id` (`purchaser_id`),
  KEY `idx_handler_id` (`handler_id`),
  CONSTRAINT `fk_in_record_lab` FOREIGN KEY (`lab_id`) REFERENCES `lab_info` (`lab_id`),
  CONSTRAINT `fk_in_record_asset` FOREIGN KEY (`asset_id`) REFERENCES `asset` (`asset_id`),
  CONSTRAINT `fk_in_record_purchaser` FOREIGN KEY (`purchaser_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_in_record_handler` FOREIGN KEY (`handler_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产入库记录表';

-- 5. 资产出库记录表
CREATE TABLE `asset_out_record` (
  `record_id` INT NOT NULL AUTO_INCREMENT COMMENT '出库记录ID',
  `lab_id` BIGINT NOT NULL COMMENT '实训室ID',
  `asset_id` BIGINT NOT NULL COMMENT '资产编号',
  `quantity` INT NOT NULL COMMENT '出库数量',
  `out_reason` VARCHAR(50) NOT NULL COMMENT '出库原因（报废/调拨/损坏处置等）',
  `out_date` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '出库日期',
  `handler_id` BIGINT NOT NULL COMMENT '出库经办人ID（关联user.id）',
  `handler_name` VARCHAR(50) NOT NULL COMMENT '冗余：出库经办人姓名',
  `approver_id` BIGINT DEFAULT NULL COMMENT '审批人ID（关联user.id）',
  `approver_name` VARCHAR(50) DEFAULT NULL COMMENT '冗余：审批人姓名',
  `approval_status` VARCHAR(20) DEFAULT '待审批' COMMENT '审批状态（待审批/已审批/已拒绝）',
  `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  KEY `idx_lab_id` (`lab_id`),
  KEY `idx_asset_id` (`asset_id`),
  KEY `idx_out_date` (`out_date`),
  KEY `idx_approval_status` (`approval_status`),
  KEY `idx_handler_id` (`handler_id`),
  KEY `idx_approver_id` (`approver_id`),
  CONSTRAINT `fk_out_record_lab` FOREIGN KEY (`lab_id`) REFERENCES `lab_info` (`lab_id`),
  CONSTRAINT `fk_out_record_asset` FOREIGN KEY (`asset_id`) REFERENCES `asset` (`asset_id`),
  CONSTRAINT `fk_out_record_handler` FOREIGN KEY (`handler_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_out_record_approver` FOREIGN KEY (`approver_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资产出库记录表';

-- 6. 固定资产台账视图（用于快速查询各实训室资产台账）
CREATE VIEW `asset_account` AS
SELECT
  a.asset_id,
  a.asset_name,
  t.type_name,
  a.spec_model,
  a.unit,
  a.unit_price,
  a.total_quantity,
  a.available_quantity,
  a.total_value,
  a.purchase_date,
  a.status,
  l.lab_name,
  a.update_time AS last_update_time
FROM
  asset a
JOIN asset_type t ON a.type_id = t.type_id
JOIN lab_info l ON a.lab_id = l.lab_id;

-- 7. 固定资产变动统计视图（用于看板数据展示）
CREATE VIEW `asset_change_stat` AS
SELECT
  l.lab_name,
  t.type_name,
  SUM(CASE WHEN r.in_date >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY) THEN r.quantity ELSE 0 END) AS month_add_quantity,
  SUM(CASE WHEN r.in_date >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY) THEN r.total_amount ELSE 0 END) AS month_add_value,
  SUM(CASE WHEN o.out_date >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY) THEN o.quantity ELSE 0 END) AS month_reduce_quantity,
  SUM(CASE WHEN o.out_date >= DATE_SUB(CURRENT_DATE, INTERVAL 30 DAY) THEN (o.quantity * a.unit_price) ELSE 0 END) AS month_reduce_value
FROM
  lab_info l
LEFT JOIN asset a ON l.lab_id = a.lab_id
LEFT JOIN asset_type t ON a.type_id = t.type_id
LEFT JOIN asset_in_record r ON a.asset_id = r.asset_id
LEFT JOIN asset_out_record o ON a.asset_id = o.asset_id
GROUP BY
  l.lab_name, t.type_name;
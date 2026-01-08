-- 实训室管理系统 - 借用管理相关表结构

-- 1. 实训室借用申请表
CREATE TABLE `lab_borrow_apply` (
  `apply_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '申请单ID',
  `lab_id` BIGINT NOT NULL COMMENT '实训室编号',
  `borrow_department` VARCHAR(100) NOT NULL COMMENT '借用部门',
  `borrower_id` BIGINT NOT NULL COMMENT '借用人员ID（关联user.id）',
  `borrower_name` VARCHAR(50) NOT NULL COMMENT '冗余：借用人员姓名',
  `contact_phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
  `start_time` DATETIME NOT NULL COMMENT '借用开始时间',
  `end_time` DATETIME NOT NULL COMMENT '借用结束时间',
  `purpose` VARCHAR(200) NOT NULL COMMENT '借用用途',
  `register_person_id` BIGINT NOT NULL COMMENT '登记人ID（关联user.id）',
  `register_person_name` VARCHAR(50) NOT NULL COMMENT '冗余：登记人姓名',
  `status` VARCHAR(20) NOT NULL DEFAULT '待审批' COMMENT '申请状态（待审批/已审批/已驳回/已完成/逾期未归还）',
  `approver_id` BIGINT DEFAULT NULL COMMENT '审批人ID（关联user.id）',
  `approver_name` VARCHAR(50) DEFAULT NULL COMMENT '冗余：审批人姓名',
  `approval_time` DATETIME DEFAULT NULL COMMENT '审批时间',
  `approval_opinion` VARCHAR(200) DEFAULT NULL COMMENT '审批意见',
  `actual_return_time` DATETIME DEFAULT NULL COMMENT '实际归还时间',
  `return_handler_id` BIGINT DEFAULT NULL COMMENT '归还经办人ID（关联user.id）',
  `return_handler_name` VARCHAR(50) DEFAULT NULL COMMENT '冗余：归还经办人姓名',
  `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`apply_id`),
  KEY `idx_lab_id` (`lab_id`),
  KEY `idx_borrower_id` (`borrower_id`),
  KEY `idx_borrow_department` (`borrow_department`),
  KEY `idx_status` (`status`),
  KEY `idx_start_time` (`start_time`),
  KEY `idx_end_time` (`end_time`),
  KEY `idx_register_person_id` (`register_person_id`),
  KEY `idx_approver_id` (`approver_id`),
  KEY `idx_return_handler_id` (`return_handler_id`),
  CONSTRAINT `fk_borrow_apply_lab` FOREIGN KEY (`lab_id`) REFERENCES `lab_info` (`lab_id`),
  CONSTRAINT `fk_borrow_apply_borrower` FOREIGN KEY (`borrower_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_borrow_apply_register` FOREIGN KEY (`register_person_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_borrow_apply_approver` FOREIGN KEY (`approver_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_borrow_apply_return` FOREIGN KEY (`return_handler_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实训室借用申请表';

-- 2. 实训室设备配置表
CREATE TABLE `lab_equipment_config` (
  `config_id` INT NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `lab_id` BIGINT NOT NULL COMMENT '实训室编号',
  `equipment_name` VARCHAR(100) NOT NULL COMMENT '设备名称',
  `spec_model` VARCHAR(100) DEFAULT NULL COMMENT '规格型号',
  `quantity` INT NOT NULL COMMENT '数量',
  `status` VARCHAR(20) NOT NULL DEFAULT '正常' COMMENT '设备状态（正常/故障/维修中）',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`config_id`),
  KEY `idx_lab_id` (`lab_id`),
  KEY `idx_equipment_name` (`equipment_name`),
  CONSTRAINT `fk_equipment_config_lab` FOREIGN KEY (`lab_id`) REFERENCES `lab_info` (`lab_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='实训室设备配置表';

-- 3. 耗材基础信息表
CREATE TABLE `consumable` (
  `consumable_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '耗材ID',
  `consumable_name` VARCHAR(100) NOT NULL COMMENT '耗材名称',
  `spec_model` VARCHAR(100) DEFAULT NULL COMMENT '规格型号',
  `unit` VARCHAR(20) NOT NULL COMMENT '单位',
  `initial_stock` INT NOT NULL COMMENT '初始库存',
  `current_stock` INT NOT NULL COMMENT '当前库存',
  `min_stock` INT NOT NULL DEFAULT 0 COMMENT '最低库存阈值',
  `lab_id` BIGINT NOT NULL COMMENT '存放实训室编号',
  `keeper_id` BIGINT NOT NULL COMMENT '保管人ID（关联user.id）',
  `keeper_name` VARCHAR(50) NOT NULL COMMENT '冗余：保管人姓名',
  `status` VARCHAR(20) NOT NULL DEFAULT '正常' COMMENT '状态（正常/停用）',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`consumable_id`),
  KEY `idx_consumable_name` (`consumable_name`),
  KEY `idx_lab_id` (`lab_id`),
  KEY `idx_keeper_id` (`keeper_id`),
  KEY `idx_status` (`status`),
  CONSTRAINT `fk_consumable_lab` FOREIGN KEY (`lab_id`) REFERENCES `lab_info` (`lab_id`),
  CONSTRAINT `fk_consumable_keeper` FOREIGN KEY (`keeper_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='耗材基础信息表';

-- 4. 耗材借用记录表
CREATE TABLE `consumable_borrow_record` (
  `record_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `consumable_id` BIGINT NOT NULL COMMENT '耗材ID',
  `borrower_id` BIGINT NOT NULL COMMENT '借用人员ID（关联user.id）',
  `borrower_name` VARCHAR(50) NOT NULL COMMENT '冗余：借用人员姓名',
  `borrow_department` VARCHAR(100) NOT NULL COMMENT '借用部门',
  `borrow_quantity` INT NOT NULL COMMENT '借用数量',
  `borrow_date` DATE NOT NULL COMMENT '借用日期',
  `expected_return_date` DATE NOT NULL COMMENT '预计归还日期',
  `register_person_id` BIGINT NOT NULL COMMENT '登记人ID（关联user.id）',
  `register_person_name` VARCHAR(50) NOT NULL COMMENT '冗余：登记人姓名',
  `return_quantity` INT DEFAULT 0 COMMENT '实际归还数量',
  `actual_return_date` DATE DEFAULT NULL COMMENT '实际归还日期',
  `damage_condition` VARCHAR(20) DEFAULT NULL COMMENT '损耗情况（完好/部分损耗/全部损耗）',
  `handler_id` BIGINT DEFAULT NULL COMMENT '经办人ID（关联user.id）',
  `handler_name` VARCHAR(50) DEFAULT NULL COMMENT '冗余：经办人姓名',
  `status` VARCHAR(20) NOT NULL DEFAULT '未归还' COMMENT '状态（未归还/部分未归还/已归还）',
  `remark` VARCHAR(200) DEFAULT NULL COMMENT '备注',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`record_id`),
  KEY `idx_consumable_id` (`consumable_id`),
  KEY `idx_borrower_id` (`borrower_id`),
  KEY `idx_borrow_department` (`borrow_department`),
  KEY `idx_borrow_date` (`borrow_date`),
  KEY `idx_status` (`status`),
  KEY `idx_register_person_id` (`register_person_id`),
  KEY `idx_handler_id` (`handler_id`),
  CONSTRAINT `fk_borrow_record_consumable` FOREIGN KEY (`consumable_id`) REFERENCES `consumable` (`consumable_id`),
  CONSTRAINT `fk_borrow_record_borrower` FOREIGN KEY (`borrower_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_borrow_record_register` FOREIGN KEY (`register_person_id`) REFERENCES `user` (`id`),
  CONSTRAINT `fk_borrow_record_handler` FOREIGN KEY (`handler_id`) REFERENCES `user` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='耗材借用记录表';

-- 5. 实训室借用统计视图
CREATE VIEW `lab_borrow_stat` AS
SELECT
  l.lab_id,
  l.lab_name,
  COUNT(ba.apply_id) AS total_borrow_times,
  COUNT(CASE WHEN ba.status = '已完成' THEN 1 END) AS completed_times,
  COUNT(CASE WHEN ba.status = '逾期未归还' THEN 1 END) AS overdue_times,
  COUNT(CASE WHEN ba.status = '待审批' THEN 1 END) AS pending_approval_times,
  SUM(CASE WHEN ba.status IN ('已完成', '逾期未归还') THEN TIMESTAMPDIFF(HOUR, ba.start_time, ba.end_time) ELSE 0 END) AS total_borrow_hours
FROM
  lab_info l
LEFT JOIN lab_borrow_apply ba ON l.lab_id = ba.lab_id
GROUP BY
  l.lab_id, l.lab_name;

-- 6. 耗材借用统计视图
CREATE VIEW `consumable_borrow_stat` AS
SELECT
  c.consumable_id,
  c.consumable_name,
  c.spec_model,
  c.unit,
  c.current_stock,
  c.min_stock,
  COUNT(cbr.record_id) AS total_borrow_times,
  SUM(cbr.borrow_quantity) AS total_borrow_quantity,
  SUM(cbr.return_quantity) AS total_return_quantity,
  CASE WHEN COUNT(cbr.record_id) > 0 THEN 
    ROUND(SUM(cbr.return_quantity) / SUM(cbr.borrow_quantity) * 100, 2)
  ELSE 0 END AS return_rate,
  SUM(cbr.borrow_quantity) - SUM(cbr.return_quantity) AS total_unreturned_quantity,
  l.lab_name AS storage_location
FROM
  consumable c
LEFT JOIN consumable_borrow_record cbr ON c.consumable_id = cbr.consumable_id
LEFT JOIN lab_info l ON c.lab_id = l.lab_id
GROUP BY
  c.consumable_id, c.consumable_name, c.spec_model, c.unit, c.current_stock, c.min_stock, l.lab_name;

-- 7. 空实训室查询视图
CREATE VIEW `available_lab_view` AS
SELECT
  l.lab_id,
  l.lab_name,
  l.capacity,
  l.location,
  l.status AS lab_status,
  GROUP_CONCAT(DISTINCT ec.equipment_name SEPARATOR ', ') AS core_equipment,
  GROUP_CONCAT(DISTINCT ec.spec_model SEPARATOR ', ') AS equipment_config
FROM
  lab_info l
LEFT JOIN lab_equipment_config ec ON l.lab_id = ec.lab_id AND ec.status = '正常'
GROUP BY
  l.lab_id, l.lab_name, l.capacity, l.location, l.status;

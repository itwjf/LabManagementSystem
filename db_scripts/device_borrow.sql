/*
 Navicat Premium Data Transfer

 Source Server         : lab
 Source Server Type    : MySQL
 Source Server Version : 80044
 Source Host           : localhost:3307
 Source Schema         : lab_db

 Target Server Type    : MySQL
 Target Server Version : 80044
 File Encoding         : 65001

 Date: 08/01/2026 19:28:27
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for device_borrow
-- ----------------------------
DROP TABLE IF EXISTS `device_borrow`;
CREATE TABLE `device_borrow`  (
  `id` bigint(0) NOT NULL AUTO_INCREMENT,
  `device_id` bigint(0) NOT NULL COMMENT '关联设备ID',
  `borrower_id` bigint(0) NOT NULL COMMENT '借用人 user.id',
  `borrower_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '冗余：借用人姓名',
  `department` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '冗余：部门',
  `lab_location` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '实训室（从 device.location 自动带出）',
  `borrow_time` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '借出时间',
  `expected_return_time` datetime(0) NOT NULL COMMENT '预计归还时间',
  `purpose` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '借用用途',
  `device_condition_on_borrow` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '正常' COMMENT '借出时状态：正常/轻微瑕疵',
  `actual_return_time` datetime(0) NULL DEFAULT NULL COMMENT '实际归还时间（NULL=未归还）',
  `return_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT '未归还' COMMENT '归还状态：未归还/正常/损坏/丢失',
  `damage_description` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '损坏说明',
  `device_status_on_return` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '归还时设备状态：正常/损坏/丢失',
  `handler` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '经办人',
  `return_time` datetime(0) NULL DEFAULT NULL COMMENT '归还登记时间',
  `registrar_id` bigint(0) NOT NULL COMMENT '登记人 user.id（通常=borrower_id）',
  `created_at` datetime(0) NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_device_id`(`device_id`) USING BTREE,
  INDEX `idx_borrower_id`(`borrower_id`) USING BTREE,
  INDEX `idx_return_status`(`return_status`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;

/*
 Navicat Premium Data Transfer

 Source Server         : MySQL
 Source Server Type    : MySQL
 Source Server Version : 80300
 Source Host           : localhost:3306
 Source Schema         : child_health_system

 Target Server Type    : MySQL
 Target Server Version : 80300
 File Encoding         : 65001

 Date: 15/05/2025 14:20:56
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for allergies
-- ----------------------------
DROP TABLE IF EXISTS `allergies`;
CREATE TABLE `allergies`  (
  `allergy_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '过敏记录ID',
  `child_id` bigint(0) NOT NULL COMMENT '儿童ID',
  `allergy_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '过敏原名称',
  `severity` enum('mild','moderate','severe') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '严重程度',
  `diagnosis_date` date NOT NULL COMMENT '诊断日期',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注说明',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`allergy_id`) USING BTREE,
  INDEX `allergies_ibfk_1`(`child_id`) USING BTREE,
  CONSTRAINT `allergies_ibfk_1` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '过敏史记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of allergies
-- ----------------------------
INSERT INTO `allergies` VALUES (1, 1, '花生', 'moderate', '2024-03-13', '症状有所缓解，但仍需避免接触', '2025-01-12 10:51:29', '2025-03-23 16:47:52');
INSERT INTO `allergies` VALUES (2, 1, '花生', 'severe', '2024-03-21', '避免接触花生及其制品123123', '2025-02-22 22:29:56', '2025-03-23 16:47:44');
INSERT INTO `allergies` VALUES (3, 1, '花生', 'severe', '2024-03-21', '避免接触花生及其制品', '2025-02-22 22:30:01', '2025-02-22 22:30:01');
INSERT INTO `allergies` VALUES (6, 9, '123123', 'moderate', '2025-03-12', '1231234', '2025-03-23 16:48:04', '2025-03-23 16:48:04');
INSERT INTO `allergies` VALUES (7, 9, '123', 'mild', '2025-03-05', '123', '2025-03-23 18:06:40', '2025-03-23 18:06:40');

-- ----------------------------
-- Table structure for children
-- ----------------------------
DROP TABLE IF EXISTS `children`;
CREATE TABLE `children`  (
  `child_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '儿童ID',
  `parent_id` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '家长ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '儿童姓名',
  `gender` tinyint(0) NOT NULL DEFAULT 0 COMMENT '性别：0-未指定 1-男性 2-女性',
  `birth_date` date NOT NULL COMMENT '出生日期',
  `blood_type` enum('A','B','AB','O') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '血型',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`child_id`) USING BTREE,
  INDEX `children_ibfk_1`(`parent_id`) USING BTREE,
  CONSTRAINT `children_ibfk_1` FOREIGN KEY (`parent_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '儿童基本信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of children
-- ----------------------------
INSERT INTO `children` VALUES (1, 'aab70950-fac7-4fea-9db2-fae293218325', '小亮亮', 1, '2020-01-01', 'AB', '2025-01-12 10:51:29', '2025-02-09 17:19:37');
INSERT INTO `children` VALUES (2, '1fd5a1ed-d090-11ef-a76f-00ffff24adec', '刘小红', 2, '2021-03-20', 'B', '2025-01-12 10:51:29', '2025-01-12 10:51:29');
INSERT INTO `children` VALUES (3, 'aab70950-fac7-4fea-9db2-fae293218325', '张小明', 1, '2020-01-01', 'A', '2025-02-09 14:09:46', '2025-02-09 14:09:46');
INSERT INTO `children` VALUES (7, 'aab70950-fac7-4fea-9db2-fae293218325', '阿德', 1, '2025-02-04', 'O', '2025-02-11 08:45:54', '2025-02-11 08:47:19');
INSERT INTO `children` VALUES (8, 'aab70950-fac7-4fea-9db2-fae293218325', '请问请问', 1, '2025-02-10', 'A', '2025-02-11 08:49:06', '2025-02-11 09:05:10');
INSERT INTO `children` VALUES (9, '1fd59fd7-d090-11ef-a76f-00ffff24adec', '陈皮糖', 1, '2025-02-03', 'O', '2025-02-11 09:01:18', '2025-02-21 16:49:43');
INSERT INTO `children` VALUES (11, '27d9affa-e3bd-11ef-81ce-00ffff24adec', '123', 1, '2025-03-11', 'B', '2025-03-23 18:04:56', '2025-03-23 18:04:56');

-- ----------------------------
-- Table structure for diet_records
-- ----------------------------
DROP TABLE IF EXISTS `diet_records`;
CREATE TABLE `diet_records`  (
  `record_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `child_id` bigint(0) NOT NULL COMMENT '儿童ID',
  `meal_time` datetime(0) NOT NULL COMMENT '用餐时间',
  `meal_type` enum('breakfast','lunch','dinner','snack') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '餐次类型',
  `food_items` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '食物项目',
  `amount` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '食用量',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注说明',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `diet_records_ibfk_1`(`child_id`) USING BTREE,
  CONSTRAINT `diet_records_ibfk_1` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '饮食记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of diet_records
-- ----------------------------
INSERT INTO `diet_records` VALUES (2, 2, '2025-01-07 18:51:29', 'lunch', '牛奶,面包,鸡蛋', '适量', '食欲正常，好吃', '2025-01-12 10:51:29', '2025-02-28 17:47:19');
INSERT INTO `diet_records` VALUES (3, 1, '2024-03-21 08:30:00', 'lunch', '牛奶, 面包, 鸡蛋', '250ml牛奶, 2片面包, 1个鸡蛋', '食欲良好', '2025-02-22 11:32:24', '2025-02-22 11:47:54');
INSERT INTO `diet_records` VALUES (4, 1, '2024-03-21 08:30:00', 'breakfast', '牛奶, 面包, 鸡蛋', '250ml牛奶, 2片面包, 1个鸡蛋', '食欲良好', '2025-02-22 11:44:24', '2025-02-22 11:44:24');
INSERT INTO `diet_records` VALUES (5, 1, '2024-03-21 00:30:00', 'breakfast', '牛奶, 面包, 鸡蛋', '250ml牛奶, 2片面包, 1个鸡蛋', '食欲不正', '2025-02-22 11:44:25', '2025-02-28 17:14:40');
INSERT INTO `diet_records` VALUES (6, 1, '2024-03-21 08:30:00', 'breakfast', '牛奶, 面包, 鸡蛋', '250ml牛奶, 2片面包, 1个鸡蛋', '食欲良好', '2025-02-22 11:44:26', '2025-02-22 11:44:26');
INSERT INTO `diet_records` VALUES (7, 1, '2024-03-21 08:30:00', 'breakfast', '牛奶, 面包, 鸡蛋', '250ml牛奶, 2片面包, 1个鸡蛋', '食欲良好', '2025-02-22 11:44:26', '2025-02-22 11:44:26');

-- ----------------------------
-- Table structure for growth_records
-- ----------------------------
DROP TABLE IF EXISTS `growth_records`;
CREATE TABLE `growth_records`  (
  `record_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `child_id` bigint(0) NOT NULL COMMENT '儿童ID',
  `height` decimal(5, 2) NOT NULL COMMENT '身高(cm)',
  `weight` decimal(5, 2) NOT NULL COMMENT '体重(kg)',
  `bmi` decimal(4, 2) GENERATED ALWAYS AS ((`weight` / ((`height` / 100) * (`height` / 100)))) STORED COMMENT 'BMI指数' NULL,
  `measure_date` date NOT NULL COMMENT '测量日期',
  `recorded_by` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '记录人ID',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `growth_records_ibfk_1`(`child_id`) USING BTREE,
  INDEX `growth_records_ibfk_2`(`recorded_by`) USING BTREE,
  CONSTRAINT `growth_records_ibfk_1` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `growth_records_ibfk_2` FOREIGN KEY (`recorded_by`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 10 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '生长发育记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of growth_records
-- ----------------------------
INSERT INTO `growth_records` VALUES (4, 1, 110.50, 20.50, DEFAULT, '2024-03-11', '1fd58a0c-d090-11ef-a76f-00ffff24adec', '2025-02-21 11:15:25', '2025-02-21 13:21:27');
INSERT INTO `growth_records` VALUES (5, 1, 121.00, 20.50, DEFAULT, '2024-03-20', '1fd58a0c-d090-11ef-a76f-00ffff24adec', '2025-02-21 11:15:26', '2025-02-21 13:20:09');
INSERT INTO `growth_records` VALUES (6, 1, 110.50, 20.50, DEFAULT, '2024-03-11', '1fd58a0c-d090-11ef-a76f-00ffff24adec', '2025-02-21 11:15:26', '2025-02-21 13:20:23');
INSERT INTO `growth_records` VALUES (7, 1, 111.00, 20.50, DEFAULT, '2024-02-10', '1fd58a0c-d090-11ef-a76f-00ffff24adec', '2025-02-21 11:15:27', '2025-02-21 13:21:20');
INSERT INTO `growth_records` VALUES (8, 7, 122.00, 44.00, DEFAULT, '2025-02-19', '1fd58a0c-d090-11ef-a76f-00ffff24adec', '2025-02-21 13:21:54', '2025-02-21 13:22:11');
INSERT INTO `growth_records` VALUES (9, 8, 133.00, 26.00, DEFAULT, '2025-03-23', '1fd58a0c-d090-11ef-a76f-00ffff24adec', '2025-03-23 17:33:56', '2025-03-23 17:34:06');

-- ----------------------------
-- Table structure for medical_records
-- ----------------------------
DROP TABLE IF EXISTS `medical_records`;
CREATE TABLE `medical_records`  (
  `record_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `child_id` bigint(0) NOT NULL COMMENT '儿童ID',
  `doctor_id` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '就诊医生ID',
  `visit_date` date NOT NULL COMMENT '就诊日期',
  `diagnosis` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '诊断结果',
  `treatment` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '治疗方案',
  `hospital_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '医院名称',
  `department` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '就诊科室',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注说明',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `medical_records_ibfk_1`(`child_id`) USING BTREE,
  INDEX `medical_records_ibfk_2`(`doctor_id`) USING BTREE,
  CONSTRAINT `medical_records_ibfk_1` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `medical_records_ibfk_2` FOREIGN KEY (`doctor_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 20 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '就医记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of medical_records
-- ----------------------------
INSERT INTO `medical_records` VALUES (3, 1, '1fd59b9a-d090-11ef-a76f-00ffff24adec', '2024-03-21', '普通感冒', '建议多休息，多喝水', '市第一人民医院', '儿科', '症状轻微', '2025-02-22 15:36:34', '2025-02-22 15:36:34');
INSERT INTO `medical_records` VALUES (4, 1, '1fd59b9a-d090-11ef-a76f-00ffff24adec', '2024-03-21', '普通感冒', '建议多休息，多喝水', '市第一人民医院', '儿科', '症状轻微', '2025-02-22 15:36:38', '2025-02-22 15:36:38');
INSERT INTO `medical_records` VALUES (5, 1, '1fd59b9a-d090-11ef-a76f-00ffff24adec', '2024-03-21', '普通感冒', '建议多休息，多喝水', '市第一人民医院', '儿科', '症状轻微', '2025-02-22 15:36:38', '2025-02-22 15:36:38');
INSERT INTO `medical_records` VALUES (6, 1, '1fd59b9a-d090-11ef-a76f-00ffff24adec', '2024-03-21', '普通感冒', '建议多休息，多喝水', '市第一人民医院', '儿科', '症状轻微', '2025-03-23 14:55:59', '2025-03-23 14:55:59');
INSERT INTO `medical_records` VALUES (7, 1, '1fd59b9a-d090-11ef-a76f-00ffff24adec', '2024-03-21', '普通感冒', '建议多休息，多喝水', '市第一人民医院', '儿科', '症状轻微', '2025-03-23 14:56:00', '2025-03-23 14:56:00');
INSERT INTO `medical_records` VALUES (8, 1, '1fd59b9a-d090-11ef-a76f-00ffff24adec', '2024-03-21', '普通感冒', '建议多休息，多喝水', '市第一人民医院', '儿科', '症状轻微', '2025-03-23 14:56:02', '2025-03-23 14:56:02');
INSERT INTO `medical_records` VALUES (9, 1, '1fd59b9a-d090-11ef-a76f-00ffff24adec', '2024-03-21', '普通感冒', '建议多休息，多喝水', '市第一人民医院', '儿科', '症状轻微', '2025-03-23 14:56:04', '2025-03-23 14:56:04');
INSERT INTO `medical_records` VALUES (11, 1, '1fd59b9a-d090-11ef-a76f-00ffff24adec', '2024-03-21', '普通感冒', '建议多休息，多喝水', '市第一人民医院', '儿科', '症状轻微', '2025-03-23 15:03:52', '2025-03-23 15:03:52');
INSERT INTO `medical_records` VALUES (12, 1, '1fd59b9a-d090-11ef-a76f-00ffff24adec', '2024-03-21', '普通感冒', '建议多休息，多喝水', '市第一人民医院', '儿科', '症状轻微', '2025-03-23 15:03:53', '2025-03-23 15:03:53');
INSERT INTO `medical_records` VALUES (13, 1, '1fd59b9a-d090-11ef-a76f-00ffff24adec', '2024-03-21', '普通感冒', '建议多休息，多喝水', '市第一人民医院', '儿科', '症状轻微', '2025-03-23 15:03:54', '2025-03-23 15:03:54');
INSERT INTO `medical_records` VALUES (14, 1, '1fd59b9a-d090-11ef-a76f-00ffff24adec', '2024-03-21', '普通感冒', '建议多休息，多喝水', '市第一人民医院', '儿科', '症状轻微', '2025-03-23 15:04:06', '2025-03-23 15:04:06');
INSERT INTO `medical_records` VALUES (15, 1, '1fd59b9a-d090-11ef-a76f-00ffff24adec', '2024-03-21', '普通感冒', '建议多休息，多喝水', '市第一人民医院', '儿科', '症状轻微', '2025-03-23 15:04:06', '2025-03-23 15:04:06');
INSERT INTO `medical_records` VALUES (16, 1, '1fd59b9a-d090-11ef-a76f-00ffff24adec', '2024-03-21', '普通感冒', '建议多休息，多喝水', '市第一人民医院', '儿科', '症状轻微', '2025-03-23 15:04:07', '2025-03-23 15:04:07');
INSERT INTO `medical_records` VALUES (17, 1, '1fd59b9a-d090-11ef-a76f-00ffff24adec', '2024-03-21', '普通感冒', '建议多休息，多喝水', '市第一人民医院', '儿科', '症状轻微', '2025-03-23 15:04:08', '2025-03-23 15:04:08');
INSERT INTO `medical_records` VALUES (18, 1, '1fd59b9a-d090-11ef-a76f-00ffff24adec', '2024-03-21', '普通感冒', '建议多休息，多喝水', '市第一人民医院', '儿科', '症状轻微', '2025-03-23 15:04:09', '2025-03-23 15:04:09');
INSERT INTO `medical_records` VALUES (19, 1, '1fd59b9a-d090-11ef-a76f-00ffff24adec', '2024-03-21', '普通感冒', '建议多休息，多喝水', '市第一人民医院', '儿科', '症状轻微', '2025-03-23 15:04:09', '2025-03-23 15:04:09');

-- ----------------------------
-- Table structure for medication_records
-- ----------------------------
DROP TABLE IF EXISTS `medication_records`;
CREATE TABLE `medication_records`  (
  `record_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `medical_record_id` bigint(0) NOT NULL COMMENT '关联就医记录ID',
  `medicine_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '药品名称',
  `dosage` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用药剂量',
  `frequency` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用药频率',
  `start_date` date NOT NULL COMMENT '开始用药日期',
  `end_date` date NOT NULL COMMENT '结束用药日期',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注说明',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `medication_records_ibfk_1`(`medical_record_id`) USING BTREE,
  CONSTRAINT `medication_records_ibfk_1` FOREIGN KEY (`medical_record_id`) REFERENCES `medical_records` (`record_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 18 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用药记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of medication_records
-- ----------------------------
INSERT INTO `medication_records` VALUES (14, 3, '布洛芬', '5ml', '每日三次', '2024-03-21', '2024-03-24', '餐后服用', '2025-03-23 15:09:00', '2025-03-23 15:09:00');
INSERT INTO `medication_records` VALUES (15, 3, '布洛芬', '5ml', '每日三次', '2024-03-21', '2024-03-24', '餐后服用', '2025-03-23 15:09:25', '2025-03-23 15:09:25');
INSERT INTO `medication_records` VALUES (16, 3, '布洛芬', '5ml', '每日三次', '2024-03-21', '2024-03-24', '餐后服用', '2025-03-23 15:09:30', '2025-03-23 15:09:30');
INSERT INTO `medication_records` VALUES (17, 3, '布洛芬', '5ml', '每日三次', '2024-03-21', '2024-03-24', '餐后服用', '2025-03-23 15:45:49', '2025-03-23 15:45:49');

-- ----------------------------
-- Table structure for permissions
-- ----------------------------
DROP TABLE IF EXISTS `permissions`;
CREATE TABLE `permissions`  (
  `permission_id` int(0) NOT NULL AUTO_INCREMENT,
  `permission_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限名称',
  `permission_key` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '权限标识符',
  `menu_url` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '菜单URL',
  `icon` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '导航栏图标',
  `parent_id` int(0) NULL DEFAULT NULL COMMENT '父级权限ID',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`permission_id`) USING BTREE,
  INDEX `fk_parent_permission`(`parent_id`) USING BTREE,
  CONSTRAINT `fk_parent_permission` FOREIGN KEY (`parent_id`) REFERENCES `permissions` (`permission_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 45 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统权限表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permissions
-- ----------------------------
INSERT INTO `permissions` VALUES (1, '系统管理', 'system:manage', '/system', 'Setting', NULL, '2025-01-26 16:44:44', '2025-01-26 17:01:12');
INSERT INTO `permissions` VALUES (2, '用户管理', 'user:manage', '/system/users', 'User', 1, '2025-01-26 16:44:44', '2025-01-26 17:01:12');
INSERT INTO `permissions` VALUES (3, '角色管理', 'role:manage', '/system/roles', 'UserFilled', 1, '2025-01-26 16:44:44', '2025-01-26 17:01:12');
INSERT INTO `permissions` VALUES (4, '权限管理', 'permission:manage', '/system/permissions', 'Lock', 1, '2025-01-26 16:44:44', '2025-01-26 17:01:12');
INSERT INTO `permissions` VALUES (5, '儿童健康管理', 'child:manage', '/child', 'House', NULL, '2025-01-26 16:04:37', '2025-01-26 17:01:12');
INSERT INTO `permissions` VALUES (6, '儿童信息', 'child:info', '/child/info', 'Avatar', 5, '2025-01-26 16:04:37', '2025-01-26 17:01:12');
INSERT INTO `permissions` VALUES (7, '生长记录', 'growth:manage', '/child/growth', 'TrendCharts', 5, '2025-01-26 16:04:37', '2025-01-26 17:01:12');
INSERT INTO `permissions` VALUES (8, '体温记录', 'temperature:manage', '/child/temperature', 'Odometer', 5, '2025-01-26 16:04:37', '2025-01-26 17:01:12');
INSERT INTO `permissions` VALUES (9, '饮食记录', 'diet:manage', '/child/diet', 'Bowl', 5, '2025-01-26 16:04:37', '2025-01-26 17:01:12');
INSERT INTO `permissions` VALUES (10, '医疗管理', 'medical:manage', '/medical', 'FirstAidKit', NULL, '2025-01-26 16:04:37', '2025-01-26 17:01:12');
INSERT INTO `permissions` VALUES (11, '就医记录', 'visit:manage', '/medical/visits', 'Notebook', 10, '2025-01-26 16:04:37', '2025-01-26 17:01:12');
INSERT INTO `permissions` VALUES (12, '用药记录', 'medication:manage', '/medical/medications', 'Grid', 10, '2025-01-26 16:04:37', '2025-01-26 17:05:54');
INSERT INTO `permissions` VALUES (13, '疫苗接种', 'vaccination:manage', '/medical/vaccinations', 'Stamp', 10, '2025-01-26 16:04:37', '2025-01-26 17:01:12');
INSERT INTO `permissions` VALUES (14, '过敏史', 'allergy:manage', '/medical/allergies', 'Warning', 10, '2025-01-26 16:04:37', '2025-01-26 17:01:12');
INSERT INTO `permissions` VALUES (17, '健康分析', 'health:analysis', '/analysis', 'DataAnalysis', NULL, '2025-01-26 16:04:37', '2025-01-26 17:01:12');
INSERT INTO `permissions` VALUES (18, '生长曲线', 'growth:chart', '/analysis/growth', 'DataAnalysis', 17, '2025-01-26 16:04:37', '2025-01-26 17:03:10');
INSERT INTO `permissions` VALUES (19, '健康趋势', 'health:trend', '/analysis/trend', 'Histogram', 17, '2025-01-26 16:04:37', '2025-01-26 17:01:12');
INSERT INTO `permissions` VALUES (40, '查看所有儿童', 'child:view:all', '/children', NULL, NULL, '2025-02-09 16:29:55', '2025-02-09 16:29:55');
INSERT INTO `permissions` VALUES (41, '查看自己的儿童', 'child:view:own', '/children/my', NULL, NULL, '2025-02-09 16:29:55', '2025-02-09 16:29:55');
INSERT INTO `permissions` VALUES (42, '添加儿童', 'child:add', NULL, NULL, NULL, '2025-02-09 16:29:55', '2025-02-09 16:29:55');
INSERT INTO `permissions` VALUES (43, '编辑儿童', 'child:edit', NULL, NULL, NULL, '2025-02-09 16:29:55', '2025-02-09 16:29:55');
INSERT INTO `permissions` VALUES (44, '删除儿童', 'child:delete', NULL, NULL, NULL, '2025-02-09 16:29:55', '2025-02-09 16:29:55');

-- ----------------------------
-- Table structure for role_permissions
-- ----------------------------
DROP TABLE IF EXISTS `role_permissions`;
CREATE TABLE `role_permissions`  (
  `role_id` int(0) NOT NULL COMMENT '角色ID',
  `permission_id` int(0) NOT NULL COMMENT '权限ID',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  PRIMARY KEY (`role_id`, `permission_id`) USING BTREE,
  INDEX `role_permissions_ibfk_2`(`permission_id`) USING BTREE,
  CONSTRAINT `role_permissions_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `role_permissions_ibfk_2` FOREIGN KEY (`permission_id`) REFERENCES `permissions` (`permission_id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '角色权限关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_permissions
-- ----------------------------
INSERT INTO `role_permissions` VALUES (1, 1, '2025-01-26 16:54:19');
INSERT INTO `role_permissions` VALUES (1, 2, '2025-01-26 16:54:19');
INSERT INTO `role_permissions` VALUES (1, 3, '2025-01-26 16:54:19');
INSERT INTO `role_permissions` VALUES (1, 4, '2025-01-26 16:54:19');
INSERT INTO `role_permissions` VALUES (1, 5, '2025-01-26 16:54:19');
INSERT INTO `role_permissions` VALUES (1, 6, '2025-01-26 16:54:19');
INSERT INTO `role_permissions` VALUES (1, 7, '2025-01-26 16:54:19');
INSERT INTO `role_permissions` VALUES (1, 8, '2025-01-26 16:54:19');
INSERT INTO `role_permissions` VALUES (1, 9, '2025-01-26 16:54:19');
INSERT INTO `role_permissions` VALUES (1, 10, '2025-01-26 16:54:19');
INSERT INTO `role_permissions` VALUES (1, 11, '2025-01-26 16:54:19');
INSERT INTO `role_permissions` VALUES (1, 12, '2025-01-26 16:54:19');
INSERT INTO `role_permissions` VALUES (1, 13, '2025-01-26 16:54:19');
INSERT INTO `role_permissions` VALUES (1, 14, '2025-01-26 16:54:19');
INSERT INTO `role_permissions` VALUES (1, 17, '2025-01-26 16:54:19');
INSERT INTO `role_permissions` VALUES (1, 18, '2025-01-26 16:54:19');
INSERT INTO `role_permissions` VALUES (1, 19, '2025-01-26 16:54:19');
INSERT INTO `role_permissions` VALUES (2, 5, '2025-01-26 16:54:34');
INSERT INTO `role_permissions` VALUES (2, 6, '2025-01-26 16:54:34');
INSERT INTO `role_permissions` VALUES (2, 7, '2025-01-26 16:54:34');
INSERT INTO `role_permissions` VALUES (2, 8, '2025-01-26 16:54:34');
INSERT INTO `role_permissions` VALUES (2, 10, '2025-01-26 16:54:34');
INSERT INTO `role_permissions` VALUES (2, 11, '2025-01-26 16:54:34');
INSERT INTO `role_permissions` VALUES (2, 12, '2025-01-26 16:54:34');
INSERT INTO `role_permissions` VALUES (2, 13, '2025-01-26 16:54:34');
INSERT INTO `role_permissions` VALUES (2, 14, '2025-01-26 16:54:34');
INSERT INTO `role_permissions` VALUES (2, 17, '2025-01-26 16:54:34');
INSERT INTO `role_permissions` VALUES (2, 18, '2025-01-26 16:54:34');
INSERT INTO `role_permissions` VALUES (2, 19, '2025-01-26 16:54:34');
INSERT INTO `role_permissions` VALUES (3, 5, '2025-01-26 16:54:39');
INSERT INTO `role_permissions` VALUES (3, 6, '2025-01-26 16:54:39');
INSERT INTO `role_permissions` VALUES (3, 7, '2025-01-26 16:54:39');
INSERT INTO `role_permissions` VALUES (3, 8, '2025-01-26 16:54:39');
INSERT INTO `role_permissions` VALUES (3, 9, '2025-01-26 16:54:39');
INSERT INTO `role_permissions` VALUES (3, 10, '2025-01-26 16:54:39');
INSERT INTO `role_permissions` VALUES (3, 11, '2025-01-26 16:54:39');
INSERT INTO `role_permissions` VALUES (3, 13, '2025-01-26 16:54:39');
INSERT INTO `role_permissions` VALUES (3, 14, '2025-01-26 16:54:39');
INSERT INTO `role_permissions` VALUES (3, 17, '2025-01-26 16:54:39');
INSERT INTO `role_permissions` VALUES (3, 18, '2025-01-26 16:54:39');

-- ----------------------------
-- Table structure for roles
-- ----------------------------
DROP TABLE IF EXISTS `roles`;
CREATE TABLE `roles`  (
  `role_id` int(0) NOT NULL AUTO_INCREMENT,
  `role_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '角色名称',
  `role_desc` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '角色描述',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`role_id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统角色表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of roles
-- ----------------------------
INSERT INTO `roles` VALUES (1, 'admin', '系统管理员', '2025-01-12 10:51:29', '2025-01-12 10:51:29');
INSERT INTO `roles` VALUES (2, 'doctor', '医生', '2025-01-12 10:51:29', '2025-01-12 10:51:29');
INSERT INTO `roles` VALUES (3, 'parent', '家长', '2025-01-12 10:51:29', '2025-01-12 10:51:29');

-- ----------------------------
-- Table structure for temperature_records
-- ----------------------------
DROP TABLE IF EXISTS `temperature_records`;
CREATE TABLE `temperature_records`  (
  `record_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `child_id` bigint(0) NOT NULL COMMENT '儿童ID',
  `temperature` decimal(3, 1) NOT NULL COMMENT '体温(℃)',
  `measure_time` datetime(0) NOT NULL COMMENT '测量时间',
  `measure_position` enum('mouth','armpit','ear') CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '测量部位',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注说明',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`record_id`) USING BTREE,
  INDEX `temperature_records_ibfk_1`(`child_id`) USING BTREE,
  CONSTRAINT `temperature_records_ibfk_1` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '体温记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of temperature_records
-- ----------------------------
INSERT INTO `temperature_records` VALUES (2, 2, 37.0, '2025-01-10 10:51:29', 'ear', '正常体温，有点难受', '2025-01-12 10:51:29', '2025-02-22 11:06:15');
INSERT INTO `temperature_records` VALUES (3, 1, 39.0, '2024-03-04 11:30:00', 'ear', '烧的很厉害', '2025-02-21 13:37:48', '2025-02-21 19:44:47');
INSERT INTO `temperature_records` VALUES (4, 1, 37.0, '2024-03-25 10:30:00', 'armpit', '正常体温', '2025-02-21 14:17:18', '2025-02-21 19:43:03');
INSERT INTO `temperature_records` VALUES (5, 1, 37.0, '2024-03-30 10:30:00', 'ear', '正常体温', '2025-02-21 14:17:48', '2025-02-21 19:43:26');
INSERT INTO `temperature_records` VALUES (12, 9, 37.0, '2025-02-21 11:45:30', 'mouth', '还行', '2025-02-21 19:45:40', '2025-02-21 19:45:40');
INSERT INTO `temperature_records` VALUES (13, 9, 37.3, '2025-02-21 11:46:06', 'mouth', '好大二', '2025-02-21 19:46:16', '2025-02-21 19:46:16');
INSERT INTO `temperature_records` VALUES (14, 3, 36.5, '2025-02-22 03:06:52', 'mouth', '你他妈的\n', '2025-02-22 11:06:55', '2025-02-28 16:47:54');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `user_id` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'UUID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '加密密码',
  `role_id` int(0) NOT NULL COMMENT '角色ID',
  `real_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '联系电话',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`user_id`) USING BTREE,
  INDEX `user_ibfk_1`(`role_id`) USING BTREE,
  CONSTRAINT `user_ibfk_1` FOREIGN KEY (`role_id`) REFERENCES `roles` (`role_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES ('05bbd9e1-d4c7-11ef-a76f-00ffff24adec', 'boliangxu', 'e10adc3949ba59abbe56e057f20f883e', 1, NULL, NULL, '2025-01-17 19:34:33', '2025-01-17 19:37:06');
INSERT INTO `user` VALUES ('19af1350-d52e-11ef-a76f-00ffff24adec', 'nihao', 'ebba48e9eed68ec4786f00b71aa4a68c', 2, '徐博亮', '15779934883', '2025-01-18 07:52:24', '2025-01-18 14:43:49');
INSERT INTO `user` VALUES ('1fd58a0c-d090-11ef-a76f-00ffff24adec', 'admin', 'e10adc3949ba59abbe56e057f20f883e', 1, '大魔王', '15779934883', '2025-01-12 10:51:29', '2025-02-20 16:11:08');
INSERT INTO `user` VALUES ('1fd59b9a-d090-11ef-a76f-00ffff24adec', 'doctor1', 'e10adc3949ba59abbe56e057f20f883e', 2, '张医生', '13800000002', '2025-01-12 10:51:29', '2025-01-12 10:51:29');
INSERT INTO `user` VALUES ('1fd59df3-d090-11ef-a76f-00ffff24adec', 'doctor2', 'e10adc3949ba59abbe56e057f20f883e', 2, '李医生', '13800000003', '2025-01-12 10:51:29', '2025-01-12 10:51:29');
INSERT INTO `user` VALUES ('1fd59fd7-d090-11ef-a76f-00ffff24adec', 'parent1', 'e10adc3949ba59abbe56e057f20f883e', 3, '王家长', '13800000004', '2025-01-12 10:51:29', '2025-01-12 10:51:29');
INSERT INTO `user` VALUES ('1fd5a1ed-d090-11ef-a76f-00ffff24adec', 'parent2', 'e10adc3949ba59abbe56e057f20f883e', 3, '刘家长', '13800000005', '2025-01-12 10:51:29', '2025-01-12 10:51:29');
INSERT INTO `user` VALUES ('23148f5b-43fc-4fca-a096-a1028c072e27', 'xuan轩轩xuan', 'e10adc3949ba59abbe56e057f20f883e', 3, '饶本轩', '15676555556', '2025-02-05 19:47:24', '2025-02-05 20:22:40');
INSERT INTO `user` VALUES ('27d9affa-e3bd-11ef-81ce-00ffff24adec', 'child321', 'e10adc3949ba59abbe56e057f20f883e', 1, '亮亮', '15779934883', '2025-02-05 20:31:42', '2025-02-09 10:29:22');
INSERT INTO `user` VALUES ('768138e3-d56a-11ef-a76f-00ffff24adec', 'laotie', 'e10adc3949ba59abbe56e057f20f883e', 1, '苏表', '13646362843', '2025-01-18 15:04:30', '2025-02-05 20:41:20');
INSERT INTO `user` VALUES ('79f8415b-e79c-4371-a208-355dbfbda080', 'zhangsan', 'e10adc3949ba59abbe56e057f20f883e', 2, '张三', '13800138000', '2025-02-05 10:43:32', '2025-02-05 10:43:32');
INSERT INTO `user` VALUES ('87d5a2fa-d557-11ef-a76f-00ffff24adec', 'iphone', 'e10adc3949ba59abbe56e057f20f883e', 2, '亮亮', '15779934883', '2025-01-18 12:48:58', '2025-02-09 14:00:30');
INSERT INTO `user` VALUES ('9fe75b44-d40b-11ef-a76f-00ffff24adec', 'boliang', 'e10adc3949ba59abbe56e057f20f883e', 1, NULL, NULL, '2025-01-16 21:13:06', '2025-01-17 19:29:11');
INSERT INTO `user` VALUES ('aab70950-fac7-4fea-9db2-fae293218325', 'xuboliang', 'e10adc3949ba59abbe56e057f20f883e', 3, '徐博亮', '15779934883', '2025-02-05 19:38:50', '2025-02-09 16:43:42');
INSERT INTO `user` VALUES ('cf252e0d-d530-11ef-a76f-00ffff24adec', 'ninihao', 'e10adc3949ba59abbe56e057f20f883e', 3, NULL, NULL, '2025-01-18 08:11:48', '2025-01-18 08:11:48');
INSERT INTO `user` VALUES ('f663ed05-d567-11ef-a76f-00ffff24adec', 'nihaojxx', 'ebba48e9eed68ec4786f00b71aa4a68c', 3, NULL, NULL, '2025-01-18 14:46:36', '2025-01-18 14:47:39');
INSERT INTO `user` VALUES ('f6fe57e9-81c6-4d0c-80a4-18691756fc3f', '汶汶的', 'ebba48e9eed68ec4786f00b71aa4a68c', 1, '徐博汶', '13687083571', '2025-02-05 19:40:22', '2025-02-05 19:40:22');

-- ----------------------------
-- Table structure for vaccinations
-- ----------------------------
DROP TABLE IF EXISTS `vaccinations`;
CREATE TABLE `vaccinations`  (
  `vaccination_id` bigint(0) NOT NULL AUTO_INCREMENT COMMENT '接种记录ID',
  `child_id` bigint(0) NOT NULL COMMENT '儿童ID',
  `vaccine_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '疫苗名称',
  `vaccination_date` date NOT NULL COMMENT '接种日期',
  `next_due_date` date NULL DEFAULT NULL COMMENT '下次接种日期',
  `doctor_id` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '接种医生ID',
  `notes` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '备注说明',
  `created_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` timestamp(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`vaccination_id`) USING BTREE,
  INDEX `vaccinations_ibfk_1`(`child_id`) USING BTREE,
  INDEX `vaccinations_ibfk_2`(`doctor_id`) USING BTREE,
  CONSTRAINT `vaccinations_ibfk_1` FOREIGN KEY (`child_id`) REFERENCES `children` (`child_id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `vaccinations_ibfk_2` FOREIGN KEY (`doctor_id`) REFERENCES `user` (`user_id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '疫苗接种记录表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of vaccinations
-- ----------------------------
INSERT INTO `vaccinations` VALUES (1, 1, '新冠疫苗', '2024-03-21', '2024-09-21', '1fd59b9a-d090-11ef-a76f-00ffff24adec', '接种反应正常，观察48小时无异常', '2025-01-12 10:51:29', '2025-02-22 22:07:58');
INSERT INTO `vaccinations` VALUES (2, 2, '麻疹疫苗', '2024-01-05', '2024-07-05', '1fd59b9a-d090-11ef-a76f-00ffff24adec', '接种反应正常', '2025-01-12 10:51:29', '2025-01-12 10:51:29');
INSERT INTO `vaccinations` VALUES (3, 1, '麻疹疫苗', '2024-03-21', '2024-09-21', '1fd59b9a-d090-11ef-a76f-00ffff24adec', '接种反应正常', '2025-02-22 22:05:10', '2025-02-22 22:05:10');
INSERT INTO `vaccinations` VALUES (4, 1, '麻疹疫苗', '2024-03-21', '2024-09-21', '1fd59b9a-d090-11ef-a76f-00ffff24adec', '接种反应正常', '2025-02-22 22:05:13', '2025-02-22 22:05:13');
INSERT INTO `vaccinations` VALUES (5, 1, '麻疹疫苗', '2024-03-21', '2024-09-21', '1fd59b9a-d090-11ef-a76f-00ffff24adec', '接种反应正常', '2025-02-22 22:05:14', '2025-02-22 22:05:14');
INSERT INTO `vaccinations` VALUES (6, 1, '新冠疫苗', '2024-03-21', '2024-09-21', '1fd59b9a-d090-11ef-a76f-00ffff24adec', '接种反应正常，观察48小时无异常', '2025-02-22 22:05:15', '2025-02-22 22:08:14');
INSERT INTO `vaccinations` VALUES (8, 3, '新冠疫苗', '2024-03-21', '2024-09-21', '1fd59b9a-d090-11ef-a76f-00ffff24adec', '接种反应正常', '2025-02-22 22:05:50', '2025-02-22 22:05:50');

-- ----------------------------
-- Triggers structure for table user
-- ----------------------------
DROP TRIGGER IF EXISTS `before_insert_users`;
delimiter ;;
CREATE TRIGGER `before_insert_users` BEFORE INSERT ON `user` FOR EACH ROW BEGIN
    IF NEW.user_id IS NULL THEN
        SET NEW.user_id = UUID();
    END IF;
END
;;
delimiter ;

SET FOREIGN_KEY_CHECKS = 1;

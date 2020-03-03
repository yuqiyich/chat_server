/*
 Navicat Premium Data Transfer

 Source Server         : ruqi_mobile_manager
 Source Server Type    : MySQL
 Source Server Version : 80019
 Source Host           : localhost:3306
 Source Schema         : ruqi_mobile_manager

 Target Server Type    : MySQL
 Target Server Version : 80019
 File Encoding         : 65001

 Date: 03/03/2020 20:18:54
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for risk_record
-- ----------------------------
DROP TABLE IF EXISTS `risk_record`;
CREATE TABLE `risk_record` (
  `id` int NOT NULL AUTO_INCREMENT,
  `risk_type` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '风险类型',
  `user_id` bigint DEFAULT NULL COMMENT '用户id',
  `device_id` varchar(0) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '设备id',
  `risk_detail` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '风险详情',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `device_brand` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '设备品牌',
  `system_verison` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '设备系统',
  `app_version` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '应用版本',
  `net_state` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '网络状态',
  `location_lat` decimal(10,0) DEFAULT NULL COMMENT '当前位置经度',
  `app_id` int DEFAULT NULL COMMENT '应用的id',
  `channel` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '应用渠道',
  `location_lng` decimal(10,0) DEFAULT NULL COMMENT '当前位置的纬度',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

SET FOREIGN_KEY_CHECKS = 1;

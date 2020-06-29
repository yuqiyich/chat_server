/*
 Navicat Premium Data Transfer

 Source Server         : 如祺测试
 Source Server Type    : MySQL
 Source Server Version : 50718
 Source Host           : 10.3.0.21:63306
 Source Schema         : ruqi_mobile_manager

 Target Server Type    : MySQL
 Target Server Version : 50718
 File Encoding         : 65001

 Date: 11/05/2020 11:15:06
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for dot_event_record
-- ----------------------------
DROP TABLE IF EXISTS `dot_event_record`;
CREATE TABLE `dot_event_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `event_key` varchar(255) DEFAULT NULL COMMENT '事件id',
  `user_id` bigint(20) DEFAULT NULL COMMENT '用户id',
  `device_id` varchar(255) DEFAULT NULL COMMENT '设备id',
  `event_detail` varchar(255) DEFAULT NULL COMMENT '事件详情',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `device_brand` varchar(255) DEFAULT NULL COMMENT '设备品牌',
  `system_version` varchar(255) DEFAULT NULL COMMENT '设备系统',
  `app_versionname` varchar(255) DEFAULT NULL COMMENT '应用版本名称',
  `net_state` varchar(255) DEFAULT NULL COMMENT '网络状态',
  `location_lat` double DEFAULT NULL COMMENT '当前位置经度',
  `app_id` int(11) DEFAULT NULL COMMENT '应用的id',
  `platform` varchar(20) DEFAULT NULL COMMENT '应用平台',
  `channel` varchar(255) DEFAULT NULL COMMENT '应用渠道',
  `location_lng` double DEFAULT NULL COMMENT '当前位置的纬度',
  `ext` varchar(255) DEFAULT NULL COMMENT '备注字段',
  `app_versioncode` varchar(255) DEFAULT NULL COMMENT '版本号',
  `device_model` varchar(255) DEFAULT NULL COMMENT '设备型号',
  `request_ip` varchar(255) DEFAULT NULL COMMENT '请求的ip',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

ALTER TABLE dot_event_record ADD order_id varchar(50) DEFAULT NULL COMMENT '订单ID'
ALTER TABLE dot_event_record ADD scene varchar(10) DEFAULT NULL COMMENT '场景'

SET FOREIGN_KEY_CHECKS = 1;

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

 Date: 04/03/2020 17:16:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for wechat_token
-- ----------------------------
DROP TABLE IF EXISTS `wechat_token`;
CREATE TABLE `wechat_token` (
  `access_token` varchar(512) NOT NULL COMMENT '微信开发者token',
  `expires_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT 'token有效截止时间',
  PRIMARY KEY (`access_token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;

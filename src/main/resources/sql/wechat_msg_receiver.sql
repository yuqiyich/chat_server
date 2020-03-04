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

 Date: 04/03/2020 17:15:53
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for wechat_msg_receiver
-- ----------------------------
DROP TABLE IF EXISTS `wechat_msg_receiver`;
CREATE TABLE `wechat_msg_receiver` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `openid` varchar(512) NOT NULL COMMENT '微信用户对每一个公众号特殊的openId',
  `nickname` varchar(512) DEFAULT NULL COMMENT '微信昵称',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注名',
  `user_status` int(11) NOT NULL COMMENT '绑定启用状态，1:启用，0:停用',
  `create_time` timestamp NULL DEFAULT NULL COMMENT '创建时间',
  `modify_time` timestamp NULL DEFAULT NULL COMMENT '变更时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `openid` (`openid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;

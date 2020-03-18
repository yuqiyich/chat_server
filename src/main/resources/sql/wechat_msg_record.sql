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

 Date: 17/03/2020 15:50:33
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for wechat_msg_record
-- ----------------------------
DROP TABLE IF EXISTS `wechat_msg_record`;
CREATE TABLE `wechat_msg_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `msgid` varchar(512) NOT NULL COMMENT '每一条发送的微信消息的id',
  `openid` varchar(512) NOT NULL COMMENT '消息接收者微信openid',
  `msg_details` varchar(512) DEFAULT NULL COMMENT '消息详情内容',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注信息',
  `result` varchar(512) DEFAULT NULL COMMENT '消息发送结果',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `modify_time` datetime NULL DEFAULT NULL COMMENT '变更时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `msgid` (`msgid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;

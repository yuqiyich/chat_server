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

 Date: 11/05/2020 11:11:07
*/
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for risk_record
-- ----------------------------
DROP TABLE IF EXISTS `recommend_point_statics`;
CREATE TABLE `recommend_point_statics` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `city_code` varchar(255) DEFAULT NULL COMMENT '城市编码',
  `city_name` varchar(255) DEFAULT NULL COMMENT '城市名称',
  `total_record_num` bigint(20) DEFAULT NULL COMMENT '所有的原始记录数据量（推荐上车点为索引）',
  `total_recmd_point_num` bigint(20) DEFAULT NULL COMMENT '所有的推荐上车点数据量（记录为索引）',
  `total_origin_point_num` bigint(20) DEFAULT NULL COMMENT '所有用户扎针点的推荐上车点的数据量（用户扎针点为索引）',
  `date` datetime DEFAULT NULL COMMENT '统计日期',
  `env` varchar(255) DEFAULT NULL COMMENT '部署环境',
	PRIMARY KEY (`id`),
  UNIQUE KEY `key` (`date`,`env`, `city_code`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=22853 DEFAULT CHARSET=utf8mb4;

SET FOREIGN_KEY_CHECKS = 1;



replace into recommend_point_statics(city_code,city_name,total_record_num,total_recmd_point_num,total_origin_point_num,date,env)
values('440100', '广州市',10372,19633,1452,'2020-08-17 16:38:39', 'dev')
replace into recommend_point_statics(city_code,city_name,total_record_num,total_recmd_point_num,total_origin_point_num,date,env)
values('440100','广州市',1032,19633,1452,'2020-08-18 16:38:39', 'dev')
replace into recommend_point_statics(city_code,city_name,total_record_num,total_recmd_point_num,total_origin_point_num,date,env)
values('440100','广州市',1032,19633,1452,'2020-08-19 16:38:39', 'dev')
replace into recommend_point_statics(city_code,city_name,total_record_num,total_recmd_point_num,total_origin_point_num,date,env)
values('440100','广州市',10932,19633,1452,'2020-08-20 16:38:39', 'dev')
replace into recommend_point_statics(city_code,city_name,total_record_num,total_recmd_point_num,total_origin_point_num,date,env)
values('440100','广州市',109572,19833,1452,'2020-08-21 16:38:39', 'dev')
replace into recommend_point_statics(city_code,city_name,total_record_num,total_recmd_point_num,total_origin_point_num,date,env)
values('440100','广州市',10972,19983,1952,'2020-08-22 16:38:39', 'dev')
replace into recommend_point_statics(city_code,city_name,total_record_num,total_recmd_point_num,total_origin_point_num,date,env)
values('440100','广州市',102372,19633,1452,'2020-08-23 16:38:39', 'dev')
replace into recommend_point_statics(city_code,city_name,total_record_num,total_recmd_point_num,total_origin_point_num,date,env)
values('440100','广州市',10972,19543,1452,'2020-08-24 16:38:39', 'dev')
replace into recommend_point_statics(city_code,city_name,total_record_num,total_recmd_point_num,total_origin_point_num,date,env)
values('440100','广州市',10472,19633,162,'2020-08-25 16:38:39', 'dev')


SELECT * FROM recommend_point_statics

SELECT * FROM recommend_point_statics where city_code = '440100' and env = 'dev' and date_sub(curdate(), INTERVAL 7 DAY) <= date(`date`) and date(`date`) < curdate() GROUP BY `date`



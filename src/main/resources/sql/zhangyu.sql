CREATE TABLE wechat_token(
	access_token VARCHAR(512) NOT NULL COMMENT '微信开发者token',
	expires_time datetime NOT NULL COMMENT 'token有效截止时间',
	PRIMARY KEY(access_token)
);

CREATE TABLE wechat_msg_receiver(
	id int PRIMARY KEY AUTO_INCREMENT,
	openid VARCHAR(512) UNIQUE NOT NULL COMMENT '微信用户对每一个公众号特殊的openId',
	nickname VARCHAR(512) COMMENT '微信昵称',
	remark VARCHAR(512)  COMMENT '备注名',
	user_status int NOT NULL COMMENT '绑定启用状态，1:启用，0:停用',
	create_time datetime NULL COMMENT '创建时间',
	modify_time datetime null COMMENT '变更时间'
);

CREATE TABLE wechat_msg_record(
	id int PRIMARY KEY AUTO_INCREMENT,
	msgid VARCHAR(512) UNIQUE NOT NULL COMMENT '每一条发送的微信消息的id',
	openid VARCHAR(512) NOT NULL COMMENT '消息接收者微信openid',
	msg_details VARCHAR(512) COMMENT '消息详情内容',
	remark VARCHAR(512) COMMENT '备注信息',
	result VARCHAR(512) COMMENT '消息发送结果',
	create_time datetime NULL COMMENT '创建时间',
	modify_time datetime null COMMENT '变更时间'
);

CREATE TABLE user(
	id int PRIMARY KEY AUTO_INCREMENT,
	account VARCHAR(20) UNIQUE NOT NULL COMMENT '用户账号',
	nickname VARCHAR(20) COMMENT '昵称',
	password VARCHAR(512)  COMMENT '密码',
	user_status int NOT NULL COMMENT '绑定启用状态，1:启用，0:停用',
	create_time datetime NULL COMMENT '创建时间',
	modify_time datetime null COMMENT '变更时间',
	token VARCHAR(512)  COMMENT 'TOKEN',
	token_expire_time datetime null COMMENT 'TOKEN过期时间'
);

UPDATE user SET token=Md5(rand() + id) WHERE user_status=1

show variables like '%time_zone%';

select count(*) as totalSize
        from wechat_msg_receiver

select
		id,
		openid,
		nickname,
		user_status
from
		wechat_msg_receiver

select id, openid, msgid, remark, msg_details, result, create_time, modify_time from wechat_msg_record order by create_time desc limit 0, 10

 select id, openid, msgid, remark, msg_details, result, create_time, modify_time
 from wechat_msg_record
 WHERE  create_time between 2020-03-03 00:00:00 and 2020-03-05 00:00:00
 order by create_time desc
 limit 0, 10


CREATE TABLE wechat_token(
	access_token VARCHAR(512) NOT NULL COMMENT '微信开发者token',
	expires_time TIMESTAMP NOT NULL COMMENT 'token有效截止时间',
	PRIMARY KEY(access_token)
);

CREATE TABLE wechat_msg_receiver(
	id int PRIMARY KEY AUTO_INCREMENT,
	openid VARCHAR(512) UNIQUE NOT NULL COMMENT '微信用户对每一个公众号特殊的openId',
	nickname VARCHAR(512) COMMENT '微信昵称',
	remark VARCHAR(512)  COMMENT '备注名',
	user_status int NOT NULL COMMENT '绑定启用状态，1:启用，0:停用',
	create_time TIMESTAMP NULL COMMENT '创建时间',
	modify_time TIMESTAMP null COMMENT '变更时间'
);

CREATE TABLE wechat_msg_record(
	id int PRIMARY KEY AUTO_INCREMENT,
	msgid VARCHAR(512) UNIQUE NOT NULL COMMENT '每一条发送的微信消息的id',
	openid VARCHAR(512) NOT NULL COMMENT '消息接收者微信openid',
	msg_details VARCHAR(512) COMMENT '消息详情内容',
	remark VARCHAR(512) COMMENT '备注信息',
	result VARCHAR(512) COMMENT '消息发送结果',
	create_time TIMESTAMP NULL COMMENT '创建时间',
	modify_time TIMESTAMP null COMMENT '变更时间'
);

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

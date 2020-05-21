CREATE TABLE wechat_token(
	access_token VARCHAR(512) NOT NULL COMMENT ''微信开发者token'',
	expires_time datetime NOT NULL COMMENT ''token有效截止时间'',
	PRIMARY KEY(access_token)
);

CREATE TABLE `dot_event_record` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `event_key` varchar(255) DEFAULT NULL COMMENT ''事件id'',
  `user_id` bigint(20) DEFAULT NULL COMMENT ''用户id'',
  `device_id` varchar(255) DEFAULT NULL COMMENT ''设备id'',
  `event_detail` varchar(255) DEFAULT NULL COMMENT ''事件详情'',
  `create_time` datetime DEFAULT NULL COMMENT ''创建时间'',
  `device_brand` varchar(255) DEFAULT NULL COMMENT ''设备品牌'',
  `system_version` varchar(255) DEFAULT NULL COMMENT ''设备系统'',
  `app_versionname` varchar(255) DEFAULT NULL COMMENT ''应用版本名称'',
  `net_state` varchar(255) DEFAULT NULL COMMENT ''网络状态'',
  `location_lat` double DEFAULT NULL COMMENT ''当前位置经度'',
  `app_id` int(11) DEFAULT NULL COMMENT ''应用的id'',
  `channel` varchar(255) DEFAULT NULL COMMENT ''应用渠道'',
  `location_lng` double DEFAULT NULL COMMENT ''当前位置的纬度'',
  `ext` varchar(255) DEFAULT NULL COMMENT ''备注字段'',
  `app_versioncode` varchar(255) DEFAULT NULL COMMENT ''版本号'',
  `device_model` varchar(255) DEFAULT NULL COMMENT ''设备型号'',
  `request_ip` varchar(255) DEFAULT NULL COMMENT ''请求的ip'',
  PRIMARY KEY (`id`)
)

CREATE TABLE wechat_msg_receiver(
	id int PRIMARY KEY AUTO_INCREMENT,
	openid VARCHAR(512) UNIQUE NOT NULL COMMENT ''微信用户对每一个公众号特殊的openId'',
	nickname VARCHAR(512) COMMENT ''微信昵称'',
	remark VARCHAR(512)  COMMENT ''备注名'',
	user_status int NOT NULL COMMENT ''绑定启用状态，1:启用，0:停用'',
	create_time datetime NULL COMMENT ''创建时间'',
	modify_time datetime null COMMENT ''变更时间''
);

SELECT * FROM risk_record WHERE id=''17823''

CREATE TABLE wechat_msg_record(
	id int PRIMARY KEY AUTO_INCREMENT,
	msgid VARCHAR(512) UNIQUE NOT NULL COMMENT ''每一条发送的微信消息的id'',
	openid VARCHAR(512) NOT NULL COMMENT ''消息接收者微信openid'',
	msg_details VARCHAR(512) COMMENT ''消息详情内容'',
	remark VARCHAR(512) COMMENT ''备注信息'',
	result VARCHAR(512) COMMENT ''消息发送结果'',
	create_time datetime NULL COMMENT ''创建时间'',
	modify_time datetime null COMMENT ''变更时间''
);

CREATE TABLE user(
	id int PRIMARY KEY AUTO_INCREMENT,
	account VARCHAR(20) UNIQUE NOT NULL COMMENT ''用户账号'',
	nickname VARCHAR(20) COMMENT ''昵称'',
	password VARCHAR(512)  COMMENT ''密码'',
	user_status int NOT NULL COMMENT ''绑定启用状态，1:启用，0:停用'',
	create_time datetime NULL COMMENT ''创建时间'',
	modify_time datetime null COMMENT ''变更时间'',
	token VARCHAR(512)  COMMENT ''TOKEN'',
	token_expire_time datetime null COMMENT ''TOKEN过期时间''
);

UPDATE user SET token=Md5(rand() + id) WHERE user_status=1

show variables like ''%time_zone%'';

select count(*) as totalSize
        from wechat_msg_receiver

select
		id,
		openid,
		nickname,
		user_status
from
		wechat_msg_receiver

SELECT * FROM user WHERE token = "006590abdcfc4b6bbb2de700e2760ce2"

select id, openid, msgid, remark, msg_details, result, create_time, modify_time from wechat_msg_record order by create_time desc limit 0, 10

 select id, openid, msgid, remark, msg_details, result, create_time, modify_time
 from wechat_msg_record
 WHERE  create_time between 2020-03-03 00:00:00 and 2020-03-05 00:00:00
 order by create_time desc
 limit 0, 10

 SELECT * FROM (SELECT * FROM risk_record WHERE 1=1 AND app_id = 1 order by create_time desc limit 0, 10) as a, app_info as b,risk_user as c where a.app_id =b.app_id and a.user_id=c.user_id order by create_time desc

# 查询某个用户是否是危险用户
SELECT * FROM risk_user WHERE user_phone = 18688862482

SELECT COUNT(*) FROM risk_record

SELECT COUNT(*) FROM risk_record a, risk_user c
SELECT COUNT(*) FROM risk_record a LEFT JOIN risk_user c ON 1=1

SELECT COUNT(*) FROM risk_record a, app_info b, risk_user c where a.app_id=b.app_id and a.user_id = c.user_id
SELECT COUNT(*) FROM (SELECT * FROM risk_record LEFT JOIN (SELECT app_key, app_name, package_name ,app_id as appid FROM app_info) tb ON risk_record.app_id=tb.appid) as tc LEFT JOIN risk_user ON tc.user_id=risk_user.user_id

SELECT DISTINCT(app_id) FROM risk_record
SELECT DISTINCT(app_versionname) FROM risk_record WHERE app_versionname!=''NULL''
SELECT DISTINCT(risk_type) FROM risk_record WHERE app_id=1

SELECT * FROM (SELECT * FROM risk_record ta LEFT JOIN (SELECT app_key, app_name, package_name ,app_id as appid FROM app_info) tb ON ta.app_id=tb.appid) as tc LEFT JOIN risk_user td ON tc.user_id=td.user_id

SELECT count(*) FROM (SELECT * FROM risk_record WHERE 1=1 ) as a, app_info as b,risk_user as c where a.app_id =b.app_id and a.user_id=c.user_id

SELECT * FROM (SELECT * FROM risk_record WHERE 1=1 AND app_id = 2 order by create_time desc) as a, app_info as b,risk_user as c where a.app_id =b.app_id and a.user_id=c.user_id order by create_time desc limit 0, 10

# 查询报警记录
SELECT * FROM (SELECT * FROM risk_record WHERE 1=1 order by create_time desc ) as a, app_info as b,risk_user as c where a.app_id =b.app_id and a.user_id=c.user_id AND c.user_phone = 18688862482 order by create_time desc limit 0, 10

SELECT * FROM (SELECT * FROM risk_record WHERE 1=1 AND app_id = 2 order by create_time desc) as a, app_info as b,risk_user as c where a.app_id =b.app_id AND (a.user_id = null or a.user_id="" or a.user_id=c.user_id) order by create_time desc limit 0, 10


	 SELECT COUNT(*) FROM risk_record

SELECT count(*) FROM (SELECT * FROM risk_record WHERE 1=1 AND app_id = 1 ) as a, app_info as b,risk_user as c where a.app_id =b.app_id and a.user_id=c.user_id

SELECT * FROM (SELECT * FROM risk_record WHERE 1=1 order by create_time desc) as a, app_info as b,risk_user as c where a.app_id =b.app_id and a.user_id=c.user_id order by create_time desc limit 0, 10

# 按照风险类型数量排序展示每个风险类型的数量等信息
SELECT risk_type, COUNT(1) as totalSize, MIN(create_time) as min_time, MAX(create_time) as max_time FROM risk_record WHERE 1=1
GROUP BY risk_type ORDER BY totalSize DESC
AND app_id = ? AND create_time > ? AND create_time < ?
	limit ?, ?


SELECT count(*) FROM (SELECT * FROM risk_record WHERE 1=1 ) as a, app_info as b,risk_user as c where a.app_id =b.app_id and a.user_id=c.user_id and a.app_id=c.app_id

SELECT INSERT(user_id, 0, 1, ''*'') as uid, * FROM table_record
SELECT INSERT(user_phone, 4, 4, ''****'') as uid FROM risk_user
SELECT *, INSERT(user_phone, 4, 4, ''****'') as user_phone FROM risk_user WHERE 1=1 AND NAME<>''id''

SELECT * FROM (SELECT * FROM risk_record WHERE 1=1 AND app_versionname = ? AND device_model like concat(''%'', ?, ''%'') AND device_brand like concat(''%'', ?, ''%'') AND device_id like concat(''%'', ?, ''%'') AND risk_type like concat(''%'', ?, ''%'') AND app_id = ? AND create_time > ? AND create_time < ? order by create_time desc) as a, app_info as b,risk_user as c where a.app_id =b.app_id and a.user_id=c.user_id AND c.user_phone like concat(''%'', ?, ''%'') order by create_time desc limit ?, ?
SELECT count(*) FROM (SELECT * FROM risk_record WHERE 1=1 AND app_versionname = ? AND device_model like concat(''%'', ?, ''%'') AND device_brand like concat(''%'', ?, ''%'') AND device_id like concat(''%'', ?, ''%'') AND risk_type like concat(''%'', ?, ''%'') AND app_id = ? AND create_time > ? AND create_time < ? ) as a, app_info as b,risk_user as c where a.app_id =b.app_id and a.user_id=c.user_id AND c.user_phone like concat(''%'', ?, ''%'')

SELECT risk_record.user_id, user_phone, app_name, COUNT(1) as total_size, MIN(create_time) as min_time, MAX(create_time) as max_time FROM risk_record, app_info, risk_user WHERE 1=1 AND risk_record.app_id=app_info.app_id AND risk_record.user_id=risk_user.user_id AND risk_record.app_id=risk_user.app_id GROUP BY user_phone, risk_record.app_id ORDER BY total_size DESC limit 0, 10

SELECT * FROM (SELECT * FROM dot_event_record WHERE 1=1 AND event_key = ''EVENT_MULIT_ORDER'' order by create_time desc) as a, app_info as b where a.app_id =b.app_id order by create_time desc limit 0, 10

DELETE FROM risk_record WHERE app_id=2


SELECT COUNT(*) FROM risk_record GROUP BY user_id, risk_record.app_id

SELECT COUNT(*) FROM (SELECT risk_type FROM risk_record WHERE 1=1 GROUP BY risk_type, app_id) as ta

SELECT ta.user_id, risk_user.user_phone, app_info.app_name, ta.total_size, ta.min_time, ta.max_time FROM app_info, risk_user, (SELECT app_id, user_id, MIN(create_time) as min_time, MAX(create_time) as max_time, COUNT(1) as total_size FROM risk_record WHERE 1=1 GROUP BY app_id, user_id ORDER BY total_size DESC limit 0, 10) as ta WHERE 1=1 AND ta.app_id=app_info.app_id AND ta.user_id=risk_user.user_id AND ta.app_id=risk_user.app_id ORDER BY ta.total_size DESC

-- 0.1s+
SELECT ta.user_id, risk_user.user_phone,app_info.app_name, ta.total_size, ta.min_time, ta.max_time  FROM app_info, risk_user,
	(SELECT app_id, user_id, MIN(create_time) as min_time, MAX(create_time) as max_time ,  COUNT(1) as total_size FROM risk_record GROUP BY app_id, user_id
		ORDER BY total_size DESC
		limit 0, 10) as ta
	WHERE 1=1 AND ta.app_id=app_info.app_id AND ta.user_id=risk_user.user_id
		AND ta.app_id=risk_user.app_id
-- 70s+
SELECT risk_record.user_id, user_phone, app_name, COUNT(1) as total_size, MIN(create_time) as min_time, MAX(create_time) as max_time
	FROM risk_record, app_info, risk_user
	WHERE 1=1 AND risk_record.app_id=app_info.app_id AND risk_record.user_id=risk_user.user_id
		AND risk_record.app_id=risk_user.app_id
	GROUP BY user_phone, risk_record.app_id
	ORDER BY total_size DESC
	limit 0, 10

-- 0.1s+
SELECT COUNT(1) FROM (SELECT user_id, app_id FROM risk_record GROUP BY user_id, app_id) as ta
-- 51s+
SELECT COUNT(*) FROM (SELECT user_phone FROM risk_record, risk_user WHERE 1=1 AND risk_record.user_id=risk_user.user_id AND risk_record.app_id=risk_user.app_id GROUP BY user_phone, risk_record.app_id) as ta

-- 0.1s+	 8w+data
SELECT * FROM (SELECT * FROM risk_record WHERE 1=1 order by create_time desc limit 0, 10) as a, app_info as b,risk_user as c where a.app_id =b.app_id and a.user_id=c.user_id and a.app_id=c.app_id order by create_time desc limit 0, 10
-- 180s+	 8w+data
SELECT * FROM (SELECT * FROM risk_record WHERE 1=1 order by create_time desc) as a, app_info as b,risk_user as c where a.app_id =b.app_id and a.user_id=c.user_id and a.app_id=c.app_id order by create_time desc limit 0, 10

SELECT * FROM risk_user WHERE nick_name=''user为空''

-- 0.05s+
SELECT count(*) FROM risk_record
-- 60s+	 8w+data
SELECT count(*) FROM (SELECT * FROM risk_record WHERE 1=1 ) as a, app_info as b,risk_user as c where a.app_id =b.app_id and a.user_id=c.user_id and a.app_id=c.app_id

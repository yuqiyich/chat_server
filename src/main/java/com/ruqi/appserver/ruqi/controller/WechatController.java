package com.ruqi.appserver.ruqi.controller;

import java.sql.Timestamp;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.ruqi.appserver.ruqi.bean.BaseBean;
import com.ruqi.appserver.ruqi.bean.BasePageBean;
import com.ruqi.appserver.ruqi.bean.WechatTemplateMsgBean;
import com.ruqi.appserver.ruqi.dao.entity.WechatAccessTokenEntity;
import com.ruqi.appserver.ruqi.dao.entity.WechatMsgReceiverEntity;
import com.ruqi.appserver.ruqi.network.BaseHttpClient;
import com.ruqi.appserver.ruqi.network.UrlConstant;
import com.ruqi.appserver.ruqi.network.WechatConstant;
import com.ruqi.appserver.ruqi.service.WechatService;
import com.ruqi.appserver.ruqi.utils.DateTimeUtils;
import com.ruqi.appserver.ruqi.utils.EncryptUtils;
import com.ruqi.appserver.ruqi.utils.MyStringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "微信API模块")
@RequestMapping(value = "/wechat")
public class WechatController {

	@Autowired
	private WechatService wechatService;
	
	/**
	 * 本地、数据库、网络，获取token。
	 * @return
	 */
    private String getAccessToken() {
		String accessToken = wechatService.queryAccessToken();

		if (MyStringUtils.isEmpty(accessToken)) {
			// api调用获取token，且保存到数据库
			String paramGetToken = String.format("grant_type=client_credential&appid=%s&secret=%s", WechatConstant.APP_ID, WechatConstant.SECRET);
			String result = BaseHttpClient.sendGet(UrlConstant.WeChatUrl.TOKEN_GET, paramGetToken);

			if (!MyStringUtils.isEmpty(result)) {
				try {
					JSONObject json = JSONObject.parseObject(result);
					if (json.containsKey("access_token")) {
						accessToken = json.getString("access_token");
						WechatAccessTokenEntity entity = new WechatAccessTokenEntity();
						entity.accessToken = accessToken;
						entity.expiresTime = new Timestamp(System.currentTimeMillis() 
							+ json.getLong("expires_in").longValue() * 1000 - 600000); // 过期时间，减少10分钟
						wechatService.updateAccessToken(entity);
					} else {
						System.out.println("--->获取accessToken失败，result: " + result);
					}
					// {"access_token":"ACCESS_TOKEN","expires_in":7200}
					// {"errcode":40013,"errmsg":"invalid appid"}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (MyStringUtils.isEmpty(accessToken)) {
			System.out.println("--->获取accessToken失败，请检查具体原因");
		} else {
			System.out.println("--->获取到了accessToken");
		}

		return accessToken;
	}

	/**
	 * TODO：暂时可以通过 api调用。后续应该只能由其他逻辑调用。
	 * @return
	 */
	@ApiOperation(value = "微信公众号模板消息发送", notes = "报警通知")
	@RequestMapping(value = "/template/send", method = RequestMethod.GET)
	// @ResponseBody
    public String sendTemplateMsg() {
		String accessToken = getAccessToken();
		
		if (MyStringUtils.isEmpty(accessToken)) {
			return "获取token失败，无法进行发送消息操作";
		}

		List<WechatMsgReceiverEntity> userList = wechatService.queryAvailableReceivers();
		for (WechatMsgReceiverEntity entity : userList) {
			WechatTemplateMsgBean wechatTemplateMsgBean = new WechatTemplateMsgBean();
			wechatTemplateMsgBean.touser = EncryptUtils.decode(entity.openid);
			wechatTemplateMsgBean.template_id = WechatConstant.TEMPLATE_ID;
			wechatTemplateMsgBean.data = new WechatTemplateMsgBean.TemplateData();
			wechatTemplateMsgBean.data.first = new WechatTemplateMsgBean.TemplateItemData();
			wechatTemplateMsgBean.data.first.value = "司机端APP"; // 标题
			wechatTemplateMsgBean.data.first.color = "#498be7";
			wechatTemplateMsgBean.data.keyword1 = new WechatTemplateMsgBean.TemplateItemData();
			wechatTemplateMsgBean.data.keyword1.value = "多开"; // 报警类型
			wechatTemplateMsgBean.data.keyword1.color = "#498be7";
			wechatTemplateMsgBean.data.keyword2 = new WechatTemplateMsgBean.TemplateItemData();
			wechatTemplateMsgBean.data.keyword2.value = DateTimeUtils.getCurrentTime(); // 报警时间
			wechatTemplateMsgBean.data.keyword2.color = "#498be7";
			wechatTemplateMsgBean.data.keyword3 = new WechatTemplateMsgBean.TemplateItemData();
			wechatTemplateMsgBean.data.keyword3.value = "比翼双开"; // 详细信息
			wechatTemplateMsgBean.data.keyword3.color = "#498be7";
			wechatTemplateMsgBean.data.remark = new WechatTemplateMsgBean.TemplateItemData();
			wechatTemplateMsgBean.data.remark.value = "请判断安全风险等级，由对应负责人进行相应处理。"; // 备注
			wechatTemplateMsgBean.data.remark.color = "#FFBF13";
			String result = BaseHttpClient.doPost(UrlConstant.WeChatUrl.TEMPLATE_MSG_SEND + accessToken, wechatTemplateMsgBean);

			// {"errcode":0,"errmsg":"ok","msgid":1226691075109289985}

			System.out.println("--->发消息result=" + result);
			// 如果接口返回token失效则获取token再重试

			// TODO：保存消息id、安全报警id、微信用户的对应id、发送结果、备注
		}

		return "size:" + (null == userList ? 0 : userList.size());
	}

	// 写一个回调接口，接收xml返回记录模板消息发送结果

	/**
	 * 查询获取微信公众号消息接收者列表
	 * @return
	 */
	@ApiOperation(value = "查询获取微信公众号消息接收者列表", notes = "")
	@ApiImplicitParams({
		@ApiImplicitParam (dataType = "Integer", name = "pageIndex", value = "页码，如0", defaultValue = "0", required = false)
		,@ApiImplicitParam (dataType = "Integer", name = "pageSize", value = "size，如10", defaultValue = "10" ,required = false)
		,@ApiImplicitParam (dataType = "string", name = "nickname", value = "微信昵称", required = false)
		,@ApiImplicitParam (dataType = "string", name = "remarks", value = "备注名", required = false)
		,@ApiImplicitParam (dataType = "string", name = "userStatus", value = "不传表示查询所有。1:启用，0:停用", required = false)
	})
	@RequestMapping(value = "/receiver/list", method = RequestMethod.GET)
	@ResponseBody
	public BaseBean<BasePageBean<WechatMsgReceiverEntity>> getReceiverList(Integer  pageIndex,
		Integer pageSize, String nickname, String remarks, String userStatus) {
		BaseBean<BasePageBean<WechatMsgReceiverEntity>> result = new BaseBean<BasePageBean<WechatMsgReceiverEntity>>();
		
		List<WechatMsgReceiverEntity> receiverEntities = wechatService.queryReceivers(pageIndex, pageSize, nickname, remarks, userStatus);
		long totalSize = wechatService.queryReceiverSize(nickname, remarks, userStatus);
		result.data = new BasePageBean<WechatMsgReceiverEntity>(pageIndex, pageSize, totalSize, receiverEntities);
		
		return result;
	}
}
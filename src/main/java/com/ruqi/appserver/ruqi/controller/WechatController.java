package com.ruqi.appserver.ruqi.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Timestamp;

import com.alibaba.fastjson.JSONObject;
import com.ruqi.appserver.ruqi.bean.WechatTemplateMsgBean;
import com.ruqi.appserver.ruqi.dao.entity.WechatAccessTokenEntity;
import com.ruqi.appserver.ruqi.network.BaseHttpClient;
import com.ruqi.appserver.ruqi.network.UrlConstant;
import com.ruqi.appserver.ruqi.network.WechatConstant;
import com.ruqi.appserver.ruqi.service.WechatService;
import com.ruqi.appserver.ruqi.utils.DateTimeUtils;
import com.ruqi.appserver.ruqi.utils.MyStringUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(value = "微信模块")
@RequestMapping(value = "/wechat")
public class WechatController {

	@Autowired
	private WechatService wechatService;
	
	/**
	 * 扫码，打开网址后内部调用微信的authorize接口得到对应的CODE，会触发打开回调URL，里面能拿到CODE。这个接口可做一些判断校验处理，如过期失效等。
	 * 
	 * @return
	 */
	@ApiOperation(value = "微信公众号authorize获取", notes = "一般是打开链接后调用微信api获取authorize")
	@RequestMapping(value = "/authorize", method = RequestMethod.GET)
	// @ResponseBody
	public String startAuthorize() {
		// 跳转到打开微信的url，如何跳转重定向打开？

		String redirectUri = WechatConstant.REDIRECT_URI_OPEN_ID;
		try {
			redirectUri = URLEncoder.encode(redirectUri, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String url = String.format("https://open.weixin.qq.com/connect/oauth2/authorize?appid=%s&redirect_uri=%s&response_type=code&scope=snsapi_base&state=STATE#wechat_redirect",
			WechatConstant.APP_ID, redirectUri);

		return url;
	}

	/**
	 * 扫码，打开网址后内部调用微信的authorize接口得到对应的CODE，会触发打开回调URL，里面能拿到CODE。这个接口可做一些判断校验处理，如过期失效等。
	 * @return
	 */
	@ApiOperation(value = "微信公众号获取openId回调地址", notes = "回调地址，会带上code参数，接着执行根据code获取openId的方法")
	@ApiImplicitParams({@ApiImplicitParam(name = "code", value = "授权接口返回的code", required = true, dataType = "String")})
	@RequestMapping(value = "/redirectUri", method = RequestMethod.GET)
	// @ResponseBody
    public String redirectUri(String code) {
		// 拿到对应的openId，与某个微信进行绑定，能获取到微信昵称等？ 保存在数据库，后续发送模板消息使用这个openId。
		
		String result = BaseHttpClient.sendGet(String.format(UrlConstant.WeChatUrl.OPEN_ID_GET, 
			WechatConstant.APP_ID, WechatConstant.SECRET, code), "");

		return result;
	}

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

	@ApiOperation(value = "微信公众号模板消息发送", notes = "报警通知")
	@ApiImplicitParams({@ApiImplicitParam(name = "touser", value = "接收者openid", required = true, dataType = "String")})
	@RequestMapping(value = "/template/sendTo/{touser}", method = RequestMethod.GET)
	// @ResponseBody
    public String sendTemplateMsg(@PathVariable("touser") String touser) {
		String accessToken = getAccessToken();
		
		if (MyStringUtils.isEmpty(accessToken)) {
			return "获取token失败，无法进行发送消息操作";
		}

		WechatTemplateMsgBean wechatTemplateMsgBean = new WechatTemplateMsgBean();
		wechatTemplateMsgBean.touser = touser;
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

		return result;
	}

	// 写一个回调接口，接收xml返回记录模板消息发送结果
}
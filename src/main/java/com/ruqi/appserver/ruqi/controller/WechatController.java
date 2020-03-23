package com.ruqi.appserver.ruqi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ruqi.appserver.ruqi.bean.BaseBean;
import com.ruqi.appserver.ruqi.bean.BaseMapBean;
import com.ruqi.appserver.ruqi.bean.BasePageBean;
import com.ruqi.appserver.ruqi.bean.WechatTemplateMsgBean;
import com.ruqi.appserver.ruqi.dao.entity.WechatAccessTokenEntity;
import com.ruqi.appserver.ruqi.dao.entity.WechatMsgEntity;
import com.ruqi.appserver.ruqi.dao.entity.WechatMsgReceiverEntity;
import com.ruqi.appserver.ruqi.network.BaseHttpClient;
import com.ruqi.appserver.ruqi.network.UrlConstant;
import com.ruqi.appserver.ruqi.network.WechatConstant;
import com.ruqi.appserver.ruqi.service.WechatMsgService;
import com.ruqi.appserver.ruqi.service.WechatService;
import com.ruqi.appserver.ruqi.utils.DateTimeUtils;
import com.ruqi.appserver.ruqi.utils.EncryptUtils;
import com.ruqi.appserver.ruqi.utils.MyStringUtils;
import com.ruqi.appserver.ruqi.utils.WxUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@Api(tags = "微信API模块")
@RequestMapping(value = "/wechat")
public class WechatController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private WechatService wechatService;

    @Autowired
    private WechatMsgService wechatMsgService;

    /**
     * 本地、数据库、网络，获取token。
     *
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
                        entity.expiresTime = new Date(System.currentTimeMillis()
                                + json.getLong("expires_in").longValue() * 1000 - 600000); // 过期时间，减少10分钟
                        wechatService.updateAccessToken(entity);
                    } else {
                        logger.info("--->获取accessToken失败，result: " + result);
                    }
                    // {"access_token":"ACCESS_TOKEN","expires_in":7200}
                    // {"errcode":40013,"errmsg":"invalid appid"}
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        if (MyStringUtils.isEmpty(accessToken)) {
            logger.info("--->获取accessToken失败，请检查具体原因");
        } else {
            logger.info("--->获取到了accessToken: " + accessToken);
        }

        return accessToken;
    }

    /**
     * 全量测试推送
     *
     * @return
     */
    @ApiOperation(value = "微信公众号模板消息全量测试发送", notes = "报警通知")
    @RequestMapping(value = "/template/testSendAll", method = RequestMethod.POST)
    @ResponseBody
    public BaseMapBean testSendAllTemplateMsg() {
        return sendSecurityTemplateMsg("test司机端APP", "test报警类型",
                "test详细信息", "test请判断安全风险等级，由对应负责人进行相应处理。", null);
    }

    /**
     * 单个测试推送
     *
     * @param openId
     * @return
     */
    @ApiOperation(value = "微信公众号模板消息指定发送", notes = "报警通知")
    @RequestMapping(value = "/template/testSend", method = RequestMethod.POST)
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "String", name = "openId", value = "微信openId", required = true)
    })
    @ResponseBody
    public BaseMapBean testSendTemplateMsg(@RequestBody String openId) {
        List<WechatMsgReceiverEntity> receiverEntityList = new ArrayList<>();
        WechatMsgReceiverEntity receiverEntity = new WechatMsgReceiverEntity();
        receiverEntity.openid = openId;
        logger.info("--->发送微信消息，openId:" + openId);
        receiverEntityList.add(receiverEntity);
        return sendSecurityTemplateMsg("test司机端APP", "test报警类型",
                "test详细信息", "test请判断安全风险等级，由对应负责人进行相应处理。", receiverEntityList);
    }

    public BaseMapBean sendSecurityTemplateMsg(String msgTitle, String msgType, String msgDetail,
                                               String msgRemark, List<WechatMsgReceiverEntity> receiverEntityList) {
        String accessToken = getAccessToken();

        BaseMapBean baseBean = new BaseMapBean();

        if (MyStringUtils.isEmpty(accessToken)) {
            baseBean.errorCode = 10001;
            baseBean.errorMsg = "获取token失败，无法进行发送消息操作";
            return baseBean;
        }

        if (null == receiverEntityList || receiverEntityList.size() == 0) {
            logger.info("--->发送微信消息，未给定用户，全量启用账号推送");
            receiverEntityList = wechatService.queryAvailableReceivers();
        } else {
            logger.info("--->发送微信消息，给定用户：" + JSON.toJSONString(receiverEntityList));
        }
        for (WechatMsgReceiverEntity entity : receiverEntityList) {
            WechatTemplateMsgBean wechatTemplateMsgBean = new WechatTemplateMsgBean();
            wechatTemplateMsgBean.touser = EncryptUtils.decode(entity.openid);
            wechatTemplateMsgBean.template_id = WechatConstant.TEMPLATE_ID;
            wechatTemplateMsgBean.data = new WechatTemplateMsgBean.TemplateData();
            wechatTemplateMsgBean.data.first = new WechatTemplateMsgBean.TemplateItemData();
            wechatTemplateMsgBean.data.first.value = msgTitle; // 标题
            wechatTemplateMsgBean.data.first.color = "#498be7";
            wechatTemplateMsgBean.data.keyword1 = new WechatTemplateMsgBean.TemplateItemData();
            wechatTemplateMsgBean.data.keyword1.value = msgType; // 报警类型
            wechatTemplateMsgBean.data.keyword1.color = "#498be7";
            wechatTemplateMsgBean.data.keyword2 = new WechatTemplateMsgBean.TemplateItemData();
            wechatTemplateMsgBean.data.keyword2.value = DateTimeUtils.getCurrentTime(); // 报警时间
            wechatTemplateMsgBean.data.keyword2.color = "#498be7";
            wechatTemplateMsgBean.data.keyword3 = new WechatTemplateMsgBean.TemplateItemData();
            wechatTemplateMsgBean.data.keyword3.value = msgDetail; // 详细信息
            wechatTemplateMsgBean.data.keyword3.color = "#498be7";
            wechatTemplateMsgBean.data.remark = new WechatTemplateMsgBean.TemplateItemData();
            wechatTemplateMsgBean.data.remark.value = msgRemark; // 备注
            // wechatTemplateMsgBean.data.remark.color = "#FFBF13";
            String result = BaseHttpClient.doPost(UrlConstant.WeChatUrl.TEMPLATE_MSG_SEND + accessToken, wechatTemplateMsgBean);

            // {"errcode":0,"errmsg":"ok","msgid":1226691075109289985}

            logger.info("--->发消息result=" + result);
            // 如果接口返回token失效则获取token再重试

            // 获取msgid保存数据库
            try {
                JSONObject json = JSONObject.parseObject(result);
                if (json.containsKey("msgid")) {
                    String msgid = json.getString("msgid");
                    WechatMsgEntity msgEntity = new WechatMsgEntity();
                    msgEntity.msgDetails = msgTitle + "\n" + msgType + "\n" + msgDetail + "\n" + msgRemark;
                    msgEntity.msgid = msgid;
                    msgEntity.openid = entity.openid;
                    wechatMsgService.addWechatMsgRecord(msgEntity);
                } else {
                    logger.info("--->发送消息失败，result: " + result);
                }
                // {"access_token":"ACCESS_TOKEN","expires_in":7200}
                // {"errcode":40013,"errmsg":"invalid appid"}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        int size = null == receiverEntityList ? 0 : receiverEntityList.size();
        Map<String, Object> map = new HashMap<>();
        map.put("size", size);
        baseBean.data = map;

        logger.info("--->result:" + JSON.toJSONString(baseBean));

        return baseBean;
    }

    /**
     * 查询获取微信公众号消息接收者列表
     *
     * @return
     */
    @ApiOperation(value = "查询获取微信公众号消息接收者列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "page", value = "页码，从1开始")
            , @ApiImplicitParam(dataType = "Integer", name = "limit", value = "size，如10")
            , @ApiImplicitParam(dataType = "string", name = "nickname", value = "微信昵称")
            , @ApiImplicitParam(dataType = "string", name = "remarks", value = "备注名")
            , @ApiImplicitParam(dataType = "string", name = "userStatus", value = "不传表示查询所有。1:启用，0:停用")
    })
    @RequestMapping(value = "/receiver/list", method = RequestMethod.GET)
    @ResponseBody
    public BaseBean<BasePageBean<WechatMsgReceiverEntity>> getReceiverList(@RequestParam(defaultValue = "1") Integer page,
                                                                           @RequestParam(defaultValue = "10") Integer limit, String nickname, String remarks, String userStatus) {
        BaseBean<BasePageBean<WechatMsgReceiverEntity>> result = new BaseBean<>();

        // 页码从1开始，但是sql中从0开始
        List<WechatMsgReceiverEntity> receiverEntities = wechatService.queryReceivers(page - 1, limit, nickname, remarks, userStatus);
        long totalSize = wechatService.queryReceiverSize(nickname, remarks, userStatus);
        result.data = new BasePageBean<>(page, limit, totalSize, receiverEntities);

        return result;
    }

    /**
     * 更新微信公众号消息接收者信息
     *
     * @return
     */
    @ApiOperation(value = "更新微信公众号消息接收者信息", notes = "备注名、状态")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "WechatMsgReceiverEntity",
                    name = "receiverEntity", value = "微信接收消息者bean", required = true, paramType = "body")
    })
    @RequestMapping(value = "/receiver/update", method = RequestMethod.POST)
    @ResponseBody
    public BaseMapBean updateReceiverInfo(@RequestBody WechatMsgReceiverEntity receiverEntity) {
        BaseMapBean result = new BaseMapBean();

        wechatService.updateReceiver(receiverEntity);

        return result;
    }

    /**
     * 微信模板消息发送后接收微信 回调消息发送结果
     */
    @RequestMapping(value = "/msgRedirectUri")
    public String msgRedirectUri(HttpServletRequest request) {
        String content = request.getQueryString();
        String result = "";
        try {
            result = WxUtils.checkSignature(content);
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger.info("--->msgRedirectUri result=" + result);

        // xml格式数据返回，读取MsgID和Status，数据库中更新消息的result字段。
        return result;
    }

    /**
     * 查询微信公众号消息发送记录列表
     */
    @ApiOperation(value = "查询获取微信公众号消息发送记录列表", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "Integer", name = "page", value = "页码，从1开始")
            , @ApiImplicitParam(dataType = "Integer", name = "limit", value = "size，如10")
            , @ApiImplicitParam(dataType = "string", name = "openid", value = "微信openid")
            , @ApiImplicitParam(dataType = "string", name = "msgid", value = "微信msgid")
            , @ApiImplicitParam(dataType = "string", name = "details", value = "消息内容")
            , @ApiImplicitParam(dataType = "string", name = "remark", value = "备注")
            , @ApiImplicitParam(dataType = "string", name = "result", value = "消息发送结果")
            , @ApiImplicitParam(dataType = "string", name = "startTime", value = "查询的开始时间，开始时间存在时结束时间也应该存在")
            , @ApiImplicitParam(dataType = "string", name = "endTime", value = "查询的结束时间")
    })
    @RequestMapping(value = "/msg/list", method = RequestMethod.GET)
    @ResponseBody
    public BaseBean<BasePageBean<WechatMsgEntity>> getMsgList(@RequestParam(defaultValue = "1") Integer page,
                                                              @RequestParam(defaultValue = "10") Integer limit, String openid, String msgid,
                                                              String details, String remark, String result, String startTime, String endTime) {
        BaseBean<BasePageBean<WechatMsgEntity>> resultBean = new BaseBean<>();

        // 页码从1开始，但是sql中从0开始
        List<WechatMsgEntity> receiverEntities = wechatMsgService.queryMsgList(page - 1, limit, openid, msgid, details, remark, result, startTime, endTime);
        long totalSize = wechatMsgService.queryMsgSize(openid, msgid, details, remark, result, startTime, endTime);
        resultBean.data = new BasePageBean<>(page, limit, totalSize, receiverEntities);

//        logger.info("--->" + new Gson().toJson(receiverEntities));

        return resultBean;
    }

    /**
     * 更新微信公众号消息记录的备注
     *
     * @return
     */
    @ApiOperation(value = "更新微信公众号消息接收者信息", notes = "备注")
    @ApiImplicitParams({
            @ApiImplicitParam(dataType = "WechatMsgEntity", name = "wechatMsgEntity", value = "修改备注信息的消息记录", paramType = "application/json;")
    })
    @RequestMapping(value = "/msg/update", method = RequestMethod.POST)
    @ResponseBody
    public BaseMapBean updateMsgRemark(@RequestBody WechatMsgEntity wechatMsgEntity) {
        BaseMapBean result = new BaseMapBean();

        long id = wechatMsgService.updateMsgRemark(wechatMsgEntity);
        logger.info("--->updateMsgRemark=" + id);

        return result;
    }
}
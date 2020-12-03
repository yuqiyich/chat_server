package com.ruqi.appserver.ruqi.controller.miniprogram;

import com.ruqi.appserver.ruqi.aspect.ApiVersion;
import com.ruqi.appserver.ruqi.bean.BaseBean;
import com.ruqi.appserver.ruqi.bean.request.miniprogram.MiniProgramAdRequest;
import com.ruqi.appserver.ruqi.bean.response.miniprogram.MiniProgramAdInfoResp;
import com.ruqi.appserver.ruqi.network.ErrorCodeMsg;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;

@RestController
@Api(tags = "微信小程序")
@RequestMapping(value = "/miniProgram")
public class MiniProgramController {
    private Logger logger = LoggerFactory.getLogger(getClass());

    @ApiOperation(value = "查询广告数据", notes = "")
    @RequestMapping(value = "/{apiVersion}/queryAds", method = RequestMethod.POST)
    @ResponseBody
    @CrossOrigin
    @ApiVersion()
    public BaseBean<MiniProgramAdInfoResp> newlogin(@Validated @RequestBody MiniProgramAdRequest request, BindingResult bindingResult) {
        BaseBean<MiniProgramAdInfoResp> result = new BaseBean();
        if (bindingResult.hasErrors()) {
            result.errorCode = ErrorCodeMsg.ERROR_INVALID_PARAMS.errorCode;
            result.errorMsg = bindingResult.getFieldError().getDefaultMessage();
        } else {
            result.data = new MiniProgramAdInfoResp();
            result.data.adList = new ArrayList<>();
            result.data.adList.add(new MiniProgramAdInfoResp.MiniProgramAdInfo("https://ss3.bdstatic.com/70cFv8Sh_Q1YnxGkpoWK1HF6hhy/it/u=1422969837,1773444198&fm=26&gp=0.jpg"));
            result.data.adList.add(new MiniProgramAdInfoResp.MiniProgramAdInfo("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1606921742527&di=c945e167a49c4050f6e4fe458d45f772&imgtype=0&src=http%3A%2F%2Fimg.pconline.com.cn%2Fimages%2Fupload%2Fupc%2Ftx%2Fwallpaper%2F1606%2F30%2Fc3%2F23589301_1467290861869_800x800.jpg"));
        }
        return result;
    }
}

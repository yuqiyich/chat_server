package com.ruqi.appserver.ruqi.bean.response.miniprogram;

import java.util.List;

/**
 * @author ZhangYu
 * @date 2020/12/2
 * @desc 小程序广告信息
 */
public class MiniProgramAdInfoResp {

    public List<MiniProgramAdInfo> adList;

    public static class MiniProgramAdInfo {
        public String imgUrl;

        public MiniProgramAdInfo() {
        }

        public MiniProgramAdInfo(String imgUrl) {
            this.imgUrl = imgUrl;
        }
    }

}

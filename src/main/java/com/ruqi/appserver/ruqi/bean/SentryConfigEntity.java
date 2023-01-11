package com.ruqi.appserver.ruqi.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * Author:liangbingkun
 * Time:2020/8/21
 * Description: sentry 开关
 */
@ApiModel(value = "SentryConfigEntity")
public class SentryConfigEntity {
    @ApiModelProperty(value = "sentry日志开关，1：开 ，0：关")
    private String sentrySwitch;
    @ApiModelProperty(value = "上报日志的最高级别，v>d>i>w>e")
    private String level;
    @ApiModelProperty(value = "sentry上报的地址设置")
    private String dns;
    @ApiModelProperty(value = "sentry上报的区域限制，如果有，则只上报命中的区域")
    private int areaSwitch;
    @ApiModelProperty(value = "sentry上报的轮询API的最大次数，默认是100")
    private int loopMaxCount;
    @ApiModelProperty(value = "项目的id")
    private int id;//项目id
    @ApiModelProperty(value = "sentry的采样率")
    private double sampleRate;//sentry的采样率

    @ApiModelProperty(value = "性能采样率, 默认为1")
    private double tracesSampleRate;

    @ApiModelProperty(value = "是否检测pv/uv数据 默认为true")
    private boolean  isHandleVisit;

    @ApiModelProperty(value = "pv/uv采样率，目前pv/uv数据全部采集， 默认为1")
    private double  visitSampleRate;

    @ApiModelProperty(value = "是否检测api接口数据 默认为true")
    private boolean  isHandleApi;

    @ApiModelProperty(value = "api采样率，目前api数据采样10%，默认为0.1")
    private double  apiSampleRate;

    @ApiModelProperty(value = "检测静态资源, 默认为true")
    private boolean  isHandleResource;

    @ApiModelProperty(value = "资源采样率，目前资源数据采样10%，默认为0.1")
    private double  resourceSampleRate;

    public List<String> getTags() {
        return tags;
    }

    public int getLoopMaxCount() {
        return loopMaxCount;
    }

    public void setLoopMaxCount(int loopMaxCount) {
        this.loopMaxCount = loopMaxCount;
    }


    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public int getAreaSwitch() {
        return areaSwitch;
    }

    public void setAreaSwitch(int areaSwitch) {
        this.areaSwitch = areaSwitch;
    }
    @ApiModelProperty(value = "上报的日志tag值集合，如果有命中tags集合则上报（在sentrySwitch为1的情况下，则不受其他条件限制）")
    private List<String> tags;

    public String getSentrySwitch() {
        return sentrySwitch;
    }

    public void setSentrySwitch(String sentrySwitch) {
        this.sentrySwitch = sentrySwitch;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }

    public String getDns() {
        return dns;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(double sampleRate) {
        this.sampleRate = sampleRate;
    }

    public double getTracesSampleRate() {
        return tracesSampleRate;
    }

    public void setTracesSampleRate(double tracesSampleRate) {
        this.tracesSampleRate = tracesSampleRate;
    }

    public boolean isHandleVisit() {
        return isHandleVisit;
    }

    public void setHandleVisit(boolean handleVisit) {
        isHandleVisit = handleVisit;
    }

    public double getVisitSampleRate() {
        return visitSampleRate;
    }

    public void setVisitSampleRate(double visitSampleRate) {
        this.visitSampleRate = visitSampleRate;
    }

    public boolean isHandleApi() {
        return isHandleApi;
    }

    public void setHandleApi(boolean handleApi) {
        isHandleApi = handleApi;
    }

    public double getApiSampleRate() {
        return apiSampleRate;
    }

    public void setApiSampleRate(double apiSampleRate) {
        this.apiSampleRate = apiSampleRate;
    }

    public boolean isHandleResource() {
        return isHandleResource;
    }

    public void setHandleResource(boolean handleResource) {
        isHandleResource = handleResource;
    }

    public double getResourceSampleRate() {
        return resourceSampleRate;
    }

    public void setResourceSampleRate(double resourceSampleRate) {
        this.resourceSampleRate = resourceSampleRate;
    }

}

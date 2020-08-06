package com.ruqi.appserver.ruqi.aspect;

import com.ruqi.appserver.ruqi.bean.BaseCodeMsgBean;
import com.ruqi.appserver.ruqi.kafka.BaseKafkaLogInfo;
import com.ruqi.appserver.ruqi.kafka.KafkaProducer;
import com.ruqi.appserver.ruqi.utils.HeaderMapUtils;
import com.ruqi.appserver.ruqi.utils.JsonUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Aspect
@Component
public class LogAspect {
    private Logger logger = LoggerFactory.getLogger(LogAspect.class);

    @Autowired
    protected KafkaProducer kafkaProducer;

    @Resource
    private HttpServletRequest request;

    /**
     * 定义切点
     */
    @Pointcut("execution(* com.ruqi.appserver.ruqi.controller..*.*(..))")
    public void log() {
    }

    @Around(value = "log() && @annotation(logAnnotation)")
    public Object around(ProceedingJoinPoint joinPoint, LogAnnotation logAnnotation) throws Throwable {
        Object[] args = joinPoint.getArgs();
        List<Object> arguments = new ArrayList<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof ServletRequest || args[i] instanceof ServletResponse || args[i] instanceof MultipartFile) {
                //ServletRequest不能序列化，从入参里排除，否则报异常：java.lang.IllegalStateException: It is illegal to call this method if the current request is not in asynchronous mode (i.e. isAsyncStarted() returns false)
                //ServletResponse不能序列化 从入参里排除，否则报异常：java.lang.IllegalStateException: getOutputStream() has already been called for this response
                continue;
            }
            arguments.add(args[i]);
        }
        String paramter = "";
        if (arguments != null) {
            try {
                paramter = JsonUtil.beanToJsonStr(arguments);
            } catch (Exception e) {
                paramter = arguments.toString();
            }
        }

        // 目标方法执行前。这里可以加上自己的逻辑
//        logger.info("目标方法执行前:" + paramter);
        // 执行目标接口
        Object object = joinPoint.proceed();
        boolean isError = (null != object && object instanceof BaseCodeMsgBean &&
                ((BaseCodeMsgBean) object).errorCode != 0) ? true : false;
        // 目标方法执行后
//        logger.info("目标方法执行后");
        // 调用异步方法
        kafkaProducer.sendLog(isError ? BaseKafkaLogInfo.LogLevel.ERROR : BaseKafkaLogInfo.LogLevel.INFO,
                String.format("request:[%s], head:[%s], parameter:[%s], response:[%s]",
                        JsonUtil.beanToJsonStr(request.getRequestURL()),
                        JsonUtil.beanToJsonStr(HeaderMapUtils.getAllHeaderParamMaps(request)),
                        paramter, JsonUtil.beanToJsonStr(object)));
        return object;
    }

}

package com.ruqi.appserver.ruqi.controller;

import com.ruqi.appserver.ruqi.bean.BaseBean;
import com.ruqi.appserver.ruqi.network.ErrorCodeMsg;
import org.springframework.validation.BindingResult;

public class BaseController {

    protected BaseBean checkRequestInvalid(BindingResult bindingResult) {
        BaseBean result = new BaseBean();
        if (bindingResult.hasErrors()) {
            result.errorCode = ErrorCodeMsg.ERROR_INVALID_PARAMS.errorCode;
            result.errorMsg = bindingResult.getFieldError().getDefaultMessage();
            return result;
        } else {
            return null;
        }
    }
}

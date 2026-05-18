package com.wms.auth.service;

import com.wms.auth.domain.vo.CaptchaResp;

public interface CaptchaService {

    CaptchaResp generateCaptcha();

    void validateCaptcha(String captchaKey, String captchaText);
}

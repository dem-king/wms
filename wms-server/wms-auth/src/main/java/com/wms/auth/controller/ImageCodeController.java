package com.wms.auth.controller;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.wms.auth.domain.constant.AuthConstants;
import com.wms.common.domain.R;
import com.wms.common.util.SpringUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 行为验证码控制器
 * 提供验证码图片获取（GET /auth/code/get）和坐标校验（POST /auth/code/check）接口，
 * 委托 anji-plus/captcha SDK 的 CaptchaService 处理
 *
 * @author wms-team
 * @since 1.0
 */
@Tag(name = "行为验证码")
@RestController
@RequestMapping("/auth/code")
@RequiredArgsConstructor
public class ImageCodeController {

    /**
     * 获取验证码图片
     * 生成滑动拼图或文字点选验证码，返回背景图Base64、拼图块Base64、token和secretKey
     *
     * @param captchaVO 验证码请求参数（captchaType: blockPuzzle 或 clickWord）
     * @return 验证码响应（repData 包含 originalImageBase64/jigsawImageBase64/token/secretKey）
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取验证码图片")
    @GetMapping("/get")
    public R<ResponseModel> getCode(CaptchaVO captchaVO) {
        CaptchaVO vo = new CaptchaVO();
        vo.setCaptchaType(AuthConstants.CAPTCHA_TYPE_BLOCK_PUZZLE);
        CaptchaService captchaService = SpringUtils.getBean(CaptchaService.class);
        return R.ok(captchaService.get(vo));
    }

    /**
     * 校验验证码坐标
     * 前端用 AES 加密坐标后回传，校验通过返回 captchaVerification 二次校验串
     *
     * @param captchaVO 验证码校验参数（token + pointJson + captchaType）
     * @return 校验结果（repData.captchaVerification 为二次校验串，登录时回传）
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "校验验证码坐标")
    @PostMapping("/check")
    public R<ResponseModel> check(CaptchaVO captchaVO) {
        CaptchaVO vo = new CaptchaVO();
        vo.setPointJson(captchaVO.getPointJson());
        vo.setToken(captchaVO.getToken());
        vo.setCaptchaType(AuthConstants.CAPTCHA_TYPE_BLOCK_PUZZLE);
        CaptchaService captchaService = SpringUtils.getBean(CaptchaService.class);
        return R.ok(captchaService.check(vo));
    }
}
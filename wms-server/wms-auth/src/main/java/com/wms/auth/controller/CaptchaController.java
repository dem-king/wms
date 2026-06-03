package com.wms.auth.controller;

import com.wms.auth.domain.vo.CaptchaImageResp;
import com.wms.auth.service.CaptchaService;
import com.wms.common.domain.R;
import com.wms.common.util.IpUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 验证码管理
 * 提供滑块拼图验证码生成接口，基于 tianai-captcha SDK 实现
 * <p>
 * 路径由旧版 /image 切换为 /slider，以与未来 word-click/rotate 等其他类型区分
 *
 * @author wms-team
 * @since 1.0
 */
@Tag(name = "验证码管理")
@RestController
@RequestMapping("/auth/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    /**
     * 获取滑块拼图验证码
     * 生成背景图、拼图块、缺口 Y 坐标和验证 Token，同时将客户端指纹（IP+UA 哈希）写入 Redis 用于后续校验防重放
     *
     * @param request HTTP 请求，用于提取客户端 IP 与 User-Agent
     * @return 滑块拼图响应（背景图 Base64 / 拼图块 Base64 / 缺口 Y / Token / 过期秒数）
     */
    @PreAuthorize("permitAll()")
    @Operation(summary = "获取滑块拼图验证码")
    @GetMapping("/slider")
    public R<CaptchaImageResp> getSliderCaptcha(HttpServletRequest request) {
        String clientIp = IpUtil.getIpAddr(request);
        String userAgent = request.getHeader("User-Agent");
        return R.ok(captchaService.generateCaptcha(clientIp, userAgent));
    }
}

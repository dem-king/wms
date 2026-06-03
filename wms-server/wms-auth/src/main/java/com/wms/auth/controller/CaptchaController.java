package com.wms.auth.controller;

import com.wms.auth.domain.vo.CaptchaImageResp;
import com.wms.auth.service.CaptchaService;
import com.wms.common.domain.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "验证码管理")
@RestController
@RequestMapping("/auth/captcha")
@RequiredArgsConstructor
public class CaptchaController {

    private final CaptchaService captchaService;

    @PreAuthorize("permitAll()")
    @Operation(summary = "获取滑块拼图验证码")
    @GetMapping("/image")
    public R<CaptchaImageResp> getCaptcha(HttpServletRequest request) {
        String clientIp = resolveClientIp(request);
        String userAgent = request.getHeader("User-Agent");
        return R.ok(captchaService.generateCaptcha(clientIp, userAgent));
    }

    /**
     * 解析客户端真实 IP：优先取 X-Forwarded-For / X-Real-IP，回退到 remoteAddr
     */
    private String resolveClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isBlank() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}

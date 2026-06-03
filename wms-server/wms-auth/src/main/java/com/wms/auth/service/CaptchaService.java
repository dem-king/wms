package com.wms.auth.service;

import com.wms.auth.domain.vo.CaptchaImageResp;

/**
 * 滑块拼图验证码服务
 * 负责生成背景图+拼图块、校验用户拖动轨迹
 */
public interface CaptchaService {

    /**
     * 生成滑块拼图验证码
     * 调用 tianai SDK 生成图片，并将客户端指纹写入 Redis 供校验时绑定
     *
     * @param clientIp 客户端 IP
     * @param userAgent User-Agent 头
     * @return 验证码响应（token + 两张图 + 缺口 Y）
     */
    CaptchaImageResp generateCaptcha(String clientIp, String userAgent);

    /**
     * 校验滑块拼图
     * 1) 校验 Redis 中的客户端指纹（IP+UA 哈希绑定，防止 token 被其他机器重放）
     * 2) 调用 tianai 校验轨迹
     *
     * @param captchaToken 验证码 Token
     * @param captchaTrack 前端采集的拖动轨迹数据
     * @param clientIp 客户端 IP（用于绑定校验）
     * @param userAgent User-Agent 头（用于绑定校验）
     * @throws BizException 校验失败时抛出 CAPTCHA_INVALID（指纹不匹配/不存在/已过期）或 CAPTCHA_MISMATCH（轨迹错误）
     */
    void validateCaptcha(String captchaToken, String captchaTrack, String clientIp, String userAgent);
}

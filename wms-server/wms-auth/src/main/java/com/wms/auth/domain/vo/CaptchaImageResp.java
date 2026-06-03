package com.wms.auth.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 滑块拼图验证码响应
 * 包含背景图、拼图块、缺口 Y 坐标和验证 token
 */
@Data
@Schema(description = "滑块拼图验证码响应")
public class CaptchaImageResp {

    /** 校验 Token（提交登录时回传） */
    @Schema(description = "校验Token")
    private String captchaToken;

    /** 背景图 Base64（包含缺口阴影） */
    @Schema(description = "背景图Base64")
    private String backgroundImage;

    /** 拼图块 Base64（用户拖动的方块） */
    @Schema(description = "拼图块Base64")
    private String blockImage;

    /** 缺口在背景图中的 Y 坐标（px） */
    @Schema(description = "缺口Y坐标")
    private Integer blockY;

    /** 有效期（秒） */
    @Schema(description = "有效期（秒）")
    private Integer expiresIn;
}

package com.wms.auth.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "登录响应")
public class LoginResp {

    /** 访问Token */
    @Schema(description = "访问Token")
    private String accessToken;

    /** 刷新Token */
    @Schema(description = "刷新Token")
    private String refreshToken;

    /** Token类型 */
    @Schema(description = "Token类型")
    private String tokenType = "Bearer";

    /** 过期时间(秒) */
    @Schema(description = "过期时间(秒)")
    private Long expiresIn;

    /** 用户信息 */
    @Schema(description = "用户信息")
    private UserInfoVo userInfo;

    /** 权限编码列表 */
    @Schema(description = "权限编码列表")
    private List<String> permissions;

    /** 用户菜单树(用于动态路由和侧边栏渲染) */
    @Schema(description = "用户菜单树")
    private List<Object> menus;
}

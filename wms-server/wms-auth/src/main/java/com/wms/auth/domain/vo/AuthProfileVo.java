package com.wms.auth.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 当前登录用户个人中心视图对象
 */
@Data
@Schema(description = "当前登录用户个人中心信息")
public class AuthProfileVo {

    /** 用户信息 */
    @Schema(description = "用户信息")
    private UserInfoVo userInfo;

    /** 最近登录信息 */
    @Schema(description = "最近登录信息")
    private LastLoginInfoVo lastLoginInfo;

    /** 权限编码列表 */
    @Schema(description = "权限编码列表")
    private List<String> permissions;

    /** 角色编码列表 */
    @Schema(description = "角色编码列表")
    private List<String> roles;
}

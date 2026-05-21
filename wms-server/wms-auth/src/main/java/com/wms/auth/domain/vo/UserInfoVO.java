package com.wms.auth.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户信息")
public class UserInfoVO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "真实姓名")
    private String realName;

    @Schema(description = "头像")
    private String avatar;

    @Schema(description = "部门ID")
    private Long deptId;

    @Schema(description = "手机号")
    private String phone;

    @Schema(description = "邮箱")
    private String email;
}

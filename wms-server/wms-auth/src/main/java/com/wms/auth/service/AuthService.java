package com.wms.auth.service;

import com.wms.auth.domain.dto.LoginReq;
import com.wms.auth.domain.dto.RefreshTokenReq;
import com.wms.auth.domain.dto.UpdateProfileDto;
import com.wms.auth.domain.vo.AuthProfileVo;
import com.wms.auth.domain.vo.LoginResp;
import com.wms.auth.domain.vo.TokenResp;
import com.wms.auth.domain.vo.UploadAvatarVo;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface AuthService {

    LoginResp login(LoginReq req, String clientIp, String userAgent);

    void logout(String accessToken);

    TokenResp refreshToken(RefreshTokenReq req);

    AuthProfileVo getCurrentProfile();

    AuthProfileVo updateCurrentProfile(UpdateProfileDto dto);

    /**
     * 上传当前用户头像
     *
     * @param file 头像文件
     * @return 上传结果
     */
    UploadAvatarVo uploadCurrentUserAvatar(MultipartFile file);

    /**
     * 加载头像资源
     *
     * @param userId 用户ID
     * @param fileName 文件名
     * @return 头像资源
     */
    Resource loadAvatarResource(Long userId, String fileName);
}

package com.wms.auth.service.impl;

import com.wms.auth.domain.dto.PasswordReq;
import com.wms.auth.enums.AuthErrorCode;
import com.wms.auth.service.CryptoService;
import com.wms.auth.service.PasswordService;
import com.wms.auth.service.TokenService;
import com.wms.common.exception.BizException;
import com.wms.common.util.SecurityUtil;
import com.wms.system.domain.entity.SysUser;
import com.wms.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

/**
 * 密码服务实现类
 * 处理密码修改、密码强度校验等业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final CryptoService cryptoService;
    private final SysUserService sysUserService;
    private final TokenService tokenService;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.+[a-z])(?=.+[A-Z])(?=.+\\d).{8,20}$");

    /**
     * 修改密码
     * 校验旧密码正确性、新旧密码不同、新密码强度，更新后撤销所有令牌
     * 
     * @param userId 用户ID
     * @param req 密码修改请求
     */
    @Override
    public void changePassword(Long userId, PasswordReq req) {
        String oldPassword = cryptoService.decryptPassword(req.getEncryptedOldPassword());
        String newPassword = cryptoService.decryptPassword(req.getEncryptedNewPassword());

        // 通过getByUsername获取含密码哈希的实体（getById返回Vo不含密码）
        SysUser user = sysUserService.getByUsername(null);
        if (user == null) {
            // 使用userId查找：先获取Vo获取username，再通过getByUsername获取实体
            var userVo = sysUserService.getById(userId);
            if (userVo == null) {
                throw new BizException(AuthErrorCode.CREDENTIAL_INVALID.getCode(), AuthErrorCode.CREDENTIAL_INVALID.getMsg());
            }
            user = sysUserService.getByUsername(userVo.getUsername());
        }
        if (user == null) {
            throw new BizException(AuthErrorCode.CREDENTIAL_INVALID.getCode(), AuthErrorCode.CREDENTIAL_INVALID.getMsg());
        }
        // 校验旧密码是否正确
        if (!cryptoService.verifyPassword(oldPassword, user.getPassword())) {
            throw new BizException(AuthErrorCode.CREDENTIAL_INVALID.getCode(), AuthErrorCode.CREDENTIAL_INVALID.getMsg());
        }
        // 新密码不能与旧密码相同
        if (oldPassword.equals(newPassword)) {
            throw new BizException(AuthErrorCode.PASSWORD_SAME.getCode(), AuthErrorCode.PASSWORD_SAME.getMsg());
        }
        // 校验新密码强度
        if (!validatePasswordStrength(newPassword)) {
            throw new BizException(AuthErrorCode.PASSWORD_STRENGTH_FAIL.getCode(),
                    AuthErrorCode.PASSWORD_STRENGTH_FAIL.getMsg());
        }

        String newHash = cryptoService.hashPassword(newPassword);
        sysUserService.updatePassword(userId, newHash);

        tokenService.revokeAllTokens(userId);
        log.info("用户{}修改密码成功,所有Token已撤销", userId);
    }

    @Override
    public void changeCurrentUserPassword(PasswordReq req) {
        changePassword(SecurityUtil.getCurrentUserId(), req);
    }

    /**
     * 校验密码强度
     * 要求包含大小写字母和数字，长度8-20
     * 
     * @param password 明文密码
     * @return 是否满足强度要求
     */
    @Override
    public boolean validatePasswordStrength(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}

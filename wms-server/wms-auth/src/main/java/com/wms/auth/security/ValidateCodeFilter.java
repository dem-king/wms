package com.wms.auth.security;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.auth.config.AuthProperties;
import com.wms.auth.domain.constant.AuthConstants;
import com.wms.auth.enums.AuthErrorCode;
import com.wms.common.domain.R;
import com.wms.common.exception.BizException;
import com.wms.common.util.SpringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 验证码二次校验过滤器
 * 拦截 POST /auth/login 请求，提取 captchaVerification 请求头，
 * 调用 anji-plus SDK 的 verification() 方法进行二次校验，
 * 确保一次 check 对应一次登录，防止 captchaVerification 被重放
 * <p>
 * 注意：当前项目登录请求使用 @RequestBody LoginReq（JSON body），
 * Filter 无法直接读取 JSON body（只能读取一次），因此采用以下策略：
 * <ul>
 *   <li>前端在 JSON body 中传 code 字段（LoginReq.code）</li>
 *   <li>同时将 code 放入请求头 captchaVerification，供 Filter 读取</li>
 *   <li>Filter 从请求头获取 captchaVerification 进行二次校验</li>
 * </ul>
 * 如果请求头中未携带 captchaVerification，Filter 跳过校验，
 * 由 AuthServiceImpl.login() 中的 code 字段做兜底校验
 *
 * @author wms-team
 * @since 1.0
 */
@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@RequiredArgsConstructor
public class ValidateCodeFilter extends OncePerRequestFilter {

    private final AuthProperties authProperties;
    private final ObjectMapper objectMapper;

    /**
     * 拦截登录请求进行验证码二次校验
     * 1) 仅拦截 POST /auth/login
     * 2) captchaEnabled=false 时跳过校验
     * 3) 从请求头提取 captchaVerification，为空时跳过（由 AuthServiceImpl 兜底）
     * 4) 调用 verification() 校验，失败时返回对应错误码
     * 5) 校验通过后放行到登录业务
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1) 仅拦截 POST /auth/login
        if (!"/auth/login".equals(request.getServletPath())
                || !"POST".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2) captchaEnabled=false 时跳过校验
        if (!authProperties.isCaptchaEnabled()) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3) 从请求头获取 captchaVerification
        // 前端在 JSON body 中传 code 的同时，将 code 放入请求头 captchaVerification
        // 如果请求头中没有，跳过 Filter 校验，由 AuthServiceImpl.login() 兜底
        String code = request.getHeader("captchaVerification");

        if (!StringUtils.hasText(code)) {
            // 请求头未携带验证码串，跳过 Filter 校验，由 Service 层兜底
            log.debug("请求头未携带 captchaVerification，跳过 Filter 校验，由 AuthServiceImpl 兜底");
            filterChain.doFilter(request, response);
            return;
        }

        // 4) 调用 AJ-Captcha 的 verification 二次校验
        CaptchaService captchaService = SpringUtils.getBean(CaptchaService.class);
        CaptchaVO vo = new CaptchaVO();
        vo.setCaptchaVerification(code);
        vo.setCaptchaType(AuthConstants.CAPTCHA_TYPE_BLOCK_PUZZLE);
        ResponseModel responseModel = captchaService.verification(vo);

        if (!responseModel.isSuccess()) {
            log.warn("验证码二次校验失败: repCode={}, repMsg={}",
                    responseModel.getRepCode(), responseModel.getRepMsg());
            // 根据返回码区分过期和校验失败
            if ("6110".equals(responseModel.getRepCode())) {
                writeErrorResponse(response, AuthErrorCode.CAPTCHA_EXPIRED);
                return;
            }
            writeErrorResponse(response, AuthErrorCode.CAPTCHA_MISMATCH);
            return;
        }

        // 5) 放行 → 真正的登录业务
        filterChain.doFilter(request, response);
    }

    /**
     * 将错误信息写入响应
     * 验证码校验失败时，直接返回 JSON 错误响应，不继续走登录流程
     *
     * @param response     HTTP 响应
     * @param errorCode    错误码枚举
     * @throws IOException 写入响应异常
     */
    private void writeErrorResponse(HttpServletResponse response, AuthErrorCode errorCode) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        R<Void> result = R.fail(errorCode.getCode(), errorCode.getMsg());
        response.getWriter().write(objectMapper.writeValueAsString(result));
        response.getWriter().flush();
    }
}
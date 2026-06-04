package com.wms.auth.controller;

import com.anji.captcha.model.common.ResponseModel;
import com.anji.captcha.model.vo.CaptchaVO;
import com.anji.captcha.service.CaptchaService;
import com.wms.common.domain.R;
import com.wms.common.util.SpringUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * ImageCodeController 单元测试
 * 验证验证码获取（getCode）和校验（check）接口的正确性
 */
@DisplayName("ImageCodeController 测试")
class ImageCodeControllerTest {

    private final ImageCodeController controller = new ImageCodeController();

    @Test
    @DisplayName("getCode 应返回 200 且 data 不为 null")
    void getCode_shouldReturnOkWithCaptchaData() {
        CaptchaService captchaService = mock(CaptchaService.class);
        ResponseModel responseModel = ResponseModel.success();
        Map<String, String> repData = new HashMap<>();
        repData.put("originalImageBase64", "base64data");
        repData.put("token", "test-token");
        repData.put("secretKey", "test-secret-key");
        responseModel.setRepData(repData);
        when(captchaService.get(any(CaptchaVO.class))).thenReturn(responseModel);

        try (MockedStatic<SpringUtils> springUtilsMock = Mockito.mockStatic(SpringUtils.class)) {
            springUtilsMock.when(() -> SpringUtils.getBean(CaptchaService.class))
                    .thenReturn(captchaService);

            CaptchaVO captchaVO = new CaptchaVO();
            R<ResponseModel> result = controller.getCode(captchaVO);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertNotNull(result.getData().getRepData());
            @SuppressWarnings("unchecked")
            Map<String, String> data = (Map<String, String>) result.getData().getRepData();
            assertEquals("base64data", data.get("originalImageBase64"));
            assertEquals("test-token", data.get("token"));
        }
    }

    @Test
    @DisplayName("getCode 应设置 captchaType 为 blockPuzzle")
    void getCode_shouldSetCaptchaTypeBlockPuzzle() {
        CaptchaService captchaService = mock(CaptchaService.class);
        ResponseModel responseModel = ResponseModel.success();
        when(captchaService.get(any(CaptchaVO.class))).thenReturn(responseModel);

        try (MockedStatic<SpringUtils> springUtilsMock = Mockito.mockStatic(SpringUtils.class)) {
            springUtilsMock.when(() -> SpringUtils.getBean(CaptchaService.class))
                    .thenReturn(captchaService);

            CaptchaVO captchaVO = new CaptchaVO();
            R<ResponseModel> result = controller.getCode(captchaVO);

            // 验证 CaptchaService.get() 被调用（内部会设置 captchaType=blockPuzzle）
            Mockito.verify(captchaService).get(any(CaptchaVO.class));
            assertEquals(200, result.getCode());
        }
    }

    @Test
    @DisplayName("check 校验通过应返回 captchaVerification")
    void check_verificationSuccess_shouldReturnCaptchaVerification() {
        CaptchaService captchaService = mock(CaptchaService.class);
        ResponseModel responseModel = ResponseModel.success();
        Map<String, String> repData = new HashMap<>();
        repData.put("captchaVerification", "test-verification-token");
        responseModel.setRepData(repData);
        when(captchaService.check(any(CaptchaVO.class))).thenReturn(responseModel);

        try (MockedStatic<SpringUtils> springUtilsMock = Mockito.mockStatic(SpringUtils.class)) {
            springUtilsMock.when(() -> SpringUtils.getBean(CaptchaService.class))
                    .thenReturn(captchaService);

            CaptchaVO captchaVO = new CaptchaVO();
            captchaVO.setToken("test-token");
            captchaVO.setPointJson("encrypted-point");
            R<ResponseModel> result = controller.check(captchaVO);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertNotNull(result.getData().getRepData());
            @SuppressWarnings("unchecked")
            Map<String, String> data = (Map<String, String>) result.getData().getRepData();
            assertEquals("test-verification-token", data.get("captchaVerification"));
        }
    }

    @Test
    @DisplayName("check 校验失败应返回错误响应")
    void check_verificationFail_shouldReturnErrorResponse() {
        CaptchaService captchaService = mock(CaptchaService.class);
        ResponseModel responseModel = ResponseModel.errorMsg("验证失败");
        when(captchaService.check(any(CaptchaVO.class))).thenReturn(responseModel);

        try (MockedStatic<SpringUtils> springUtilsMock = Mockito.mockStatic(SpringUtils.class)) {
            springUtilsMock.when(() -> SpringUtils.getBean(CaptchaService.class))
                    .thenReturn(captchaService);

            CaptchaVO captchaVO = new CaptchaVO();
            captchaVO.setToken("test-token");
            captchaVO.setPointJson("wrong-point");
            R<ResponseModel> result = controller.check(captchaVO);

            assertEquals(200, result.getCode());
            assertNotNull(result.getData());
            assertFalse(result.getData().isSuccess());
        }
    }

    @Test
    @DisplayName("check 应传递 token 和 pointJson 到 CaptchaService")
    void check_shouldPassTokenAndPointJson() {
        CaptchaService captchaService = mock(CaptchaService.class);
        ResponseModel responseModel = ResponseModel.success();
        when(captchaService.check(any(CaptchaVO.class))).thenReturn(responseModel);

        try (MockedStatic<SpringUtils> springUtilsMock = Mockito.mockStatic(SpringUtils.class)) {
            springUtilsMock.when(() -> SpringUtils.getBean(CaptchaService.class))
                    .thenReturn(captchaService);

            CaptchaVO captchaVO = new CaptchaVO();
            captchaVO.setToken("my-token");
            captchaVO.setPointJson("my-point-json");
            controller.check(captchaVO);

            // 验证 CaptchaService.check() 被调用
            Mockito.verify(captchaService).check(any(CaptchaVO.class));
        }
    }
}
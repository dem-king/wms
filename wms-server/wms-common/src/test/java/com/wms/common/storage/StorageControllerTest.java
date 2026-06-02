package com.wms.common.storage;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * StorageController 测试
 * 验证图片资源读取接口的访问控制契约。
 */
class StorageControllerTest {

    /**
     * 文件读取接口应允许图片标签直接访问。
     *
     * @throws NoSuchMethodException 方法不存在时抛出
     */
    @Test
    void shouldAllowPublicAccessForStoredFileContent() throws NoSuchMethodException {
        Method method = StorageController.class.getMethod(
                "serveFile",
                String.class,
                HttpServletRequest.class
        );

        PreAuthorize preAuthorize = method.getAnnotation(PreAuthorize.class);

        assertThat(preAuthorize).isNotNull();
        assertThat(preAuthorize.value()).isEqualTo("permitAll()");
    }

    /**
     * 图片读取接口应返回浏览器可识别的图片类型。
     */
    @Test
    void shouldReturnImageMediaTypeForStoredImageContent() {
        StorageController controller = new StorageController(new InMemoryStorageStrategy(), new StorageProperties());
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/storage/items/demo.jpeg");
        request.setContextPath("/api");

        var response = controller.serveFile(StorageConstants.BUCKET_ITEMS, request);

        assertThat(response.getHeaders().getContentType()).isEqualTo(MediaType.IMAGE_JPEG);
    }

    private static class InMemoryStorageStrategy implements StorageStrategy {

        @Override
        public String upload(String bucket, String objectName, InputStream inputStream, String contentType, long fileSize) {
            return "/api/storage/" + bucket + "/" + objectName;
        }

        @Override
        public InputStream download(String bucket, String objectName) {
            return new ByteArrayInputStream(new byte[]{1, 2, 3});
        }

        @Override
        public void delete(String bucket, String objectName) {
        }

        @Override
        public String getUrl(String bucket, String objectName) {
            return "/api/storage/" + bucket + "/" + objectName;
        }

        @Override
        public PresignedUploadResult getPresignedUploadUrl(String bucket, String objectName, String contentType, long expireSeconds) {
            return null;
        }
    }
}

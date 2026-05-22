package com.wms.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Jackson 配置测试
 * 验证 Long 类型在响应中以字符串形式输出，避免前端丢失雪花 ID 精度。
 */
class JacksonConfigTest {

    /**
     * 验证 Long 类型序列化为字符串。
     */
    @Test
    void shouldSerializeLongAsString() throws Exception {
        ObjectMapper objectMapper = new JacksonConfig().objectMapper();

        String json = objectMapper.writeValueAsString(new IdPayload(2056308883391909890L));

        assertThat(json).contains("\"id\":\"2056308883391909890\"");
    }

    /**
     * ID 测试载体
     */
    private record IdPayload(Long id) {
    }
}

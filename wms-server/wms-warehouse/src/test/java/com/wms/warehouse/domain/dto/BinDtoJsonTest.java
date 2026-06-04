package com.wms.warehouse.domain.dto;

import com.wms.common.config.JacksonConfig;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 库位 DTO JSON 绑定测试
 * 验证前端提交的字段名可以正确绑定到后端 DTO。
 */
class BinDtoJsonTest {

    /**
     * 验证 row、col、status 字段可以兼容绑定到后端属性。
     */
    @Test
    void shouldDeserializeFrontendFieldNames() throws Exception {
        String json = """
                {
                  "cabinetId": 10,
                  "binCode": "BIN-01",
                  "row": 2,
                  "col": 3,
                  "status": 1
                }
                """;

        BinDto dto = new JacksonConfig().objectMapper().readValue(json, BinDto.class);

        assertThat(dto.getCabinetId()).isEqualTo(10L);
        assertThat(dto.getBinCode()).isEqualTo("BIN-01");
        assertThat(dto.getRowNum()).isEqualTo(2);
        assertThat(dto.getColNum()).isEqualTo(3);
        assertThat(dto.getBinStatus()).isEqualTo(1);
    }
}

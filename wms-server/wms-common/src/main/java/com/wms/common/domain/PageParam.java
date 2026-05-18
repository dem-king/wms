package com.wms.common.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Data
@Schema(description = "分页查询参数")
public class PageParam {

    @Schema(description = "当前页码")
    @Min(1)
    private Integer page = 1;

    @Schema(description = "每页数量")
    @Min(1)
    @Max(100)
    private Integer size = 20;

    @Schema(description = "排序字段")
    private String sort;

    @Schema(description = "排序方向(asc/desc)")
    private String order = "desc";
}

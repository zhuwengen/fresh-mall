package com.freshmall.product.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Map;

/**
 * SKU 更新请求
 */
@Schema(description = "SKU 更新请求")
@Data
public class SkuUpdateRequest {

    @Schema(description = "规格信息（JSON 格式）", example = "{\"颜色\": \"白色\", \"容量\": \"512GB\"}", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "规格不能为空")
    private Map<String, Object> specifications;
}

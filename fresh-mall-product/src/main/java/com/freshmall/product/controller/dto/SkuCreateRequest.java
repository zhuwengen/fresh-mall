package com.freshmall.product.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.Map;

/**
 * SKU 创建请求
 */
@Schema(description = "SKU 创建请求")
@Data
public class SkuCreateRequest {

    @Schema(description = "SPU ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "SPU ID 不能为空")
    private Long spuId;

    @Schema(description = "SKU 编码", example = "SKU-IPHONE15-256GB-BLACK", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "SKU 编码不能为空")
    private String skuCode;

    @Schema(description = "规格信息（JSON 格式）", example = "{\"颜色\": \"黑色\", \"容量\": \"256GB\"}", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "规格不能为空")
    private Map<String, Object> specifications;
}

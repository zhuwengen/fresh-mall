package com.freshmall.product.controller.dto;

import com.freshmall.product.domain.model.AttributeType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 属性创建请求
 */
@Schema(description = "属性创建请求")
@Data
public class AttributeCreateRequest {

    @Schema(description = "属性名称", example = "颜色", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "属性名称不能为空")
    private String name;

    @Schema(description = "属性类型", example = "ENUM", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "属性类型不能为空")
    private AttributeType attributeType;

    @Schema(description = "值范围（JSON 格式，ENUM 类型为数组，NUMBER 类型为 {\"min\": 0, \"max\": 100}）", example = "[\"黑色\", \"白色\", \"金色\"]")
    private String valueRange;
}

package com.freshmall.product.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 类目-属性关联请求
 */
@Schema(description = "类目-属性关联请求")
@Data
public class CategoryAttributeRequest {

    @Schema(description = "属性 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "属性 ID 不能为空")
    private Long attributeId;

    @Schema(description = "是否必需", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "是否必需不能为空")
    private Boolean required;

    @Schema(description = "排序值", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序不能为空")
    @Min(value = 0, message = "排序不能为负数")
    private Integer sortOrder;
}

package com.freshmall.product.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 类目创建请求
 */
@Schema(description = "类目创建请求")
@Data
public class CategoryCreateRequest {

    @Schema(description = "类目编码", example = "ELECTRONICS", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "类目编码不能为空")
    private String categoryCode;

    @Schema(description = "类目名称", example = "电子产品", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "类目名称不能为空")
    private String name;

    @Schema(description = "父类目 ID（可选，根类目不填）", example = "1")
    private Long parentId;

    @Schema(description = "排序值", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "排序不能为空")
    @Min(value = 0, message = "排序不能为负数")
    private Integer sortOrder;
}

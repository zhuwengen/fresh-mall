package com.freshmall.product.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * SPU 更新请求
 */
@Schema(description = "SPU 更新请求")
@Data
public class SpuUpdateRequest {

    @Schema(description = "SPU 名称", example = "iPhone 15 Pro", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "SPU 名称不能为空")
    @Size(max = 200, message = "SPU 名称不能超过 200 个字符")
    private String name;

    @Schema(description = "类目 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "类目 ID 不能为空")
    private Long categoryId;

    @Schema(description = "商品图片 URL（多个用逗号分隔）", example = "https://example.com/img1.jpg,https://example.com/img2.jpg", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "商品图片不能为空")
    private String images;
}

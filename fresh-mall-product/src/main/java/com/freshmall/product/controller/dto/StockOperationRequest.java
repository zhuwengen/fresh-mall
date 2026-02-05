package com.freshmall.product.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 库存操作请求（扣减、确认、释放）
 */
@Schema(description = "库存操作请求（扣减、确认、释放）")
@Data
public class StockOperationRequest {

    @Schema(description = "SKU ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "SKU ID 不能为空")
    private Long skuId;

    @Schema(description = "操作数量", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "数量不能为空")
    @Min(value = 1, message = "数量必须大于 0")
    private Integer quantity;
}

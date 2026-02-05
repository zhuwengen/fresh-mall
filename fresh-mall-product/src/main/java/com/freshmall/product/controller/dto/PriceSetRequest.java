package com.freshmall.product.controller.dto;

import com.freshmall.product.domain.model.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 价格设置请求
 */
@Schema(description = "价格设置请求")
@Data
public class PriceSetRequest {

    @Schema(description = "SKU ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "SKU ID 不能为空")
    private Long skuId;

    @Schema(description = "销售渠道", example = "APP", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "销售渠道不能为空")
    private ChannelType channel;

    @Schema(description = "价格（元）", example = "6999.00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于 0")
    private BigDecimal price;

    @Schema(description = "生效时间", example = "2024-01-01T00:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "生效时间不能为空")
    private LocalDateTime startTime;

    @Schema(description = "失效时间（可选）", example = "2024-12-31T23:59:59")
    private LocalDateTime endTime;
}

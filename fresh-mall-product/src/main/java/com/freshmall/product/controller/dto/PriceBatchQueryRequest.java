package com.freshmall.product.controller.dto;

import com.freshmall.product.domain.model.ChannelType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 批量查询价格请求
 */
@Schema(description = "批量查询价格请求")
@Data
public class PriceBatchQueryRequest {

    @Schema(description = "SKU ID 列表", example = "[1, 2, 3]", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "SKU ID 列表不能为空")
    private List<Long> skuIds;

    @Schema(description = "销售渠道", example = "APP", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "销售渠道不能为空")
    private ChannelType channel;
}

package com.freshmall.product.controller.admin;

import com.freshmall.common.result.Result;
import com.freshmall.product.controller.dto.PriceBatchQueryRequest;
import com.freshmall.product.controller.dto.PriceSetRequest;
import com.freshmall.product.domain.model.ChannelType;
import com.freshmall.product.domain.service.PriceService;
import com.freshmall.product.domain.valueobject.Money;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 价格管理控制器
 */
@Tag(name = "价格管理", description = "SKU 多渠道价格管理接口，包括设置价格和查询价格等操作")
@RestController
@RequestMapping("/api/price")
@RequiredArgsConstructor
public class PriceController {

    private final PriceService priceService;

    @Operation(summary = "设置价格", description = "为指定 SKU 和渠道设置价格策略，支持时间范围")
    @PostMapping
    public Result<Long> setPrice(@Valid @RequestBody PriceSetRequest request) {
        Long priceId = priceService.setSkuPrice(
            request.getSkuId(),
            request.getChannel(),
            request.getPrice(),
            request.getStartTime(),
            request.getEndTime()
        );
        return Result.success(priceId);
    }

    @Operation(summary = "查询当前价格", description = "查询指定 SKU 在指定渠道的当前有效价格")
    @GetMapping("/{skuId}")
    public Result<Money> getCurrentPrice(
            @Parameter(description = "SKU ID", required = true) @PathVariable Long skuId,
            @Parameter(description = "销售渠道", required = true) @RequestParam ChannelType channel) {
        Money price = priceService.getCurrentPrice(skuId, channel);
        return Result.success(price);
    }

    @Operation(summary = "批量查询价格", description = "批量查询多个 SKU 在指定渠道的当前有效价格")
    @PostMapping("/batch")
    public Result<Map<Long, Money>> batchGetPrices(@Valid @RequestBody PriceBatchQueryRequest request) {
        Map<Long, Money> prices = priceService.batchGetPrices(
            request.getSkuIds(),
            request.getChannel()
        );
        return Result.success(prices);
    }
}

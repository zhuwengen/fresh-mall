package com.freshmall.product.controller.admin;

import com.freshmall.common.result.Result;
import com.freshmall.product.controller.dto.StockInitRequest;
import com.freshmall.product.controller.dto.StockOperationRequest;
import com.freshmall.product.domain.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 库存管理控制器
 */
@Tag(name = "库存管理", description = "SKU 库存管理接口，包括初始化、扣减、确认和释放等操作")
@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @Operation(summary = "初始化库存", description = "为指定 SKU 初始化库存数量")
    @PostMapping("/init")
    public Result<Void> initStock(@Valid @RequestBody StockInitRequest request) {
        stockService.initStock(request.getSkuId(), request.getTotalStock());
        return Result.success();
    }

    @Operation(summary = "扣减库存", description = "扣减指定 SKU 的可用库存，增加锁定库存（用于订单预占）")
    @PostMapping("/deduct")
    public Result<Void> deductStock(@Valid @RequestBody StockOperationRequest request) {
        stockService.deductStock(request.getSkuId(), request.getQuantity());
        return Result.success();
    }

    @Operation(summary = "确认扣减", description = "确认库存扣减，减少总库存和锁定库存（用于订单支付成功）")
    @PostMapping("/confirm")
    public Result<Void> confirmDeduction(@Valid @RequestBody StockOperationRequest request) {
        stockService.confirmDeduction(request.getSkuId(), request.getQuantity());
        return Result.success();
    }

    @Operation(summary = "释放库存", description = "释放锁定的库存，恢复为可用库存（用于订单取消）")
    @PostMapping("/release")
    public Result<Void> releaseStock(@Valid @RequestBody StockOperationRequest request) {
        stockService.releaseStock(request.getSkuId(), request.getQuantity());
        return Result.success();
    }

    @Operation(summary = "查询可用库存", description = "查询指定 SKU 的当前可用库存数量")
    @GetMapping("/{skuId}")
    public Result<Integer> getAvailableStock(
            @Parameter(description = "SKU ID", required = true) @PathVariable Long skuId) {
        Integer availableStock = stockService.getAvailableStock(skuId);
        return Result.success(availableStock);
    }
}

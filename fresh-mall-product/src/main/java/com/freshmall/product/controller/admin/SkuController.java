package com.freshmall.product.controller.admin;

import com.freshmall.common.result.Result;
import com.freshmall.product.controller.dto.SkuCreateRequest;
import com.freshmall.product.controller.dto.SkuUpdateRequest;
import com.freshmall.product.domain.model.Sku;
import com.freshmall.product.domain.service.SkuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * SKU 管理控制器
 */
@Tag(name = "SKU 管理", description = "库存量单位（SKU）管理接口，包括创建、更新、启用和禁用等操作")
@RestController
@RequestMapping("/api/sku")
@RequiredArgsConstructor
public class SkuController {

    private final SkuService skuService;

    @Operation(summary = "创建 SKU", description = "为指定 SPU 创建新的 SKU，包含规格信息")
    @PostMapping
    public Result<Long> createSku(@Valid @RequestBody SkuCreateRequest request) {
        Long skuId = skuService.createSku(
            request.getSpuId(),
            request.getSkuCode(),
            request.getSpecifications()
        );
        return Result.success(skuId);
    }

    @Operation(summary = "更新 SKU 规格", description = "更新 SKU 的规格信息")
    @PutMapping("/{id}")
    public Result<Void> updateSkuSpecifications(
            @Parameter(description = "SKU ID", required = true) @PathVariable Long id,
            @Valid @RequestBody SkuUpdateRequest request) {
        skuService.updateSkuSpecifications(id, request.getSpecifications());
        return Result.success();
    }

    @Operation(summary = "查询 SPU 的 SKU 列表", description = "查询指定 SPU 下的所有 SKU")
    @GetMapping("/spu/{spuId}")
    public Result<List<Sku>> listSkusBySpu(
            @Parameter(description = "SPU ID", required = true) @PathVariable Long spuId) {
        List<Sku> skus = skuService.listSkusBySpu(spuId);
        return Result.success(skus);
    }

    @Operation(summary = "启用 SKU", description = "启用指定的 SKU，使其可以销售")
    @PostMapping("/{id}/enable")
    public Result<Void> enableSku(
            @Parameter(description = "SKU ID", required = true) @PathVariable Long id) {
        skuService.enableSku(id);
        return Result.success();
    }

    @Operation(summary = "禁用 SKU", description = "禁用指定的 SKU，使其不可销售")
    @PostMapping("/{id}/disable")
    public Result<Void> disableSku(
            @Parameter(description = "SKU ID", required = true) @PathVariable Long id) {
        skuService.disableSku(id);
        return Result.success();
    }
}

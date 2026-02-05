package com.freshmall.product.controller.admin;

import com.freshmall.common.result.Result;
import com.freshmall.product.controller.dto.SpuCreateRequest;
import com.freshmall.product.controller.dto.SpuUpdateRequest;
import com.freshmall.product.domain.aggregate.Spu;
import com.freshmall.product.domain.service.SpuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * SPU 管理控制器
 */
@Tag(name = "SPU 管理", description = "商品标准单元（SPU）管理接口，包括创建、更新、发布、下架和删除等操作")
@RestController
@RequestMapping("/api/spu")
@RequiredArgsConstructor
public class SpuController {

    private final SpuService spuService;

    @Operation(summary = "创建 SPU", description = "创建新的商品标准单元，初始状态为草稿（DRAFT）")
    @PostMapping
    public Result<Long> createSpu(@Valid @RequestBody SpuCreateRequest request) {
        Long spuId = spuService.createSpu(
            request.getName(),
            request.getCategoryId(),
            request.getImages()
        );
        return Result.success(spuId);
    }

    @Operation(summary = "更新 SPU", description = "更新 SPU 的基本信息（名称、类目、图片）")
    @PutMapping("/{id}")
    public Result<Void> updateSpu(
            @Parameter(description = "SPU ID", required = true) @PathVariable Long id,
            @Valid @RequestBody SpuUpdateRequest request) {
        spuService.updateSpu(
            id,
            request.getName(),
            request.getCategoryId(),
            request.getImages()
        );
        return Result.success();
    }

    @Operation(summary = "查询 SPU 详情", description = "根据 ID 查询 SPU 的完整信息")
    @GetMapping("/{id}")
    public Result<Spu> getSpuDetail(
            @Parameter(description = "SPU ID", required = true) @PathVariable Long id) {
        Spu spu = spuService.getSpuDetail(id);
        return Result.success(spu);
    }

    @Operation(summary = "发布 SPU", description = "将 SPU 状态从草稿（DRAFT）变更为已发布（PUBLISHED），需要至少一个启用的 SKU")
    @PostMapping("/{id}/publish")
    public Result<Void> publishSpu(
            @Parameter(description = "SPU ID", required = true) @PathVariable Long id) {
        spuService.publishSpu(id);
        return Result.success();
    }

    @Operation(summary = "下架 SPU", description = "将 SPU 状态从已发布（PUBLISHED）变更为已下架（UNPUBLISHED）")
    @PostMapping("/{id}/unpublish")
    public Result<Void> unpublishSpu(
            @Parameter(description = "SPU ID", required = true) @PathVariable Long id) {
        spuService.unpublishSpu(id);
        return Result.success();
    }

    @Operation(summary = "逻辑删除 SPU", description = "逻辑删除 SPU 及其关联的所有 SKU，不进行物理删除")
    @DeleteMapping("/{id}")
    public Result<Void> deleteSpu(
            @Parameter(description = "SPU ID", required = true) @PathVariable Long id) {
        spuService.deleteSpu(id);
        return Result.success();
    }
}

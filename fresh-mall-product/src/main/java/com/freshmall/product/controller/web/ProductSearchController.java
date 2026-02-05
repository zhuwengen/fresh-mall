package com.freshmall.product.controller.web;

import com.freshmall.common.result.Result;
import com.freshmall.product.controller.dto.ProductSearchResponse;
import com.freshmall.product.domain.model.ProductIndex;
import com.freshmall.product.domain.model.StockStatus;
import com.freshmall.product.domain.service.ProductSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品搜索控制器
 */
@Tag(name = "商品搜索", description = "商品搜索接口，支持多条件过滤和分页")
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductSearchController {

    private final ProductSearchService productSearchService;

    @Operation(summary = "搜索商品", description = "根据类目、价格范围、库存状态等条件搜索商品，支持分页")
    @GetMapping("/search")
    public Result<ProductSearchResponse> searchProducts(
            @Parameter(description = "类目 ID（可选）") @RequestParam(required = false) Long categoryId,
            @Parameter(description = "最低价格（可选）") @RequestParam(required = false) BigDecimal minPrice,
            @Parameter(description = "最高价格（可选）") @RequestParam(required = false) BigDecimal maxPrice,
            @Parameter(description = "库存状态（可选）") @RequestParam(required = false) StockStatus stockStatus,
            @Parameter(description = "页码", example = "1") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小", example = "20") @RequestParam(defaultValue = "20") Integer pageSize) {

        // 计算偏移量
        int offset = (page - 1) * pageSize;

        // 搜索商品
        List<ProductIndex> products = productSearchService.searchProducts(
            categoryId,
            minPrice,
            maxPrice,
            stockStatus,
            offset,
            pageSize
        );

        // 统计总数
        Long total = productSearchService.countSearchResults(
            categoryId,
            minPrice,
            maxPrice,
            stockStatus
        );

        // 构建响应
        ProductSearchResponse response = new ProductSearchResponse(
            products,
            total,
            page,
            pageSize
        );

        return Result.success(response);
    }
}

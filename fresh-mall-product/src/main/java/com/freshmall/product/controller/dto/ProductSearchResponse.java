package com.freshmall.product.controller.dto;

import com.freshmall.product.domain.model.ProductIndex;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 商品搜索响应
 */
@Schema(description = "商品搜索响应")
@Data
@AllArgsConstructor
public class ProductSearchResponse {

    @Schema(description = "商品列表")
    private List<ProductIndex> products;

    @Schema(description = "总数", example = "100")
    private Long total;

    @Schema(description = "当前页码", example = "1")
    private Integer page;

    @Schema(description = "每页大小", example = "20")
    private Integer pageSize;
}

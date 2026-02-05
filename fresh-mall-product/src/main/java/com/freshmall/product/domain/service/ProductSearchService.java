package com.freshmall.product.domain.service;

import com.freshmall.product.domain.model.ProductIndex;
import com.freshmall.product.domain.model.StockStatus;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品搜索领域服务接口
 * 管理商品索引的更新和搜索功能
 */
public interface ProductSearchService {

    /**
     * 搜索商品（支持多条件过滤和分页）
     * 
     * @param categoryId 类目 ID（可选，包含子类目）
     * @param minPrice 最低价格（可选）
     * @param maxPrice 最高价格（可选）
     * @param stockStatus 库存状态（可选）
     * @param offset 分页偏移量
     * @param limit 分页大小
     * @return 商品索引列表
     */
    List<ProductIndex> searchProducts(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice,
                                     StockStatus stockStatus, Integer offset, Integer limit);

    /**
     * 统计搜索结果总数
     * 
     * @param categoryId 类目 ID（可选，包含子类目）
     * @param minPrice 最低价格（可选）
     * @param maxPrice 最高价格（可选）
     * @param stockStatus 库存状态（可选）
     * @return 总数
     */
    Long countSearchResults(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, StockStatus stockStatus);

    /**
     * 更新索引（SPU 发布时同步调用）
     * 为 SPU 下的所有启用 SKU 创建或更新索引记录
     * 
     * @param spuId SPU ID
     */
    void updateIndex(Long spuId);

    /**
     * 异步更新索引（SKU 数据变更时）
     * 更新单个 SKU 的索引记录
     * 
     * @param skuId SKU ID
     */
    void asyncUpdateIndex(Long skuId);

    /**
     * 删除索引（SPU 删除或下架时）
     * 
     * @param spuId SPU ID
     */
    void deleteIndex(Long spuId);
}

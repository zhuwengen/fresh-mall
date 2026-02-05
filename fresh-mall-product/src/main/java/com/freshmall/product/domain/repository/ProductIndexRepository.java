package com.freshmall.product.domain.repository;

import com.freshmall.product.domain.model.ProductIndex;
import com.freshmall.product.domain.model.StockStatus;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 商品索引仓储接口
 * 定义商品索引的持久化和查询操作
 */
public interface ProductIndexRepository {

    /**
     * 保存商品索引
     * 
     * @param productIndex 商品索引实体
     * @return 保存后的商品索引（包含生成的 ID）
     */
    ProductIndex save(ProductIndex productIndex);

    /**
     * 更新商品索引
     * 
     * @param productIndex 商品索引实体
     */
    void update(ProductIndex productIndex);

    /**
     * 根据 SKU ID 查询商品索引
     * 
     * @param skuId SKU ID
     * @return 商品索引实体（如果存在）
     */
    Optional<ProductIndex> findBySkuId(Long skuId);

    /**
     * 根据 SPU ID 查询所有商品索引
     * 
     * @param spuId SPU ID
     * @return 商品索引列表
     */
    List<ProductIndex> findBySpuId(Long spuId);

    /**
     * 根据 SPU ID 删除所有商品索引
     * 
     * @param spuId SPU ID
     */
    void deleteBySpuId(Long spuId);

    /**
     * 根据 SKU ID 删除商品索引
     * 
     * @param skuId SKU ID
     */
    void deleteBySkuId(Long skuId);

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
    List<ProductIndex> search(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, 
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
    Long countSearch(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, StockStatus stockStatus);

    /**
     * 批量保存或更新商品索引
     * 
     * @param productIndexes 商品索引列表
     */
    void batchSaveOrUpdate(List<ProductIndex> productIndexes);
}

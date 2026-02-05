package com.freshmall.product.domain.service;

/**
 * 库存领域服务接口
 * 管理库存数量、处理高并发扣减
 */
public interface StockService {

    /**
     * 初始化库存
     * 
     * @param skuId SKU ID
     * @param totalStock 总库存
     */
    void initStock(Long skuId, Integer totalStock);

    /**
     * 扣减库存（锁定）
     * 使用乐观锁，失败时自动重试
     * 
     * @param skuId SKU ID
     * @param quantity 扣减数量
     */
    void deductStock(Long skuId, Integer quantity);

    /**
     * 确认扣减（真正减少总库存）
     * 
     * @param skuId SKU ID
     * @param quantity 确认数量
     */
    void confirmDeduction(Long skuId, Integer quantity);

    /**
     * 释放锁定库存
     * 
     * @param skuId SKU ID
     * @param quantity 释放数量
     */
    void releaseStock(Long skuId, Integer quantity);

    /**
     * 查询可用库存
     * 
     * @param skuId SKU ID
     * @return 可用库存数量
     */
    Integer getAvailableStock(Long skuId);
}

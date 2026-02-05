package com.freshmall.product.domain.repository;

import com.freshmall.product.domain.stock.SkuStock;

import java.util.Optional;

/**
 * SKU 库存仓储接口
 * 定义库存聚合根的持久化操作
 */
public interface SkuStockRepository {

    /**
     * 保存库存记录
     * 
     * @param stock 库存实体
     * @return 保存后的库存（包含生成的 ID）
     */
    SkuStock save(SkuStock stock);

    /**
     * 更新库存记录（使用乐观锁）
     * 
     * @param stock 库存实体
     * @return 更新是否成功（版本号匹配）
     */
    boolean update(SkuStock stock);

    /**
     * 根据 SKU ID 查询库存
     * 
     * @param skuId SKU ID
     * @return 库存实体（如果存在）
     */
    Optional<SkuStock> findBySkuId(Long skuId);

    /**
     * 根据 ID 查询库存
     * 
     * @param id 库存 ID
     * @return 库存实体（如果存在）
     */
    Optional<SkuStock> findById(Long id);

    /**
     * 检查 SKU 是否有库存记录
     * 
     * @param skuId SKU ID
     * @return 是否存在
     */
    boolean existsBySkuId(Long skuId);

    /**
     * 删除库存记录
     * 
     * @param id 库存 ID
     */
    void deleteById(Long id);

    /**
     * 根据 SKU ID 删除库存记录
     * 
     * @param skuId SKU ID
     */
    void deleteBySkuId(Long skuId);
}

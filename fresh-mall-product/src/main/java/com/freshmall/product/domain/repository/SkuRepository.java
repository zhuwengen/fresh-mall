package com.freshmall.product.domain.repository;

import com.freshmall.product.domain.model.Sku;

import java.util.List;
import java.util.Optional;

/**
 * SKU 仓储接口
 * 定义 SKU 聚合根的持久化操作
 */
public interface SkuRepository {

    /**
     * 保存 SKU
     * 
     * @param sku SKU 实体
     * @return 保存后的 SKU（包含生成的 ID）
     */
    Sku save(Sku sku);

    /**
     * 更新 SKU
     * 
     * @param sku SKU 实体
     */
    void update(Sku sku);

    /**
     * 根据 ID 查询 SKU（排除已删除）
     * 
     * @param id SKU ID
     * @return SKU 实体（如果存在且未删除）
     */
    Optional<Sku> findById(Long id);

    /**
     * 根据 ID 查询 SKU（包含已删除）
     * 
     * @param id SKU ID
     * @return SKU 实体（如果存在）
     */
    Optional<Sku> findByIdIncludingDeleted(Long id);

    /**
     * 根据 SPU ID 查询所有 SKU（排除已删除）
     * 
     * @param spuId SPU ID
     * @return SKU 列表
     */
    List<Sku> findBySpuId(Long spuId);

    /**
     * 根据 SPU ID 查询启用的 SKU（排除已删除）
     * 
     * @param spuId SPU ID
     * @return 启用的 SKU 列表
     */
    List<Sku> findEnabledBySpuId(Long spuId);

    /**
     * 根据 SKU 编码查询 SKU（排除已删除）
     * 
     * @param skuCode SKU 编码
     * @return SKU 实体（如果存在且未删除）
     */
    Optional<Sku> findBySkuCode(String skuCode);

    /**
     * 检查 SKU 编码是否存在（排除已删除）
     * 
     * @param skuCode SKU 编码
     * @return 是否存在
     */
    boolean existsBySkuCode(String skuCode);

    /**
     * 检查 SKU 编码是否存在（排除指定 ID 和已删除）
     * 
     * @param skuCode SKU 编码
     * @param excludeId 排除的 SKU ID
     * @return 是否存在
     */
    boolean existsBySkuCodeExcludingId(String skuCode, Long excludeId);

    /**
     * 检查 SPU 是否有启用的 SKU（排除已删除）
     * 
     * @param spuId SPU ID
     * @return 是否有启用的 SKU
     */
    boolean hasEnabledSku(Long spuId);

    /**
     * 逻辑删除 SKU
     * 
     * @param id SKU ID
     */
    void deleteById(Long id);

    /**
     * 根据 SPU ID 逻辑删除所有 SKU
     * 
     * @param spuId SPU ID
     */
    void deleteBySpuId(Long spuId);
}

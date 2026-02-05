package com.freshmall.product.domain.service;

import com.freshmall.product.domain.model.Sku;

import java.util.List;
import java.util.Map;

/**
 * SKU 领域服务接口
 * 管理 SKU 的创建、更新、查询和启用/禁用
 */
public interface SkuService {

    /**
     * 创建 SKU
     * 
     * @param spuId SPU ID
     * @param skuCode SKU 编码
     * @param specifications 规格 Map
     * @return 创建的 SKU ID
     */
    Long createSku(Long spuId, String skuCode, Map<String, Object> specifications);

    /**
     * 更新 SKU 规格
     * 
     * @param skuId SKU ID
     * @param specifications 规格 Map
     */
    void updateSkuSpecifications(Long skuId, Map<String, Object> specifications);

    /**
     * 启用 SKU
     * 
     * @param skuId SKU ID
     */
    void enableSku(Long skuId);

    /**
     * 禁用 SKU
     * 
     * @param skuId SKU ID
     */
    void disableSku(Long skuId);

    /**
     * 查询 SKU 详情
     * 
     * @param skuId SKU ID
     * @return SKU 实体
     */
    Sku getSkuDetail(Long skuId);

    /**
     * 查询 SPU 的所有 SKU
     * 
     * @param spuId SPU ID
     * @return SKU 列表
     */
    List<Sku> listSkusBySpu(Long spuId);

    /**
     * 查询 SPU 的启用 SKU
     * 
     * @param spuId SPU ID
     * @return 启用的 SKU 列表
     */
    List<Sku> listEnabledSkusBySpu(Long spuId);

    /**
     * 验证规格合法性
     * 
     * @param categoryId 类目 ID
     * @param specifications 规格 Map
     */
    void validateSpecifications(Long categoryId, Map<String, Object> specifications);
}

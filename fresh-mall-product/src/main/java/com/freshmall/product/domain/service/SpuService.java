package com.freshmall.product.domain.service;

import com.freshmall.product.domain.aggregate.Spu;

/**
 * SPU 领域服务接口
 * 管理 SPU 的创建、更新、查询和生命周期
 */
public interface SpuService {

    /**
     * 创建 SPU（草稿状态）
     * 
     * @param name SPU 名称
     * @param categoryId 类目 ID
     * @param images 商品图片
     * @return 创建的 SPU ID
     */
    Long createSpu(String name, Long categoryId, String images);

    /**
     * 更新 SPU 基本信息
     * 
     * @param spuId SPU ID
     * @param name SPU 名称
     * @param categoryId 类目 ID
     * @param images 商品图片
     */
    void updateSpu(Long spuId, String name, Long categoryId, String images);

    /**
     * 发布 SPU（DRAFT -> PUBLISHED）
     * 
     * @param spuId SPU ID
     */
    void publishSpu(Long spuId);

    /**
     * 下架 SPU（PUBLISHED -> UNPUBLISHED）
     * 
     * @param spuId SPU ID
     */
    void unpublishSpu(Long spuId);

    /**
     * 重新发布 SPU（UNPUBLISHED -> PUBLISHED）
     * 
     * @param spuId SPU ID
     */
    void republishSpu(Long spuId);

    /**
     * 逻辑删除 SPU（级联删除所有关联的 SKU）
     * 
     * @param spuId SPU ID
     */
    void deleteSpu(Long spuId);

    /**
     * 查询 SPU 详情
     * 
     * @param spuId SPU ID
     * @return SPU 实体
     */
    Spu getSpuDetail(Long spuId);

    /**
     * 验证类目引用是否有效（必须是叶子类目）
     * 
     * @param categoryId 类目 ID
     */
    void validateCategoryReference(Long categoryId);
}

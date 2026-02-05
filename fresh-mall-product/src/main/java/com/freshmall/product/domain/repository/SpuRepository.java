package com.freshmall.product.domain.repository;

import com.freshmall.product.domain.aggregate.Spu;

import java.util.Optional;

/**
 * SPU 仓储接口
 * 定义 SPU 聚合根的持久化操作
 */
public interface SpuRepository {

    /**
     * 保存 SPU
     * 
     * @param spu SPU 实体
     * @return 保存后的 SPU（包含生成的 ID）
     */
    Spu save(Spu spu);

    /**
     * 更新 SPU
     * 
     * @param spu SPU 实体
     */
    void update(Spu spu);

    /**
     * 根据 ID 查询 SPU（排除已删除）
     * 
     * @param id SPU ID
     * @return SPU 实体（如果存在且未删除）
     */
    Optional<Spu> findById(Long id);

    /**
     * 根据 ID 查询 SPU（包含已删除）
     * 
     * @param id SPU ID
     * @return SPU 实体（如果存在）
     */
    Optional<Spu> findByIdIncludingDeleted(Long id);

    /**
     * 检查 SPU 是否存在（排除已删除）
     * 
     * @param id SPU ID
     * @return 是否存在
     */
    boolean existsById(Long id);

    /**
     * 逻辑删除 SPU
     * 
     * @param id SPU ID
     */
    void deleteById(Long id);
}

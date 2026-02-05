package com.freshmall.product.domain.repository;

import com.freshmall.product.domain.aggregate.Attribute;

import java.util.List;
import java.util.Optional;

/**
 * Attribute 仓储接口
 * 定义 Attribute 聚合根的持久化操作
 */
public interface AttributeRepository {

    /**
     * 保存属性
     * 
     * @param attribute 属性实体
     * @return 保存后的属性（包含生成的 ID）
     */
    Attribute save(Attribute attribute);

    /**
     * 更新属性
     * 
     * @param attribute 属性实体
     */
    void update(Attribute attribute);

    /**
     * 根据 ID 查询属性
     * 
     * @param id 属性 ID
     * @return 属性实体（如果存在）
     */
    Optional<Attribute> findById(Long id);

    /**
     * 根据属性名称查询属性
     * 
     * @param name 属性名称
     * @return 属性实体（如果存在）
     */
    Optional<Attribute> findByName(String name);

    /**
     * 检查属性名称是否存在
     * 
     * @param name 属性名称
     * @return 是否存在
     */
    boolean existsByName(String name);

    /**
     * 查询所有属性
     * 
     * @return 所有属性列表
     */
    List<Attribute> findAll();

    /**
     * 根据 ID 列表批量查询属性
     * 
     * @param ids 属性 ID 列表
     * @return 属性列表
     */
    List<Attribute> findByIds(List<Long> ids);

    /**
     * 删除属性
     * 
     * @param id 属性 ID
     */
    void deleteById(Long id);
}

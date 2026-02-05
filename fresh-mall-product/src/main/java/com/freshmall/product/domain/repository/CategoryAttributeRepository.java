package com.freshmall.product.domain.repository;

import com.freshmall.product.domain.aggregate.CategoryAttribute;

import java.util.List;
import java.util.Optional;

/**
 * CategoryAttribute 仓储接口
 * 定义 CategoryAttribute 聚合根的持久化操作
 */
public interface CategoryAttributeRepository {

    /**
     * 保存类目-属性关联
     * 
     * @param categoryAttribute 类目-属性关联实体
     * @return 保存后的关联（包含生成的 ID）
     */
    CategoryAttribute save(CategoryAttribute categoryAttribute);

    /**
     * 更新类目-属性关联
     * 
     * @param categoryAttribute 类目-属性关联实体
     */
    void update(CategoryAttribute categoryAttribute);

    /**
     * 根据 ID 查询类目-属性关联
     * 
     * @param id 关联 ID
     * @return 类目-属性关联实体（如果存在）
     */
    Optional<CategoryAttribute> findById(Long id);

    /**
     * 根据类目 ID 查询所有属性关联（按 sortOrder 排序）
     * 
     * @param categoryId 类目 ID
     * @return 类目-属性关联列表
     */
    List<CategoryAttribute> findByCategoryId(Long categoryId);

    /**
     * 根据属性 ID 查询所有类目关联
     * 
     * @param attributeId 属性 ID
     * @return 类目-属性关联列表
     */
    List<CategoryAttribute> findByAttributeId(Long attributeId);

    /**
     * 根据类目 ID 和属性 ID 查询关联
     * 
     * @param categoryId 类目 ID
     * @param attributeId 属性 ID
     * @return 类目-属性关联实体（如果存在）
     */
    Optional<CategoryAttribute> findByCategoryIdAndAttributeId(Long categoryId, Long attributeId);

    /**
     * 检查类目-属性关联是否存在
     * 
     * @param categoryId 类目 ID
     * @param attributeId 属性 ID
     * @return 是否存在
     */
    boolean existsByCategoryIdAndAttributeId(Long categoryId, Long attributeId);

    /**
     * 根据类目 ID 查询必需属性关联
     * 
     * @param categoryId 类目 ID
     * @return 必需属性关联列表
     */
    List<CategoryAttribute> findRequiredByCategoryId(Long categoryId);

    /**
     * 删除类目-属性关联
     * 
     * @param id 关联 ID
     */
    void deleteById(Long id);

    /**
     * 根据类目 ID 删除所有关联
     * 
     * @param categoryId 类目 ID
     */
    void deleteByCategoryId(Long categoryId);

    /**
     * 根据属性 ID 删除所有关联
     * 
     * @param attributeId 属性 ID
     */
    void deleteByAttributeId(Long attributeId);
}

package com.freshmall.product.domain.repository;

import com.freshmall.product.domain.aggregate.Category;

import java.util.List;
import java.util.Optional;

/**
 * Category 仓储接口
 * 定义 Category 聚合根的持久化操作
 */
public interface CategoryRepository {

    /**
     * 保存类目
     * 
     * @param category 类目实体
     * @return 保存后的类目（包含生成的 ID）
     */
    Category save(Category category);

    /**
     * 更新类目
     * 
     * @param category 类目实体
     */
    void update(Category category);

    /**
     * 根据 ID 查询类目
     * 
     * @param id 类目 ID
     * @return 类目实体（如果存在）
     */
    Optional<Category> findById(Long id);

    /**
     * 根据类目编码查询类目
     * 
     * @param categoryCode 类目编码
     * @return 类目实体（如果存在）
     */
    Optional<Category> findByCategoryCode(String categoryCode);

    /**
     * 检查类目编码是否存在
     * 
     * @param categoryCode 类目编码
     * @return 是否存在
     */
    boolean existsByCategoryCode(String categoryCode);

    /**
     * 查询所有类目
     * 
     * @return 所有类目列表
     */
    List<Category> findAll();

    /**
     * 根据父类目 ID 查询子类目
     * 
     * @param parentId 父类目 ID
     * @return 子类目列表
     */
    List<Category> findByParentId(Long parentId);

    /**
     * 检查类目是否有子类目
     * 
     * @param categoryId 类目 ID
     * @return 是否有子类目
     */
    boolean hasChildren(Long categoryId);

    /**
     * 检查类目是否有关联的 SPU
     * 
     * @param categoryId 类目 ID
     * @return 是否有关联的 SPU
     */
    boolean hasAssociatedSpu(Long categoryId);

    /**
     * 删除类目
     * 
     * @param id 类目 ID
     */
    void deleteById(Long id);
}

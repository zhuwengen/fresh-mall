package com.freshmall.product.domain.service;

import com.freshmall.product.domain.aggregate.Category;

import java.util.List;

/**
 * Category 领域服务接口
 * 管理类目的创建、查询、树结构和删除约束
 */
public interface CategoryService {

    /**
     * 创建类目
     * 
     * @param categoryCode 类目编码（唯一）
     * @param name 类目名称
     * @param parentId 父类目 ID（根类目为 null）
     * @param sortOrder 排序顺序
     * @return 创建的类目 ID
     */
    Long createCategory(String categoryCode, String name, Long parentId, Integer sortOrder);

    /**
     * 查询类目树
     * 返回完整的类目树结构，从根类目开始
     * 
     * @return 类目树列表（根类目列表，每个根类目包含其子类目）
     */
    List<Category> getCategoryTree();

    /**
     * 查询类目路径（从根到叶）
     * 返回从根类目到指定类目的完整路径
     * 
     * @param categoryId 类目 ID
     * @return 类目路径列表（从根到叶）
     */
    List<Category> getCategoryPath(Long categoryId);

    /**
     * 判断是否为叶子类目
     * 叶子类目是没有子类目的类目
     * 
     * @param categoryId 类目 ID
     * @return 是否为叶子类目
     */
    boolean isLeafCategory(Long categoryId);

    /**
     * 删除类目
     * 检查删除约束：类目不能有关联的 SPU
     * 
     * @param categoryId 类目 ID
     */
    void deleteCategory(Long categoryId);

    /**
     * 根据 ID 查询类目
     * 
     * @param categoryId 类目 ID
     * @return 类目实体
     */
    Category getCategoryById(Long categoryId);
}

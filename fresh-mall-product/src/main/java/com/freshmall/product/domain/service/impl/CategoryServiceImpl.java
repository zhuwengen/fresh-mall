package com.freshmall.product.domain.service.impl;

import com.freshmall.common.enums.ResultCode;
import com.freshmall.common.exception.BusinessException;
import com.freshmall.product.domain.aggregate.Category;
import com.freshmall.product.domain.repository.CategoryRepository;
import com.freshmall.product.domain.service.CategoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Category 领域服务实现
 * 实现类目管理的核心业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Long createCategory(String categoryCode, String name, Long parentId, Integer sortOrder) {
        log.info("创建类目: categoryCode={}, name={}, parentId={}, sortOrder={}", 
                 categoryCode, name, parentId, sortOrder);

        // 验证类目编码和名称
        Category.validateCategoryCode(categoryCode);
        Category.validateName(name);

        // 检查类目编码是否已存在
        if (categoryRepository.existsByCategoryCode(categoryCode)) {
            throw new BusinessException(ResultCode.CATEGORY_CODE_DUPLICATE);
        }

        // 如果有父类目，验证父类目是否存在
        Integer parentLevel = null;
        if (parentId != null) {
            Category parentCategory = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new BusinessException(ResultCode.CATEGORY_PARENT_NOT_FOUND));
            parentLevel = parentCategory.getLevel();
        }

        // 创建类目实体
        Category category = new Category();
        category.setCategoryCode(categoryCode);
        category.setName(name);
        category.setParentId(parentId);
        category.setSortOrder(sortOrder != null ? sortOrder : 0);
        
        // 计算并设置层级
        category.setLevelFromParent(parentLevel);

        // 保存类目
        Category savedCategory = categoryRepository.save(category);
        
        log.info("类目创建成功: id={}, level={}", savedCategory.getId(), savedCategory.getLevel());
        return savedCategory.getId();
    }

    @Override
    public List<Category> getCategoryTree() {
        log.info("查询类目树");

        // 查询所有类目
        List<Category> allCategories = categoryRepository.findAll();

        // 构建类目树
        return buildCategoryTree(allCategories);
    }

    @Override
    public List<Category> getCategoryPath(Long categoryId) {
        log.info("查询类目路径: categoryId={}", categoryId);

        // 验证类目是否存在
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ResultCode.CATEGORY_NOT_FOUND));

        // 构建从根到叶的路径
        List<Category> path = new ArrayList<>();
        path.add(category);

        // 向上遍历到根类目
        Long currentParentId = category.getParentId();
        while (currentParentId != null) {
            Category parentCategory = categoryRepository.findById(currentParentId)
                    .orElseThrow(() -> new BusinessException(ResultCode.CATEGORY_NOT_FOUND));
            path.add(0, parentCategory); // 添加到列表开头
            currentParentId = parentCategory.getParentId();
        }

        log.info("类目路径查询成功: categoryId={}, pathLength={}", categoryId, path.size());
        return path;
    }

    @Override
    public boolean isLeafCategory(Long categoryId) {
        log.info("判断是否为叶子类目: categoryId={}", categoryId);

        // 验证类目是否存在
        if (!categoryRepository.findById(categoryId).isPresent()) {
            throw new BusinessException(ResultCode.CATEGORY_NOT_FOUND);
        }

        // 检查是否有子类目
        boolean hasChildren = categoryRepository.hasChildren(categoryId);
        boolean isLeaf = !hasChildren;

        log.info("叶子类目判断结果: categoryId={}, isLeaf={}", categoryId, isLeaf);
        return isLeaf;
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        log.info("删除类目: categoryId={}", categoryId);

        // 验证类目是否存在
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ResultCode.CATEGORY_NOT_FOUND));

        // 检查是否有关联的 SPU
        if (categoryRepository.hasAssociatedSpu(categoryId)) {
            throw new BusinessException(ResultCode.CATEGORY_HAS_SPU);
        }

        // 删除类目
        categoryRepository.deleteById(categoryId);

        log.info("类目删除成功: categoryId={}", categoryId);
    }

    @Override
    public Category getCategoryById(Long categoryId) {
        log.info("查询类目详情: categoryId={}", categoryId);

        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ResultCode.CATEGORY_NOT_FOUND));
    }

    /**
     * 构建类目树
     * 将扁平的类目列表转换为树形结构
     * 
     * @param allCategories 所有类目列表
     * @return 根类目列表（每个根类目包含其子类目）
     */
    private List<Category> buildCategoryTree(List<Category> allCategories) {
        // 按父类目 ID 分组
        Map<Long, List<Category>> categoryMap = allCategories.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(Category::getParentId));

        // 获取根类目（parentId 为 null）
        List<Category> rootCategories = allCategories.stream()
                .filter(c -> c.getParentId() == null)
                .collect(Collectors.toList());

        // 递归构建树
        for (Category root : rootCategories) {
            buildTreeRecursive(root, categoryMap);
        }

        return rootCategories;
    }

    /**
     * 递归构建类目树
     * 
     * @param parent 父类目
     * @param categoryMap 类目映射（父 ID -> 子类目列表）
     */
    private void buildTreeRecursive(Category parent, Map<Long, List<Category>> categoryMap) {
        List<Category> children = categoryMap.get(parent.getId());
        if (children != null && !children.isEmpty()) {
            // 注意：这里我们不在 Category 实体中存储 children 字段
            // 因为 Category 是聚合根，不应该包含集合关系
            // 如果需要返回树形结构，应该创建一个 VO 对象
            // 这里仅作为示例，实际使用时需要创建 CategoryTreeVO
            for (Category child : children) {
                buildTreeRecursive(child, categoryMap);
            }
        }
    }
}

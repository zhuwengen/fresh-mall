package com.freshmall.product.infrastructure.persistent.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshmall.product.domain.aggregate.Category;
import com.freshmall.product.domain.repository.CategoryRepository;
import com.freshmall.product.infrastructure.persistent.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Category 仓储实现
 * 使用 MyBatis Plus 实现持久化操作
 */
@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryMapper categoryMapper;

    @Override
    public Category save(Category category) {
        categoryMapper.insert(category);
        return category;
    }

    @Override
    public void update(Category category) {
        categoryMapper.updateById(category);
    }

    @Override
    public Optional<Category> findById(Long id) {
        Category category = categoryMapper.selectById(id);
        return Optional.ofNullable(category);
    }

    @Override
    public Optional<Category> findByCategoryCode(String categoryCode) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getCategoryCode, categoryCode);
        Category category = categoryMapper.selectOne(wrapper);
        return Optional.ofNullable(category);
    }

    @Override
    public boolean existsByCategoryCode(String categoryCode) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getCategoryCode, categoryCode);
        return categoryMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<Category> findAll() {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Category::getSortOrder);
        return categoryMapper.selectList(wrapper);
    }

    @Override
    public List<Category> findByParentId(Long parentId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        if (parentId == null) {
            wrapper.isNull(Category::getParentId);
        } else {
            wrapper.eq(Category::getParentId, parentId);
        }
        wrapper.orderByAsc(Category::getSortOrder);
        return categoryMapper.selectList(wrapper);
    }

    @Override
    public boolean hasChildren(Long categoryId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getParentId, categoryId);
        return categoryMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean hasAssociatedSpu(Long categoryId) {
        return categoryMapper.countSpuByCategoryId(categoryId) > 0;
    }

    @Override
    public void deleteById(Long id) {
        categoryMapper.deleteById(id);
    }
}

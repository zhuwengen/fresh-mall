package com.freshmall.product.infrastructure.persistent.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshmall.product.domain.aggregate.CategoryAttribute;
import com.freshmall.product.domain.repository.CategoryAttributeRepository;
import com.freshmall.product.infrastructure.persistent.mapper.CategoryAttributeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * CategoryAttribute 仓储实现
 * 使用 MyBatis Plus 实现持久化操作
 */
@Repository
@RequiredArgsConstructor
public class CategoryAttributeRepositoryImpl implements CategoryAttributeRepository {

    private final CategoryAttributeMapper categoryAttributeMapper;

    @Override
    public CategoryAttribute save(CategoryAttribute categoryAttribute) {
        categoryAttributeMapper.insert(categoryAttribute);
        return categoryAttribute;
    }

    @Override
    public void update(CategoryAttribute categoryAttribute) {
        categoryAttributeMapper.updateById(categoryAttribute);
    }

    @Override
    public Optional<CategoryAttribute> findById(Long id) {
        CategoryAttribute categoryAttribute = categoryAttributeMapper.selectById(id);
        return Optional.ofNullable(categoryAttribute);
    }

    @Override
    public List<CategoryAttribute> findByCategoryId(Long categoryId) {
        LambdaQueryWrapper<CategoryAttribute> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryAttribute::getCategoryId, categoryId);
        wrapper.orderByAsc(CategoryAttribute::getSortOrder);
        return categoryAttributeMapper.selectList(wrapper);
    }

    @Override
    public List<CategoryAttribute> findByAttributeId(Long attributeId) {
        LambdaQueryWrapper<CategoryAttribute> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryAttribute::getAttributeId, attributeId);
        return categoryAttributeMapper.selectList(wrapper);
    }

    @Override
    public Optional<CategoryAttribute> findByCategoryIdAndAttributeId(Long categoryId, Long attributeId) {
        LambdaQueryWrapper<CategoryAttribute> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryAttribute::getCategoryId, categoryId);
        wrapper.eq(CategoryAttribute::getAttributeId, attributeId);
        CategoryAttribute categoryAttribute = categoryAttributeMapper.selectOne(wrapper);
        return Optional.ofNullable(categoryAttribute);
    }

    @Override
    public boolean existsByCategoryIdAndAttributeId(Long categoryId, Long attributeId) {
        LambdaQueryWrapper<CategoryAttribute> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryAttribute::getCategoryId, categoryId);
        wrapper.eq(CategoryAttribute::getAttributeId, attributeId);
        return categoryAttributeMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<CategoryAttribute> findRequiredByCategoryId(Long categoryId) {
        LambdaQueryWrapper<CategoryAttribute> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryAttribute::getCategoryId, categoryId);
        wrapper.eq(CategoryAttribute::getRequired, true);
        wrapper.orderByAsc(CategoryAttribute::getSortOrder);
        return categoryAttributeMapper.selectList(wrapper);
    }

    @Override
    public void deleteById(Long id) {
        categoryAttributeMapper.deleteById(id);
    }

    @Override
    public void deleteByCategoryId(Long categoryId) {
        LambdaQueryWrapper<CategoryAttribute> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryAttribute::getCategoryId, categoryId);
        categoryAttributeMapper.delete(wrapper);
    }

    @Override
    public void deleteByAttributeId(Long attributeId) {
        LambdaQueryWrapper<CategoryAttribute> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(CategoryAttribute::getAttributeId, attributeId);
        categoryAttributeMapper.delete(wrapper);
    }
}

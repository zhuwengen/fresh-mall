package com.freshmall.product.infrastructure.persistent.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshmall.product.domain.aggregate.Attribute;
import com.freshmall.product.domain.repository.AttributeRepository;
import com.freshmall.product.infrastructure.persistent.mapper.AttributeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Attribute 仓储实现
 * 使用 MyBatis Plus 实现持久化操作
 */
@Repository
@RequiredArgsConstructor
public class AttributeRepositoryImpl implements AttributeRepository {

    private final AttributeMapper attributeMapper;

    @Override
    public Attribute save(Attribute attribute) {
        attributeMapper.insert(attribute);
        return attribute;
    }

    @Override
    public void update(Attribute attribute) {
        attributeMapper.updateById(attribute);
    }

    @Override
    public Optional<Attribute> findById(Long id) {
        Attribute attribute = attributeMapper.selectById(id);
        return Optional.ofNullable(attribute);
    }

    @Override
    public Optional<Attribute> findByName(String name) {
        LambdaQueryWrapper<Attribute> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attribute::getName, name);
        Attribute attribute = attributeMapper.selectOne(wrapper);
        return Optional.ofNullable(attribute);
    }

    @Override
    public boolean existsByName(String name) {
        LambdaQueryWrapper<Attribute> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attribute::getName, name);
        return attributeMapper.selectCount(wrapper) > 0;
    }

    @Override
    public List<Attribute> findAll() {
        return attributeMapper.selectList(null);
    }

    @Override
    public List<Attribute> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return attributeMapper.selectBatchIds(ids);
    }

    @Override
    public void deleteById(Long id) {
        attributeMapper.deleteById(id);
    }
}

package com.freshmall.product.infrastructure.persistent.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.freshmall.product.domain.model.Sku;
import com.freshmall.product.domain.repository.SkuRepository;
import com.freshmall.product.infrastructure.persistent.mapper.SkuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * SKU 仓储实现
 * 使用 MyBatis Plus 实现持久化操作
 */
@Repository
@RequiredArgsConstructor
public class SkuRepositoryImpl implements SkuRepository {

    private final SkuMapper skuMapper;

    @Override
    public Sku save(Sku sku) {
        skuMapper.insert(sku);
        return sku;
    }

    @Override
    public void update(Sku sku) {
        skuMapper.updateById(sku);
    }

    @Override
    public Optional<Sku> findById(Long id) {
        Sku sku = skuMapper.selectById(id);
        return Optional.ofNullable(sku);
    }

    @Override
    public Optional<Sku> findByIdIncludingDeleted(Long id) {
        LambdaQueryWrapper<Sku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Sku::getId, id);
        wrapper.apply("1=1");
        Sku sku = skuMapper.selectOne(wrapper);
        return Optional.ofNullable(sku);
    }

    @Override
    public List<Sku> findBySpuId(Long spuId) {
        LambdaQueryWrapper<Sku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Sku::getSpuId, spuId);
        return skuMapper.selectList(wrapper);
    }

    @Override
    public List<Sku> findEnabledBySpuId(Long spuId) {
        LambdaQueryWrapper<Sku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Sku::getSpuId, spuId);
        wrapper.eq(Sku::getEnabled, true);
        return skuMapper.selectList(wrapper);
    }

    @Override
    public Optional<Sku> findBySkuCode(String skuCode) {
        LambdaQueryWrapper<Sku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Sku::getSkuCode, skuCode);
        Sku sku = skuMapper.selectOne(wrapper);
        return Optional.ofNullable(sku);
    }

    @Override
    public boolean existsBySkuCode(String skuCode) {
        LambdaQueryWrapper<Sku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Sku::getSkuCode, skuCode);
        return skuMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean existsBySkuCodeExcludingId(String skuCode, Long excludeId) {
        LambdaQueryWrapper<Sku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Sku::getSkuCode, skuCode);
        wrapper.ne(Sku::getId, excludeId);
        return skuMapper.selectCount(wrapper) > 0;
    }

    @Override
    public boolean hasEnabledSku(Long spuId) {
        LambdaQueryWrapper<Sku> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Sku::getSpuId, spuId);
        wrapper.eq(Sku::getEnabled, true);
        return skuMapper.selectCount(wrapper) > 0;
    }

    @Override
    public void deleteById(Long id) {
        skuMapper.deleteById(id);
    }

    @Override
    public void deleteBySpuId(Long spuId) {
        LambdaUpdateWrapper<Sku> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(Sku::getSpuId, spuId);
        wrapper.set(Sku::getDeleted, 1);
        wrapper.set(Sku::getDeletedAt, LocalDateTime.now());
        skuMapper.update(null, wrapper);
    }
}

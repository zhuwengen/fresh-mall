package com.freshmall.product.infrastructure.persistent.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshmall.product.domain.repository.SkuStockRepository;
import com.freshmall.product.domain.stock.SkuStock;
import com.freshmall.product.infrastructure.persistent.mapper.SkuStockMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * SKU 库存仓储实现
 * 使用 MyBatis Plus 实现持久化操作
 * 乐观锁通过 @Version 注解自动处理
 */
@Repository
@RequiredArgsConstructor
public class SkuStockRepositoryImpl implements SkuStockRepository {

    private final SkuStockMapper skuStockMapper;

    @Override
    public SkuStock save(SkuStock stock) {
        skuStockMapper.insert(stock);
        return stock;
    }

    @Override
    public boolean update(SkuStock stock) {
        // MyBatis Plus 的 updateById 会自动处理乐观锁
        // 如果版本号不匹配，返回 0（更新失败）
        int affected = skuStockMapper.updateById(stock);
        return affected > 0;
    }

    @Override
    public Optional<SkuStock> findBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuStock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuStock::getSkuId, skuId);
        SkuStock stock = skuStockMapper.selectOne(wrapper);
        return Optional.ofNullable(stock);
    }

    @Override
    public Optional<SkuStock> findById(Long id) {
        SkuStock stock = skuStockMapper.selectById(id);
        return Optional.ofNullable(stock);
    }

    @Override
    public boolean existsBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuStock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuStock::getSkuId, skuId);
        return skuStockMapper.selectCount(wrapper) > 0;
    }

    @Override
    public void deleteById(Long id) {
        skuStockMapper.deleteById(id);
    }

    @Override
    public void deleteBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuStock> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuStock::getSkuId, skuId);
        skuStockMapper.delete(wrapper);
    }
}

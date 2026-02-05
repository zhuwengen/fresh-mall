package com.freshmall.product.infrastructure.persistent.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshmall.product.domain.model.ChannelType;
import com.freshmall.product.domain.price.SkuPrice;
import com.freshmall.product.domain.repository.SkuPriceRepository;
import com.freshmall.product.infrastructure.persistent.mapper.SkuPriceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * SKU 价格仓储实现
 * 使用 MyBatis Plus 实现持久化操作
 */
@Repository
@RequiredArgsConstructor
public class SkuPriceRepositoryImpl implements SkuPriceRepository {

    private final SkuPriceMapper skuPriceMapper;

    @Override
    public SkuPrice save(SkuPrice skuPrice) {
        skuPriceMapper.insert(skuPrice);
        return skuPrice;
    }

    @Override
    public Optional<SkuPrice> findById(Long id) {
        SkuPrice skuPrice = skuPriceMapper.selectById(id);
        return Optional.ofNullable(skuPrice);
    }

    @Override
    public List<SkuPrice> findBySkuIdAndChannel(Long skuId, ChannelType channel) {
        LambdaQueryWrapper<SkuPrice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuPrice::getSkuId, skuId)
               .eq(SkuPrice::getChannel, channel)
               .orderByDesc(SkuPrice::getStartTime);
        return skuPriceMapper.selectList(wrapper);
    }

    @Override
    public List<SkuPrice> findActiveBySkuIdAndChannel(Long skuId, ChannelType channel, LocalDateTime now) {
        LambdaQueryWrapper<SkuPrice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuPrice::getSkuId, skuId)
               .eq(SkuPrice::getChannel, channel)
               .le(SkuPrice::getStartTime, now)
               .and(w -> w.isNull(SkuPrice::getEndTime).or().ge(SkuPrice::getEndTime, now))
               .orderByDesc(SkuPrice::getStartTime);
        return skuPriceMapper.selectList(wrapper);
    }

    @Override
    public List<SkuPrice> findBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuPrice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuPrice::getSkuId, skuId)
               .orderByDesc(SkuPrice::getStartTime);
        return skuPriceMapper.selectList(wrapper);
    }

    @Override
    public List<SkuPrice> findActivePricesBySkuIds(List<Long> skuIds, ChannelType channel, LocalDateTime now) {
        if (skuIds == null || skuIds.isEmpty()) {
            return List.of();
        }
        
        LambdaQueryWrapper<SkuPrice> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SkuPrice::getSkuId, skuIds)
               .eq(SkuPrice::getChannel, channel)
               .le(SkuPrice::getStartTime, now)
               .and(w -> w.isNull(SkuPrice::getEndTime).or().ge(SkuPrice::getEndTime, now))
               .orderByDesc(SkuPrice::getStartTime);
        return skuPriceMapper.selectList(wrapper);
    }

    @Override
    public void deleteById(Long id) {
        skuPriceMapper.deleteById(id);
    }

    @Override
    public void deleteBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuPrice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SkuPrice::getSkuId, skuId);
        skuPriceMapper.delete(wrapper);
    }
}

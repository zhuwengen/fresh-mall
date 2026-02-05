package com.freshmall.product.domain.service.impl;

import com.freshmall.common.enums.ResultCode;
import com.freshmall.common.exception.BusinessException;
import com.freshmall.product.domain.model.ChannelType;
import com.freshmall.product.domain.price.SkuPrice;
import com.freshmall.product.domain.repository.SkuPriceRepository;
import com.freshmall.product.domain.repository.SkuRepository;
import com.freshmall.product.domain.service.PriceService;
import com.freshmall.product.domain.valueobject.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 价格领域服务实现
 * 管理多渠道定价、查询有效价格
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PriceServiceImpl implements PriceService {

    private final SkuPriceRepository skuPriceRepository;
    private final SkuRepository skuRepository;

    /**
     * 设置 SKU 价格（创建新记录）
     * 
     * 验证需求：
     * - 5.1: 支持多渠道定价（APP、WEB、STORE）
     * - 5.2: 验证价格金额大于零
     * - 5.5: 更新价格时创建新记录（不修改现有记录）
     * - 5.6: 支持时间范围（start_time 和 end_time）
     * - 11.5: 验证 SKU 引用存在且未删除
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long setSkuPrice(Long skuId, ChannelType channel, BigDecimal price,
                           LocalDateTime startTime, LocalDateTime endTime) {
        log.info("设置 SKU 价格: skuId={}, channel={}, price={}, startTime={}, endTime={}", 
                 skuId, channel, price, startTime, endTime);
        
        // 验证 SKU 引用存在且未删除（需求 11.5）
        validateSkuReference(skuId);
        
        // 创建价格记录（会自动验证价格金额 > 0 和时间范围，需求 5.2, 5.6）
        SkuPrice skuPrice = SkuPrice.create(skuId, channel, price, startTime, endTime);
        
        // 保存价格记录（需求 5.5: 创建新记录而不是修改现有记录）
        SkuPrice savedPrice = skuPriceRepository.save(skuPrice);
        
        log.info("SKU 价格设置成功: priceId={}", savedPrice.getId());
        return savedPrice.getId();
    }

    /**
     * 查询 SKU 当前有效价格
     * 如果存在多个有效价格，返回 start_time 最晚的价格
     * 
     * 验证需求：
     * - 5.3: 返回当前时间在 start_time 和 end_time 之间的有效价格
     * - 5.4: 如果存在多个有效价格，返回 start_time 最晚的价格
     */
    @Override
    @Transactional(readOnly = true)
    public Money getCurrentPrice(Long skuId, ChannelType channel) {
        log.debug("查询 SKU 当前有效价格: skuId={}, channel={}", skuId, channel);
        
        LocalDateTime now = LocalDateTime.now();
        
        // 查询有效价格（按 start_time 降序排序，需求 5.3）
        List<SkuPrice> activePrices = skuPriceRepository.findActiveBySkuIdAndChannel(
            skuId, channel, now
        );
        
        // 如果没有有效价格，抛出异常
        if (activePrices.isEmpty()) {
            log.warn("未找到有效价格: skuId={}, channel={}", skuId, channel);
            throw new BusinessException(ResultCode.PRICE_NOT_FOUND);
        }
        
        // 返回 start_time 最晚的价格（列表已按 start_time 降序排序，需求 5.4）
        SkuPrice latestPrice = activePrices.get(0);
        Money price = Money.of(latestPrice.getPrice());
        
        log.debug("查询到有效价格: skuId={}, channel={}, price={}", skuId, channel, price);
        return price;
    }

    /**
     * 批量查询多个 SKU 的当前有效价格
     * 
     * 验证需求：
     * - 5.3: 返回当前时间在 start_time 和 end_time 之间的有效价格
     * - 5.4: 如果存在多个有效价格，返回 start_time 最晚的价格
     */
    @Override
    @Transactional(readOnly = true)
    public Map<Long, Money> batchGetPrices(List<Long> skuIds, ChannelType channel) {
        log.debug("批量查询 SKU 当前有效价格: skuIds={}, channel={}", skuIds, channel);
        
        if (skuIds == null || skuIds.isEmpty()) {
            return new HashMap<>();
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // 批量查询有效价格（需求 5.3）
        List<SkuPrice> activePrices = skuPriceRepository.findActivePricesBySkuIds(
            skuIds, channel, now
        );
        
        // 按 SKU ID 分组，每组取 start_time 最晚的价格（需求 5.4）
        Map<Long, Money> priceMap = activePrices.stream()
            .collect(Collectors.groupingBy(
                SkuPrice::getSkuId,
                Collectors.collectingAndThen(
                    Collectors.maxBy((p1, p2) -> p1.getStartTime().compareTo(p2.getStartTime())),
                    optional -> optional.map(price -> Money.of(price.getPrice())).orElse(null)
                )
            ));
        
        log.debug("批量查询到有效价格: count={}", priceMap.size());
        return priceMap;
    }

    /**
     * 验证 SKU 引用存在且未删除
     * 
     * @param skuId SKU ID
     * @throws BusinessException 如果 SKU 不存在或已删除
     */
    private void validateSkuReference(Long skuId) {
        boolean exists = skuRepository.findById(skuId).isPresent();
        if (!exists) {
            log.warn("价格关联的 SKU 不存在或已删除: skuId={}", skuId);
            throw new BusinessException(ResultCode.PRICE_SKU_NOT_FOUND);
        }
    }
}

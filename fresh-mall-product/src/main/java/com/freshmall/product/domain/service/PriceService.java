package com.freshmall.product.domain.service;

import com.freshmall.product.domain.model.ChannelType;
import com.freshmall.product.domain.valueobject.Money;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 价格领域服务接口
 * 管理多渠道定价、查询有效价格
 */
public interface PriceService {

    /**
     * 设置 SKU 价格（创建新记录）
     * 
     * @param skuId SKU ID
     * @param channel 销售渠道
     * @param price 价格金额
     * @param startTime 生效时间
     * @param endTime 失效时间（null 表示永久有效）
     * @return 创建的价格记录 ID
     */
    Long setSkuPrice(Long skuId, ChannelType channel, BigDecimal price, 
                     LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 查询 SKU 当前有效价格
     * 如果存在多个有效价格，返回 start_time 最晚的价格
     * 
     * @param skuId SKU ID
     * @param channel 销售渠道
     * @return 当前有效价格
     */
    Money getCurrentPrice(Long skuId, ChannelType channel);

    /**
     * 批量查询多个 SKU 的当前有效价格
     * 
     * @param skuIds SKU ID 列表
     * @param channel 销售渠道
     * @return SKU ID 到价格的映射（不存在有效价格的 SKU 不在映射中）
     */
    Map<Long, Money> batchGetPrices(List<Long> skuIds, ChannelType channel);
}

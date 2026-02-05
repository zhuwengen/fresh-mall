package com.freshmall.product.domain.valueobject;

import com.freshmall.product.domain.model.ChannelType;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 价格策略值对象
 * 封装价格、渠道和有效期信息
 */
@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class PricePolicy implements Serializable {

    private final Long skuId;
    private final ChannelType channel;
    private final Money price;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;

    public static PricePolicy of(Long skuId, ChannelType channel, Money price, 
                                 LocalDateTime startTime, LocalDateTime endTime) {
        if (skuId == null) {
            throw new IllegalArgumentException("SKU ID 不能为空");
        }
        if (channel == null) {
            throw new IllegalArgumentException("渠道不能为空");
        }
        if (price == null || !price.isPositive()) {
            throw new IllegalArgumentException("价格必须大于零");
        }
        if (startTime == null) {
            throw new IllegalArgumentException("开始时间不能为空");
        }
        if (endTime != null && endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("结束时间不能早于开始时间");
        }
        return new PricePolicy(skuId, channel, price, startTime, endTime);
    }

    public boolean isActive(LocalDateTime now) {
        if (now == null) {
            throw new IllegalArgumentException("时间不能为空");
        }
        boolean afterStart = !now.isBefore(startTime);
        boolean beforeEnd = endTime == null || !now.isAfter(endTime);
        return afterStart && beforeEnd;
    }
    
    @Override
    public String toString() {
        return String.format("PricePolicy[skuId=%d, channel=%s, price=%s, startTime=%s, endTime=%s]",
                skuId, channel, price, startTime, endTime);
    }
}

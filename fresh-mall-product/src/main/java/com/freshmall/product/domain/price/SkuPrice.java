package com.freshmall.product.domain.price;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.freshmall.common.domain.BaseEntity;
import com.freshmall.product.domain.model.ChannelType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * SKU 价格聚合根
 * 管理多渠道定价策略
 * 
 * 设计原则：
 * 1. 价格记录不可变 - 更新价格时创建新记录
 * 2. 支持时间范围 - 通过 start_time 和 end_time 控制有效期
 * 3. 多渠道定价 - 同一 SKU 可以在不同渠道有不同价格
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "t_sku_price", autoResultMap = true)
public class SkuPrice extends BaseEntity {

    /**
     * SKU ID
     */
    @TableField("sku_id")
    private Long skuId;

    /**
     * 销售渠道
     */
    @TableField("channel")
    private ChannelType channel;

    /**
     * 价格金额（单位：元）
     */
    @TableField("price")
    private BigDecimal price;

    /**
     * 价格生效时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 价格失效时间（null 表示永久有效）
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 判断价格在指定时间是否有效
     * 
     * @param now 当前时间
     * @return 是否有效
     */
    public boolean isActive(LocalDateTime now) {
        if (now == null) {
            throw new IllegalArgumentException("时间不能为空");
        }
        
        // 检查是否在开始时间之后
        boolean afterStart = !now.isBefore(startTime);
        
        // 检查是否在结束时间之前（null 表示永久有效）
        boolean beforeEnd = endTime == null || !now.isAfter(endTime);
        
        return afterStart && beforeEnd;
    }

    /**
     * 验证价格数据的合法性
     * 
     * @throws IllegalArgumentException 如果数据不合法
     */
    public void validate() {
        if (skuId == null) {
            throw new IllegalArgumentException("SKU ID 不能为空");
        }
        
        if (channel == null) {
            throw new IllegalArgumentException("渠道不能为空");
        }
        
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("价格必须大于零");
        }
        
        if (startTime == null) {
            throw new IllegalArgumentException("开始时间不能为空");
        }
        
        if (endTime != null && endTime.isBefore(startTime)) {
            throw new IllegalArgumentException("结束时间不能早于开始时间");
        }
    }

    /**
     * 创建价格记录的工厂方法
     * 
     * @param skuId SKU ID
     * @param channel 销售渠道
     * @param price 价格金额
     * @param startTime 生效时间
     * @param endTime 失效时间
     * @return 价格实体
     */
    public static SkuPrice create(Long skuId, ChannelType channel, BigDecimal price,
                                  LocalDateTime startTime, LocalDateTime endTime) {
        SkuPrice skuPrice = SkuPrice.builder()
                .skuId(skuId)
                .channel(channel)
                .price(price)
                .startTime(startTime)
                .endTime(endTime)
                .build();
        
        skuPrice.validate();
        return skuPrice;
    }
}

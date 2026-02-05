package com.freshmall.product.domain.model;

import com.baomidou.mybatisplus.annotation.TableName;
import com.freshmall.common.domain.BaseEntity;
import com.freshmall.product.domain.valueobject.Money;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 商品索引实体
 * 用于商品搜索和过滤的反规范化表
 * 
 * 设计说明：
 * - 索引表仅用于查询，不作为业务真实数据源
 * - 当 SPU 发布时创建/更新索引记录
 * - 当 SKU 数据变更（价格、库存、启用状态）时异步更新索引
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_product_index")
public class ProductIndex extends BaseEntity {

    /**
     * SPU ID
     */
    private Long spuId;

    /**
     * SKU ID（唯一索引）
     */
    private Long skuId;

    /**
     * SPU 名称（用于搜索）
     */
    private String spuName;

    /**
     * 类目 ID（用于类目过滤）
     */
    private Long categoryId;

    /**
     * 最低价格（所有渠道中的最低价）
     */
    private BigDecimal minPrice;

    /**
     * 最高价格（所有渠道中的最高价）
     */
    private BigDecimal maxPrice;

    /**
     * 库存状态
     */
    private StockStatus stockStatus;

    /**
     * SPU 是否已删除（冗余字段，用于快速过滤）
     */
    private Boolean spuDeleted;

    /**
     * SKU 是否启用（冗余字段，用于快速过滤）
     */
    private Boolean skuEnabled;

    /**
     * 获取最低价格的 Money 对象
     * 
     * @return Money 对象
     */
    public Money getMinPriceMoney() {
        return Money.of(this.minPrice);
    }

    /**
     * 获取最高价格的 Money 对象
     * 
     * @return Money 对象
     */
    public Money getMaxPriceMoney() {
        return Money.of(this.maxPrice);
    }

    /**
     * 设置最低价格
     * 
     * @param money Money 对象
     */
    public void setMinPriceMoney(Money money) {
        this.minPrice = money.getAmount();
    }

    /**
     * 设置最高价格
     * 
     * @param money Money 对象
     */
    public void setMaxPriceMoney(Money money) {
        this.maxPrice = money.getAmount();
    }

    /**
     * 检查是否应该在搜索结果中显示
     * 
     * @return 是否可见
     */
    public boolean isVisible() {
        return !Boolean.TRUE.equals(this.spuDeleted) && Boolean.TRUE.equals(this.skuEnabled);
    }

    /**
     * 检查价格是否在指定范围内
     * 
     * @param minPriceFilter 最低价格过滤条件
     * @param maxPriceFilter 最高价格过滤条件
     * @return 是否在范围内
     */
    public boolean isPriceInRange(BigDecimal minPriceFilter, BigDecimal maxPriceFilter) {
        if (minPriceFilter != null && this.maxPrice.compareTo(minPriceFilter) < 0) {
            return false;
        }
        if (maxPriceFilter != null && this.minPrice.compareTo(maxPriceFilter) > 0) {
            return false;
        }
        return true;
    }
}

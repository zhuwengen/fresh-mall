package com.freshmall.product.domain.stock;

import com.baomidou.mybatisplus.annotation.*;
import com.freshmall.common.domain.BaseEntity;
import com.freshmall.common.enums.ResultCode;
import com.freshmall.common.exception.BusinessException;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * SKU 库存聚合根
 * 管理库存数量和锁定操作，支持高并发场景
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_sku_stock")
public class SkuStock extends BaseEntity {

    /**
     * SKU ID（唯一索引）
     */
    @TableField("sku_id")
    private Long skuId;

    /**
     * 总库存
     */
    @TableField("total_stock")
    private Integer totalStock;

    /**
     * 可用库存
     */
    @TableField("available_stock")
    private Integer availableStock;

    /**
     * 锁定库存
     */
    @TableField("lock_stock")
    private Integer lockStock;

    /**
     * 版本号（乐观锁）
     */
    @Version
    @TableField("version")
    private Integer version;

    /**
     * 初始化库存
     * 
     * @param skuId SKU ID
     * @param totalStock 总库存
     * @return 初始化的库存实体
     */
    public static SkuStock initialize(Long skuId, Integer totalStock) {
        if (totalStock < 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "库存数量不能为负数");
        }
        
        SkuStock stock = new SkuStock();
        stock.setSkuId(skuId);
        stock.setTotalStock(totalStock);
        stock.setAvailableStock(totalStock);
        stock.setLockStock(0);
        stock.setVersion(0);
        
        return stock;
    }

    /**
     * 扣减库存（锁定）
     * 减少可用库存，增加锁定库存
     * 
     * @param quantity 扣减数量
     * @throws BusinessException 如果库存不足
     */
    public void deduct(Integer quantity) {
        if (quantity <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "扣减数量必须大于零");
        }
        
        if (this.availableStock < quantity) {
            throw new BusinessException(ResultCode.STOCK_INSUFFICIENT, 
                String.format("库存不足，可用库存: %d, 请求扣减: %d", this.availableStock, quantity));
        }
        
        this.availableStock -= quantity;
        this.lockStock += quantity;
        
        // 验证不变式
        assertInvariant();
    }

    /**
     * 确认扣减
     * 减少总库存和锁定库存
     * 
     * @param quantity 确认数量
     * @throws BusinessException 如果锁定库存不足
     */
    public void confirm(Integer quantity) {
        if (quantity <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "确认数量必须大于零");
        }
        
        if (this.lockStock < quantity) {
            throw new BusinessException(ResultCode.STOCK_INSUFFICIENT, 
                String.format("锁定库存不足，锁定库存: %d, 请求确认: %d", this.lockStock, quantity));
        }
        
        this.totalStock -= quantity;
        this.lockStock -= quantity;
        
        // 验证不变式
        assertInvariant();
    }

    /**
     * 释放库存
     * 减少锁定库存，增加可用库存
     * 
     * @param quantity 释放数量
     * @throws BusinessException 如果锁定库存不足
     */
    public void release(Integer quantity) {
        if (quantity <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "释放数量必须大于零");
        }
        
        if (this.lockStock < quantity) {
            throw new BusinessException(ResultCode.STOCK_INSUFFICIENT, 
                String.format("锁定库存不足，锁定库存: %d, 请求释放: %d", this.lockStock, quantity));
        }
        
        this.lockStock -= quantity;
        this.availableStock += quantity;
        
        // 验证不变式
        assertInvariant();
    }

    /**
     * 验证库存不变式
     * 不变式：total_stock = available_stock + lock_stock
     * 
     * @throws BusinessException 如果不变式被违反
     */
    public void assertInvariant() {
        if (this.totalStock != this.availableStock + this.lockStock) {
            throw new BusinessException(ResultCode.STOCK_INVARIANT_VIOLATED,
                String.format("库存数据不一致，总库存: %d, 可用库存: %d, 锁定库存: %d",
                    this.totalStock, this.availableStock, this.lockStock));
        }
    }

    /**
     * 检查库存是否充足
     * 
     * @param quantity 需要的数量
     * @return 是否充足
     */
    public boolean hasEnoughStock(Integer quantity) {
        return this.availableStock >= quantity;
    }
}

package com.freshmall.product.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 库存状态枚举
 */
@Getter
@AllArgsConstructor
public enum StockStatus {
    
    /**
     * 有货
     */
    IN_STOCK("有货"),
    
    /**
     * 低库存
     */
    LOW_STOCK("低库存"),
    
    /**
     * 无货
     */
    OUT_OF_STOCK("无货");
    
    private final String description;
    
    /**
     * 根据可用库存计算库存状态
     * @param availableStock 可用库存数量
     * @param lowStockThreshold 低库存阈值
     * @return 库存状态
     */
    public static StockStatus fromAvailableStock(Integer availableStock, Integer lowStockThreshold) {
        if (availableStock == null || availableStock <= 0) {
            return OUT_OF_STOCK;
        }
        if (availableStock < lowStockThreshold) {
            return LOW_STOCK;
        }
        return IN_STOCK;
    }
    
    /**
     * 使用默认阈值（10）计算库存状态
     */
    public static StockStatus fromAvailableStock(Integer availableStock) {
        return fromAvailableStock(availableStock, 10);
    }
}

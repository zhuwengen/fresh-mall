package com.freshmall.product.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * SPU 状态枚举
 */
@Getter
@AllArgsConstructor
public enum SpuStatus {
    
    /**
     * 草稿状态 - 新创建的 SPU
     */
    DRAFT("草稿"),
    
    /**
     * 已发布状态 - 可供销售
     */
    PUBLISHED("已发布"),
    
    /**
     * 已下架状态 - 不可销售
     */
    UNPUBLISHED("已下架");
    
    private final String description;
    
    /**
     * 检查是否可以转换到目标状态
     */
    public boolean canTransitionTo(SpuStatus target) {
        if (this == target) {
            return false;
        }
        
        return switch (this) {
            case DRAFT -> target == PUBLISHED;
            case PUBLISHED -> target == UNPUBLISHED;
            case UNPUBLISHED -> target == PUBLISHED;
        };
    }
}

package com.freshmall.product.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 销售渠道类型枚举
 */
@Getter
@AllArgsConstructor
public enum ChannelType {
    
    /**
     * APP 渠道
     */
    APP("APP渠道"),
    
    /**
     * WEB 渠道
     */
    WEB("WEB渠道"),
    
    /**
     * 门店渠道
     */
    STORE("门店渠道");
    
    private final String description;
}

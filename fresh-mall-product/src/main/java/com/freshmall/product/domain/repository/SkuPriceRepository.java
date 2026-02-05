package com.freshmall.product.domain.repository;

import com.freshmall.product.domain.model.ChannelType;
import com.freshmall.product.domain.price.SkuPrice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * SKU 价格仓储接口
 * 定义价格聚合根的持久化操作
 */
public interface SkuPriceRepository {

    /**
     * 保存价格记录
     * 
     * @param skuPrice 价格实体
     * @return 保存后的价格（包含生成的 ID）
     */
    SkuPrice save(SkuPrice skuPrice);

    /**
     * 根据 ID 查询价格
     * 
     * @param id 价格 ID
     * @return 价格实体（如果存在）
     */
    Optional<SkuPrice> findById(Long id);

    /**
     * 查询 SKU 在指定渠道的所有价格记录
     * 
     * @param skuId SKU ID
     * @param channel 销售渠道
     * @return 价格列表
     */
    List<SkuPrice> findBySkuIdAndChannel(Long skuId, ChannelType channel);

    /**
     * 查询 SKU 在指定渠道和时间的有效价格
     * 按 start_time 降序排序（最新的在前）
     * 
     * @param skuId SKU ID
     * @param channel 销售渠道
     * @param now 当前时间
     * @return 有效价格列表
     */
    List<SkuPrice> findActiveBySkuIdAndChannel(Long skuId, ChannelType channel, LocalDateTime now);

    /**
     * 查询 SKU 的所有价格记录
     * 
     * @param skuId SKU ID
     * @return 价格列表
     */
    List<SkuPrice> findBySkuId(Long skuId);

    /**
     * 批量查询多个 SKU 在指定渠道和时间的有效价格
     * 
     * @param skuIds SKU ID 列表
     * @param channel 销售渠道
     * @param now 当前时间
     * @return 有效价格列表
     */
    List<SkuPrice> findActivePricesBySkuIds(List<Long> skuIds, ChannelType channel, LocalDateTime now);

    /**
     * 删除价格记录
     * 
     * @param id 价格 ID
     */
    void deleteById(Long id);

    /**
     * 根据 SKU ID 删除所有价格记录
     * 
     * @param skuId SKU ID
     */
    void deleteBySkuId(Long skuId);
}

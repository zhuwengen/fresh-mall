package com.freshmall.product.infrastructure.persistent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freshmall.product.domain.stock.SkuStock;
import org.apache.ibatis.annotations.Mapper;

/**
 * SKU 库存 Mapper
 * 使用 MyBatis Plus 提供的基础 CRUD 操作
 * 乐观锁通过 @Version 注解自动处理
 */
@Mapper
public interface SkuStockMapper extends BaseMapper<SkuStock> {
}

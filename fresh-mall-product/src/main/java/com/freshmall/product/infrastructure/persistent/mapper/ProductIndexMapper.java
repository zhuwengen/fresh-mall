package com.freshmall.product.infrastructure.persistent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freshmall.product.domain.model.ProductIndex;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品索引 Mapper
 * 使用 MyBatis Plus 提供的基础 CRUD 操作
 */
@Mapper
public interface ProductIndexMapper extends BaseMapper<ProductIndex> {
}

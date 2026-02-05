package com.freshmall.product.infrastructure.persistent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freshmall.product.domain.aggregate.CategoryAttribute;
import org.apache.ibatis.annotations.Mapper;

/**
 * CategoryAttribute Mapper
 * 使用 MyBatis Plus 提供的基础 CRUD 操作
 */
@Mapper
public interface CategoryAttributeMapper extends BaseMapper<CategoryAttribute> {
}

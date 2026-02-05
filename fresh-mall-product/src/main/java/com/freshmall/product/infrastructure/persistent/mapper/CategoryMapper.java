package com.freshmall.product.infrastructure.persistent.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.freshmall.product.domain.aggregate.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
 * Category Mapper
 * 使用 MyBatis Plus 提供的基础 CRUD 操作
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    /**
     * 检查类目是否有关联的 SPU
     * 
     * @param categoryId 类目 ID
     * @return SPU 数量
     */
    @Select("SELECT COUNT(*) FROM t_spu WHERE category_id = #{categoryId} AND is_deleted = 0")
    int countSpuByCategoryId(Long categoryId);
}

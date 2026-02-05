package com.freshmall.product.domain.aggregate;

import com.baomidou.mybatisplus.annotation.TableName;
import com.freshmall.common.domain.BaseEntity;
import com.freshmall.common.enums.ResultCode;
import com.freshmall.common.exception.BusinessException;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * CategoryAttribute 聚合根
 * 管理类目和属性的关联关系
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_category_attribute")
public class CategoryAttribute extends BaseEntity {

    /**
     * 类目 ID
     */
    private Long categoryId;

    /**
     * 属性 ID
     */
    private Long attributeId;

    /**
     * 是否必需（true: 必需, false: 可选）
     */
    private Boolean required;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 验证类目 ID
     * 
     * @param categoryId 类目 ID
     * @throws BusinessException 如果 ID 为空
     */
    public static void validateCategoryId(Long categoryId) {
        if (categoryId == null) {
            throw new BusinessException("类目 ID 不能为空");
        }
    }

    /**
     * 验证属性 ID
     * 
     * @param attributeId 属性 ID
     * @throws BusinessException 如果 ID 为空
     */
    public static void validateAttributeId(Long attributeId) {
        if (attributeId == null) {
            throw new BusinessException("属性 ID 不能为空");
        }
    }

    /**
     * 验证排序顺序
     * 
     * @param sortOrder 排序顺序
     * @throws BusinessException 如果排序顺序为负数
     */
    public static void validateSortOrder(Integer sortOrder) {
        if (sortOrder != null && sortOrder < 0) {
            throw new BusinessException("排序顺序不能为负数");
        }
    }

    /**
     * 设置默认值
     */
    public void setDefaults() {
        if (this.required == null) {
            this.required = false;
        }
        if (this.sortOrder == null) {
            this.sortOrder = 0;
        }
    }
}

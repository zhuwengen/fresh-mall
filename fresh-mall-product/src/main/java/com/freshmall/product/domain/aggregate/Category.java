package com.freshmall.product.domain.aggregate;

import com.baomidou.mybatisplus.annotation.TableName;
import com.freshmall.common.domain.BaseEntity;
import com.freshmall.common.enums.ResultCode;
import com.freshmall.common.exception.BusinessException;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Category 聚合根
 * 管理类目层级结构和类目信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_category")
public class Category extends BaseEntity {

    /**
     * 类目编码（唯一）
     */
    private String categoryCode;

    /**
     * 类目名称
     */
    private String name;

    /**
     * 父类目 ID（根类目为 null）
     */
    private Long parentId;

    /**
     * 类目层级（根类目 level = 1）
     */
    private Integer level;

    /**
     * 排序顺序
     */
    private Integer sortOrder;

    /**
     * 计算类目层级
     * 根据父类目的层级计算当前类目的层级
     * 
     * @param parentLevel 父类目的层级（如果是根类目则为 null）
     * @return 计算后的层级
     */
    public static Integer calculateLevel(Integer parentLevel) {
        if (parentLevel == null) {
            return 1; // 根类目
        }
        return parentLevel + 1;
    }

    /**
     * 验证类目编码
     * 
     * @param categoryCode 类目编码
     * @throws BusinessException 如果编码为空
     */
    public static void validateCategoryCode(String categoryCode) {
        if (categoryCode == null || categoryCode.trim().isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
    }

    /**
     * 验证类目名称
     * 
     * @param name 类目名称
     * @throws BusinessException 如果名称为空
     */
    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
    }

    /**
     * 判断是否为根类目
     * 
     * @return 是否为根类目
     */
    public boolean isRoot() {
        return this.parentId == null;
    }

    /**
     * 设置层级（基于父类目层级）
     * 
     * @param parentLevel 父类目层级（如果是根类目则为 null）
     */
    public void setLevelFromParent(Integer parentLevel) {
        this.level = calculateLevel(parentLevel);
    }
}

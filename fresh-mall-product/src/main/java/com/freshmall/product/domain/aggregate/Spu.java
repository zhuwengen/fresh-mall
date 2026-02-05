package com.freshmall.product.domain.aggregate;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.freshmall.common.domain.BaseEntity;
import com.freshmall.common.enums.ResultCode;
import com.freshmall.common.exception.BusinessException;
import com.freshmall.product.domain.model.SpuStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * SPU 聚合根
 * 管理商品标准信息和生命周期状态转换
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_spu")
public class Spu extends BaseEntity {

    /**
     * SPU 名称
     */
    private String name;

    /**
     * 类目 ID
     */
    private Long categoryId;

    /**
     * 商品图片（JSON 数组或逗号分隔）
     */
    private String images;

    /**
     * SPU 状态
     */
    private SpuStatus status;

    /**
     * 发布时间
     */
    private LocalDateTime publishTime;

    /**
     * 逻辑删除时间
     */
    @TableField("deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 发布 SPU（DRAFT -> PUBLISHED）
     * 
     * @param hasEnabledSku 是否有启用的 SKU
     * @throws BusinessException 如果状态转换无效或没有启用的 SKU
     */
    public void publish(boolean hasEnabledSku) {
        if (this.getDeleted() != null && this.getDeleted() == 1) {
            throw new BusinessException(ResultCode.SPU_ALREADY_DELETED);
        }
        
        if (!hasEnabledSku) {
            throw new BusinessException(ResultCode.SPU_NO_ENABLED_SKU);
        }
        
        if (!this.status.canTransitionTo(SpuStatus.PUBLISHED)) {
            throw new BusinessException(ResultCode.SPU_INVALID_STATUS_TRANSITION);
        }
        
        this.status = SpuStatus.PUBLISHED;
        this.publishTime = LocalDateTime.now();
    }

    /**
     * 下架 SPU（PUBLISHED -> UNPUBLISHED）
     * 
     * @throws BusinessException 如果状态转换无效
     */
    public void unpublish() {
        if (this.getDeleted() != null && this.getDeleted() == 1) {
            throw new BusinessException(ResultCode.SPU_ALREADY_DELETED);
        }
        
        if (!this.status.canTransitionTo(SpuStatus.UNPUBLISHED)) {
            throw new BusinessException(ResultCode.SPU_INVALID_STATUS_TRANSITION);
        }
        
        this.status = SpuStatus.UNPUBLISHED;
    }

    /**
     * 重新发布 SPU（UNPUBLISHED -> PUBLISHED）
     * 
     * @param hasEnabledSku 是否有启用的 SKU
     * @throws BusinessException 如果状态转换无效或没有启用的 SKU
     */
    public void republish(boolean hasEnabledSku) {
        if (this.getDeleted() != null && this.getDeleted() == 1) {
            throw new BusinessException(ResultCode.SPU_ALREADY_DELETED);
        }
        
        if (!hasEnabledSku) {
            throw new BusinessException(ResultCode.SPU_NO_ENABLED_SKU);
        }
        
        if (!this.status.canTransitionTo(SpuStatus.PUBLISHED)) {
            throw new BusinessException(ResultCode.SPU_INVALID_STATUS_TRANSITION);
        }
        
        this.status = SpuStatus.PUBLISHED;
        this.publishTime = LocalDateTime.now();
    }

    /**
     * 验证 SPU 名称
     * 
     * @param name SPU 名称
     * @throws BusinessException 如果名称为空或超过 200 个字符
     */
    public static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BusinessException(ResultCode.SPU_NAME_INVALID);
        }
        if (name.length() > 200) {
            throw new BusinessException(ResultCode.SPU_NAME_INVALID);
        }
    }

    /**
     * 标记为逻辑删除
     */
    public void markAsDeleted() {
        this.setDeleted(1);
        this.deletedAt = LocalDateTime.now();
    }
}

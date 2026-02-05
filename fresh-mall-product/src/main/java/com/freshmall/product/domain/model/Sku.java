package com.freshmall.product.domain.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.freshmall.common.domain.BaseEntity;
import com.freshmall.common.enums.ResultCode;
import com.freshmall.common.exception.BusinessException;
import com.freshmall.product.domain.valueobject.Specifications;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * SKU 聚合根
 * 管理商品规格和启用状态
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_sku")
public class Sku extends BaseEntity {

    /**
     * SPU ID
     */
    private Long spuId;

    /**
     * SKU 编码（唯一）
     */
    private String skuCode;

    /**
     * 规格（JSON 格式）
     */
    private String specifications;

    /**
     * 是否启用
     */
    private Boolean enabled;

    /**
     * 逻辑删除时间
     */
    @TableField("deleted_at")
    private LocalDateTime deletedAt;

    /**
     * 获取规格值对象
     * 
     * @return 规格值对象
     */
    public Specifications getSpecificationsVO() {
        if (this.specifications == null || this.specifications.trim().isEmpty()) {
            throw new BusinessException(ResultCode.SKU_SPECIFICATIONS_EMPTY);
        }
        return Specifications.fromJson(this.specifications);
    }

    /**
     * 设置规格值对象
     * 
     * @param specs 规格值对象
     */
    public void setSpecificationsVO(Specifications specs) {
        if (specs == null) {
            throw new BusinessException(ResultCode.SKU_SPECIFICATIONS_EMPTY);
        }
        this.specifications = specs.toJson();
    }

    /**
     * 验证 SKU 编码
     * 
     * @param skuCode SKU 编码
     * @throws BusinessException 如果编码为空
     */
    public static void validateSkuCode(String skuCode) {
        if (skuCode == null || skuCode.trim().isEmpty()) {
            throw new BusinessException("SKU 编码不能为空");
        }
    }

    /**
     * 验证规格
     * 
     * @param specifications 规格 JSON
     * @throws BusinessException 如果规格为空或格式不合法
     */
    public static void validateSpecifications(String specifications) {
        if (specifications == null || specifications.trim().isEmpty()) {
            throw new BusinessException(ResultCode.SKU_SPECIFICATIONS_EMPTY);
        }
        
        // 验证 JSON 格式并确保至少有一个键值对
        try {
            Specifications specs = Specifications.fromJson(specifications);
            if (specs.size() == 0) {
                throw new BusinessException(ResultCode.SKU_SPECIFICATIONS_EMPTY);
            }
        } catch (IllegalArgumentException e) {
            throw new BusinessException(ResultCode.SKU_INVALID_SPECIFICATIONS, e.getMessage());
        }
    }

    /**
     * 启用 SKU
     */
    public void enable() {
        if (this.getDeleted() != null && this.getDeleted() == 1) {
            throw new BusinessException("已删除的 SKU 不能启用");
        }
        this.enabled = true;
    }

    /**
     * 禁用 SKU
     */
    public void disable() {
        if (this.getDeleted() != null && this.getDeleted() == 1) {
            throw new BusinessException("已删除的 SKU 不能禁用");
        }
        this.enabled = false;
    }

    /**
     * 检查是否启用
     * 
     * @return 是否启用
     */
    public boolean isEnabled() {
        return Boolean.TRUE.equals(this.enabled);
    }

    /**
     * 标记为逻辑删除
     */
    public void markAsDeleted() {
        this.setDeleted(1);
        this.deletedAt = LocalDateTime.now();
    }
}

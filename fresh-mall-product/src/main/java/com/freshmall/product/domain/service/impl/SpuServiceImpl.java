package com.freshmall.product.domain.service.impl;

import com.freshmall.common.enums.ResultCode;
import com.freshmall.common.exception.BusinessException;
import com.freshmall.product.domain.aggregate.Category;
import com.freshmall.product.domain.aggregate.Spu;
import com.freshmall.product.domain.model.SpuStatus;
import com.freshmall.product.domain.repository.CategoryRepository;
import com.freshmall.product.domain.repository.ProductIndexRepository;
import com.freshmall.product.domain.repository.SkuRepository;
import com.freshmall.product.domain.repository.SpuRepository;
import com.freshmall.product.domain.service.SpuService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * SPU 领域服务实现
 * 管理 SPU 的创建、更新、查询和生命周期
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpuServiceImpl implements SpuService {

    private final SpuRepository spuRepository;
    private final SkuRepository skuRepository;
    private final CategoryRepository categoryRepository;
    private final ProductIndexRepository productIndexRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSpu(String name, Long categoryId, String images) {
        // 1. 验证 SPU 名称
        Spu.validateName(name);

        // 2. 验证类目引用（必须是叶子类目）
        validateCategoryReference(categoryId);

        // 3. 创建 SPU 实体
        Spu spu = new Spu();
        spu.setName(name);
        spu.setCategoryId(categoryId);
        spu.setImages(images);
        spu.setStatus(SpuStatus.DRAFT);
        spu.setDeleted(0);

        // 4. 保存 SPU
        Spu savedSpu = spuRepository.save(spu);
        
        log.info("创建 SPU 成功, ID: {}, 名称: {}, 类目: {}", savedSpu.getId(), name, categoryId);
        
        return savedSpu.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSpu(Long spuId, String name, Long categoryId, String images) {
        // 1. 查询 SPU
        Spu spu = spuRepository.findById(spuId)
                .orElseThrow(() -> new BusinessException(ResultCode.SPU_NOT_FOUND));

        // 2. 检查是否已删除
        if (spu.getDeleted() != null && spu.getDeleted() == 1) {
            throw new BusinessException(ResultCode.SPU_ALREADY_DELETED);
        }

        // 3. 验证 SPU 名称
        if (name != null) {
            Spu.validateName(name);
            spu.setName(name);
        }

        // 4. 验证类目引用（如果更新了类目）
        if (categoryId != null && !categoryId.equals(spu.getCategoryId())) {
            validateCategoryReference(categoryId);
            spu.setCategoryId(categoryId);
        }

        // 5. 更新图片
        if (images != null) {
            spu.setImages(images);
        }

        // 6. 保存更新
        spuRepository.update(spu);
        
        log.info("更新 SPU 成功, ID: {}", spuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishSpu(Long spuId) {
        // 1. 查询 SPU
        Spu spu = spuRepository.findById(spuId)
                .orElseThrow(() -> new BusinessException(ResultCode.SPU_NOT_FOUND));

        // 2. 检查是否有启用的 SKU
        boolean hasEnabledSku = skuRepository.hasEnabledSku(spuId);

        // 3. 执行发布（状态转换在 Spu 实体中处理）
        spu.publish(hasEnabledSku);

        // 4. 保存状态变更
        spuRepository.update(spu);

        // 5. 更新商品索引
        updateProductIndex(spuId);
        
        log.info("发布 SPU 成功, ID: {}, 发布时间: {}", spuId, spu.getPublishTime());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unpublishSpu(Long spuId) {
        // 1. 查询 SPU
        Spu spu = spuRepository.findById(spuId)
                .orElseThrow(() -> new BusinessException(ResultCode.SPU_NOT_FOUND));

        // 2. 执行下架（状态转换在 Spu 实体中处理）
        spu.unpublish();

        // 3. 保存状态变更
        spuRepository.update(spu);

        // 4. 删除商品索引
        productIndexRepository.deleteBySpuId(spuId);
        
        log.info("下架 SPU 成功, ID: {}", spuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void republishSpu(Long spuId) {
        // 1. 查询 SPU
        Spu spu = spuRepository.findById(spuId)
                .orElseThrow(() -> new BusinessException(ResultCode.SPU_NOT_FOUND));

        // 2. 检查是否有启用的 SKU
        boolean hasEnabledSku = skuRepository.hasEnabledSku(spuId);

        // 3. 执行重新发布（状态转换在 Spu 实体中处理）
        spu.republish(hasEnabledSku);

        // 4. 保存状态变更
        spuRepository.update(spu);

        // 5. 更新商品索引
        updateProductIndex(spuId);
        
        log.info("重新发布 SPU 成功, ID: {}, 发布时间: {}", spuId, spu.getPublishTime());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteSpu(Long spuId) {
        // 1. 查询 SPU
        Spu spu = spuRepository.findById(spuId)
                .orElseThrow(() -> new BusinessException(ResultCode.SPU_NOT_FOUND));

        // 2. 标记 SPU 为逻辑删除
        spu.markAsDeleted();
        spuRepository.update(spu);

        // 3. 级联逻辑删除所有关联的 SKU
        skuRepository.deleteBySpuId(spuId);

        // 4. 删除商品索引
        productIndexRepository.deleteBySpuId(spuId);
        
        log.info("逻辑删除 SPU 成功, ID: {}, 删除时间: {}", spuId, spu.getDeletedAt());
    }

    @Override
    public Spu getSpuDetail(Long spuId) {
        // 查询 SPU（排除已删除）
        return spuRepository.findById(spuId)
                .orElseThrow(() -> new BusinessException(ResultCode.SPU_NOT_FOUND));
    }

    @Override
    public void validateCategoryReference(Long categoryId) {
        if (categoryId == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "类目 ID 不能为空");
        }

        // 1. 检查类目是否存在
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ResultCode.CATEGORY_NOT_FOUND));

        // 2. 检查是否为叶子类目（不能有子类目）
        boolean hasChildren = categoryRepository.hasChildren(categoryId);
        if (hasChildren) {
            throw new BusinessException(ResultCode.SPU_CATEGORY_NOT_LEAF);
        }
    }

    /**
     * 更新商品索引
     * 当 SPU 发布时，为所有启用的 SKU 创建或更新索引记录
     * 
     * @param spuId SPU ID
     */
    private void updateProductIndex(Long spuId) {
        // 注意：这里只是删除旧索引，实际的索引创建逻辑将在后续的 ProductSearchService 中实现
        // 因为索引创建需要查询价格和库存信息，这些逻辑应该在应用服务层或搜索服务中处理
        productIndexRepository.deleteBySpuId(spuId);
        
        log.info("清理 SPU 商品索引, ID: {}", spuId);
    }
}

package com.freshmall.product.domain.service.impl;

import com.freshmall.common.enums.ResultCode;
import com.freshmall.common.exception.BusinessException;
import com.freshmall.product.domain.aggregate.Spu;
import com.freshmall.product.domain.model.Sku;
import com.freshmall.product.domain.repository.SkuRepository;
import com.freshmall.product.domain.repository.SkuStockRepository;
import com.freshmall.product.domain.repository.SpuRepository;
import com.freshmall.product.domain.service.SkuService;
import com.freshmall.product.domain.service.SpecificationValidator;
import com.freshmall.product.domain.stock.SkuStock;
import com.freshmall.product.domain.valueobject.Specifications;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * SKU 领域服务实现
 * 管理 SKU 的创建、更新、查询和启用/禁用
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SkuServiceImpl implements SkuService {

    private final SkuRepository skuRepository;
    private final SpuRepository spuRepository;
    private final SkuStockRepository skuStockRepository;
    private final SpecificationValidator specificationValidator;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSku(Long spuId, String skuCode, Map<String, Object> specifications) {
        log.info("创建 SKU: spuId={}, skuCode={}", spuId, skuCode);

        // 1. 验证 SKU 编码
        Sku.validateSkuCode(skuCode);

        // 2. 验证 SPU 引用（必须是现有的未删除 SPU）
        Spu spu = spuRepository.findById(spuId)
                .orElseThrow(() -> new BusinessException(ResultCode.SKU_SPU_NOT_FOUND));

        // 3. 验证 SKU 编码唯一性
        if (skuRepository.existsBySkuCode(skuCode)) {
            throw new BusinessException(ResultCode.SKU_CODE_DUPLICATE);
        }

        // 4. 验证规格
        if (specifications == null || specifications.isEmpty()) {
            throw new BusinessException(ResultCode.SKU_SPECIFICATIONS_EMPTY);
        }

        // 5. 验证规格是否符合类目属性定义
        specificationValidator.validate(spu.getCategoryId(), specifications);

        // 6. 创建 SKU 实体
        Sku sku = new Sku();
        sku.setSpuId(spuId);
        sku.setSkuCode(skuCode);
        sku.setEnabled(true);  // 初始状态为启用
        sku.setDeleted(0);

        // 设置规格
        Specifications specs = Specifications.of(specifications);
        sku.setSpecificationsVO(specs);

        // 7. 保存 SKU
        Sku savedSku = skuRepository.save(sku);

        // 8. 初始化库存记录（数量为 0）
        SkuStock stock = new SkuStock();
        stock.setSkuId(savedSku.getId());
        stock.setTotalStock(0);
        stock.setAvailableStock(0);
        stock.setLockStock(0);
        stock.setVersion(0);
        skuStockRepository.save(stock);

        log.info("SKU 创建成功: skuId={}", savedSku.getId());
        return savedSku.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSkuSpecifications(Long skuId, Map<String, Object> specifications) {
        log.info("更新 SKU 规格: skuId={}", skuId);

        // 1. 查询 SKU
        Sku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new BusinessException(ResultCode.SKU_NOT_FOUND));

        // 2. 验证规格
        if (specifications == null || specifications.isEmpty()) {
            throw new BusinessException(ResultCode.SKU_SPECIFICATIONS_EMPTY);
        }

        // 3. 查询 SPU 获取类目 ID
        Spu spu = spuRepository.findById(sku.getSpuId())
                .orElseThrow(() -> new BusinessException(ResultCode.SKU_SPU_NOT_FOUND));

        // 4. 验证规格是否符合类目属性定义
        specificationValidator.validate(spu.getCategoryId(), specifications);

        // 5. 更新规格
        Specifications specs = Specifications.of(specifications);
        sku.setSpecificationsVO(specs);

        // 6. 保存更新
        skuRepository.update(sku);

        log.info("SKU 规格更新成功: skuId={}", skuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void enableSku(Long skuId) {
        log.info("启用 SKU: skuId={}", skuId);

        // 1. 查询 SKU
        Sku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new BusinessException(ResultCode.SKU_NOT_FOUND));

        // 2. 启用 SKU
        sku.enable();

        // 3. 保存更新
        skuRepository.update(sku);

        log.info("SKU 启用成功: skuId={}", skuId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void disableSku(Long skuId) {
        log.info("禁用 SKU: skuId={}", skuId);

        // 1. 查询 SKU
        Sku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new BusinessException(ResultCode.SKU_NOT_FOUND));

        // 2. 禁用 SKU
        sku.disable();

        // 3. 保存更新
        skuRepository.update(sku);

        log.info("SKU 禁用成功: skuId={}", skuId);
    }

    @Override
    public Sku getSkuDetail(Long skuId) {
        log.info("查询 SKU 详情: skuId={}", skuId);

        return skuRepository.findById(skuId)
                .orElseThrow(() -> new BusinessException(ResultCode.SKU_NOT_FOUND));
    }

    @Override
    public List<Sku> listSkusBySpu(Long spuId) {
        log.info("查询 SPU 的所有 SKU: spuId={}", spuId);

        // 验证 SPU 存在
        if (!spuRepository.existsById(spuId)) {
            throw new BusinessException(ResultCode.SKU_SPU_NOT_FOUND);
        }

        return skuRepository.findBySpuId(spuId);
    }

    @Override
    public List<Sku> listEnabledSkusBySpu(Long spuId) {
        log.info("查询 SPU 的启用 SKU: spuId={}", spuId);

        // 验证 SPU 存在
        if (!spuRepository.existsById(spuId)) {
            throw new BusinessException(ResultCode.SKU_SPU_NOT_FOUND);
        }

        return skuRepository.findEnabledBySpuId(spuId);
    }

    @Override
    public void validateSpecifications(Long categoryId, Map<String, Object> specifications) {
        log.info("验证规格合法性: categoryId={}", categoryId);

        if (specifications == null || specifications.isEmpty()) {
            throw new BusinessException(ResultCode.SKU_SPECIFICATIONS_EMPTY);
        }

        specificationValidator.validate(categoryId, specifications);
    }
}

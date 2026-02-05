package com.freshmall.product.domain.service.impl;

import com.freshmall.common.enums.ResultCode;
import com.freshmall.common.exception.BusinessException;
import com.freshmall.product.domain.aggregate.Spu;
import com.freshmall.product.domain.model.ChannelType;
import com.freshmall.product.domain.model.ProductIndex;
import com.freshmall.product.domain.model.Sku;
import com.freshmall.product.domain.model.StockStatus;
import com.freshmall.product.domain.price.SkuPrice;
import com.freshmall.product.domain.repository.CategoryRepository;
import com.freshmall.product.domain.repository.ProductIndexRepository;
import com.freshmall.product.domain.repository.SkuPriceRepository;
import com.freshmall.product.domain.repository.SkuRepository;
import com.freshmall.product.domain.repository.SkuStockRepository;
import com.freshmall.product.domain.repository.SpuRepository;
import com.freshmall.product.domain.service.ProductSearchService;
import com.freshmall.product.domain.stock.SkuStock;
import com.freshmall.product.domain.valueobject.Money;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品搜索领域服务实现
 * 实现商品索引更新和搜索功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductSearchServiceImpl implements ProductSearchService {

    private final ProductIndexRepository productIndexRepository;
    private final SpuRepository spuRepository;
    private final SkuRepository skuRepository;
    private final SkuPriceRepository skuPriceRepository;
    private final SkuStockRepository skuStockRepository;
    private final CategoryRepository categoryRepository;

    @Override
    public List<ProductIndex> searchProducts(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice,
                                            StockStatus stockStatus, Integer offset, Integer limit) {
        log.info("搜索商品: categoryId={}, minPrice={}, maxPrice={}, stockStatus={}, offset={}, limit={}",
                categoryId, minPrice, maxPrice, stockStatus, offset, limit);

        // 如果指定了类目，获取类目及其所有后代类目
        Long searchCategoryId = null;
        if (categoryId != null) {
            // 验证类目是否存在
            categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException(ResultCode.CATEGORY_NOT_FOUND));
            
            // 注意：这里简化处理，直接使用 categoryId
            // 实际的后代查询逻辑在 ProductIndexRepository 的 search 方法中实现
            searchCategoryId = categoryId;
        }

        // 调用仓储层搜索
        List<ProductIndex> results = productIndexRepository.search(
                searchCategoryId, minPrice, maxPrice, stockStatus, offset, limit);

        log.info("搜索完成: 返回 {} 条结果", results.size());
        return results;
    }

    @Override
    public Long countSearchResults(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, 
                                   StockStatus stockStatus) {
        log.info("统计搜索结果: categoryId={}, minPrice={}, maxPrice={}, stockStatus={}",
                categoryId, minPrice, maxPrice, stockStatus);

        // 如果指定了类目，验证类目是否存在
        Long searchCategoryId = null;
        if (categoryId != null) {
            categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException(ResultCode.CATEGORY_NOT_FOUND));
            searchCategoryId = categoryId;
        }

        // 调用仓储层统计
        Long count = productIndexRepository.countSearch(
                searchCategoryId, minPrice, maxPrice, stockStatus);

        log.info("统计完成: 共 {} 条结果", count);
        return count;
    }

    @Override
    @Transactional
    public void updateIndex(Long spuId) {
        log.info("更新商品索引: spuId={}", spuId);

        // 查询 SPU
        Spu spu = spuRepository.findById(spuId)
                .orElseThrow(() -> new BusinessException(ResultCode.SPU_NOT_FOUND));

        // 查询 SPU 下所有启用的 SKU
        List<Sku> enabledSkus = skuRepository.findEnabledBySpuId(spuId);

        if (enabledSkus.isEmpty()) {
            log.warn("SPU 没有启用的 SKU，跳过索引更新: spuId={}", spuId);
            return;
        }

        // 为每个启用的 SKU 创建或更新索引
        List<ProductIndex> indexes = new ArrayList<>();
        for (Sku sku : enabledSkus) {
            ProductIndex index = buildProductIndex(spu, sku);
            indexes.add(index);
        }

        // 批量保存或更新索引
        productIndexRepository.batchSaveOrUpdate(indexes);

        log.info("商品索引更新完成: spuId={}, 更新了 {} 个 SKU 的索引", spuId, indexes.size());
    }

    @Override
    @Transactional
    public void asyncUpdateIndex(Long skuId) {
        log.info("异步更新商品索引: skuId={}", skuId);

        // 查询 SKU
        Sku sku = skuRepository.findById(skuId)
                .orElseThrow(() -> new BusinessException(ResultCode.SKU_NOT_FOUND));

        // 查询 SPU
        Spu spu = spuRepository.findById(sku.getSpuId())
                .orElseThrow(() -> new BusinessException(ResultCode.SPU_NOT_FOUND));

        // 如果 SKU 被禁用或 SPU 被删除，删除索引
        if (Boolean.FALSE.equals(sku.getEnabled()) || (spu.getDeleted() != null && spu.getDeleted() == 1)) {
            productIndexRepository.deleteBySkuId(skuId);
            log.info("SKU 已禁用或 SPU 已删除，删除索引: skuId={}", skuId);
            return;
        }

        // 构建并更新索引
        ProductIndex index = buildProductIndex(spu, sku);
        
        // 查询是否已存在索引
        Optional<ProductIndex> existingIndex = productIndexRepository.findBySkuId(skuId);
        if (existingIndex.isPresent()) {
            index.setId(existingIndex.get().getId());
            productIndexRepository.update(index);
        } else {
            productIndexRepository.save(index);
        }

        log.info("商品索引异步更新完成: skuId={}", skuId);
    }

    @Override
    @Transactional
    public void deleteIndex(Long spuId) {
        log.info("删除商品索引: spuId={}", spuId);

        productIndexRepository.deleteBySpuId(spuId);

        log.info("商品索引删除完成: spuId={}", spuId);
    }

    /**
     * 构建商品索引
     * 
     * @param spu SPU 实体
     * @param sku SKU 实体
     * @return 商品索引实体
     */
    private ProductIndex buildProductIndex(Spu spu, Sku sku) {
        // 查询价格范围（查询所有渠道的价格）
        List<SkuPrice> allPrices = skuPriceRepository.findBySkuId(sku.getId());
        LocalDateTime now = LocalDateTime.now();
        
        // 过滤有效价格
        List<SkuPrice> activePrices = allPrices.stream()
                .filter(price -> price.isActive(now))
                .collect(Collectors.toList());

        BigDecimal minPrice = null;
        BigDecimal maxPrice = null;
        
        if (!activePrices.isEmpty()) {
            minPrice = activePrices.stream()
                    .map(SkuPrice::getPrice)
                    .min(Comparator.naturalOrder())
                    .orElse(null);
            
            maxPrice = activePrices.stream()
                    .map(SkuPrice::getPrice)
                    .max(Comparator.naturalOrder())
                    .orElse(null);
        }

        // 查询库存状态
        StockStatus stockStatus = calculateStockStatus(sku.getId());

        // 构建索引实体
        ProductIndex index = new ProductIndex();
        index.setSpuId(spu.getId());
        index.setSkuId(sku.getId());
        index.setSpuName(spu.getName());
        index.setCategoryId(spu.getCategoryId());
        index.setMinPrice(minPrice);
        index.setMaxPrice(maxPrice);
        index.setStockStatus(stockStatus);
        index.setSpuDeleted(spu.getDeleted() != null && spu.getDeleted() == 1);
        index.setSkuEnabled(sku.getEnabled());

        return index;
    }

    /**
     * 计算库存状态
     * 
     * @param skuId SKU ID
     * @return 库存状态
     */
    private StockStatus calculateStockStatus(Long skuId) {
        Optional<SkuStock> stockOpt = skuStockRepository.findBySkuId(skuId);
        
        if (!stockOpt.isPresent()) {
            // 没有库存记录，视为无货
            return StockStatus.OUT_OF_STOCK;
        }

        SkuStock stock = stockOpt.get();
        Integer availableStock = stock.getAvailableStock();

        if (availableStock == null || availableStock == 0) {
            return StockStatus.OUT_OF_STOCK;
        } else if (availableStock < 10) {
            // 低库存阈值设为 10
            return StockStatus.LOW_STOCK;
        } else {
            return StockStatus.IN_STOCK;
        }
    }

    /**
     * 获取类目及其所有后代类目的 ID 列表
     * 这是一个辅助方法，用于支持类目过滤时包含子类目
     * 
     * @param categoryId 类目 ID
     * @return 类目 ID 列表（包含自身和所有后代）
     */
    private List<Long> getCategoryAndDescendants(Long categoryId) {
        Set<Long> categoryIds = new HashSet<>();
        categoryIds.add(categoryId);
        
        // 递归查询所有子类目
        collectDescendants(categoryId, categoryIds);
        
        return new ArrayList<>(categoryIds);
    }

    /**
     * 递归收集所有后代类目
     * 
     * @param parentId 父类目 ID
     * @param categoryIds 类目 ID 集合（用于收集结果）
     */
    private void collectDescendants(Long parentId, Set<Long> categoryIds) {
        List<com.freshmall.product.domain.aggregate.Category> children = 
                categoryRepository.findByParentId(parentId);
        
        for (com.freshmall.product.domain.aggregate.Category child : children) {
            categoryIds.add(child.getId());
            // 递归查询子类目的子类目
            collectDescendants(child.getId(), categoryIds);
        }
    }
}

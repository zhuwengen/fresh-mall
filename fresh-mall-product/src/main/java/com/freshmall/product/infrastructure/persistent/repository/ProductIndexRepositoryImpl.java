package com.freshmall.product.infrastructure.persistent.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshmall.product.domain.model.ProductIndex;
import com.freshmall.product.domain.model.StockStatus;
import com.freshmall.product.domain.repository.ProductIndexRepository;
import com.freshmall.product.infrastructure.persistent.mapper.ProductIndexMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 商品索引仓储实现
 * 使用 MyBatis Plus 实现持久化操作
 */
@Repository
@RequiredArgsConstructor
public class ProductIndexRepositoryImpl implements ProductIndexRepository {

    private final ProductIndexMapper productIndexMapper;

    @Override
    public ProductIndex save(ProductIndex productIndex) {
        productIndexMapper.insert(productIndex);
        return productIndex;
    }

    @Override
    public void update(ProductIndex productIndex) {
        productIndexMapper.updateById(productIndex);
    }

    @Override
    public Optional<ProductIndex> findBySkuId(Long skuId) {
        LambdaQueryWrapper<ProductIndex> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductIndex::getSkuId, skuId);
        ProductIndex productIndex = productIndexMapper.selectOne(wrapper);
        return Optional.ofNullable(productIndex);
    }

    @Override
    public List<ProductIndex> findBySpuId(Long spuId) {
        LambdaQueryWrapper<ProductIndex> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductIndex::getSpuId, spuId);
        return productIndexMapper.selectList(wrapper);
    }

    @Override
    public void deleteBySpuId(Long spuId) {
        LambdaQueryWrapper<ProductIndex> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductIndex::getSpuId, spuId);
        productIndexMapper.delete(wrapper);
    }

    @Override
    public void deleteBySkuId(Long skuId) {
        LambdaQueryWrapper<ProductIndex> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProductIndex::getSkuId, skuId);
        productIndexMapper.delete(wrapper);
    }

    @Override
    public List<ProductIndex> search(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice,
                                    StockStatus stockStatus, Integer offset, Integer limit) {
        LambdaQueryWrapper<ProductIndex> wrapper = buildSearchWrapper(categoryId, minPrice, maxPrice, stockStatus);
        
        // 分页
        wrapper.last("LIMIT " + limit + " OFFSET " + offset);
        
        return productIndexMapper.selectList(wrapper);
    }

    @Override
    public Long countSearch(Long categoryId, BigDecimal minPrice, BigDecimal maxPrice, StockStatus stockStatus) {
        LambdaQueryWrapper<ProductIndex> wrapper = buildSearchWrapper(categoryId, minPrice, maxPrice, stockStatus);
        return productIndexMapper.selectCount(wrapper);
    }

    @Override
    public void batchSaveOrUpdate(List<ProductIndex> productIndexes) {
        for (ProductIndex productIndex : productIndexes) {
            // 检查是否已存在
            Optional<ProductIndex> existing = findBySkuId(productIndex.getSkuId());
            if (existing.isPresent()) {
                productIndex.setId(existing.get().getId());
                update(productIndex);
            } else {
                save(productIndex);
            }
        }
    }

    /**
     * 构建搜索查询条件
     */
    private LambdaQueryWrapper<ProductIndex> buildSearchWrapper(Long categoryId, BigDecimal minPrice,
                                                                BigDecimal maxPrice, StockStatus stockStatus) {
        LambdaQueryWrapper<ProductIndex> wrapper = new LambdaQueryWrapper<>();
        
        // 排除已删除的 SPU 和禁用的 SKU
        wrapper.eq(ProductIndex::getSpuDeleted, false);
        wrapper.eq(ProductIndex::getSkuEnabled, true);
        
        // 类目过滤（注意：这里简化实现，实际应该包含子类目）
        if (categoryId != null) {
            wrapper.eq(ProductIndex::getCategoryId, categoryId);
        }
        
        // 价格范围过滤
        if (minPrice != null) {
            wrapper.ge(ProductIndex::getMaxPrice, minPrice);
        }
        if (maxPrice != null) {
            wrapper.le(ProductIndex::getMinPrice, maxPrice);
        }
        
        // 库存状态过滤
        if (stockStatus != null) {
            wrapper.eq(ProductIndex::getStockStatus, stockStatus);
        }
        
        return wrapper;
    }
}

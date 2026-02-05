package com.freshmall.product.application.service;

import com.freshmall.product.domain.model.Sku;
import com.freshmall.product.domain.service.ProductSearchService;
import com.freshmall.product.domain.service.SkuService;
import com.freshmall.product.domain.service.SpuService;
import com.freshmall.product.domain.service.StockService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 商品应用服务
 * 编排层，负责协调多个领域服务完成复杂业务流程
 * 使用事务确保数据一致性
 */
@Service
public class ProductApplicationService {

    private final SpuService spuService;
    private final SkuService skuService;
    private final StockService stockService;
    private final ProductSearchService productSearchService;

    public ProductApplicationService(SpuService spuService,
                                    SkuService skuService,
                                    StockService stockService,
                                    ProductSearchService productSearchService) {
        this.spuService = spuService;
        this.skuService = skuService;
        this.stockService = stockService;
        this.productSearchService = productSearchService;
    }

    /**
     * 创建商品（SPU + SKU + 库存初始化）
     * 编排流程：
     * 1. 创建 SPU（草稿状态）
     * 2. 为 SPU 创建 SKU
     * 3. 为每个 SKU 初始化库存（初始数量为 0）
     * 
     * @param spuName SPU 名称
     * @param categoryId 类目 ID
     * @param images 商品图片
     * @param skuList SKU 列表（包含 skuCode 和 specifications）
     * @return 创建的 SPU ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long createProduct(String spuName, Long categoryId, String images,
                             List<SkuCreateRequest> skuList) {
        // 1. 创建 SPU（草稿状态）
        Long spuId = spuService.createSpu(spuName, categoryId, images);

        // 2. 为 SPU 创建 SKU
        for (SkuCreateRequest skuRequest : skuList) {
            Long skuId = skuService.createSku(spuId, skuRequest.getSkuCode(), 
                                             skuRequest.getSpecifications());
            
            // 3. 为每个 SKU 初始化库存（初始数量为 0）
            stockService.initStock(skuId, 0);
        }

        return spuId;
    }

    /**
     * 发布商品（状态转换 + 索引更新）
     * 编排流程：
     * 1. 发布 SPU（DRAFT -> PUBLISHED 或 UNPUBLISHED -> PUBLISHED）
     * 2. 更新商品索引（为所有启用的 SKU 创建索引记录）
     * 
     * @param spuId SPU ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void publishProduct(Long spuId) {
        // 1. 发布 SPU（状态转换）
        // SpuService 会根据当前状态选择 publishSpu 或 republishSpu
        spuService.publishSpu(spuId);

        // 2. 更新商品索引
        productSearchService.updateIndex(spuId);
    }

    /**
     * 下架商品（状态转换 + 删除索引）
     * 编排流程：
     * 1. 下架 SPU（PUBLISHED -> UNPUBLISHED）
     * 2. 删除商品索引
     * 
     * @param spuId SPU ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void unpublishProduct(Long spuId) {
        // 1. 下架 SPU
        spuService.unpublishSpu(spuId);

        // 2. 删除商品索引
        productSearchService.deleteIndex(spuId);
    }

    /**
     * 删除商品（级联删除 SKU + 删除索引）
     * 编排流程：
     * 1. 逻辑删除 SPU（级联删除所有关联的 SKU）
     * 2. 删除商品索引
     * 
     * @param spuId SPU ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long spuId) {
        // 1. 逻辑删除 SPU（SpuService 内部会级联删除 SKU）
        spuService.deleteSpu(spuId);

        // 2. 删除商品索引
        productSearchService.deleteIndex(spuId);
    }

    /**
     * 更新商品基本信息
     * 
     * @param spuId SPU ID
     * @param spuName SPU 名称
     * @param categoryId 类目 ID
     * @param images 商品图片
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(Long spuId, String spuName, Long categoryId, String images) {
        spuService.updateSpu(spuId, spuName, categoryId, images);
    }

    /**
     * 添加 SKU 到现有商品
     * 编排流程：
     * 1. 创建 SKU
     * 2. 初始化库存
     * 3. 如果 SPU 已发布，更新索引
     * 
     * @param spuId SPU ID
     * @param skuCode SKU 编码
     * @param specifications 规格
     * @return 创建的 SKU ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long addSkuToProduct(Long spuId, String skuCode, Map<String, Object> specifications) {
        // 1. 创建 SKU
        Long skuId = skuService.createSku(spuId, skuCode, specifications);

        // 2. 初始化库存
        stockService.initStock(skuId, 0);

        // 3. 如果 SPU 已发布，异步更新索引
        productSearchService.asyncUpdateIndex(skuId);

        return skuId;
    }

    /**
     * SKU 创建请求
     */
    public static class SkuCreateRequest {
        private String skuCode;
        private Map<String, Object> specifications;

        public SkuCreateRequest() {
        }

        public SkuCreateRequest(String skuCode, Map<String, Object> specifications) {
            this.skuCode = skuCode;
            this.specifications = specifications;
        }

        public String getSkuCode() {
            return skuCode;
        }

        public void setSkuCode(String skuCode) {
            this.skuCode = skuCode;
        }

        public Map<String, Object> getSpecifications() {
            return specifications;
        }

        public void setSpecifications(Map<String, Object> specifications) {
            this.specifications = specifications;
        }
    }
}

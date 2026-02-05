package com.freshmall.product.domain.service.impl;

import com.freshmall.common.enums.ResultCode;
import com.freshmall.common.exception.BusinessException;
import com.freshmall.product.domain.repository.SkuStockRepository;
import com.freshmall.product.domain.service.StockService;
import com.freshmall.product.domain.stock.SkuStock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 库存领域服务实现
 * 处理库存操作，包含乐观锁重试逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final SkuStockRepository skuStockRepository;

    /**
     * 最大重试次数（乐观锁冲突时）
     */
    private static final int MAX_RETRIES = 3;

    /**
     * 重试间隔基数（毫秒）
     */
    private static final long RETRY_INTERVAL_MS = 10;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void initStock(Long skuId, Integer totalStock) {
        if (skuId == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "SKU ID 不能为空");
        }
        if (totalStock == null || totalStock < 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "库存数量不能为空或负数");
        }

        // 检查是否已存在库存记录
        if (skuStockRepository.existsBySkuId(skuId)) {
            throw new BusinessException(ResultCode.PARAM_ERROR, 
                String.format("SKU [%d] 的库存记录已存在", skuId));
        }

        // 初始化库存
        SkuStock stock = SkuStock.initialize(skuId, totalStock);
        skuStockRepository.save(stock);

        log.info("初始化库存成功，SKU ID: {}, 总库存: {}", skuId, totalStock);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductStock(Long skuId, Integer quantity) {
        if (skuId == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "SKU ID 不能为空");
        }
        if (quantity == null || quantity <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "扣减数量必须大于零");
        }

        // 使用乐观锁重试机制
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                // 1. 查询当前库存
                SkuStock stock = skuStockRepository.findBySkuId(skuId)
                    .orElseThrow(() -> new BusinessException(ResultCode.STOCK_NOT_FOUND, 
                        String.format("SKU [%d] 的库存记录不存在", skuId)));

                // 2. 执行扣减操作（会检查库存是否充足）
                stock.deduct(quantity);

                // 3. 乐观锁更新
                boolean success = skuStockRepository.update(stock);
                
                if (success) {
                    log.info("扣减库存成功，SKU ID: {}, 扣减数量: {}, 尝试次数: {}", 
                        skuId, quantity, attempt);
                    return;
                }

                // 版本冲突，准备重试
                if (attempt < MAX_RETRIES) {
                    long sleepTime = RETRY_INTERVAL_MS * attempt;
                    log.warn("库存扣减版本冲突，准备重试，SKU ID: {}, 尝试次数: {}, 等待: {}ms", 
                        skuId, attempt, sleepTime);
                    Thread.sleep(sleepTime);
                }

            } catch (BusinessException e) {
                // 业务异常（如库存不足）直接抛出，不重试
                throw e;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BusinessException(ResultCode.STOCK_OPTIMISTIC_LOCK_FAILED, 
                    "库存扣减被中断");
            } catch (Exception e) {
                log.error("库存扣减异常，SKU ID: {}, 尝试次数: {}", skuId, attempt, e);
                if (attempt == MAX_RETRIES) {
                    throw new BusinessException(ResultCode.FAILURE, "库存扣减失败");
                }
            }
        }

        // 所有重试都失败
        throw new BusinessException(ResultCode.STOCK_OPTIMISTIC_LOCK_FAILED, 
            String.format("库存扣减失败，已重试 %d 次，请稍后重试", MAX_RETRIES));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void confirmDeduction(Long skuId, Integer quantity) {
        if (skuId == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "SKU ID 不能为空");
        }
        if (quantity == null || quantity <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "确认数量必须大于零");
        }

        // 使用乐观锁重试机制
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                // 1. 查询当前库存
                SkuStock stock = skuStockRepository.findBySkuId(skuId)
                    .orElseThrow(() -> new BusinessException(ResultCode.STOCK_NOT_FOUND, 
                        String.format("SKU [%d] 的库存记录不存在", skuId)));

                // 2. 执行确认操作
                stock.confirm(quantity);

                // 3. 乐观锁更新
                boolean success = skuStockRepository.update(stock);
                
                if (success) {
                    log.info("确认扣减成功，SKU ID: {}, 确认数量: {}, 尝试次数: {}", 
                        skuId, quantity, attempt);
                    return;
                }

                // 版本冲突，准备重试
                if (attempt < MAX_RETRIES) {
                    long sleepTime = RETRY_INTERVAL_MS * attempt;
                    log.warn("确认扣减版本冲突，准备重试，SKU ID: {}, 尝试次数: {}, 等待: {}ms", 
                        skuId, attempt, sleepTime);
                    Thread.sleep(sleepTime);
                }

            } catch (BusinessException e) {
                // 业务异常直接抛出，不重试
                throw e;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BusinessException(ResultCode.STOCK_OPTIMISTIC_LOCK_FAILED, 
                    "确认扣减被中断");
            } catch (Exception e) {
                log.error("确认扣减异常，SKU ID: {}, 尝试次数: {}", skuId, attempt, e);
                if (attempt == MAX_RETRIES) {
                    throw new BusinessException(ResultCode.FAILURE, "确认扣减失败");
                }
            }
        }

        // 所有重试都失败
        throw new BusinessException(ResultCode.STOCK_OPTIMISTIC_LOCK_FAILED, 
            String.format("确认扣减失败，已重试 %d 次，请稍后重试", MAX_RETRIES));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void releaseStock(Long skuId, Integer quantity) {
        if (skuId == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "SKU ID 不能为空");
        }
        if (quantity == null || quantity <= 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "释放数量必须大于零");
        }

        // 使用乐观锁重试机制
        for (int attempt = 1; attempt <= MAX_RETRIES; attempt++) {
            try {
                // 1. 查询当前库存
                SkuStock stock = skuStockRepository.findBySkuId(skuId)
                    .orElseThrow(() -> new BusinessException(ResultCode.STOCK_NOT_FOUND, 
                        String.format("SKU [%d] 的库存记录不存在", skuId)));

                // 2. 执行释放操作
                stock.release(quantity);

                // 3. 乐观锁更新
                boolean success = skuStockRepository.update(stock);
                
                if (success) {
                    log.info("释放库存成功，SKU ID: {}, 释放数量: {}, 尝试次数: {}", 
                        skuId, quantity, attempt);
                    return;
                }

                // 版本冲突，准备重试
                if (attempt < MAX_RETRIES) {
                    long sleepTime = RETRY_INTERVAL_MS * attempt;
                    log.warn("释放库存版本冲突，准备重试，SKU ID: {}, 尝试次数: {}, 等待: {}ms", 
                        skuId, attempt, sleepTime);
                    Thread.sleep(sleepTime);
                }

            } catch (BusinessException e) {
                // 业务异常直接抛出，不重试
                throw e;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new BusinessException(ResultCode.STOCK_OPTIMISTIC_LOCK_FAILED, 
                    "释放库存被中断");
            } catch (Exception e) {
                log.error("释放库存异常，SKU ID: {}, 尝试次数: {}", skuId, attempt, e);
                if (attempt == MAX_RETRIES) {
                    throw new BusinessException(ResultCode.FAILURE, "释放库存失败");
                }
            }
        }

        // 所有重试都失败
        throw new BusinessException(ResultCode.STOCK_OPTIMISTIC_LOCK_FAILED, 
            String.format("释放库存失败，已重试 %d 次，请稍后重试", MAX_RETRIES));
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getAvailableStock(Long skuId) {
        if (skuId == null) {
            throw new BusinessException(ResultCode.PARAM_ERROR, "SKU ID 不能为空");
        }

        SkuStock stock = skuStockRepository.findBySkuId(skuId)
            .orElseThrow(() -> new BusinessException(ResultCode.STOCK_NOT_FOUND, 
                String.format("SKU [%d] 的库存记录不存在", skuId)));

        return stock.getAvailableStock();
    }
}

package com.freshmall.product.infrastructure.persistent.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.freshmall.product.domain.aggregate.Spu;
import com.freshmall.product.domain.repository.SpuRepository;
import com.freshmall.product.infrastructure.persistent.mapper.SpuMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * SPU 仓储实现
 * 使用 MyBatis Plus 实现持久化操作
 */
@Repository
@RequiredArgsConstructor
public class SpuRepositoryImpl implements SpuRepository {

    private final SpuMapper spuMapper;

    @Override
    public Spu save(Spu spu) {
        spuMapper.insert(spu);
        return spu;
    }

    @Override
    public void update(Spu spu) {
        spuMapper.updateById(spu);
    }

    @Override
    public Optional<Spu> findById(Long id) {
        // MyBatis Plus 的 selectById 会自动排除逻辑删除的记录
        Spu spu = spuMapper.selectById(id);
        return Optional.ofNullable(spu);
    }

    @Override
    public Optional<Spu> findByIdIncludingDeleted(Long id) {
        // 查询包含已删除的记录
        LambdaQueryWrapper<Spu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Spu::getId, id);
        // 禁用逻辑删除过滤
        wrapper.apply("1=1");
        Spu spu = spuMapper.selectOne(wrapper);
        return Optional.ofNullable(spu);
    }

    @Override
    public boolean existsById(Long id) {
        return spuMapper.selectById(id) != null;
    }

    @Override
    public void deleteById(Long id) {
        // MyBatis Plus 的 deleteById 会执行逻辑删除
        spuMapper.deleteById(id);
    }
}

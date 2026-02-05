package com.freshmall.product.controller.admin;

import com.freshmall.common.result.Result;
import com.freshmall.product.controller.dto.CategoryCreateRequest;
import com.freshmall.product.domain.aggregate.Category;
import com.freshmall.product.domain.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 类目管理控制器
 */
@Tag(name = "类目管理", description = "商品类目管理接口，包括创建类目、查询类目树和类目路径等操作")
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(summary = "创建类目", description = "创建新的商品类目，支持层级结构")
    @PostMapping
    public Result<Long> createCategory(@Valid @RequestBody CategoryCreateRequest request) {
        Long categoryId = categoryService.createCategory(
            request.getCategoryCode(),
            request.getName(),
            request.getParentId(),
            request.getSortOrder()
        );
        return Result.success(categoryId);
    }

    @Operation(summary = "查询类目树", description = "查询完整的类目树结构")
    @GetMapping("/tree")
    public Result<List<Category>> getCategoryTree() {
        List<Category> tree = categoryService.getCategoryTree();
        return Result.success(tree);
    }

    @Operation(summary = "查询类目路径", description = "查询从根类目到指定类目的完整路径")
    @GetMapping("/{id}/path")
    public Result<List<Category>> getCategoryPath(
            @Parameter(description = "类目 ID", required = true) @PathVariable Long id) {
        List<Category> path = categoryService.getCategoryPath(id);
        return Result.success(path);
    }
}

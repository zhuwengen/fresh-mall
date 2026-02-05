package com.freshmall.product.controller.admin;

import com.freshmall.common.result.Result;
import com.freshmall.product.controller.dto.AttributeCreateRequest;
import com.freshmall.product.controller.dto.CategoryAttributeRequest;
import com.freshmall.product.domain.aggregate.Attribute;
import com.freshmall.product.domain.service.AttributeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 属性管理控制器
 */
@Tag(name = "属性管理", description = "商品属性管理接口，包括创建属性、关联属性到类目和查询类目属性等操作")
@RestController
@RequestMapping("/api/attribute")
@RequiredArgsConstructor
public class AttributeController {

    private final AttributeService attributeService;

    @Operation(summary = "创建属性", description = "创建新的商品属性定义，支持文本、数字和枚举类型")
    @PostMapping
    public Result<Long> createAttribute(@Valid @RequestBody AttributeCreateRequest request) {
        Long attributeId = attributeService.createAttribute(
            request.getName(),
            request.getAttributeType().name(),
            request.getValueRange()
        );
        return Result.success(attributeId);
    }

    @Operation(summary = "关联属性到类目", description = "将属性关联到指定类目，设置是否必需和排序")
    @PostMapping("/category/{categoryId}/attribute")
    public Result<Void> associateAttribute(
            @Parameter(description = "类目 ID", required = true) @PathVariable Long categoryId,
            @Valid @RequestBody CategoryAttributeRequest request) {
        attributeService.associateAttributeToCategory(
            categoryId,
            request.getAttributeId(),
            request.getRequired(),
            request.getSortOrder()
        );
        return Result.success();
    }

    @Operation(summary = "查询类目属性", description = "查询指定类目关联的所有属性，按排序返回")
    @GetMapping("/category/{categoryId}/attributes")
    public Result<List<Attribute>> getCategoryAttributes(
            @Parameter(description = "类目 ID", required = true) @PathVariable Long categoryId) {
        List<Attribute> attributes = attributeService.getCategoryAttributes(categoryId);
        return Result.success(attributes);
    }
}

package uz.codebyz.course.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import uz.codebyz.course.dto.ApiResponse;
import uz.codebyz.course.dto.category.CategoryResponse;
import uz.codebyz.course.service.CategoryService;

import java.util.List;
import java.util.UUID;

// Kategoriya GET endpointlari (hamma uchun ochiq)
@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Kategoriya ro'yxatini ko'rish")
public class CategoryQueryController {

    private final CategoryService categoryService;

    public CategoryQueryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    @Operation(summary = "Kategoriya ro'yxati")
    public ApiResponse<List<CategoryResponse>> list() {
        return categoryService.getAll();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Bitta kategoriya")
    public ApiResponse<CategoryResponse> get(@PathVariable UUID id) {
        return categoryService.getOne(id);
    }

    @GetMapping("/search")
    @Operation(summary = "Slug yoki nom bo'yicha qidirish")
    public ApiResponse<List<CategoryResponse>> search(@RequestParam(name = "q", required = false) String q) {
        return categoryService.search(q);
    }

    @GetMapping("/order/{orderNumber}")
    @Operation(summary = "Tartib raqami bo'yicha topish")
    public ApiResponse<List<CategoryResponse>> findByOrder(@PathVariable Integer orderNumber) {
        return categoryService.findByOrder(orderNumber);
    }
}

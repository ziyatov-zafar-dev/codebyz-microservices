package uz.codebyz.course.controller.admin;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import uz.codebyz.course.dto.ApiResponse;
import uz.codebyz.course.dto.category.CategoryRequest;
import uz.codebyz.course.dto.category.CategoryResponse;
import uz.codebyz.course.service.CategoryService;

import java.util.List;
import java.util.UUID;

// Admin uchun kategoriya CRUD endpointlari
@RestController
@RequestMapping("/api/admin/categories")
@Tag(name = "Admin: Categories", description = "Kategoriya CRUD (faqat ADMIN)")
public class CategoryAdminController {

    private final CategoryService categoryService;

    public CategoryAdminController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @PostMapping
    @Operation(summary = "Kategoriya yaratish (ADMIN)")
    public ApiResponse<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        return categoryService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Kategoriya yangilash (ADMIN)")
    public ApiResponse<CategoryResponse> update(@PathVariable UUID id, @Valid @RequestBody CategoryRequest request) {
        return categoryService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Kategoriya o'chirish (ADMIN)")
    public ApiResponse<Void> delete(@PathVariable UUID id) {
        return categoryService.delete(id);
    }

    @GetMapping("/deleted")
    @Operation(summary = "Soft delete qilingan kategoriyalar (ADMIN)")
    public ApiResponse<List<CategoryResponse>> softDeleted() {
        return categoryService.getSoftDeleted();
    }

    @GetMapping("/deleted/{id}")
    @Operation(summary = "Soft delete kategoriya (ADMIN)")
    public ApiResponse<CategoryResponse> softById(@PathVariable UUID id) {
        return categoryService.getSoftById(id);
    }

    @PostMapping("/{id}/restore")
    @Operation(summary = "Soft delete'dan tiklash (ADMIN)")
    public ApiResponse<Void> restore(@PathVariable UUID id) {
        return categoryService.restore(id);
    }

    @DeleteMapping("/deleted/{id}/hard")
    @Operation(summary = "Soft delete yozuvni HARD_DELETE qilish (ADMIN)")
    public ApiResponse<Void> hardDelete(@PathVariable UUID id) {
        return categoryService.hardDelete(id);
    }
}

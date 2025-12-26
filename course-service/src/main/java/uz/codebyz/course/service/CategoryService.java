package uz.codebyz.course.service;

import uz.codebyz.course.dto.ApiResponse;
import uz.codebyz.course.dto.category.CategoryRequest;
import uz.codebyz.course.dto.category.CategoryResponse;

import java.util.List;
import java.util.UUID;

// Kategoriya xizmatlari interfeysi
public interface CategoryService {
    ApiResponse<List<CategoryResponse>> getAll();

    ApiResponse<CategoryResponse> getOne(UUID id);

    ApiResponse<CategoryResponse> create(CategoryRequest request);

    ApiResponse<CategoryResponse> update(UUID id, CategoryRequest request);

    ApiResponse<Void> delete(UUID id);

    ApiResponse<List<CategoryResponse>> search(String query);

    ApiResponse<List<CategoryResponse>> findByOrder(Integer orderNumber);

    ApiResponse<List<CategoryResponse>> getSoftDeleted();

    ApiResponse<CategoryResponse> getSoftById(UUID id);

    ApiResponse<Void> restore(UUID id);

    ApiResponse<Void> hardDelete(UUID id);
}

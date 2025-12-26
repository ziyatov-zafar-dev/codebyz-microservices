package uz.codebyz.course.service.mapper;

import org.springframework.stereotype.Component;
import uz.codebyz.course.dto.category.CategoryRequest;
import uz.codebyz.course.dto.category.CategoryResponse;
import uz.codebyz.course.entity.Category;

// Category entitini DTO bilan almashish uchun mapper
@Component
public class CategoryMapper {

    // Request -> Entity (yangi)
    public Category toEntity(CategoryRequest request) {
        Category category = new Category();
        apply(request, category);
        return category;
    }

    // Entity -> Response
    public CategoryResponse toResponse(Category entity) {
        CategoryResponse resp = new CategoryResponse();
        resp.setId(entity.getId());
        resp.setName(entity.getName());
        resp.setSlug(entity.getSlug());
        resp.setOrderNumber(entity.getOrderNumber());
        resp.setDescription(entity.getDescription());
        resp.setCreatedAt(entity.getCreatedAt());
        return resp;
    }

    // Request -> Entity (yangilash)
    public void apply(CategoryRequest request, Category entity) {
        entity.setName(request.getName());
        entity.setSlug(request.getSlug());
        entity.setOrderNumber(request.getOrderNumber());
        entity.setDescription(request.getDescription());
    }
}

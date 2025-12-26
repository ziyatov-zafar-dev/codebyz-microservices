package uz.codebyz.course.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import uz.codebyz.course.dto.category.CategoryRequest;
import uz.codebyz.course.dto.category.CategoryResponse;
import uz.codebyz.course.dto.ApiResponse;
import uz.codebyz.course.dto.ErrorCode;
import uz.codebyz.course.entity.Category;
import uz.codebyz.course.domain.RecordStatus;
import uz.codebyz.course.repo.CategoryRepository;
import uz.codebyz.course.service.CategoryService;
import uz.codebyz.course.service.mapper.CategoryMapper;

import java.util.List;
import java.util.UUID;

// Kategoriya CRUD implementatsiyasi
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository, CategoryMapper categoryMapper) {
        this.categoryRepository = categoryRepository;
        this.categoryMapper = categoryMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<CategoryResponse>> getAll() {
        List<CategoryResponse> data = categoryRepository.findAllActive().stream()
                .map(categoryMapper::toResponse)
                .toList();
        return ApiResponse.ok("Kategoriyalar ro'yxati", data);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<CategoryResponse> getOne(UUID id) {
        Category category = categoryRepository.findActiveById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Kategoriya topilmadi"));
        return ApiResponse.ok("Kategoriya topildi", categoryMapper.toResponse(category));
    }

    @Override
    @Transactional
    public ApiResponse<CategoryResponse> create(CategoryRequest request) {
        String normalizedSlug = normalizeSlug(request.getSlug(), request.getName());
        ensureSlugUnique(normalizedSlug, null);
        Category category = categoryMapper.toEntity(request);
        category.setSlug(normalizedSlug);
        Category saved = categoryRepository.save(category);
        return ApiResponse.created("Kategoriya yaratildi", categoryMapper.toResponse(saved));
    }

    @Override
    @Transactional
    public ApiResponse<CategoryResponse> update(UUID id, CategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Kategoriya topilmadi"));
        String normalizedSlug = normalizeSlug(request.getSlug(), request.getName());
        ensureSlugUnique(normalizedSlug, id);
        categoryMapper.apply(request, category);
        category.setSlug(normalizedSlug);
        Category saved = categoryRepository.save(category);
        return ApiResponse.ok("Kategoriya yangilandi", categoryMapper.toResponse(saved));
    }

    @Override
    @Transactional
    public ApiResponse<Void> delete(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Kategoriya topilmadi");
        }
        Category category = categoryRepository.findById(id).orElseThrow();
        category.setStatus(RecordStatus.SOFT_DELETE);
        categoryRepository.save(category);
        return new ApiResponse<>(true, "Kategoriya soft delete qilindi", 200, ErrorCode.OK, null);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<CategoryResponse>> search(String query) {
        String q = query == null ? "" : query;
        List<CategoryResponse> data = categoryRepository
                .search(q)
                .stream()
                .map(categoryMapper::toResponse)
                .toList();
        return ApiResponse.ok("Qidiruv natijalari", data);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<CategoryResponse>> findByOrder(Integer orderNumber) {
        List<CategoryResponse> data = categoryRepository.findByOrderNumber(orderNumber).stream()
                .map(categoryMapper::toResponse)
                .toList();
        return ApiResponse.ok("Tartib raqami bo'yicha", data);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<List<CategoryResponse>> getSoftDeleted() {
        List<CategoryResponse> data = categoryRepository.findAllSoftDeleted().stream()
                .map(categoryMapper::toResponse)
                .toList();
        return ApiResponse.ok("Soft delete qilinganlar", data);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiResponse<CategoryResponse> getSoftById(UUID id) {
        Category category = categoryRepository.findSoftById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Soft delete kategoriya topilmadi"));
        return ApiResponse.ok("Soft delete kategoriya topildi", categoryMapper.toResponse(category));
    }

    @Override
    @Transactional
    public ApiResponse<Void> restore(UUID id) {
        Category category = categoryRepository.findSoftById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Soft delete kategoriya topilmadi"));
        category.setStatus(RecordStatus.ACTIVE);
        categoryRepository.save(category);
        return new ApiResponse<>(true, "Kategoriya tiklandi", 200, ErrorCode.OK, null);
    }

    @Override
    @Transactional
    public ApiResponse<Void> hardDelete(UUID id) {
        Category category = categoryRepository.findSoftById(id)
                .orElseThrow(() -> new ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Soft delete kategoriya topilmadi"));
        category.setStatus(RecordStatus.HARD_DELETE);
        categoryRepository.save(category);
        return new ApiResponse<>(true, "Kategoriya hard delete holatiga o'tkazildi", 200, ErrorCode.OK, null);
    }

    // Slugni normalizatsiya qilish (bo'sh bo'lsa name dan yasash)
    private String normalizeSlug(String slug, String name) {
        String base = (slug == null || slug.isBlank()) ? name : slug;
        if (base == null || base.isBlank()) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Slug yoki name bo'sh bo'lmasin");
        }
        String normalized = base.toLowerCase()
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
        if (normalized.isBlank()) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Slug noto'g'ri");
        }
        return normalized;
    }

    // Slug takrorini tekshirish
    private void ensureSlugUnique(String slug, UUID currentId) {
        boolean exists = currentId == null
                ? categoryRepository.existsBySlug(slug)
                : categoryRepository.existsBySlugAndIdNot(slug, currentId);
        if (exists) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT, "Slug allaqachon band");
        }
    }
}

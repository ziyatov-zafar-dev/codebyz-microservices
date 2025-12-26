package uz.codebyz.course.dto.category;

import java.time.LocalDateTime;
import java.util.UUID;

// Kategoriyani chiqarish uchun DTO
public class CategoryResponse {
    private UUID id; // Identifikator
    private String name; // Kategoriya nomi
    private String slug; // URL identifikator
    private Integer orderNumber; // Tartib
    private String description; // Tavsif
    private LocalDateTime createdAt; // Yaratilgan vaqt

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public Integer getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(Integer orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

package uz.codebyz.course.dto.course;

import uz.codebyz.course.domain.CourseLevel;

import java.time.LocalDateTime;
import java.util.UUID;

// Kursni chiqarish uchun DTO
public class CourseResponse {
    private UUID id; // Kurs identifikatori
    private String title; // Kurs nomi
    private String slug; // Slug
    private String summary; // Qisqa ta'rif
    private CourseLevel level; // Murakkablik darajasi
    private boolean published; // Nashr holati
    private LocalDateTime publishedAt; // Nashr vaqti
    private LocalDateTime createdAt; // Yaratilgan vaqt
    private LocalDateTime updatedAt; // Yangilangan vaqt
    private UUID categoryId; // Kategoriya ID
    private String categoryName; // Kategoriya nomi (ixtiyoriy)

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public CourseLevel getLevel() {
        return level;
    }

    public void setLevel(CourseLevel level) {
        this.level = level;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public LocalDateTime getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(LocalDateTime publishedAt) {
        this.publishedAt = publishedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}

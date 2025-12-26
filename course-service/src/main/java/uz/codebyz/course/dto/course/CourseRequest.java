package uz.codebyz.course.dto.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.UUID;

// Kurs yaratish/yangilash uchun DTO
public class CourseRequest {
    @NotBlank
    @Size(max = 180)
    private String title; // Kurs nomi

    @Size(max = 180)
    private String slug; // Slug (bo'sh bo'lsa nomdan olinadi)

    @Size(max = 1000)
    private String summary; // Qisqa ta'rif

    private UUID categoryId; // Kategoriya (ixtiyoriy)

    @NotBlank
    private String level; // Murakkablik darajasi (enum nomi)

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

    public UUID getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(UUID categoryId) {
        this.categoryId = categoryId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
}

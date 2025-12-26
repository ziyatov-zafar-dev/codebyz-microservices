package uz.codebyz.course.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import uz.codebyz.course.domain.CourseLevel;

import java.time.LocalDateTime;
import java.util.UUID;

// Platformadagi asosiy kurs yozuvi
@Entity
@Table(
        name = "courses",
        uniqueConstraints = @UniqueConstraint(name = "uk_courses_slug", columnNames = "slug")
)
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Asosiy kalit

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category; // Ixtiyoriy kategoriya bog'lanishi

    @Column(nullable = false, length = 180)
    private String title; // Kurs nomi

    @Column(nullable = false, length = 180)
    private String slug; // URL uchun qulay identifikator

    @Column(length = 1000)
    private String summary; // Qisqa ta'rif

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private CourseLevel level = CourseLevel.BEGINNER; // Murakkablik darajasi

    @Column(nullable = false)
    private boolean published = false; // Ko'rinish belgisi

    @Column
    private LocalDateTime publishedAt; // Nashr qilingan vaqt

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // Yaratilgan vaqt

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now(); // Oxirgi yangilangan vaqt

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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
}

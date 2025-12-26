package uz.codebyz.course.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

// Darsning asosiy kontent qismi
@Entity
@Table(name = "lesson_contents")
public class LessonContent {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Asosiy kalit

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false, unique = true)
    private Lesson lesson; // Bog'langan dars

    @Column(length = 16000)
    private String body; // Matn/markdown kontent

    @Column(length = 1024)
    private String videoUrl; // Ixtiyoriy video havolasi

    @Column(length = 1024)
    private String slideUrl; // Ixtiyoriy slayd havolasi

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now(); // Oxirgi yangilanish vaqti

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Lesson getLesson() {
        return lesson;
    }

    public void setLesson(Lesson lesson) {
        this.lesson = lesson;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getSlideUrl() {
        return slideUrl;
    }

    public void setSlideUrl(String slideUrl) {
        this.slideUrl = slideUrl;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

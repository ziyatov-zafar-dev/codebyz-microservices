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
import uz.codebyz.course.domain.CoursePublishStatus;

import java.time.LocalDateTime;
import java.util.UUID;

// Kurs nashr holati o'zgarishlarini kuzatadi
@Entity
@Table(name = "course_publish_events")
public class CoursePublish {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id; // Asosiy kalit

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course; // Maqsadli kurs

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private CoursePublishStatus status; // Yangi nashr holati

    @Column(length = 500)
    private String note; // Tekshiruvchilar uchun izoh

    @Column(nullable = false)
    private UUID changedBy; // Holatni o'zgartirgan foydalanuvchi

    @Column(nullable = false)
    private LocalDateTime changedAt = LocalDateTime.now(); // O'zgarish vaqti

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public CoursePublishStatus getStatus() {
        return status;
    }

    public void setStatus(CoursePublishStatus status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public UUID getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(UUID changedBy) {
        this.changedBy = changedBy;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}

package uz.codebyz.course.dto.course;

import java.util.List;

// Kurslar ro'yxati uchun o'rovchi DTO (zarur bo'lsa)
public class CourseListResponse {
    private List<CourseResponse> items;

    public List<CourseResponse> getItems() {
        return items;
    }

    public void setItems(List<CourseResponse> items) {
        this.items = items;
    }
}

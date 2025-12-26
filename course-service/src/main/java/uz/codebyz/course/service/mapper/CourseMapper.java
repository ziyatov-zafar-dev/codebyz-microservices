package uz.codebyz.course.service.mapper;

import org.springframework.stereotype.Component;
import uz.codebyz.course.dto.course.CourseRequest;
import uz.codebyz.course.dto.course.CourseResponse;
import uz.codebyz.course.entity.Category;
import uz.codebyz.course.entity.Course;

// Course entitini DTOlar bilan map qilish
@Component
public class CourseMapper {

    public Course toEntity(CourseRequest request, Category category) {
        Course course = new Course();
        apply(request, category, course);
        return course;
    }

    public void apply(CourseRequest request, Category category, Course entity) {
        entity.setTitle(request.getTitle());
        entity.setSlug(request.getSlug());
        entity.setSummary(request.getSummary());
        entity.setLevel(uz.codebyz.course.domain.CourseLevel.valueOf(request.getLevel()));
        entity.setCategory(category);
    }

    public CourseResponse toResponse(Course entity) {
        CourseResponse resp = new CourseResponse();
        resp.setId(entity.getId());
        resp.setTitle(entity.getTitle());
        resp.setSlug(entity.getSlug());
        resp.setSummary(entity.getSummary());
        resp.setLevel(entity.getLevel());
        resp.setPublished(entity.isPublished());
        resp.setPublishedAt(entity.getPublishedAt());
        resp.setCreatedAt(entity.getCreatedAt());
        resp.setUpdatedAt(entity.getUpdatedAt());
        if (entity.getCategory() != null) {
            resp.setCategoryId(entity.getCategory().getId());
            resp.setCategoryName(entity.getCategory().getName());
        }
        return resp;
    }
}

package uz.codebyz.course.dto.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Kategoriya yaratish/yangilash uchun DTO
public class CategoryRequest {
    @NotBlank
    @Size(max = 150)
    private String name; // Kategoriya nomi

    @Size(max = 150)
    private String slug; // URL uchun identifikator (bo'sh bo'lsa avtomatik yaratiladi)

    private Integer orderNumber; // Tartib raqami (ixtiyoriy)

    @Size(max = 1000)
    private String description; // Qisqa tavsif

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
}

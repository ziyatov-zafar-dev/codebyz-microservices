package uz.codebyz.course.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.course.entity.Category;

import java.util.UUID;

// Kategoriyalar uchun repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    @Query("""
            select (count(c)>0)
            from Category c
            where lower(c.slug) = lower(:slug)
              and c.status <> uz.codebyz.course.domain.RecordStatus.HARD_DELETE
            """)
    boolean existsBySlug(@Param("slug") String slug); // Slug bo'yicha mavjudligini tekshirish

    @Query("""
            select (count(c)>0)
            from Category c
            where lower(c.slug) = lower(:slug)
              and c.id <> :id
              and c.status <> uz.codebyz.course.domain.RecordStatus.HARD_DELETE
            """)
    boolean existsBySlugAndIdNot(@Param("slug") String slug, @Param("id") UUID id); // O'zidan boshqa slug bor-yo'qligi

    @Query("""
            select c
            from Category c
            where (
                lower(c.name) like lower(concat('%', :q, '%'))
                or lower(c.slug) like lower(concat('%', :q, '%'))
                or lower(c.description) like lower(concat('%', :q, '%'))
            )
            and c.status = uz.codebyz.course.domain.RecordStatus.ACTIVE
            """)
    java.util.List<Category> search(@Param("q") String query); // Qidirish (name/slug/description)

    @Query("""
            select c
            from Category c
            where c.orderNumber = :orderNumber
              and c.status = uz.codebyz.course.domain.RecordStatus.ACTIVE
            """)
    java.util.List<Category> findByOrderNumber(@Param("orderNumber") Integer orderNumber); // Tartib raqami bo'yicha

    @Query("""
            select c
            from Category c
            where c.status = uz.codebyz.course.domain.RecordStatus.ACTIVE
            """)
    java.util.List<Category> findAllActive(); // Faqat aktivlar

    @Query("""
            select c
            from Category c
            where c.id = :id
              and c.status = uz.codebyz.course.domain.RecordStatus.ACTIVE
            """)
    java.util.Optional<Category> findActiveById(@Param("id") UUID id); // Aktiv bitta

    @Query("""
            select c
            from Category c
            where c.status = uz.codebyz.course.domain.RecordStatus.SOFT_DELETE
            """)
    java.util.List<Category> findAllSoftDeleted(); // Soft delete qilinganlar

    @Query("""
            select c
            from Category c
            where c.id = :id
              and c.status = uz.codebyz.course.domain.RecordStatus.SOFT_DELETE
            """)
    java.util.Optional<Category> findSoftById(@Param("id") UUID id); // Soft delete bitta
}

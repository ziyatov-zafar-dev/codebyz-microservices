package uz.codebyz.course.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import uz.codebyz.course.entity.ModuleProgress;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

// Modul progressi uchun repository
public interface ModuleProgressRepository extends JpaRepository<ModuleProgress, UUID> {
    @Query("select mp from ModuleProgress mp where mp.module.id = :moduleId")
    List<ModuleProgress> findByModuleId(@Param("moduleId") UUID moduleId); // Modul bo'yicha progresslar

    @Query("select mp from ModuleProgress mp where mp.studentId = :studentId")
    List<ModuleProgress> findByStudentId(@Param("studentId") UUID studentId); // O'quvchining modul progresslari

    @Query("select mp from ModuleProgress mp where mp.module.id = :moduleId and mp.studentId = :studentId")
    Optional<ModuleProgress> findByModuleIdAndStudentId(@Param("moduleId") UUID moduleId, @Param("studentId") UUID studentId); // Bitta modul progressi
}

package uz.codebyz.ads.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import uz.codebyz.ads.domain.Ad;
import uz.codebyz.ads.domain.AdStatus;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AdRepository extends JpaRepository<Ad, UUID>, JpaSpecificationExecutor<Ad> {
    List<Ad> findByDeletedFalseAndEndAtNotNullAndEndAtLessThanEqualAndStatusNot(Instant now, AdStatus status);
}

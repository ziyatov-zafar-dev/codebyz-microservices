package uz.codebyz.ads.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import uz.codebyz.ads.common.PagedResult;
import uz.codebyz.ads.domain.Ad;
import uz.codebyz.ads.domain.AdAudienceRole;
import uz.codebyz.ads.domain.AdStatus;
import uz.codebyz.ads.domain.AdType;
import uz.codebyz.ads.repository.AdRepository;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AdService {

    private static final Logger log = LoggerFactory.getLogger(AdService.class);
    private static final long VIEW_COOLDOWN_SECONDS = 30;
    private static final long CLICK_COOLDOWN_SECONDS = 15;

    private final AdRepository repository;
    private final Map<String, Instant> viewThrottles = new ConcurrentHashMap<>();
    private final Map<String, Instant> clickThrottles = new ConcurrentHashMap<>();

    public AdService(AdRepository repository) {
        this.repository = repository;
    }

    public Ad create(UUID ownerId,
                     String title,
                     String description,
                     String mediaUrl,
                     String mediaPath,
                     String targetUrl,
                     AdType type,
                     String page,
                     String section,
                     String position,
                     Instant startAt,
                     Instant endAt,
                     AdStatus status,
                     int priority,
                     Set<AdAudienceRole> audienceRoles) {
        UUID id = UUID.randomUUID();
        Instant now = Instant.now();
        Ad ad = new Ad(id, ownerId, title, description, mediaUrl, mediaPath, targetUrl, type, page, section, position,
                startAt, endAt, status, priority, audienceRoles, now);
        ad.setLastUpdatedBy(ownerId);
        repository.save(ad);
        log.info("Ad created id={} by={}", id, ownerId);
        return ad;
    }

    public Optional<Ad> update(UUID adId,
                               UUID requesterId,
                               boolean admin,
                               String title,
                               String description,
                               String mediaUrl,
                               String mediaPath,
                               String targetUrl,
                               AdType type,
                               String page,
                               String section,
                               String position,
                               Instant startAt,
                               Instant endAt,
                               Integer priority,
                               Set<AdAudienceRole> audienceRoles) {
        return find(adId).filter(ad -> canEdit(ad, requesterId, admin)).map(ad -> {
            if (title != null && !title.isBlank()) ad.setTitle(title);
            if (description != null && !description.isBlank()) ad.setDescription(description);
            if (mediaUrl != null) ad.setMediaUrl(mediaUrl);
            if (mediaPath != null) ad.setMediaPath(mediaPath);
            if (targetUrl != null) ad.setTargetUrl(targetUrl);
            if (type != null) ad.setType(type);
            if (page != null) ad.setPage(page);
            if (section != null) ad.setSection(section);
            if (position != null) ad.setPosition(position);
            if (startAt != null) ad.setStartAt(startAt);
            if (endAt != null) ad.setEndAt(endAt);
            if (priority != null) ad.setPriority(priority);
            if (audienceRoles != null && !audienceRoles.isEmpty()) ad.setAudienceRoles(audienceRoles);
            ad.setUpdatedAt(Instant.now());
            ad.setLastUpdatedBy(requesterId);
            repository.save(ad);
            log.info("Ad updated id={} by={}", adId, requesterId);
            return ad;
        });
    }

    public Optional<Ad> attachMedia(UUID adId, UUID requesterId, boolean admin, String mediaPath, String mediaUrl) {
        return find(adId).filter(ad -> canEdit(ad, requesterId, admin)).map(ad -> {
            ad.setMediaPath(mediaPath);
            ad.setMediaUrl(mediaUrl);
            ad.setUpdatedAt(Instant.now());
            ad.setLastUpdatedBy(requesterId);
            repository.save(ad);
            return ad;
        });
    }

    public Optional<Ad> clearMedia(UUID adId, UUID requesterId, boolean admin) {
        return find(adId).filter(ad -> canEdit(ad, requesterId, admin)).map(ad -> {
            ad.setMediaPath(null);
            ad.setMediaUrl(null);
            ad.setUpdatedAt(Instant.now());
            ad.setLastUpdatedBy(requesterId);
            repository.save(ad);
            return ad;
        });
    }

    public Optional<Ad> changeStatus(UUID adId, UUID requesterId, boolean admin, AdStatus status) {
        return find(adId).filter(ad -> canEdit(ad, requesterId, admin)).map(ad -> {
            ad.setStatus(status);
            ad.setUpdatedAt(Instant.now());
            ad.setLastUpdatedBy(requesterId);
            repository.save(ad);
            log.info("Ad status changed id={} status={} by={}", adId, status, requesterId);
            return ad;
        });
    }

    public boolean softDelete(UUID adId, UUID requesterId, boolean admin) {
        return find(adId)
                .filter(ad -> canEdit(ad, requesterId, admin))
                .map(ad -> {
                    ad.setDeleted(true);
                    ad.setStatus(AdStatus.INACTIVE);
                    ad.setUpdatedAt(Instant.now());
                    ad.setLastUpdatedBy(requesterId);
                    repository.save(ad);
                    log.info("Ad soft-deleted id={} by={}", adId, requesterId);
                    return true;
                }).orElse(false);
    }

    public Optional<Ad> find(UUID id) {
        expireOverdue();
        return repository.findById(id).filter(ad -> !ad.isDeleted());
    }

    public PagedResult<Ad> listAdmin(int page, int size, AdStatus status, String pageKey, String section, String position, AdType type) {
        expireOverdue();
        Pageable pageable = PageRequest.of(page, size, Sort.by(
                Sort.Order.asc("status"),
                Sort.Order.desc("createdAt")
        ));
        Specification<Ad> spec = Specification.where(notDeleted())
                .and(optionalStatus(status))
                .and(optionalEquals("page", pageKey))
                .and(optionalEquals("section", section))
                .and(optionalEquals("position", position))
                .and(optionalEnumEquals("type", type));
        Page<Ad> result = repository.findAll(spec, pageable);
        return new PagedResult<>(result.getTotalElements(), result.getContent());
    }

    public List<Ad> listForAudience(String role, String pageKey, String section, String position, AdType type, int limit) {
        expireOverdue();
        Instant now = Instant.now();
        Pageable pageable = PageRequest.of(0, limit > 0 ? limit : 50, Sort.by(
                Sort.Order.desc("priority"),
                Sort.Order.asc("startAt"),
                Sort.Order.desc("createdAt")
        ));
        Specification<Ad> spec = Specification.where(notDeleted())
                .and(activeNow(now))
                .and(roleMatches(role))
                .and(optionalEquals("page", pageKey))
                .and(optionalEquals("section", section))
                .and(optionalEquals("position", position))
                .and(optionalEnumEquals("type", type));
        return repository.findAll(spec, pageable).getContent();
    }

    public boolean recordView(UUID adId, UUID userId) {
        Optional<Ad> adOpt = find(adId);
        if (adOpt.isEmpty() || !adOpt.get().isActiveNow(Instant.now())) return false;
        String key = buildThrottleKey("view", adId, userId);
        if (isThrottled(viewThrottles, key, VIEW_COOLDOWN_SECONDS)) return false;
        Ad ad = adOpt.get();
        ad.setViewCount(ad.getViewCount() + 1);
        repository.save(ad);
        return true;
    }

    public boolean recordClick(UUID adId, UUID userId) {
        Optional<Ad> adOpt = find(adId);
        if (adOpt.isEmpty() || !adOpt.get().isActiveNow(Instant.now())) return false;
        String key = buildThrottleKey("click", adId, userId);
        if (isThrottled(clickThrottles, key, CLICK_COOLDOWN_SECONDS)) return false;
        Ad ad = adOpt.get();
        ad.setClickCount(ad.getClickCount() + 1);
        repository.save(ad);
        return true;
    }

    private void expireOverdue() {
        Instant now = Instant.now();
        List<Ad> overdue = repository.findByDeletedFalseAndEndAtNotNullAndEndAtLessThanEqualAndStatusNot(now, AdStatus.EXPIRED);
        overdue.forEach(ad -> {
            ad.setStatus(AdStatus.EXPIRED);
            ad.setUpdatedAt(now);
        });
        if (!overdue.isEmpty()) {
            repository.saveAll(overdue);
            log.info("Expired {} ads", overdue.size());
        }
    }

    private boolean canEdit(Ad ad, UUID requesterId, boolean admin) {
        return admin || ad.getOwnerId().equals(requesterId);
    }

    private Specification<Ad> notDeleted() {
        return (root, query, cb) -> cb.isFalse(root.get("deleted"));
    }

    private Specification<Ad> optionalStatus(AdStatus status) {
        if (status == null) return null;
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    private Specification<Ad> optionalEquals(String field, String value) {
        if (value == null) return null;
        return (root, query, cb) -> cb.equal(
                cb.lower(root.get(field).as(String.class)),
                value.toLowerCase()
        );
    }

    private Specification<Ad> optionalEnumEquals(String field, Enum<?> value) {
        if (value == null) return null;
        return (root, query, cb) -> cb.equal(root.get(field), value);
    }

    private Specification<Ad> activeNow(Instant now) {
        return (root, query, cb) -> cb.and(
                cb.equal(root.get("status"), AdStatus.ACTIVE),
                cb.or(cb.isNull(root.get("startAt")), cb.lessThanOrEqualTo(root.get("startAt"), now)),
                cb.or(cb.isNull(root.get("endAt")), cb.greaterThan(root.get("endAt"), now))
        );
    }

    private Specification<Ad> roleMatches(String role) {
        return (root, query, cb) -> {
            query.distinct(true);
            var rolesJoin = root.join("audienceRoles");
            if (role == null) {
                return cb.or(
                        cb.equal(rolesJoin, AdAudienceRole.ALL),
                        cb.equal(rolesJoin, AdAudienceRole.STUDENT) // default anonymous ko'rsin
                );
            }
            AdAudienceRole userRole = AdAudienceRole.ALL;
            try {
                userRole = AdAudienceRole.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException ignored) {}
            return cb.or(
                    cb.equal(rolesJoin, AdAudienceRole.ALL),
                    cb.equal(rolesJoin, userRole)
            );
        };
    }

    private String buildThrottleKey(String type, UUID adId, UUID userId) {
        return type + ":" + adId + ":" + (userId == null ? "anon" : userId);
    }

    private boolean isThrottled(Map<String, Instant> map, String key, long cooldownSeconds) {
        Instant now = Instant.now();
        Instant last = map.get(key);
        if (last != null && now.minusSeconds(cooldownSeconds).isBefore(last)) {
            return true;
        }
        map.put(key, now);
        return false;
    }

}

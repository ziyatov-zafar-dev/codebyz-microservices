package uz.codebyz.ads.domain;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "ads")
public class Ad {
    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID ownerId;

    @Column(nullable = false, length = 140)
    private String title;

    @Column(nullable = false, length = 2048)
    private String description;

    @Column(length = 1024)
    private String mediaUrl;

    @Column(length = 1024)
    private String mediaPath;

    @Column(length = 1024)
    private String targetUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AdType type;

    @Column(nullable = false)
    private String page;

    @Column(nullable = false)
    private String section;

    @Column(nullable = false)
    private String position;

    private Instant startAt;
    private Instant endAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private AdStatus status;

    @Column(nullable = false)
    private int priority;

    @ElementCollection(targetClass = AdAudienceRole.class)
    @CollectionTable(name = "ad_audience_roles", joinColumns = @JoinColumn(name = "ad_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 16)
    private Set<AdAudienceRole> audienceRoles = EnumSet.of(AdAudienceRole.ALL);

    @Column(nullable = false)
    private boolean deleted = false;

    @Column(nullable = false)
    private long viewCount = 0;

    @Column(nullable = false)
    private long clickCount = 0;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    private UUID lastUpdatedBy;

    public Ad() {
    }

    public Ad(UUID id,
              UUID ownerId,
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
              Set<AdAudienceRole> audienceRoles,
              Instant createdAt) {
        this.id = id;
        this.ownerId = ownerId;
        this.title = title;
        this.description = description;
        this.mediaUrl = mediaUrl;
        this.mediaPath = mediaPath;
        this.targetUrl = targetUrl;
        this.type = type;
        this.page = page;
        this.section = section;
        this.position = position;
        this.startAt = startAt;
        this.endAt = endAt;
        this.status = status;
        this.priority = priority;
        this.audienceRoles = audienceRoles == null || audienceRoles.isEmpty()
                ? EnumSet.of(AdAudienceRole.ALL) : EnumSet.copyOf(audienceRoles);
        this.deleted = false;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getMediaPath() {
        return mediaPath;
    }

    public void setMediaPath(String mediaPath) {
        this.mediaPath = mediaPath;
    }

    public AdType getType() {
        return type;
    }

    public void setType(AdType type) {
        this.type = type;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public Instant getStartAt() {
        return startAt;
    }

    public void setStartAt(Instant startAt) {
        this.startAt = startAt;
    }

    public Instant getEndAt() {
        return endAt;
    }

    public void setEndAt(Instant endAt) {
        this.endAt = endAt;
    }

    public AdStatus getStatus() {
        return status;
    }

    public void setStatus(AdStatus status) {
        this.status = status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Set<AdAudienceRole> getAudienceRoles() {
        return audienceRoles;
    }

    public void setAudienceRoles(Set<AdAudienceRole> audienceRoles) {
        this.audienceRoles = audienceRoles == null || audienceRoles.isEmpty()
                ? EnumSet.of(AdAudienceRole.ALL) : EnumSet.copyOf(audienceRoles);
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public long getViewCount() {
        return viewCount;
    }

    public void setViewCount(long viewCount) {
        this.viewCount = viewCount;
    }

    public long getClickCount() {
        return clickCount;
    }

    public void setClickCount(long clickCount) {
        this.clickCount = clickCount;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public UUID getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(UUID lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public boolean canShowForRole(String role) {
        return audienceRoles.stream().anyMatch(r -> AdAudienceRole.matches(r, role));
    }

    public boolean isActiveNow(Instant now) {
        boolean timeOk = (startAt == null || !now.isBefore(startAt)) &&
                (endAt == null || now.isBefore(endAt));
        return status == AdStatus.ACTIVE && timeOk && !deleted;
    }
}

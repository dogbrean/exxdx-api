package com.exxdx.exxdx_api.diary.domain;

import java.time.LocalDateTime;
import java.util.Objects;

public class DiaryEntry {

    public enum Visibility { PUBLIC, PRIVATE }

    private final Long id;              // 생성 전에는 null
    private final Long ownerId;         // 글 소유자 (접근제어 판단용)
    private String title;
    private String content;
    private Visibility visibility;      // 공개 범위
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // 생성자
    public DiaryEntry(Long id,
                      Long ownerId,
                      String title,
                      String content,
                      Visibility visibility,
                      LocalDateTime createdAt,
                      LocalDateTime updatedAt) {

        if (ownerId == null) throw new IllegalArgumentException("ownerId는 필수입니다");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("제목은 필수입니다");

        this.id = id;
        this.ownerId = ownerId;
        this.title = title;
        this.content = content == null ? "" : content;
        this.visibility = visibility == null ? Visibility.PUBLIC : visibility;
        this.createdAt = createdAt == null ? LocalDateTime.now() : createdAt;
        this.updatedAt = updatedAt == null ? this.createdAt : updatedAt;
    }

    // 팩토리 (편의용)
    public static DiaryEntry createNew(Long ownerId, String title, String content, Visibility visibility) {
        return new DiaryEntry(null, ownerId, title, content, visibility, null, null);
    }

    // 비즈니스 행위
    public DiaryEntry update(String newTitle, String newContent) {
        if (newTitle == null || newTitle.isBlank())
            throw new IllegalArgumentException("제목은 필수입니다");
        this.title = newTitle;
        this.content = newContent == null ? "" : newContent;
        this.updatedAt = LocalDateTime.now();
        return this;
    }

    public void makePrivate(Long actorId) {
        requireOwner(actorId);
        if (this.visibility != Visibility.PRIVATE) {
            this.visibility = Visibility.PRIVATE;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void makePublic(Long actorId) {
        requireOwner(actorId);
        if (this.visibility != Visibility.PUBLIC) {
            this.visibility = Visibility.PUBLIC;
            this.updatedAt = LocalDateTime.now();
        }
    }

    // 조회 권한 판정 (간단 버전)
    public boolean canView(Long viewerId) {
        if (this.visibility == Visibility.PUBLIC) return true;
        return Objects.equals(this.ownerId, viewerId);
    }

    // 내부 규칙
    private void requireOwner(Long actorId) {
        if (!Objects.equals(this.ownerId, actorId))
            throw new IllegalStateException("소유자만 수정할 수 있습니다");
    }

    // === getters (IntelliJ ⌘N → Getter로 생성) ===
    public Long getId() { return id; }
    public Long getOwnerId() { return ownerId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public Visibility getVisibility() { return visibility; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
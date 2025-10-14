// diary/web/response/DiaryResponse.java
package com.exxdx.exxdx_api.diary.web.response;

import com.exxdx.exxdx_api.diary.domain.DiaryEntry;
import com.exxdx.exxdx_api.diary.domain.DiaryEntry.Visibility;

import java.time.LocalDateTime;

public record DiaryResponse(
        Long id,
        Long ownerId,
        String title,
        String content,
        Visibility visibility,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static DiaryResponse from(DiaryEntry e) {
        return new DiaryResponse(
                e.getId(), e.getOwnerId(), e.getTitle(), e.getContent(),
                e.getVisibility(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
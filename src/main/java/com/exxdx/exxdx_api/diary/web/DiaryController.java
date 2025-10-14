package com.exxdx.exxdx_api.diary.web;

import com.exxdx.exxdx_api.diary.app.DiaryService;
import com.exxdx.exxdx_api.diary.domain.DiaryEntry;
import com.exxdx.exxdx_api.diary.domain.DiaryEntry.Visibility;
import com.exxdx.exxdx_api.diary.web.request.DiaryCreateRequest;
import com.exxdx.exxdx_api.diary.web.request.DiaryUpdateRequest;
import com.exxdx.exxdx_api.diary.web.request.DiaryVisibilityRequest;
import com.exxdx.exxdx_api.diary.web.response.DiaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/diary")
@RequiredArgsConstructor
public class DiaryController {

    private final DiaryService service;

    // ⛳️ MVP용: 인증 붙이기 전까지는 헤더 X-USER-ID로 사용자 ID를 받자.
    private Long currentUserId(String header) {
        if (header == null || header.isBlank()) return null;
        return Long.parseLong(header);
    }

    @PostMapping
    public ResponseEntity<DiaryResponse> create(@RequestHeader(value = "X-USER-ID") String userHeader,
                                                @RequestBody DiaryCreateRequest req) {
        Long ownerId = Long.parseLong(userHeader);
        DiaryEntry e = service.create(ownerId, req.title(), req.content(), req.visibility());
        return ResponseEntity.ok(DiaryResponse.from(e));
    }

    // 공개/비공개 권한 체크 버전
    @GetMapping("/{id}")
    public DiaryResponse getForViewer(@PathVariable("id") Long id,
                                      @RequestHeader(value = "X-USER-ID", required = false) String userHeader) {
        Long viewerId = currentUserId(userHeader);
        return DiaryResponse.from(service.getForViewer(id, viewerId));
    }

    @GetMapping
    public List<DiaryResponse> list(@RequestParam(name = "page", defaultValue = "0") int page,
                                    @RequestParam(name = "size", defaultValue = "10") int size) {
        return service.list(page, size).stream().map(DiaryResponse::from).toList();
    }

    @PutMapping("/{id}")
    public DiaryResponse update(@PathVariable("id") Long id,
                                @RequestHeader("X-USER-ID") String userHeader,
                                @RequestBody DiaryUpdateRequest req) {
        Long actorId = Long.parseLong(userHeader);
        return DiaryResponse.from(service.update(id, actorId, req.title(), req.content()));
    }

    @PatchMapping("/{id}/visibility")
    public DiaryResponse changeVisibility(@PathVariable("id") Long id,
                                          @RequestHeader("X-USER-ID") String userHeader,
                                          @RequestBody DiaryVisibilityRequest req) {
        Long actorId = Long.parseLong(userHeader);
        DiaryEntry e = switch (req.visibility()) {
            case PRIVATE -> service.makePrivate(id, actorId);
            case PUBLIC  -> service.makePublic(id, actorId);
        };
        return DiaryResponse.from(e);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Long id,
                       @RequestHeader("X-USER-ID") String userHeader) {
        Long actorId = Long.parseLong(userHeader);
        service.delete(id, actorId);
    }
}
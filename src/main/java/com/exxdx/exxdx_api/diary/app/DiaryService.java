package com.exxdx.exxdx_api.diary.app;

import com.exxdx.exxdx_api.diary.domain.DiaryEntry;
import com.exxdx.exxdx_api.diary.domain.DiaryEntry.Visibility;
import com.exxdx.exxdx_api.diary.domain.DiaryEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryEntryRepository repo;  // Port에 의존 (도메인 인터페이스)

    /**
     * 글 생성 — 로그인 사용자 ID(ownerId)를 반드시 받도록 변경
     */
    @Transactional
    public DiaryEntry create(Long ownerId, String title, String content, Visibility visibility) {
        DiaryEntry entry = DiaryEntry.createNew(ownerId, title, content, visibility);
        return repo.save(entry);
    }

    /**
     * 단건 조회(소유자/공개범위 고려)
     * - viewerId가 null이면 공개글만 허용하고 싶다면 정책에 맞게 수정 가능
     */
    @Transactional(readOnly = true)
    public DiaryEntry getForViewer(Long id, Long viewerId) {
        DiaryEntry e = repo.findById(id).orElseThrow(() ->
                new NoSuchElementException("존재하지 않는 글: " + id));
        if (!e.canView(viewerId)) {
            throw new IllegalStateException("열람 권한이 없습니다");
        }
        return e;
    }

    /**
     * 단건 조회(내부/관리용) — 권한 체크 없이 가져오는 버전
     */
    @Transactional(readOnly = true)
    public DiaryEntry get(Long id) {
        return repo.findById(id).orElseThrow(() ->
                new NoSuchElementException("존재하지 않는 글: " + id));
    }

    /**
     * 목록 — 필요 시 공개글만/소유자별 필터 메서드를 레포지토리에 추가
     */
    @Transactional(readOnly = true)
    public List<DiaryEntry> list(int page, int size) {
        return repo.findAll(page, size);
    }

    /**
     * 수정 — 작성자만 허용 (도메인에 requireOwner가 있으니 여기서 체크)
     */
    @Transactional
    public DiaryEntry update(Long id, Long actorId, String title, String content) {
        DiaryEntry entry = get(id);
        if (!entry.getOwnerId().equals(actorId)) {
            throw new IllegalStateException("소유자만 수정할 수 있습니다");
        }
        entry.update(title, content);
        return repo.save(entry);
    }

    /**
     * 공개 범위 변경 — 도메인 메서드 사용
     */
    @Transactional
    public DiaryEntry makePrivate(Long id, Long actorId) {
        DiaryEntry entry = get(id);
        entry.makePrivate(actorId); // 내부에서 owner 체크 + updatedAt 갱신
        return repo.save(entry);
    }

    @Transactional
    public DiaryEntry makePublic(Long id, Long actorId) {
        DiaryEntry entry = get(id);
        entry.makePublic(actorId);
        return repo.save(entry);
    }

    /**
     * 삭제 — 작성자만 허용(정책에 따라 소프트 삭제로 바꿀 수도 있음)
     */
    @Transactional
    public void delete(Long id, Long actorId) {
        DiaryEntry entry = get(id);
        if (!entry.getOwnerId().equals(actorId)) {
            throw new IllegalStateException("소유자만 삭제할 수 있습니다");
        }
        repo.deleteById(id);
    }
}
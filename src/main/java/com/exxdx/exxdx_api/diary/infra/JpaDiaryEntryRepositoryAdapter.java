package com.exxdx.exxdx_api.diary.infra;

import com.exxdx.exxdx_api.diary.domain.DiaryEntry;
import com.exxdx.exxdx_api.diary.domain.DiaryEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaDiaryEntryRepositoryAdapter implements DiaryEntryRepository {

    private final SpringDataDiaryJpa jpa;

    @Override
    public DiaryEntry save(DiaryEntry d) {
        JpaDiaryEntryEntity e = new JpaDiaryEntryEntity(
                d.getId(), d.getOwnerId(), d.getTitle(), d.getContent(),
                d.getVisibility(), d.getCreatedAt(), d.getUpdatedAt()
        );
        JpaDiaryEntryEntity saved = jpa.save(e);
        return toDomain(saved);
    }

    @Override
    public Optional<DiaryEntry> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<DiaryEntry> findAll(int page, int size) {
        var pr = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return jpa.findAll(pr).map(this::toDomain).toList();
    }

    @Override
    public void deleteById(Long id) {
        jpa.deleteById(id);
    }

    // 도메인 확장에 대비한 추가 구현(선택): count/exists
    public long countByOwner(Long ownerId) { return jpa.countByOwnerId(ownerId); }
    public boolean existsById(Long id) { return jpa.existsById(id); }

    private DiaryEntry toDomain(JpaDiaryEntryEntity e) {
        return new DiaryEntry(
                e.getId(), e.getOwnerId(), e.getTitle(), e.getContent(),
                e.getVisibility(), e.getCreatedAt(), e.getUpdatedAt()
        );
    }
}
package com.exxdx.exxdx_api.diary.domain;

import java.util.*;

public interface DiaryEntryRepository {
    DiaryEntry save(DiaryEntry entry);
    Optional<DiaryEntry> findById(Long id);
    List<DiaryEntry> findAll(int page, int size);
    void deleteById(Long id);
}
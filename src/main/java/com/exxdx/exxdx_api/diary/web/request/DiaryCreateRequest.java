package com.exxdx.exxdx_api.diary.web.request;

import com.exxdx.exxdx_api.diary.domain.DiaryEntry.Visibility;

public record DiaryCreateRequest(String title, String content, Visibility visibility) {}
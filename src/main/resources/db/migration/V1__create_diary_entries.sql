-- V1: diary_entries 테이블 생성
CREATE TABLE IF NOT EXISTS diary_entries (
  id BIGSERIAL PRIMARY KEY,
  title VARCHAR(200) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMP NOT NULL DEFAULT NOW()
);
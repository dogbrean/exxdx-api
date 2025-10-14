-- V3: add owner_id and visibility to diary_entries

-- 1) 새 컬럼 추가 (기존 데이터가 있다면 임시 default로 채운 뒤 default 제거)
ALTER TABLE diary_entries
  ADD COLUMN owner_id BIGINT,
  ADD COLUMN visibility VARCHAR(16) NOT NULL DEFAULT 'PUBLIC';

-- 2) 기존 row에 owner_id 값 채우기 (데이터가 없으면 이 단계는 영향 없음)
--    실제 서비스라면 적절한 소유자 값으로 백필 필요.
UPDATE diary_entries SET owner_id = 0 WHERE owner_id IS NULL;

-- 3) NOT NULL 제약 걸고, visibility 값 보호를 위한 CHECK 제약 추가
ALTER TABLE diary_entries
  ALTER COLUMN owner_id SET NOT NULL;

ALTER TABLE diary_entries
  ADD CONSTRAINT chk_diary_visibility
  CHECK (visibility IN ('PUBLIC','PRIVATE'));

-- 4) 인덱스 (조회 패턴 대비)
CREATE INDEX IF NOT EXISTS idx_diary_owner ON diary_entries (owner_id);
CREATE INDEX IF NOT EXISTS idx_diary_created_desc ON diary_entries (created_at DESC);
CREATE INDEX IF NOT EXISTS idx_diary_visibility ON diary_entries (visibility);
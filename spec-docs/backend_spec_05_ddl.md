# 5. DDL 스크립트

```sql
-- =====================================================
-- 아이니이누 (Aini Inu) Database DDL
-- Version: 1.0
-- Created: 2026-01-26
-- Database: MySQL 9+
-- Charset: utf8mb4_unicode_ci
-- =====================================================

-- -----------------------------------------------------
-- 회원 관리 (Member Context)
-- -----------------------------------------------------

CREATE TABLE member (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '회원 ID',
    email VARCHAR(255) NOT NULL COMMENT '이메일',
    nickname VARCHAR(50) NOT NULL COMMENT '닉네임 (최대 10자)',
    profile_image_url VARCHAR(500) COMMENT '프로필 이미지 URL',
    age INT COMMENT '나이',
    gender VARCHAR(10) COMMENT '성별: MALE, FEMALE, UNKNOWN',
    mbti VARCHAR(10) COMMENT 'MBTI',
    personality VARCHAR(200) COMMENT '성격',
    self_introduction TEXT COMMENT '자기소개',
    member_type VARCHAR(20) NOT NULL DEFAULT 'NON_PET_OWNER' COMMENT '회원 유형: PET_OWNER, NON_PET_OWNER',
    linked_nickname VARCHAR(50) COMMENT '애견 연계 닉네임 (예: 몽이아빠)',
    manner_temperature DECIMAL(3,1) NOT NULL DEFAULT 5.0 COMMENT '매너 온도 (평균, 1.0~10.0)',
    manner_score_sum INT NOT NULL DEFAULT 0 COMMENT '받은 매너 점수 합 (정수)',
    manner_score_count INT NOT NULL DEFAULT 0 COMMENT '받은 매너 점수 개수',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '상태: ACTIVE, INACTIVE, BANNED',
    is_verified TINYINT(1) NOT NULL DEFAULT 0 COMMENT '인증 완료 여부',
    social_provider VARCHAR(20) NOT NULL COMMENT '소셜 로그인: NAVER, KAKAO, GOOGLE',
    social_id VARCHAR(255) NOT NULL COMMENT '소셜 고유 ID',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    
    UNIQUE KEY uk_email (email),
    UNIQUE KEY uk_social (social_provider, social_id),
    UNIQUE KEY uk_nickname (nickname),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='회원';

-- -----------------------------------------------------
-- 리프레시 토큰 (Member Context)
-- -----------------------------------------------------

CREATE TABLE refresh_token (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '리프레시 토큰 ID',
    member_id BIGINT NOT NULL COMMENT '회원 ID (member.id 참조)',
    token_hash VARCHAR(255) NOT NULL COMMENT '리프레시 토큰 해시',
    expires_at DATETIME NOT NULL COMMENT '만료 시각',
    revoked_at DATETIME COMMENT '폐기 시각',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    
    INDEX idx_member_id (member_id),
    INDEX idx_expires_at (expires_at),
    UNIQUE KEY uk_token_hash (token_hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='리프레시 토큰';

-- -----------------------------------------------------
-- 견주 성향 카테고리 (Member Context)
-- -----------------------------------------------------

CREATE TABLE member_personality_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '성향 ID',
    name VARCHAR(50) NOT NULL COMMENT '성향명',
    code VARCHAR(30) NOT NULL COMMENT '성향 코드',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    
    UNIQUE KEY uk_name (name),
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='견주 성향 카테고리';

CREATE TABLE member_personality (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    member_id BIGINT NOT NULL COMMENT '회원 ID (member.id 참조)',
    personality_type_id BIGINT NOT NULL COMMENT '성향 ID (member_personality_type.id 참조)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    
    INDEX idx_member_id (member_id),
    INDEX idx_personality_type_id (personality_type_id),
    UNIQUE KEY uk_member_personality (member_id, personality_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='회원-견주 성향 연결';

-- -----------------------------------------------------
-- 매너 평가 (Member Context)
-- -----------------------------------------------------

CREATE TABLE manner_score (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '후기 ID',
    chat_room_id BIGINT NOT NULL COMMENT '채팅방 ID (chat_room.id 참조, WALK)',
    reviewer_id BIGINT NOT NULL COMMENT '작성자 ID (member.id 참조)',
    reviewee_id BIGINT NOT NULL COMMENT '대상자 ID (member.id 참조)',
    score TINYINT NOT NULL COMMENT '점수 (1~10)',
    comment TEXT COMMENT '한줄 후기 (선택)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    
    INDEX idx_chat_room_id (chat_room_id),
    INDEX idx_reviewer_id (reviewer_id),
    INDEX idx_reviewee_id (reviewee_id),
    UNIQUE KEY uk_chat_room_reviewer_reviewee (chat_room_id, reviewer_id, reviewee_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='산책 후기(매너 평가)';

-- -----------------------------------------------------
-- 반려견 관리 (Pet Context)
-- -----------------------------------------------------

CREATE TABLE breed (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '견종 ID',
    name VARCHAR(100) NOT NULL COMMENT '견종명',
    size VARCHAR(10) NOT NULL COMMENT '크기: SMALL, MEDIUM, LARGE',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    
    UNIQUE KEY uk_name (name),
    INDEX idx_size (size)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='견종';

CREATE TABLE pet_personality_type (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '성향 ID',
    name VARCHAR(50) NOT NULL COMMENT '성향명',
    code VARCHAR(30) NOT NULL COMMENT '성향 코드',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    
    UNIQUE KEY uk_name (name),
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='반려견 성향 카테고리';

CREATE TABLE pet (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '반려견 ID',
    member_id BIGINT NOT NULL COMMENT '소유자 ID (member.id 참조)',
    breed_id BIGINT NOT NULL COMMENT '견종 ID (breed.id 참조)',
    name VARCHAR(50) NOT NULL COMMENT '반려견 이름 (최대 10자)',
    age INT NOT NULL COMMENT '나이',
    gender VARCHAR(10) NOT NULL COMMENT '성별: MALE, FEMALE',
    size VARCHAR(10) NOT NULL COMMENT '크기: SMALL, MEDIUM, LARGE',
    mbti VARCHAR(10) COMMENT 'MBTI',
    is_neutered TINYINT(1) NOT NULL DEFAULT 0 COMMENT '중성화 여부',
    photo_url VARCHAR(500) NOT NULL COMMENT '사진 URL',
    is_main TINYINT(1) NOT NULL DEFAULT 0 COMMENT '메인 반려견 여부',
    certification_number VARCHAR(20) COMMENT '동물등록번호 (15자리)',
    is_certified TINYINT(1) NOT NULL DEFAULT 0 COMMENT '공공데이터 인증 여부',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    
    INDEX idx_member_id (member_id),
    INDEX idx_breed_id (breed_id),
    INDEX idx_member_main (member_id, is_main)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='반려견';

CREATE TABLE pet_personality (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    pet_id BIGINT NOT NULL COMMENT '반려견 ID (pet.id 참조)',
    personality_type_id BIGINT NOT NULL COMMENT '성향 ID (pet_personality_type.id 참조)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    
    INDEX idx_pet_id (pet_id),
    INDEX idx_personality_type_id (personality_type_id),
    UNIQUE KEY uk_pet_personality (pet_id, personality_type_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='반려견-성향 연결';

CREATE TABLE walking_style (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '산책 스타일 ID',
    name VARCHAR(50) NOT NULL COMMENT '스타일명',
    code VARCHAR(30) NOT NULL COMMENT '스타일 코드',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    
    UNIQUE KEY uk_name (name),
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='산책 스타일 마스터';

CREATE TABLE pet_walking_style (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    pet_id BIGINT NOT NULL COMMENT '반려견 ID (pet.id 참조)',
    walking_style_id BIGINT NOT NULL COMMENT '산책 스타일 ID (walking_style.id 참조)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    
    INDEX idx_pet_id (pet_id),
    INDEX idx_style_id (walking_style_id),
    UNIQUE KEY uk_pet_style (pet_id, walking_style_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='반려견 산책 스타일';

-- -----------------------------------------------------
-- 산책 매칭 (Walk Context)
-- -----------------------------------------------------

CREATE TABLE thread (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '스레드 ID',
    author_id BIGINT NOT NULL COMMENT '작성자 ID (member.id 참조)',
    title VARCHAR(100) NOT NULL COMMENT '제목 (최대 30자)',
    description TEXT NOT NULL COMMENT '소개글 (최대 500자)',
    walk_date DATE NOT NULL COMMENT '산책 날짜',
    start_time DATETIME NOT NULL COMMENT '시작 시간 (KST)',
    end_time DATETIME NOT NULL COMMENT '종료 시간 (KST)',
    chat_type VARCHAR(20) NOT NULL COMMENT '채팅 방식: INDIVIDUAL, GROUP',
    max_participants INT COMMENT '최대 참가자 수 (그룹: 3~10)',
    current_participants INT NOT NULL DEFAULT 0 COMMENT '현재 참가자/신청 수 (GROUP: 참가자 수, INDIVIDUAL: 활성 1:1 채팅방 수)',
    is_allow_non_pet_owner TINYINT(1) NOT NULL COMMENT '비애견인 참여 허용 여부',
    is_visible_always TINYINT(1) NOT NULL DEFAULT 1 COMMENT '항상 지도 표시 여부',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '상태: ACTIVE, CLOSED',
    place_name VARCHAR(200) NOT NULL COMMENT '장소명',
    latitude DECIMAL(10,8) NOT NULL COMMENT '위도',
    longitude DECIMAL(11,8) NOT NULL COMMENT '경도',
    address VARCHAR(500) COMMENT '주소',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    
    INDEX idx_author_id (author_id),
    INDEX idx_status (status),
    INDEX idx_status_date (status, walk_date),
    INDEX idx_location (latitude, longitude),
    INDEX idx_start_time (start_time),
    INDEX idx_author_status (author_id, status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='산책 모집 스레드';

CREATE TABLE thread_pet (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    thread_id BIGINT NOT NULL COMMENT '스레드 ID (thread.id 참조)',
    pet_id BIGINT NOT NULL COMMENT '반려견 ID (pet.id 참조)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    
    INDEX idx_thread_id (thread_id),
    INDEX idx_pet_id (pet_id),
    UNIQUE KEY uk_thread_pet (thread_id, pet_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='스레드-반려견 연결';

CREATE TABLE thread_filter (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    thread_id BIGINT NOT NULL COMMENT '스레드 ID (thread.id 참조)',
    filter_type VARCHAR(30) NOT NULL COMMENT '필터 유형: SIZE, GENDER, NEUTERED, BREED, MBTI, PERSONALITY, WALKING_STYLE',
    filter_values TEXT NOT NULL COMMENT '필터 값 목록 (JSON 배열)',
    is_required TINYINT(1) NOT NULL DEFAULT 0 COMMENT '필수 조건 여부',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    
    INDEX idx_thread_id (thread_id),
    INDEX idx_filter_type (filter_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='스레드 참가 조건 필터';

-- -----------------------------------------------------
-- 채팅 (Chat Context)
-- -----------------------------------------------------

CREATE TABLE chat_room (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '채팅방 ID',
    room_purpose VARCHAR(20) NOT NULL COMMENT '채팅방 용도: WALK, LOST_PET_MATCH',
    thread_id BIGINT COMMENT '스레드 ID (thread.id 참조, WALK 채팅방만 사용)',
    lost_pet_match_id BIGINT COMMENT '실종-제보 매칭 ID (lost_pet_match.id 참조, LOST_PET_MATCH 채팅방만 사용)',
    chat_type VARCHAR(20) NOT NULL COMMENT '채팅 방식: INDIVIDUAL, GROUP',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '상태: ACTIVE, ARCHIVED',
    archived_at DATETIME COMMENT '아카이브 시간',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    
    INDEX idx_room_purpose (room_purpose),
    INDEX idx_thread_id (thread_id),
    INDEX idx_lost_pet_match_id (lost_pet_match_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='채팅방';

CREATE TABLE chat_participant (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    chat_room_id BIGINT NOT NULL COMMENT '채팅방 ID (chat_room.id 참조)',
    member_id BIGINT NOT NULL COMMENT '회원 ID (member.id 참조)',
    is_author TINYINT(1) NOT NULL DEFAULT 0 COMMENT '작성자 여부',
    joined_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '입장 시간',
    left_at DATETIME COMMENT '퇴장 시간',
    walk_confirmed_at DATETIME COMMENT '산책 참가 확정 시간 (WALK + INDIVIDUAL 전용)',
    
    INDEX idx_chat_room_id (chat_room_id),
    INDEX idx_member_id (member_id),
    INDEX idx_room_member (chat_room_id, member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='채팅방 참여자';

CREATE TABLE chat_participant_pet (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    chat_participant_id BIGINT NOT NULL COMMENT '참여자 ID (chat_participant.id 참조)',
    pet_id BIGINT NOT NULL COMMENT '반려견 ID (pet.id 참조)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    
    INDEX idx_participant_id (chat_participant_id),
    INDEX idx_pet_id (pet_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='채팅 참여 반려견';

CREATE TABLE message (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '메시지 ID',
    chat_room_id BIGINT NOT NULL COMMENT '채팅방 ID (chat_room.id 참조)',
    sender_id BIGINT COMMENT '발신자 ID (member.id 참조, 시스템 메시지는 NULL)',
    message_type VARCHAR(20) NOT NULL COMMENT '메시지 유형: USER, SYSTEM',
    content TEXT NOT NULL COMMENT '메시지 내용 (최대 500자)',
    sent_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '전송 시간',
    
    INDEX idx_chat_room_id (chat_room_id),
    INDEX idx_sender_id (sender_id),
    INDEX idx_room_sent (chat_room_id, sent_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='채팅 메시지';

-- -----------------------------------------------------
-- 실종 반려견 찾기 (LostPet Context)
-- -----------------------------------------------------

CREATE TABLE lost_pet_report (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '실종 신고 ID',
    member_id BIGINT NOT NULL COMMENT '견주 ID (member.id 참조)',
    pet_id BIGINT NOT NULL COMMENT '반려견 ID (pet.id 참조)',
    description TEXT COMMENT '상세 설명',
    photo_urls TEXT NOT NULL COMMENT '사진 URL 목록 (JSON 배열)',
    cropped_photo_url VARCHAR(500) COMMENT 'YOLO 크롭 이미지 URL',
    image_embedding VECTOR(512) COMMENT 'CLIP 이미지 임베딩 벡터',
    text_embedding VECTOR(512) COMMENT 'CLIP 텍스트 임베딩 벡터',
    text_features TEXT COMMENT '텍스트 특징 설명',
    place_name VARCHAR(200) NOT NULL COMMENT '마지막 목격 장소명',
    last_seen_latitude DECIMAL(10,8) NOT NULL COMMENT '마지막 목격 위도',
    last_seen_longitude DECIMAL(11,8) NOT NULL COMMENT '마지막 목격 경도',
    address VARCHAR(500) COMMENT '주소',
    last_seen_at DATETIME NOT NULL COMMENT '마지막 목격 시간',
    status VARCHAR(20) NOT NULL DEFAULT 'SEARCHING' COMMENT '상태: SEARCHING, FOUND, CLOSED',
    is_notification_enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '알림 수신 여부',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    closed_at DATETIME COMMENT '종료일시',
    
    INDEX idx_member_id (member_id),
    INDEX idx_pet_id (pet_id),
    INDEX idx_status (status),
    INDEX idx_location (last_seen_latitude, last_seen_longitude),
    INDEX idx_last_seen_at (last_seen_at),
    INDEX idx_status_notification (status, is_notification_enabled)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='실종 반려견 신고';

CREATE TABLE sighting (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '제보 ID',
    finder_id BIGINT NOT NULL COMMENT '제보자 ID (member.id 참조)',
    photo_url VARCHAR(500) NOT NULL COMMENT '사진 URL',
    cropped_photo_url VARCHAR(500) COMMENT 'YOLO 크롭 이미지 URL',
    image_embedding VECTOR(512) COMMENT 'CLIP 이미지 임베딩 벡터',
    description TEXT COMMENT '설명 (선택)',
    place_name VARCHAR(200) NOT NULL COMMENT '발견 장소명',
    found_latitude DECIMAL(10,8) NOT NULL COMMENT '발견 위도',
    found_longitude DECIMAL(11,8) NOT NULL COMMENT '발견 경도',
    address VARCHAR(500) COMMENT '주소',
    found_at DATETIME NOT NULL COMMENT '발견 시간',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '상태: ACTIVE, MATCHED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    
    INDEX idx_finder_id (finder_id),
    INDEX idx_status (status),
    INDEX idx_location (found_latitude, found_longitude),
    INDEX idx_found_at (found_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='발견 제보';

CREATE TABLE lost_pet_match (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '매칭 ID',
    lost_pet_report_id BIGINT NOT NULL COMMENT '실종 신고 ID (lost_pet_report.id 참조)',
    sighting_id BIGINT NOT NULL COMMENT '제보 ID (sighting.id 참조)',
    chat_room_id BIGINT COMMENT '채팅방 ID (chat_room.id 참조)',
    similarity_score DECIMAL(5,4) NOT NULL COMMENT '종합 유사도 점수 (0.0~1.0)',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '상태: PENDING, CONFIRMED, REJECTED',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    
    INDEX idx_lost_pet_report_id (lost_pet_report_id),
    INDEX idx_sighting_id (sighting_id),
    INDEX idx_chat_room_id (chat_room_id),
    INDEX idx_status (status),
    UNIQUE KEY uk_report_sighting (lost_pet_report_id, sighting_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='실종-제보 매칭';

-- -----------------------------------------------------
-- 커뮤니티 (Community Context)
-- -----------------------------------------------------

CREATE TABLE post (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '게시물 ID',
    author_id BIGINT NOT NULL COMMENT '작성자 ID (member.id 참조)',
    content TEXT NOT NULL COMMENT '게시물 내용',
    image_urls TEXT COMMENT '이미지 URL 목록 (JSON 배열)',
    like_count INT NOT NULL DEFAULT 0 COMMENT '좋아요 수 (비정규화)',
    comment_count INT NOT NULL DEFAULT 0 COMMENT '댓글 수 (비정규화)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    
    INDEX idx_author_id (author_id),
    INDEX idx_created_at (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='커뮤니티 게시물';

CREATE TABLE comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '댓글 ID',
    post_id BIGINT NOT NULL COMMENT '게시물 ID (post.id 참조)',
    author_id BIGINT NOT NULL COMMENT '작성자 ID (member.id 참조)',
    content TEXT NOT NULL COMMENT '댓글 내용',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    
    INDEX idx_post_id (post_id),
    INDEX idx_author_id (author_id),
    INDEX idx_post_created (post_id, created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='댓글';

CREATE TABLE post_like (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '좋아요 ID',
    post_id BIGINT NOT NULL COMMENT '게시물 ID (post.id 참조)',
    member_id BIGINT NOT NULL COMMENT '회원 ID (member.id 참조)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    
    INDEX idx_post_id (post_id),
    INDEX idx_member_id (member_id),
    UNIQUE KEY uk_post_member (post_id, member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='게시물 좋아요';

-- -----------------------------------------------------
-- 차단 (Safety Context)
-- -----------------------------------------------------

CREATE TABLE block (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '차단 ID',
    blocker_id BIGINT NOT NULL COMMENT '차단자 ID (member.id 참조)',
    blocked_id BIGINT NOT NULL COMMENT '차단당한 ID (member.id 참조)',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    
    INDEX idx_blocker_id (blocker_id),
    INDEX idx_blocked_id (blocked_id),
    UNIQUE KEY uk_block (blocker_id, blocked_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='차단';

-- -----------------------------------------------------
-- 알림 (Notification Context)
-- -----------------------------------------------------

CREATE TABLE notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '알림 ID',
    member_id BIGINT NOT NULL COMMENT '수신자 ID (member.id 참조)',
    notification_type VARCHAR(30) NOT NULL COMMENT '알림 유형: CHAT_MESSAGE, WALK_APPLICATION, LOST_PET_SIMILAR',
    title VARCHAR(100) NOT NULL COMMENT '알림 제목',
    content TEXT NOT NULL COMMENT '알림 내용',
    target_type VARCHAR(30) COMMENT '대상 유형: CHAT_ROOM, THREAD, LOST_PET',
    target_id BIGINT COMMENT '대상 ID',
    is_read TINYINT(1) NOT NULL DEFAULT 0 COMMENT '읽음 여부',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    
    INDEX idx_member_id (member_id),
    INDEX idx_member_read (member_id, is_read),
    INDEX idx_created_at (created_at),
    INDEX idx_type (notification_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='알림';

CREATE TABLE notification_setting (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT 'ID',
    member_id BIGINT NOT NULL COMMENT '회원 ID (member.id 참조)',
    is_chat_message_enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '채팅 메시지 알림',
    is_walk_application_enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '산책 신청 알림',
    is_lost_pet_similar_enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '유사 제보 알림',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '생성일시',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '수정일시',
    
    UNIQUE KEY uk_member_id (member_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='알림 설정';

-- -----------------------------------------------------
-- 초기 데이터 (견종, 성향)
-- -----------------------------------------------------

-- 견종 기본 데이터
INSERT INTO breed (name, size) VALUES
('골든 리트리버', 'LARGE'),
('래브라도 리트리버', 'LARGE'),
('저먼 셰퍼드', 'LARGE'),
('불독', 'MEDIUM'),
('푸들', 'MEDIUM'),
('비글', 'MEDIUM'),
('요크셔 테리어', 'SMALL'),
('닥스훈트', 'SMALL'),
('시베리안 허스키', 'LARGE'),
('복서', 'LARGE'),
('시추', 'SMALL'),
('포메라니안', 'SMALL'),
('말티즈', 'SMALL'),
('치와와', 'SMALL'),
('웰시 코기', 'MEDIUM'),
('보더 콜리', 'MEDIUM'),
('사모예드', 'LARGE'),
('진돗개', 'LARGE'),
('풍산개', 'LARGE'),
('삽살개', 'LARGE'),
('믹스견', 'MEDIUM');

-- 성향 카테고리 기본 데이터
INSERT INTO pet_personality_type (name, code) VALUES
('소심해요', 'SHY'),
('에너지넘침', 'ENERGETIC'),
('간식좋아함', 'TREAT_LOVER'),
('사람좋아함', 'PEOPLE_LOVER'),
('친구구함', 'SEEKING_FRIENDS'),
('주인바라기', 'OWNER_FOCUSED'),
('까칠해요', 'GRUMPY');

-- 견주 성향 카테고리 기본 데이터
INSERT INTO member_personality_type (name, code) VALUES
('동네친구', 'LOCAL_FRIEND'),
('반려견정보공유', 'PET_INFO_SHARING'),
('랜선집사', 'ONLINE_PET_LOVER'),
('강아지만좋아함', 'DOG_LOVER_ONLY');

-- 산책 스타일 기본 데이터
INSERT INTO walking_style (name, code) VALUES
('전력질주', 'ENERGY_BURST'),
('냄새맡기집중', 'SNIFF_EXPLORER'),
('공원벤치휴식형', 'BENCH_REST'),
('느긋함', 'RELAXED'),
('냄새탐정', 'SNIFF_DETECTIVE'),
('무한동력', 'ENDLESS_ENERGY'),
('저질체력', 'LOW_STAMINA');

-- =====================================================
-- END OF DDL
-- =====================================================
```

---

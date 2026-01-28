# Spec Sync Rules

API 명세서, ERD, DDL 문서 간의 동기화 규칙입니다.

## 1. 엔티티 매핑 규칙

### 1.1 API Response → ERD Entity

| API Response | ERD Entity | 비고 |
|--------------|------------|------|
| MemberResponse | MEMBER | 1:1 매핑 |
| PetResponse | PET | 1:1 매핑 |
| ThreadResponse | THREAD | 1:1 매핑 |
| ChatRoomResponse | CHAT_ROOM | 1:1 매핑 |
| MessageResponse | MESSAGE | 1:1 매핑 |
| PostResponse | POST | 1:1 매핑 |
| NotificationResponse | NOTIFICATION | 1:1 매핑 |

### 1.2 중첩 객체 매핑

API Response 내의 중첩 객체는 두 가지 패턴으로 매핑:

**패턴 1: 별도 테이블 (ID 참조)**
```json
// API Response
{
  "breed": {
    "id": 15,
    "name": "포메라니안"
  }
}
```
```
ERD: PET.breed_id → BREED.id (FK 관계)
```

**패턴 2: 같은 테이블 (Embedded)**
```json
// API Response
{
  "location": {
    "placeName": "여의도 한강공원",
    "latitude": 37.5283,
    "longitude": 126.9328
  }
}
```
```
ERD: THREAD 테이블에 place_name, latitude, longitude 컬럼
```

### 1.3 배열 필드 매핑

**단순 배열 (JSON 저장)**
```json
// API
{
  "photoUrls": ["url1", "url2"]
}
```
```sql
-- DDL
photo_urls TEXT COMMENT 'JSON 배열'
```

**관계 배열 (연결 테이블)**
```json
// API
{
  "personalities": [
    {"id": 1, "name": "소심해요"},
    {"id": 3, "name": "사람좋아함"}
  ]
}
```
```
ERD: PET_PERSONALITY (pet_id, personality_type_id)
DDL: CREATE TABLE pet_personality (...)
```

## 2. 필드 타입 동기화 규칙

### 2.1 기본 타입 매핑

| API Type | Java Type | ERD Type | DDL Type |
|----------|-----------|----------|----------|
| string | String | VARCHAR(n) | VARCHAR(n) |
| string (long text) | String | TEXT | TEXT |
| integer | Integer | INT | INT |
| long (id) | Long | BIGINT | BIGINT |
| boolean | Boolean | TINYINT(1) | TINYINT(1) |
| number (decimal) | BigDecimal | DECIMAL(p,s) | DECIMAL(p,s) |

### 2.2 날짜/시간 타입 매핑

| API Format | Java Type | ERD/DDL Type |
|------------|-----------|--------------|
| ISO 8601 datetime with timezone | OffsetDateTime | DATETIME |
| ISO 8601 datetime | LocalDateTime | DATETIME |
| ISO 8601 date | LocalDate | DATE |

예시:
```
API: "2026-01-27T14:00:00+09:00"
Java: OffsetDateTime
DDL: DATETIME (MySQL은 timezone 미지원, 앱에서 처리)
```

### 2.3 Enum 타입 매핑

```
API: "MALE", "FEMALE"
Java: enum Gender { MALE, FEMALE }
ERD/DDL: VARCHAR(10) COMMENT '성별: MALE, FEMALE'
```

## 3. 필수/선택 동기화 규칙

### 3.1 API Required → DDL NOT NULL

| API 필드 | 필수 여부 | DDL |
|---------|----------|-----|
| required (O) | 필수 | NOT NULL |
| optional (X) | 선택 | nullable |

### 3.2 기본값 동기화

API에서 기본값이 정의된 경우 DDL에도 DEFAULT 설정:

```
API: memberType (기본값: NON_PET_OWNER)
DDL: member_type VARCHAR(20) NOT NULL DEFAULT 'NON_PET_OWNER'
```

## 4. 네이밍 동기화 규칙

### 4.1 API ↔ ERD/DDL 네이밍 변환

| API (camelCase) | ERD/DDL (snake_case) |
|-----------------|----------------------|
| memberId | member_id |
| profileImageUrl | profile_image_url |
| isNeutered | is_neutered |
| createdAt | created_at |
| walkDate | walk_date |
| chatType | chat_type |
| maxParticipants | max_participants |

### 4.2 테이블명 규칙

| API Context | ERD Entity | DDL Table |
|-------------|------------|-----------|
| Member | MEMBER | member |
| Pet | PET | pet |
| Thread | THREAD | thread |
| ChatRoom | CHAT_ROOM | chat_room |

## 5. 관계 동기화 규칙

### 5.1 1:N 관계

```
API: Member가 여러 Pet을 소유
ERD: MEMBER ||--o{ PET : owns
DDL: pet.member_id BIGINT (INDEX)
```

### 5.2 N:M 관계

```
API: Pet이 여러 Personality를 가짐
ERD: PET ||--o{ PET_PERSONALITY : has
     PET_PERSONALITY_TYPE ||--o{ PET_PERSONALITY : defines
DDL: pet_personality (pet_id, personality_type_id) 연결 테이블
```

### 5.3 1:1 관계

```
API: Member가 하나의 NotificationSetting을 가짐
ERD: MEMBER ||--|| NOTIFICATION_SETTING : has
DDL: notification_setting.member_id UNIQUE KEY
```

## 6. Context별 엔티티 매핑

### 6.1 Member Context

| API | ERD | DDL |
|-----|-----|-----|
| MemberResponse | MEMBER | member |
| MemberPersonalityType | MEMBER_PERSONALITY_TYPE | member_personality_type |
| - | MEMBER_PERSONALITY | member_personality |
| - | REFRESH_TOKEN | refresh_token |
| MannerScoreResponse | MANNER_SCORE | manner_score |

### 6.2 Pet Context

| API | ERD | DDL |
|-----|-----|-----|
| PetResponse | PET | pet |
| BreedResponse | BREED | breed |
| PersonalityResponse | PET_PERSONALITY_TYPE | pet_personality_type |
| - | PET_PERSONALITY | pet_personality |
| WalkingStyleResponse | WALKING_STYLE | walking_style |
| - | PET_WALKING_STYLE | pet_walking_style |

### 6.3 Thread/Walk Context

| API | ERD | DDL |
|-----|-----|-----|
| ThreadResponse | THREAD | thread |
| ThreadFilterResponse | THREAD_FILTER | thread_filter |
| - | THREAD_PET | thread_pet |

### 6.4 Chat Context

| API | ERD | DDL |
|-----|-----|-----|
| ChatRoomResponse | CHAT_ROOM | chat_room |
| ChatParticipantResponse | CHAT_PARTICIPANT | chat_participant |
| - | CHAT_PARTICIPANT_PET | chat_participant_pet |
| MessageResponse | MESSAGE | message |

### 6.5 LostPet Context

| API | ERD | DDL |
|-----|-----|-----|
| LostPetResponse | LOST_PET_REPORT | lost_pet_report |
| SightingResponse | SIGHTING | sighting |
| LostPetMatchResponse | LOST_PET_MATCH | lost_pet_match |

### 6.6 Community Context

| API | ERD | DDL |
|-----|-----|-----|
| PostResponse | POST | post |
| CommentResponse | COMMENT | comment |
| - | POST_LIKE | post_like |

### 6.7 기타

| API | ERD | DDL |
|-----|-----|-----|
| BlockResponse | BLOCK | block |
| NotificationResponse | NOTIFICATION | notification |
| NotificationSettingResponse | NOTIFICATION_SETTING | notification_setting |

## 7. 검증 체크리스트

### 7.1 API 변경 시 확인사항

- [ ] 새로운 Response 필드 → ERD 컬럼 존재 여부
- [ ] 새로운 엔티티 → ERD/DDL 테이블 존재 여부
- [ ] 필드 타입 변경 → ERD/DDL 타입 일치 여부
- [ ] 필수/선택 변경 → DDL NOT NULL/nullable 일치 여부
- [ ] 새로운 관계 → 연결 테이블 또는 FK 컬럼 존재 여부

### 7.2 ERD 변경 시 확인사항

- [ ] 새로운 엔티티 → DDL CREATE TABLE 존재 여부
- [ ] 새로운 컬럼 → DDL 컬럼 정의 존재 여부
- [ ] 새로운 관계 → DDL INDEX 존재 여부
- [ ] 타입 변경 → DDL 타입 일치 여부

### 7.3 DDL 변경 시 확인사항

- [ ] DDL만 변경하고 ERD 미반영 → ERD 업데이트 필요
- [ ] 새로운 인덱스 → ERD 인덱스 전략에 반영

## 8. 동기화 우선순위

| 우선순위 | 이슈 유형 | 설명 |
|----------|----------|------|
| Critical | 엔티티 누락 | API에 있는 엔티티가 ERD/DDL에 없음 |
| High | 필수 필드 누락 | required 필드가 DDL에 없음 |
| High | 타입 불일치 | 데이터 손실 가능한 타입 차이 |
| Medium | Nullable 불일치 | API optional vs DDL NOT NULL |
| Medium | 관계 누락 | 연결 테이블/FK 없음 |
| Low | 인덱스 누락 | 성능 관련 |
| Low | 기본값 불일치 | DEFAULT 값 차이 |

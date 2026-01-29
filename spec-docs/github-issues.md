# 아이니이누 백엔드 팀원별 상세 구현 계획

## 프로젝트 개요
- **프로젝트명**: 아이니이누 (Aini Inu) - 반려견 산책 소셜 매칭 플랫폼
- **기술 스택**: Java 21, Spring Boot 3.x, JPA, MySQL 9+
- **팀 구성**: 팀장 1명 + 숙련 개발자 2명 + 초보 개발자 2명 (총 5명)

---

## 팀원별 담당 Context

| 팀원 | 담당 Context | API 수 | 난이도 |
|------|-------------|--------|--------|
| 팀장 | Common + Chat + LostPet + Notification | 26개 | 고급 |
| 숙련 개발자 A | Member (인증, 프로필, 차단) | 12개 | 중급 |
| 숙련 개발자 B | Walk/Thread (모집글, 필터링) | 9개 | 중급 |
| 초보 개발자 C | Community (게시글, 댓글, 좋아요) | 8개 | 초급 |
| 초보 개발자 D | Pet (반려견, 견종, 성격) | 8개 | 초급 |

---

# 📌 Phase 1: Foundation (기반 구축)

## 팀장 - Common 모듈 완성

### Issue #1: Security 설정 구현
**Labels**: `priority:high`, `context:common`, `phase:1`

JWT + OAuth2 보안 설정을 구현합니다.

**구현 파일**:
- `common/config/SecurityConfig.java`
- `common/security/JwtTokenProvider.java`
- `common/security/JwtAuthenticationFilter.java`

---

### Issue #2: Location Value Object 구현
**Labels**: `priority:high`, `context:common`, `phase:1`

모든 Context에서 공유하는 위치 정보 Value Object를 구현합니다.

**필드**:
```
placeName: String (장소명)
latitude: BigDecimal (위도, precision=10, scale=8)
longitude: BigDecimal (경도, precision=11, scale=8)
address: String (주소)
```

---

## 숙련 개발자 A - Member 기반 작업

### Issue #3: Member 관련 Entity 생성
**Labels**: `priority:high`, `context:member`, `phase:1`

Member Context의 모든 Entity를 생성합니다.

**Entity 목록**:
- `Member` - 회원 정보
- `MemberPersonalityType` - 회원 성격 유형 마스터
- `MemberPersonality` - 회원-성격 연결 테이블
- `RefreshToken` - 리프레시 토큰
- `MannerScore` - 매너 점수
- `Block` - 차단

**Enum 목록**:
- `MemberType`: PET_OWNER, NON_PET_OWNER
- `MemberGender`: MALE, FEMALE, UNKNOWN
- `MemberStatus`: ACTIVE, INACTIVE, BANNED
- `SocialProvider`: NAVER, KAKAO, GOOGLE

---

### Issue #4: MemberErrorCode 정의
**Labels**: `priority:high`, `context:member`, `phase:1`

```
M001: 회원을 찾을 수 없습니다
M002: 닉네임이 유효하지 않음
M003: 이미 사용 중인 닉네임
M004: 유효하지 않은 소셜 토큰
M005: 정지된 회원
M006: 작성자에게 차단됨
```

---

## 숙련 개발자 B - Walk 기반 작업

### Issue #5: Walk 관련 Enum 정의
**Labels**: `priority:medium`, `context:walk`, `phase:1`

Walk Context에서 사용하는 Enum을 정의합니다.

**Enum 목록**:
- `ChatType`: INDIVIDUAL, GROUP (Chat Context와 공유)
- `ThreadStatus`: ACTIVE, CLOSED
- `FilterType`: SIZE, GENDER, NEUTERED, BREED, MBTI, PERSONALITY, WALKING_STYLE

---

## 초보 개발자 C - Community 기반 작업

### Issue #6: Post Entity 완성
**Labels**: `priority:medium`, `context:community`, `phase:1`, `good-first-issue`

기존 스켈레톤 코드를 기반으로 Post Entity를 완성합니다.

**필드**:
```
id: Long (PK)
authorId: Long (작성자 Member ID)
content: String (내용, max 2000자)
imageUrls: String (JSON 배열, TEXT)
likeCount: int (좋아요 수, default 0)
commentCount: int (댓글 수, default 0)
createdAt: LocalDateTime
updatedAt: LocalDateTime
```

---

## 초보 개발자 D - Pet 기반 작업

### Issue #7: 마스터 데이터 Entity 생성
**Labels**: `priority:medium`, `context:pet`, `phase:1`, `good-first-issue`

Pet Context의 마스터 데이터 Entity를 생성합니다.

**Entity 목록**:
- `Breed` - 견종 (name, size)
- `PetPersonalityType` - 반려견 성격 유형 (name, code)
- `WalkingStyle` - 산책 스타일 (name, code)

**PetSize Enum**: SMALL, MEDIUM, LARGE

---

# 📌 Phase 2: Core APIs (핵심 API 구현)

## 숙련 개발자 A - Auth & Member APIs

### Issue #8: 소셜 로그인 API
**Labels**: `priority:critical`, `context:member`, `phase:2`

#### `POST /auth/login/{provider}`
소셜 로그인을 처리합니다.

**Path Parameters**:
| 이름 | 타입 | 설명 |
|------|------|------|
| provider | String | naver, kakao, google |

**Request Body**:
```json
{
  "accessToken": "소셜 제공자로부터 받은 액세스 토큰"
}
```

**Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "accessToken": "JWT 액세스 토큰",
    "refreshToken": "JWT 리프레시 토큰",
    "isNewMember": true,
    "member": {
      "id": 1,
      "nickname": "홍길동",
      "profileImageUrl": "https://...",
      "memberType": "NON_PET_OWNER",
      "mannerTemperature": 5.0
    }
  },
  "error": null
}
```

---

### Issue #9: 토큰 갱신 API
**Labels**: `priority:critical`, `context:member`, `phase:2`

#### `POST /auth/refresh`
액세스 토큰을 갱신합니다.

**Request Body**:
```json
{
  "refreshToken": "리프레시 토큰"
}
```

**Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "accessToken": "새로운 JWT 액세스 토큰",
    "refreshToken": "새로운 JWT 리프레시 토큰"
  },
  "error": null
}
```

---

### Issue #10: 로그아웃 API
**Labels**: `priority:high`, `context:member`, `phase:2`

#### `POST /auth/logout`
로그아웃하고 리프레시 토큰을 무효화합니다.

**Headers**: `Authorization: Bearer {accessToken}`

**Response (200 OK)**:
```json
{
  "success": true,
  "data": null,
  "error": null
}
```

---

### Issue #11: 회원가입 완료 API
**Labels**: `priority:critical`, `context:member`, `phase:2`

#### `POST /members/profile`
소셜 로그인 후 프로필 정보를 설정하여 회원가입을 완료합니다.

**Headers**: `Authorization: Bearer {accessToken}`

**Request Body**:
```json
{
  "nickname": "강아지좋아",
  "profileImageUrl": "https://s3.../profile.jpg",
  "gender": "MALE",
  "birthYear": 1995,
  "personalityTypeIds": [1, 3]
}
```

**Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "nickname": "강아지좋아",
    "profileImageUrl": "https://...",
    "gender": "MALE",
    "birthYear": 1995,
    "memberType": "NON_PET_OWNER",
    "mannerTemperature": 5.0,
    "personalityTypes": [
      {"id": 1, "name": "동네 친구 찾기"},
      {"id": 3, "name": "온라인 애견인"}
    ]
  },
  "error": null
}
```

**Validation**:
- nickname: 최대 10자, 중복 불가

---

### Issue #12: 내 프로필 조회 API
**Labels**: `priority:high`, `context:member`, `phase:2`

#### `GET /members/me`
내 프로필 정보를 조회합니다 (반려견 목록 포함).

**Headers**: `Authorization: Bearer {accessToken}`

**Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "nickname": "강아지좋아",
    "profileImageUrl": "https://...",
    "gender": "MALE",
    "birthYear": 1995,
    "memberType": "PET_OWNER",
    "mannerTemperature": 7.5,
    "personalityTypes": [...],
    "pets": [
      {
        "id": 1,
        "name": "뽀삐",
        "breedName": "포메라니안",
        "age": 3,
        "photoUrl": "https://...",
        "isMain": true
      }
    ]
  },
  "error": null
}
```

---

### Issue #13: 프로필 수정 API
**Labels**: `priority:high`, `context:member`, `phase:2`

#### `PATCH /members/me`
내 프로필 정보를 수정합니다.

**Headers**: `Authorization: Bearer {accessToken}`

**Request Body** (부분 수정 가능):
```json
{
  "nickname": "새닉네임",
  "profileImageUrl": "https://...",
  "personalityTypeIds": [1, 2]
}
```

**Response (200 OK)**: 수정된 전체 프로필 반환

---

### Issue #14: 다른 회원 프로필 조회 API
**Labels**: `priority:medium`, `context:member`, `phase:2`

#### `GET /members/{memberId}`
다른 회원의 프로필을 조회합니다.

**Path Parameters**:
| 이름 | 타입 | 설명 |
|------|------|------|
| memberId | Long | 조회할 회원 ID |

**Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "id": 2,
    "nickname": "산책마스터",
    "profileImageUrl": "https://...",
    "memberType": "PET_OWNER",
    "mannerTemperature": 8.2,
    "personalityTypes": [...],
    "pets": [...]
  },
  "error": null
}
```

---

### Issue #15: 회원 성격 유형 목록 API
**Labels**: `priority:low`, `context:member`, `phase:2`

#### `GET /member-personality-types`
회원 성격 유형 마스터 데이터를 조회합니다.

**Response (200 OK)**:
```json
{
  "success": true,
  "data": [
    {"id": 1, "name": "동네 친구 찾기", "code": "LOCAL_FRIEND"},
    {"id": 2, "name": "애견 정보 공유", "code": "PET_INFO_SHARING"},
    {"id": 3, "name": "온라인 애견인", "code": "ONLINE_PET_LOVER"},
    {"id": 4, "name": "강아지만", "code": "DOG_LOVER_ONLY"}
  ],
  "error": null
}
```

---

## 숙련 개발자 B - Thread APIs

### Issue #16: Thread Entity 생성
**Labels**: `priority:high`, `context:walk`, `phase:2`

Thread Context의 Entity를 생성합니다.

**Entity 목록**:
- `Thread` - 산책 모집글
- `ThreadPet` - 모집글-반려견 연결
- `ThreadFilter` - 참여 조건 필터

**ThreadErrorCode**:
```
T001: 스레드를 찾을 수 없습니다
T002: 이미 활성 스레드가 존재함
T003: 스레드 작성자가 아님
T004: 스레드 종료됨
T005: 정원 초과
T006: 비애견인 참여가 허용되지 않음
T007: 필수 필터 조건 미충족
T008: 유효하지 않은 산책 시간
T009: 종료 시간이 시작 시간보다 이전
T010: 참가자 수 범위 오류 (3~10)
T011: 필수 필터 3개 초과
T012: 비애견인은 스레드 작성 불가
T013: 이미 신청함
```

---

### Issue #17: 스레드 생성 API
**Labels**: `priority:critical`, `context:walk`, `phase:2`

#### `POST /threads`
산책 모집글을 생성합니다.

**Headers**: `Authorization: Bearer {accessToken}`

**Request Body**:
```json
{
  "title": "저녁 산책 같이해요",
  "description": "한강공원에서 같이 산책해요!",
  "walkDate": "2025-02-01",
  "startTime": "2025-02-01T18:00:00",
  "endTime": "2025-02-01T19:30:00",
  "chatType": "GROUP",
  "maxParticipants": 5,
  "allowNonPetOwner": false,
  "location": {
    "placeName": "여의도 한강공원",
    "latitude": 37.5283,
    "longitude": 126.9322,
    "address": "서울 영등포구 여의동로 330"
  },
  "petIds": [1, 2],
  "filters": [
    {"filterType": "SIZE", "values": ["SMALL", "MEDIUM"], "isRequired": true},
    {"filterType": "GENDER", "values": ["FEMALE"], "isRequired": false}
  ]
}
```

**Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "저녁 산책 같이해요",
    "author": {
      "id": 1,
      "nickname": "강아지좋아",
      "profileImageUrl": "https://...",
      "mannerTemperature": 7.5
    },
    "walkDate": "2025-02-01",
    "startTime": "2025-02-01T18:00:00",
    "endTime": "2025-02-01T19:30:00",
    "chatType": "GROUP",
    "maxParticipants": 5,
    "currentParticipants": 1,
    "location": {...},
    "pets": [...],
    "filters": [...],
    "status": "ACTIVE",
    "createdAt": "2025-01-29T10:00:00"
  },
  "error": null
}
```

**비즈니스 규칙**:
- 사용자당 활성 스레드 최대 1개
- 산책 일정: 오늘 ~ +7일
- 그룹 채팅: 3~10명
- 필수 필터: 최대 3개
- 비애견인(NON_PET_OWNER)은 스레드 생성 불가

---

### Issue #18: 스레드 목록 조회 API
**Labels**: `priority:critical`, `context:walk`, `phase:2`

#### `GET /threads`
스레드 목록을 조회합니다 (필터링, 정렬, 페이지네이션).

**Query Parameters**:
| 이름 | 타입 | 필수 | 설명 |
|------|------|------|------|
| startDate | LocalDate | N | 시작 날짜 |
| endDate | LocalDate | N | 종료 날짜 |
| startHour | Integer | N | 시작 시간 (0-23) |
| endHour | Integer | N | 종료 시간 (0-23) |
| chatType | String | N | INDIVIDUAL, GROUP |
| latitude | BigDecimal | N | 현재 위치 위도 |
| longitude | BigDecimal | N | 현재 위치 경도 |
| radiusKm | Integer | N | 검색 반경 (km) |
| sort | String | N | DISTANCE, CREATED_AT, START_TIME |
| page | Integer | N | 페이지 번호 (default: 0) |
| size | Integer | N | 페이지 크기 (default: 20) |

**Response (200 OK)** - SliceResponse:
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "title": "저녁 산책 같이해요",
        "author": {...},
        "walkDate": "2025-02-01",
        "startTime": "18:00",
        "endTime": "19:30",
        "chatType": "GROUP",
        "maxParticipants": 5,
        "currentParticipants": 3,
        "location": {...},
        "distanceKm": 1.2,
        "mainPet": {...}
      }
    ],
    "hasNext": true,
    "size": 20,
    "number": 0
  },
  "error": null
}
```

**필터링 규칙**:
- 필수 필터 미충족 시 목록에서 제외
- 차단한/된 사용자 스레드 제외
- 비애견인: `allowNonPetOwner=true` && 반려견 관련 필수 필터 없는 스레드만

---

### Issue #19: 스레드 상세 조회 API
**Labels**: `priority:high`, `context:walk`, `phase:2`

#### `GET /threads/{threadId}`
스레드 상세 정보를 조회합니다.

**Path Parameters**:
| 이름 | 타입 | 설명 |
|------|------|------|
| threadId | Long | 스레드 ID |

**Response (200 OK)**: 스레드 전체 정보 + 참여자 목록

---

### Issue #20: 스레드 수정 API
**Labels**: `priority:medium`, `context:walk`, `phase:2`

#### `PATCH /threads/{threadId}`
스레드 정보를 수정합니다 (작성자만 가능).

**Request Body** (부분 수정 가능):
```json
{
  "title": "수정된 제목",
  "description": "수정된 설명",
  "endTime": "2025-02-01T20:00:00"
}
```

---

### Issue #21: 스레드 삭제 API
**Labels**: `priority:medium`, `context:walk`, `phase:2`

#### `DELETE /threads/{threadId}`
스레드를 삭제합니다 (작성자만 가능).

---

### Issue #22: 스레드 신청 API
**Labels**: `priority:critical`, `context:walk`, `phase:2`

#### `POST /threads/{threadId}/apply`
스레드에 참가 신청합니다. 신청 즉시 채팅방이 생성됩니다.

**Request Body**:
```json
{
  "petIds": [1],
  "message": "같이 산책해요!"
}
```

**Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "chatRoomId": 1,
    "chatType": "INDIVIDUAL"
  },
  "error": null
}
```

**비즈니스 규칙**:
- INDIVIDUAL: 작성자와 1:1 채팅방 생성
- GROUP: 기존 그룹 채팅방에 참여
- 필수 필터 검증 (메인 반려견 기준)
- 그룹 채팅 정원 초과 시 에러

---

### Issue #23: 스레드 신청 취소 API
**Labels**: `priority:medium`, `context:walk`, `phase:2`

#### `DELETE /threads/{threadId}/apply`
스레드 참가를 취소합니다.

---

### Issue #24: 지도용 스레드 조회 API
**Labels**: `priority:medium`, `context:walk`, `phase:2`

#### `GET /threads/map`
지도에 표시할 스레드 마커 목록을 조회합니다.

**Query Parameters**:
| 이름 | 타입 | 필수 | 설명 |
|------|------|------|------|
| swLat | BigDecimal | Y | 남서쪽 위도 |
| swLng | BigDecimal | Y | 남서쪽 경도 |
| neLat | BigDecimal | Y | 북동쪽 위도 |
| neLng | BigDecimal | Y | 북동쪽 경도 |

**Response (200 OK)**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "latitude": 37.5283,
      "longitude": 126.9322,
      "title": "저녁 산책",
      "chatType": "GROUP",
      "currentParticipants": 3,
      "maxParticipants": 5
    }
  ],
  "error": null
}
```

---

### Issue #25: 중복 스레드 확인 API
**Labels**: `priority:low`, `context:walk`, `phase:2`

#### `GET /threads/check-duplicate`
비슷한 시간/장소에 내 활성 스레드가 있는지 확인합니다.

---

## 초보 개발자 C - Community APIs

### Issue #26: Comment, PostLike Entity 생성
**Labels**: `priority:high`, `context:community`, `phase:2`, `good-first-issue`

**Entity 목록**:
- `Comment` - 댓글 (postId, authorId, content)
- `PostLike` - 좋아요 (postId, memberId, unique constraint)

**CommunityErrorCode**:
```
CO001: 게시물을 찾을 수 없습니다
CO002: 게시물 작성자가 아닙니다
CO003: 댓글을 찾을 수 없습니다
CO004: 댓글 작성자가 아닙니다
CO005: 내용이 너무 깁니다
```

---

### Issue #27: 게시물 생성 API
**Labels**: `priority:high`, `context:community`, `phase:2`, `good-first-issue`

#### `POST /posts`
게시물을 생성합니다.

**Headers**: `Authorization: Bearer {accessToken}`

**Request Body**:
```json
{
  "content": "오늘 뽀삐랑 산책했어요!",
  "imageUrls": ["https://s3.../image1.jpg", "https://s3.../image2.jpg"]
}
```

**Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "author": {
      "id": 1,
      "nickname": "강아지좋아",
      "profileImageUrl": "https://..."
    },
    "content": "오늘 뽀삐랑 산책했어요!",
    "imageUrls": ["https://..."],
    "likeCount": 0,
    "commentCount": 0,
    "isLiked": false,
    "createdAt": "2025-01-29T10:00:00"
  },
  "error": null
}
```

**Validation**:
- content: 최대 2000자

---

### Issue #28: 게시물 목록 조회 API
**Labels**: `priority:high`, `context:community`, `phase:2`, `good-first-issue`

#### `GET /posts`
게시물 목록을 조회합니다 (무한 스크롤).

**Query Parameters**:
| 이름 | 타입 | 필수 | 설명 |
|------|------|------|------|
| page | Integer | N | 페이지 번호 (default: 0) |
| size | Integer | N | 페이지 크기 (default: 20) |

**Response (200 OK)** - SliceResponse:
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "author": {...},
        "content": "오늘 뽀삐랑...",
        "imageUrls": [...],
        "likeCount": 10,
        "commentCount": 5,
        "isLiked": true,
        "createdAt": "2025-01-29T10:00:00"
      }
    ],
    "hasNext": true,
    "size": 20,
    "number": 0
  },
  "error": null
}
```

---

### Issue #29: 게시물 상세 조회 API
**Labels**: `priority:high`, `context:community`, `phase:2`, `good-first-issue`

#### `GET /posts/{postId}`
게시물 상세를 조회합니다 (댓글 포함).

**Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "author": {...},
    "content": "...",
    "imageUrls": [...],
    "likeCount": 10,
    "commentCount": 5,
    "isLiked": true,
    "comments": [
      {
        "id": 1,
        "author": {...},
        "content": "귀여워요!",
        "createdAt": "2025-01-29T10:30:00"
      }
    ],
    "createdAt": "2025-01-29T10:00:00"
  },
  "error": null
}
```

---

### Issue #30: 게시물 수정 API
**Labels**: `priority:medium`, `context:community`, `phase:2`, `good-first-issue`

#### `PATCH /posts/{postId}`
게시물을 수정합니다 (작성자만 가능).

---

### Issue #31: 게시물 삭제 API
**Labels**: `priority:medium`, `context:community`, `phase:2`, `good-first-issue`

#### `DELETE /posts/{postId}`
게시물을 삭제합니다 (작성자만 가능).

---

### Issue #32: 좋아요 토글 API
**Labels**: `priority:high`, `context:community`, `phase:2`, `good-first-issue`

#### `POST /posts/{postId}/like`
좋아요를 토글합니다 (이미 좋아요 상태면 취소).

**Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "isLiked": true,
    "likeCount": 11
  },
  "error": null
}
```

---

### Issue #33: 댓글 작성 API
**Labels**: `priority:high`, `context:community`, `phase:2`, `good-first-issue`

#### `POST /posts/{postId}/comments`
댓글을 작성합니다.

**Request Body**:
```json
{
  "content": "귀여워요!"
}
```

**Validation**:
- content: 최대 500자

---

### Issue #34: 댓글 삭제 API
**Labels**: `priority:medium`, `context:community`, `phase:2`, `good-first-issue`

#### `DELETE /posts/{postId}/comments/{commentId}`
댓글을 삭제합니다 (작성자만 가능).

---

## 초보 개발자 D - Pet APIs

### Issue #35: Pet Entity 완성
**Labels**: `priority:high`, `context:pet`, `phase:2`, `good-first-issue`

**Entity 목록**:
- `Pet` - 반려견 정보
- `PetPersonality` - 반려견-성격 연결 테이블
- `PetWalkingStyle` - 반려견-산책스타일 연결 테이블

**Enum 목록**:
- `PetGender`: MALE, FEMALE
- `PetSize`: SMALL, MEDIUM, LARGE

**PetErrorCode**:
```
P001: 반려견을 찾을 수 없습니다
P002: 등록 가능 마릿수(10) 초과
P003: 이미 메인 반려견입니다
P004: 존재하지 않는 견종
P006: 본인 소유 반려견이 아님
P007: 반려견 이름이 유효하지 않음
P008: 반려견 이름 길이 초과 (최대 10자)
P009: 동물등록번호 검증 실패
```

---

### Issue #36: 반려견 등록 API
**Labels**: `priority:critical`, `context:pet`, `phase:2`, `good-first-issue`

#### `POST /pets`
반려견을 등록합니다.

**Headers**: `Authorization: Bearer {accessToken}`

**Request Body**:
```json
{
  "breedId": 1,
  "name": "뽀삐",
  "age": 3,
  "gender": "FEMALE",
  "size": "SMALL",
  "mbti": "ENFP",
  "isNeutered": true,
  "photoUrl": "https://s3.../pet.jpg",
  "isMain": true,
  "personalityIds": [1, 3, 5],
  "walkingStyleCodes": ["ENERGY_BURST", "SNIFF_EXPLORER"],
  "certificationNumber": "410123456789012"
}
```

**Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "name": "뽀삐",
    "breed": {
      "id": 1,
      "name": "포메라니안",
      "size": "SMALL"
    },
    "age": 3,
    "gender": "FEMALE",
    "size": "SMALL",
    "mbti": "ENFP",
    "isNeutered": true,
    "photoUrl": "https://...",
    "isMain": true,
    "isCertified": false,
    "personalities": [
      {"id": 1, "name": "소심쟁이"},
      {"id": 3, "name": "간식러버"}
    ],
    "walkingStyles": [
      {"code": "ENERGY_BURST", "name": "에너지 폭발"}
    ]
  },
  "error": null
}
```

**비즈니스 규칙**:
- 회원당 최대 10마리
- 첫 번째 반려견은 자동으로 메인
- 첫 등록 시 회원 타입 PET_OWNER로 변경
- 이름 최대 10자

---

### Issue #37: 내 반려견 목록 조회 API
**Labels**: `priority:high`, `context:pet`, `phase:2`, `good-first-issue`

#### `GET /pets`
내 반려견 목록을 조회합니다.

**Response (200 OK)**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "name": "뽀삐",
      "breed": {...},
      "age": 3,
      "photoUrl": "https://...",
      "isMain": true
    },
    {
      "id": 2,
      "name": "몽이",
      "breed": {...},
      "age": 5,
      "photoUrl": "https://...",
      "isMain": false
    }
  ],
  "error": null
}
```

---

### Issue #38: 반려견 수정 API
**Labels**: `priority:medium`, `context:pet`, `phase:2`, `good-first-issue`

#### `PATCH /pets/{petId}`
반려견 정보를 수정합니다.

**Request Body** (부분 수정 가능):
```json
{
  "name": "뽀삐2",
  "age": 4,
  "personalityIds": [1, 2],
  "walkingStyleCodes": ["RELAXED"]
}
```

---

### Issue #39: 반려견 삭제 API
**Labels**: `priority:medium`, `context:pet`, `phase:2`, `good-first-issue`

#### `DELETE /pets/{petId}`
반려견을 삭제합니다.

**비즈니스 규칙**:
- 마지막 반려견 삭제 시 회원 타입 NON_PET_OWNER로 변경
- 메인 반려견 삭제 시 다른 반려견이 메인으로 자동 지정

---

### Issue #40: 메인 반려견 변경 API
**Labels**: `priority:medium`, `context:pet`, `phase:2`, `good-first-issue`

#### `PATCH /pets/{petId}/main`
메인 반려견을 변경합니다.

---

### Issue #41: 견종 목록 조회 API
**Labels**: `priority:high`, `context:pet`, `phase:2`, `good-first-issue`

#### `GET /breeds`
견종 목록을 조회합니다.

**Query Parameters**:
| 이름 | 타입 | 필수 | 설명 |
|------|------|------|------|
| name | String | N | 견종명 검색 |
| size | String | N | SMALL, MEDIUM, LARGE |

**Response (200 OK)**:
```json
{
  "success": true,
  "data": [
    {"id": 1, "name": "포메라니안", "size": "SMALL"},
    {"id": 2, "name": "말티즈", "size": "SMALL"},
    {"id": 3, "name": "골든 리트리버", "size": "LARGE"}
  ],
  "error": null
}
```

---

### Issue #42: 반려견 성격 유형 목록 API
**Labels**: `priority:medium`, `context:pet`, `phase:2`, `good-first-issue`

#### `GET /personalities`
반려견 성격 유형 마스터 데이터를 조회합니다.

**Response (200 OK)**:
```json
{
  "success": true,
  "data": [
    {"id": 1, "name": "소심쟁이", "code": "SHY"},
    {"id": 2, "name": "에너자이저", "code": "ENERGETIC"},
    {"id": 3, "name": "간식러버", "code": "TREAT_LOVER"},
    {"id": 4, "name": "사람좋아", "code": "PEOPLE_LOVER"},
    {"id": 5, "name": "친구찾아", "code": "SEEKING_FRIENDS"},
    {"id": 6, "name": "주인바라기", "code": "OWNER_FOCUSED"},
    {"id": 7, "name": "까칠이", "code": "GRUMPY"}
  ],
  "error": null
}
```

---

### Issue #43: 산책 스타일 목록 API
**Labels**: `priority:medium`, `context:pet`, `phase:2`, `good-first-issue`

#### `GET /walking-styles`
산책 스타일 마스터 데이터를 조회합니다.

**Response (200 OK)**:
```json
{
  "success": true,
  "data": [
    {"code": "ENERGY_BURST", "name": "에너지 폭발"},
    {"code": "SNIFF_EXPLORER", "name": "냄새 탐험가"},
    {"code": "BENCH_REST", "name": "벤치 휴식"},
    {"code": "RELAXED", "name": "여유로운 산책"},
    {"code": "SNIFF_DETECTIVE", "name": "냄새 탐정"},
    {"code": "ENDLESS_ENERGY", "name": "지칠 줄 모르는"},
    {"code": "LOW_STAMINA", "name": "체력 부족"}
  ],
  "error": null
}
```

---

# 📌 Phase 3: Advanced Features (고급 기능)

## 숙련 개발자 A - Block APIs

### Issue #44: 차단 API
**Labels**: `priority:medium`, `context:member`, `phase:3`

#### `POST /blocks`
회원을 차단합니다.

**Request Body**:
```json
{
  "targetMemberId": 5
}
```

**Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "id": 1,
    "blockedMember": {
      "id": 5,
      "nickname": "차단할사람"
    },
    "createdAt": "2025-01-29T10:00:00"
  },
  "error": null
}
```

**비즈니스 규칙**:
- 차단한 사용자의 스레드 목록에서 제외
- 차단한 사용자는 내 스레드에 신청 불가

---

### Issue #45: 차단 목록 조회 API
**Labels**: `priority:medium`, `context:member`, `phase:3`

#### `GET /blocks`
차단한 회원 목록을 조회합니다.

---

### Issue #46: 차단 해제 API
**Labels**: `priority:medium`, `context:member`, `phase:3`

#### `DELETE /blocks/{blockId}`
차단을 해제합니다.

---

## 팀장 - Chat APIs

### Issue #47: Chat Entity 생성
**Labels**: `priority:high`, `context:chat`, `phase:3`

**Entity 목록**:
- `ChatRoom` - 채팅방 (roomPurpose, threadId, chatType, status)
- `ChatParticipant` - 참여자 (chatRoomId, memberId, walkConfirmedAt)
- `ChatParticipantPet` - 참여자 반려견
- `Message` - 메시지 (chatRoomId, senderId, content)

**Enum 목록**:
- `RoomPurpose`: WALK, LOST_PET_MATCH
- `ChatRoomStatus`: ACTIVE, ARCHIVED
- `MessageType`: USER, SYSTEM

**ChatErrorCode**:
```
CH001: 메시지 길이 초과 (500자)
CH002: 아카이브된 채팅방
CH003: 채팅방 참여자가 아님
CH004: 산책 채팅방이 아님
CH005: 1:1 채팅방이 아님
CH006: 이미 산책 확정함
CH007: 작성자가 다른 채팅방 확정함
CH008: 아직 산책 확정하지 않음
CH009: 상대가 이미 확정하여 취소 불가
CH010: 점수 범위 오류 (1~10)
CH011: 대상 회원이 유효하지 않음
CH012: 산책 참가 확정이 필요함
CH013: 산책이 아직 종료되지 않음
CH014: 이미 후기 작성함
```

---

### Issue #48: 채팅방 목록 조회 API
**Labels**: `priority:high`, `context:chat`, `phase:3`

#### `GET /chat-rooms`
내 채팅방 목록을 조회합니다.

**Query Parameters**:
| 이름 | 타입 | 필수 | 설명 |
|------|------|------|------|
| status | String | N | ACTIVE, ARCHIVED |

**Response (200 OK)**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "roomPurpose": "WALK",
      "chatType": "GROUP",
      "status": "ACTIVE",
      "thread": {
        "id": 1,
        "title": "저녁 산책",
        "walkDate": "2025-02-01"
      },
      "participantCount": 3,
      "lastMessage": {
        "content": "내일 봐요!",
        "sentAt": "2025-01-29T20:00:00"
      },
      "unreadCount": 2
    }
  ],
  "error": null
}
```

---

### Issue #49: 채팅방 상세 조회 API
**Labels**: `priority:high`, `context:chat`, `phase:3`

#### `GET /chat-rooms/{chatRoomId}`
채팅방 상세 정보를 조회합니다.

---

### Issue #50: 메시지 목록 조회 API
**Labels**: `priority:high`, `context:chat`, `phase:3`

#### `GET /chat-rooms/{chatRoomId}/messages`
메시지 목록을 조회합니다 (커서 기반 페이지네이션).

**Query Parameters**:
| 이름 | 타입 | 필수 | 설명 |
|------|------|------|------|
| cursor | Long | N | 기준 메시지 ID |
| size | Integer | N | 조회 개수 (default: 50) |
| direction | String | N | BEFORE, AFTER (default: BEFORE) |

**Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "messages": [
      {
        "id": 100,
        "sender": {
          "id": 1,
          "nickname": "강아지좋아",
          "profileImageUrl": "https://..."
        },
        "messageType": "USER",
        "content": "안녕하세요!",
        "sentAt": "2025-01-29T10:00:00"
      }
    ],
    "hasMore": true,
    "nextCursor": 50
  },
  "error": null
}
```

---

### Issue #51: 메시지 전송 API
**Labels**: `priority:high`, `context:chat`, `phase:3`

#### `POST /chat-rooms/{chatRoomId}/messages`
메시지를 전송합니다.

**Request Body**:
```json
{
  "content": "안녕하세요!"
}
```

**Validation**:
- content: 최대 500자
- 아카이브된 채팅방 전송 불가

---

### Issue #52: 채팅방 나가기 API
**Labels**: `priority:medium`, `context:chat`, `phase:3`

#### `DELETE /chat-rooms/{chatRoomId}/leave`
채팅방을 나갑니다.

---

### Issue #53: 산책 확정 API
**Labels**: `priority:high`, `context:chat`, `phase:3`

#### `POST /chat-rooms/{chatRoomId}/walk-confirm`
1:1 채팅에서 산책을 확정합니다.

**비즈니스 규칙**:
- 1:1 채팅방만 가능
- 양쪽 모두 확정해야 산책 성립
- 작성자는 하나의 1:1 채팅방만 확정 가능

---

### Issue #54: 산책 확정 상태 조회 API
**Labels**: `priority:medium`, `context:chat`, `phase:3`

#### `GET /chat-rooms/{chatRoomId}/walk-confirm`
산책 확정 상태를 조회합니다.

**Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "myConfirmed": true,
    "partnerConfirmed": false,
    "fullyConfirmed": false
  },
  "error": null
}
```

---

### Issue #55: 산책 확정 취소 API
**Labels**: `priority:medium`, `context:chat`, `phase:3`

#### `DELETE /chat-rooms/{chatRoomId}/walk-confirm`
산책 확정을 취소합니다.

**비즈니스 규칙**:
- 상대방이 아직 확정하지 않은 경우만 취소 가능

---

### Issue #56: 매너 후기 작성 API
**Labels**: `priority:high`, `context:chat`, `phase:3`

#### `POST /chat-rooms/{chatRoomId}/reviews`
매너 후기를 작성합니다.

**Request Body**:
```json
{
  "targetMemberId": 2,
  "score": 8
}
```

**비즈니스 규칙**:
- 그룹 채팅: 누구나 누구에게나 작성 가능
- 1:1 채팅: 양쪽 확정 후에만 작성 가능
- 산책 종료 시간 이후에만 작성 가능
- 점수: 1~10점
- 중복 작성 불가

---

### Issue #57: 내 후기 조회 API
**Labels**: `priority:low`, `context:chat`, `phase:3`

#### `GET /chat-rooms/{chatRoomId}/reviews/me`
이 채팅방에서 내가 작성한 후기를 조회합니다.

---

## 팀장 - LostPet APIs

### Issue #58: LostPet Entity 생성
**Labels**: `priority:high`, `context:lostpet`, `phase:3`

**Entity 목록**:
- `LostPetReport` - 실종 신고 (memberId, petId, embeddings, location)
- `Sighting` - 제보 (finderId, embedding, location, foundAt)
- `LostPetMatch` - 매칭 (lostPetReportId, sightingId, similarityScore)

**Enum 목록**:
- `LostPetStatus`: SEARCHING, FOUND, CLOSED
- `SightingStatus`: ACTIVE, MATCHED
- `MatchStatus`: PENDING, CONFIRMED, REJECTED

**LostPetErrorCode**:
```
L001: 사진에서 강아지가 감지되지 않음
L002: 이미 실종 신고된 반려견
L003: 본인 제보가 아님
L004: 제보를 찾을 수 없음
```

---

### Issue #59: 실종 신고 API
**Labels**: `priority:high`, `context:lostpet`, `phase:3`

#### `POST /lost-pets`
실종 신고를 등록합니다.

**Request Body**:
```json
{
  "petId": 1,
  "description": "포메라니안, 흰색, 빨간 목줄",
  "photoUrls": ["https://s3.../lost1.jpg"],
  "location": {
    "placeName": "여의도 한강공원",
    "latitude": 37.5283,
    "longitude": 126.9322,
    "address": "서울 영등포구"
  },
  "lostAt": "2025-01-28T15:00:00"
}
```

**비즈니스 규칙**:
- 사진에서 YOLOv8로 강아지 감지
- CLIP으로 멀티모달 임베딩 생성
- 동일 반려견 중복 신고 불가

---

### Issue #60: 내 실종 신고 목록 API
**Labels**: `priority:medium`, `context:lostpet`, `phase:3`

#### `GET /lost-pets/mine`
내 실종 신고 목록을 조회합니다.

---

### Issue #61: 실종 신고 상세 API
**Labels**: `priority:medium`, `context:lostpet`, `phase:3`

#### `GET /lost-pets/{lostPetId}`
실종 신고 상세를 조회합니다.

---

### Issue #62: 유사 제보 목록 API
**Labels**: `priority:high`, `context:lostpet`, `phase:3`

#### `GET /lost-pets/{lostPetId}/similar-sightings`
유사한 제보 목록을 조회합니다.

**Response (200 OK)**:
```json
{
  "success": true,
  "data": [
    {
      "id": 1,
      "photoUrl": "https://...",
      "location": {...},
      "foundAt": "2025-01-29T10:00:00",
      "similarityScore": 0.87,
      "distanceKm": 1.2
    }
  ],
  "error": null
}
```

**유사도 계산**:
- 이미지 유사도: 50%
- 위치 근접도: 30%
- 시간 근접도: 20%

---

### Issue #63: 실종견 매칭 API
**Labels**: `priority:high`, `context:lostpet`, `phase:3`

#### `POST /lost-pets/{lostPetId}/match`
"이 아이가 맞아요!" 매칭을 생성합니다.

**Request Body**:
```json
{
  "sightingId": 1
}
```

**Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "matchId": 1,
    "chatRoomId": 10
  },
  "error": null
}
```

**비즈니스 규칙**:
- 자동으로 1:1 채팅방 생성 (roomPurpose: LOST_PET_MATCH)

---

### Issue #64: 실종 신고 종료 API
**Labels**: `priority:medium`, `context:lostpet`, `phase:3`

#### `PATCH /lost-pets/{lostPetId}/close`
실종 신고를 종료합니다.

**Request Body**:
```json
{
  "status": "FOUND"
}
```

---

### Issue #65: 제보 등록 API
**Labels**: `priority:high`, `context:lostpet`, `phase:3`

#### `POST /sightings`
발견 제보를 등록합니다.

**Request Body**:
```json
{
  "photoUrl": "https://s3.../sighting.jpg",
  "description": "흰색 소형견, 목줄 없음",
  "location": {
    "placeName": "여의도공원 입구",
    "latitude": 37.5290,
    "longitude": 126.9330,
    "address": "서울 영등포구"
  },
  "foundAt": "2025-01-29T09:30:00"
}
```

---

### Issue #66: 내 제보 목록 API
**Labels**: `priority:low`, `context:lostpet`, `phase:3`

#### `GET /sightings/mine`
내 제보 목록을 조회합니다.

---

### Issue #67: 제보 상세 API
**Labels**: `priority:low`, `context:lostpet`, `phase:3`

#### `GET /sightings/{sightingId}`
제보 상세를 조회합니다.

---

### Issue #68: 제보 삭제 API
**Labels**: `priority:low`, `context:lostpet`, `phase:3`

#### `DELETE /sightings/{sightingId}`
제보를 삭제합니다.

---

# 📌 Phase 4: Integration (통합 및 알림)

## 팀장 - Notification APIs

### Issue #69: Notification Entity 생성
**Labels**: `priority:medium`, `context:notification`, `phase:4`

**Entity 목록**:
- `Notification` - 알림 (memberId, type, title, content, targetType, targetId, isRead)
- `NotificationSetting` - 알림 설정 (memberId, chatMessage, walkApplication, lostPetSimilar)

**Enum 목록**:
- `NotificationType`: CHAT_MESSAGE, WALK_APPLICATION, LOST_PET_SIMILAR
- `TargetType`: CHAT_ROOM, THREAD, LOST_PET

---

### Issue #70: 알림 목록 조회 API
**Labels**: `priority:medium`, `context:notification`, `phase:4`

#### `GET /notifications`
알림 목록을 조회합니다.

**Query Parameters**:
| 이름 | 타입 | 필수 | 설명 |
|------|------|------|------|
| page | Integer | N | 페이지 번호 |
| size | Integer | N | 페이지 크기 |

**Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "type": "CHAT_MESSAGE",
        "title": "새 메시지",
        "content": "강아지좋아님이 메시지를 보냈습니다",
        "targetType": "CHAT_ROOM",
        "targetId": 1,
        "isRead": false,
        "createdAt": "2025-01-29T10:00:00"
      }
    ],
    "totalElements": 50,
    "totalPages": 3
  },
  "error": null
}
```

---

### Issue #71: 알림 읽음 처리 API
**Labels**: `priority:medium`, `context:notification`, `phase:4`

#### `PATCH /notifications/{notificationId}/read`
알림을 읽음 처리합니다.

---

### Issue #72: 전체 알림 읽음 처리 API
**Labels**: `priority:low`, `context:notification`, `phase:4`

#### `PATCH /notifications/read-all`
모든 알림을 읽음 처리합니다.

---

### Issue #73: 알림 설정 조회 API
**Labels**: `priority:low`, `context:notification`, `phase:4`

#### `GET /notification-settings`
알림 설정을 조회합니다.

**Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "chatMessage": true,
    "walkApplication": true,
    "lostPetSimilar": true
  },
  "error": null
}
```

---

### Issue #74: 알림 설정 수정 API
**Labels**: `priority:low`, `context:notification`, `phase:4`

#### `PATCH /notification-settings`
알림 설정을 수정합니다.

**Request Body**:
```json
{
  "chatMessage": false
}
```

---

### Issue #75: 이벤트 리스너 구현
**Labels**: `priority:high`, `context:notification`, `phase:4`

각 Context의 이벤트를 구독하여 알림을 생성합니다.

**구현할 리스너**:
- `ChatEventListener` - 새 메시지 알림
- `WalkEventListener` - 산책 신청 알림
- `LostPetEventListener` - 유사 제보 발견 알림

---

## 전체 - 이미지 업로드 API

### Issue #76: Presigned URL 발급 API
**Labels**: `priority:high`, `context:common`, `phase:2`

#### `POST /images/presigned-url`
S3 업로드용 Presigned URL을 발급합니다.

**Request Body**:
```json
{
  "filename": "profile.jpg",
  "contentType": "image/jpeg"
}
```

**Response (200 OK)**:
```json
{
  "success": true,
  "data": {
    "presignedUrl": "https://s3.amazonaws.com/...",
    "imageUrl": "https://cdn.aini-inu.com/images/..."
  },
  "error": null
}
```

---

# 📌 의존성 매트릭스

```
Phase 1 (의존성 없음):
├── 팀장: Common 모듈, Security, Location VO
├── 숙련 A: Member Entity, ErrorCode
├── 숙련 B: Walk Enum (ChatType 공유)
├── 초보 C: Post Entity 완성
└── 초보 D: Breed, Personality, WalkingStyle 마스터 데이터

Phase 2 (Member 필요):
├── 팀장: 대기 (Phase 3 준비)
├── 숙련 A: Auth API, Member API 완료
├── 숙련 B: Thread Entity, Thread API (authorId → Member)
├── 초보 C: Post CRUD, Comment, PostLike
└── 초보 D: Pet CRUD (memberId → Member)

Phase 3 (Pet, Walk 필요):
├── 팀장: Chat (Thread 연동), LostPet (Pet 연동)
├── 숙련 A: Block 기능
├── 숙련 B: Thread apply → ChatRoom 생성
├── 초보 C: 좋아요 토글, 댓글
└── 초보 D: 메인 반려견 변경, 10마리 제한 로직

Phase 4 (모든 Context):
├── 팀장: Notification (모든 이벤트 구독)
└── 전체: 통합 테스트, 크로스 컨텍스트 플로우
```

---

# 📌 Error Code 요약

| Context | Prefix | 범위 | 담당 |
|---------|--------|------|------|
| Common | C | C001-C999 | 팀장 |
| Member | M | M001-M006 | 숙련 A |
| Pet | P | P001-P009 | 초보 D |
| Thread | T | T001-T013 | 숙련 B |
| Chat | CH | CH001-CH014 | 팀장 |
| Community | CO | CO001-CO005 | 초보 C |
| LostPet | L | L001-L004 | 팀장 |
| Notification | N | N001-N0xx | 팀장 |

---

# 📌 GitHub Labels 정의

**Priority**:
- `priority:critical` - 차단 이슈, 즉시 해결 필요
- `priority:high` - 핵심 기능, 우선 구현
- `priority:medium` - 중요 기능
- `priority:low` - 부가 기능

**Context**:
- `context:common`, `context:member`, `context:pet`
- `context:walk`, `context:chat`, `context:community`
- `context:lostpet`, `context:notification`

**Phase**:
- `phase:1`, `phase:2`, `phase:3`, `phase:4`

**Type**:
- `good-first-issue` - 초보 개발자용
- `needs-discussion` - 논의 필요
- `blocked` - 다른 작업에 의해 차단됨

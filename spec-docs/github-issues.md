# 아이니이누 백엔드 팀원별 상세 구현 계획

## 프로젝트 개요
- **프로젝트명**: 아이니이누 (Aini Inu) - 반려견 산책 소셜 매칭 플랫폼
- **기술 스택**: Java 21, Spring Boot 3.x, JPA, MySQL 9+
- **팀 구성**: 팀장 1명 + 숙련 개발자 2명 + 초보 개발자 2명 (총 5명)

---

## 팀원별 이슈 목록 (Quick Reference)

### 건홍 (26개)
**Phase 1 - Common**
- #1: Security 설정 구현
- #2: Location Value Object 구현

**Phase 3 - Chat**
- #47: Chat Entity 생성
- #48: 채팅방 목록 조회 API
- #49: 채팅방 상세 조회 API
- #50: 메시지 목록 조회 API
- #51: 메시지 전송 API
- #52: 채팅방 나가기 API
- #53: 산책 확정 API
- #54: 산책 확정 상태 조회 API
- #55: 산책 확정 취소 API
- #56: 매너 후기 작성 API
- #57: 내 후기 조회 API

**Phase 3 - LostPet**
- #58: LostPet Entity 생성
- #59: 실종 신고 API
- #60: 내 실종 신고 목록 API
- #61: 실종 신고 상세 API
- #62: 유사 제보 목록 API
- #63: 실종견 매칭 API
- #64: 실종 신고 종료 API
- #65: 제보 등록 API
- #66: 내 제보 목록 API
- #67: 제보 상세 API
- #68: 제보 삭제 API

**Phase 4 - Notification**
- #69: Notification Entity 생성
- #70: 알림 목록 조회 API
- #71: 알림 읽음 처리 API
- #72: 전체 알림 읽음 처리 API
- #73: 알림 설정 조회 API
- #74: 알림 설정 수정 API
- #75: 이벤트 리스너 구현

---

### 동욱 - Member (12개)
**Phase 1**
- #3: Member 관련 Entity 생성
- #4: MemberErrorCode 정의

**Phase 2 - Auth & Profile**
- #8: 소셜 로그인 API
- #9: 토큰 갱신 API
- #10: 로그아웃 API
- #11: 회원가입 완료 API
- #12: 내 프로필 조회 API
- #13: 프로필 수정 API
- #14: 다른 회원 프로필 조회 API
- #15: 회원 성격 유형 목록 API

**Phase 3 - Block**
- #44: 차단 API
- #45: 차단 목록 조회 API
- #46: 차단 해제 API

---

### 혁진 - Walk/Thread (11개)
**Phase 1**
- #5: Walk 관련 Enum 정의

**Phase 2 - Thread**
- #16: Thread Entity 생성
- #17: 스레드 생성 API
- #18: 스레드 목록 조회 API
- #19: 스레드 상세 조회 API
- #20: 스레드 수정 API
- #21: 스레드 삭제 API
- #22: 스레드 신청 API
- #23: 스레드 신청 취소 API
- #24: 지도용 스레드 조회 API
- #25: 중복 스레드 확인 API

---

### 하늘 - Community (10개)
**Phase 1**
- #6: Post Entity 완성

**Phase 2**
- #26: Comment, PostLike Entity 생성
- #27: 게시물 생성 API
- #28: 게시물 목록 조회 API
- #29: 게시물 상세 조회 API
- #30: 게시물 수정 API
- #31: 게시물 삭제 API
- #32: 좋아요 토글 API
- #33: 댓글 작성 API
- #34: 댓글 삭제 API

---

### 효주 - Pet (10개)
**Phase 1**
- #7: 마스터 데이터 Entity 생성

**Phase 2**
- #35: Pet Entity 완성
- #36: 반려견 등록 API
- #37: 내 반려견 목록 조회 API
- #38: 반려견 수정 API
- #39: 반려견 삭제 API
- #40: 메인 반려견 변경 API
- #41: 견종 목록 조회 API
- #42: 반려견 성격 유형 목록 API
- #43: 산책 스타일 목록 API

---

### 전체 (1개)
**Phase 2**
- #76: Presigned URL 발급 API

---

## 팀원별 담당 Context

| 팀원 | 담당 Context | API 수 | 난이도 |
|------|-------------|--------|--------|
| 건홍 | Common + Chat + LostPet + Notification | 26개 | 고급 |
| 동욱 | Member (인증, 프로필, 차단) | 12개 | 중급 |
| 혁진 | Walk/Thread (모집글, 필터링) | 9개 | 중급 |
| 하늘 | Community (게시글, 댓글, 좋아요) | 8개 | 초급 |
| 효주 | Pet (반려견, 견종, 성격) | 8개 | 초급 |

---

# 📌 Phase 1: Foundation (기반 구축)

## 건홍 - Common 모듈 완성

### Issue #1: Security 설정 구현
**Labels**: `priority:high`, `context:common`, `phase:1`

#### 1. 개요 (Overview)
- JWT 기반 인증 및 OAuth2 소셜 로그인 지원을 위한 Security 설정을 구현합니다.
- **Objective**: 모든 API 요청에 대해 인증/인가를 수행하고, CORS 및 예외 처리를 통합 관리합니다.

#### 2. 구현 상세 (Implementation Specs)
- **SecurityConfig**:
  - `formLogin`, `httpBasic` 비활성화 (Stateless)
  - `sessionManagement`: STATELESS 설정
  - `CorsConfiguration`: 프론트엔드 도메인 허용, `Authorization` 헤더 노출
  - `JwtAuthenticationFilter`: Request Header의 `Authorization: Bearer` 토큰 검증
  - `AuthenticationEntryPoint`: 인증 실패 시 401 응답 (`C101`)
  - `AccessDeniedHandler`: 권한 부족 시 403 응답 (`C201`)
- **JwtTokenProvider**:
  - Access Token 생성 (만료: 1시간)
  - Refresh Token 생성 (만료: 14일)
  - 토큰 검증 및 Claims 파싱
- **PermitAll Endpoints**:
  - `/api/v1/auth/login/**` (소셜 로그인)
  - `/api/v1/auth/refresh` (토큰 갱신)
  - `/api/v1/breeds`, `/api/v1/personalities`, `/api/v1/walking-styles` (마스터 데이터 조회)
  - `/api/v1/member-personality-types` (회원 성격 유형 목록)
  - Swagger UI (`/v3/api-docs/**`, `/swagger-ui/**`)
  - Health Check (`/actuator/health`)

#### 3. 예외 처리 (Error Handling)
- `C101`: 인증이 필요합니다 (토큰 없음)
- `C102`: 유효하지 않은 토큰입니다 (잘못된 서명, 형식 오류)
- `C103`: 만료된 토큰입니다
- `C201`: 권한이 없습니다

#### 4. 구현 체크리스트
- [ ] SecurityConfig 클래스 생성 및 설정
- [ ] JwtTokenProvider 클래스 생성 (토큰 생성/검증/파싱)
- [ ] JwtAuthenticationFilter 생성 (OncePerRequestFilter 상속)
- [ ] CustomAuthenticationEntryPoint 구현
- [ ] CustomAccessDeniedHandler 구현
- [ ] CORS 설정 (프론트엔드 도메인, 허용 헤더)
- [ ] JWT Secret Key 환경변수 설정 (application.yml)
- [ ] 공통 에러 응답 포맷 적용 (ApiResponse)

---

### Issue #2: Location Value Object 구현
**Labels**: `priority:high`, `context:common`, `phase:1`

#### 1. 개요 (Overview)
- 산책 장소(Thread), 실종 장소(LostPet), 발견 장소(Sighting) 등에서 공통으로 사용하는 위치 정보 객체를 구현합니다.
- **Objective**: 위치 정보의 포맷과 정밀도를 통일하고, 재사용성을 높입니다.

#### 2. 구현 상세 (Implementation Specs)
- **Embeddable Class**: `Location`
- **Fields**:
  - `placeName` (String): 장소명 (예: 여의도 한강공원)
  - `latitude` (BigDecimal): 위도 (Precision 10, Scale 8) - 네이버 지도 API 호환
  - `longitude` (BigDecimal): 경도 (Precision 11, Scale 8)
  - `address` (String, Nullable): 도로명/지번 주소

#### 3. 사용처
- `Thread.location`: 산책 모집 장소
- `LostPetReport.lastSeenLocation`: 실종견 마지막 목격 장소
- `Sighting.foundLocation`: 제보 발견 장소

#### 4. 거리 계산
- Haversine 공식을 사용한 두 지점 간 거리 계산 유틸리티 메서드 제공
- 스레드 목록 조회 시 거리순 정렬에 활용

#### 5. 구현 체크리스트
- [ ] JPA `@Embeddable` 적용
- [ ] BigDecimal Scale 8자리 설정 (MySQL `DECIMAL(10,8)`, `DECIMAL(11,8)`)
- [ ] `@Column(precision = 10, scale = 8)` 어노테이션 적용
- [ ] Haversine 거리 계산 유틸리티 메서드 구현
- [ ] 위도/경도 유효 범위 검증 (위도: -90~90, 경도: -180~180)

---

## 동욱 - Member 기반 작업

### Issue #3: Member 관련 Entity 생성
**Labels**: `priority:high`, `context:member`, `phase:1`

#### 1. 개요 (Overview)
- 회원 및 프로필 정보를 저장할 Entity 설계를 구현합니다.
- **Objective**: 회원 가입, 프로필 관리, 차단 기능을 위한 데이터 구조를 확립합니다.

#### 2. Entity 명세 (Entity Specs)
- **Member**:
  - `id` (PK)
  - `email` (Unique)
  - `nickname` (Unique, max 10)
  - `memberType` (Enum: `PET_OWNER`, `NON_PET_OWNER` - Default: `NON_PET_OWNER`)
  - `mannerTemperature` (Double - Default: 5.0)
  - `status` (Enum: `ACTIVE`, `INACTIVE`, `BANNED`)
  - `profileImageUrl` (String, nullable)
  - `linkedNickname` (String, nullable): 애견 연계 닉네임 (예: 몽이아빠)
  - `age` (Integer, nullable)
  - `gender` (Enum: `MALE`, `FEMALE`, `UNKNOWN`, nullable)
  - `mbti` (String, max 4, nullable)
  - `personality` (String, nullable): 성격 설명
  - `selfIntroduction` (String, nullable): 자기소개
  - `isVerified` (Boolean - Default: false): 반려동물등록 공공데이터 인증 여부
  - `socialProvider` (Enum: `NAVER`, `KAKAO`, `GOOGLE`)
  - `socialId` (String): 소셜 로그인 고유 ID
  - `BaseTimeEntity` 상속 (createdAt, updatedAt)
- **MemberPersonalityType** (마스터 데이터):
  - `id` (PK)
  - `name` (String): 표시명 (예: 동네친구)
  - `code` (String): 코드값 (예: LOCAL_FRIEND)
  - **초기 데이터**: `LOCAL_FRIEND` (동네친구), `PET_INFO_SHARING` (반려견정보공유), `ONLINE_PET_LOVER` (랜선집사), `DOG_LOVER_ONLY` (강아지만좋아함)
- **MemberPersonality**: `Member`와 `MemberPersonalityType`의 N:M 연결 (중간 테이블)
  - `member` (Member, ManyToOne)
  - `personalityType` (MemberPersonalityType, ManyToOne)
- **Block**:
  - `blocker` (Member, ManyToOne)
  - `blocked` (Member, ManyToOne)
  - `BaseTimeEntity` 상속 (createdAt)
  - Unique Constraint: `(blocker_id, blocked_id)` 중복 방지
- **RefreshToken** (토큰 관리):
  - `id` (PK)
  - `member` (Member, ManyToOne): 토큰 소유자
  - `token` (String, Unique): Refresh Token 값
  - `expiresAt` (LocalDateTime): 만료 시간
  - `createdAt` (LocalDateTime): 생성 시간
  - Index: `member_id` (회원별 토큰 조회용)

#### 3. 비즈니스 로직 & 제약조건
- **닉네임**: 중복 불가, 최대 10자, 특수문자 허용.
- **회원 타입**: 초기 가입 시 `NON_PET_OWNER`. 반려견 등록 시 `PET_OWNER`로 자동 전환.
- **매너 온도**: 초기값 5.0 (1.0 ~ 10.0 범위), 산책 후기 점수 평균으로 계산.
- **자기 차단 불가**: blocker_id와 blocked_id가 같을 수 없음.
- **Refresh Token Rotation (RTR)**: 토큰 갱신 시 기존 토큰 삭제 후 새 토큰 발급 (보안 강화).

#### 4. 구현 체크리스트
- [ ] Member Entity 생성 및 JPA 어노테이션 적용
- [ ] MemberPersonalityType Entity 생성 (마스터 데이터)
- [ ] MemberPersonality 중간 테이블 Entity 생성
- [ ] Block Entity 생성 및 Unique Constraint 적용
- [ ] RefreshToken Entity 생성
- [ ] Enum 클래스 생성 (MemberType, MemberStatus, SocialProvider, Gender)
- [ ] BaseTimeEntity 상속 구조 확인

---

### Issue #4: MemberErrorCode 정의
**Labels**: `priority:high`, `context:member`, `phase:1`

#### 1. 개요 (Overview)
- Member Context에서 발생할 수 있는 모든 예외 상황에 대한 에러 코드를 정의합니다.

#### 2. 에러 코드 명세 (Error Codes)

| 에러 코드 | HTTP 상태 | 설명 | 발생 조건 |
|----------|----------|------|----------|
| M001 | 404 | 회원을 찾을 수 없음 | 탈퇴하거나 존재하지 않는 회원 ID 조회 시 |
| M002 | 400 | 닉네임이 유효하지 않음 | 길이 초과(10자), 공백만 포함 등 |
| M003 | 409 | 이미 사용 중인 닉네임 | 닉네임 중복 검사 실패 시 |
| M004 | 401 | 유효하지 않은 소셜 토큰 | OAuth 인증 실패 시 |
| M005 | 403 | 정지된 회원 | BANNED 상태 회원 로그인 시도 시 |
| M006 | 403 | 작성자에게 차단됨 | 차단된 회원이 접근 시도 시 |

#### 3. 구현 체크리스트
- [ ] MemberErrorCode Enum 생성 (ErrorCode 인터페이스 구현)
- [ ] MemberException 클래스 생성 (RuntimeException 상속)
- [ ] GlobalExceptionHandler에 MemberException 핸들러 추가
- [ ] 각 에러 코드별 HTTP 상태 코드 매핑

---

## 혁진 - Walk 기반 작업

### Issue #5: Walk 관련 Enum 정의
**Labels**: `priority:medium`, `context:walk`, `phase:1`

#### 1. 개요 (Overview)
- 산책 모집 및 필터링에 사용되는 상수 값들을 정의합니다.

#### 2. Enum 명세 (Enum Specs)
- **ChatType**:
  - `INDIVIDUAL`: 1:1 개별 채팅 (승인 없이 바로 대화)
  - `GROUP`: 3~10명 그룹 채팅
- **ThreadStatus**:
  - `ACTIVE`: 모집 중
  - `CLOSED`: 종료됨 (아카이브)
- **FilterType** (참가 조건 필터):
  - `SIZE`: 소형/중형/대형
  - `GENDER`: 수컷/암컷
  - `NEUTERED`: 중성화 여부
  - `BREED`: 견종
  - `MBTI`: 반려견 MBTI
  - `PERSONALITY`: 반려견 성향
  - `WALKING_STYLE`: 산책 스타일

#### 3. FilterType별 값 예시

| FilterType | 가능한 값 (filterValue) |
|------------|------------------------|
| SIZE | `["SMALL"]`, `["MEDIUM", "LARGE"]` |
| GENDER | `["MALE"]`, `["FEMALE"]` |
| NEUTERED | `["true"]`, `["false"]` |
| BREED | `["15", "20"]` (견종 ID) |
| MBTI | `["ENFP", "INFJ"]` |
| PERSONALITY | `["1", "3", "5"]` (성향 ID) |
| WALKING_STYLE | `["ENERGY_BURST", "RELAXED"]` |

#### 4. 구현 체크리스트
- [ ] ChatType Enum 생성
- [ ] ThreadStatus Enum 생성
- [ ] FilterType Enum 생성
- [ ] 각 Enum에 description 필드 추가 (API 응답용)

---

## 하늘 - Community 기반 작업

### Issue #6: Post Entity 완성
**Labels**: `priority:medium`, `context:community`, `phase:1`, `good-first-issue`

#### 1. 개요 (Overview)
- 커뮤니티 게시글 저장을 위한 Post Entity를 구현합니다.

#### 2. Entity 명세 (Entity Specs)
- **Post**:
  - `id` (PK)
  - `content` (String, TEXT): 게시글 내용, 최대 2000자
  - `imageUrls` (List<String>): JSON 컬럼 또는 `@ElementCollection`, 최대 5장
  - `likeCount` (int, Default 0): 좋아요 수 (동시성 고려 필요)
  - `commentCount` (int, Default 0): 댓글 수
  - `author` (Member, ManyToOne): 작성자
  - `BaseTimeEntity` 상속 (createdAt, updatedAt)

#### 3. 비즈니스 로직 & 제약조건
- **내용**: 필수, 최대 2000자 (`CO005` 에러 시 반환).
- **이미지**: 선택, 최대 5장까지 첨부 가능.
- **좋아요/댓글 수**: 정합성 보장을 위해 증가/감소 시 동시성 처리 필요 (낙관적/비관적 락 또는 Atomic 연산).
- **Soft Delete**: 게시글 삭제 시 물리 삭제 또는 deleted 플래그 사용 (팀 컨벤션에 따름).
- **차단 회원 필터링**: 조회 시 차단한/차단된 회원의 게시글 제외.

#### 4. 구현 체크리스트
- [ ] Post Entity 생성 및 JPA 어노테이션 적용
- [ ] imageUrls 컬럼 JSON 타입 또는 ElementCollection 적용
- [ ] BaseTimeEntity 상속
- [ ] content 최대 길이 검증 (@Size 또는 @Column(length=2000))
- [ ] likeCount, commentCount 동시성 처리 방안 결정

---

## 효주 - Pet 기반 작업

### Issue #7: 마스터 데이터 Entity 생성
**Labels**: `priority:medium`, `context:pet`, `phase:1`, `good-first-issue`

#### 1. 개요 (Overview)
- 견종, 성격, 산책 스타일 등 변경이 적은 기준 데이터를 관리할 Entity를 생성합니다.
- **Objective**: 반려견 등록 및 필터링 시 사용할 마스터 데이터 구조를 확립합니다.

#### 2. Entity 명세 (Entity Specs)
- **Breed**:
  - `id` (PK)
  - `name` (String): 견종명 (예: 포메라니안)
  - `size` (Enum: `SMALL`, `MEDIUM`, `LARGE`)
- **PetPersonalityType**:
  - `id` (PK)
  - `name` (String): 표시명 (예: 소심해요)
  - `code` (String, Unique): 코드값 (예: SHY)
- **WalkingStyle**:
  - `id` (PK)
  - `name` (String): 표시명 (예: 전력질주)
  - `code` (String, Unique): 코드값 (예: ENERGY_BURST)

#### 3. 초기 데이터 (Seed Data)
- **PetPersonalityType** (7개):

| ID | Code | Name |
|----|------|------|
| 1 | SHY | 소심해요 |
| 2 | ENERGETIC | 에너지넘침 |
| 3 | TREAT_LOVER | 간식좋아함 |
| 4 | PEOPLE_LOVER | 사람좋아함 |
| 5 | SEEKING_FRIENDS | 친구구함 |
| 6 | OWNER_FOCUSED | 주인바라기 |
| 7 | GRUMPY | 까칠해요 |

- **WalkingStyle** (7개):

| ID | Code | Name |
|----|------|------|
| 1 | ENERGY_BURST | 전력질주 |
| 2 | SNIFF_EXPLORER | 냄새맡기집중 |
| 3 | BENCH_REST | 공원벤치휴식형 |
| 4 | RELAXED | 느긋함 |
| 5 | SNIFF_DETECTIVE | 냄새탐정 |
| 6 | ENDLESS_ENERGY | 무한동력 |
| 7 | LOW_STAMINA | 저질체력 |

#### 4. 구현 체크리스트
- [ ] Breed Entity 생성 및 JPA 어노테이션 적용
- [ ] PetPersonalityType Entity 생성 (code Unique 제약)
- [ ] WalkingStyle Entity 생성 (code Unique 제약)
- [ ] PetSize Enum 생성 (SMALL, MEDIUM, LARGE)
- [ ] data.sql 또는 Flyway 마이그레이션으로 초기 데이터 삽입
- [ ] 견종 데이터는 별도 CSV 또는 SQL로 대량 삽입 (공공데이터 활용)

---

# 📌 Phase 2: Core APIs (핵심 API 구현)

## 동욱 - Auth & Member APIs

### Issue #8: 소셜 로그인 API
**Labels**: `priority:critical`, `context:member`, `phase:2`

#### 1. 개요 (Overview)
- 클라이언트로부터 소셜 액세스 토큰을 받아 검증하고, 자체 JWT를 발급합니다.
- **Objective**: 회원 가입 여부를 판단하여 분기 처리(신규/기존)를 수행합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `POST /auth/login/{provider}`

**Request Body**
```json
{
  "accessToken": "소셜 로그인 액세스 토큰"
}
```

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "isNewMember": true,
    "memberId": 1
  }
}
```

- **Key Fields**:
  - `provider`: Path Parameter (naver, kakao, google)
  - `isNewMember`: true(최초 가입), false(기존 회원)

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- **검증**: `accessToken`이 비어있거나 소셜 서버 검증 실패 시 `M004` 에러.
- **가입**: 이메일(Social ID)로 조회하여 없으면 임시 회원 생성(이때 `memberType`은 `NON_PET_OWNER`).
- **정지**: `status`가 `BANNED`인 경우 `M005` 에러 반환.

#### 4. 예외 처리 (Error Handling)
- `C001`: 지원하지 않는 소셜 로그인 제공자
- `M004`: 유효하지 않은 소셜 토큰
- `M005`: 정지된 회원

---

### Issue #9: 토큰 갱신 API
**Labels**: `priority:critical`, `context:member`, `phase:2`

#### 1. 개요 (Overview)
- 만료된 Access Token을 Refresh Token을 사용하여 갱신합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `POST /auth/refresh`

**Request Body**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIs...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIs...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  }
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- DB에 저장된 Refresh Token과 일치하는지 확인.
- 만료 여부 및 서명 검증.
- Refresh Token Rotation (RTR) 적용 여부 결정 (보안 강화).

#### 4. 예외 처리 (Error Handling)
- `C102`: 유효하지 않은 리프레시 토큰

---

### Issue #10: 로그아웃 API
**Labels**: `priority:high`, `context:member`, `phase:2`

#### 1. 개요 (Overview)
- 서버 측에서 Refresh Token을 삭제하여 세션을 만료시킵니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `POST /auth/logout`

**Request Body**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIs..."
}
```

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": null
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- DB에서 해당 Refresh Token 삭제.

#### 4. 예외 처리 (Error Handling)
- `C101`: 인증 실패
- `C102`: 유효하지 않은 리프레시 토큰

---

### Issue #11: 회원가입 완료 API
**Labels**: `priority:critical`, `context:member`, `phase:2`

#### 1. 개요 (Overview)
- 소셜 로그인 후 부족한 프로필 정보를 입력받아 가입을 완료합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `POST /members/profile`

**Request Body**
```json
{
  "nickname": "건홍이네",
  "profileImageUrl": "https://s3.../profile.jpg",
  "age": 29,
  "gender": "MALE",
  "mbti": "INTJ",
  "personality": "차분하고 배려심이 많아요",
  "selfIntroduction": "한강 근처에서 자주 산책해요!",
  "personalityTypeIds": [1, 3]
}
```

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "nickname": "건홍이네",
    "memberType": "NON_PET_OWNER",
    "profileImageUrl": "https://s3.../profile.jpg",
    "linkedNickname": null,
    "age": 29,
    "gender": "MALE",
    "mbti": "INTJ",
    "personality": "차분하고 배려심이 많아요",
    "selfIntroduction": "한강 근처에서 자주 산책해요!",
    "personalityTypes": [
      {"id": 1, "name": "동네친구", "code": "LOCAL_FRIEND"},
      {"id": 3, "name": "랜선집사", "code": "ONLINE_PET_LOVER"}
    ],
    "mannerTemperature": 5.0,
    "status": "ACTIVE",
    "createdAt": "2026-01-26T10:00:00+09:00"
  }
}
```

- **Key Fields**:
  - `nickname`: 필수, 최대 10자
  - `personalityTypeIds`: 선택, 견주 성향 카테고리 ID 목록

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- **닉네임 검증**: 중복 시 `M003`, 형식 위반(10자 초과 등) 시 `M002`.
- **회원 타입**: 최초 가입 시 `NON_PET_OWNER`로 설정 (반려견 등록 시 변경).
- **성향**: `MemberPersonality` 테이블에 매핑 정보 저장.

#### 4. 예외 처리 (Error Handling)
- `M002`: 닉네임이 유효하지 않음 (길이 초과 등)
- `M003`: 이미 사용 중인 닉네임

---

### Issue #12: 내 프로필 조회 API
**Labels**: `priority:high`, `context:member`, `phase:2`

#### 1. 개요 (Overview)
- 접속한 회원의 본인 프로필 및 반려견 목록을 조회합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /members/me`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "id": 1,
    "email": "user@example.com",
    "nickname": "건홍이네",
    "memberType": "PET_OWNER",
    "profileImageUrl": "https://s3.../profile.jpg",
    "linkedNickname": "몽이아빠",
    "mannerTemperature": 5.0,
    "status": "ACTIVE",
    "age": 29,
    "gender": "MALE",
    "mbti": "INTJ",
    "personality": "차분하고 배려심이 많아요",
    "selfIntroduction": "한강 근처에서 자주 산책해요!",
    "personalityTypes": [
      {"id": 1, "name": "동네친구", "code": "LOCAL_FRIEND"}
    ],
    "isVerified": true,
    "createdAt": "2026-01-26T10:00:00+09:00",
    "pets": [
      {
        "id": 1,
        "name": "몽이",
        "breed": "포메라니안",
        "photoUrl": "https://s3.../pet.jpg",
        "isMain": true
      }
    ]
  }
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- `pets`에는 메인 반려견(`isMain=true`) 여부가 포함되어야 함.

---

### Issue #13: 프로필 수정 API
**Labels**: `priority:high`, `context:member`, `phase:2`

#### 1. 개요 (Overview)
- 닉네임, 프로필 사진, 자기소개 등을 수정합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `PATCH /members/me`

**Request Body**
```json
{
  "nickname": "새닉네임",
  "profileImageUrl": "https://s3.../new-profile.jpg",
  "linkedNickname": "몽이엄마",
  "selfIntroduction": "주말마다 산책해요!",
  "personalityTypeIds": [1, 4]
}
```

**Response Body**
- (수정된 전체 프로필 반환, Issue #12 Response와 동일)

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- 닉네임 변경 시 중복 검사 (`M003`) 다시 수행.

---

### Issue #14: 다른 회원 프로필 조회 API
**Labels**: `priority:medium`, `context:member`, `phase:2`

#### 1. 개요 (Overview)
- 스레드나 채팅방에서 다른 사용자의 프로필을 클릭했을 때 정보를 조회합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /members/{memberId}`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "id": 2,
    "nickname": "뭉치맘",
    "memberType": "PET_OWNER",
    "profileImageUrl": "https://s3.../profile.jpg",
    "linkedNickname": "뭉치엄마",
    "mannerTemperature": 7.5,
    "age": 31,
    "gender": "FEMALE",
    "mbti": "ENFP",
    "personality": "활발하고 친화적이에요",
    "selfIntroduction": "뭉치랑 동네 친구 구해요!",
    "personalityTypes": [
      {"id": 1, "name": "동네친구", "code": "LOCAL_FRIEND"}
    ],
    "pets": [
      {
        "id": 5,
        "name": "뭉치",
        "breed": "말티즈",
        "photoUrl": "https://s3.../pet.jpg",
        "isMain": true
      }
    ]
  }
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- 민감한 정보(이메일, 전화번호 등) 제외.
- 매너 온도, 대표 반려견 정보, 닉네임 등 공개 정보만 반환.

---

### Issue #15: 회원 성격 유형 목록 API
**Labels**: `priority:low`, `context:member`, `phase:2`

#### 1. 개요 (Overview)
- 회원 가입/수정 화면에서 선택할 수 있는 성격 유형(마스터 데이터)을 제공합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /member-personality-types`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": [
    {"id": 1, "name": "동네친구", "code": "LOCAL_FRIEND"},
    {"id": 2, "name": "반려견정보공유", "code": "PET_INFO_SHARING"},
    {"id": 3, "name": "랜선집사", "code": "ONLINE_PET_LOVER"},
    {"id": 4, "name": "강아지만좋아함", "code": "DOG_LOVER_ONLY"}
  ]
}
```

---

## 혁진 - Thread APIs

### Issue #16: Thread Entity 생성
**Labels**: `priority:high`, `context:walk`, `phase:2`

#### 1. 개요 (Overview)
- 산책 모집글(Thread) 및 관련 필터 정보를 저장할 Entity를 설계합니다.
- **Objective**: 모집 조건, 참여자 수, 위치 정보 등을 효율적으로 관리할 수 있는 구조를 만듭니다.

#### 2. Entity 명세 (Entity Specs)
- **Thread**:
  - `id` (PK)
  - `title` (String, max 30): 모집글 제목
  - `description` (String, max 500): 소개글
  - `walkDate` (LocalDate): 산책 예정 날짜
  - `startTime` (LocalDateTime): 시작 시간 (KST)
  - `endTime` (LocalDateTime): 종료 시간 (KST)
  - `chatType` (Enum: `INDIVIDUAL`, `GROUP`): 채팅 방식
  - `maxParticipants` (Integer, 3~10): 최대 참가자 수 (GROUP일 때만 유효)
  - `currentParticipants` (Integer, Default 1): 현재 참가자 수 (작성자 포함)
  - `allowNonPetOwner` (Boolean): 비애견인 참여 허용 여부
  - `isVisibleAlways` (Boolean): 항상 지도에 표시 여부
  - `location` (Embedded Location VO): 산책 장소 정보
  - `status` (Enum: `ACTIVE`, `CLOSED`): 모집 상태
  - `author` (Member, ManyToOne): 작성자
  - `BaseTimeEntity` 상속 (createdAt, updatedAt)
- **ThreadFilter**: `Thread` 1:N 관계
  - `id` (PK)
  - `thread` (Thread, ManyToOne)
  - `filterType` (Enum: `SIZE`, `GENDER`, `NEUTERED`, `BREED`, `MBTI`, `PERSONALITY`, `WALKING_STYLE`)
  - `filterValue` (String): 필터 값 (JSON Array 형태로 저장, 예: `["SMALL", "MEDIUM"]`)
  - `isRequired` (Boolean): 필수 조건 여부 (true=Hard Filter, false=Soft Filter)
- **ThreadPet**: `Thread`와 `Pet`의 N:M 연결 (스레드에 참여하는 반려견)
  - `thread` (Thread, ManyToOne)
  - `pet` (Pet, ManyToOne)

#### 3. 비즈니스 로직 & 제약조건
- **동시 작성 제한**: 사용자당 `ACTIVE` 상태 스레드는 최대 1개.
- **시간 검증**:
  - 시작 시간은 현재보다 미래여야 함.
  - 최대 7일(1주일) 후까지만 예약 가능.
  - 종료 시간 > 시작 시간.
- **필터 제한**: `isRequired=true`인 필터는 최대 3개까지.
- **참가자 수**: GROUP일 때 3~10명 범위.
- **currentParticipants 의미**:
  - `GROUP`: 그룹 채팅방의 현재 참가자 수 (작성자 포함)
  - `INDIVIDUAL`: 해당 스레드에서 생성된 활성 1:1 채팅방 수 (신청 수)

#### 4. 예외 처리 (ThreadErrorCode)
- `T001`: 스레드를 찾을 수 없습니다
- `T002`: 이미 활성 스레드가 존재함
- `T003`: 스레드 작성자가 아님
- `T004`: 스레드 종료됨
- `T005`: 정원 초과
- `T006`: 비애견인 참여가 허용되지 않음
- `T007`: 필수 필터 조건 미충족
- `T008`: 유효하지 않은 산책 시간 (과거, 1주일 초과)
- `T009`: 종료 시간이 시작 시간보다 이전
- `T010`: 참가자 수 범위 오류 (3~10)
- `T011`: 필수 필터 3개 초과
- `T012`: 비애견인은 스레드 작성 불가
- `T013`: 이미 신청함

#### 5. 구현 체크리스트
- [ ] Thread Entity 생성 및 JPA 어노테이션 적용
- [ ] ThreadFilter Entity 생성 (1:N 관계)
- [ ] ThreadPet 중간 테이블 Entity 생성 (N:M 관계)
- [ ] Location Embeddable 클래스 연동 (Issue #2)
- [ ] Enum 클래스 생성 (ChatType, ThreadStatus, FilterType)
- [ ] filterValue JSON Array 형태 저장 방식 결정
- [ ] BaseTimeEntity 상속

---

### Issue #17: 스레드 생성 API
**Labels**: `priority:critical`, `context:walk`, `phase:2`

#### 1. 개요 (Overview)
- 산책 모집글을 생성하고 초기 상태(ACTIVE)로 설정합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `POST /threads`

**Request Body**
```json
{
  "title": "한강공원 같이 산책해요",
  "description": "오후에 한강공원에서 산책해요! 대형견 환영합니다~",
  "walkDate": "2026-01-27",
  "startTime": "2026-01-27T14:00:00+09:00",
  "endTime": "2026-01-27T16:00:00+09:00",
  "chatType": "GROUP",
  "maxParticipants": 5,
  "allowNonPetOwner": false,
  "isVisibleAlways": true,
  "location": {
    "placeName": "여의도 한강공원",
    "latitude": 37.5283,
    "longitude": 126.9328,
    "address": "서울특별시 영등포구 여의동로 330"
  },
  "petIds": [1, 2],
  "filters": [
    {"type": "SIZE", "values": ["MEDIUM", "LARGE"], "isRequired": true},
    {"type": "PERSONALITY", "values": [5], "isRequired": false}
  ]
}
```

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "id": 100,
    "author": {
      "id": 1,
      "nickname": "몽이아빠",
      "profileImageUrl": "https://s3.../profile.jpg",
      "mannerTemperature": 5.0
    },
    "title": "한강공원 같이 산책해요",
    "description": "오후에 한강공원에서 산책해요! 대형견 환영합니다~",
    "walkDate": "2026-01-27",
    "startTime": "2026-01-27T14:00:00+09:00",
    "endTime": "2026-01-27T16:00:00+09:00",
    "chatType": "GROUP",
    "maxParticipants": 5,
    "currentParticipants": 1,
    "allowNonPetOwner": false,
    "isVisibleAlways": true,
    "status": "ACTIVE",
    "location": {
      "placeName": "여의도 한강공원",
      "latitude": 37.5283,
      "longitude": 126.9328,
      "address": "서울특별시 영등포구 여의동로 330"
    },
    "pets": [
      {"id": 1, "name": "몽이", "photoUrl": "https://s3.../pet.jpg"},
      {"id": 2, "name": "콩이", "photoUrl": "https://s3.../pet2.jpg"}
    ],
    "filters": [
      {"type": "SIZE", "values": ["MEDIUM", "LARGE"], "isRequired": true},
      {"type": "PERSONALITY", "values": [5], "isRequired": false}
    ],
    "createdAt": "2026-01-26T10:00:00+09:00"
  }
}
```

- **Key Fields**:
  - `title`: 필수, 최대 30자
  - `description`: 필수, 최대 500자
  - `maxParticipants`: 그룹 채팅 시 3~10명
  - `allowNonPetOwner`: 필수 선택

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- **활성 스레드 제한**: 사용자당 `ACTIVE` 상태 스레드는 최대 1개 (`T002`).
- **시간 검증**: 
  - 시작 시간은 현재보다 미래여야 함 (`T008`).
  - 최대 7일 후까지만 예약 가능.
  - 종료 시간은 시작 시간 이후여야 함 (`T009`).
- **필터 제한**: `isRequired=true`인 필터는 최대 3개까지만 설정 가능 (`T011`).
- **권한**: 비애견인(`NON_PET_OWNER`)은 스레드 생성 불가 (`T012`).

#### 4. 예외 처리 (Error Handling)
- `T002`: 이미 활성 스레드가 존재함
- `T008`: 유효하지 않은 산책 시간 (과거, 1주일 초과)
- `T009`: 종료 시간이 시작 시간보다 이전
- `T010`: 참가자 수 범위 오류 (3~10)
- `T011`: 필수 필터 3개 초과
- `T012`: 비애견인은 스레드 작성 불가

---

### Issue #18: 스레드 목록 조회 API
**Labels**: `priority:critical`, `context:walk`, `phase:2`

#### 1. 개요 (Overview)
- 조건(위치, 시간, 필터)에 맞는 스레드 목록을 조회합니다. 무한 스크롤(Slice)을 지원합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /threads`

**Query Params**
- `page`, `size`
- `sort`: `-startTime` (기본값)
- `latitude`, `longitude`, `radius`: 위치 기반 검색
- `startDate`, `endDate`, `startHour`, `endHour`: 시간 필터

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "content": [
      {
        "id": 100,
        "author": {
          "id": 1,
          "nickname": "몽이아빠",
          "profileImageUrl": "https://s3.../profile.jpg"
        },
        "title": "한강공원 같이 산책해요",
        "description": "오후에 한강공원에서 산책해요!",
        "walkDate": "2026-01-27",
        "startTime": "2026-01-27T14:00:00+09:00",
        "endTime": "2026-01-27T16:00:00+09:00",
        "chatType": "GROUP",
        "maxParticipants": 5,
        "currentParticipants": 3,
        "allowNonPetOwner": false,
        "location": {
          "placeName": "여의도 한강공원",
          "latitude": 37.5283,
          "longitude": 126.9328
        },
        "distance": 2.5,
        "mainPet": {
          "id": 1,
          "name": "몽이",
          "photoUrl": "https://s3.../pet.jpg"
        },
        "myFilterStatus": {
          "meetsRequired": true,
          "meetsPreferred": false,
          "unmatchedPreferred": ["친구구함"]
        }
      }
    ],
    "pageNumber": 0,
    "pageSize": 20,
    "first": true,
    "last": false,
    "hasNext": true
  }
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- **노출 제한**: 
  - 작성자의 필수 필터(Hard Filter)를 충족하지 못하는 회원은 목록에서 제외.
  - 비애견인은 `allowNonPetOwner=true`이면서 반려견 관련 필수 필터가 없는 스레드만 조회 가능.
  - 차단 관계에 있는 사용자의 스레드는 제외.
- **정렬**: 거리순, 시작 시간순 지원.

---

### Issue #19: 스레드 상세 조회 API
**Labels**: `priority:high`, `context:walk`, `phase:2`

#### 1. 개요 (Overview)
- 스레드의 상세 정보와 설정된 필터 목록을 조회합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /threads/{threadId}`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "id": 100,
    "author": {
      "id": 1,
      "nickname": "몽이아빠",
      "profileImageUrl": "https://s3.../profile.jpg",
      "mannerTemperature": 5.0
    },
    "title": "한강공원 같이 산책해요",
    "description": "오후에 한강공원에서 산책해요! 대형견 환영합니다~",
    "walkDate": "2026-01-27",
    "startTime": "2026-01-27T14:00:00+09:00",
    "endTime": "2026-01-27T16:00:00+09:00",
    "chatType": "GROUP",
    "maxParticipants": 5,
    "currentParticipants": 3,
    "allowNonPetOwner": false,
    "isVisibleAlways": true,
    "status": "ACTIVE",
    "location": {
      "placeName": "여의도 한강공원",
      "latitude": 37.5283,
      "longitude": 126.9328,
      "address": "서울특별시 영등포구 여의동로 330"
    },
    "pets": [
      {
        "id": 1,
        "name": "몽이",
        "breed": "포메라니안",
        "age": 3,
        "gender": "MALE",
        "photoUrl": "https://s3.../pet.jpg",
        "personalities": ["에너지넘침", "친구구함"]
      }
    ],
    "filters": {
      "required": [
        {"type": "SIZE", "values": ["MEDIUM", "LARGE"]}
      ],
      "preferred": [
        {"type": "PERSONALITY", "values": ["친구구함"]}
      ]
    },
    "myFilterStatus": {
      "meetsRequired": true,
      "meetsPreferred": false,
      "unmatchedPreferred": ["친구구함"]
    },
    "isApplied": false,
    "createdAt": "2026-01-26T10:00:00+09:00",
    "updatedAt": "2026-01-26T10:00:00+09:00"
  }
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- 필수 필터 미충족 사용자가 직접 URL로 접근 시 `T007` 혹은 `404 Not Found` 처리 (보안 정책에 따름).

---

### Issue #20: 스레드 수정 API
**Labels**: `priority:medium`, `context:walk`, `phase:2`

#### 1. 개요 (Overview)
- 스레드 내용을 수정합니다. 작성자만 가능합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `PATCH /threads/{threadId}`

**Request Body** (수정할 필드만 포함)
```json
{
  "title": "수정된 제목",
  "description": "수정된 설명",
  "endTime": "2026-01-27T17:00:00+09:00"
}
```

**Response Body**
- (수정된 스레드 정보 반환)

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- 수정 시 기존 채팅방 참여자에게 알림 발송(Notification 연동 고려).

---

### Issue #21: 스레드 삭제 API
**Labels**: `priority:medium`, `context:walk`, `phase:2`

#### 1. 개요 (Overview)
- 스레드를 삭제하고 연관된 채팅방을 종료 처리합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `DELETE /threads/{threadId}`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": null
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- 스레드 삭제 시 상태를 `CLOSED`로 변경하거나 DB에서 Soft Delete.
- 연결된 채팅방도 종료 상태로 변경.

---

### Issue #22: 스레드 신청 API
**Labels**: `priority:critical`, `context:walk`, `phase:2`

#### 1. 개요 (Overview)
- 산책 스레드에 참여 신청을 합니다. 신청 즉시 채팅방에 입장(생성)됩니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `POST /threads/{threadId}/apply`

**Request Body**
```json
{
  "petIds": [1, 2]
}
```

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "chatRoomId": 500,
    "chatType": "GROUP",
    "threadId": 100,
    "joinedAt": "2026-01-26T10:30:00+09:00"
  }
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- **필터 검증**: 신청자의 메인 반려견(또는 선택한 반려견)이 필수 필터를 충족하는지 확인 (`T007`).
- **채팅방 생성/참여**:
  - `INDIVIDUAL`: 작성자와의 1:1 채팅방 신규 생성.
  - `GROUP`: 기존 그룹 채팅방에 참여자 추가. 정원 초과 시 `T005`.
- **중복 신청**: 이미 신청한 경우 `T013`.
- **차단 확인**: 작성자가 신청자를 차단했으면 `M006`.

#### 4. 예외 처리 (Error Handling)
- `T004`: 스레드 종료됨
- `T005`: 정원 초과 (그룹)
- `T006`: 비애견인 참여가 허용되지 않음
- `T007`: 필수 필터 조건 미충족 (메인 반려견 기준)
- `T013`: 이미 신청함
- `M006`: 작성자에게 차단됨

---

### Issue #23: 스레드 신청 취소 API
**Labels**: `priority:medium`, `context:walk`, `phase:2`

#### 1. 개요 (Overview)
- 채팅방에서 나가며 스레드 참여를 취소합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `DELETE /threads/{threadId}/apply`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": null
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- `GROUP`: 참여자 명단에서 제거, 정원 여유 발생.
- `INDIVIDUAL`: 1:1 채팅방 비활성화(나가기 처리).

---

### Issue #24: 지도용 스레드 조회 API
**Labels**: `priority:medium`, `context:walk`, `phase:2`

#### 1. 개요 (Overview)
- 지도 영역(Viewport) 내의 스레드 핀 정보를 조회합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /threads/map`

**Query Params**: `latitude`, `longitude`, `radius`, `startHour`, `endHour`, `date`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": [
    {
      "id": 100,
      "title": "한강공원 같이 산책해요",
      "location": {
        "placeName": "여의도 한강공원",
        "latitude": 37.5283,
        "longitude": 126.9328
      },
      "startTime": "2026-01-27T14:00:00+09:00",
      "endTime": "2026-01-27T16:00:00+09:00",
      "chatType": "GROUP",
      "currentParticipants": 3,
      "maxParticipants": 5,
      "mainPet": {
        "name": "몽이",
        "photoUrl": "https://s3.../pet.jpg"
      }
    }
  ]
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- 필터 조건(필수 필터 미충족 제외)은 목록 조회와 동일하게 적용.
- 데이터 양을 줄이기 위해 필요한 최소 정보만 반환.

---

### Issue #25: 중복 스레드 확인 API
**Labels**: `priority:low`, `context:walk`, `phase:2`

#### 1. 개요 (Overview)
- 동일 시간대/장소에 중복 작성을 방지하기 위한 사전 체크 API입니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /threads/check-duplicate`

**Query Params**: `placeName`, `latitude`, `longitude`, `date`, `startTime`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "hasDuplicate": true,
    "nearbyThreads": [
      {
        "id": 99,
        "title": "여의도 산책 모집",
        "startTime": "2026-01-27T13:00:00+09:00",
        "endTime": "2026-01-27T15:00:00+09:00",
        "currentParticipants": 2,
        "author": {"nickname": "뭉치맘"}
      }
    ]
  }
}
```

---

## 하늘 - Community APIs

### Issue #26: Comment, PostLike Entity 생성
**Labels**: `priority:high`, `context:community`, `phase:2`, `good-first-issue`

#### 1. 개요 (Overview)
- 댓글과 좋아요 정보를 저장할 Entity를 생성합니다.
- **Objective**: 커뮤니티 게시글에 대한 상호작용(댓글, 좋아요) 데이터 구조를 확립합니다.

#### 2. Entity 명세 (Entity Specs)
- **Comment**:
  - `id` (PK)
  - `post` (Post, ManyToOne): 댓글이 달린 게시글
  - `author` (Member, ManyToOne): 댓글 작성자
  - `content` (String, max 500): 댓글 내용
  - `BaseTimeEntity` 상속 (createdAt, updatedAt)
- **PostLike**:
  - `id` (PK)
  - `post` (Post, ManyToOne): 좋아요한 게시글
  - `member` (Member, ManyToOne): 좋아요한 회원
  - `createdAt` (LocalDateTime): 좋아요 시간
  - Unique Constraint: `(post_id, member_id)` - 중복 좋아요 방지

#### 3. 비즈니스 로직 & 제약조건
- **댓글 내용**: 최대 500자, 빈 내용 불가.
- **좋아요 토글**: 이미 좋아요한 상태에서 다시 호출 시 좋아요 취소.
- **카운트 동기화**:
  - 댓글 작성/삭제 시 `Post.commentCount` 증가/감소.
  - 좋아요 토글 시 `Post.likeCount` 증가/감소.
- **차단 회원 필터링**: 차단한 회원의 댓글은 조회에서 제외.

#### 4. 예외 처리 (CommunityErrorCode)
- `CO001`: 게시물을 찾을 수 없습니다
- `CO002`: 게시물 작성자가 아닙니다
- `CO003`: 댓글을 찾을 수 없습니다
- `CO004`: 댓글 작성자가 아닙니다
- `CO005`: 내용이 너무 깁니다 (게시글 2000자, 댓글 500자)

#### 5. 구현 체크리스트
- [ ] Comment Entity 생성 및 JPA 어노테이션 적용
- [ ] PostLike Entity 생성 (Unique Constraint 포함)
- [ ] content 최대 길이 검증 (@Size 또는 @Column(length=500))
- [ ] 좋아요 토글 로직 구현 (존재하면 삭제, 없으면 생성)
- [ ] Post의 commentCount, likeCount 동기화 로직

---

### Issue #27: 게시물 생성 API
**Labels**: `priority:high`, `context:community`, `phase:2`, `good-first-issue`

#### 1. 개요 (Overview)
- 커뮤니티 게시글을 작성합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `POST /posts`

**Request Body**
```json
{
  "content": "오늘 몽이랑 한강 산책 다녀왔어요! 날씨가 너무 좋았습니다 ☀️",
  "imageUrls": ["https://s3.../post1.jpg", "https://s3.../post2.jpg"]
}
```

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "id": 1000,
    "author": {
      "id": 1,
      "nickname": "몽이아빠",
      "profileImageUrl": "https://s3.../profile.jpg"
    },
    "content": "오늘 몽이랑 한강 산책 다녀왔어요! 날씨가 너무 좋았습니다 ☀️",
    "imageUrls": ["https://s3.../post1.jpg", "https://s3.../post2.jpg"],
    "likeCount": 0,
    "commentCount": 0,
    "createdAt": "2026-01-26T17:00:00+09:00"
  }
}
```

#### 3. 구현 체크리스트
- [ ] 로그인한 사용자만 작성 가능
- [ ] 내용 길이 검증 (최대 2000자)

---

### Issue #28: 게시물 목록 조회 API
**Labels**: `priority:high`, `context:community`, `phase:2`, `good-first-issue`

#### 1. 개요 (Overview)
- 최신순으로 게시글 목록을 무한 스크롤로 조회합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /posts`

**Query Params**: `page`, `size`, `sort`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "content": [
      {
        "id": 1000,
        "author": {
          "id": 1,
          "nickname": "몽이아빠",
          "profileImageUrl": "https://s3.../profile.jpg"
        },
        "content": "오늘 몽이랑 한강 산책 다녀왔어요!",
        "imageUrls": ["https://s3.../post1.jpg"],
        "likeCount": 15,
        "commentCount": 3,
        "isLiked": false,
        "createdAt": "2026-01-26T17:00:00+09:00"
      }
    ],
    "pageNumber": 0,
    "pageSize": 20,
    "first": true,
    "last": false,
    "hasNext": true
  }
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- 차단한 회원의 게시글은 조회되지 않아야 함.

---

### Issue #29: 게시물 상세 조회 API
**Labels**: `priority:high`, `context:community`, `phase:2`, `good-first-issue`

#### 1. 개요 (Overview)
- 게시글 내용과 댓글 목록을 함께 조회합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /posts/{postId}`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "id": 1000,
    "content": "...",
    "comments": [ ... ]
  }
}
```

---

### Issue #30: 게시물 수정 API
**Labels**: `priority:medium`, `context:community`, `phase:2`, `good-first-issue`

#### 1. 개요 (Overview)
- 게시글 내용을 수정합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `PATCH /posts/{postId}`

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- 작성자 본인만 수정 가능 (`CO002`).

---

### Issue #31: 게시물 삭제 API
**Labels**: `priority:medium`, `context:community`, `phase:2`, `good-first-issue`

#### 1. 개요 (Overview)
- 게시글을 삭제합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `DELETE /posts/{postId}`

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- 작성자 본인만 삭제 가능.

---

### Issue #32: 좋아요 토글 API
**Labels**: `priority:high`, `context:community`, `phase:2`, `good-first-issue`

#### 1. 개요 (Overview)
- 게시글에 좋아요를 누르거나 취소합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `POST /posts/{postId}/like`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "isLiked": true,
    "likeCount": 16
  }
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- 이미 좋아요가 있으면 삭제(취소), 없으면 생성(좋아요).
- `Post` 엔티티의 `likeCount`를 동기화(증가/감소).

---

### Issue #33: 댓글 작성 API
**Labels**: `priority:high`, `context:community`, `phase:2`, `good-first-issue`

#### 1. 개요 (Overview)
- 게시글에 댓글을 답니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `POST /posts/{postId}/comments`

**Request Body**
```json
{
  "content": "와 너무 귀여워요! 🐕"
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- 댓글 작성 시 게시글의 `commentCount` 증가.
- 내용 최대 500자.

---

### Issue #34: 댓글 삭제 API
**Labels**: `priority:medium`, `context:community`, `phase:2`, `good-first-issue`

#### 1. 개요 (Overview)
- 댓글을 삭제합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `DELETE /posts/{postId}/comments/{commentId}`

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- 댓글 작성자 본인만 삭제 가능 (`CO004`).

---

## 효주 - Pet APIs

### Issue #35: Pet Entity 완성
**Labels**: `priority:high`, `context:pet`, `phase:2`, `good-first-issue`

#### 1. 개요 (Overview)
- 반려견 정보 및 관련 속성(성격, 산책 스타일)을 저장할 Entity를 구현합니다.
- **Objective**: 반려견의 프로필, 성향, 활동 정보를 관리하는 DB 구조를 확립합니다.

#### 2. Entity 명세 (Entity Specs)
- **Pet**:
  - `id` (PK)
  - `name` (String, max 10자)
  - `age` (Integer)
  - `gender` (Enum: `MALE`, `FEMALE`)
  - `size` (Enum: `SMALL`, `MEDIUM`, `LARGE`)
  - `mbti` (String, max 4, nullable)
  - `isNeutered` (Boolean)
  - `photoUrl` (String): 반려견 프로필 사진 URL
  - `isMain` (Boolean - Default: false): 메인 반려견 여부
  - `certificationNumber` (String, max 15, nullable): 동물등록번호 (15자리 숫자)
  - `breed` (Breed, ManyToOne): 견종 정보
  - `owner` (Member, ManyToOne): 소유자 정보
  - `BaseTimeEntity` 상속 (createdAt, updatedAt)
- **PetPersonality**: `Pet` - `PetPersonalityType` (N:M 매핑)
  - `pet` (Pet, ManyToOne)
  - `personalityType` (PetPersonalityType, ManyToOne)
- **PetWalkingStyle**: `Pet` - `WalkingStyle` (N:M 매핑)
  - `pet` (Pet, ManyToOne)
  - `walkingStyle` (WalkingStyle, ManyToOne)

#### 3. 비즈니스 로직 & 제약조건
- **등록 제한**: 한 회원은 최대 10마리의 반려견만 등록 가능 (`P002`).
- **메인 반려견**: 회원당 반드시 1마리의 메인 반려견이 존재해야 함. 첫 등록 시 자동으로 메인으로 설정.
- **이름**: 최대 10자, 공백만으로 구성될 수 없음 (`P007`, `P008`).
- **동물등록번호**: 15자리 숫자 형식 검증 (`P009`).
- **회원 타입 자동 전환**: 첫 반려견 등록 시 회원의 `memberType`이 `NON_PET_OWNER` → `PET_OWNER`로 변경.
- **마지막 반려견 삭제 시**: 회원의 `memberType`이 `PET_OWNER` → `NON_PET_OWNER`로 변경.

#### 4. 예외 처리 (PetErrorCode)
- `P001`: 반려견을 찾을 수 없습니다
- `P002`: 등록 가능 마릿수(10) 초과
- `P003`: 잘못된 반려견 정보
- `P004`: 존재하지 않는 견종
- `P005`: 존재하지 않는 성향
- `P006`: 본인 소유 반려견이 아님
- `P007`: 반려견 이름이 유효하지 않음 (공백 등)
- `P008`: 반려견 이름 길이 초과 (최대 10자)
- `P009`: 동물등록번호 검증 실패 (15자리 숫자)

#### 5. 구현 체크리스트
- [ ] Pet Entity 생성 및 JPA 어노테이션 적용
- [ ] PetPersonality 중간 테이블 Entity 생성
- [ ] PetWalkingStyle 중간 테이블 Entity 생성
- [ ] Enum 클래스 생성 (PetGender, PetSize)
- [ ] 10마리 제한 로직 구현 (Service 레이어)
- [ ] 메인 반려견 관리 로직 구현
- [ ] 동물등록번호 15자리 숫자 검증 로직 구현

---

### Issue #36: 반려견 등록 API
**Labels**: `priority:critical`, `context:pet`, `phase:2`, `good-first-issue`

#### 1. 개요 (Overview)
- 새로운 반려견을 등록합니다. 첫 등록 시 회원 등급이 `PET_OWNER`로 승격됩니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `POST /pets`

**Request Body**
```json
{
  "name": "몽이",
  "breedId": 15,
  "age": 3,
  "gender": "MALE",
  "size": "SMALL",
  "mbti": "ENFP",
  "isNeutered": true,
  "photoUrl": "https://s3.../pet.jpg",
  "isMain": true,
  "certificationNumber": "410123456789012",
  "walkingStyles": ["ENERGY_BURST", "SNIFF_EXPLORER"],
  "personalityIds": [1, 3, 5]
}
```

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "id": 1,
    "name": "몽이",
    "breed": {
      "id": 15,
      "name": "포메라니안"
    },
    "age": 3,
    "gender": "MALE",
    "size": "SMALL",
    "mbti": "ENFP",
    "isNeutered": true,
    "photoUrl": "https://s3.../pet.jpg",
    "isMain": true,
    "isCertified": true,
    "walkingStyles": ["ENERGY_BURST", "SNIFF_EXPLORER"],
    "personalities": [
      {"id": 1, "name": "에너지넘침"},
      {"id": 3, "name": "사람좋아함"},
      {"id": 5, "name": "친구구함"}
    ],
    "createdAt": "2026-01-26T10:00:00+09:00"
  }
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- **개수 제한**: 이미 10마리면 `P002` 에러.
- **메인 반려견 자동 설정**: 등록된 반려견이 없었다면, 이 반려견을 자동으로 메인으로 설정.
- **회원 등급 변경**: `NON_PET_OWNER`였다면 `PET_OWNER`로 변경.
- **유효성 검사**: 이름(최대 10자, `P008`), 견종 존재 여부(`P004`).
- **동물등록번호 검증 (선택)**: `certificationNumber` 입력 시 공공데이터 API로 검증
  - 형식 검증: 15자리 숫자가 아니면 `P009` 에러
  - 조회 실패: 공공 API 응답 없음/에러 시 `P010` 에러
  - 정보 불일치: 등록된 견종/소유자 정보가 불일치하면 `P011` 에러
  - 검증 성공: `isCertified = true`로 저장, 프로필에 인증마크 표시

**동물등록번호 검증 프로세스**

```
┌─────────────────────────────────────────────────────────────┐
│ 1. certificationNumber 입력 여부 확인                        │
│    └─ 미입력: isCertified = false로 저장 (정상 진행)         │
│    └─ 입력됨: 2단계로 진행                                   │
├─────────────────────────────────────────────────────────────┤
│ 2. 형식 검증 (15자리 숫자)                                   │
│    └─ 실패: P009 (동물등록번호 형식 오류)                    │
│    └─ 성공: 3단계로 진행                                     │
├─────────────────────────────────────────────────────────────┤
│ 3. 공공데이터 API 호출 (농림축산식품부 동물등록정보 조회)     │
│    └─ 응답 없음/에러: P010 (조회 실패)                       │
│    └─ 응답 성공: 4단계로 진행                                │
├─────────────────────────────────────────────────────────────┤
│ 4. 정보 일치 검증 (견종, 소유자명 등)                        │
│    └─ 불일치: P011 (정보 불일치)                             │
│    └─ 일치: isCertified = true로 저장                        │
└─────────────────────────────────────────────────────────────┘
```

**공공데이터 API 연동**
- **API명**: 농림축산식품부_동물등록정보 조회서비스
- **Open API URL**: https://www.data.go.kr/data/15098931/openapi.do
- **인증 방식**: API Key (serviceKey)
- **주요 응답 필드**: 소유자명, 견종명, 등록일자, 중성화 여부

#### 4. 구현 체크리스트
- [ ] 동물등록번호 형식 검증 로직 (15자리 숫자)
- [ ] 공공데이터 API 연동 (RestTemplate/WebClient)
- [ ] API Key 환경변수 설정 (application.yml)
- [ ] 검증 결과에 따른 에러 처리 (P009, P010, P011)
- [ ] isCertified 플래그 업데이트 로직

---

### Issue #37: 내 반려견 목록 조회 API
**Labels**: `priority:high`, `context:pet`, `phase:2`, `good-first-issue`

#### 1. 개요 (Overview)
- 로그인한 사용자의 모든 반려견 목록을 조회합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /pets`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": [
    {
      "id": 1,
      "name": "몽이",
      "breed": {"id": 15, "name": "포메라니안"},
      "age": 3,
      "gender": "MALE",
      "size": "SMALL",
      "photoUrl": "https://s3.../pet.jpg",
      "isMain": true,
      "isCertified": true
    },
    {
      "id": 2,
      "name": "콩이",
      "breed": {"id": 15, "name": "포메라니안"},
      "age": 1,
      "gender": "FEMALE",
      "size": "SMALL",
      "photoUrl": "https://s3.../pet2.jpg",
      "isMain": false,
      "isCertified": false
    }
  ]
}
```

---

### Issue #38: 반려견 수정 API
**Labels**: `priority:medium`, `context:pet`, `phase:2`, `good-first-issue`

#### 1. 개요 (Overview)
- 반려견의 정보(이름, 사진, 성향 등)를 수정합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `PATCH /pets/{petId}`

**Request Body** (수정할 필드만 포함)
```json
{
  "name": "뽀삐2",
  "age": 4,
  "personalityIds": [1, 2],
  "walkingStyleCodes": ["RELAXED"]
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- **권한 확인**: 본인의 반려견이 아니면 `P006`.
- 견종(Breed) 변경은 불가능하도록 제한.

---

### Issue #39: 반려견 삭제 API
**Labels**: `priority:medium`, `context:pet`, `phase:2`, `good-first-issue`

#### 1. 개요 (Overview)
- 반려견을 삭제합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `DELETE /pets/{petId}`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": null
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- **메인 반려견 삭제 시**: 남은 반려견 중 하나를 자동으로 메인으로 승격.
- **마지막 반려견 삭제 시**: 회원을 `NON_PET_OWNER`로 강등.

---

### Issue #40: 메인 반려견 변경 API
**Labels**: `priority:medium`, `context:pet`, `phase:2`, `good-first-issue`

#### 1. 개요 (Overview)
- 대표(메인) 반려견을 변경합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `PATCH /pets/{petId}/main`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "id": 2,
    "name": "콩이",
    "isMain": true
  }
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- 기존 메인 반려견의 `isMain`을 `false`로, 대상 반려견을 `true`로 변경 (Atomic하게 처리).

---

### Issue #41: 견종 목록 조회 API
**Labels**: `priority:high`, `context:pet`, `phase:2`, `good-first-issue`

#### 1. 개요 (Overview)
- 견종 선택을 위한 마스터 데이터 목록을 제공합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /breeds`

**Query Params**: `search`, `size`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": [
    {"id": 1, "name": "골든 리트리버", "size": "LARGE"},
    {"id": 2, "name": "래브라도 리트리버", "size": "LARGE"},
    {"id": 15, "name": "포메라니안", "size": "SMALL"}
  ]
}
```

---

### Issue #42: 반려견 성격 유형 목록 API
**Labels**: `priority:medium`, `context:pet`, `phase:2`, `good-first-issue`

#### 1. 개요 (Overview)
- 반려견 성격 선택을 위한 마스터 데이터를 제공합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /personalities`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": [
    {"id": 1, "name": "소심해요", "code": "SHY"},
    {"id": 2, "name": "에너지넘침", "code": "ENERGETIC"},
    {"id": 3, "name": "간식좋아함", "code": "TREAT_LOVER"},
    {"id": 4, "name": "사람좋아함", "code": "PEOPLE_LOVER"},
    {"id": 5, "name": "친구구함", "code": "SEEKING_FRIENDS"},
    {"id": 6, "name": "주인바라기", "code": "OWNER_FOCUSED"},
    {"id": 7, "name": "까칠해요", "code": "GRUMPY"}
  ]
}
```

---

### Issue #43: 산책 스타일 목록 API
**Labels**: `priority:medium`, `context:pet`, `phase:2`, `good-first-issue`

#### 1. 개요 (Overview)
- 산책 스타일 선택을 위한 마스터 데이터를 제공합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /walking-styles`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": [
    {"id": 1, "name": "전력질주", "code": "ENERGY_BURST"},
    {"id": 2, "name": "냄새맡기집중", "code": "SNIFF_EXPLORER"},
    {"id": 3, "name": "공원벤치휴식형", "code": "BENCH_REST"},
    {"id": 4, "name": "느긋함", "code": "RELAXED"},
    {"id": 5, "name": "냄새탐정", "code": "SNIFF_DETECTIVE"},
    {"id": 6, "name": "무한동력", "code": "ENDLESS_ENERGY"},
    {"id": 7, "name": "저질체력", "code": "LOW_STAMINA"}
  ]
}
```

---

# 📌 Phase 3: Advanced Features (고급 기능)

## 동욱 - Block APIs

### Issue #44: 차단 API
**Labels**: `priority:medium`, `context:member`, `phase:3`

#### 1. 개요 (Overview)
- 특정 회원을 차단하여 상호 작용(스레드 조회, 채팅 신청 등)을 제한합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `POST /blocks`

**Request Body**
```json
{
  "targetMemberId": 5
}
```

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": null
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- **상호 차단 효과**:
  - 차단한/된 사용자의 스레드 미노출.
  - 채팅 신청 불가 (`M006`).
- 자기 자신 차단 불가.

---

### Issue #45: 차단 목록 조회 API
**Labels**: `priority:medium`, `context:member`, `phase:3`

#### 1. 개요 (Overview)
- 내가 차단한 회원 목록을 조회합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /blocks`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": [
    {
      "id": 10,
      "blockedMember": {
        "id": 5,
        "nickname": "차단된사람",
        "profileImageUrl": "https://s3.../profile.jpg"
      },
      "createdAt": "2026-01-26T18:00:00+09:00"
    }
  ]
}
```

---

### Issue #46: 차단 해제 API
**Labels**: `priority:medium`, `context:member`, `phase:3`

#### 1. 개요 (Overview)
- 차단을 해제합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `DELETE /blocks/{blockId}`

---

## 건홍 - Chat APIs

### Issue #47: Chat Entity 생성
**Labels**: `priority:high`, `context:chat`, `phase:3`

#### 1. 개요 (Overview)
- 채팅방, 참여자, 메시지 저장을 위한 Entity를 설계합니다.
- **Objective**: 산책 채팅, 실종 매칭 채팅 등 다양한 목적의 채팅을 통합 관리하는 구조를 만듭니다.

#### 2. Entity 명세 (Entity Specs)
- **ChatRoom**:
  - `id` (PK)
  - `roomPurpose` (Enum: `WALK`, `LOST_PET_MATCH`): 채팅방 목적
  - `chatType` (Enum: `INDIVIDUAL`, `GROUP`): 채팅 방식
  - `threadId` (Long, Nullable): 산책 스레드 ID (WALK 목적일 때)
  - `lostPetMatchId` (Long, Nullable): 실종 매칭 ID (LOST_PET_MATCH 목적일 때)
  - `status` (Enum: `ACTIVE`, `ARCHIVED`): 채팅방 상태
  - `chatEndTime` (LocalDateTime): 채팅 가능 종료 시간 (산책 종료 +2시간)
  - `BaseTimeEntity` 상속 (createdAt, updatedAt)
- **ChatParticipant**:
  - `id` (PK)
  - `chatRoom` (ChatRoom, ManyToOne)
  - `member` (Member, ManyToOne)
  - `isAuthor` (Boolean): 스레드 작성자 여부
  - `walkConfirmedAt` (LocalDateTime, Nullable): 산책 확정 시간 (1:1 채팅에서 사용)
  - `joinedAt` (LocalDateTime): 채팅방 참여 시간
  - Unique Constraint: `(chat_room_id, member_id)` 중복 참여 방지
- **ChatParticipantPet**: 참여자와 참여 반려견 N:M 연결
  - `chatParticipant` (ChatParticipant, ManyToOne)
  - `pet` (Pet, ManyToOne)
- **Message**:
  - `id` (PK)
  - `chatRoom` (ChatRoom, ManyToOne)
  - `sender` (Member, ManyToOne, Nullable): SYSTEM 메시지일 때 null
  - `content` (String, max 500): 메시지 내용
  - `messageType` (Enum: `USER`, `SYSTEM`): 메시지 유형
  - `sentAt` (LocalDateTime): 전송 시간
- **Review** (산책 후기):
  - `id` (PK)
  - `chatRoom` (ChatRoom, ManyToOne)
  - `reviewer` (Member, ManyToOne): 작성자
  - `targetMember` (Member, ManyToOne): 평가 대상
  - `score` (Integer, 1~10): 매너 점수
  - `comment` (String, Nullable): 후기 코멘트
  - `createdAt` (LocalDateTime)
  - Unique Constraint: `(chat_room_id, reviewer_id, target_member_id)` 중복 후기 방지

#### 3. 비즈니스 로직 & 제약조건
- **채팅 가능 시간**: 스레드 작성 시점 ~ 산책 종료 시간 +2시간.
- **산책 확정 (1:1)**: 작성자는 한 스레드 내에서 1개의 채팅방만 확정 가능.
- **확정 취소**: 상대방이 아직 확정하지 않은 경우에만 가능.
- **후기 작성 조건**:
  - 산책 시간 종료 후.
  - 1:1 채팅: 양쪽 모두 산책 확정된 상태.
  - 그룹 채팅: 채팅방 참여자끼리.
- **시스템 메시지 예시**:
  - "OO님이 참여했습니다"
  - "OO님이 나가셨습니다"
  - "스레드 정보가 수정되었습니다"
  - "채팅방이 종료되었습니다"

#### 4. 예외 처리 (ChatErrorCode)
- `CH001`: 메시지 길이 초과 (500자)
- `CH002`: 아카이브된 채팅방
- `CH003`: 채팅방 참여자가 아님
- `CH004`: 산책 채팅방이 아님
- `CH005`: 1:1 채팅방이 아님
- `CH006`: 이미 산책 확정함
- `CH007`: 작성자가 동일 스레드의 다른 채팅방을 이미 확정함
- `CH008`: 아직 산책 확정하지 않음
- `CH009`: 상대가 이미 확정하여 취소할 수 없음
- `CH010`: 점수 범위 오류 (1~10)
- `CH011`: 대상 회원이 유효하지 않음 (본인/미참여자)
- `CH012`: 산책 참가 확정이 필요함 (1:1 채팅 후기)
- `CH013`: 산책이 아직 종료되지 않음
- `CH014`: 이미 후기 작성함

#### 5. 구현 체크리스트
- [ ] ChatRoom Entity 생성 및 JPA 어노테이션 적용
- [ ] ChatParticipant Entity 생성 (Unique Constraint 포함)
- [ ] ChatParticipantPet 중간 테이블 Entity 생성
- [ ] Message Entity 생성
- [ ] Review Entity 생성 (Unique Constraint 포함)
- [ ] Enum 클래스 생성 (RoomPurpose, ChatRoomStatus, MessageType)
- [ ] 산책 확정 로직 설계 (작성자 1명 제한)
- [ ] chatEndTime 자동 계산 로직 (산책 종료 +2시간)

---

### Issue #48: 채팅방 목록 조회 API
**Labels**: `priority:high`, `context:chat`, `phase:3`

#### 1. 개요 (Overview)
- 내 채팅방 목록을 조회합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /chat-rooms`

**Query Params**: `status`, `page`, `size`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "content": [
      {
        "id": 500,
        "roomPurpose": "WALK",
        "threadId": 100,
        "lostPetMatchId": null,
        "chatType": "GROUP",
        "status": "ACTIVE",
        "thread": {
          "id": 100,
          "title": "한강공원 같이 산책해요",
          "startTime": "2026-01-27T14:00:00+09:00",
          "location": {"placeName": "여의도 한강공원"}
        },
        "participantCount": 3,
        "lastMessage": {
          "content": "안녕하세요! 내일 뵙겠습니다~",
          "senderNickname": "뭉치맘",
          "sentAt": "2026-01-26T15:30:00+09:00"
        },
        "createdAt": "2026-01-26T10:30:00+09:00"
      },
      {
        "id": 600,
        "roomPurpose": "LOST_PET_MATCH",
        "threadId": null,
        "lostPetMatchId": 10,
        "chatType": "INDIVIDUAL",
        "status": "ACTIVE",
        "lostPetMatch": {
          "id": 10,
          "lostPetId": 50,
          "sightingId": 200
        },
        "participantCount": 2,
        "lastMessage": {
          "content": "사진 다시 한번 확인 부탁드려요",
          "senderNickname": "길가던사람",
          "sentAt": "2026-01-26T16:10:00+09:00"
        },
        "createdAt": "2026-01-26T16:00:00+09:00"
      }
    ],
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 2,
    "totalPages": 1,
    "first": true,
    "last": true
  }
}
```

---

### Issue #49: 채팅방 상세 조회 API
**Labels**: `priority:high`, `context:chat`, `phase:3`

#### 1. 개요 (Overview)
- 특정 채팅방의 상세 정보와 참여자 목록을 조회합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /chat-rooms/{chatRoomId}`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "id": 500,
    "roomPurpose": "WALK",
    "threadId": 100,
    "lostPetMatchId": null,
    "chatType": "GROUP",
    "status": "ACTIVE",
    "thread": {
      "id": 100,
      "title": "한강공원 같이 산책해요",
      "description": "오후에 한강공원에서 산책해요! 대형견 환영합니다~",
      "walkDate": "2026-01-27",
      "startTime": "2026-01-27T14:00:00+09:00",
      "endTime": "2026-01-27T16:00:00+09:00",
      "location": {"placeName": "여의도 한강공원"}
    },
    "participants": [
      {
        "memberId": 1,
        "nickname": "몽이아빠",
        "memberType": "PET_OWNER",
        "profileImageUrl": "https://s3.../profile.jpg",
        "isAuthor": true,
        "joinedAt": "2026-01-26T10:00:00+09:00",
        "pets": [
          {"id": 1, "name": "몽이", "photoUrl": "https://s3.../pet.jpg"}
        ]
      },
      {
        "memberId": 2,
        "nickname": "뭉치맘",
        "memberType": "NON_PET_OWNER",
        "profileImageUrl": "https://s3.../profile2.jpg",
        "isAuthor": false,
        "joinedAt": "2026-01-26T10:30:00+09:00"
      }
    ],
    "chatEndTime": "2026-01-27T18:00:00+09:00",
    "createdAt": "2026-01-26T10:00:00+09:00"
  }
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- 참여자가 아니면 조회 불가 (`CH003`).

---

### Issue #50: 메시지 목록 조회 API
**Labels**: `priority:high`, `context:chat`, `phase:3`

#### 1. 개요 (Overview)
- 채팅방의 메시지를 커서 기반으로 페이징 조회합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /chat-rooms/{chatRoomId}/messages`

**Query Params**: `cursor`, `size`, `direction`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "content": [
      {
        "id": 1001,
        "chatRoomId": 500,
        "messageType": "USER",
        "content": "안녕하세요! 반갑습니다~",
        "sender": {
          "memberId": 1,
          "nickname": "몽이아빠",
          "memberType": "PET_OWNER",
          "profileImageUrl": "https://s3.../profile.jpg",
          "mainPetPhotoUrl": "https://s3.../pet.jpg"
        },
        "sentAt": "2026-01-26T10:30:00+09:00"
      },
      {
        "id": 1002,
        "chatRoomId": 500,
        "messageType": "SYSTEM",
        "content": "뭉치맘님이 참여했습니다",
        "sender": null,
        "sentAt": "2026-01-26T10:35:00+09:00"
      }
    ],
    "nextCursor": "1000",
    "hasMore": true
  }
}
```

---

### Issue #51: 메시지 전송 API
**Labels**: `priority:high`, `context:chat`, `phase:3`

#### 1. 개요 (Overview)
- 채팅방에 텍스트 메시지를 전송합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `POST /chat-rooms/{chatRoomId}/messages`

**Request Body**
```json
{
  "content": "안녕하세요! 내일 뵙겠습니다~"
}
```

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "id": 1003,
    "chatRoomId": 500,
    "messageType": "USER",
    "content": "안녕하세요! 내일 뵙겠습니다~",
    "sentAt": "2026-01-26T15:30:00+09:00"
  }
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- 500자 초과 시 `CH001`.
- `ARCHIVED` 상태인 방에는 전송 불가 (`CH002`).

---

### Issue #52: 채팅방 나가기 API
**Labels**: `priority:medium`, `context:chat`, `phase:3`

#### 1. 개요 (Overview)
- 채팅방을 나갑니다. 스레드/매칭 참여도 취소됩니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `DELETE /chat-rooms/{chatRoomId}/leave`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": null
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- 시스템 메시지("OO님이 나갔습니다") 생성.

---

### Issue #53: 산책 확정 API
**Labels**: `priority:high`, `context:chat`, `phase:3`

#### 1. 개요 (Overview)
- 1:1 채팅방에서 "산책 확정" 의사를 표시합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `POST /chat-rooms/{chatRoomId}/walk-confirm`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "chatRoomId": 500,
    "myConfirmedAt": "2026-01-27T10:00:00+09:00",
    "otherConfirmedAt": null,
    "isWalkConfirmed": false
  }
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- **제약**: 1:1 방(`INDIVIDUAL`)만 가능 (`CH005`).
- 작성자는 동일 스레드 내 다른 채팅방에서 이미 확정했으면 불가 (`CH007`).

---

### Issue #54: 산책 확정 상태 조회 API
**Labels**: `priority:medium`, `context:chat`, `phase:3`

#### 1. 개요 (Overview)
- 현재 채팅방의 산책 확정 현황(나/상대)을 조회합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /chat-rooms/{chatRoomId}/walk-confirm`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "chatRoomId": 500,
    "myConfirmedAt": "2026-01-27T10:00:00+09:00",
    "otherConfirmedAt": "2026-01-27T10:05:00+09:00",
    "isWalkConfirmed": true
  }
}
```

---

### Issue #55: 산책 확정 취소 API
**Labels**: `priority:medium`, `context:chat`, `phase:3`

#### 1. 개요 (Overview)
- 산책 확정을 취소합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `DELETE /chat-rooms/{chatRoomId}/walk-confirm`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "chatRoomId": 500,
    "myConfirmedAt": null,
    "otherConfirmedAt": null,
    "isWalkConfirmed": false
  }
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- 상대방이 아직 확정하지 않은 상태에서만 취소 가능 (`CH009`).

---

### Issue #56: 매너 후기 작성 API
**Labels**: `priority:high`, `context:chat`, `phase:3`

#### 1. 개요 (Overview)
- 산책 종료 후 상호 간에 매너 점수와 후기를 남깁니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `POST /chat-rooms/{chatRoomId}/reviews`

**Request Body**
```json
{
  "targetMemberId": 2,
  "score": 9,
  "comment": "약속 시간 잘 지키고 배려심이 있어요!"
}
```

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "id": 900,
    "chatRoomId": 500,
    "reviewerId": 1,
    "targetMemberId": 2,
    "score": 9,
    "comment": "약속 시간 잘 지키고 배려심이 있어요!",
    "createdAt": "2026-01-27T18:10:00+09:00"
  }
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- **작성 조건**: 
  - 산책 시간 종료 후 (`CH013`).
  - 1:1은 확정된 상태여야 함 (`CH012`).
  - 참여자끼리만 가능 (`CH003`).
- **매너 온도 업데이트**: 작성 시 대상 회원의 평균 점수 재계산.

---

### Issue #57: 내 후기 조회 API
**Labels**: `priority:low`, `context:chat`, `phase:3`

#### 1. 개요 (Overview)
- 특정 채팅방에서 내가 작성한 후기를 조회합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /chat-rooms/{chatRoomId}/reviews/me`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": [
    {
      "id": 900,
      "targetMemberId": 2,
      "score": 9,
      "comment": "약속 시간 잘 지키고 배려심이 있어요!",
      "createdAt": "2026-01-27T18:10:00+09:00"
    }
  ]
}
```

---

## 건홍 - LostPet APIs

### Issue #58: LostPet Entity 생성
**Labels**: `priority:high`, `context:lostpet`, `phase:3`

#### 1. 개요 (Overview)
- 실종 반려견, 제보, 매칭 정보를 저장할 Entity를 생성합니다.
- **Objective**: AI 기반 유사도 검색을 위한 벡터 저장 구조와 견주-제보자 연결 구조를 확립합니다.

#### 2. Entity 명세 (Entity Specs)
- **LostPetReport** (실종 신고):
  - `id` (PK)
  - `pet` (Pet, ManyToOne): 실종된 반려견
  - `reporter` (Member, ManyToOne): 신고자 (반려견 주인)
  - `photoUrls` (List<String>, JSON): 실종견 사진 URL 목록
  - `croppedPhotoUrl` (String): YOLO로 크롭된 강아지 사진 URL
  - `imageEmbedding` (Vector): CLIP 이미지 임베딩 벡터 (MySQL 9+ 벡터)
  - `textFeatures` (String, Nullable): 텍스트 특징 설명 (예: "갈색 포메라니안, 빨간 목줄")
  - `textEmbedding` (Vector, Nullable): CLIP 텍스트 임베딩 벡터
  - `description` (String): 상세 설명
  - `lastSeenLocation` (Embedded Location VO): 마지막 목격 장소
  - `lastSeenAt` (LocalDateTime): 마지막 목격 시간
  - `status` (Enum: `SEARCHING`, `FOUND`, `CANCELLED`): 신고 상태
  - `isNotificationEnabled` (Boolean): 유사 제보 알림 수신 여부
  - `closedAt` (LocalDateTime, Nullable): 종료 시간
  - `BaseTimeEntity` 상속 (createdAt, updatedAt)
- **Sighting** (제보):
  - `id` (PK)
  - `finder` (Member, ManyToOne): 제보자
  - `photoUrl` (String): 발견한 강아지 사진 URL
  - `croppedPhotoUrl` (String): YOLO로 크롭된 강아지 사진 URL
  - `imageEmbedding` (Vector): CLIP 이미지 임베딩 벡터
  - `description` (String, Nullable): 상세 설명
  - `foundLocation` (Embedded Location VO): 발견 장소
  - `foundAt` (LocalDateTime): 발견 시간
  - `status` (Enum: `ACTIVE`, `MATCHED`, `DELETED`): 제보 상태
  - `BaseTimeEntity` 상속 (createdAt, updatedAt)
- **LostPetMatch** (매칭 - 견주가 "이 아이예요!" 클릭 시 생성):
  - `id` (PK)
  - `lostPetReport` (LostPetReport, ManyToOne)
  - `sighting` (Sighting, ManyToOne)
  - `chatRoomId` (Long): 생성된 채팅방 ID
  - `similarityScore` (Double): 유사도 점수
  - `BaseTimeEntity` 상속 (createdAt)

#### 3. 비즈니스 로직 & 제약조건
- **YOLO 객체 탐지**: 업로드된 사진에서 강아지 감지 실패 시 `L001` 에러.
- **CLIP 임베딩**: 크롭된 이미지와 텍스트 특징을 동일 벡터 공간에 임베딩.
- **중복 신고 방지**: 이미 SEARCHING 상태인 반려견은 재신고 불가 (`L002`).
- **유사도 계산 공식**:
  ```
  최종 점수 = (멀티모달 유사도 × 0.5) + (지역 근접도 × 0.3) + (시간 근접도 × 0.2)
  ```
- **알림**: 새 제보 등록 시 유사한 실종 신고 건주에게 다이제스트 알림.

#### 4. 예외 처리 (LostPetErrorCode)
- `L001`: 사진에서 강아지가 감지되지 않음
- `L002`: 이미 실종 신고된 반려견
- `L003`: 본인 제보가 아님
- `L004`: 제보를 찾을 수 없음

#### 5. 구현 체크리스트
- [ ] LostPetReport Entity 생성 및 JPA 어노테이션 적용
- [ ] Sighting Entity 생성
- [ ] LostPetMatch Entity 생성
- [ ] Enum 클래스 생성 (LostPetStatus, SightingStatus)
- [ ] Location Embeddable 클래스 연동 (Issue #2)
- [ ] MySQL 9+ Vector 타입 컬럼 설정
- [ ] photoUrls JSON 컬럼 저장 방식 결정

---

### Issue #59: 실종 신고 API
**Labels**: `priority:high`, `context:lostpet`, `phase:3`

#### 1. 개요 (Overview)
- 실종 반려견 정보를 등록하고 AI 분석(Embedding)을 요청합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `POST /lost-pets`

**Request Body**
```json
{
  "petId": 1,
  "photoUrls": ["https://s3.../lost1.jpg", "https://s3.../lost2.jpg"],
  "textFeatures": "갈색 포메라니안, 엉덩이에 검은 점, 빨간 목줄 착용",
  "description": "한강공원에서 산책 중 놓쳤습니다. 겁이 많아서 도망칠 수 있어요.",
  "lastSeenLocation": {
    "placeName": "여의도 한강공원",
    "latitude": 37.5283,
    "longitude": 126.9328,
    "address": "서울특별시 영등포구 여의동로 330"
  },
  "lastSeenAt": "2026-01-26T14:30:00+09:00",
  "isNotificationEnabled": true
}
```

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "id": 50,
    "pet": {
      "id": 1,
      "name": "몽이",
      "breed": "포메라니안",
      "photoUrl": "https://s3.../pet.jpg"
    },
    "photoUrls": ["https://s3.../lost1.jpg"],
    "croppedPhotoUrl": "https://s3.../lost1_cropped.jpg",
    "textFeatures": "갈색 포메라니안, 엉덩이에 검은 점, 빨간 목줄 착용",
    "description": "한강공원에서 산책 중 놓쳤습니다.",
    "lastSeenLocation": {
      "placeName": "여의도 한강공원",
      "latitude": 37.5283,
      "longitude": 126.9328
    },
    "lastSeenAt": "2026-01-26T14:30:00+09:00",
    "status": "SEARCHING",
    "isNotificationEnabled": true,
    "similarSightingsCount": 3,
    "createdAt": "2026-01-26T15:00:00+09:00"
  }
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- **AI 처리**: 
  - 업로드된 사진에서 강아지 객체 탐지(YOLO) -> 실패 시 `L001`.
  - CLIP 모델로 이미지 임베딩 추출 및 저장 (벡터 검색용).
- 이미 신고된 반려견이면 `L002`.

---

### Issue #60: 내 실종 신고 목록 API
**Labels**: `priority:medium`, `context:lostpet`, `phase:3`

#### 1. 개요 (Overview)
- 내가 등록한 실종 신고 목록을 조회합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /lost-pets/mine`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": [
    {
      "id": 50,
      "pet": {"id": 1, "name": "몽이", "photoUrl": "https://s3.../pet.jpg"},
      "status": "SEARCHING",
      "lastSeenAt": "2026-01-26T14:30:00+09:00",
      "newSightingsCount": 2,
      "createdAt": "2026-01-26T15:00:00+09:00"
    }
  ]
}
```

---

### Issue #61: 실종 신고 상세 API
**Labels**: `priority:medium`, `context:lostpet`, `phase:3`

#### 1. 개요 (Overview)
- 실종 신고 상세 정보를 조회합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /lost-pets/{lostPetId}`

**Response Body**
- (상세 정보 반환, JSON 예시 생략되지 않음)

---

### Issue #62: 유사 제보 목록 API
**Labels**: `priority:high`, `context:lostpet`, `phase:3`

#### 1. 개요 (Overview)
- 실종견과 유사도가 높은 제보(Sighting) 목록을 조회합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /lost-pets/{lostPetId}/similar-sightings`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "content": [
      {
        "id": 200,
        "photoUrl": "https://s3.../sighting1.jpg",
        "foundLocation": {
          "placeName": "영등포 타임스퀘어 근처",
          "latitude": 37.5170,
          "longitude": 126.9033
        },
        "foundAt": "2026-01-26T16:00:00+09:00",
        "similarityScore": {
          "total": 0.78,
          "image": 0.85,
          "location": 0.6,
          "time": 0.9
        },
        "distanceKm": 3.2,
        "hoursAgo": 2
      }
    ],
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 8,
    "totalPages": 1,
    "first": true,
    "last": true
  }
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- **유사도 계산**: Vector Similarity(0.5) + Distance(0.3) + Time(0.2) 가중치 적용.

---

### Issue #63: 실종견 매칭 API
**Labels**: `priority:high`, `context:lostpet`, `phase:3`

#### 1. 개요 (Overview)
- 견주가 "이 아이예요!"를 선택하여 제보자와 연결(매칭)합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `POST /lost-pets/{lostPetId}/match`

**Request Body**
```json
{
  "sightingId": 200
}
```

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "chatRoomId": 600,
    "sighting": {
      "id": 200,
      "finder": {
        "id": 5,
        "nickname": "길가던사람"
      }
    }
  }
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- `LOST_PET_MATCH` 목적의 1:1 채팅방 자동 생성.

---

### Issue #64: 실종 신고 종료 API
**Labels**: `priority:medium`, `context:lostpet`, `phase:3`

#### 1. 개요 (Overview)
- 반려견을 찾았거나 신고를 취소하여 상태를 변경합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `PATCH /lost-pets/{lostPetId}/close`

**Request Body**
```json
{
  "reason": "FOUND"
}
```

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "id": 50,
    "status": "FOUND",
    "closedAt": "2026-01-26T18:00:00+09:00"
  }
}
```

---

### Issue #65: 제보 등록 API
**Labels**: `priority:high`, `context:lostpet`, `phase:3`

#### 1. 개요 (Overview)
- 발견한 유기견/미아견 정보를 등록합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `POST /sightings`

**Request Body**
```json
{
  "photoUrl": "https://s3.../found_dog.jpg",
  "description": "공원 벤치 근처에서 배회하고 있었어요",
  "foundLocation": {
    "placeName": "영등포 타임스퀘어 근처",
    "latitude": 37.5170,
    "longitude": 126.9033,
    "address": "서울특별시 영등포구 영중로 15"
  },
  "foundAt": "2026-01-26T16:00:00+09:00"
}
```

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "id": 201,
    "photoUrl": "https://s3.../found_dog.jpg",
    "croppedPhotoUrl": "https://s3.../found_dog_cropped.jpg",
    "description": "공원 벤치 근처에서 배회하고 있었어요",
    "foundLocation": {
      "placeName": "영등포 타임스퀘어 근처",
      "latitude": 37.5170,
      "longitude": 126.9033
    },
    "foundAt": "2026-01-26T16:00:00+09:00",
    "potentialMatchCount": 2,
    "createdAt": "2026-01-26T16:30:00+09:00"
  }
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- 등록 즉시 임베딩 생성하여 유사한 실종 신고 건 검색 -> 견주들에게 알림 발송.

---

### Issue #66: 내 제보 목록 API
**Labels**: `priority:low`, `context:lostpet`, `phase:3`

#### 1. 개요 (Overview)
- 내가 등록한 제보 목록을 조회합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /sightings/mine`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "content": [
      {
        "id": 201,
        "photoUrl": "https://s3.../found_dog.jpg",
        "foundLocation": {
          "placeName": "영등포 타임스퀘어 근처",
          "latitude": 37.5170,
          "longitude": 126.9033
        },
        "foundAt": "2026-01-26T16:00:00+09:00",
        "status": "ACTIVE",
        "potentialMatchCount": 2,
        "createdAt": "2026-01-26T16:30:00+09:00"
      }
    ],
    "pageNumber": 0,
    "pageSize": 20,
    "totalElements": 1,
    "totalPages": 1,
    "first": true,
    "last": true
  }
}
```

---

### Issue #67: 제보 상세 API
**Labels**: `priority:low`, `context:lostpet`, `phase:3`

#### 1. 개요 (Overview)
- 제보 상세 정보를 조회합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /sightings/{sightingId}`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "id": 201,
    "photoUrl": "https://s3.../found_dog.jpg",
    "croppedPhotoUrl": "https://s3.../found_dog_cropped.jpg",
    "description": "공원 벤치 근처에서 배회하고 있었어요",
    "foundLocation": {
      "placeName": "영등포 타임스퀘어 근처",
      "latitude": 37.5170,
      "longitude": 126.9033,
      "address": "서울특별시 영등포구 영중로 15"
    },
    "foundAt": "2026-01-26T16:00:00+09:00",
    "status": "ACTIVE",
    "createdAt": "2026-01-26T16:30:00+09:00",
    "updatedAt": "2026-01-26T16:30:00+09:00"
  }
}
```

---

### Issue #68: 제보 삭제 API
**Labels**: `priority:low`, `context:lostpet`, `phase:3`

#### 1. 개요 (Overview)
- 제보를 삭제합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `DELETE /sightings/{sightingId}`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": null
}
```

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- **Security**: 본인 제보만 삭제 가능 (`L003`).

---

# 📌 Phase 4: Integration (통합 및 알림)

## 건홍 - Notification APIs

### Issue #69: Notification Entity 생성
**Labels**: `priority:medium`, `context:notification`, `phase:4`

#### 1. 개요 (Overview)
- 알림 데이터 및 사용자별 알림 설정을 저장할 Entity를 생성합니다.
- **Objective**: 채팅 메시지, 산책 신청, 실종 유사 제보 등 다양한 알림을 통합 관리합니다.

#### 2. Entity 명세 (Entity Specs)
- **Notification**:
  - `id` (PK)
  - `member` (Member, ManyToOne): 알림 수신자
  - `type` (Enum: `CHAT_MESSAGE`, `WALK_APPLICATION`, `LOST_PET_SIMILAR`): 알림 유형
  - `title` (String): 알림 제목 (예: "새 메시지", "산책 신청")
  - `content` (String): 알림 내용 (예: "몽이아빠님이 메시지를 보냈습니다")
  - `targetType` (Enum: `CHAT_ROOM`, `THREAD`, `LOST_PET`): 대상 리소스 유형
  - `targetId` (Long): 대상 리소스 ID
  - `isRead` (Boolean, Default false): 읽음 여부
  - `createdAt` (LocalDateTime): 생성 시간
- **NotificationSetting**:
  - `id` (PK)
  - `member` (Member, OneToOne): 회원
  - `chatMessage` (Boolean, Default true): 채팅 메시지 알림 수신
  - `walkApplication` (Boolean, Default true): 산책 신청 알림 수신
  - `lostPetSimilar` (Boolean, Default true): 실종 유사 제보 알림 수신
  - Unique Constraint: `member_id`

#### 3. 비즈니스 로직 & 제약조건
- **기본 설정**: 회원 가입 시 모든 알림 ON 상태로 NotificationSetting 자동 생성.
- **알림 발송 조건**:
  - 해당 알림 유형이 ON 상태일 때만 발송.
  - 채팅 메시지: 수신자가 해당 채팅방에 접속 중이 아닐 때만.
- **읽음 처리**: 개별 읽음, 전체 읽음 지원.

#### 4. 알림 유형별 발송 조건

| 알림 유형 | 발송 조건 | targetType | targetId |
|----------|----------|------------|----------|
| CHAT_MESSAGE | 새 메시지 수신 시 (미접속 시) | CHAT_ROOM | chatRoomId |
| WALK_APPLICATION | 내 스레드에 새 참여자 입장 시 | THREAD | threadId |
| LOST_PET_SIMILAR | 내 실종 신고에 유사 제보 등록 시 | LOST_PET | lostPetReportId |

#### 5. 구현 체크리스트
- [ ] Notification Entity 생성 및 JPA 어노테이션 적용
- [ ] NotificationSetting Entity 생성 (OneToOne 관계)
- [ ] Enum 클래스 생성 (NotificationType, TargetType)
- [ ] 회원 가입 시 NotificationSetting 자동 생성 로직
- [ ] 알림 설정 확인 후 발송 로직 구현

---

### Issue #70: 알림 목록 조회 API
**Labels**: `priority:medium`, `context:notification`, `phase:4`

#### 1. 개요 (Overview)
- 내 알림 목록을 페이징 조회합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /notifications`

**Response Body**
```json
{
  "success": true,
  "status": 200,
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
  }
}
```

---

### Issue #71: 알림 읽음 처리 API
**Labels**: `priority:medium`, `context:notification`, `phase:4`

#### 1. 개요 (Overview)
- 특정 알림을 읽음 상태로 변경합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `PATCH /notifications/{notificationId}/read`

---

### Issue #72: 전체 알림 읽음 처리 API
**Labels**: `priority:low`, `context:notification`, `phase:4`

#### 1. 개요 (Overview)
- 모든 안 읽은 알림을 읽음 처리합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `PATCH /notifications/read-all`

---

### Issue #73: 알림 설정 조회 API
**Labels**: `priority:low`, `context:notification`, `phase:4`

#### 1. 개요 (Overview)
- 내 알림 수신 설정(ON/OFF)을 조회합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `GET /notification-settings`

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "chatMessage": true,
    "walkApplication": true,
    "lostPetSimilar": true
  }
}
```

---

### Issue #74: 알림 설정 수정 API
**Labels**: `priority:low`, `context:notification`, `phase:4`

#### 1. 개요 (Overview)
- 알림 수신 설정을 변경합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `PATCH /notification-settings`

**Request Body**
```json
{
  "chatMessage": false
}
```

---

### Issue #75: 이벤트 리스너 구현
**Labels**: `priority:high`, `context:notification`, `phase:4`

#### 1. 개요 (Overview)
- 시스템 내 주요 이벤트 발생 시 알림을 생성하는 리스너를 구현합니다.
- **Objective**: 비즈니스 로직과 알림 로직을 분리(Decoupling)합니다.

#### 2. 구현 상세
- **ChatEventListener**: 메시지 수신 시 상대방에게 알림 (채팅방 미접속 시).
- **WalkEventListener**: 산책 신청 접수, 스레드 수정/삭제 시 참여자에게 알림.
- **LostPetEventListener**: 유사 제보 등록 시 견주에게 알림.

---

## 전체 - 이미지 업로드 API

### Issue #76: Presigned URL 발급 API
**Labels**: `priority:high`, `context:common`, `phase:2`

#### 1. 개요 (Overview)
- S3에 이미지를 직접 업로드하기 위한 Presigned URL을 발급합니다.
- **Objective**: 서버 부하를 줄이고 보안을 유지하며 파일 업로드를 처리합니다.

#### 2. API 명세 (Technical Specs)
- **Endpoint**: `POST /images/presigned-url`

**Request Body**
```json
{
  "purpose": "PET_PHOTO",
  "fileName": "photo.jpg",
  "contentType": "image/jpeg"
}
```

**Request Fields**

| 필드 | 타입 | 필수 | 설명 |
|------|------|------|------|
| purpose | string | O | 업로드 목적 (PROFILE, PET_PHOTO, POST, LOST_PET, SIGHTING) |
| fileName | string | O | 원본 파일명 |
| contentType | string | O | MIME 타입 (예: image/jpeg, image/png) |

**Response Body**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "presignedUrl": "https://s3....",
    "imageUrl": "https://cdn.aini-inu.com/....",
    "expiresIn": 300
  }
}
```

**Response Fields**

| 필드 | 타입 | 설명 |
|------|------|------|
| presignedUrl | string | S3 업로드용 Presigned URL |
| imageUrl | string | 업로드 후 접근 가능한 CDN URL |
| expiresIn | integer | URL 만료 시간 (초) |

#### 3. 비즈니스 로직 & 제약조건 (Business Logic)
- **purpose별 S3 경로**: 목적에 따라 다른 S3 버킷 경로에 저장
  - `PROFILE`: `profiles/{memberId}/{uuid}.{ext}`
  - `PET_PHOTO`: `pets/{memberId}/{uuid}.{ext}`
  - `POST`: `posts/{memberId}/{uuid}.{ext}`
  - `LOST_PET`: `lost-pets/{memberId}/{uuid}.{ext}`
  - `SIGHTING`: `sightings/{memberId}/{uuid}.{ext}`
- **파일명 생성**: `{uuid}.{extension}` 형식으로 고유한 파일명 생성
- **지원 Content-Type**: `image/jpeg`, `image/png`, `image/gif`, `image/webp`
- **URL 유효 시간**: 300초 (5분)

#### 4. 구현 체크리스트
- [ ] UploadPurpose Enum 생성 (PROFILE, PET_PHOTO, POST, LOST_PET, SIGHTING)
- [ ] AWS S3 Presigned URL 생성 로직 구현
- [ ] Content-Type 화이트리스트 검증
- [ ] UUID 기반 파일명 생성 로직
- [ ] 응답 DTO 생성 (presignedUrl, imageUrl, expiresIn)

---

# 📌 의존성 매트릭스

```
Phase 1 (의존성 없음):
├── 건홍: Common 모듈, Security, Location VO
├── 동욱: Member Entity, ErrorCode
├── 혁진: Walk Enum (ChatType 공유)
├── 하늘: Post Entity 완성
└── 효주: Breed, Personality, WalkingStyle 마스터 데이터

Phase 2 (Member 필요):
├── 건홍: 대기 (Phase 3 준비)
├── 동욱: Auth API, Member API 완료
├── 혁진: Thread Entity, Thread API (authorId → Member)
├── 하늘: Post CRUD, Comment, PostLike
└── 효주: Pet CRUD (memberId → Member)

Phase 3 (Pet, Walk 필요):
├── 건홍: Chat (Thread 연동), LostPet (Pet 연동)
├── 동욱: Block 기능
├── 혁진: Thread apply → ChatRoom 생성
├── 하늘: 좋아요 토글, 댓글
└── 효주: 메인 반려견 변경, 10마리 제한 로직

Phase 4 (모든 Context):
├── 건홍: Notification (모든 이벤트 구독)
└── 전체: 통합 테스트, 크로스 컨텍스트 플로우
```

---

# 📌 Error Code 요약

| Context | Prefix | 범위 | 담당 |
|---------|--------|------|------|
| Common | C | C001-C999 | 건홍 |
| Member | M | M001-M006 | 동욱 |
| Pet | P | P001-P009 | 효주 |
| Thread | T | T001-T013 | 혁진 |
| Chat | CH | CH001-CH014 | 건홍 |
| Community | CO | CO001-CO005 | 하늘 |
| LostPet | L | L001-L004 | 건홍 |
| Notification | N | N001-N0xx | 건홍 |

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
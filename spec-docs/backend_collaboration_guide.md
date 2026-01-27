# 🐕 아이니이누 (Aini Inu) - 팀 협업 가이드

**버전**: v1.1  
**작성일**: 2026-01-26  
**최종 수정일**: 2026-01-27  
**대상**: 백엔드 개발팀 (팀장 + 신입 4명)

---

## 목차

0. [초보 온보딩 (필독)](#0-초보-온보딩-필독)
   - [문서 읽는 순서](#01-문서-읽는-순서)
   - [첫날 세팅 체크리스트](#02-첫날-세팅-체크리스트)
   - [작업 시작과 종료 루틴](#03-작업-시작과-종료-루틴)
   - [엔드포인트 1개 체크리스트](#04-엔드포인트-1개-체크리스트)
1. [팀 분업 전략](#1-팀-분업-전략)
2. [개발 순서](#2-개발-순서)
   - [초보 팀원 전용 가이드](#21-초보-팀원-전용-가이드)
3. [코드 작성 주의사항](#3-ddd도메인-주도-개발-코드-작성-주의사항)
   - [API 응답과 예외 처리](#36-api-응답과-예외-처리)
   - [DTO와 Validation](#37-dto와-validation)
   - [트랜잭션과 Dirty Checking](#38-트랜잭션과-dirty-checking)
   - [Repository와 N+1 방지](#39-repository와-n1-방지)
4. [패키지 구조](#4-패키지-구조)
5. [각 팀원별 패키지 범위](#5-각-팀원별-패키지-범위)
6. [Common 패키지](#6-common-패키지)
7. [충돌 방지 규칙](#7-충돌-방지-규칙)
   - [PR과 리뷰 운영](#74-pr과-리뷰-운영)
8. [코드 예시](#8-코드-예시)

---

## 0. 초보 온보딩 (필독)

### 0.1 문서 읽는 순서

> 목표: **스펙(무엇)** → **DB(어떻게 저장)** → **API(무엇을 주고받나)** → **컨벤션(어떻게 구현)** 순서로 머릿속에 “지도”를 만든 뒤 코딩 시작하기.

### 0.2 첫날 세팅 체크리스트

- JDK: `21`
- IntelliJ: Lombok 플러그인 + Annotation Processing 활성화
- 로컬 확인: `./gradlew test` 가 통과하는지 확인

---

## 1. 팀 분업 전략

### 팀 구성 현황

| 구분 | 인원 |
|-----|-----|
| 팀장 | 1명 |
| 숙련 신입 | 2명 |
| 초보 신입 | 2명 |

### 바운디드 컨텍스트 기반 분배

| 담당자 | 컨텍스트                           | API 범위           | 난이도 | 비고 |
|--------|--------------------------------|------------------|-------|------|
| **팀장** | Common + Chat + 아키텍처 + LostPet | 공통 모듈, WebSocket | 🔴 높음 | * |
| **숙련 A** | Member Context                 | `/auth/*`, `/members/*` | 🟡 중간 | 소셜 로그인 포함 |
| **숙련 B** | Walk Context (Thread)          | `/threads/*`     | 🟡 중간 | 필터, 지도 검색 |
| **초보 C** | **Community**                  | `/posts/*`, `/comments/*` | 🟢 입문 | 순수 CRUD |
| **초보 D** | **Pet**                        | `/pets/*`, `/breeds/*` | 🟢~🟡 입문~초급 | 다대다 학습 |

### 복잡한 모듈 처리 (팀장)
- **LostPet Context**: AI 서버 연동 포함 → 팀장
- **Notification Context**: 이벤트 기반 → 팀장이 뼈대 잡고 신입이 채움
- **Block (차단)**: 숙련 A가 Member Context 완료 후 담당

### 팀장 역할

| 역할 | 설명 |
|-----|------|
| 아키텍처 가이드 | 패키지 구조, 공통 모듈 정의 |
| 복잡한 모듈 | Chat, LostPet AI 연동 직접 구현 |
| 코드 스켈레톤 | 초보 팀원용 뼈대 코드 제공 |
| 문서화 | 공통 규칙 문서 작성 |

---

## 2. 개발 순서

```
Phase 1: 기반 구축
├── 팀장: 공통 모듈 (예외처리, 응답 포맷) + 초보 팀원 스켈레톤 제공
├── 숙련 A: Member (선행) ⭐ 다른 팀원들이 authorId, ownerId로 참조
├── 숙련 B: Walk 준비 (설계)
├── 초보 C: Community Post 엔티티 
└── 초보 D: Breed, Personality 조회 API 

Phase 2: 핵심 기능
├── 숙련 A: Member 완료 + Block 시작
├── 숙련 B: Walk/Thread (Member, Pet 의존) ⚠️ [ID 참조 방법](#31-서로-다른-컨텍스트의-엔티티는-id로만-참조하기)
├── 팀장: Chat (Thread 의존) ⚠️ [ID 참조 방법](#31-서로-다른-컨텍스트의-엔티티는-id로만-참조하기)
├── 초보 C: Community CRUD 완료
└── 초보 D: Pet CRUD + 다대다 연결

Phase 3: 부가 기능
├── 팀장: LostPet (AI 연동)
├── 숙련 A: Block (차단) 완료
├── 초보 C: 좋아요, 댓글 기능
└── 초보 D: 메인 반려견 변경, 10마리 제한 로직

Phase 4: 통합
└── 팀장: Notification (모든 컨텍스트 이벤트 수집)
```

---

## 2.1 초보 팀원 전용 가이드

### 난이도 비교: Community vs Pet

| 기준 | Community | Pet |
|-----|-----------|-----|
| **엔티티 수** | 3개 (Post, Comment, PostLike) | 5개 (Pet, Breed, Personality, PetPersonality, PetWalkingStyle) |
| **관계 복잡도** | 단순 (1:N) | 다대다 연결 테이블 있음 |
| **비즈니스 로직** | 거의 없음 (CRUD 위주) | 약간 있음 (메인 반려견 변경, 최대 10마리 제한) |
| **외부 연동** | 없음 | 공공데이터 API |
| **API 수** | 7개 | 8개 |
| **난이도** | 🟢 **입문** | 🟢~🟡 **입문~초급** |

### 초보 C: Community Context 학습 로드맵

**왜 적합한가?**
- 순수 CRUD: 복잡한 비즈니스 로직 없음
- 1:N 관계만: Post → Comment, Post → PostLike
- 독립적: 다른 컨텍스트 의존도 낮음 (Member ID만 참조) → [ID 참조 방법](#31-서로-다른-컨텍스트의-엔티티는-id로만-참조하기)
- SNS 패턴: 직관적으로 이해하기 쉬움

**학습 포인트**:
- `@Entity`, `@Id`, `@GeneratedValue`
- `@ManyToOne`, `@OneToMany`
- `JpaRepository` 기본 CRUD
- `@Transactional` 기초
- 페이지네이션 (`Pageable`)

**Phase별 과제**:
```
Phase 1: Post 엔티티 + PostRepository + Post CRUD
Phase 2: Post 목록 조회 (페이지네이션)
Phase 3: Comment CRUD
Phase 4: PostLike 토글 (좋아요 추가/취소)
```

### 초보 D: Pet Context 학습 로드맵

**왜 적합한가?**
- 다대다 관계 학습: 중요한 JPA 개념
- 비즈니스 로직 연습: "메인 반려견 변경", "최대 10마리 제한"
- 마스터 데이터: Breed, Personality는 조회만 (쉬움)
- Member 의존: Pet은 ownerId로 Member 참조 → [ID 참조 방법](#31-서로-다른-컨텍스트의-엔티티는-id로만-참조하기)

**학습 포인트**:
- `@ManyToOne` / `@OneToMany` 엔티티 관계
- `@Embedded` / `@Embeddable` (값 객체)
- 조건부 업데이트 (isMain 변경 시 기존 메인 해제)
- 비즈니스 규칙 엔티티에 구현

**Phase별 과제**:
```
Phase 1: Breed, Personality 엔티티 + 조회 API (쉬움)
Phase 2: Pet 엔티티 + 기본 CRUD
Phase 3: PetPersonality 연결 (다대다)
Phase 4: 메인 반려견 변경 로직, 10마리 제한
```

### 팀장의 초보 팀원 지원 전략

**1. 코드 스켈레톤 제공**

팀장이 먼저 만들어줄 것:
```java
// 예시: 엔티티 뼈대
@Entity
public class Post extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private Long authorId;  // Member ID 참조
    
    private String content;
    
    // TODO: 나머지 필드 추가해보세요
}
```

---

## 3. DDD(도메인 주도 개발) 코드 작성 주의사항

### 3.1 서로 다른 컨텍스트의 엔티티는 ID로만 참조하기

**핵심 원칙**: 다른 컨텍스트(다른 팀원이 담당하는 영역)의 엔티티를 사용할 때는 **ID 값만 저장**합니다.

```java
// ❌ 잘못된 예: 다른 엔티티 그룹을 직접 참조
public class Thread {
    private Member author;  // ❌ Member 객체 전체를 직접 참조
}

// ✅ 올바른 예: ID로만 참조
public class Thread {
    private Long authorId;  // ✅ Member의 ID만 보관
}
```

**왜 ID로만 참조해야 하나요?**

1. **엔티티 그룹 경계 유지**: 각 컨텍스트(Member, Pet, Walk 등)의 독립성을 보장
2. **결합도 감소**: Walk Context가 Member Context의 구현 변경에 영향받지 않음
3. **트랜잭션 경계 명확화**: 하나의 트랜잭션은 하나의 엔티티 그룹만 수정
4. **성능**: N+1 문제 방지 및 지연 로딩 이슈 회피

**실제 예시**:
```java
// Walk Context의 Thread 엔티티
@Entity
public class Thread {
    private Long authorId;      // Member Context의 Member ID만 참조
    private Long petId;         // Pet Context의 Pet ID만 참조
    // Member나 Pet 정보가 필요하면 서비스 레이어에서 조회
}

// 서비스 레이어에서 필요시 조회
@Service
public class ThreadService {
    private final ThreadRepository threadRepository;
    private final MemberRepository memberRepository;  // 다른 컨텍스트 Repository

    public ThreadDetailResponse getThreadDetail(Long threadId) {
        Thread thread = threadRepository.findById(threadId)...;
        Member author = memberRepository.findById(thread.getAuthorId())...;
        return ThreadDetailResponse.of(thread, author);
    }
}
```

### 3.2 도메인 로직은 엔티티 안에

```java
// ❌ 잘못된 예: 서비스에 비즈니스 로직
@Service
public class MannerScoreService {
    public void addScore(Member member, int score) {
        // ❌ 점수 검증/평균 계산 로직이 서비스에 흩어짐
        if (score < 1 || score > 10) {
            throw new IllegalArgumentException("점수는 1~10점입니다");
        }
        member.setMannerTemperature(BigDecimal.valueOf(score)); // ❌ 임의로 덮어씀
    }
}

// ✅ 올바른 예: 엔티티에 비즈니스 로직
@Entity
public class Member {
    public void addMannerScore(int score) {
        if (score < 1 || score > 10) {
            throw new BusinessException(MemberErrorCode.INVALID_MANNER_SCORE);
        }
        this.mannerScoreSum += score;
        this.mannerScoreCount += 1;
        this.mannerTemperature = MannerTemperature.fromAverage(this.mannerScoreSum, this.mannerScoreCount);
    }
}
```

### 3.3 값 객체 활용

```java
// ❌ 잘못된 예: 원시 타입 나열
public class Thread {
    private String placeName;
    private BigDecimal latitude;
    private BigDecimal longitude;
}

// ✅ 올바른 예: 값 객체로 캡슐화
@Embeddable
public class Location {
    private String placeName;
    private BigDecimal latitude;
    private BigDecimal longitude;
    
    public double distanceTo(Location other) {
        // 거리 계산 로직
    }
}

public class Thread {
    @Embedded
    private Location location;
}
```

### 3.4 컨텍스트 간 통신은 이벤트로

```java
// ❌ 잘못된 예: 직접 호출
@Service
public class ThreadService {
    private final NotificationService notificationService;  // ❌ 다른 컨텍스트 직접 의존
    
    public void createThread(ThreadCreateRequest req) {
        Thread thread = threadRepository.save(...);
        notificationService.sendNotification(...);  // ❌
    }
}

// ✅ 올바른 예: 이벤트 발행
@Service
public class ThreadService {
    private final ApplicationEventPublisher eventPublisher;
    
    public void createThread(ThreadCreateRequest req) {
        Thread thread = threadRepository.save(...);
        eventPublisher.publishEvent(new ThreadCreatedEvent(thread.getId()));  // ✅
    }
}

// Notification 컨텍스트에서 이벤트 구독
@EventListener
public void handleThreadCreated(ThreadCreatedEvent event) {
    // 알림 발송 로직
}
```

### 3.5 리뷰 체크리스트

```markdown
### DDD 원칙 체크리스트

#### 도메인 모델
- [ ] 엔티티에 비즈니스 로직이 있는가? (빈약한 도메인 모델 ❌)
- [ ] 다른 컨텍스트의 엔티티를 ID로만 참조하는가?
- [ ] 값 객체를 적절히 사용했는가?

#### 레이어 분리
- [ ] Controller → Service → Repository 흐름인가?
- [ ] DTO ↔ Entity 변환이 명확한가?

#### 컨텍스트 분리
- [ ] 다른 컨텍스트 서비스를 직접 호출하지 않았는가?
- [ ] 컨텍스트 간 통신이 필요하면 이벤트를 사용했는가?
```

### 3.6 API 응답과 예외 처리

관련 문서: `spec-docs/backend_spec_03_api.md`, `.gemini/skills/spring-backend-generator/references/response-patterns.md`

- **성공 응답은 항상 200**: `POST/PUT/DELETE`도 `ResponseEntity.ok(ApiResponse.success(...))`
- **실패는 BusinessException으로 통일**: `throw new BusinessException({Context}ErrorCode.SOMETHING)`
- **문자열 예외 금지**: `new RuntimeException("...")`, `IllegalArgumentException("...")` 같은 “문자열 던지기”는 협업 시 디버깅이 어려움
- **에러코드 prefix 준수**: API 스펙의 `{PREFIX}{NUMBER}` 규칙을 그대로 사용 (예: `P001`, `CO002`)

```java
return ResponseEntity.ok(ApiResponse.success(response));
```

### 3.7 DTO와 Validation

관련 문서: `.gemini/skills/spring-backend-generator/references/conventions.md`, `spec-docs/backend_spec_03_api.md`

- DTO는 **class로 통일** (초보 팀원 기준 record 지양)
- Request DTO: `@Getter @Setter @NoArgsConstructor` + `jakarta.validation`으로 입력값 검증
- Response DTO: 불변(Setter 금지) + `from(entity)` 정적 팩토리로 변환 로직 한 곳에 모으기

### 3.8 트랜잭션과 Dirty Checking

관련 문서: `.gemini/skills/spring-backend-generator/references/conventions.md`, `.gemini/skills/spring-backend-generator/references/entity-patterns.md`

- Service 클래스 기본은 `@Transactional(readOnly = true)`로 두고, **쓰기 메서드만 `@Transactional`로 오버라이드**
- 수정은 `entity.updateSomething(...)` 같은 **도메인 메서드(엔티티에 정의된 메서드) + Dirty Checking**으로 처리 (`save()`로 덮어쓰기 지양)
- 한 트랜잭션에서 “여러 컨텍스트의 애그리게잇을 동시에 수정”하지 않기 (경계가 무너짐)

### 3.9 Repository와 N+1 방지

관련 문서: `.gemini/skills/spring-backend-generator/references/entity-patterns.md`

- `@ManyToOne`, `@OneToOne`는 **무조건 LAZY**
- 단순 조건 조회는 메서드 네이밍 우선 (`findBy...`, `existsBy...`, `countBy...`)
- 관계 데이터가 필요한 조회는 fetch join 등으로 **N+1을 의식적으로 차단**

---

## 4. 패키지 구조

### 4.1 전체 구조 개요

```
src/main/java/com/ainiinu/
│
├── common/                       ← 팀장 (공용 모듈)
│
├── member/                       ← 숙련 A (소셜 로그인 포함)
├── walk/                         ← 숙련 B (필터, 지도)
├── community/                    ← 초보 C (순수 CRUD)
├── pet/                          ← 초보 D (다대다 학습)
├── chat/                         ← 팀장 (WebSocket)
├── lostpet/                      ← 팀장 + 숙련 페어 (AI)
└── notification/                 ← 팀장 + 숙련 페어 (이벤트)
```

### 4.2 각 컨텍스트 내부 구조 (단순화된 5폴더)

```
{context}/
├── entity/                       ← 💎 엔티티 (@Entity, JPA 직접 사용)
│   ├── enums/                    # 열거형 (필요시)
│   └── vo/                       # 값 객체 (필요시)
├── repository/                   ← 📦 JpaRepository 인터페이스
├── service/                      ← 🎯 비즈니스 로직
├── controller/                   ← 🌐 API 엔드포인트
└── dto/                          ← 📨 요청/응답 객체
    ├── request/
    └── response/
```

> 💡 **단순화 원칙**: 필요하면 각자 구조를 확장해도 OK. 처음부터 복잡하게 만들지 말자!

### 4.3 레이어 역할

```
┌─────────────────┬───────────────────────────────────────────────┐
│  controller/    │  HTTP 요청/응답 처리, 입력 검증                │
│                 │  → @RestController, @Valid                    │
├─────────────────┼───────────────────────────────────────────────┤
│  service/       │  비즈니스 로직, 트랜잭션 관리                   │
│                 │  → @Service, @Transactional                   │
├─────────────────┼───────────────────────────────────────────────┤
│  repository/    │  DB 접근 (Spring Data JPA)                    │
│                 │  → extends JpaRepository                      │
├─────────────────┼───────────────────────────────────────────────┤
│  entity/        │  테이블 매핑 + 비즈니스 규칙                    │
│                 │  → @Entity, @Embeddable                       │
├─────────────────┼───────────────────────────────────────────────┤
│  dto/           │  API 요청/응답 객체                            │
│                 │  → class (권장)                               │
└─────────────────┴───────────────────────────────────────────────┘
```

### 4.4 의존 방향 (단순!)

```
controller → service → repository → entity
                ↓
               dto
```

- **흐름이 직관적**: 요청 → 처리 → 저장 → 응답
- **복잡한 인터페이스 분리 없음**: JpaRepository 직접 사용

---

## 5. 각 팀원별 패키지 범위

### 숙련 A: Member Context

```
member/
├── entity/               # Member, Block, MannerScore + enums, vo
├── repository/
├── service/
├── controller/
└── dto/
```
**핵심 기능**: 소셜 로그인, 프로필, 차단, 매너 점수

---

### 숙련 B: Walk Context

```
walk/
├── entity/               # Thread, ThreadPet + enums, vo(Location, TimeRange)
├── repository/
├── service/
├── controller/
├── dto/
└── event/                # 다른 컨텍스트 알림용 이벤트 (필요시)
```
**핵심 기능**: 산책 모집글 CRUD, 필터 검색, 지도 기반 조회

---

### 초보 C: Community Context

```
community/
├── entity/               # Post, Comment, PostLike
├── repository/
├── service/
├── controller/
└── dto/
```
**핵심 기능**: 게시글/댓글 CRUD, 좋아요 토글

---

### 초보 D: Pet Context

```
pet/
├── entity/               # Pet, Breed, Personality + 연결 테이블, enums
├── repository/
├── service/
├── controller/
└── dto/
```
**핵심 기능**: 반려견 CRUD, 품종/성격 조회, 다대다 연결

---

### 팀장: Chat Context

```
chat/
├── entity/               # ChatRoom, ChatParticipant, Message + enums
├── repository/
├── service/
├── controller/
├── dto/
└── websocket/            # 실시간 채팅 (필요시)
```
**핵심 기능**: 채팅방 생성, 메시지 송수신, WebSocket

---

### 팀장 : LostPet Context

```
lostpet/
├── entity/               # LostPetReport, Sighting, LostPetMatch + enums, vo
├── repository/
├── service/
├── controller/
├── dto/
└── client/               # 외부 AI 서비스 연동 (필요시)
```
**핵심 기능**: 실종 신고, 목격 제보, AI 매칭

---

### 팀장 + 숙련 페어: Notification Context

```
notification/
├── entity/               # Notification, NotificationSetting + enums
├── repository/
├── service/
├── controller/
├── dto/
├── listener/             # 다른 컨텍스트 이벤트 구독 (필요시)
└── client/               # FCM 등 푸시 알림 (필요시)
```
**핵심 기능**: 알림 발송, 알림 설정, 이벤트 구독

---

## 6. Common 패키지

팀장이 선작업으로 제공:

```
common/
├── config/
│   ├── SecurityConfig.java
│   ├── WebConfig.java
│   ├── JpaConfig.java
│   └── SwaggerConfig.java
│
├── security/
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   └── CustomUserDetails.java
│
├── response/
│   ├── ApiResponse.java          # 공통 응답 포맷
│   └── PageResponse.java         # 페이지네이션 응답
│
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── ErrorCode.java            # 에러 코드 인터페이스
│   ├── CommonErrorCode.java      # 공통 에러 코드
│   └── BusinessException.java    # 비즈니스 예외 기본 클래스
│
├── entity/
│   └── BaseTimeEntity.java       # createdAt, updatedAt
│
└── util/
    ├── DateTimeUtil.java
    └── LocationUtil.java         # 거리 계산 등
```

---

## 7. 충돌 방지 규칙

### 7.1 Git 브랜치 전략

```
main
└── develop
    ├── feature/member-auth        ← 숙련 A
    ├── feature/member-profile     ← 숙련 A
    ├── feature/thread-create      ← 숙련 B
    ├── feature/thread-filter      ← 숙련 B
    ├── feature/post-crud          ← 초보 C
    ├── feature/comment-crud       ← 초보 C
    ├── feature/pet-crud           ← 초보 D
    ├── feature/pet-personality    ← 초보 D
    ├── feature/chat-websocket     ← 팀장
    └── feature/lostpet-ai         ← 팀장 + 숙련 페어
```

### 7.2 충돌 가능 지점 & 해결

| 충돌 지점 | 해결 방법 |
|----------|----------|
| `common/` 수정 | 팀장만 수정, PR 필수 |
| 다른 컨텍스트 엔티티 참조 | **ID만 참조, 직접 import 금지** → [자세한 방법](#31-서로-다른-컨텍스트의-엔티티는-id로만-참조하기) |
| 공용 DTO 필요 시 | `common/dto/shared/` 에 정의 |

### 7.3 협업 도구

| 용도 | 도구                                          |
|-----|---------------------------------------------|
| 이슈 관리 | GitHub Issues + 컨텍스트별 라벨 + Notion + Discode |

---

## 8. 코드 예시

### Member Context 예시

```java
// 1. entity/Member.java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private MemberType memberType;

    @Embedded
    private MannerTemperature mannerTemperature;

    private int mannerScoreSum;
    private int mannerScoreCount;

    public void addMannerScore(int score) {
        this.mannerScoreSum += score;
        this.mannerScoreCount += 1;
        this.mannerTemperature = MannerTemperature.fromAverage(this.mannerScoreSum, this.mannerScoreCount);
    }
}

// 2. entity/vo/MannerTemperature.java
@Embeddable
@Getter
public class MannerTemperature {
    private static final BigDecimal MIN = BigDecimal.ONE;
    private static final BigDecimal MAX = BigDecimal.TEN;
    private static final BigDecimal DEFAULT = new BigDecimal("5.0");

    private BigDecimal value;

    public static MannerTemperature fromAverage(int sum, int count) {
        if (count <= 0) {
            return new MannerTemperature(DEFAULT);
        }
        BigDecimal avg = BigDecimal.valueOf(sum)
            .divide(BigDecimal.valueOf(count), 1, RoundingMode.HALF_UP);
        if (avg.compareTo(MIN) < 0) avg = MIN;
        if (avg.compareTo(MAX) > 0) avg = MAX;
        return new MannerTemperature(avg);
    }
}

// 3. repository/MemberRepository.java (JpaRepository 직접 사용!)
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findBySocialProviderAndSocialId(SocialProvider provider, String socialId);
    boolean existsByNickname(String nickname);
}

// 4. service/MemberService.java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private final MemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public MemberResponse createProfile(Long memberId, MemberCreateRequest request) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new BusinessException(MemberErrorCode.MEMBER_NOT_FOUND));

        member.updateProfile(request.getNickname(), request.getMemberType());

        eventPublisher.publishEvent(new MemberRegisteredEvent(member.getId()));

        return MemberResponse.from(member);
    }
}

// 5. controller/MemberController.java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/members")
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/profile")
    public ResponseEntity<ApiResponse<MemberResponse>> createProfile(
            @AuthenticationPrincipal CustomUserDetails user,
            @Valid @RequestBody MemberCreateRequest request) {
        return ResponseEntity.ok(
            ApiResponse.success(memberService.createProfile(user.getMemberId(), request))
        );
    }
}
```

---

## 부록: ChatRoom 설계 결정사항

### 실종-제보 연결 채팅방 지원

`chat_room` 테이블에 `thread_id`와 `lost_pet_match_id` 두 개의 컬럼을 둔 이유:

```sql
chat_room {
    room_purpose      VARCHAR(20)  -- 'WALK' 또는 'LOST_PET_MATCH'
    thread_id         BIGINT       -- nullable (WALK 채팅방만)
    lost_pet_match_id BIGINT       -- nullable (LOST_PET_MATCH 채팅방만)
}
```

### 장점
- 명시성: 어떤 컨텍스트와 연결되는지 컬럼명만 봐도 명확
- 타입 안정성: 각 컬럼에 올바른 FK 인덱스를 설정 가능
- 쿼리 직관성: `WHERE thread_id = ?` 처럼 직관적인 쿼리
- ORM 친화적: JPA에서 `@ManyToOne` 관계 매핑이 명확

### 단점
- 스키마 확장 비용: 새로운 채팅방 용도 추가 시 컬럼 추가 필요
- NULL 컬럼 증가: 용도가 늘어날수록 항상 NULL인 컬럼이 많아짐

### 결론
채팅방 용도가 2~3개로 제한적이므로 개별 컬럼 방식이 적절함.
5개 이상의 채팅방 용도가 예상된다면 연결 테이블 방식으로 리팩토링 고려.

---

**문서 끝**

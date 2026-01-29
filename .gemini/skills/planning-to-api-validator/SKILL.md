---
name: planning-to-api-validator
description: "Validate that API specification fully covers all requirements from the planning document (spec_v4.2.md). Use when you need to: (1) Check if all planning requirements have corresponding APIs, (2) Verify input constraints are properly reflected, (3) Ensure business rules are implementable via APIs, (4) Find missing API endpoints for planned features, (5) Generate coverage report between planning and API spec."
---

# Planning to API Validator

Validate that the API specification (backend_spec_03_api.md) fully covers all requirements from the planning document (spec_v4.2.md) for the 아이니이누 (Aini Inu) project.

## When to Use This Skill

Use this skill when:

- **API 명세서 완성도 검증**: "기획서의 모든 요구사항이 API로 구현 가능한지 확인해줘"
- **누락된 API 찾기**: "기획서에는 있는데 API 명세서에 없는 기능이 있는지 확인해줘"
- **입력값 제한 검증**: "기획서의 입력값 제한이 API에 제대로 반영되었는지 확인해줘"
- **비즈니스 로직 검증**: "기획서의 비즈니스 규칙을 API로 처리할 수 있는지 확인해줘"
- **PR 전 검증**: "API 명세서 변경 전에 기획서 요구사항을 모두 충족하는지 확인해줘"

## Document Relationship

```
┌─────────────────────────────────────────────────────────────┐
│            Planning Document (spec_v4.2.md)                 │
│                                                             │
│  ├── 용어 정의 (Glossary)                                    │
│  ├── 입력값 제한 (Input Constraints)                         │
│  ├── 회원가입/프로필 (Membership)                            │
│  ├── 산책 모집 스레드 (Thread Features)                      │
│  ├── 매칭/채팅 시스템 (Chat System)                          │
│  ├── 안전/신뢰 시스템 (Safety Features)                      │
│  ├── 커뮤니티 (Community)                                    │
│  ├── 알림 시스템 (Notifications)                             │
│  └── 실종 반려견 찾기 (Lost Pet)                             │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ 요구사항 충족 여부 검증
                              ▼
┌─────────────────────────────────────────────────────────────┐
│           API Specification (backend_spec_03_api.md)        │
│                                                             │
│  ├── 3.2 인증 (Authentication)                              │
│  ├── 3.3 회원 (Members)                                     │
│  ├── 3.4 반려견 (Pets)                                      │
│  ├── 3.5 스레드 (Threads)                                   │
│  ├── 3.6 채팅 (Chat)                                        │
│  ├── 3.7 실종 반려견 (Lost Pets)                            │
│  ├── 3.8 커뮤니티 (Community)                               │
│  ├── 3.9 차단 (Block)                                       │
│  ├── 3.10 알림 (Notifications)                              │
│  └── 3.11 이미지 업로드 (Uploads)                           │
└─────────────────────────────────────────────────────────────┘
```

## Quick Start

### Step 1: Read Planning Document

**ALWAYS** read the planning document first:

```
view spec-docs/spec_v4.2.md
```

### Step 2: Read API Specification

```
view spec-docs/backend_spec_03_api.md
```

### Step 3: Read Validation Rules

```
view references/validation-rules.md
```

### Step 4: Perform Validation

Compare planning requirements against API endpoints and generate coverage report.

## Validation Categories

### 1. Feature Coverage (기능 커버리지)

기획서의 각 기능이 API로 구현 가능한지 확인:

| Planning Feature | Required APIs | Coverage |
|------------------|---------------|----------|
| 소셜 로그인 (네이버/카카오/구글) | `POST /auth/login/{provider}` | ✅ |
| 반려견 등록 (최대 10마리) | `POST /pets`, `GET /pets` | ✅ |
| 메인 반려견 변경 | `PATCH /pets/{petId}/main` | ✅ |
| 스레드 작성 (동시 1개 제한) | `POST /threads` + 비즈니스 로직 | ✅ |

### 2. Input Constraints (입력값 제한)

기획서의 입력값 제한이 API Request에 반영되었는지 확인:

| Planning Constraint | API Validation | Status |
|---------------------|----------------|--------|
| 닉네임 최대 10자 | `nickname` max length 10 | ✅ |
| 반려견 이름 최대 10자 | `name` max length 10 | ✅ |
| 스레드 제목 최대 30자 | `title` max length 30 | ✅ |
| 스레드 소개글 최대 500자 | `description` max length 500 | ✅ |
| 메시지 최대 500자 | `content` max length 500 | ✅ |
| 예약 가능 기간 최대 1주일 | startTime validation | ✅ |
| 그룹 채팅 3~10명 | `maxParticipants` 3-10 | ✅ |

### 3. Business Rules (비즈니스 규칙)

기획서의 비즈니스 규칙이 API 또는 에러 코드로 처리 가능한지 확인:

| Planning Rule | API/Error Handling | Status |
|---------------|-------------------|--------|
| 사용자당 스레드 1개 제한 | T002 에러 코드 | ✅ |
| 비애견인은 스레드 작성 불가 | T012 에러 코드 | ✅ |
| 필수 필터 최대 3개 | T011 에러 코드 | ✅ |
| 차단한 유저 스레드 미노출 | 스레드 목록 필터링 로직 | ✅ |
| 1:1 채팅 산책 확정 (양방향) | walk-confirm API | ✅ |
| 매너 온도 평균 계산 | 후기 점수 → mannerTemperature | ✅ |

### 4. User Flow Coverage (사용자 플로우)

기획서의 사용자 플로우가 API 시퀀스로 구현 가능한지 확인:

```
[회원가입 플로우]
1. 소셜 로그인 → POST /auth/login/{provider}
2. 프로필 설정 → POST /members/profile
3. 반려견 등록 → POST /pets
   → memberType 자동 전환 (NON_PET_OWNER → PET_OWNER)
```

```
[산책 신청 플로우]
1. 스레드 탐색 → GET /threads (필터링)
2. 스레드 상세 → GET /threads/{threadId}
3. 산책 신청 → POST /threads/{threadId}/apply
   → 채팅방 자동 생성/입장
4. (개별 대화) 산책 확정 → POST /chat-rooms/{id}/walk-confirm
```

```
[실종 반려견 플로우]
1. 실종 등록 → POST /lost-pets (YOLO+CLIP 임베딩)
2. 유사 제보 조회 → GET /lost-pets/{id}/similar-sightings
3. 매칭 → POST /lost-pets/{id}/match
   → 1:1 채팅방 자동 생성
```

### 5. Response Data Coverage (응답 데이터 커버리지)

기획서에서 필요한 데이터가 API Response에 포함되어 있는지 확인:

| Planning Data Need | API Response Field | Status |
|--------------------|-------------------|--------|
| 매너 온도 표시 | `mannerTemperature` | ✅ |
| 반려견 인증마크 | `isCertified` | ✅ |
| 스레드 참가자 수 | `currentParticipants` | ✅ |
| 필터 충족 여부 | `myFilterStatus` | ✅ |
| 유사도 점수 | `similarityScore.total/image/location/time` | ✅ |

## Output Format

### Coverage Validation Report

```markdown
# Planning → API Coverage Report

## Executive Summary
- Planning Features: 45
- API Covered: 43
- Missing/Partial: 2
- Coverage Rate: 95.6%

## Fully Covered Features (43)

### 회원/인증 (6/6) ✅
| Feature | API Endpoints | Status |
|---------|---------------|--------|
| 소셜 로그인 | POST /auth/login/{provider} | ✅ |
| 토큰 갱신 | POST /auth/refresh | ✅ |
| 로그아웃 | POST /auth/logout | ✅ |
| 프로필 설정 | POST /members/profile | ✅ |
| 프로필 조회 | GET /members/me, /members/{id} | ✅ |
| 프로필 수정 | PATCH /members/me | ✅ |

### 반려견 (8/8) ✅
| Feature | API Endpoints | Status |
|---------|---------------|--------|
| 반려견 등록 (10마리 제한) | POST /pets | ✅ |
| 반려견 목록 | GET /pets | ✅ |
| ... | ... | ... |

## Missing/Partial Coverage (2)

### 1. 정기 모임 기능
- **Planning**: "Post MVP 기능으로 검토" (보류)
- **API**: 없음
- **Status**: ⏸️ 보류 항목 - API 구현 불필요
- **Action**: None (기획 보류 상태)

### 2. 노쇼 처리 기능
- **Planning**: "페널티, 판단 기준 등" (보류)
- **API**: 없음
- **Status**: ⏸️ 보류 항목 - API 구현 불필요
- **Action**: None (기획 보류 상태)

## Input Constraints Validation

| Constraint | Planning | API Spec | Status |
|------------|----------|----------|--------|
| 닉네임 | 최대 10자, 중복불가 | max 10, M003 에러 | ✅ |
| 반려견 이름 | 최대 10자 | max 10, P008 에러 | ✅ |
| 스레드 제목 | 최대 30자 | max 30 | ✅ |
| 스레드 소개글 | 최대 500자 | max 500 | ✅ |
| 메시지 | 최대 500자 | max 500, CH001 에러 | ✅ |
| 그룹 인원 | 3~10명 | 3-10, T010 에러 | ✅ |
| 예약 기간 | 오늘~1주일 | T008 에러 | ✅ |
| 필수 필터 | 최대 3개 | T011 에러 | ✅ |
| 반려견 수 | 최대 10마리 | P002 에러 | ✅ |
| 후기 점수 | 1~10점 | CH010 에러 | ✅ |

## Business Rules Validation

| Rule | API/Logic | Error Code | Status |
|------|-----------|------------|--------|
| 사용자당 활성 스레드 1개 | Thread 생성 시 검증 | T002 | ✅ |
| 비애견인 스레드 작성 불가 | memberType 검증 | T012 | ✅ |
| 차단한 유저 스레드 미노출 | 목록 조회 필터링 | - | ✅ |
| 나를 차단한 유저 스레드 신청 불가 | 신청 시 검증 | M006 | ✅ |
| 필수 필터 미충족 시 미노출 | 목록/상세 조회 필터링 | T001 | ✅ |
| 비애견인은 반려견 필수필터 스레드 미노출 | 조회 필터링 | - | ✅ |
| 개별대화 작성자 1명만 확정 | walk-confirm 로직 | CH007 | ✅ |
| 확정 취소는 상대 확정 전만 | walk-confirm 로직 | CH009 | ✅ |
| 채팅방 종료 후 +2시간 아카이브 | 자동 종료 로직 | - | ✅ |
| 신규 유저 매너온도 5.0 | 기본값 설정 | - | ✅ |

## User Flow Validation

### ✅ 회원가입 플로우
```
Planning: 소셜 로그인 → 프로필 설정 → (선택) 반려견 등록
API Flow:
1. POST /auth/login/{provider} → isNewMember=true
2. POST /members/profile
3. POST /pets → memberType 자동 전환
```

### ✅ 산책 모집 플로우
```
Planning: 스레드 작성 → 참가자 대기 → 채팅
API Flow:
1. GET /threads/check-duplicate (중복 확인)
2. POST /threads
3. GET /chat-rooms (채팅방 자동 생성됨)
```

### ✅ 산책 신청 플로우
```
Planning: 스레드 탐색 → 상세 확인 → 신청 → 채팅
API Flow:
1. GET /threads (필터링)
2. GET /threads/{id} (myFilterStatus 확인)
3. POST /threads/{id}/apply → chatRoomId 반환
4. GET /chat-rooms/{id}/messages
```

### ✅ 실종 반려견 플로우
```
Planning: 실종 등록 → AI 유사 검색 → 매칭 → 채팅
API Flow:
1. POST /lost-pets (YOLO crop + CLIP embedding)
2. GET /lost-pets/{id}/similar-sightings
3. POST /lost-pets/{id}/match → chatRoomId 반환
```

## Recommendations

1. ✅ **모든 MVP 기능 커버됨** - 기획서의 핵심 기능 100% API 구현 가능
2. ⏸️ **보류 항목 확인** - 정기 모임, 노쇼 처리 등은 기획 보류 상태
3. ⚠️ **에러 메시지 검토** - 사용자 친화적 메시지 확인 필요
```

## Typical Workflow

### Full Coverage Validation

```
User: "기획서의 모든 요구사항이 API로 구현 가능한지 확인해줘"

Steps:
1. Read spec-docs/spec_v4.2.md (전체)
2. Read spec-docs/backend_spec_03_api.md (전체)
3. Read references/validation-rules.md
4. Map each planning feature to API endpoints
5. Check input constraints
6. Verify business rules
7. Trace user flows
8. Generate coverage report
```

### Specific Feature Validation

```
User: "기획서의 '산책 모집 스레드' 기능이 API에 제대로 반영되었는지 확인해줘"

Steps:
1. Read spec_v4.2.md Section 5.A (산책 모집 스레드 생성)
2. Read backend_spec_03_api.md Section 3.5 (스레드)
3. Compare feature requirements vs API capabilities
4. Check specific constraints (30자 제목, 500자 소개글, etc.)
5. Verify error handling for business rules
6. Generate focused validation report
```

### Input Constraints Validation

```
User: "기획서의 입력값 제한이 API에 제대로 반영되었는지 확인해줘"

Steps:
1. Read spec_v4.2.md Section 3 (입력값 제한 및 규격)
2. Read backend_spec_03_api.md (각 섹션 Request Fields)
3. Compare each constraint
4. Check corresponding error codes
5. Generate constraints validation report
```

## Important Notes

- **Planning first**: 항상 기획서를 먼저 읽고 API 명세서와 비교
- **보류 항목 확인**: 기획서의 "보류" 항목은 API 미구현이 정상
- **비즈니스 로직 vs API**: 일부 로직은 API가 아닌 서버 내부에서 처리
- **에러 코드 중요**: 비즈니스 규칙 위반은 에러 코드로 처리되어야 함
- **사용자 플로우**: 단일 API가 아닌 API 시퀀스로 검증

## Planning Document Key Sections

| Section | 검증 포인트 |
|---------|-------------|
| 3. 입력값 제한 | API Request 필드 제약 조건 |
| 4. 회원가입/프로필 | Auth/Member API 커버리지 |
| 5.A 산책 모집 스레드 | Thread API + 필터 시스템 |
| 5.B 지도 기반 탐색 | Thread 목록/지도 API |
| 6. 매칭/채팅 시스템 | Thread apply + Chat API |
| 7. 안전/신뢰 시스템 | Block + Review API |
| 8. 커뮤니티 | Post/Comment API |
| 9. 알림 | Notification API |
| 10. 실종 반려견 | LostPet/Sighting API |

## References

- [Validation Rules](references/validation-rules.md) - Detailed validation rules
- [Planning Document](../../../spec-docs/spec_v4.2.md) - Source of requirements
- [API Specification](../../../spec-docs/backend_spec_03_api.md) - Implementation spec

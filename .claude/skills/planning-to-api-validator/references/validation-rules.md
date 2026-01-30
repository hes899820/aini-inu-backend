# Planning to API Validation Rules

기획서(spec_v4.2.md)의 요구사항이 API 명세서(backend_spec_03_api.md)에 올바르게 반영되었는지 검증하기 위한 상세 규칙입니다.

## 1. 입력값 제한 검증 규칙

### 1.1 사용자 정보

| 기획서 제한 | API 검증 포인트 | 에러 코드 |
|-------------|-----------------|-----------|
| 닉네임 최대 10자 | `POST /members/profile` nickname max length 10 | M002 |
| 닉네임 특수문자 허용 | nickname validation 패턴 | M002 |
| 닉네임 중복 불가 | nickname unique 검증 | M003 |
| 프로필 사진 제한 없음 | profileImageUrl URL만 검증 | - |

### 1.2 반려견 정보

| 기획서 제한 | API 검증 포인트 | 에러 코드 |
|-------------|-----------------|-----------|
| 반려견 이름 최대 10자 | `POST /pets` name max length 10 | P008 |
| 나이 직접 입력 (숫자) | age integer 타입 | C001 |
| 견종 목록 선택 | breedId 존재 여부 | P004 |
| 등록 가능 최대 10마리 | pets count 검증 | P002 |
| 메인 반려견 변경 가능 | `PATCH /pets/{id}/main` 제공 | - |

### 1.3 스레드

| 기획서 제한 | API 검증 포인트 | 에러 코드 |
|-------------|-----------------|-----------|
| 제목 최대 30자 | `POST /threads` title max length 30 | C001 |
| 소개글 최대 500자 | description max length 500 | C001 |
| 예약 기간 오늘~1주일 | startTime 검증 (과거 불가, 7일 초과 불가) | T008 |
| 과거 시간 선택 불가 | startTime >= now 검증 | T008 |
| 사용자당 1개 제한 | 활성 스레드 존재 여부 검증 | T002 |
| 모든 항목 수정 가능 | `PATCH /threads/{id}` 모든 필드 지원 | - |
| 비애견인 참여 허용 필수 선택 | allowNonPetOwner required field | C001 |

### 1.4 채팅

| 기획서 제한 | API 검증 포인트 | 에러 코드 |
|-------------|-----------------|-----------|
| 텍스트만 가능 | messageType USER only | - |
| 메시지 최대 500자 | content max length 500 | CH001 |
| 메시지 삭제 불가 | DELETE 엔드포인트 없음 | - |
| 읽음 표시 없음 | Response에 readAt 없음 | - |

### 1.5 알림

| 기획서 제한 | API 검증 포인트 | 에러 코드 |
|-------------|-----------------|-----------|
| 채팅 메시지 알림 | type: CHAT_MESSAGE | - |
| 산책 신청 알림 | type: WALK_APPLICATION | - |
| 실종 유사 제보 알림 | type: LOST_PET_SIMILAR | - |
| 종류별 ON/OFF | `PATCH /notification-settings` | - |

---

## 2. 기능별 API 매핑 규칙

### 2.1 회원가입 및 프로필 시스템 (Section 4)

#### 회원가입 프로세스

| 기획서 요구사항 | 필수 API | 검증 포인트 |
|-----------------|----------|-------------|
| 소셜 로그인 (네이버/카카오/구글) | `POST /auth/login/{provider}` | provider: naver, kakao, google |
| 기본 닉네임 설정 | `POST /members/profile` | nickname 필드 |
| 기본 프로필 설정 (선택) | `POST /members/profile` | 선택 필드들 nullable |
| 초기 상태 비애견인 | Response memberType: NON_PET_OWNER | isNewMember=true 시 |
| 반려견 등록 시 애견인 전환 | `POST /pets` 후 memberType 변경 | 자동 전환 로직 |

#### 프로필 구조

| 기획서 요구사항 | API Response | 검증 포인트 |
|-----------------|--------------|-------------|
| 견주 성향 카테고리 4종 | `GET /member-personality-types` | 동네친구, 반려견정보공유, 랜선집사, 강아지만좋아함 |
| 매너 온도 (평균값) | mannerTemperature | 1~10 범위, 기본값 5.0 |
| 다견 가구 지원 (최대 10마리) | `GET /pets` | 배열 반환 |
| 메인 반려견 1마리 선택 | isMain 필드 | 정확히 1개 true |

#### 비애견인 프로필

| 기획서 요구사항 | API 검증 포인트 |
|-----------------|-----------------|
| 강아지 없이 가입 가능 | memberType: NON_PET_OWNER 상태 허용 |
| 매너 온도 동일 적용 | mannerTemperature 필드 존재 |
| 산책 참여 가능 (허용 시) | allowNonPetOwner=true 스레드 신청 가능 |

---

### 2.2 산책 모집 스레드 (Section 5.A)

#### 기본 정보

| 기획서 요구사항 | API Request Field | 검증 포인트 |
|-----------------|-------------------|-------------|
| 제목 (필수) | title (required) | max 30자 |
| 산책 날짜 (필수) | walkDate (required) | ISO 8601 date |
| 장소 (필수) | location (required) | placeName, latitude, longitude |
| 시작/종료 시간 (필수) | startTime, endTime (required) | KST, endTime > startTime |
| 모집 방식 선택 | chatType (required) | INDIVIDUAL / GROUP |
| 그룹 최대 참가자 | maxParticipants | 그룹만, 3~10 |
| 비애견인 참여 허용 | allowNonPetOwner (required) | boolean |

#### 참가 조건 필터

| 기획서 요구사항 | API 검증 포인트 | 에러 코드 |
|-----------------|-----------------|-----------|
| 필터 7종 지원 | filterType: SIZE, GENDER, NEUTERED, BREED, MBTI, PERSONALITY, WALKING_STYLE | - |
| 필수 조건 최대 3개 | isRequired=true 개수 <= 3 | T011 |
| 필수 미충족 시 미노출 | 목록/상세 응답에서 제외 | T001 (상세) |
| 메인 반려견 기준 판정 | myFilterStatus 계산 로직 | - |
| 비애견인 필수필터 스레드 미노출 | NON_PET_OWNER + 반려견 필수필터 → 미노출 | - |

#### 스레드 작성 제한

| 기획서 요구사항 | API 에러 코드 | 검증 포인트 |
|-----------------|---------------|-------------|
| 동시 1개 제한 | T002 | 활성 스레드 존재 시 |
| 비애견인 작성 불가 | T012 | memberType 검증 |

#### 지도 표시 옵션

| 기획서 요구사항 | API Field | 검증 포인트 |
|-----------------|-----------|-------------|
| 항상 표시 | isVisibleAlways: true | - |
| 시간대만 표시 | isVisibleAlways: false | startHour/endHour 필터로 노출 |

---

### 2.3 지도 기반 탐색 (Section 5.B)

| 기획서 요구사항 | API Endpoint | 검증 포인트 |
|-----------------|--------------|-------------|
| 시간대 필터링 | `GET /threads?startHour=&endHour=` | Query param 지원 |
| 시간순 정렬 | `GET /threads?sort=startTime` | sort 파라미터 |
| 거리순 정렬 | `GET /threads?sort=distance&latitude=&longitude=` | 위치 기반 정렬 |
| 인기순 정렬 | `GET /threads?sort=popularity` | 참여자수/조회수 기준 |
| 지도 마커용 API | `GET /threads/map` | 간략 정보 반환 |
| 중복 방지 로직 | `GET /threads/check-duplicate` | 같은 장소/날짜/시간대 확인 |

---

### 2.4 매칭 및 채팅 시스템 (Section 6)

#### 산책 신청 프로세스

| 기획서 요구사항 | API | 검증 포인트 |
|-----------------|-----|-------------|
| 차단 유저 스레드 미노출 | `GET /threads` 필터링 | 차단 목록 제외 |
| 나를 차단한 유저 신청 불가 | `POST /threads/{id}/apply` | M006 에러 |
| 다견 가구 반려견 선택 | petIds 파라미터 | PET_OWNER 필수 |
| 신청 = 채팅방 입장 | apply 응답에 chatRoomId | 즉시 생성 |

#### 개별 대화 방식 (INDIVIDUAL)

| 기획서 요구사항 | API 검증 포인트 |
|-----------------|-----------------|
| 작성자와 각각 1:1 채팅방 생성 | chatType: INDIVIDUAL |
| 신청자들 서로 모름 | 다른 채팅방 참여자 정보 없음 |
| 정원 제한 없음 | maxParticipants null |
| 작성자가 1명만 확정 | CH007 에러 (중복 확정 시) |

#### 그룹 대화 방식 (GROUP)

| 기획서 요구사항 | API 검증 포인트 |
|-----------------|-----------------|
| 단체 채팅방 하나 | chatType: GROUP |
| 참여자 모두 표시 | participants 배열 |
| 선착순 정원 제한 | T005 에러 (정원 초과 시) |

#### 산책 참가 확정 (1:1 전용)

| 기획서 요구사항 | API | 에러 코드 |
|-----------------|-----|-----------|
| 양쪽 각각 확정 필요 | `POST /chat-rooms/{id}/walk-confirm` | - |
| 작성자 1명만 확정 가능 | 같은 스레드 다른 채팅방 확정 검증 | CH007 |
| 상대 확정 전만 취소 가능 | `DELETE /chat-rooms/{id}/walk-confirm` | CH009 |
| 확정 정보 공개 영역 미표시 | Thread/프로필에 없음 | - |

#### 채팅방 운영

| 기획서 요구사항 | API 검증 포인트 |
|-----------------|-----------------|
| 산책 종료 +2시간 자동 종료 | chatEndTime 필드, ARCHIVED 상태 |
| 아카이브 후 읽기 전용 | ARCHIVED 상태에서 메시지 전송 시 CH002 |
| 시스템 메시지 | messageType: SYSTEM |

---

### 2.5 안전 및 신뢰 시스템 (Section 7)

#### 차단

| 기획서 요구사항 | API | 검증 포인트 |
|-----------------|-----|-------------|
| 사용자 차단 | `POST /blocks` | targetMemberId |
| 차단 유저 스레드 미노출 | 목록 필터링 | - |
| 차단 유저 내 스레드 신청 불가 | 신청 시 M006 | - |
| 차단 목록 조회 | `GET /blocks` | - |
| 차단 해제 | `DELETE /blocks/{blockId}` | - |

#### 산책 후기

| 기획서 요구사항 | API | 에러 코드 |
|-----------------|-----|-----------|
| 1~10점 점수 | score 필드 1-10 | CH010 |
| 한줄 후기 (선택) | comment 필드 nullable | - |
| 그룹: 모두 평가 가능 | 같은 채팅방 참가자 대상 | CH011 |
| 1:1: 확정 후만 평가 | isWalkConfirmed 검증 | CH012 |
| 산책 종료 후만 작성 | endTime 이후 | CH013 |
| 같은 대상 1회만 | 중복 검증 | CH014 |

#### 매너 온도

| 기획서 요구사항 | 검증 포인트 |
|-----------------|-------------|
| 신규 유저 5.0점 | 기본값 5.0 |
| 평균값 계산 | 받은 점수 합계 / 개수 |
| 소수점 1자리 | 반올림 처리 |

---

### 2.6 커뮤니티 기능 (Section 8)

| 기획서 요구사항 | API | 검증 포인트 |
|-----------------|-----|-------------|
| 게시물 작성 | `POST /posts` | content, imageUrls |
| 게시물 목록 | `GET /posts` | SliceResponse (무한 스크롤) |
| 게시물 상세 | `GET /posts/{id}` | 댓글 포함 |
| 게시물 수정 | `PATCH /posts/{id}` | 작성자만 |
| 게시물 삭제 | `DELETE /posts/{id}` | 작성자만 |
| 댓글 | `POST /posts/{id}/comments` | - |
| 좋아요 | `POST /posts/{id}/like` | 토글 방식 |

---

### 2.7 알림 시스템 (Section 9)

| 알림 유형 | NotificationType | targetType |
|-----------|------------------|------------|
| 채팅 메시지 | CHAT_MESSAGE | CHAT_ROOM |
| 산책 신청 | WALK_APPLICATION | THREAD |
| 실종 유사 제보 | LOST_PET_SIMILAR | LOST_PET |

---

### 2.8 실종 반려견 찾기 (Section 10)

#### 견주 플로우

| 기획서 요구사항 | API | 검증 포인트 |
|-----------------|-----|-------------|
| 실종 등록 (사진 필수) | `POST /lost-pets` | photoUrls required |
| 텍스트 특징 설명 (선택) | textFeatures nullable | - |
| YOLO 강아지 감지 | croppedPhotoUrl 응답 | L001 에러 (미감지) |
| 멀티모달 임베딩 | 내부 처리 | - |
| 유사 제보 목록 | `GET /lost-pets/{id}/similar-sightings` | similarityScore |
| 필터링 (반경, 기간) | radius, days 파라미터 | - |
| "이 아이예요!" | `POST /lost-pets/{id}/match` | chatRoomId 반환 |
| 실종 등록 종료 | `PATCH /lost-pets/{id}/close` | FOUND/CLOSED |

#### 제보자 플로우

| 기획서 요구사항 | API | 검증 포인트 |
|-----------------|-----|-------------|
| 발견 사진 업로드 | `POST /sightings` | photoUrl required |
| 설명 (선택) | description nullable | - |
| 내 제보 목록 | `GET /sightings/mine` | - |
| 제보 삭제 | `DELETE /sightings/{id}` | L003 (본인만) |

#### 유사도 점수

| 기획서 요구사항 | API Response | 검증 포인트 |
|-----------------|--------------|-------------|
| 멀티모달 유사도 50% | similarityScore.image | 0~1 |
| 지역 근접도 30% | similarityScore.location | 0~1 |
| 시간 근접도 20% | similarityScore.time | 0~1 |
| 종합 점수 | similarityScore.total | 가중 합계 |

---

## 3. 보류 항목 확인

기획서 Section 13에 명시된 보류 항목은 API 미구현이 정상입니다:

| 보류 항목 | API 상태 | 비고 |
|-----------|----------|------|
| 노쇼 처리 | 미구현 | 페널티/판단 기준 미정 |
| 평가 미완료 페널티 | 미구현 | MVP 제외 |
| 애견 MBTI 측정 | 미구현 | 테스트 제공 검토 중 |
| 정기 모임 | 미구현 | Post MVP |
| 멀티모달 유사도 계산 방식 | 구현됨 (가중평균) | 튜닝 대상 |
| LLM 2차 판단 | 미구현 | 고도화 단계 |
| 아카이브 보관 기간 | 미정의 | 정책 결정 필요 |

---

## 4. 검증 체크리스트 요약

### 필수 검증 항목

- [ ] 모든 입력값 제한이 API Request에 반영됨
- [ ] 모든 입력값 제한 위반에 대한 에러 코드 존재
- [ ] 모든 비즈니스 규칙이 API 또는 에러 코드로 처리 가능
- [ ] 모든 사용자 플로우가 API 시퀀스로 구현 가능
- [ ] 보류 항목은 API 미구현 상태 확인

### 검증 순서

1. **입력값 제한** → Request Field 검증
2. **에러 코드** → 비즈니스 규칙 위반 처리
3. **Response 데이터** → 기획서 필요 데이터 포함 여부
4. **사용자 플로우** → API 시퀀스 추적
5. **보류 항목** → 미구현 정상 확인

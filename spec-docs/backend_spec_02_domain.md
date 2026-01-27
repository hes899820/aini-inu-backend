# 2. 도메인 모델링

## 2.1 바운디드 컨텍스트 (Bounded Context)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                              아이니이누 도메인                                │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐         │
│  │  Member Context │    │   Pet Context   │    │  Walk Context   │         │
│  │                 │    │                 │    │                 │         │
│  │  - Member       │    │  - Pet          │    │  - Thread       │         │
│  │  - SocialAuth   │    │  - PetBreed     │    │  - ThreadFilter │         │
│  │  - Block        │    │  - PetPersonalityType│    │  - Application  │         │
│  │  - MemberPersonalityType│    │  - WalkingStyle│    │                 │         │
│  │  - MannerScore  │    │  - PetCert      │    │  - Location     │         │
│  └────────┬────────┘    └────────┬────────┘    └────────┬────────┘         │
│           │                      │                      │                  │
│           └──────────────────────┼──────────────────────┘                  │
│                                  │                                         │
│  ┌─────────────────┐    ┌────────┴────────┐    ┌─────────────────┐         │
│  │  Chat Context   │    │ LostPet Context │    │Community Context│         │
│  │                 │    │                 │    │                 │         │
│  │  - ChatRoom     │    │  - LostReport   │    │  - Post         │         │
│  │  - Participant  │    │  - Sighting     │    │  - Comment      │         │
│  │  - Message      │    │  - AIEmbedding  │    │  - PostLike     │         │
│  │  - Archive      │    │  - Match        │    │                 │         │
│  └─────────────────┘    └─────────────────┘    └─────────────────┘         │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────┐       │
│  │                      Notification Context                        │       │
│  │                                                                  │       │
│  │  - Notification    - NotificationSetting    - DigestQueue        │       │
│  └─────────────────────────────────────────────────────────────────┘       │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```

## 2.2 컨텍스트 간 관계

| Source Context | Target Context | 관계 유형 | 설명 |
|---------------|----------------|----------|------|
| Member | Pet | 고객-공급자 | 회원이 반려견 소유 |
| Member | Walk | 고객-공급자 | 회원이 스레드 작성/참여 |
| Walk | Chat | 고객-공급자 | 스레드에서 채팅방 생성 |
| Pet | LostPet | 고객-공급자 | 반려견 실종 신고 |
| Member | Community | 고객-공급자 | 회원이 게시물 작성 |
| All | Notification | 이벤트 기반 | 도메인 이벤트 발행 |

## 2.3 애그리게잇 설계

### Member Aggregate

```
Member (루트)
├── id: Long
├── email: String
├── nickname: String
├── profileImageUrl: String
├── age: Integer (nullable)
├── gender: MemberGender (MALE, FEMALE, UNKNOWN)
├── mbti: String (nullable)
├── personality: String (nullable, 성격)
├── selfIntroduction: String (nullable, 자기소개)
├── personalityTypes: List<MemberPersonalityType> (연결 엔티티)
├── memberType: MemberType (PET_OWNER, NON_PET_OWNER)
├── linkedNickname: String (nullable, 애견연계닉네임)
├── mannerScoreSum: Integer (>= 0)
├── mannerScoreCount: Integer (>= 0)
├── mannerTemperature: Decimal (1.0 ~ 10.0, 평균)
├── status: MemberStatus (ACTIVE, INACTIVE, BANNED)
├── isVerified: Boolean
├── socialProvider: SocialProvider (NAVER, KAKAO, GOOGLE)
├── socialId: String
├── createdAt: DateTime
└── updatedAt: DateTime

불변식:
- mannerTemperature 범위: 1.0 ≤ x ≤ 10.0
- mannerTemperature 계산:
  - mannerScoreCount = 0 → 5.0 (기본 표시)
  - 그 외 → round(mannerScoreSum / mannerScoreCount, 1)
- nickname 길이: 1 ~ 10자
- nickname 유니크
- 가입 완료 시 기본 memberType: NON_PET_OWNER
- 반려견을 1마리 이상 등록하면 memberType: PET_OWNER (자동 전환)
- 반려견이 0마리가 되면 memberType: NON_PET_OWNER (자동 전환)
- socialProvider + socialId 조합 유니크
```

### MemberPersonalityType / MemberPersonality (견주 성향)

```
MemberPersonalityType (마스터)
├── id: Long
├── name: String
└── code: String

MemberPersonality (연결)
├── id: Long
├── memberId: Long
└── personalityTypeId: Long

불변식:
- (memberId, personalityTypeId) 유니크 (중복 선택 방지)
```

### MannerScore Aggregate (산책 후기/매너 평가)

```
MannerScore (루트)
├── id: Long
├── chatRoomId: Long (외부 참조, roomPurpose=WALK)
├── reviewerId: Long (외부 참조)
├── revieweeId: Long (외부 참조)
├── score: Integer (1 ~ 10)
├── comment: String (nullable)
└── createdAt: DateTime

불변식:
- score 범위: 1 ≤ x ≤ 10
- reviewerId ≠ revieweeId
- (chatRoomId, reviewerId, revieweeId) 유니크 (중복 후기 방지)
- 산책 종료 후에만 생성 가능
- chatType=INDIVIDUAL 인 경우, 산책 참가 확정(양측) 완료 시에만 생성 가능
```

### Pet Aggregate

```
Pet (루트)
├── id: Long
├── memberId: Long (외부 참조)
├── name: String (최대 10자)
├── breedId: Long (외부 참조)
├── age: Integer
├── gender: PetGender (MALE, FEMALE)
├── size: PetSize (SMALL, MEDIUM, LARGE)
├── mbti: String (nullable)
├── isNeutered: Boolean
├── photoUrl: String
├── isMain: Boolean
├── certificationNumber: String (nullable, 동물등록번호)
├── isCertified: Boolean
├── walkingStyles: List<WalkingStyle> (값 객체)
├── personalities: List<PetPersonality> (연결 엔티티)
├── createdAt: DateTime
└── updatedAt: DateTime

불변식:
- 한 회원당 최대 10마리
- 한 회원당 isMain=true인 반려견 최대 1마리
- name 길이: 1 ~ 10자
```

### Thread Aggregate

```
Thread (루트)
├── id: Long
├── authorId: Long (외부 참조)
├── title: String (최대 30자)
├── description: String (최대 500자)
├── walkDate: Date
├── startTime: DateTime
├── endTime: DateTime
├── chatType: ChatType (INDIVIDUAL, GROUP)
├── maxParticipants: Integer (3~10, 그룹만)
├── currentParticipants: Integer (GROUP: 참가자 수, INDIVIDUAL: 활성 1:1 채팅방 수)
├── allowNonPetOwner: Boolean
├── status: ThreadStatus (ACTIVE, CLOSED)
├── isVisibleAlways: Boolean
├── location: Location (값 객체)
│   ├── placeName: String
│   ├── latitude: Decimal
│   └── longitude: Decimal
├── threadPets: List<ThreadPet> (연결)
├── filters: List<ThreadFilter> (연결)
├── createdAt: DateTime
└── updatedAt: DateTime

불변식:
- endTime > startTime
- walkDate: 현재 ~ +7일 이내
- 그룹 채팅: 3 ≤ maxParticipants ≤ 10
- 필수 필터 최대 3개
- 스레드 작성자(author)는 PET_OWNER
- 작성자당 동시 ACTIVE 스레드 1개
- currentParticipants ≤ maxParticipants (그룹)
- chatType=INDIVIDUAL 인 경우, 작성자는 스레드 내에서 **1개의 1:1 채팅방만** 산책 참가 확정 가능 (상대 확정 전에는 취소 후 변경 가능)
- 필수 조건(Hard Filter)을 충족하지 못하는 회원에게는 스레드가 노출되지 않음
- 다견 가구 애견인의 필터 충족 판정은 메인 반려견 기준
- NON_PET_OWNER는 allowNonPetOwner=true인 스레드만 노출되며, 반려견 관련 필수 조건이 있는 스레드는 노출되지 않음
```

### ChatRoom Aggregate

```
ChatRoom (루트)
├── id: Long
├── roomPurpose: RoomPurpose (WALK, LOST_PET_MATCH)
├── threadId: Long (nullable, WALK 채팅방만 사용)
├── lostPetMatchId: Long (nullable, LOST_PET_MATCH 채팅방만 사용)
├── chatType: ChatType (INDIVIDUAL, GROUP)
├── status: ChatRoomStatus (ACTIVE, ARCHIVED)
├── archivedAt: DateTime (nullable)
├── participants: List<ChatParticipant> (연결)
├── createdAt: DateTime
└── updatedAt: DateTime

ChatParticipant (연결)
├── id: Long
├── memberId: Long (외부 참조)
├── isAuthor: Boolean
├── joinedAt: DateTime
├── leftAt: DateTime (nullable)
└── walkConfirmedAt: DateTime (nullable, WALK + INDIVIDUAL 전용)

불변식:
- 아카이브 후 새 메시지 불가
- roomPurpose = WALK: threadId 필수, lostPetMatchId NULL
- roomPurpose = LOST_PET_MATCH: lostPetMatchId 필수, threadId NULL
- WALK + INDIVIDUAL: 참여자 2명 고정
- WALK + INDIVIDUAL: 산책 참가 확정은 각 참여자의 walkConfirmedAt으로 관리 (양쪽 모두 확정 시 확정 완료)
- WALK + INDIVIDUAL: walkConfirmedAt 취소(되돌리기)는 상대가 아직 확정하지 않은 경우에만 가능 (양쪽 확정 완료 후에는 취소 불가)
- WALK + GROUP: 참여자 2~10명
- LOST_PET_MATCH: 항상 INDIVIDUAL (견주-제보자 1:1)
```

### LostPetReport Aggregate

```
LostPetReport (루트)
├── id: Long
├── memberId: Long (외부 참조)
├── petId: Long (외부 참조)
├── description: String
├── photoUrls: List<String>
├── croppedPhotoUrl: String (YOLO 크롭)
├── imageEmbedding: Vector (CLIP 임베딩)
├── textEmbedding: Vector (CLIP 임베딩, nullable)
├── textFeatures: String (텍스트 특징 설명)
├── lastSeenLocation: Location (값 객체)
├── lastSeenAt: DateTime
├── status: LostPetStatus (SEARCHING, FOUND, CLOSED)
├── isNotificationEnabled: Boolean
├── createdAt: DateTime
├── updatedAt: DateTime
└── closedAt: DateTime (nullable)

불변식:
- 종료 후 알림 발송 중단
- imageEmbedding 필수 (사진 필수)
```

### Sighting Aggregate (제보)

```
Sighting (루트)
├── id: Long
├── finderId: Long (외부 참조)
├── photoUrl: String
├── croppedPhotoUrl: String (YOLO 크롭)
├── imageEmbedding: Vector (CLIP 임베딩)
├── description: String (선택)
├── foundLocation: Location (값 객체)
├── foundAt: DateTime
├── status: SightingStatus (ACTIVE, MATCHED)
├── createdAt: DateTime
└── updatedAt: DateTime

불변식:
- photoUrl 필수
- imageEmbedding 필수
```

## 2.4 값 객체 (Value Objects)

### Location
```java
Location {
    placeName: String
    latitude: Decimal(10, 8)
    longitude: Decimal(11, 8)
    address: String (nullable)
}
```

### TimeRange
```java
TimeRange {
    startTime: DateTime
    endTime: DateTime
    
    // 불변식: endTime > startTime
}
```

### SimilarityScore
```java
SimilarityScore {
    imageScore: Decimal (0.0 ~ 1.0)
    textScore: Decimal (0.0 ~ 1.0, nullable)
    locationScore: Decimal (0.0 ~ 1.0)
    timeScore: Decimal (0.0 ~ 1.0)
    totalScore: Decimal (가중 합계)
}
```

## 2.5 도메인 이벤트

| 이벤트 | 발생 조건 | 후속 처리 |
|--------|----------|----------|
| `MemberRegistered` | 회원 가입 완료 | 환영 알림 발송 |
| `ThreadCreated` | 스레드 생성 | 지도 노출, 알림 대상 추출 |
| `ThreadModified` | 스레드 수정 | 채팅방 시스템 메시지 |
| `ThreadDeleted` | 스레드 삭제 | 채팅방 종료, 아카이브 |
| `ApplicationSubmitted` | 산책 신청 | 채팅방 입장, 알림 |
| `ChatRoomArchived` | 채팅방 종료 | 읽기 전용 전환 |
| `LostPetReported` | 실종 등록 | 기존 제보 검색, 알림 설정 |
| `SightingReported` | 제보 등록 | 실종 DB 검색, 다이제스트 큐 |
| `LostPetMatched` | "이 아이예요!" 클릭 | 1:1 채팅방 생성 |
| `MannerScoreCreated` | 산책 후기 작성 | 매너 온도 재계산, 프로필 업데이트 |

## 2.6 상태 전이 다이어그램

### Thread Status
```
          ┌─────────┐
   생성 → │ ACTIVE  │
          └────┬────┘
               │
               ▼
          ┌───────┐
          │ CLOSED │
          └───────┘
          (종료시간)
```

### ChatRoom Status
```
          ┌─────────┐
   생성 → │ ACTIVE  │
          └────┬────┘
               │ (종료시간+2h 또는 스레드 삭제)
               ▼
          ┌──────────┐
          │ ARCHIVED │
          └──────────┘
```

### LostPetReport Status
```
          ┌───────────┐
   등록 → │ SEARCHING │
          └─────┬─────┘
                │
        ┌───────┼───────┐
        ▼               ▼
    ┌───────┐       ┌────────┐
    │ FOUND │       │ CLOSED │
    └───────┘       └────────┘
    (반려견발견)     (견주직접종료)
```

---

# Validation Checklist

API 명세서와 구현 코드 간의 검증 체크리스트입니다.

## 1. Endpoint 검증

### 1.1 HTTP Method & Path

```
Spec: POST /pets
Code: @PostMapping (in @RequestMapping("/api/v1/pets") controller)
```

| 검증 항목 | 확인 방법 |
|----------|----------|
| HTTP Method | `@GetMapping`, `@PostMapping`, `@PatchMapping`, `@DeleteMapping` |
| Path | `@RequestMapping` value + method mapping value |
| Path Variables | `@PathVariable` 이름과 타입 |
| Query Parameters | `@RequestParam` 이름, 타입, required 여부 |

### 1.2 Base URL 규칙

```
Spec Base URL: /api/v1
Controller: @RequestMapping("/api/v1/{context}")
```

## 2. Request DTO 검증

### 2.1 필드 존재 여부

명세서의 모든 Request 필드가 DTO에 존재하는지 확인:

```java
// Spec: name (string, required, max 10)
// Code:
@NotBlank
@Size(max = 10)
private String name;
```

### 2.2 필수/선택 여부

| Spec | Code Annotation |
|------|-----------------|
| 필수 (O) | `@NotNull`, `@NotBlank`, `@NotEmpty` |
| 선택 (X) | 어노테이션 없음 또는 `@Nullable` |

### 2.3 타입 매핑

| Spec Type | Java Type |
|-----------|-----------|
| string | String |
| integer | Integer, Long |
| boolean | Boolean |
| array | List<T> |
| object | 별도 DTO 클래스 |
| decimal | BigDecimal, Double |
| datetime (ISO 8601) | LocalDateTime, OffsetDateTime |
| date (ISO 8601) | LocalDate |

### 2.4 Validation 규칙

| Spec 규칙 | Code Annotation |
|----------|-----------------|
| 최대 길이 10자 | `@Size(max = 10)` |
| 최소값 0 | `@Min(0)` |
| 최대값 100 | `@Max(100)` |
| 이메일 형식 | `@Email` |
| 정규식 패턴 | `@Pattern(regexp = "...")` |
| Enum 값 | enum 타입 사용 |

### 2.5 필드 이름 규칙

- Spec: camelCase (예: `profileImageUrl`)
- Code: 동일한 camelCase (예: `private String profileImageUrl`)
- JSON 직렬화 시 동일한 이름 사용

## 3. Response DTO 검증

### 3.1 공통 응답 포맷

```json
{
  "success": true,
  "status": 200,
  "data": { ... }
}
```

```java
// ApiResponse<T> 구조 확인
public class ApiResponse<T> {
    private boolean success;
    private int status;
    private T data;
    // 에러 시 추가 필드
    private String errorCode;
    private String message;
}
```

### 3.2 Response 필드

명세서의 모든 Response 필드가 DTO에 존재하는지 확인:

```java
// Spec Response:
// {
//   "id": 1,
//   "name": "몽이",
//   "breed": { "id": 15, "name": "포메라니안" }
// }

// Code:
@Getter
@Builder
public class PetResponse {
    private Long id;
    private String name;
    private BreedInfo breed;  // 중첩 객체

    @Getter
    @Builder
    public static class BreedInfo {
        private Long id;
        private String name;
    }
}
```

### 3.3 페이징 응답

**PageResponse** (총 개수 필요):
```java
public class PageResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
}
```

**SliceResponse** (무한 스크롤):
```java
public class SliceResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private boolean first;
    private boolean last;
    private boolean hasNext;
}
```

## 4. Error Code 검증

### 4.1 에러 코드 형식

```
{PREFIX}{NUMBER}
```

| Context | Prefix | 예시 |
|---------|--------|------|
| Common | C | C001, C101, C201 |
| Member | M | M001, M002 |
| Pet | P | P001, P002 |
| Thread | T | T001, T002 |
| Chat | CH | CH001, CH002 |
| Community | CO | CO001 |
| LostPet | L | L001, L002 |
| Notification | N | N001 |

### 4.2 ErrorCode Enum 구조

```java
@Getter
@RequiredArgsConstructor
public enum PetErrorCode implements ErrorCode {
    PET_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "반려견을 찾을 수 없습니다"),
    PET_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "P002", "등록 가능 마릿수(10)를 초과했습니다"),
    // ...
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
```

### 4.3 HTTP Status 매핑

| Spec Status | HttpStatus |
|-------------|------------|
| 400 | HttpStatus.BAD_REQUEST |
| 401 | HttpStatus.UNAUTHORIZED |
| 403 | HttpStatus.FORBIDDEN |
| 404 | HttpStatus.NOT_FOUND |
| 409 | HttpStatus.CONFLICT |
| 500 | HttpStatus.INTERNAL_SERVER_ERROR |

## 5. Context별 검증 대상

### 5.1 Member Context (회원)

| Endpoint | Controller | DTO | ErrorCode |
|----------|------------|-----|-----------|
| POST /members/profile | MemberController | MemberProfileRequest, MemberResponse | MemberErrorCode |
| GET /members/me | MemberController | MemberResponse | MemberErrorCode |
| PATCH /members/me | MemberController | MemberUpdateRequest | MemberErrorCode |
| GET /members/{memberId} | MemberController | MemberPublicResponse | MemberErrorCode |

### 5.2 Pet Context (반려견)

| Endpoint | Controller | DTO | ErrorCode |
|----------|------------|-----|-----------|
| POST /pets | PetController | PetCreateRequest, PetResponse | PetErrorCode |
| GET /pets | PetController | List<PetSummaryResponse> | PetErrorCode |
| PATCH /pets/{petId} | PetController | PetUpdateRequest | PetErrorCode |
| DELETE /pets/{petId} | PetController | - | PetErrorCode |
| PATCH /pets/{petId}/main | PetController | PetMainResponse | PetErrorCode |

### 5.3 Thread Context (스레드)

| Endpoint | Controller | DTO | ErrorCode |
|----------|------------|-----|-----------|
| POST /threads | ThreadController | ThreadCreateRequest, ThreadResponse | ThreadErrorCode |
| GET /threads | ThreadController | SliceResponse<ThreadSummaryResponse> | ThreadErrorCode |
| GET /threads/{threadId} | ThreadController | ThreadDetailResponse | ThreadErrorCode |
| POST /threads/{threadId}/apply | ThreadController | ThreadApplyRequest, ThreadApplyResponse | ThreadErrorCode |

### 5.4 Chat Context (채팅)

| Endpoint | Controller | DTO | ErrorCode |
|----------|------------|-----|-----------|
| GET /chat-rooms | ChatRoomController | PageResponse<ChatRoomSummaryResponse> | ChatErrorCode |
| GET /chat-rooms/{chatRoomId} | ChatRoomController | ChatRoomDetailResponse | ChatErrorCode |
| GET /chat-rooms/{chatRoomId}/messages | MessageController | CursorResponse<MessageResponse> | ChatErrorCode |
| POST /chat-rooms/{chatRoomId}/messages | MessageController | MessageCreateRequest | ChatErrorCode |

## 6. 검증 결과 판정

### 6.1 Match (일치)

- 모든 항목이 명세서와 정확히 일치

### 6.2 Mismatch (불일치)

- 필드 이름, 타입, 필수 여부 등이 다름
- 수정 필요

### 6.3 Missing (누락)

- 명세서에 있지만 코드에 없음
- 구현 필요

### 6.4 Extra (추가)

- 코드에 있지만 명세서에 없음
- 명세서 업데이트 또는 코드 제거 필요

## 7. 검증 우선순위

1. **Critical**: Endpoint 존재 여부
2. **High**: 필수 필드 누락
3. **Medium**: 타입 불일치, Validation 규칙 누락
4. **Low**: 필드 이름 대소문자, 선택 필드 누락

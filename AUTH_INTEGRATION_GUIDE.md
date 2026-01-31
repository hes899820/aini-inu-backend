# JWT Authentication Integration Guide

### 구현된 컴포넌트

#### 1. Annotations
- **`@Public`**: 인증이 필요 없는 엔드포인트 표시
- **`@CurrentMember`**: 인증된 회원 ID 자동 주입

#### 2. Core Components
- **`JwtTokenProvider`**: JWT 생성, 검증, 파싱
- **`JwtAuthInterceptor`**: 모든 API 요청 인증 처리
- **`CurrentMemberArgumentResolver`**: @CurrentMember 파라미터 주입
- **`WebConfig`**: Interceptor, ArgumentResolver, CORS 설정
- **`SecurityConfig`**: Spring Security 기본 동작 비활성화

#### 3. Test Utilities
- **`TestAuthController`**: 테스트용 토큰 생성 엔드포인트

---

## 기존 Controller 마이그레이션

### Before (기존 코드)
```java
@GetMapping
public ResponseEntity<ApiResponse<SliceResponse<PostResponse>>> getPosts(
    @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
) {
    // TODO Security 적용 후: 로그인 사용자에서 memberId 추출
    Long memberId = 1L; // 임시 사용자

    SliceResponse<PostResponse> response = postService.getPosts(memberId, pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
}
```

### After (마이그레이션 후)
```java
@GetMapping
public ResponseEntity<ApiResponse<SliceResponse<PostResponse>>> getPosts(
    @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,
    @CurrentMember Long memberId  // 자동 주입
) {
    SliceResponse<PostResponse> response = postService.getPosts(memberId, pageable);
    return ResponseEntity.ok(ApiResponse.success(response));
}
```

### 마이그레이션 체크리스트
1. ✅ TODO 주석 삭제
2. ✅ `Long memberId = 1L;` 하드코딩 제거
3. ✅ 메서드 파라미터에 `@CurrentMember Long memberId` 추가
4. ✅ Import 추가: `import scit.ainiinu.common.security.annotation.CurrentMember;`

---

## 사용 예시

### 1. Protected Endpoint (인증 필수)
```java
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 인증 필수 (기본 동작)
    @GetMapping
    public ResponseEntity<ApiResponse<List<Post>>> getPosts(
        @CurrentMember Long memberId  // 인증된 사용자 ID 자동 주입
    ) {
        return ResponseEntity.ok(ApiResponse.success(
            postService.getPostsByMember(memberId)
        ));
    }
}
```

### 2. Public Endpoint (인증 불필요)
```java
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Public  // 전체 컨트롤러 공개
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    // @Public 어노테이션이 컨트롤러 레벨에 있으므로 인증 불필요
    @PostMapping("/login/{provider}")
    public ResponseEntity<ApiResponse<TokenResponse>> login(
        @PathVariable String provider,
        @RequestBody LoginRequest request
    ) {
        Member member = memberService.loginWithSocial(provider, request);

        String accessToken = jwtTokenProvider.generateAccessToken(member.getId());
        String refreshToken = jwtTokenProvider.generateRefreshToken(member.getId());

        return ResponseEntity.ok(ApiResponse.success(
            new TokenResponse(accessToken, refreshToken, member.getId())
        ));
    }
}
```

### 3. Optional Authentication (선택적 인증)
```java
@GetMapping("/{postId}")
public ResponseEntity<ApiResponse<PostDetailResponse>> getPost(
    @PathVariable Long postId,
    @CurrentMember(required = false) Long memberId  // null 가능
) {
    // memberId가 null이면 비회원, 아니면 회원
    PostDetailResponse response = postService.getPost(postId, memberId);
    return ResponseEntity.ok(ApiResponse.success(response));
}
```

### 4. Method-Level @Public (특정 메서드만 공개)
```java
@RestController
@RequestMapping("/api/v1/posts")
public class PostController {

    // 인증 필수
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
        @CurrentMember Long memberId,
        @RequestBody PostCreateRequest request
    ) {
        // 인증된 사용자만 접근 가능
    }

    // 인증 불필요
    @Public
    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPopularPosts() {
        // 누구나 접근 가능
    }
}
```

---

## 테스트 방법

### 1. 애플리케이션 실행
```bash
./gradlew bootRun
```

### 2. 테스트 스크립트 실행
```bash
./test-auth.sh
```

이 스크립트는 다음을 테스트합니다:
- ✅ 토큰 생성
- ✅ 인증 없이 Protected Endpoint 접근 (C101 에러)
- ✅ 유효한 토큰으로 Protected Endpoint 접근 (성공)
- ✅ @CurrentMember 파라미터 주입
- ✅ 잘못된 토큰으로 접근 (C102 에러)
- ✅ Public Endpoint 접근 (인증 없이 성공)

### 3. 수동 테스트

#### Step 1: 토큰 생성
```bash
curl -X POST http://localhost:8080/api/v1/test/auth/token?memberId=1
```

**응답:**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
    "memberId": "1"
  }
}
```

#### Step 2: 토큰 없이 Protected Endpoint 호출 (실패)
```bash
curl -X GET http://localhost:8080/api/v1/posts
```

**응답:**
```json
{
  "success": false,
  "status": 401,
  "errorCode": "C101",
  "message": "인증이 필요합니다"
}
```

#### Step 3: 유효한 토큰으로 Protected Endpoint 호출 (성공)
```bash
TOKEN="eyJhbGciOiJIUzI1NiJ9..."  # Step 1에서 받은 accessToken

curl -X GET http://localhost:8080/api/v1/posts \
  -H "Authorization: Bearer $TOKEN"
```

**응답:**
```json
{
  "success": true,
  "status": 200,
  "data": {
    "content": [...],
    "hasNext": false
  }
}
```

---

## 에러 코드

| Error Code | HTTP Status | 설명 | 발생 시점 |
|-----------|-------------|------|----------|
| C101 | 401 Unauthorized | 인증이 필요합니다 (토큰 없음) | Authorization 헤더 누락 |
| C102 | 401 Unauthorized | 유효하지 않은 토큰입니다 | 잘못된 서명, 형식 오류 |
| C103 | 401 Unauthorized | 만료된 토큰입니다 | 토큰 만료 (1시간 경과) |
| C201 | 403 Forbidden | 권한이 없습니다 | 향후 RBAC 구현 시 사용 |

---

## PostController 마이그레이션 예시

### 현재 코드 (src/main/java/scit/ainiinu/community/controller/PostController.java)

총 7개의 TODO 주석이 있습니다:

1. **Line 34-35**: `getPosts()` - 게시글 목록 조회
2. **Line 45-46**: `createPost()` - 게시글 생성
3. **Line 57**: `getPost()` - 게시글 상세 조회
4. **Line 68**: `updatePost()` - 게시글 수정
5. **Line 79-80**: `deletePost()` - 게시글 삭제
6. **Line 91**: `likePost()` - 게시글 좋아요
7. **Line 103**: `unlikePost()` - 게시글 좋아요 취소
8. **Line 114**: `createComment()` - 댓글 작성

### 마이그레이션 방법

각 메서드에서:
1. `// TODO Security 적용 후: ...` 주석 삭제
2. `Long memberId = 1L;` 하드코딩 삭제
3. 메서드 파라미터에 `@CurrentMember Long memberId` 추가

---

## 다른 이슈와의 통합

### Issue #8: 소셜 로그인 API
```java
@Public
@PostMapping("/auth/login/{provider}")
public ResponseEntity<ApiResponse<TokenResponse>> login(...) {
    Member member = memberService.findOrCreateByEmail(email);

    // JwtTokenProvider 사용
    String accessToken = jwtTokenProvider.generateAccessToken(member.getId());
    String refreshToken = jwtTokenProvider.generateRefreshToken(member.getId());

    // RefreshToken DB에 저장
    refreshTokenRepository.save(new RefreshToken(refreshToken, member.getId()));

    return ResponseEntity.ok(ApiResponse.success(
        new TokenResponse(accessToken, refreshToken, member.getId())
    ));
}
```

### Issue #9: 토큰 갱신 API
```java
@Public
@PostMapping("/auth/refresh")
public ResponseEntity<ApiResponse<TokenResponse>> refresh(@RequestBody RefreshRequest request) {
    // JwtTokenProvider로 Refresh Token 검증
    Long memberId = jwtTokenProvider.validateAndGetMemberId(request.getRefreshToken());

    // DB에서 Refresh Token 확인
    RefreshToken refreshToken = refreshTokenRepository.findByToken(request.getRefreshToken())
        .orElseThrow(() -> new BusinessException(CommonErrorCode.INVALID_TOKEN));

    // 새 토큰 생성
    String newAccessToken = jwtTokenProvider.generateAccessToken(memberId);
    String newRefreshToken = jwtTokenProvider.generateRefreshToken(memberId);

    // Refresh Token Rotation (RTR)
    refreshTokenRepository.delete(refreshToken);
    refreshTokenRepository.save(new RefreshToken(newRefreshToken, memberId));

    return ResponseEntity.ok(ApiResponse.success(
        new TokenResponse(newAccessToken, newRefreshToken, memberId)
    ));
}
```

### Issue #10: 로그아웃 API
```java
// @Public 어노테이션 없음 → 인증 필요
@PostMapping("/auth/logout")
public ResponseEntity<ApiResponse<Void>> logout(
    @CurrentMember Long memberId,  // 인증된 사용자
    @RequestBody LogoutRequest request
) {
    // DB에서 Refresh Token 삭제
    refreshTokenRepository.deleteByToken(request.getRefreshToken());

    return ResponseEntity.ok(ApiResponse.success(null));
}
```

### Issue #11: 회원가입 완료 API
```java
@PostMapping("/members/profile")
public ResponseEntity<ApiResponse<MemberResponse>> createProfile(
    @CurrentMember Long memberId,  // 소셜 로그인 후 발급받은 JWT에서 추출
    @Valid @RequestBody MemberCreateRequest request
) {
    // memberId로 임시 회원 조회
    Member member = memberRepository.findById(memberId)
        .orElseThrow(() -> new BusinessException(CommonErrorCode.MEMBER_NOT_FOUND));

    // 닉네임 중복 검증
    if (memberRepository.existsByNickname(request.getNickname())) {
        throw new BusinessException(MemberErrorCode.DUPLICATE_NICKNAME);
    }

    // 프로필 업데이트 (Dirty Checking)
    member.updateProfile(request);

    return ResponseEntity.ok(ApiResponse.success(
        MemberResponse.from(member)
    ));
}
```

---

## 프로덕션 배포 전 체크리스트

- [ ] `TestAuthController.java` 삭제
- [ ] `test-auth.sh` 삭제
- [ ] `application.properties`의 `jwt.secret`을 환경 변수로 변경
- [ ] 환경 변수 설정: `JWT_SECRET=$(openssl rand -base64 32)`
- [ ] `WebConfig.java`의 CORS allowedOrigins에 프로덕션 도메인 추가
- [ ] HTTPS 설정 (토큰 전송 보안)
- [ ] 모든 Controller에 TODO 주석 제거 및 @CurrentMember 적용 확인
- [ ] 통합 테스트 작성 (JwtAuthInterceptorTest, CurrentMemberArgumentResolverTest)

---

## 보안 권장사항

### 1. JWT Secret Key
- ❌ 개발용 키를 프로덕션에 사용하지 말 것
- ✅ 최소 32자 이상의 강력한 랜덤 키 사용
- ✅ 환경 변수로 관리 (`JWT_SECRET`)
- ✅ Git에 커밋하지 말 것

**키 생성:**
```bash
openssl rand -base64 32
```

**환경 변수 설정:**
```bash
export JWT_SECRET="your-generated-secret-key"
```

### 2. HTTPS 필수
- JWT는 평문으로 전송되므로 HTTPS 필수
- 로컬 개발: HTTP 허용
- 프로덕션: HTTPS 강제

### 3. Token Storage (프론트엔드)
- ❌ LocalStorage: XSS 공격에 취약
- ✅ HttpOnly Cookie (권장): XSS 방어
- ✅ In-memory storage + Refresh Token in HttpOnly Cookie

### 4. CORS 설정
- ❌ `allowedOrigins("*")`: 모든 도메인 허용 (보안 취약)
- ✅ 특정 도메인만 화이트리스트

---

## FAQ

### Q1: Access Token이 만료되면 어떻게 하나요?
**A:** 프론트엔드에서 Refresh Token으로 `/api/v1/auth/refresh` 호출하여 새 Access Token 발급받습니다.

### Q2: @Public과 Interceptor excludePathPatterns의 차이는?
**A:**
- `@Public`: 컨트롤러 레벨에서 특정 메서드/클래스를 공개 (유연함)
- `excludePathPatterns`: URL 패턴 기반 제외 (Swagger, Actuator 등 고정 경로)

### Q3: @CurrentMember(required = false)는 언제 사용하나요?
**A:** 로그인 여부에 따라 다른 정보를 보여주는 경우 (예: 비회원도 볼 수 있지만 로그인 시 좋아요 정보 추가 표시)

### Q4: JWT를 강제로 무효화할 수 있나요?
**A:** JWT는 Stateless이므로 서버에서 강제 무효화 불가. 완벽한 로그아웃을 위해서는 Redis 기반 Token Blacklist 구현 필요. (현재 MVP에서는 Refresh Token 삭제로 충분)

### Q5: 여러 기기에서 동시 로그인을 지원하나요?
**A:** 현재는 단일 Refresh Token만 저장. Multi-device 지원을 위해서는 RefreshToken 엔티티에 deviceId 추가 및 1:N 관계로 변경 필요.

---

## 다음 단계

1. ✅ Issue #1 (인증 인프라) - **완료**
2. ⏭️ Issue #8 (소셜 로그인 API) - JwtTokenProvider 사용
3. ⏭️ Issue #9 (토큰 갱신 API) - Refresh Token 처리
4. ⏭️ Issue #10 (로그아웃 API) - Refresh Token 삭제
5. ⏭️ Issue #11+ (모든 Protected API) - @CurrentMember 적용

---

## 문제 해결

### 문제: "Unauthorized (C101)" 에러가 계속 발생
**원인:** Authorization 헤더 형식 오류
**해결:**
```bash
# ❌ 잘못된 형식
curl -H "Authorization: token"

# ✅ 올바른 형식
curl -H "Authorization: Bearer {token}"
```

### 문제: "Invalid Token (C102)" 에러
**원인:** JWT secret key 불일치 또는 토큰 손상
**해결:** 애플리케이션 재시작 후 새 토큰 생성

### 문제: "Expired Token (C103)" 에러
**원인:** Access Token 만료 (1시간 경과)
**해결:** Refresh Token으로 새 Access Token 발급

### 문제: CORS 에러
**원인:** 프론트엔드 도메인이 allowedOrigins에 없음
**해결:** `WebConfig.java`의 allowedOrigins에 프론트엔드 도메인 추가

---

**구현 완료일:** 2026-01-31
**구현자:** 건홍
**이슈:** #1 - 인증/인가 설정 구현 (Interceptor + ArgumentResolver)

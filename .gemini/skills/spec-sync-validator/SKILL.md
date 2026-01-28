---
name: spec-sync-validator
description: "Validate consistency between API spec, ERD, and DDL documents. Use when you need to: (1) Check if API changes require ERD/DDL updates, (2) Verify field mappings between API responses and database columns, (3) Ensure new entities in API are reflected in ERD/DDL, (4) Find inconsistencies across specification documents."
---

# Spec Sync Validator

Validate consistency between API specification, ERD, and DDL documents for the 아이니이누 (Aini Inu) project.

## When to Use This Skill

Use this skill when:

- **API 명세서 변경 후**: "API 명세서에 필드를 추가했는데 ERD/DDL도 수정해야 할까?"
- **새 엔티티 추가**: "새로운 API를 추가했는데 테이블 설계가 필요할까?"
- **문서 일관성 검토**: "spec 문서들이 서로 일치하는지 확인해줘"
- **PR 전 검증**: "명세서 변경사항이 다른 문서에도 반영되었는지 확인해줘"

## Document Relationships

```
┌─────────────────────────────────────────────────────────────┐
│              API Spec (backend_spec_03_api.md)              │
│                    - Endpoints                              │
│                    - Request/Response DTOs                  │
│                    - Error Codes                            │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ 필드/엔티티 매핑
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                ERD (backend_spec_04_erd.md)                 │
│                    - Entity Definitions                     │
│                    - Relationships                          │
│                    - Field Types                            │
└─────────────────────────────────────────────────────────────┘
                              │
                              │ 1:1 매핑
                              ▼
┌─────────────────────────────────────────────────────────────┐
│                DDL (backend_spec_05_ddl.md)                 │
│                    - CREATE TABLE statements                │
│                    - Indexes                                │
│                    - Constraints                            │
└─────────────────────────────────────────────────────────────┘
```

## Quick Start

### Step 1: Read All Spec Documents

```
view spec-docs/backend_spec_03_api.md
view spec-docs/backend_spec_04_erd.md
view spec-docs/backend_spec_05_ddl.md
```

### Step 2: Read Sync Rules

```
view references/sync-rules.md
```

### Step 3: Perform Validation

Compare documents and generate sync report.

## Validation Categories

### 1. API → ERD Validation

API Response 필드가 ERD 엔티티에 존재하는지 확인:

| API Response Field | ERD Column | Status |
|--------------------|------------|--------|
| `member.nickname` | `MEMBER.nickname` | Match |
| `member.mannerTemperature` | `MEMBER.manner_temperature` | Match (camelCase → snake_case) |
| `pet.walkingStyles` | `PET_WALKING_STYLE` (연결 테이블) | Match (N:M) |

### 2. ERD → DDL Validation

ERD 정의가 DDL에 정확히 반영되었는지 확인:

| ERD Definition | DDL Statement | Status |
|----------------|---------------|--------|
| `MEMBER.nickname VARCHAR(50)` | `nickname VARCHAR(50) NOT NULL` | Match |
| `PET.is_main TINYINT(1)` | `is_main TINYINT(1) NOT NULL DEFAULT 0` | Match |

### 3. New Entity Detection

API에서 새로운 엔티티가 추가되었는지 감지:

```
API에 새로 추가된 Response:
- WalkHistoryResponse (산책 이력)

필요한 조치:
1. ERD에 WALK_HISTORY 엔티티 추가
2. DDL에 walk_history 테이블 CREATE 문 추가
```

## Output Format

### Sync Validation Report

```markdown
# Spec Sync Validation Report

## Summary
- API Entities: 15
- ERD Entities: 15
- DDL Tables: 15
- Sync Issues: 3

## API → ERD Sync Issues

### 1. Missing ERD Column
- **API**: `PetResponse.vaccinations` (array)
- **ERD**: MISSING
- **Action**: Add `PET_VACCINATION` 연결 테이블 to ERD

### 2. Type Mismatch
- **API**: `ThreadResponse.maxParticipants` (integer, required)
- **ERD**: `THREAD.max_participants` (INT, nullable)
- **Action**: ERD에서 nullable 제거 또는 API에서 optional로 변경

## ERD → DDL Sync Issues

### 1. Missing DDL Column
- **ERD**: `MEMBER.phone_number VARCHAR(20)`
- **DDL**: MISSING
- **Action**: DDL에 phone_number 컬럼 추가

### 2. Index Missing
- **ERD**: INDEX on `THREAD.walk_date`
- **DDL**: MISSING
- **Action**: DDL에 인덱스 추가

## New Entities Detected in API

| API Response | ERD Status | DDL Status |
|--------------|------------|------------|
| WalkHistoryResponse | Missing | Missing |
| PetVaccinationResponse | Missing | Missing |

## Recommendations

1. **High Priority**: ERD/DDL에 WALK_HISTORY 테이블 추가
2. **Medium Priority**: THREAD.max_participants nullable 일치시키기
3. **Low Priority**: 인덱스 정의 동기화
```

## Typical Workflow

### After API Spec Change

```
User: "API 명세서에 Pet에 vaccinations 필드를 추가했는데 ERD/DDL도 변경해야 할까?"

Steps:
1. Read spec-docs/backend_spec_03_api.md (Pet 관련 섹션)
2. Read spec-docs/backend_spec_04_erd.md (PET 엔티티)
3. Read spec-docs/backend_spec_05_ddl.md (pet 테이블)
4. Compare field mappings
5. Identify missing columns/tables
6. Generate sync report with recommendations
```

### Full Document Sync Check

```
User: "전체 명세서 문서들이 서로 일치하는지 확인해줘"

Steps:
1. Read all three spec documents
2. Build entity/field mapping
3. Compare API ↔ ERD
4. Compare ERD ↔ DDL
5. Generate comprehensive sync report
```

## Field Type Mapping Reference

| API Type | ERD Type | DDL Type |
|----------|----------|----------|
| string | VARCHAR(n) | VARCHAR(n) |
| string (long) | TEXT | TEXT |
| integer | INT | INT |
| long | BIGINT | BIGINT |
| boolean | TINYINT(1) | TINYINT(1) |
| decimal | DECIMAL(p,s) | DECIMAL(p,s) |
| datetime (ISO 8601) | DATETIME | DATETIME |
| date (ISO 8601) | DATE | DATE |
| array (simple) | TEXT (JSON) | TEXT |
| array (relation) | 연결 테이블 | 연결 테이블 |
| object (embedded) | 같은 테이블 | 같은 테이블 |
| object (relation) | 별도 테이블 | 별도 테이블 |

## Naming Convention Mapping

| API (camelCase) | ERD/DDL (snake_case) |
|-----------------|----------------------|
| `memberId` | `member_id` |
| `profileImageUrl` | `profile_image_url` |
| `isNeutered` | `is_neutered` |
| `createdAt` | `created_at` |
| `mannerTemperature` | `manner_temperature` |

## Important Notes

- **Read all three documents** before validation
- **Check both directions**: API→ERD and ERD→DDL
- **Consider relationships**: N:M relations need junction tables
- **Nullable matters**: API required fields should be NOT NULL in DDL
- **Default values**: Check if defaults in DDL match API behavior

## References

- [Sync Rules](references/sync-rules.md) - Detailed mapping rules
- [API Spec](../../../spec-docs/backend_spec_03_api.md) - API definitions
- [ERD](../../../spec-docs/backend_spec_04_erd.md) - Entity relationships
- [DDL](../../../spec-docs/backend_spec_05_ddl.md) - Database schema

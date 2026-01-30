---
name: api-spec-validator
description: "Validate implementation code against API specification. Use when you need to: (1) Check if Controllers match API spec endpoints, (2) Verify Request/Response DTOs match spec fields, (3) Ensure ErrorCodes are properly defined, (4) Find discrepancies between spec and implementation, (5) Generate change reports after spec updates."
---

# API Spec Validator

Validate that implementation code matches the API specification document for the 아이니이누 (Aini Inu) project.

## When to Use This Skill

Use this skill when you need to:

- **Spec changed**: "API 명세서가 변경되었는데 코드가 일치하는지 확인해줘"
- **Code review**: "Pet API 구현이 명세서와 맞는지 검증해줘"
- **Before PR**: "PR 올리기 전에 명세서 준수 여부 확인해줘"
- **Gap analysis**: "아직 구현되지 않은 API 목록 보여줘"

## Quick Start

### Step 1: Read API Specification

**ALWAYS** start by reading the API specification:

```
view spec-docs/backend_spec_03_api.md
```

### Step 2: Read Validation Checklist

```
view references/validation-checklist.md
```

### Step 3: Perform Validation

Based on the target context, validate:

1. **Endpoint validation**: Controller mappings match spec
2. **DTO validation**: Request/Response fields match spec
3. **Error code validation**: ErrorCode enums match spec
4. **Response format validation**: ApiResponse structure matches spec

## Validation Categories

### 1. Endpoint Validation

Check that Controller endpoints match the API spec:

| Spec | Code Check |
|------|------------|
| `POST /pets` | `@PostMapping` in PetController |
| `GET /pets/{petId}` | `@GetMapping("/{petId}")` in PetController |
| Path parameters | `@PathVariable` annotations |
| Query parameters | `@RequestParam` annotations |

### 2. Request DTO Validation

Check that Request DTOs match spec fields:

| Spec Field | Code Check |
|------------|------------|
| Required fields | `@NotNull`, `@NotBlank` annotations |
| Field types | Java type matches (String, Integer, etc.) |
| Validation rules | `@Size`, `@Min`, `@Max`, etc. |
| Field names | Exact match (camelCase) |

### 3. Response DTO Validation

Check that Response DTOs match spec fields:

| Spec Field | Code Check |
|------------|------------|
| All spec fields exist | DTO has matching fields |
| Field types | Java type matches |
| Nested objects | Separate DTO or embedded |

### 4. Error Code Validation

Check that ErrorCodes match the spec:

| Spec | Code Check |
|------|------------|
| Error code format | `{PREFIX}{NUMBER}` (e.g., P001) |
| HTTP status | `@ResponseStatus` or handler mapping |
| Message | ErrorCode enum message |

## Output Format

### Validation Report

```markdown
# API Spec Validation Report

## Summary
- Total endpoints: 15
- Implemented: 12
- Missing: 3
- Discrepancies: 2

## Missing Implementations
1. `PATCH /pets/{petId}` - PetController
2. `DELETE /pets/{petId}` - PetController
3. `PATCH /pets/{petId}/main` - PetController

## Discrepancies

### PetController.createPet
| Item | Spec | Code | Status |
|------|------|------|--------|
| Endpoint | POST /pets | POST /api/pets | Mismatch (prefix) |
| Response | PetResponse | PetResponse | Match |

### PetCreateRequest
| Field | Spec | Code | Status |
|-------|------|------|--------|
| name | required, max 10 | @NotBlank | Match |
| breedId | required | missing | Missing |
| walkingStyles | optional, array | missing | Missing |

## Error Codes

### PetErrorCode
| Code | Spec | Code | Status |
|------|------|------|--------|
| P001 | 반려견을 찾을 수 없음 | PET_NOT_FOUND | Match |
| P002 | 등록 가능 마릿수 초과 | missing | Missing |
```

## Typical Workflow

### Full Context Validation

```
User: "Pet API가 명세서와 일치하는지 검증해줘"

Steps:
1. Read spec-docs/backend_spec_03_api.md (Section 3.4 반려견)
2. Read references/validation-checklist.md
3. Find and read PetController
4. Find and read Pet-related DTOs (PetCreateRequest, PetResponse, etc.)
5. Find and read PetErrorCode
6. Compare each item against spec
7. Generate validation report
```

### After Spec Change

```
User: "API 명세서에서 Pet 필드가 추가됐는데 코드 업데이트가 필요한지 확인해줘"

Steps:
1. Read updated spec-docs/backend_spec_03_api.md
2. Read current implementation
3. Identify new/changed fields
4. List required code changes
5. Optionally generate update plan
```

## Important Notes

- **Read spec FIRST**: Always start by reading the relevant spec section
- **Check all layers**: Controller, DTO, ErrorCode, Service (if needed)
- **Report format**: Use consistent markdown table format
- **Be specific**: Include exact field names, types, and annotations
- **Suggest fixes**: When discrepancies found, suggest how to fix

## References

- [Validation Checklist](references/validation-checklist.md) - Detailed validation rules
- [API Spec](../../../spec-docs/backend_spec_03_api.md) - Source of truth

---
name: issue-detailer
description: Supplements github-issues.md with detailed implementation tasks by cross-referencing spec_v4.2.md (planning) and backend_spec_03_api.md (API). Use when you need to flesh out high-level plans into concrete development tasks.
---

# Issue Detailer

This skill specializes in bridging the gap between high-level planning documents and concrete implementation tasks. It reads the architectural plan (`github-issues.md`), enriches it with specific business logic (`spec_v4.2.md`) and technical specifications (`backend_spec_03_api.md`), and outputs detailed, actionable task descriptions.

## Source Documents

Always rely on these specific files as the "Source of Truth":

1.  **Target Plan**: `spec-docs/github-issues.md` (The file to be updated or referenced)
2.  **API Spec**: `spec-docs/backend_spec_03_api.md` (Technical details: Endpoints, JSON, Error Codes)
3.  **Planning Spec**: `spec-docs/spec_v4.2.md` (Business Logic: Rules, Flows, Constraints)

## Workflow

When asked to detail an issue or a set of issues, follow this process:

### 1. Analyze the Context
   - Identify which issue(s) from `github-issues.md` are being targeted.
   - Determine the relevant Domain/Context (e.g., Member, Pet, Walk/Thread).

### 2. Retrieve Specifications
   - **From API Spec (`backend_spec_03_api.md`)**:
     - Find the specific Endpoint(s) related to the issue.
     - Extract: HTTP Method, URL, Request Body fields (validation rules), Response fields, and specific Error Codes defined for this feature.
   - **From Planning Spec (`spec_v4.2.md`)**:
     - Find the functional requirements and business rules.
     - Extract: Constraints (e.g., "Max 10 pets"), flow logic (e.g., "Status changes to ACTIVE when..."), and permissions.

### 3. Generate Detailed Task Description
   For each issue, generate a detailed markdown section using the following template. **Do not just copy-paste the specs; synthesize them into instructions for a developer.**

   ```markdown
   ### Issue #{Number}: {Title}
   **Labels**: {Original Labels}

   #### 1. 개요 (Overview)
   - {Brief summary of what needs to be implemented}
   - **Objective**: {What functionality this enables for the user}

   #### 2. API 명세 (Technical Specs)
   - **Endpoint**: `{METHOD} {URL}`
   
   **Request Body**
   ```json
   {JSON Example from API Spec}
   ```

   **Response Body**
   ```json
   {JSON Example from API Spec}
   ```

   - **Key Fields**:
     - `{Field Name}`: {Constraints/Description}

   - **Security**: `{Auth requirements, e.g., User only, Owner only}`

   #### 3. 비즈니스 로직 & 제약조건 (Business Logic)
   - {Rule 1 from spec_v4.2.md, e.g., "User cannot have more than 1 active thread."}
   - {Rule 2 from spec_v4.2.md}
   - {Logic regarding status changes or side effects}

   #### 4. 예외 처리 (Error Handling)
   Implement the following specific error codes defined in `backend_spec_03_api.md`:
   - `{ErrorCode}`: {Condition triggering this error}
   - `{ErrorCode}`: {Condition triggering this error}

   #### 5. 구현 체크리스트 (Acceptance Criteria)
   - [ ] {Specific verifiable action, e.g., "Verify that nickname duplicates return M003"}
   - [ ] {Test case coverage requirements}
   ```

## Rules & Best Practices

1.  **Self-Contained Issues (Crucial)**: 
    - **Do NOT** use phrases like "Refer to the planning doc," "See Issue #5 for details," or "Check the API spec."
    - **Instead**, explicitly copy and paste the relevant details (Enums, logic, constraints, field descriptions) directly into the issue description.
2.  **NO Truncation in JSON**:
    - When including JSON examples for Request/Response bodies, you must provide the **FULL** JSON content from the API spec.
    - **NEVER** use `...`, `etc.`, or comments like `// ... other fields` to truncate the JSON.
    - Include every single field and value example provided in `backend_spec_03_api.md`.
3.  **Precision**: Be exact with field names and error codes. Do not guess. If a detail is missing in the spec, note it as "To Be Determined".
4.  **Language**: Write the output in **Korean** (as the source documents are in Korean), unless instructed otherwise.
5.  **Cross-Check**: Ensure the Entity fields in `github-issues.md` match the API Request/Response fields in `backend_spec_03_api.md`. If there is a discrepancy, prioritize the API Spec but highlight the difference.
6.  **Error Codes**: Always list the *specific* error codes (e.g., `T001`, `M002`) relevant to the task, not just "Handle errors".

## Example Command Handling

**User**: "Issue #17번 스레드 생성 API 상세 내용을 작성해줘."

**Action**:
1.  Read Issue #17 in `github-issues.md`.
2.  Search `backend_spec_03_api.md` for `POST /threads`.
    -   Found: Request body has `filters`, `petIds`. Error codes `T002`, `T008`...
3.  Search `spec_v4.2.md` for "스레드 생성".
    -   Found: "User can only have 1 active thread", "Max 3 hard filters".
4.  Combine into the template above.
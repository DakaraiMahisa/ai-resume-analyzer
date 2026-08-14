# 🚪 Authentication — Logout

> **Owner:** Developer 3  
> **Scope:** Refresh-token revocation  
> **Endpoint:** `POST /auth/logout`

---

## 🎯 Objective

Implement the **logout** flow.

The current authentication architecture uses short-lived JWT access tokens and server-side refresh tokens.

Therefore, logout must revoke the refresh token so it cannot be used to obtain another access token.

---

## 🧩 Logout Flow

```text
POST /auth/logout
        │
        ▼
AuthController
        │
        ▼
AuthService.logout()
        │
        ▼
Hash incoming refresh token
        │
        ▼
RefreshTokenRepository
        │
        ▼
Find refresh-token record
        │
        ├── Missing ──► reject
        ├── Revoked ──► reject / existing policy
        └── Active ───► continue
                │
                ▼
          revokedAt = now
                │
                ▼
              save
                │
                ▼
           ApiResponse
```

---

## 📁 Files You Own

Primary files:

```text
src/main/java/com/airesumeanalyzer/backend/auth/service/AuthService.java
src/main/java/com/airesumeanalyzer/backend/auth/controller/AuthController.java
```

### 🚫 Do not modify

```text
auth/security/**
auth/entity/**
auth/repository/**
common/**
```

unless explicitly requested.

---

# 1️⃣ Endpoint Contract

```http
POST /api/v1/auth/logout
Content-Type: application/json
```

### Request

```json
{
  "refreshToken": "<refresh-token>"
}
```

Use the existing `LogoutRequest`.

The controller must use:

```java
@Valid
```

---

# 2️⃣ Revocation Rules

The refresh token is stored as a **hash**.

Therefore:

```text
raw refresh token
        │
        ▼
same hashing mechanism
        │
        ▼
stored token hash
        │
        ▼
repository lookup
```

Do not search the database using the raw token.

> [!CAUTION]
> Never log the raw refresh token.

---

## ❗ Do Not Delete the Token

Logout should **not** physically delete the refresh-token record.

Instead:

```text
revokedAt = current timestamp
```

This preserves audit information.

The state becomes:

```text
ACTIVE
  │
  │ logout
  ▼
REVOKED
```

---

# 3️⃣ Access JWT Behavior

Logout does not invalidate an already-issued JWT access token under the current architecture.

The design is:

```text
Access JWT
   │
   └── short-lived → expires naturally

Refresh Token
   │
   └── server-side → can be revoked
```

Therefore your responsibility is:

> **Revoke the refresh token.**

After logout:

```text
POST /auth/refresh
using the revoked token
        ↓
MUST FAIL
```

---

# 4️⃣ Successful Response

Return:

```java
ResponseEntity<ApiResponse<Void>>
```

Example:

```text
200 OK

{
  "success": true,
  "message": "Logout successful"
}
```

Keep the API response structure consistent with the rest of the project.

---

# 🧪 Postman Test Plan

## Test 1 — Obtain refresh token

First call:

```http
POST http://localhost:8080/api/v1/auth/login
```

Save:

```text
refreshToken
```

---

## Test 2 — Logout

```http
POST http://localhost:8080/api/v1/auth/logout
```

```json
{
  "refreshToken": "<refresh-token-from-login>"
}
```

Expected:

```text
200 OK
success = true
```

---

## Test 3 — Verify database

Run:

```sql
SELECT
    id,
    user_id,
    expires_at,
    revoked_at
FROM refresh_tokens;
```

The corresponding token must have:

```text
revoked_at != NULL
```

---

## Test 4 — Reuse revoked token

Call:

```http
POST http://localhost:8080/api/v1/auth/refresh
```

```json
{
  "refreshToken": "<revoked-refresh-token>"
}
```

Expected:

```text
4xx response
```

No new access token should be issued.

---

## Test 5 — Invalid token

```json
{
  "refreshToken": "not-a-real-refresh-token"
}
```

Expected:

```text
4xx response
```

No internal database details should be exposed.

---

# 🔐 Security Checklist

- [ ] Incoming refresh token is hashed before lookup.
- [ ] Raw refresh token is never logged.
- [ ] Refresh token is not physically deleted.
- [ ] `revokedAt` is populated.
- [ ] Revoked token cannot be refreshed.
- [ ] No sensitive implementation details are exposed.

---

# ✅ Definition of Done

- [ ] Logout endpoint works.
- [ ] Correct refresh-token record is found.
- [ ] Token is revoked.
- [ ] Token is not deleted.
- [ ] Revoked token cannot be used for refresh.
- [ ] Standard `ApiResponse` is returned.
- [ ] Postman tests pass.
- [ ] No security foundation code was changed.
- [ ] Code is clean and formatted.

### Commit

```text
feat(auth): implement logout
```

> **Keep your changes focused on logout and refresh-token revocation.**

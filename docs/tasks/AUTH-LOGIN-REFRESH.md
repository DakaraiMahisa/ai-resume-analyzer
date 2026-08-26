# 🔐 Authentication — Login & Refresh Token

> **Owner:** Developer 1  
> **Scope:** Login + refresh-token rotation  
> **Endpoints:** `POST /auth/login`, `POST /auth/refresh`

---

## 🎯 Objective

Implement the **login** and **refresh-token** flows using the authentication foundation already established.

You are responsible for the **service and controller logic** only.

> [!IMPORTANT]
> **Do not redesign the security foundation.** If something appears missing or incorrect, report it rather than changing unrelated foundation code.

---

## 🧩 Existing Architecture

You must use these existing components:

| Component                   | Responsibility                            |
| --------------------------- | ----------------------------------------- |
| `AuthenticationManager`     | Starts Spring Security authentication     |
| `DaoAuthenticationProvider` | Performs username/password authentication |
| `CustomUserDetailsService`  | Loads the user                            |
| `UserRepository`            | Database access                           |
| `PasswordEncoder`           | Verifies the password                     |
| `JwtService`                | Creates and validates JWTs                |
| `RefreshTokenRepository`    | Persists refresh-token records            |
| `RefreshToken`              | Refresh-token persistence model           |
| `ApiResponse`               | Standard API response                     |
| `GlobalExceptionHandler`    | Centralized error handling                |

### Login flow

```text
POST /auth/login
       │
       ▼
AuthController
       │
       ▼
AuthService.login()
       │
       ▼
AuthenticationManager
       │
       ▼
DaoAuthenticationProvider
       │
       ├──► CustomUserDetailsService
       │          │
       │          ▼
       │     UserRepository
       │
       └──► PasswordEncoder
                 │
                 ▼
          Authentication SUCCESS
                 │
                 ▼
             JwtService
                 │
                 ├──► Access JWT
                 │
                 └──► Refresh Token
```

---

## 📁 Files You Own

Primary files:

```text
src/main/java/com/airesumeanalyzer/backend/auth/service/AuthService.java
src/main/java/com/airesumeanalyzer/backend/auth/controller/AuthController.java
```

You may add a small supporting class under `auth/` **only if genuinely necessary**.

### 🚫 Do not modify

```text
auth/security/**
auth/entity/**
auth/repository/**
common/**
```

unless the project owner explicitly asks you to.

---

# 1️⃣ Login

## Endpoint

```http
POST /api/v1/auth/login
Content-Type: application/json
```

### Request

```json
{
  "email": "user@example.com",
  "password": "Password123"
}
```

### Required flow

1. Accept the validated `LoginRequest`.
2. Authenticate through `AuthenticationManager`.
3. Do **not** manually query the user and compare passwords.
4. Obtain the authenticated user's identity.
5. Generate an access JWT using `JwtService`.
6. Generate a cryptographically secure refresh token.
7. Store **only the refresh-token hash** in the database.
8. Associate the refresh token with the authenticated user.
9. Return the token pair using `LoginResponse`.

### Security requirements

> [!CAUTION]
> Never log or persist the raw password.

> [!CAUTION]
> Never store the raw refresh token in MySQL.

> [!CAUTION]
> Never bypass `AuthenticationManager` for password authentication.

---

# 2️⃣ Refresh Token

## Endpoint

```http
POST /api/v1/auth/refresh
Content-Type: application/json
```

### Request

```json
{
  "refreshToken": "<refresh-token>"
}
```

### Required flow

```text
Raw refresh token
        │
        ▼
Hash token
        │
        ▼
RefreshTokenRepository
        │
        ▼
Find token
        │
        ├── Not found ──► reject
        ├── Revoked ────► reject
        └── Expired ────► reject
        │
        ▼
Revoke old token
        │
        ▼
Generate new access JWT
        │
        ▼
Generate new refresh token
        │
        ▼
Persist new refresh-token hash
        │
        ▼
Return new token pair
```

### 🔄 Rotation requirement

Refresh-token rotation is mandatory.

If token `A` is successfully refreshed:

```text
Token A → REVOKED
Token B → ACTIVE
```

Calling `/refresh` again with **Token A** must fail.

Calling `/refresh` with **Token B** must succeed.

---

# 🧪 Postman Test Plan

## Test 1 — Successful login

```http
POST http://localhost:8080/api/v1/auth/login
```

```json
{
  "email": "user@example.com",
  "password": "Password123"
}
```

Expected:

```text
200 OK
success = true
accessToken = present
refreshToken = present
```

Save the returned refresh token.

---

## Test 2 — Invalid password

Use the correct email and an incorrect password.

Expected:

```text
401 Unauthorized
```

No tokens should be returned.

---

## Test 3 — Successful refresh

```http
POST http://localhost:8080/api/v1/auth/refresh
```

```json
{
  "refreshToken": "<refresh-token-from-login>"
}
```

Expected:

```text
200 OK
new access token
new refresh token
```

---

## Test 4 — Verify rotation

Call `/refresh` again using the **old** refresh token.

Expected:

```text
4xx response
```

Then use the newly issued refresh token.

Expected:

```text
200 OK
```

---

## Test 5 — Invalid refresh token

Send a random token:

```json
{
  "refreshToken": "invalid-token"
}
```

Expected:

```text
4xx response
```

No token pair should be issued.

---

# ✅ Definition of Done

- [ ] Login endpoint works.
- [ ] Login uses `AuthenticationManager`.
- [ ] Invalid credentials are rejected.
- [ ] Access JWT is generated by `JwtService`.
- [ ] Refresh token is cryptographically secure.
- [ ] Only the refresh-token hash is stored.
- [ ] Refresh-token expiration is checked.
- [ ] Refresh-token revocation is checked.
- [ ] Refresh-token rotation works.
- [ ] Old refresh tokens cannot be reused.
- [ ] Postman tests pass.
- [ ] No unrelated foundation code was modified.
- [ ] Code is clean and formatted.

### Commit

```text
feat(auth): implement login and refresh token flow
```

> **Remember:** You are implementing a feature inside an existing architecture, not redesigning the architecture.

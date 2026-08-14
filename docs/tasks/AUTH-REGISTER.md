# 📝 Authentication — User Registration

> **Owner:** Developer 2  
> **Scope:** User registration  
> **Endpoint:** `POST /auth/register`

---

## 🎯 Objective

Implement the **user registration** flow using the existing authentication foundation.

You are responsible for the **service and controller logic**.

> [!IMPORTANT]
> Do not redesign the security foundation, entity model, repository contracts, or common infrastructure.

---

## 🧩 Registration Flow

```text
POST /auth/register
        │
        ▼
AuthController
        │
        ▼
AuthService.register()
        │
        ├── Validate request
        │
        ├── Normalize email
        │
        ├── Check email uniqueness
        │
        ├── Hash password
        │
        ├── Create User
        │
        └── Save User
                │
                ▼
          UserRepository
                │
                ▼
        RegisterResponse
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

unless explicitly requested by the project owner.

---

# 1️⃣ Endpoint Contract

```http
POST /api/v1/auth/register
Content-Type: application/json
```

### Request

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "Password123"
}
```

Use the existing `RegisterRequest` and `RegisterResponse`.

The controller must use:

```java
@Valid
```

---

# 2️⃣ Registration Rules

## Email

Normalize the email before persistence.

Example:

```text
John@Example.COM
        ↓
john@example.com
```

Check whether the email already exists.

The database's unique constraint remains the final protection against duplicates.

---

## Password

Use the existing:

```java
PasswordEncoder
```

Store:

```text
passwordHash = passwordEncoder.encode(password)
```

Never store:

```text
password = "Password123"
```

> [!CAUTION]
> Never log, return, or persist the raw password.

---

## Role

The registering client **must not choose the role**.

A newly registered user must receive:

```text
role = USER
```

Do not trust a client-supplied value such as:

```json
{
  "role": "ADMIN"
}
```

---

## Account Status

New users should be:

```text
enabled = true
```

according to the existing entity defaults and project contract.

---

# 3️⃣ Response

Return the existing:

```java
ResponseEntity<ApiResponse<RegisterResponse>>
```

Successful registration should return:

```text
201 Created
```

Do **not** return the complete `User` entity.

Never expose:

- `passwordHash`
- internal security information
- unnecessary database fields

---

# 4️⃣ Error Handling

### Duplicate email

Expected:

```text
409 Conflict
```

### Invalid request

Expected:

```text
400 Bad Request
```

Use the existing validation infrastructure.

Do not create another global exception handler.

---

# 🧪 Postman Test Plan

## Test 1 — Successful registration

```http
POST http://localhost:8080/api/v1/auth/register
```

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john@example.com",
  "password": "Password123"
}
```

Expected:

```text
201 Created
success = true
userId = present
email = john@example.com
```

---

## Test 2 — Verify database

Run:

```sql
SELECT
    id,
    first_name,
    last_name,
    email,
    password_hash,
    role,
    enabled
FROM users;
```

Verify:

```text
email       → normalized
password    → BCrypt hash
role        → USER
enabled     → true
```

The raw password must never appear.

---

## Test 3 — Duplicate email

Send the same registration again.

Expected:

```text
409 Conflict
```

---

## Test 4 — Invalid email

```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "not-an-email",
  "password": "Password123"
}
```

Expected:

```text
400 Bad Request
```

---

## Test 5 — Invalid required fields

```json
{
  "firstName": "",
  "lastName": "",
  "email": "",
  "password": ""
}
```

Expected:

```text
400 Bad Request
```

Validation errors should use the existing `ApiResponse` structure.

---

# 🔐 Security Checklist

- [ ] Password is hashed with the existing `PasswordEncoder`.
- [ ] Raw password never reaches the database.
- [ ] Raw password is never logged.
- [ ] Client cannot assign `ADMIN`.
- [ ] Email is normalized.
- [ ] Duplicate email is rejected.
- [ ] Safe response DTO is returned.

---

# ✅ Definition of Done

- [ ] Registration endpoint works.
- [ ] `@Valid` validation works.
- [ ] Email is normalized.
- [ ] Duplicate emails are rejected.
- [ ] Password is BCrypt-hashed.
- [ ] New users receive `USER`.
- [ ] New users are enabled.
- [ ] `RegisterResponse` is returned.
- [ ] `201 Created` is returned on success.
- [ ] Postman tests pass.
- [ ] No unrelated foundation code was changed.
- [ ] Code is clean and formatted.

### Commit

```text
feat(auth): implement user registration
```

> **Keep your changes focused on registration.**

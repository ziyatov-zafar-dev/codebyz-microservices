# CodeByZ Auth Service (from scratch)

✅ Includes:
- /api/auth sign-up + email code verify
- /api/auth sign-in + email code verify
- JWT Bearer auth for protected endpoints
- Login guard: 3 fails -> 1h, 6 fails -> 2h, after -> +1h each fail
- Max 3 active devices per user
- Device location from ipwho.is
- Logout current device + logout any device + logout all

## Run
Postgres:
- db: codebyzplatform
- user: postgres
- pass: 122333

Start:
```bash
mvn -DskipTests spring-boot:run
```

Swagger:
- http://localhost:8081/swagger-ui/index.html

### Headers you must send
For verify + logout:
- `X-Device-Id: any-unique-string`

For protected endpoints:
- `Authorization: Bearer <accessToken>`

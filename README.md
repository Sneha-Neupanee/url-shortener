# URL Shortener System 

Production-style **full-stack URL shortener** running locally with:

- **Backend**: Java 17, Spring Boot 3, Spring Web, Spring Data JPA, Spring Validation
- **DB**: PostgreSQL
- **Cache**: Redis (read-through cache for redirects)
- **Frontend**: React (Vite), TailwindCSS, Axios

## CI & Docker

- **CI**: GitHub Actions workflow at `.github/workflows/ci.yml` (backend tests/build, frontend build, and Docker image builds on pushes to `main`)
- **Docker**: `docker-compose.yml` runs **Postgres + Redis + Backend + Frontend** together

Project structure:

```
root/
├── backend/
└── frontend/
```

---

## Run with Docker (recommended)

From the repo root:

```bash
docker compose up --build
```

Services:

- **Frontend**: `http://localhost:5173`
- **Backend**: `http://localhost:8080`
- **Postgres**: `localhost:5432` (DB: `urlshortener`, user/pass: `postgres` / `postgres`)
- **Redis**: `localhost:6379`

Stop:

```bash
docker compose down
```

Reset DB volume (deletes local container DB data):

```bash
docker compose down -v
```

---

## Prerequisites

- Java 17
- Maven
- Node.js + npm
- PostgreSQL running locally (if not using Docker)
- Redis running locally (if not using Docker)

If you need quick local starters (varies by distro):

```bash
# Redis
redis-server

# PostgreSQL (Ubuntu/Debian)
sudo service postgresql start
```

---

## Database setup (PostgreSQL)

The backend is configured (by default) for:

- DB: `urlshortener`
- user: `postgres`
- password: `postgres`

Create the DB:

```bash
psql -U postgres -c "CREATE DATABASE urlshortener;"
```

If your Postgres username/password differ, update:

- `backend/src/main/resources/application.yml`

---

## Redis setup

Make sure Redis is running on:

- host: `localhost`
- port: `6379`

---

## Run the backend

```bash
cd backend
mvn spring-boot:run
```

Backend runs at:

- `http://localhost:8080`

---

## Run the frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend runs at:

- `http://localhost:5173`

---

## How it works (quick)

### Create short URL

`POST /api/shorten`

Request:

```json
{
  "longUrl": "https://example.com/some/long/url",
  "customAlias": "optional-alias"
}
```

Response:

```json
{
  "shortUrl": "http://localhost:8080/abc1234"
}
```

### Redirect

Open the returned `shortUrl` in a browser:

- `GET /{shortCode}` → **302 redirect**

Redirect flow:

1. Check **Redis** for `shortCode -> originalUrl`
2. If hit: redirect immediately
3. If miss: query **PostgreSQL**
4. If found: cache in Redis and redirect
5. If not found: return **404**

On every redirect the backend increments `clickCount` in PostgreSQL.

### Stats

`GET /api/stats/{shortCode}`

Response:

```json
{
  "originalUrl": "https://example.com/some/long/url",
  "clickCount": 3,
  "createdAt": "2026-05-12T09:00:00.000Z"
}
```

---

## Error behaviors (required)

- **400**: invalid input (bad URL, alias format/length, missing longUrl)
- **404**: short code not found
- **409**: custom alias already taken

409 body:

```json
{ "error": "Alias already taken" }
```


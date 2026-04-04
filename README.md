# DevMate

개발팀을 위한 서버 모니터링 및 업무 알림 플랫폼입니다.
서버 헬스체크, PR 방치 알림, 배포 현황, 일일 리포트를 슬랙과 이메일로 자동 발송합니다.

---

## 주요 기능

**서버 헬스체크**
등록된 서버 URL을 1분마다 체크하여 응답 실패 시 슬랙과 이메일로 즉시 알림을 발송합니다.

**PR 방치 알림**
GitHub API를 통해 N시간 이상 리뷰가 없는 PR을 감지하고 매일 오전 9시에 슬랙으로 알림을 발송합니다.

**배포 현황**
GitHub Webhook으로 main 브랜치 push 이벤트를 수신하여 배포 이력을 저장하고 슬랙으로 알림을 발송합니다.

**일일 리포트**
매일 오전 9시에 열린 PR, 어제 머지된 PR, 헬스체크 실패 건수를 슬랙과 이메일로 발송합니다.

**모니터링 대시보드**
서버 상태, 최근 배포 이력, 알림 발송 현황을 REST API로 제공합니다.

---

## 기술 스택

| 구분 | 기술 |
|---|---|
| Backend | Spring Boot 4.0.3, Spring Batch, Spring Scheduler |
| Database | MySQL 8.0, Redis 7 |
| 알림 | [notification-engine](https://github.com/Ha-minsang/notification-engine) (SSE, Email, Slack) |
| 외부 연동 | GitHub API, GitHub Webhook |
| 인프라 | Docker, Docker Compose |

---

## 아키텍처

```
GitHub API / Webhook
        |
Spring Scheduler / Batch
        |
    이벤트 감지
        |
notification-engine
    ├── SSE   → 대시보드 실시간 업데이트
    ├── Email → 담당자 이메일
    └── Slack → 팀 채널 알림
```

---

## 시작하기

### 요구 사항

- Java 17
- Docker, Docker Compose

### 환경변수 설정

루트 디렉토리에 `.env` 파일을 생성합니다.

```
# MySQL
MYSQL_ROOT_PASSWORD=루트비밀번호
MYSQL_DATABASE=devmate
MYSQL_USER=devmate
MYSQL_PASSWORD=비밀번호

# GitHub
GITHUB_TOKEN=깃허브토큰
GITHUB_OWNER=깃허브유저명
GITHUB_REPO=레포지토리명

# Slack
SLACK_WEBHOOK_URL=슬랙웹훅URL

# Mail
MAIL_USERNAME=Gmail주소
MAIL_PASSWORD=앱비밀번호
```

### 실행

```bash
# Docker로 MySQL, Redis 실행
docker-compose up -d

# 앱 실행
./gradlew bootRun
```

---

## API 명세

앱 실행 후 아래 URL에서 Swagger UI로 확인할 수 있습니다.

```
http://localhost:8080/swagger-ui/index.html
```

### 주요 엔드포인트

| Method | URL | 설명 |
|---|---|---|
| GET | /api/dashboard | 대시보드 전체 현황 조회 |
| GET | /api/servers | 헬스체크 서버 목록 조회 |
| POST | /api/servers | 헬스체크 서버 등록 |
| PATCH | /api/servers/{id}/toggle | 서버 활성화/비활성화 |
| GET | /api/pr-alerts | PR 알림 설정 목록 조회 |
| POST | /api/pr-alerts | PR 알림 설정 등록 |
| PATCH | /api/pr-alerts/{id}/toggle | PR 알림 활성화/비활성화 |
| DELETE | /api/pr-alerts/{id} | PR 알림 설정 삭제 |
| GET | /api/deploys | 전체 배포 이력 조회 |
| GET | /api/deploys/{repoName} | 레포별 배포 이력 조회 |
| POST | /webhook/github | GitHub Webhook 수신 |
| GET | /api/sse/connect/{targetId} | SSE 연결 |

---

## GitHub Webhook 설정

1. GitHub 레포 → **Settings** → **Webhooks** → **Add webhook**
2. Payload URL: `http://서버주소/webhook/github`
3. Content type: `application/json`
4. Events: **Just the push event** 선택

---

## 연계 프로젝트

알림 발송은 직접 개발한 [notification-engine](https://github.com/Ha-minsang/notification-engine) 라이브러리를 사용합니다.
SSE, Email, Slack 채널을 단일 인터페이스로 통합하여 사용할 수 있습니다.
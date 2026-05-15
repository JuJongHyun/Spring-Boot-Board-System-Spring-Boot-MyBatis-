# Community Board Platform

Spring Boot + MyBatis 기반 커뮤니티 게시판 웹 애플리케이션입니다.

---

## 기술 스택

- **Backend**: Java 21, Spring Boot 3.5
- **Security**: Spring Security
- **Template**: Thymeleaf
- **Frontend**: JavaScript, jQuery
- **Database**: MySQL
- **ORM**: MyBatis
- **Build**: Gradle
- **Other**: Lombok, AOP, Swagger, SSE

---

## 주요 기능

- 회원가입 / 로그인 / 프로필 수정 / 회원 탈퇴
- 게시글 CRUD, 키워드 검색, 페이지네이션
- 댓글 및 대댓글
- 파일 업로드 / 다운로드 (날짜 기반 디렉토리, UUID 파일명)
- SSE 기반 실시간 댓글 알림
- 관리자 대시보드 (회원 / 게시글 / 댓글 통계)

---

## 실행 방법

`application-local.properties`에 DB 정보 입력 후 실행

```bash
./gradlew bootRun
```

- 접속: `http://localhost:8080`
- API 문서: `http://localhost:8080/swagger-ui.html`

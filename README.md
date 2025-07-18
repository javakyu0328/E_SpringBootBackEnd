
#   IntegratedWebProject 프로젝트

##  프로젝트 소개
Vue 3 및 React 프론트엔드와 연동되는 **Spring Boot 기반 백엔드** 애플리케이션입니다.  
MySQL을 기반으로 회원 관리, 영화 정보 제공, 영화 추천 등의 기능을 제공합니다.  
RESTful API 형식으로 데이터를 주고받으며, 프론트 통합 프로젝트에서 **인증 및 데이터 처리의 중심 역할**을 담당합니다.


---

##  버전  
1.0V
---

##  사용 기술

|          |                                     |
|----------|-----------------------------------------|
| **백엔드 프레임워크**        | Spring Boot          |
| **데이터베이스**       | 	MySQL                   |
| **Object-Relational Mapping**  |JPA                  |
| **빌드 도구**        | Maven                         |

---

##  주요 기능

### 회원 기능

- 회원가입 처리 (POST /api/member/signup)<br>
사용자 입력 데이터 검증 및 DB 저장 처리

- 인증 처리 (POST /api/member/login)<br>
세션 기반 로그인 로직 구현

- 세션 상태 확인 (GET /api/member/check-session)<br>
로그인 여부 유지 및 확인 기능

- 회원 정보 조회 및 수정 (GET/PUT /api/member/{id})<br>
인증 사용자 대상 데이터 조회 및 수정 로직

- 회원 탈퇴 처리 (DELETE /api/member/{id})<br>
사용자 본인 인증 후 DB에서 회원 정보 삭제 처리

### 영화 도메인 (Movie Domain)
- 영화 목록 조회 API (GET /api/movies)<br>
전체 영화 리스트 제공

- 영화 상세 정보 제공 (GET /api/movies/{id})<br>
특정 영화 ID 기준 상세 정보 제공

- 영화 추천 기능 (GET /api/movies/recommend)<br>
사용자 성향 기반 추천 알고리즘 연동

### 인증/보안 도메인 (Security Domain)
- 세션 기반 인증 발급 및 검증
로그인 상태 유지를 위한 인증 로직

- 인증 필터 / 인터셉터 구성
비인가 요청 차단 처리

### 공통 처리
- 전역 예외 처리기 (@ControllerAdvice)
예외 상황에 대한 공통 응답 처리

- CORS 설정
Vue3 / React 프론트엔드 요청 허용 설정

---

## API 연동 예시

| HTTP Method | URL | 설명 |
|-------------|-----|------|
| `POST` | `/api/member/login` | 로그인 |
| `POST` | `/api/member/signup` | 회원가입 |
| `GET`  | `/api/movies` | 전체 영화 목록 조회 |
| `GET`  | `/api/movies/{id}` | 영화 상세 정보 조회 |

---

## 프론트엔드 연동

본 백엔드는 다음 프론트엔드 프로젝트와 연동

- Vue3 프로젝트: [C_VUEProject](https://github.com/javakyu0328/C_VUEProject)
- React 프로젝트: [D_REACTProject](https://github.com/javakyu0328/D_REACTProject)

---
## 접속
-  localhost:8083에서 실행됨 (CORS 설정 시 백엔드에서 허용)

- 프로젝트 통합을 통해 클라이언트-서버 간 실제 통신 기반 구조를 구현

---


## 개발자 정보  
이름: 최정규  
이메일: javakyu4030@naver.com


---


## 수정 및 추가 사항

---
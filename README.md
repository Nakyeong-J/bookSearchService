# Book Search 
#### made by nk.jeon
Spring Boot를 이용한 도서 검색API

# jar 실행 방법
- 경로 : https://drive.google.com/drive/folders/1GKkYdVWaPH8-LuLT7cmS4uBoc8ovy5NF?usp=sharing
- 실행방법
    - bookSearchService.jar 다운로드 
    - 다운로드 위치에서 java -jar bookSearchService.jar 실행
    - localhost:8080/ or localhost:8080/login 으로 접속  

# 기능
1. 로그인/회원가입
    - JWT Token 인증 방식
2. 책 검색
    - Kakao OpenApi, Naver Open API 사용
    - 기본적으로 Kakao OpenApi를 이용하여 검색하나, Kakao OpenAPI 장애 발생 시, Naver OpenAPI를 사용하여 검색한다.
3. 내 검색 히스토리 조회
    - 로그인 한 유저가 검색한 이력을 최근순으로 제공
4. 인기 키워드 목록 조회
    - 최상위 인기 키워드 10개까지 제공

# 오픈소스 및 라이브러리 사용 목록
- spring-boot-starter-webflux
    - 웹플럭스 기반 웹 서비스 구현 
    - 비동기 논블로킹으로 적은 자원으로 트래픽 처리에 효율적
- spring-boot-starter-security
    - Spring 기반 어플리케이션 인증 보안 기능 제공
- spring-boot-starter-data-jpa
    - Spring에서 JPA를 추상화 하여 사용할수 있도록 제공하는 모듈
    - 추상화된 Repository 인터페이스 기반으로 사용
- jjwt
    - JWT 토큰 생성 및 검증을 위한 Library
- lombok
    - Getter, Setter, Construct, Builder 등을 어노테이션으로 쉽게 구현할수 있음
    - 컴파일 시점에 AnnotationProcessor를 이용하여 실제 코드를 생성
- bootstrap
    - css화면 개발을 위해 사용
- spring-boot-starter-thymeleaf
    - 스프링 부트에서 html과 함께 사용하는 템플릿 엔진


#구현 기능 설명

### 1. 회원 가입

#### API Path
POST /join

#### Request Body
|     키    |  타입  |     설명    | 필수여부 | 기본값 |
|:---------:|:------:|:-----------:|:--------:|:------:|
|   userId  | String |  사용자 ID  |   true   |        |
|  password | String |   비밀번호  |   true   |        |
|  userName | String |     이름    |   true  |        |

#### Response
생략

### 로그인

#### API Path
POST /login

#### Request Body
|    키    |  타입  |       설명      | 필수여부 | 기본값 |
|:--------:|:------:|:---------------:|:--------:|:------:|
|  userId  | String |    사용자 ID    |   true   |        |
| password | String | 사용자 비밀번호 |   true   |        |

#### Response
생략

### 책 검색

#### API Path
GET /searchBook

#### Request Header
|       키      |  타입  |  설명   | 필수여부 | 기본값 |
|:-------------:|:------:|:----------------------------:|:--------:|:------:|
| Authorization | String | 인증 토큰 |   true   |        |

#### Request Parameter
|        키        |   타입   |        설명       | 필수여부 | 기본값 |
|:----------------:|:-------:|:----------------:|:--------:|:------:|
|  searchKeyword   | String  |     검색 키워드    |   true   |        |
| searchPageNumber | Integer | 검색할 페이지 번호  |   false  |    1   |
|     historyYn    | Boolean | 검색히스토리저장여부 |   false  | false  |

#### Response
생략

### 검색 히스토리 조회

#### API Path
GET /getHistoryByUser

#### Request Header
|       키      |  타입  |  설명   | 필수여부 | 기본값 |
|:-------------:|:------:|:----------------------------:|:--------:|:------:|
| Authorization | String | 인증 토큰 |   true   |        |

#### Response
생략

### 인기 키워드 목록 조회

#### API Path
GET /getTopRankKeyword

#### Request Header
|       키      |  타입  |  설명   | 필수여부 | 기본값 |
|:-------------:|:------:|:----------------------------:|:--------:|:------:|
| Authorization | String | 인증 토큰 |   true   |        |

#### Response
생략

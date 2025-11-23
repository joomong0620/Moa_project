# SpringBoot - Project - MoA Stage
스프링 부트 - 공연 전시 통합 플랫폼

----

## 📂 프로젝트 소개
MOA Stage는 공연과 전시 정보를 한눈에 보고, 예매부터 리뷰까지 한 번에 관리할 수 있는 통합 문화 예매 웹 플랫폼입니다. 사용자는 공연과 전시를 쉽게 찾을 수 있고 예매·결제·리뷰까지 모든 과정을 하나의 서비스 내에서 경험할 수 있으며,
마이페이지, 알림, 채팅 기능을 통해 개인화된 소통과 관리가 가능합니다.

## 🕰️ 개발 기간
* 25.09.20일 ~ 25.10.20일


### 👩‍💻 멤버구성
- 김주연 : 자유게시판(CRUD) 구현, WebSocket을 활용한 실시간 채팅/알람 기능 구현, 마이페이지 구현
- 김소연 : 리뷰게시판(CRUD) 구현, 결제 기능 구현, 메인 화면 구현
- 김승주 : 공연 API 연동, 지도 API 구현, 기대평(CRUD) 게시판 구현
- 박율영 : 전시 API 연동, 전시게시판(CRUD) 구현
- 정태웅 : 로그인 기능 구현, 회원가입 기능 구현


### ⚙️개발 환경
- 'Java: 17 (OpenJDK)'
- 'JDK: 11.0.17_8'
- **IDE** : STS 3.9
- **Framework**: Spring Boot 3.5.5
- **Database**: Oracle DB
- **ORM**: MyBatis

## 📌 주요 기능
#### 자유게시판 - [상세 보기 - WIKI 이동](https://github.com/joomong0620/Moa_project/wiki/%EC%A3%BC%EC%9A%94-%EA%B8%B0%EB%8A%A5-%EC%86%8C%EA%B0%9C(%EC%9E%90%EC%9C%A0%EA%B2%8C%EC%8B%9C%ED%8C%90))
- 게시글 목록 조회
- 게시글 작성, 수정, 삭제, 조회 (CRUD)
- 게시글 상세 페이지 내 좋아요, 댓글 작성 기능 지원


#### 알림 - [상세 보기 - WIKI 이동](https://github.com/joomong0620/Moa_project/wiki/%EC%A3%BC%EC%9A%94-%EA%B8%B0%EB%8A%A5-%EC%86%8C%EA%B0%9C(%EC%95%8C%EB%A6%BC))
- 좋아요·댓글 발생 시 실시간 알림 전송
- 클릭 시 관련 게시글로 바로 이동
- 읽음 여부 시각화 및 알림 개별 삭제 기능

  
#### 마이페이지 - [상세 보기 - WIKI 이동](https://github.com/joomong0620/Moa_project/wiki/%EC%A3%BC%EC%9A%94-%EA%B8%B0%EB%8A%A5-%EC%86%8C%EA%B0%9C(%EB%A7%88%EC%9D%B4%ED%8E%98%EC%9D%B4%EC%A7%80))
- 프로필 이미지 및 닉네임 변경
- 주소 API(Daum Postcode) 연동 후 DB 저장
- 예매 내역 조회 및 페이지 이동
- 예매 취소 및 상태 변경
- 예매 내역 기반 리뷰 작성

#### 채팅 - [상세 보기 - WIKI 이동](https://github.com/joomong0620/Moa_project/wiki/%EC%A3%BC%EC%9A%94-%EA%B8%B0%EB%8A%A5-%EC%86%8C%EA%B0%9C(%EC%B1%84%ED%8C%85))
- 개인 및 팀 단위 채팅방 생성 (닉네임 검색)
- 메시지 입력·전송 및 읽음 알림 표시
- 팀 채팅 시 다수 인원 그룹 대화 지원
- 채팅방 나가기, 메시지 기록 조회 기능
- 사용자 프로필 이미지와 발신자 구분 UI 구현

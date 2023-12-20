### ReCode-BE 프로젝트 소개 
Recode 서비스는 온라인 스터디 매칭 웹 서비스로 Recode = Let’s coding + Record의 합성어로 같이 코딩하며 공부한 내용을 기록하자는 의미를 담았습니다.

본인이 공부하고 싶은 공부 주제(기술 스택)에 맞는 스터디 그룹에 가입 할 수 있게 도와주는 것이 주요 목적입니다.

#### 🕰️ 개발 기간
- 2023.10.16 ~ 2023.12.20

#### ERD

![RECODE_ERD](https://github.com/HeoJungBaekKang/ReCode-BE/assets/137294567/dd8133ac-d7a1-4ce5-983e-7933a9ac3ab6)


#### 👨‍💻 멤버 구성 
- 팀장_백승주 : 프로젝트 전체 PM, Frontend PM 
- 팀원_강민희 : 문서, 일정 정리, Fronted PM
- 팀원_김훈호 : DB PM
- 팀원_정성현 : (프로젝트 진행 중 취업) 
- 팀원_한다현 :  CI/CD PM, Backend PM
- 팀원_허 찬 : Backend PM

#### 개발 환경 
- OS
    - Windows, macOS
    - Ubuntu 22.04 : AWS EC2 인스턴스 OS
    - iOS,Android : 알람 기능
- Server
    - Tomcat :  Springboot 기반 Backend API 서버
    - Nginx : React 기반 Frontend API 서버
    - Netty : Springboot Reactive Web 기반 채팅 API 서버
- Framework : SpringBoot, React
- IDE
    - IntelliJ : Backend 개발
    - Visual Studio Code : Frontend 개발
    - HeidiSQL : 최종 배포판 DB 관리
    - h2 Database : [Localhost](http://Localhost) 환경에서 DB 테스트
    - MongoDBCompass : 채팅 데이터 확인
- DB Tool
    - MariaDB : 프로젝트 전체 DB
    - MongoDB : 채팅 메세지 저장을 위한 DB
- 개발 언어
    - JavaSE 11 : Backend 개발 언어
    - JavaSE 21 : 채팅 기능 개발 언어
- 부수적인 언어
    - JavaScript : React 개발 언어
    - Python3 : AWS Lambda 개발 언어

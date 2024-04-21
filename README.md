# PROJECT NAME : Recode




## 📚 PROJECT DESCRIPTION

### ReCode-BE 프로젝트 소개 
Recode 서비스는 온라인 스터디 매칭 웹 서비스로 Recode = Let’s coding + Record의 합성어로 같이 코딩하며 공부한 내용을 기록하자는 의미를 담았습니다.

본인이 공부하고 싶은 공부 주제(기술 스택)에 맞는 스터디 그룹에 가입 할 수 있게 도와주는 것이 주요 목적입니다.

#### 기존 프로젝트와의 차별점
- 기존 온라인 스터디 매칭 서비스는 Hola(https://holaworld.io/)가 있습니다.
- 홀라와 Recode는 온라인 공간에서 상호 관심사(공부하고 싶은 주제)에 맞는 사람을 매칭해주는 기능은 동일합니다.
- 홀라에서는 스터디 중개 과정에서 타 플랫폼(카카오톡 오픈 채팅, 이메일 등등)을 이용하는데에 그치는 반면 Recode 서비스에서는 스터디 룸 내에서 채팅, 자료공유 게시판, 출석체크 기능을  제공합니다.

#### 🕰️ 개발 기간
- 2023.10.16 ~ 2023.12.20

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

#### WBS
![Untitled](https://github.com/HeoJungBaekKang/ReCode-BE/assets/137294567/daaeabc3-a014-4442-b377-67c8d5378b0d)

#### 주요 기능 명세서

##### 1. System Architecture
<br>

![Untitled (1)](https://github.com/HeoJungBaekKang/ReCode-BE/assets/137294567/42e3d902-fd81-4dd1-8332-a5982fa052be)

##### 2. ERD
<br>

![RECODE_ERD](https://github.com/HeoJungBaekKang/ReCode-BE/assets/137294567/dd8133ac-d7a1-4ce5-983e-7933a9ac3ab6)

##### 3. Data Flow Chart
<br>
   
![Architecture](https://github.com/bearkuang/ReCode-BE/assets/137677440/9520497e-4ff2-408a-8326-98f2cfdcadc8)

---

#### 주요 기능 설명
<br>
<details>
<summary><b>1. 메인 페이지</b></summary>
    <div markdown="1">
        <br>
            <p align="center">
                <img src="https://github.com/bearkuang/ReCode-BE/assets/137677440/77a6be5c-2c9c-4f10-8552-e73fa09fbd86" alt="RECODE_Main">
            </p>
        <br>
- 모집 중인 스터디 그룹 목록을 띄워줌. 각 컴포넌트에는 스터디 이름, 기술 스택, 만든 사람 등의 정보를 한 번에 표시하여 스터디 그룹을 찾는 사람이 쉽게 원하는 스터디 그룹을 찾을 수       있게함.
        <br>
            <p align="center">
                <img src="https://github.com/bearkuang/ReCode-BE/assets/137677440/320d7cf9-f796-4e35-b8f3-07bf12703c8b" alt="STACK">
            </p>
        <br>
- 본인이 공부하고 싶은 기술 스택을 지정하면 그 조건에 맞는 스터디 그룹만 나타나도록 필터링함.
        <br>
    </div>
</details>
<br>

<details>
<summary><b>2. 스터디 그룹 생성</b></summary>
    <div markdown="2">
        <br>
            <p align="center">
                <img src="https://github.com/bearkuang/ReCode-BE/assets/137677440/4f4373e2-f33c-446e-b3c1-81eb9d89b43b" alt="CREATE_ROOM">
            </p>
        <br>
- 스터디 그룹 생성 페이지에서는 스터디 이름, 출석 요일, 기술 스택 등 StudyRoom 테이블에 요구되는 각종 정보를 기입하고 생성함.
        <br>
    </div>
</details>
<br>

<details>
<summary><b>3. 채팅</b></summary>
    <div markdown="3">
        <br>
            <p align="center">
                <img src="https://github.com/bearkuang/ReCode-BE/assets/137677440/8a55b14d-be6c-4ee4-b37d-b4cafa69750b" alt="RECODE_CHAT">
            </p>
            <br>
- WebFlux을 적용하여 SSE 방식으로 MongoDB에 저장된 메세지를 실시간으로 가져오는 방식을 적용함.
            <br>
    </div>
</details>
<br> 

<details>
<summary><b>4. 마이 페이지</b></summary>
    <div markdown="4">
        <br>
            <p align="center">
                <img src="https://github.com/bearkuang/ReCode-BE/assets/137677440/d05f5124-07e0-468d-84e4-2a899cf03c82" alt="RECODE_MYPAGE">
        </p>
        <br>
- 스터디 그룹 생성 페이지에서는 스터디 이름, 출석 요일, 기술 스택 등 StudyRoom 테이블에 요구되는 각종 정보를 기입하고 생성함.
        <br>
            <p align="center">
                <img src="https://github.com/bearkuang/ReCode-BE/assets/137677440/4dfa2978-d283-4332-8256-903a88f19f16" alt="RECODE_EMAIL">
            </p>
        <br>
- 개인정보 수정 시 비밀번호 변경을 할 경우 가입 시 입력한 이메일로 비밀번호 링크가 전송되며 URL에 EmailCheckToken이 전달되어 사용자를 식별함.
        <br>
    </div>
</details>
<br> 

<details>
<summary><b>5. 스터디 룸</b></summary>
    <div markdown="5">
        <br>
**1. 메인페이지**
        <br>
            <p align="center">
                <img src="https://github.com/bearkuang/ReCode-BE/assets/137677440/a82f6a84-68e6-449b-99e3-b613e9a46bc3" alt="RECODE_ROOM">
            </p>
        <br>
- 스터디룸 메인 페이지에서는 스터디 관련 정보를 볼 수 있으며 탈퇴 버튼을 통해 스터디 탈퇴가 가능함.
        <br>
**2. 게시판**
        <br>
            <p align="center">
                <img src="https://github.com/bearkuang/ReCode-BE/assets/137677440/a64d122a-1752-40ad-9706-713be0b2d0b0" alt="RECODE_BOARD_MAIN">
            </p>
        <br>
- 스터디룸 사이드바의 전체 글 보기를 클릭하여 게시판 이동 가능함.
- 스터디룸의 마스터의 경우 공지사항, 자료공유, 회고록을 작성할 수 있으며 마스터가 아닌 멤버의 경우 공지사항을 제외한 카테코리에 대해 작성 가능함.
        <br>
            <p align="center">
                <img src="https://github.com/bearkuang/ReCode-BE/assets/137677440/cc74637b-1ee2-4735-b460-748d2b3a0642" alt="BOARD_UPLOAD">
            </p>
        <br>
- 게시글에서는 글과 댓글을 통해 소통이 가능하고 자료공유 또한 가능함.
- 업로드된 파일은 AWS S3에 저장되고 파일명이 겹치지 않도록 파일명 앞에 UUID가 추가됨.
         <br>
            <p align="center">
                <img src="https://github.com/bearkuang/ReCode-BE/assets/137677440/9f7e9475-bf99-470b-8177-ce0f174dfffd" alt="QUIZ">
        </p>
        <br>
- 스터디 구성원의 공부 방향과 실력을 가늠할 수 있는 퀴즈 게시판이 존재함.
- 구글 폼을 이용하기 위한 OAuth 2.0 사용함.
        <br>
**3. 출석체크**
        <br>
            <p align="center">
                <img src="https://github.com/bearkuang/ReCode-BE/assets/137677440/e40202cb-976e-456d-8c98-90b738649580" alt="Attendence">
            </p>
        <br>
- 스터디 활동의 지속을 위한 출석체크 기능이 존재함.
- 현재 시간과 스터디 생성 시 설정된 출석 체크 시간, 요일과 비교하여 조건 충족 시 출석이 인정됨.
        <br>
**4. 스터디 멤버 평가와 뱃지**
        <br>
            <p align="center">
                <img src="https://github.com/bearkuang/ReCode-BE/assets/137677440/fb473a51-7138-4cb3-9499-1a302f5fad68" alt="POINT">
            </p>
        <br>
- 스터디 생성 시 설정한 스터디 목표의 마지막날이 되면 사이드바에 스터디 참가 인원 평가 탭이 깜빡이며 추가됨.
- 각 인원별 평가를 통해 해당 인원에게 점수가 부여되며 점수에 따라 뱃지가 부여됨. (뱃지는 스터디 마스터가 참가 신청 인원을 승인할 때 해당 인원을 승인할 지에 대한 기준이됨)
        <br>
    </div>
</details>
<br> 

<details>
<summary><b>6. QnA</b></summary>
    <div markdown="6">
        <br>
            <p align="center">
                <img src="https://github.com/bearkuang/ReCode-BE/assets/137677440/98dc31c5-d161-49f2-8d52-cde251d44e29" alt="QNA">
            </p>
        <br>
- QnA 게시판에 글이 올라오게 되면 스터디 웹 사이트의 관리자의 Slack 으로 Qna 글이 등록되었음을 알리는 알림이 전송됨.
- QnA 의 글, 댓글의 대한 수정 및 삭제는 해당 글의 작성자만이 가능함
        <br>
    </div>
</details>
<br>

<details>
<summary><b>7. 관리자</b></summary>
    <div markdown="7">
        <br>
            <p align="center">
                <img src="https://github.com/bearkuang/ReCode-BE/assets/137677440/0a7fbaab-67eb-4bf2-8596-828c15147403" alt="ADMIN_MANAGE">
        </p>
        <br>
- 스터디의 스터디 마스터의 연락이 끊겼을 경우 사용자에 요청에 대한 응답으로 해당 관리자는 스터디 마스터 권한을 다른 스터디원에게 양도 가능함.
        <br>
            <p align="center">
                <img src="https://github.com/bearkuang/ReCode-BE/assets/137677440/97201f49-2fc1-4f14-a641-20b31b24a8f3" alt="ADD_STACK">
        </p>
        <br>
- 신기술 등장하거나 사용자가 기술 스택 추가를 요청할 경우 기술 스택 추가 가능함.
        <br>
    </div>
</details>
<br>



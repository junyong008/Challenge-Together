<p align='center'>
    <img src="https://capsule-render.vercel.app/api?type=venom&color=0:F78F52,100:F3B238&height=300&section=header&text=CHALLENGE%20TOGETHER&fontSize=50&fontColor=ffffff&animation=fadeIn&fontAlignY=50"/>
    <img alt="AndroidStudio Version" src="https://img.shields.io/badge/Android_Studio-Ladybug_2024.2.1-E53935?style=flat">  
    <img alt="Kotlin Version" src="https://img.shields.io/badge/Kotlin-2.0.20-purple?style=flat">
    <img alt="Java Version" src="https://img.shields.io/badge/Java-17-blue?style=flat">
    <img alt="minSdk Version" src="https://img.shields.io/badge/minSdk-24-green?style=flat">
    <img alt="targetSdk Version" src="https://img.shields.io/badge/targetSdk-34-green?style=flat">
</p>
<br>

## 개요
* 1인 프로젝트 [기획, 디자인, 프론트엔드, 백엔드]
* 플레이스토어 12개 언어 출시 [[🇰🇷](https://play.google.com/store/apps/details?id=com.yjy.challengetogether&hl=ko) [🇺🇸](https://play.google.com/store/apps/details?id=com.yjy.challengetogether&hl=en) [🇯🇵](https://play.google.com/store/apps/details?id=com.yjy.challengetogether&hl=ja) [🇨🇳](https://play.google.com/store/apps/details?id=com.yjy.challengetogether&hl=zh) [🇮🇳](https://play.google.com/store/apps/details?id=com.yjy.challengetogether&hl=hi) [🇪🇸](https://play.google.com/store/apps/details?id=com.yjy.challengetogether&hl=es) [🇩🇪](https://play.google.com/store/apps/details?id=com.yjy.challengetogether&hl=de) [🇻🇳](https://play.google.com/store/apps/details?id=com.yjy.challengetogether&hl=vi) [🇫🇷](https://play.google.com/store/apps/details?id=com.yjy.challengetogether&hl=fr) [🇮🇹](https://play.google.com/store/apps/details?id=com.yjy.challengetogether&hl=it) [🇹🇭](https://play.google.com/store/apps/details?id=com.yjy.challengetogether&hl=th) [🇵🇹](https://play.google.com/store/apps/details?id=com.yjy.challengetogether&hl=pt)]
* 나쁜 습관, 중독들로부터 벗어나기 위한 다양한 기능을 제공하는 안드로이드 어플리케이션
<br>

## 기능 구현
* SNS 간편 로그인, 계정 연동, 비회원 로그인, 이메일 인증번호를 통한 비밀번호 찾기 구현
* 서드 파티 라이브러리 없이 그래프, 캘린더 등의 커스텀뷰 구현
* Redis Queue를 통한 Firebase Cloud Messaging 처리
* 백엔드 채팅 웹소켓 구현 및 callbackFlow를 활용한 웹소켓 연결 핸들링
* OkHttp 인터셉터를 통한 세션 관리 및 글로벌 시간 동기화 처리
* 로컬 캐싱 기반의 홈 위젯 제공 및 위젯 투명도 커스터마이징 옵션 구현
* Google Play Billing과 AdMob을 활용한 수익화 모델 구현 및 Cloud Pub/Sub 기반 환불 시스템 구축
* AppsFlyer를 활용한 앱 내 OneLink 및 딥링크 공유 시스템 구현
* Github Action, Firebase CLI를 활용한 CI/CD 구축
<br>

## 모듈화
> 본 프로젝트는 [Modularization Guide](https://developer.android.com/topic/modularization?hl=ko)에 따라 모듈화되었습니다.

![module_graph](https://github.com/user-attachments/assets/67449566-85ea-4b43-9ecf-6970709fcca0)
<br>
<br>

## 아키텍처
> 본 프로젝트는 [Google Recommend Architecture](https://developer.android.com/topic/architecture?hl=ko)를 따르고 있습니다.

![architecture](https://github.com/user-attachments/assets/27104295-2ebb-4bca-a3eb-e1b3423f0115)
<br>
<br>

## 구현
![graphic](https://github.com/user-attachments/assets/3acf4387-6490-498d-8521-78f2feac05c5)
![preview](https://github.com/user-attachments/assets/7431dea9-6c95-4d13-80cb-5123e62bb5a6)
![screenshot1](https://github.com/user-attachments/assets/3e17a494-488b-4635-b934-52697b50a4c2)
![screenshot2](https://github.com/user-attachments/assets/f55801e9-6a90-42ae-9f66-948440bf8cc8)
![screenshot3](https://github.com/user-attachments/assets/b8a569af-a6bd-44fe-9999-18f5807a2581)
![screenshot4](https://github.com/user-attachments/assets/fb6bc89e-1ef9-45dc-baae-a5e1de3fa926)
<br>
<br>

## 구현 - 과거(리팩토링 이전)
> 리팩토링 이전 과거 Java 코드로 개발하여 초기 서비스한 버전.
> `legacy/old-version` Branche에 저장.

![graphic](https://user-images.githubusercontent.com/69251013/234477690-a1666914-c619-486b-a5e3-db0b296285ee.png)
![7](https://user-images.githubusercontent.com/69251013/234552152-3f4f2cab-948e-4c62-9a35-8f933b529437.png)
![1](https://user-images.githubusercontent.com/69251013/234531462-08144a73-a0db-4672-9676-510c2c67dad6.png)
![2](https://user-images.githubusercontent.com/69251013/234531737-6c0f21eb-7567-48e4-89e1-bb541b3fccd4.png)
![3](https://user-images.githubusercontent.com/69251013/234531411-4d899913-367d-4168-a6f0-1bfb1c468aa0.png)
![4](https://user-images.githubusercontent.com/69251013/234546007-63e37ed7-8807-4c77-b509-3dd9c0004c5f.png)
![5](https://user-images.githubusercontent.com/69251013/234546016-201ae809-c40e-4d30-b564-3df16fcf9b0e.png)
![6](https://user-images.githubusercontent.com/69251013/234546023-3b3d6c90-42f1-4723-9932-b95af41f8373.png)

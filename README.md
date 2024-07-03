# Tutoring Hub

---


## Tutoring Hub란?

과외를 뜻하는 **tutoring**과 여러 기능을 합쳐 놓은 중심지 역할을 한다는 점에서 **Hub** 단어를 합친 것으로 과외를 진행하는 사람들을 위한 **과외 학생 관리 어플**입니다.

---

## Team

### 윤우성

[SPWooSeong - Overview](https://github.com/SPWooSeong)

### 장세일

[seilj - Overview](https://github.com/seilj)

---

## Task Stack

**Front-end** : Kotlin

**IDE** : Android Studio

---

## About
![1](https://github.com/seilj/madcamp_week1/assets/134405041/791a6aeb-f11b-45fa-ad03-03cbf054b57c)![2](https://github.com/seilj/madcamp_week1/assets/134405041/ff1a196a-c5fd-4a3a-9c67-84536506676f)



`splash` 탭 - 앱의 제목인 Tutoring Hub를 앱의 테마 색상과 함께 출력하였습니다.

학생 목록, 필기 노트, 일정 관리 3가지 탭으로 구성된 어플입니다.     

                

**학생 목록 탭**
![3](https://github.com/seilj/madcamp_week1/assets/134405041/54b5e56b-661d-4226-bea0-f97d9dac657d)![4](https://github.com/seilj/madcamp_week1/assets/134405041/013147af-79ad-4e98-aeeb-008521020171)![5](https://github.com/seilj/madcamp_week1/assets/134405041/c2389986-0909-4c40-8570-ac26321a1541)
- 학생 목록 확인 : 학생의 이름, 나이, 학교, 전화번호, 배우는 과목, 시급, 요일, 시간/횟수를 확인할 수 있습니다.  `ViewModel`에서 통해 불러온 `PeopleData.json`이라는 핸드폰 내부 파일을 통해 찾은 학생의 Data를 `RecyclerView` 로 보여줍니다.
- 학생 수정/삭제 : 학생 목록에서 지우고 싶은 학생을 꾹 누르는 것으로 학생을 지울 수도 학생의 정보를 수정할 수 있습니다. `AlertDialog` 기능을 통해 수정 사항과 삭제 확인을 표현하였습니다
- 학생 추가 : 우측 하단의 연필 버튼을 클릭하여 학생의 이름, 나이, 학교, 전화번호, 배우는 과목, 시급, 강의 요일, 1회 강의 시간을 입력함으로 학생 목록에 학생을 추가할 수 있습니다.
- 휴대전화 내부에 저장되어 있어, 앱을 껐다가 켜도 변경 사항이 저장됩니다.

**필기 노트**
![6](https://github.com/seilj/madcamp_week1/assets/134405041/d5473a1c-f935-4d02-83be-7ea4488e9728)![7](https://github.com/seilj/madcamp_week1/assets/134405041/272f4536-c6c8-41d8-8208-6db2adc86d8d)![8](https://github.com/seilj/madcamp_week1/assets/134405041/84a4bc49-dcbd-45a5-892f-f1f1b65d8c1c)
- 사진 불러오기 : 내 헨드폰 갤러리에서 사진을 불러와, 제목을 지정해주고 저장을 할 수 있습니다. `GridView`를 통해 불러온 사진들을 보여줍니다.
- 사진 확대 : 사진을 클릭하면 확대 된 사진을 볼 수 있습니다.
- 사진 촬영 : 갤러리에 있는 사진을 불러오는 것과 더불어 사진을 직접 찍어 추가할 수 있습니다.
- 사진 삭제 : 지우고 싶은 사진을 꾹 누르는 것으로 사진을 지울 수 있습니다.
- 휴대전화 내부에 저장되어 있어, 앱을 껐다가 켜도 변경 사항이 저장됩니다.

**일정 관리**
![9](https://github.com/seilj/madcamp_week1/assets/134405041/6968ae4d-ba74-4379-bbd7-eb083c30bbee)![10](https://github.com/seilj/madcamp_week1/assets/134405041/0a32fd4d-03ce-4597-a70d-f3226aaf1b20)
- 휴/보강 관리 : 학생 목록에서 입력한 정보에 따라 달력 API로 생성한 캘린더에 과외 계획이 생깁니다. 원래 있던 강의를 휴강할 수도, 보강을 할 수도 있습니다. (휴/보강 계확은 `ViewModel`의 `SchduelsData.json`으로 저장이됩니다.)
- 과외 월급 계산기 : 캘린더에서 조정한 과외 계획에 따라 과외 비용을 계산할 수 있습니다.
- 학생 목록 탭에서 변경 사항이 있을 때마다 바로 반영이 됩니다.

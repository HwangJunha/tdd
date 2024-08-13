
<img src="https://capsule-render.vercel.app/api?type=wave&color=auto&height=300&section=header&text=Test-Driven%20Development&fontSize=60" />

![spring](https://img.shields.io/badge/Spring-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![mariadb](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white)
![redis](https://img.shields.io/badge/redis-%23DD0031.svg?&style=for-the-badge&logo=redis&logoColor=white)
![hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)
![gradle](https://img.shields.io/badge/Gradle-02303A.svg?style=for-the-badge&logo=Gradle&logoColor=white)
![swagger](https://img.shields.io/badge/-Swagger-%23Clojure?style=for-the-badge&logo=swagger&logoColor=white)
![java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)




<h1>코드리뷰 규칙</h1>
  <ul>
    <h2>중요도 규칙</h2>
    <li>P1: 필수 반영</li>
    <li>P2: 고려 사항</li>
    <li>P3: 의견</li>
  </ul>
  <ul>
    <h2>리뷰어 규칙</h2>
    <li>리뷰어는 총 2명을 지정할 수 있으면 메인과 서브로 나누어 진다.</li>
    <li>메인으로 지정된 리뷰어는 P1~P3중 하나를 무조건 남겨야 한다.</li>
    <li>서브로 지정된 리뷰어는 의견을 남겨도 되고 단순 확인했다라는 댓글을 남기면 된다.</li>
    <li>리뷰 요청자는 PR을 요청할때 메인으로 지정된 리뷰어를 연속으로 지정할 수 없으면 PR 한 번당 돌아가면서 1명씩 지정한다.</li>
    <li>주제가 TDD인 만큼 기능을 중점적으로 보는게 아니라 테스트 코드를 중점적으로 리뷰를 진행해야 한다.</li>
    <li>리뷰어는 정해진 날짜까지 리뷰를 반드시 완료를 해야 한다.</li>
  </ul>
  <ul>
    <h2>코드 작성 규칙</h2>
    <li>주석을 작성하되 간략하게 1줄로만 적는다.</li>
  </ul>
</ul>

<h1>issue 작성</h1>
<ul>
    <li>단위 기능 구현전 issue를 생성해서 작업 진행</li>
    <li>코드 작성 중 요구사항 기능 명세서를 변경해야 할 상황이 온다면 issue를 작성해서 요구사항이 어떻게 변경되었는지 남겨야 한다</li>
</ul>
<details>
<summary>
  issue 작성 방법
</summary>
   <br>
<h2>New Issue 클릭</h2>
  
![createissue1](https://github.com/user-attachments/assets/17f56672-e5c6-42ba-9e32-58bc28f6b7e7)

<h2>title, Labels 입력</h2>

![createissue2](https://github.com/user-attachments/assets/6dcc7a46-a6b4-4efe-8c50-b4c83ef9dccd)

<h2>create a branch 입력</h2>

![createissue3](https://github.com/user-attachments/assets/9394286c-d4b6-4f9b-bd82-eca1b7aea59e)

<h2>issue-number + "feature"로 변경</h2>

![createissue4](https://github.com/user-attachments/assets/117d1df3-21fb-4f6b-ab86-e7f5cd650782)

</details>

<h1>PR 규칙</h1>
<ul>
    <li>요구사항 기능 명세서의 단위 기능으로 PR을 요청한다</li>
    <li>코드 리뷰는 올린시점에서 최대 5일안에 진행하여야 한다.</li>
  </ul>

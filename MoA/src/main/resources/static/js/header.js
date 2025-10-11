console.log("header.js loaded");

//SSE 연결하는 함수
// -> 연결을 요청한 클라이언트가 서버로부터 데이터가 전달될 때까지 대기 상태 (비동기)
const connectSse = () => {
  

  //로그인이 되어있지 않은 경우 함수 종료
  if (notificationLoginCheck == false) return;

  console.log("connectSse() 호출");

  // 서버의 "/sse/connect" 주소로 연결 요청
  const eventSource = new EventSource("/sse/connect");

  //-------------------------------------------------

  // 서버로부터 메세지가 왔을 경우(전달 받은 경우)
  eventSource.addEventListener("message", (e) => {
    // console.log(e.data); //e.data== 전달받은 메세지
    // -> Spring HttpMessageConvertor가
    // JSON으로 변환해서 응답해줌

    const obj = JSON.parse(e.data);
    console.log(obj); //알림 받는 회원 번호, 읽지 않은 알림 개수

    // 알림 개수 표시
    const notificationContentArea = document.querySelector(
      ".notification-count-area"
    );
    notificationContentArea.innerText = obj.notiCount;

    // 종 버튼에 색 추가(활성화)
    const notificationBtn = document.getElementById("my-element");
    notificationBtn.classList.remove("fa-regular");
    notificationBtn.classList.add("fa-solid");

    //만약 알림 목록이 열려있는 경우
    const notificationList = document.querySelector(".notification-list");
    if(notificationList.classList.contains("notification-show")){
      selectNotificationList(); //알림 목록 비동기 조회
    }
  });
  //서버 연결이 종료된 경우(타임아웃)

  eventSource.addEventListener("error", () => {
    console.log("SSE 재연결 시도");

    eventSource.close(); // 기존 연결 닫기

    // 5초 후 재연결 시도 -> 비동기 통신이기 때문에 기존 연결이 제대로 안 닫힌 채로 연결할 수 있으니
    setTimeout(() => connectSse(), 5000);
  });
};


const sendNotification = (type, url, pkNo, content) => {
  //로그인이 되어있지 않은 경우 함수 종료
  if (notificationLoginCheck == false) return;
// type: 댓글, 답글, 게시글 좋아요  등 구분하는 값
  // url : 알림 클릭 시 이동할 페이지 주소
  // pkNo : 알림 받는 회원 번호 또는 회원 번호를 찾을 수 있는 값
  // content : 알림 내용
  // 서버로 제출할 데이터를 JS 객체 형태로 저장

  //  자기 자신에게는 알림 전송하지 않음
  if (pkNo == loginMemberNo) {
    console.log("자기 자신에게는 알림이 전송되지 않습니다.");
    return;
  }
  const notification = {
    notificationType: type,
    notificationUrl: url,
    pkNo: pkNo,
    notificationContent: content,
  };

  fetch("/sse/send", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(notification),
  })
    .then((response) => {
      if (!response.ok) {
        //비동기 통신 실패
        throw new Error("알림 전송 실패");
      }
      console.log("알림 전송 성공");
    })
    //.then 생략, 전달 받는 값이 업어서
    .catch((err) => console.log(err));
};




//비동기로 알림 목록 조회하는 함수
const selectNotificationList = () => {
  //로그인이 안 된 경우 함수 종료
  if (notificationLoginCheck == false) return;

  fetch("/notification")
    .then((resp) => {
      if (resp.ok) return resp.json();
      throw new Error("알림 목록 조회 실패");
      //성공하면 두번째 then으로 실패하면 catch문으로
    })
    .then((selectList) => {
      {
        //console.log(selectList);

        // 이전 알림 목록 삭제
        const notiList = document.querySelector(".notification-list");
        notiList.innerHTML = "";

        for (let data of selectList) {
          // 알림 전체를 감싸는 요소
          const notiItem = document.createElement("li");
          notiItem.className = "notification-item";

          // 알림을 읽지 않은 경우 'not-read' 추가
          if (data.notificationCheck == "N") notiItem.classList.add("not-read");

          // 알림 관련 내용(프로필 이미지 + 시간 + 내용)
          const notiText = document.createElement("div");
          notiText.className = "notification-text";

          // 알림 클릭 시 동작
          notiText.addEventListener("click", (e) => {
            // 만약 읽지 않은 알람인 경우
            if (data.notificationCheck == "N") {
              fetch("/notification", {
                method: "PUT",
                headers: { "Content-Type": "application/json" },
                body: data.notificationNo,
              });
              //1) 로그인 또는 알림 삭제 시 + 읽지 않은 알림 개수 조회 종아이콘 색상 변경
              //2) 알림 삭제
              //3) 댓글, 답글, 게시글 좋아요 -> 알림 전달
              // 컨트롤러 메서드 반환값이 없으므로 then 작성 X
            }

            // 클릭 시 알림에 기록된 경로로 이동
            location.href = data.notificationUrl; // #어느 주소로 이동해야하는지
          });

          // 알림 보낸 회원 프로필 이미지
          const senderProfile = document.createElement("img");
          if (data.sendMemberProfileImg == null)
            senderProfile.src = notificationDefaultImage; // 기본 이미지
          else senderProfile.src = data.sendMemberProfileImg; // 프로필 이미지

          // 알림 내용 영역
          const contentContainer = document.createElement("div");
          contentContainer.className = "notification-content-container";

          // 알림 보내진 시간
          const notiDate = document.createElement("p");
          notiDate.className = "notification-date";
          notiDate.innerText = data.notificationDate;

          // 알림 내용
          const notiContent = document.createElement("p");
          notiContent.className = "notification-content";
          notiContent.innerHTML = data.notificationContent; // 태그가 해석 될 수 있도록 innerHTML

          // 삭제 버튼
          const notiDelete = document.createElement("span");
          notiDelete.className = "notification-delete";
          notiDelete.innerHTML = "&times;";

          /* 삭제 버튼 클릭 시 비동기로 해당 알림 지움 */
          notiDelete.addEventListener("click", (e) => {
            fetch("/notification", {
              method: "DELETE",
              headers: { "Content-Type": "application/json" },
              body: data.notificationNo,
            })
              .then((resp) => {
                if (resp.ok) {
                  // 클릭된 x버튼이 포함된 알림 삭제
                  notiDelete.parentElement.remove();
                  notReadCheck();
                  return;
                }

                throw new Error("네트워크 응답이 좋지 않습니다.");
              })
              .catch((err) => console.error(err));
          });

          // 조립
          notiList.append(notiItem);
          notiItem.append(notiText, notiDelete);
          notiText.append(senderProfile, contentContainer);
          contentContainer.append(notiDate, notiContent);
        }
      }
    })
    .catch((err) => console.log(err));
};

//읽지 않은 알림 개수 조회 및 알림 유무 표시 여부 변경
const notReadCheck = () => {
  //로그인 되어있지 않으면 리턴해서 종료
  if (!notificationLoginCheck) return;

  fetch("/notification/notReadCheck")
    .then((response) => {
      if (response.ok) return response.text();
      throw new Error("알림 개수 조회 실패");
    })
    .then((count) => {
      // console.log(count);

      const notificationContentArea = document.querySelector(
        ".notification-count-area"
      );

      //알람개수 화면에 표시
      notificationContentArea.innerText = count;
      const notificationBtn = document.getElementById("my-element");

      if (count != 0) {
        // 읽지 않은 알림 수가 존재한다면 노란색 불 들어오게 하기
        notificationBtn.classList.add("fa-solid");
        notificationBtn.classList.remove("fa-regular");
      } else {
        notificationBtn.classList.remove("fa-solid");
        notificationBtn.classList.add("fa-regular");
      }
    })
    .catch((err) => console.log(err));
};

// 페이지 로딩 완료 후 수행
document.addEventListener("DOMContentLoaded", () => {
  connectSse();

  notReadCheck();

  //종 버튼(알림) 클릭 시 알림 목록 출력하기
  const notificationBtn = document.querySelector(".notification-btn");

  notificationBtn?.addEventListener("click", () => {
    //알림 목록
    const notificationList = document.querySelector(".notification-list");

    //알림 목록이 보이고 있을 경우
    if (notificationList.classList.contains("notification-show")) {
      // 안보이게 하기
      notificationList.classList.remove("notification-show");
    } else {
      // 안보이는 경우
      selectNotificationList(); // 비동기로 알림 목록 조회하는 함수 호출

      // 화면에 목록 보이기
      notificationList.classList.add("notification-show");
    }
  });
})
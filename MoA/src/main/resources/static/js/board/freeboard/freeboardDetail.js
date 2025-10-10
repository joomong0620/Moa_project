console.log("boardDetail.js loaded");

const boardLike = document.getElementById("boardLike");
// 좋아요 버튼이 클릭 되었을 때
boardLike.addEventListener("click", (e) => {
  // 로그인 X
  if (!loginMemberNo || loginMemberNo === "") {
    alert("로그인 후 이용해주세요.");
    return;
  }


  let check; // 기존에 좋아요 X(빈하트) : 0, 기존에 좋아요 O(꽉찬하트) : 1

  // contains("클래스명") : 클래스가 있으면 true, 없으면 false
  if (e.target.classList.contains("fa-regular")) {
    // 좋아요 X(빈하트)
    check = 0;
  } else {
    // 좋아요 O(꽉찬하트)
    check = 1;
  }

  // ajax로 서버에 제출할 파라미터를 모아둔 JS 객체
  const data = { memberNo: loginMemberNo, boardNo: boardNo, check: check };

  // ajax 비동기 통신
  fetch("/board/like", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  })
    .then((resp) => resp.text()) // 응답 객체를 필요한 형태로 파싱하여 리턴
    .then((count) => {
      // 파싱된 데이터를 받아서 처리하는 코드 작성
      console.log("count : " + count);
      console.log(JSON.stringify(data));

      // INSERT,DELETE 실패 시
      if (count == -1) {
        alert("좋아요 처리 실패ㅠㅠ");
        return;
      }

      // toggle() : 클래스가 있으면 없애고, 없으면 추가
      e.target.classList.toggle("fa-regular");
      e.target.classList.toggle("fa-solid");

      // 현재 게시글의 좋아요 수를 화면에 출력
      e.target.nextElementSibling.innerText = count;


    })
    .catch((err) => { console.log(err);
    }); // 예외 발생 시 처리할 코드
});

//-------------------------------------
// 게시글 수정

// 게시글 버튼 수정 클릭 시
document.getElementById("updateBtn")?.addEventListener("click", () => {
  location.href =
    location.pathname.replace("board", "board2") + "/update" + location.search;
  // /board2/1/1500/update?cp=1 (get) 요청 보내기
});
// ------------------------------------

// 게시글 삭제 버튼이 클릭 되었을 때
document.getElementById("deleteBtn")?.addEventListener("click", () => {
  if (confirm("정말 삭제하시겠습니까?")) {
    // /board2/1/1500/delete (GET)
    location.href = location.pathname.replace("board", "board2") + "/delete";
  }
});

// ----------------------------------------------------------------
// 목록으로
const goToListBtn = document.getElementById("goToListBtn");
goToListBtn.addEventListener("click", () => {
  // 게시판 검색인 경우 : /board/1
  // 통합 검색인 경우   : /board/search

  // URL 내장 객체 : 주소 관련 정보를 나타내는 객체
  // URL.searchParams : 쿼리스트링만 별도 객체로 반환

  const params = new URL(location.href).searchParams;

  let url;
  if (params.get("key") == "all") {
    url = "/board/search";
  } else {
    url = "/board/" + boardCode;
  }

  location.href = url + location.search;
  // location.search : 쿼리스트링 반환
});
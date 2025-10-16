console.log("comment.js");

console.log("loginMemberNo:", loginMemberNo);
console.log("boardNo:", boardNo);

// 댓글 목록 조회
function selectCommentList() {
  fetch("/comment?boardNo=" + boardNo)
    .then((resp) => resp.json())
    .then((cList) => {
      console.log(cList);

      const commentList = document.getElementById("commentList");
      commentList.innerHTML = "";

      for (let comment of cList) {
        const commentRow = document.createElement("li");
        commentRow.classList.add("comment-row");

        // 작성자
        const commentWriter = document.createElement("p");
        commentWriter.classList.add("comment-writer");

        // 프로필 이미지
        const profileImage = document.createElement("img");
        if (comment.profileImage != null) {
          profileImage.setAttribute("src", comment.profileImage);
        } else {
          profileImage.setAttribute("src", "/images/board/freeboard/user.png");
        }

        // 작성자 닉네임
        const memberNickname = document.createElement("span");
        memberNickname.innerText = comment.memberNickname;

        // 작성일
        const commentDate = document.createElement("span");
        commentDate.classList.add("comment-date");
        commentDate.innerText = "(" + comment.commentCreateDate + ")";

        commentWriter.append(profileImage, memberNickname, commentDate);

        // 댓글 내용
        const commentContent = document.createElement("p");
        commentContent.classList.add("comment-content");
        commentContent.innerHTML = comment.commentContent;

        commentRow.append(commentWriter, commentContent);

        // 로그인한 회원번호와 댓글 작성자의 회원번호가 같을 때만 버튼 추가
        if (loginMemberNo && loginMemberNo == comment.memberNo) {
          const commentBtnArea = document.createElement("div");
          commentBtnArea.classList.add("comment-btn-area");

          // 수정 버튼
          const updateBtn = document.createElement("button");
          updateBtn.innerText = "수정";
          updateBtn.setAttribute(
            "onclick",
            "showUpdateComment(" + comment.commentNo + ", this)"
          );

          // 삭제 버튼
          const deleteBtn = document.createElement("button");
          deleteBtn.innerText = "삭제";
          deleteBtn.setAttribute(
            "onclick",
            "deleteComment(" + comment.commentNo + ")"
          );

          commentBtnArea.append(updateBtn, deleteBtn);
          commentRow.append(commentBtnArea);
        }

        commentList.append(commentRow);
      }
    })
    .catch((err) => console.log(err));
}

// 댓글 등록
const addComment = document.getElementById("addComment");
const commentContent = document.getElementById("commentContent");

addComment.addEventListener("click", (e) => {
  console.log("댓글 등록 버튼 클릭");
  console.log("현재 loginMemberNo:", loginMemberNo);
  console.log("loginMemberNo 타입:", typeof loginMemberNo);

  // 로그인 체크
  if (!loginMemberNo || loginMemberNo == 0) {
    alert("로그인 후 이용해주세요.");
    return;
  }

  // 댓글 내용 체크
  if (commentContent.value.trim().length == 0) {
    alert("댓글을 작성한 후 버튼을 클릭해주세요.");
    commentContent.value = "";
    commentContent.focus();
    return;
  }

  // 비동기 요청을 통한 댓글 등록
  const data = {
    commentContent: commentContent.value,
    memberNo: loginMemberNo,
    boardNo: boardNo,
  };

  fetch("/comment", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  })
    .then((resp) => resp.text())
    .then((commentNo) => {
      if (commentNo > 0) {
        alert("댓글이 등록되었습니다.");
        console.log(commentNo);
        
        commentContent.value = "";

        selectCommentList(); // 알림 요청
        sendNotification(
            "insertComment",
            location.pathname + "?cn=" + commentNo, 
            boardNo,
             `<strong>${memberNickname}</strong>님이 <strong>${boardTitle}</strong> 게시글에 댓글을 작성했습니다.`

        );
      } else {
        alert("댓글 등록에 실패했습니다...");
      }
    })
    .catch((err) => console.log(err));
});

// 댓글 삭제
function deleteComment(commentNo) {
  if (confirm("정말로 삭제 하시겠습니까?")) {
    const data = { commentNo: commentNo };

    fetch("/comment", {
      method: "DELETE",
      headers: { "Content-type": "application/json" },
      body: JSON.stringify(data),
    })
      .then((resp) => resp.text())
      .then((result) => {
        if (result > 0) {
          alert("삭제되었습니다");
          selectCommentList();
        } else {
          alert("삭제 실패");
        }
      })
      .catch((err) => console.log(err));
  }
}

// 댓글 수정
let beforeCommentRow;

function showUpdateComment(commentNo, btn) {
  const temp = document.getElementsByClassName("update-textarea");

  if (temp.length > 0) {
    if (confirm("다른 댓글이 수정 중입니다. 현재 댓글을 수정 하시겠습니까?")) {
      temp[0].parentElement.innerHTML = beforeCommentRow;
    } else {
      return;
    }
  }

  const commentRow = btn.parentElement.parentElement;
  beforeCommentRow = commentRow.innerHTML;

  let beforeContent = commentRow.children[1].innerHTML;

  commentRow.innerHTML = "";

  const textarea = document.createElement("textarea");
  textarea.classList.add("update-textarea");

  // XSS 방지 처리 해제
  beforeContent = beforeContent.replaceAll("&amp;", "&");
  beforeContent = beforeContent.replaceAll("&lt;", "<");
  beforeContent = beforeContent.replaceAll("&gt;", ">");
  beforeContent = beforeContent.replaceAll("&quot;", '"');

  textarea.value = beforeContent;
  commentRow.append(textarea);

  const commentBtnArea = document.createElement("div");
  commentBtnArea.classList.add("comment-btn-area");

  const updateBtn = document.createElement("button");
  updateBtn.innerText = "수정";
  updateBtn.setAttribute("onclick", "updateComment(" + commentNo + ", this)");

  const cancelBtn = document.createElement("button");
  cancelBtn.innerText = "취소";
  cancelBtn.setAttribute("onclick", "updateCancel(this)");

  commentBtnArea.append(updateBtn, cancelBtn);
  commentRow.append(commentBtnArea);
}

// 댓글 수정 취소
function updateCancel(btn) {
  if (confirm("댓글 수정을 취소하시겠습니까?")) {
    btn.parentElement.parentElement.innerHTML = beforeCommentRow;
  }
}

// 댓글 수정
function updateComment(commentNo, btn) {
  const commentContent = btn.parentElement.previousElementSibling.value;

  fetch("/comment", {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      commentNo: commentNo,
      commentContent: commentContent,
    }),
  })
    .then((resp) => resp.text())
    .then((result) => {
      if (result > 0) {
        alert("댓글이 수정되었습니다.");
        selectCommentList();
      } else {
        alert("댓글 수정 실패");
      }
    })
    .catch((err) => console.log(err));
}

document.addEventListener("DOMContentLoaded", () => {
  selectCommentList();
});
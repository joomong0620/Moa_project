console.log("reviewDetail.js loaded");

document.addEventListener("DOMContentLoaded", () => {
  const reviewNo = document.body.dataset.boardNo;
  const commentList = document.getElementById("commentList");
  const commentInput = document.getElementById("commentInput");
  const loginMemberNo = document.body.dataset.loginMemberNo;
  const submitBtn = document.querySelector(".submit-btn");

  loadComments();

  // 댓글 등록
  submitBtn.addEventListener("click", () => {
    const text = commentInput.value.trim();
    if (!text) return alert("댓글을 입력해주세요.");

    fetch("/reviewboard/comment", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        boardNo: reviewNo,
        commentContent: text,
      }),
    })
      .then((res) => res.json())
      .then((result) => {
        if (result > 0) {
          commentInput.value = "";
          loadComments(); // 새로고침 없이 반영
        } else {
          alert("댓글 등록 실패");
        }
      })
      .catch((err) => console.error(err));
  });

  // 댓글 목록
  function loadComments() {
    fetch(`/reviewboard/comment?boardNo=${reviewNo}`)
      .then((res) => res.json())
      .then((list) => {
        commentList.innerHTML = "";

        if (list.length === 0) {
          commentList.innerHTML = `<p class="no-comment">등록된 댓글이 없습니다.</p>`;
          return;
        }

        list.forEach((comment) => {
          const row = document.createElement("div");
          row.classList.add("comment-item");

          // 작성자
          const img = document.createElement("img");
          img.src = comment.profileImg || "/images/default-user.png";
          img.classList.add("comment-img");

          const box = document.createElement("div");
          box.classList.add("comment-box");

          const user = document.createElement("p");
          user.classList.add("comment-user");
          user.innerText = comment.memberNickname;

          const text = document.createElement("p");
          text.classList.add("comment-text");
          text.innerText = comment.commentContent;

          box.append(user, text);
          row.append(img, box);

          // 로그인 사용자 == 작성자일 때 버튼 표시
          if (loginMemberNo && Number(loginMemberNo) === comment.memberNo) {
            
            const btnArea = document.createElement("div");
            btnArea.classList.add("comment-btn-area");

            const updateBtn = document.createElement("button");
            updateBtn.innerText = "수정";
            updateBtn.onclick = () => showUpdateForm(comment, row);

            const deleteBtn = document.createElement("button");
            deleteBtn.innerText = "삭제";
            deleteBtn.onclick = () => deleteComment(comment.commentNo);

            btnArea.append(updateBtn, deleteBtn);
            row.append(btnArea);
          }          

          commentList.append(row);
        });
      })
      .catch((err) => console.error(err));
  }

  // 댓글 수정
  function showUpdateForm(comment, row) {
    const originalHTML = row.innerHTML;
    row.innerHTML = "";

    const textarea = document.createElement("textarea");
    textarea.value = comment.commentContent;
    textarea.classList.add("update-textarea");

    const btnArea = document.createElement("div");
    btnArea.classList.add("comment-btn-area");

    const saveBtn = document.createElement("button");
    saveBtn.innerText = "저장";
    saveBtn.onclick = () => updateComment(comment.commentNo, textarea.value);

    const cancelBtn = document.createElement("button");
    cancelBtn.innerText = "취소";
    cancelBtn.onclick = () => (row.innerHTML = originalHTML);

    btnArea.append(saveBtn, cancelBtn);
    row.append(textarea, btnArea);
  }

  // 댓글 수정
  function updateComment(commentNo, newText) {
    if (newText.trim().length === 0) return alert("내용을 입력하세요.");

    fetch("/reviewboard/comment", {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ commentNo, commentContent: newText }),
    })
      .then((res) => res.text())
      .then((result) => {
        if (result > 0) loadComments();
        else alert("댓글 수정 실패");
      })
      .catch((err) => console.error(err));
  }

  // 댓글 삭제
  function deleteComment(commentNo) {
    if (!confirm("정말 삭제하시겠습니까?")) return;

    fetch("/reviewboard/comment", {
      method: "DELETE",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ commentNo }),
    })
      .then((res) => res.text())
      .then((result) => {
        if (result > 0) {
          alert("삭제되었습니다.");
          loadComments();
        } else alert("삭제 실패");
      })
      .catch((err) => console.error(err));
  }
});

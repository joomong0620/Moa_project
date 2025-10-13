document.addEventListener("DOMContentLoaded", () => {
  const params = new URLSearchParams(location.search);
  const reviewNo = params.get("reviewNo");
  loadReviewDetail(reviewNo);
  loadComments(reviewNo);

  document.querySelector(".submit-btn").addEventListener("click", () => {
    const text = document.getElementById("commentInput").value.trim();
    if (!text) return alert("댓글을 입력해주세요.");

    fetch("/review/comment", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ reviewNo, commentContent: text }),
    })
      .then((res) => res.json())
      .then((result) => {
        if (result > 0) {
          loadComments(reviewNo);
          document.getElementById("commentInput").value = "";
        }
      });
  });
});

function loadReviewDetail(no) {
  fetch(`/review/detail?reviewNo=${no}`)
    .then((res) => res.json())
    .then((data) => {
      document.getElementById("reviewTitle").textContent = data.boardTitle;
      document.getElementById("reviewContent").textContent = data.boardContent;
      document.getElementById("username").textContent =
        data.memberNickname + " 님";
      document.getElementById("reviewDate").textContent = data.createDate;
      document.getElementById("reviewStars").textContent = "★".repeat(
        data.star || 0
      );
      document.getElementById("viewCount").textContent = data.boardCount;
    });
}

function loadComments(no) {
  fetch(`/review/comment/list?reviewNo=${no}`)
    .then((res) => res.json())
    .then((list) => {
      const container = document.getElementById("commentList");
      container.innerHTML = "";
      list.forEach((c) => {
        const div = document.createElement("div");
        div.className = "comment-item";
        div.innerHTML = `
          <img src="/images/user.svg" alt="프로필" class="comment-img">
          <div class="comment-box">
            <p class="comment-user">${c.memberNickname}</p>
            <p class="comment-text">${c.commentContent}</p>
          </div>`;
        container.appendChild(div);
      });
    });
}

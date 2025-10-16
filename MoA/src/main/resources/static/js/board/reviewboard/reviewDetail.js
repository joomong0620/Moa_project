document.addEventListener("DOMContentLoaded", () => {
  const reviewNo = document.body.dataset.boardNo;
  loadReviewDetail(reviewNo);
});

// 리뷰 상세
function loadReviewDetail(no) {
  fetch(`/reviewboard/2/${reviewNo}`)
    .then((res) => res.json())
    .then((data) => {
      if (!data) return;
      document.getElementById("reviewTitle").textContent = data.boardTitle;
      document.getElementById("reviewContent").textContent = data.boardContent;
      document.getElementById("username").textContent =
        data.memberNickname + " 님";
      document.getElementById("reviewDate").textContent = data.createDate;
      document.getElementById("reviewStars").textContent = "★".repeat(
        data.star || 0
      );
      document.getElementById("viewCount").textContent = data.boardCount;
    })
    .catch((err) => console.error(err));
}

console.log("boardList.js loaded");
// 글쓰기 버튼 클릭 시 

// Optional Chaining(옵셔널 체이닝) : ?.
// ?. '앞'의 대상이 null 이거나 undefined면 평가를 멈추고 undefined를 반환 
// -> 에러 발생 X 
// ?. 은 존재하지 않아도 괜찮은 대상에 사용
document.getElementById("free-write")?.addEventListener("click", ()=>{
    // JS BOM 객체 중 location

    // location.href = '주소' : 해당 주소로 요청(GET 방식)

    location.href = `/board2/free/${location.pathname.split("/")[2]}/insert`;
                    // /board2/2/insert

                    console.log(location.pathname.split("/"));
});


// ==================== 이미지 모달 기능 ====================
(function () {
  const modal = document.getElementById("imgModal");
  const modalImg = document.getElementById("modalImg");
  const caption = document.getElementById("caption");
  const closeBtn = document.getElementsByClassName("close")[0];

  // 카드 내 썸네일 이미지 모두 선택
  const thumbnails = document.querySelectorAll(".card img");

  thumbnails.forEach((img) => {
    img.addEventListener("click", function () {
      modal.style.display = "block";
      modalImg.src = this.src;
      caption.innerText = this.alt || "이미지 미리보기";
    });
  });

  // 닫기 버튼
  closeBtn.onclick = function () {
    modal.style.display = "none";
  };

  // 모달 외부 클릭 시 닫기
  modal.onclick = function (e) {
    if (e.target === modal) {
      modal.style.display = "none";
    }
  };
})();

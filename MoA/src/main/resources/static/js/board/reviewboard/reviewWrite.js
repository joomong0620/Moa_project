document.addEventListener("DOMContentLoaded", () => {
  /** 별점 선택 **/
  const stars = document.querySelectorAll(".star");
  const starValueInput = document.getElementById("starValue");

  stars.forEach((star) => {
    star.addEventListener("click", () => {
      const value = parseInt(star.dataset.value);
      starValueInput.value = value;

      // 선택된 별을 모두 ★ 로 변경
      stars.forEach((s, i) => {
        s.textContent = i < value ? "★" : "☆";
      });
    });

    // 호버 시 미리보기 효과
    star.addEventListener("mouseover", () => {
      const value = parseInt(star.dataset.value);
      stars.forEach((s, i) => {
        s.textContent = i < value ? "★" : "☆";
      });
    });

    // 마우스가 벗어나면 원래 선택한 값으로 복원
    star.addEventListener("mouseleave", () => {
      const value = parseInt(starValueInput.value);
      stars.forEach((s, i) => {
        s.textContent = i < value ? "★" : "☆";
      });
    });
  });

  /** 글자 수 카운트 **/
  const textarea = document.getElementById("contentInput");
  const charCount = document.getElementById("charCount");

  textarea.addEventListener("input", () => {
    const length = textarea.value.length;
    charCount.textContent = length;
    if (length > 500) {
      textarea.value = textarea.value.substring(0, 500);
      charCount.textContent = 500;
    }
  });

  /** 이미지 미리보기 **/
  const photoInput = document.getElementById("photoInput");
  const photoPreview = document.getElementById("photoPreview");

  photoInput.addEventListener("change", (e) => {
    const files = Array.from(e.target.files);
    photoPreview.innerHTML = ""; // 기존 미리보기 초기화

    if (files.length > 5) {
      alert("사진은 최대 5장까지만 등록 가능합니다.");
      photoInput.value = "";
      return;
    }

    files.forEach((file) => {
      if (!file.type.startsWith("image/")) return;

      const reader = new FileReader();
      reader.onload = (event) => {
        const img = document.createElement("img");
        img.src = event.target.result;
        img.alt = "사진 미리보기";
        img.classList.add("preview-image");
        photoPreview.appendChild(img);
      };
      reader.readAsDataURL(file);
    });
  });

  /** 유효성 검사 **/
  const form = document.querySelector("form");
  form.addEventListener("submit", (e) => {
    const title = document.getElementById("titleInput").value.trim();
    const content = textarea.value.trim();
    const starValue = parseInt(starValueInput.value);

    if (title.length === 0) {
      alert("제목을 입력해주세요.");
      e.preventDefault();
      return;
    }

    if (content.length < 10) {
      alert("내용은 최소 10자 이상 입력해주세요.");
      e.preventDefault();
      return;
    }

    if (starValue === 0) {
      alert("별점을 선택해주세요.");
      e.preventDefault();
      return;
    }
  });
});

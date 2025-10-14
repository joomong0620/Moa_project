let slideIndex = 0;
let slideTimer;

showSlides();

// 계산식
function showSlides() {
  const slides = document.getElementsByClassName("banner-slide");
  const dots = document.getElementsByClassName("dot");

  for (let i = 0; i < slides.length; i++) {
    slides[i].style.display = "none";
  }

  slideIndex++;
  if (slideIndex > slides.length) { slideIndex = 1 }

  for (let i = 0; i < dots.length; i++) {
    dots[i].classList.remove("active");
  }

  slides[slideIndex - 1].style.display = "block";
  dots[slideIndex - 1].classList.add("active");

  // 5초마다 자동 변경
  slideTimer = setTimeout(showSlides, 5000);
}

function plusSlides(n) {
  clearTimeout(slideTimer);
  slideIndex += n - 1;
  showSlides();
}

function currentSlide(n) {
  clearTimeout(slideTimer);
  slideIndex = n - 1;
  showSlides();
}

// 전시 슬라이드 관련
let exhibitionIndex = 0;
let exhibitionTimer;

function initExhibitionSlider() {
  showExhibitionSlides();
}

function showExhibitionSlides() {
  const slides = document.querySelectorAll(".exhibition-slide");
  const wrapper = document.querySelector(".exhibition-slide-wrapper");

  const totalGroups = Math.ceil(slides.length / 5); // 여기에 적은 수만큼 카드를 묶을 수 있음
  if (exhibitionIndex >= totalGroups) exhibitionIndex = 0;
  if (exhibitionIndex < 0) exhibitionIndex = totalGroups - 1;

  wrapper.style.transform = `translateX(-${exhibitionIndex * 100}%)`;

  clearTimeout(exhibitionTimer);
  exhibitionTimer = setTimeout(() => {
    exhibitionIndex++;
    showExhibitionSlides();
  }, 6000); // 6초마다 자동으로 넘어가도록
}

function moveExhibitionSlide(n) {
  clearTimeout(exhibitionTimer);
  exhibitionIndex += n;
  showExhibitionSlides();
}

document.addEventListener("DOMContentLoaded", initExhibitionSlider);

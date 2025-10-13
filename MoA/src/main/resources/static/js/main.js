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

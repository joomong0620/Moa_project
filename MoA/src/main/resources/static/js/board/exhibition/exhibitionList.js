console.log("exhibitionList.js loaded...");


// 글쓰기 버튼 클릭 시
document.getElementById("insertBtn")?.addEventListener("click", ()=>{

    location.href = `/board2/${location.pathname.split("/")[2]}/insert`;

});




// ---------------------------------------
// 검색창에 이전 검색 기록 남기기
const searchKey = document.getElementById("searchKey");
const searchQuery = document.getElementById("searchQuery");
const options = document.querySelectorAll("#searchKey > option"); 

// 즉시실행함수 
(()=>{
    // 파라미터
    const params = new URL(location.href).searchParams;
    // URL 내장 객체 : 주소 관련 정보를 나타내는 객체
    // location.href : 현재 페이지 주소 + 쿼리스트링
    // URL.searchParams : 쿼리스트링만 별도 객체로 반환

    const key = params.get("key"); 
    const query = params.get("query"); 

    // 검색을 했을 때
    if (key != null) { 
        // 검색어 화면에 출력
        searchQuery.value = query;

        // option 태그에 selected 속성 추가
        for (let option of options) {
            if(option.value == key){
                option.selected = true;
            }
        }
    }

})()


// 카드목록 슬라이더 부분
const sliderWrapperPresent = document.getElementById('sliderWrapperPresent');
const leftBtnPresent = document.getElementById('leftBtnPresent');
const rightBtnPresent = document.getElementById('rightBtnPresent');            
const sliderWrapperFuture = document.getElementById('sliderWrapperFuture');
const leftBtnFuture = document.getElementById('leftBtnFuture');
const rightBtnFuture = document.getElementById('rightBtnFuture');    
const sliderWrapperPast = document.getElementById('sliderWrapperPast');
const leftBtnPast = document.getElementById('leftBtnPast');
const rightBtnPast = document.getElementById('rightBtnPast');    
const itemsPerPage = 3;
let presentIndex = 0;
let futureIndex = 0;
let pastIndex = 0;            

// 빈 칸 채우기
function padEmptyItems(sliderWrapper, tagSW) {
        const totalItems = sliderWrapper.children.length;
        const remainder = totalItems % itemsPerPage;
        if (remainder !== 0) {
            const itemsToAdd = itemsPerPage - remainder;
            for (let i = 0; i < itemsToAdd; i++) {
                const emptySlide = document.createElement('div');
                emptySlide.className = 'slide' + ' ' + tagSW;
                emptySlide.innerHTML = '<div class="slide-item ' + tagSW + '" style="opacity: 0;"></div>';
                sliderWrapper.appendChild(emptySlide);
            }
        }
}

function slideLeftPresent() {
    if (presentIndex > 0) {
        presentIndex--;
        updateSlider(sliderWrapperPresent, presentIndex, 'present');
    }
}


function slideLeftFuture() {
    if (futureIndex > 0) {
        futureIndex--;

        let tagString = 'future';
        updateSlider(sliderWrapperFuture, futureIndex, tagString);
    }
}

function slideLeftPast() {
    if (pastIndex > 0) {
        pastIndex--;
        updateSlider(sliderWrapperPast, pastIndex, 'past');
    }
}            

function slideRightPresent() {
    const totalSlides = sliderWrapperPresent.children.length;
    const maxIndex = Math.ceil(totalSlides / itemsPerPage) - 1;
    if (presentIndex < maxIndex) {
        presentIndex++;
        updateSlider(sliderWrapperPresent, presentIndex, 'present');
    }
}

function slideRightFuture() {
    const totalSlides = sliderWrapperFuture.children.length;
    const maxIndex = Math.ceil(totalSlides / itemsPerPage) - 1;
    if (futureIndex < maxIndex) {
        futureIndex++;
        let tagString = 'future';
        updateSlider(sliderWrapperFuture, futureIndex, tagString);
    }
}

function slideRightPast() {
    const totalSlides = sliderWrapperPast.children.length;
    const maxIndex = Math.ceil(totalSlides / itemsPerPage) - 1;
    if (pastIndex < maxIndex) {
        pastIndex++;
        updateSlider(sliderWrapperPast, pastIndex, 'past');
    }
}

function updateSlider(sliderWrapper, index, tagString) {
    const offset = index * 100;
    sliderWrapper.style.transform = `translateX(-${offset}%)`;

    updateButtons(sliderWrapper, index, tagString);
}

function updateButtons(sliderWrapper, index, tagString) {
    const totalSlides = sliderWrapper.children.length;
    const maxIndex = Math.ceil(totalSlides / itemsPerPage) - 1;
    if (tagString == 'present'){
        leftBtnPresent.disabled = index === 0;
        rightBtnPresent.disabled = index >= maxIndex;
    } else if (tagString == 'future') {
        leftBtnFuture.disabled = index === 0;
        rightBtnFuture.disabled = index >= maxIndex;                   
    } else if (tagString == 'past') {
        leftBtnPast.disabled = index === 0;
        rightBtnPast.disabled = index >= maxIndex;     
    }
}


// 초기 실행
window.addEventListener('load', () => {
    const sliderWrappersToChange = [sliderWrapperPresent , sliderWrapperFuture , sliderWrapperPast ];
    const leftBtnsToChange = [leftBtnPresent , leftBtnFuture , leftBtnPast ];
    const rightBtnsToChange = [rightBtnPresent , rightBtnFuture , rightBtnPast ];

    padEmptyItems(sliderWrapperPresent, 'present');
    padEmptyItems(sliderWrapperFuture, 'future');
    padEmptyItems(sliderWrapperPast, 'past');

    updateSlider(sliderWrapperPresent, 0, 'present');
    updateSlider(sliderWrapperFuture, 0, 'future');
    updateSlider(sliderWrapperPast, 0, 'past');
});



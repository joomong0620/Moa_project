console.log("boardWrite.js loaded");

// 사진 미리보기
const preview = document.getElementsByClassName("preview"); // img 5개

const inputImage = document.getElementsByClassName("inputImage"); // file 5개

const deleteImage = document.getElementsByClassName("delete-image"); // x버튼 5개

for(let i=0; i<preview.length; i++){

    // 파일이 선택되거나, 선택 후 취소 되었을 때
    inputImage[i].addEventListener("change", e=>{

        const file = e.target.files[0]; // 파일은 한번에 하나만 선택 가능 -> 0번 인덱스에 담긴다.

        // 파일이 선택된 경우 
        if(file != undefined){
            const reader = new FileReader(); // 파일을 읽는 객체

            reader.readAsDataURL(file);
            // 지정된 파일을 읽은 후 result 속성에 url 형식으로 저장

            reader.onload = e=>{ // 파일을 다 읽은 후 수행
                preview[i].setAttribute("src", e.target.result); 
            }
        } else{ // 선택 후 취소 되었을 때  == 선택된 파일 없음 -> 미리보기 삭제
            preview[i].removeAttribute("src");

        }
    })

    // 미리보기 삭제(x버튼)
    deleteImage[i].addEventListener("click", e=>{
       
        // 미리보기 이미지가 있을 경우
        if(preview[i].getAttribute("src") != ''){

            // 미리보기 삭제
            preview[i].removeAttribute("src");

            // file 태그의 value 삭제
            // * input type='file'의 value는 빈칸만 대입 가능!
            inputImage[i].value = '';
        }
    })


}

// 게시글 등록 시 제목, 내용 작성 여부 검사
const boardWriteFrm = document.getElementById("boardWriteFrm");
const boardTitle = document.getElementsByName("boardTitle")[0];
const boardContent = document.getElementsByName("boardContent")[0];

boardWriteFrm.addEventListener("submit", e=>{
    // 제출 시 제목,내용이 입력 안된 경우 
    // -> OO을 입력해주세요. 알림창 / 포커스 / 제출막기 / 띄어쓰기만 있는 경우도 제출 X

    if(boardTitle.value.trim() == ""){
        alert("제목을 입력해주세요.");
        boardTitle.focus();
        e.preventDefault();
        boardTitle.value = "";
        return;
    }

    if(boardContent.value.trim() == ""){
        alert("내용을 입력해주세요.");
        boardContent.focus();
        e.preventDefault();
        boardContent.value = "";
        return;
    }
})
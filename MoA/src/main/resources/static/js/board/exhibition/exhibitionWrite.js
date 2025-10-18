console.log("exhibitionWrite.js loaded");


// 사진 미리보기
const preview = document.getElementsByClassName("preview"); // img 요소 1개(img-tag)
const inputImage = document.getElementsByClassName("inputImage"); // input-file 요소 1개(input-tag)
const deleteImage = document.getElementsByClassName("delete-image"); // x버튼 요소 1개 (span-tag)

console.log(preview.length);

for (let i=0; i<preview.length; i++){

    // 파일이 선택되거나(inputImage에 change 이벤트 발생), 선택 후 취소 되었을 때

    inputImage[i].addEventListener("change", e => {
            console.log("이벤트 리스너 test");
            // 파일이 선택된 경우 미리 보기
            const file = e.target.files[0]; 
            console.log(file);

            // 파일이 선택된 경우 미리보기 (서버로 보내서 저장하기 전에 미리보기)
            if (file != undefined){ // undefined는 '취소'버튼을 누른 경우, 선택 파일 없음
                const reader = new FileReader(); // 파일을 읽는 객체
    
                reader.readAsDataURL(file); 
                // 지정된 파일을 읽은 후 "result 속성"에 "url형식"으로 저장 ->  e.target.result
    
                // 파일 다 읽은 후(onload이벤트) 수행
                reader.onload = e => {
                    preview[i].setAttribute('src', e.target.result); // src에 파일 주소저장
                }
            } else { // 선택 후 취소 되었을 때 == 선택된 파일 없음 -> 미리보기 삭제
                preview[i].removeAttribute('src');
            
            }

    })


    // 미리보기 삭제(x버튼 눌렀을 때)
    deleteImage[i].addEventListener("click", () => {
        // 미리보기 이미지가 있을 경우
        if(preview[i].getAttribute("src") != '') {
            // 미리보기 삭제
            preview[i].removeAttribute('src');

            // input-file 태그의 value 삭제
            // * input type = 'file'의 value는 빈칸만 가능
            inputImage[i].value = '';
        }

    })

}



// 게시글 등록 시 제목, 내용 작성 여부 검사: 유효성 검사
const boardWriteFrm = document.getElementById("boardWriteFrm");
const exhibitTitle = document.getElementsByName("exhibitTitle")[0]; // 배열 인덱스 0
const exhibitContent = document.getElementsByName("exhibitContent")[0]; 

const exhibitInstitution = document.getElementsByName("exhibitInstitution")[0]; 
const exhibitDate = document.getElementsByName("exhibitDate")[0]; 
const exhibitAuthor = document.getElementsByName("exhibitAuthor")[0]; 
const exhibitContact = document.getElementsByName("exhibitContact")[0]; 
const imageInput = document.getElementsByName("images")[0]; 


boardWriteFrm.addEventListener("submit", e=>{

    // 제출 시 제목, 내용, 전시 기관, 전시 기간, 참여 작가, 문의전화번호, 전시포스터가 입력 않된 경우 
    // --> OO을 입력해 주세요. 알림창/ 포커스 / 제출막기 / 띄어쓰기만 있는 경우도 제출 X


    if(exhibitTitle.value.trim() == ""){
        alert("제목을 입력해 주세요.");
        exhibitTitle.focus();
        e.preventDefault();
        exhibitTitle.value="";
        return;
    }

    if(exhibitContent.value.trim() == ""){
        alert("내용을 입력해 주세요.");
        exhibitContent.focus();
        e.preventDefault();
        exhibitContent.value="";
        return;
    }


    if(exhibitInstitution.value.trim() == ""){
        alert("전시 기관을 입력해 주세요.");
        exhibitInstitution.focus();
        e.preventDefault();
        exhibitInstitution.value="";
        return;
    }

    if(exhibitDate.value.trim() == ""){
        alert("전시 기간을 입력해 주세요.");
        exhibitDate.focus();
        e.preventDefault();
        exhibitDate.value="";
        return;
    }

    if(exhibitAuthor.value.trim() == ""){
        alert("참여 작가를 입력해 주세요.");
        exhibitAuthor.focus();
        e.preventDefault();
        exhibitAuthor.value="";
        return;
    }

    if(exhibitContact.value.trim() == ""){
        alert("문의 전화번호를 입력해 주세요.");
        exhibitContact.focus();
        e.preventDefault();
        exhibitContact.value="";
        return;
    }  

    // 파일 선택 여부 검사
    if (!imageInput || imageInput.files.length === 0) {
        alert("전시포스터를 반드시 올려주세요.(예시 작품이미지로 대체가능)");
        imageInput.focus();
        e.preventDefault();
        return;
    }

    // 선택된 파일 타입 검사 (선택사항)
    const file = imageInput.files[0];
    if (!file.type.startsWith("image/")) {
        alert("이미지 파일만 업로드할 수 있습니다.");
        imageInput.value = ""; // 잘못된 파일 제거
        imageInput.focus();
        e.preventDefault();
        return;
    }
        

})

// 게시글 등록 취소시 목록으로 
const insertCancelBtn = document.getElementById("insertCancelBtn");
insertCancelBtn.addEventListener("click", ()=>  {
    console.log("insertCancelBtn clicked... ")
    const confirmCancel = confirm("취소하시겠습니까?");
    if (confirmCancel) {
        console.log("입력 취소, 전시게시판 목록으로...");
        location.href = '/board/3'; 
    } else {
        console.log("계속입력...")
    }
    
})


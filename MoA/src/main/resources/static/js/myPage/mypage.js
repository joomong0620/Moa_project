console.log("My Page JS Loaded");

document.querySelectorAll('.menu-btn').forEach(btn => {
  btn.addEventListener('click', () => {
    // 모든 버튼/섹션 비활성화
    document.querySelectorAll('.menu-btn').forEach(b => b.classList.remove('active'));
    document.querySelectorAll('.content-section').forEach(sec => sec.classList.add('hidden'));

    // 현재 버튼/섹션 활성화
    btn.classList.add('active');
    const targetId = btn.dataset.target;
    document.getElementById(targetId).classList.remove('hidden');
  });
});




document.addEventListener("DOMContentLoaded", () => {
  const nicknameSpan = document.getElementById("memberNickname");
  const nicknameInput = document.getElementById("nicknameInput");
  const nicknameBtn = document.getElementById("changeNicknameBtn");

  nicknameBtn.addEventListener("click", () => {
    // 수정 모드 전환
    if (nicknameInput.classList.contains("hidden")) {
      nicknameInput.classList.remove("hidden");
      nicknameSpan.classList.add("hidden");
      nicknameBtn.textContent = "저장";
      nicknameInput.focus();
    } else {
      // 저장 요청
      const formData = new FormData();
      formData.append("memberNickname", nicknameInput.value);

      fetch("/mypage/info", {
        method: "POST",
        body: formData
      })
      .then(resp => resp.ok ? resp.text() : Promise.reject("요청 실패"))
      .then(() => {
        nicknameSpan.textContent = nicknameInput.value;
        nicknameInput.classList.add("hidden");
        nicknameSpan.classList.remove("hidden");
        nicknameBtn.textContent = "변경";
        alert("닉네임이 변경되었습니다.");
      })
      .catch(err => console.error(err));
    }
  });
});


//=====================================================================

// 회원 탈퇴 페이지인 경우
const secessionFrm = document.getElementById("secessionFrm");
const memberPw = document.getElementById("memberPw");
const agree = document.getElementById("agree");

if(secessionFrm != null){

    secessionFrm.addEventListener("submit", e=>{
        // 비밀번호 미작성
        if(memberPw.value == ""){
            alert("비밀번호를 작성해주세요.");
            e.preventDefault();
            memberPw.focus();
            memberPw.value = '';
            return;
        }

        // 약관동의가 체크되지 않은 경우
        if(!agree.checked){
            alert("약관 동의 후 탈퇴버튼을 눌러주세요.");
            e.preventDefault();
            return;
        }
        
        // 탈퇴 버튼 클릭 시 "정말로 탈퇴하시겠습니까?" 
        // 이 때, 취소 클릭 시 "탈퇴 취소" 알림창
        if(!confirm("정말로 탈퇴하시겠습니까?")){
            alert("탈퇴 취소");
            e.preventDefault();
        }
    })
}

//=====================================================================
// 프로필 이미지 추가/변경/삭제
const profileImg = document.getElementById("profileImg"); // img 태그
const deleteImage = document.getElementById("deleteImage"); // X 버튼
const imageInput = document.getElementById("imageInput");  // input type='file'

let initCheck; // 초기 프로필 이미지 상태를 저장하는 변수
               // false == 기본 이미지, true == 이전 업로드 이미지
let deleteCheck = -1;
// 프로필 이미지가 새로 업로드 되거나 삭제 되었음을 나타내는 변수
// -1 == 초기값, 0 == 프로필 삭제(x버튼), 1 == 새 이미지 업로드

let originalImage; // 초기 프로필 이미지 파일 경로 저장

// 화면에 imageInput이 있을 경우 == 프로필 페이지인 경우
if(imageInput != null){

    // 프로필 이미지가 출력되는 img 태그의 src 속성 저장
    originalImage = profileImg.getAttribute("src");

    // 현재 회원의 프로필 이미지 상태 확인
    if(originalImage == "/images/common/user.svg"){
        // 기본 이미지
        initCheck = false;
    }else{
        initCheck = true;
    }

    imageInput.addEventListener("change", e => {

        //console.log(e.target) // input
        //console.log(e.target.value) // 업로드된 파일 경로
        //console.log(e.target.files) // 업로드한 파일의 정보가 담긴 배열
        
        const file = e.target.files[0]; // 선택된 파일


        // 파일을 한 번 선택한 후 취소 했을 때
        if(file == undefined){
            console.log("파일 선택 취소");
            deleteCheck = -1; // 파일 취소 == 파일 없음 == 초기 상태

            // 취소 시 기존 이미지로 되돌리기
            profileImg.setAttribute('src', originalImage);
            return;
        }

        // 2MB로 최대 크기 제한
        const maxSize = 1 * 1024 * 1024 * 2; // 파일 최대 크기 지정
        //                  1KB    1MB   2MB

        // 선택된 파일의 크기가 최대 크기(maxSize)를 초과한 경우
        if(file.size > maxSize){
            alert("2MB 이하의 이미지를 선택해주세요.");
            imageInput.value = ""; // input type='file' 태그의 value는 빈칸만 대입 가능!
            deleteCheck = -1; // 파일 취소 == 초기 상태
            profileImg.setAttribute('src', originalImage); // 기존 프로필 이미지로 변경
            return;
        }


        // JS에서 파일을 읽는 객체
        // - 파일을 읽고 클라이언트 컴퓨터에 파일을 저장할 수 있음
        const reader = new FileReader();

        reader.readAsDataURL(file);
        // 매개변수에 작성된 파일을 읽어서 저장 후
        // 파일을 나타내는 URL을 result 속성으로 얻어옴

        // 파일을 다 읽었을 때
        reader.onload = e => {
            //console.log(e.target);
            //console.log(e.target.result); // 읽은 파일의 URL

            // 프로필 이미지 태그에 src 속성으로 읽어온 파일의 URL 추가
            profileImg.setAttribute("src", e.target.result);

            deleteCheck = 1;
        }

    })

    // x 버튼 클릭 시 
    deleteImage.addEventListener('click', ()=>{

        // 프로필 이미지를 기본 프로필 이미지로 변경
        profileImg.setAttribute('src', "/images/common/user.svg");
        deleteCheck = 0;
        imageInput.value = '';
    })

    // #profileFrm이 제출 되었을 때
    document.getElementById("profileFrm").addEventListener("submit", e=>{

        // let initCheck; // 초기 프로필 이미지 상태를 저장하는 변수
        // false == 기본 이미지, true == 이전 업로드 이미지

        // let deleteCheck = -1;
        // 프로필 이미지가 새로 업로드 되거나 삭제 되었음을 나타내는 변수
        // -1 == 초기값, 0 == 프로필 삭제(x버튼), 1 == 새 이미지 업로드

        let flag = false;

        // 프로필 이미지가 없다 -> 있다
        if(!initCheck && deleteCheck == 1) flag = true;

        // 이전 프로필 이미지가 있다 -> 삭제
        if(initCheck && deleteCheck == 0) flag = true;

        // 이전 프로필 이미지가 있다 -> 새 이미지
        if(initCheck && deleteCheck == 1) flag = true;

        // 위 3가지 경우가 아니라면 제출 X
        if(!flag){
            alert("이미지 변경 후 제출하세요.");
            e.preventDefault();  // form 기본 이벤트 제거
        }

    })

    // x 버튼 클릭 후 이미지 선택 취소 시 기존 이미지로 변경하기
    imageInput.addEventListener("click", () => {

        // 이미지 선택 이전에 x버튼 클릭 여부 확인
        if(deleteCheck == 0){ // x버튼 클릭 O


            document.body.onfocus = ()=>{
                //console.log(imageInput.value) // 빈칸('') 왜? 이미지를 얻어오기 전에 출력하기 때문에

                setTimeout(function(){
                    // 파일 선택 취소 시
                    if(imageInput.value == ''){
                        profileImg.setAttribute('src', originalImage);
                        deleteCheck = -1;
                    }
                }, 400); // 0.4초 후 실행

                // 추가된 이벤트 초기화
                document.body.onfocus = null;
            }
        }
    })

}


// 예매 취소 버튼 클릭 시
document.addEventListener("click", (e) => {
  if (e.target.classList.contains("cancel-btn")) {
    const payNo = e.target.dataset.rsvno;

    if (!confirm("정말 예매를 취소하시겠습니까?")) return;

    fetch("/mypage/cancel", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ payNo: payNo }),
    })
      .then(resp => resp.text())
      .then(result => {
        if (result > 0) {
          alert("예매가 취소되었습니다.");
          location.reload();
        } else {
          alert("취소에 실패했습니다.");
        }
      })
      .catch(err => console.error(err));
  }
});


  // 리뷰 작성 버튼 (예시)
  document.querySelectorAll(".review-btn").forEach(btn => {
    btn.addEventListener("click", () => {
      const boardNo = btn.dataset.boardno;
      location.href = `/review/write?boardNo=${boardNo}`;
    });
  });

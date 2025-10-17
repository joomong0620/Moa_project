console.log("detail.js loaded");

const boardLike = document.getElementById("boardLike");

// 좋아요 버튼이 클릭되었을 때
boardLike.addEventListener("click", e => {
    
    // 로그인 X : "로그인 후 이용해주세요."
    if (loginMemberNo == "") {
        alert("로그인 후 이용해주세요.");
        return;
    }

    let check; // 기존에 좋아요X (빈하트) : 0, 기존에 좋아요O (꽉찬 하트) : 1
    
    // contains("클래스명") : 클래스가 있으면 true, 없으면 false
    if (e.target.classList.contains("fa-regular")) { // 좋아요X(빈하트) 이라면
        check = 0;
    } else{ // 좋아요O (꽉찬 하트) 라면
        check = 1;
    }
    // ==> 예전에 이미 좋아요를 했었을 경우에 1 이 담기고 아니라면 0이 담긴다
    // ==> 이걸로 

    // ajax로 서버에 제출할 파라미터를 모아둔 JS 객체
    const data = {
        memberNo    : loginMemberNo,
        "boardNo"   : boardNo,
        "check"     : check
    }; 
    // ==> K:V 형식으로 담기, JS는 어떻게 담던간에 전부 String으로 됨

    // ajax 비동기 통신
    fetch("/board/like", {
        method  : "POST", // ==> POST 방식이기 떄문에 java에서 @RequestBody로 받아야 함
        headers : {"Content-Type" : "application/json"},
        body    : JSON.stringify(data)
    })
    .then(resp => resp.text()) // 응답 객체를 필요한 형태로 파싱하여 리턴 (==> 변수는 자유)
    .then(count => {
        // 파싱된 데이터를 받아서 처리하는 코드 작성
        console.log("count : " + count);

        // INSERT, DELETE 실패 시
        if (count == -1) {
            alert("좋아요 처리에 문제가 발생하였습니다.")
            return;
        }

        // toggle() : 클래스가 있으면 없애고, 없으면 추가
        e.target.classList.toggle("fa-regular");
        e.target.classList.toggle("fa-solid");
        
        // 현재 게시글의 좋아요 수를 화면에 출력
        e.target.nextElementSibling.innerText = count;
    
    })
    .catch(err => {console.log(err)}) // 예외 발생 시 처리할 코드






});


document,getElementById("pm-buy-btn").addEventListener("click", () =>{
    
    // 1) 로그인이 되어있나? -> 전역변수 memberNo 이용
    if(loginMemberNo == ""){ // 로그인 X
        alert("로그인 후 이용해주세요.");
        return;
    }
})
console.log("signUp.js");

// 유효성 검사 진행 여부 확인 객체
const checkObj = {
    'memberEmail' : false,
    'memberPw' : false,
    'memberPwConfirm' : false,
    'memberNickname' : false,
    'memberTel' : false,
    'authKey' : false
};

// 아이디 유효성 검사
const memberId = document.getElementById("memberId");
const idMessage = document.getElementById("idMessage");

memberId.addEventListener("input", ()=>{

    if(memberId.value == ''){
        console.log(memberId)

        idMessage.innerText = "아이디를 입력해 주세요"
        idMessage.classList.remove("confirm");
        idMessage.classList.add("error");
        checkObj.memberId = false;

        console.log(idMessage)
        console.log("idMessage")
        return;
    }

    const regEx = /^[a-z0-9]{5,12}$/


    if(regEx.test(memberId.value)){

        // 중복 검사        
        fetch("/dupCheck/memberId?memberId=" + memberId.value)
        .then(resp => resp.text())
        .then(count => {
    
            if(count == 1){
    
                idMessage.innerText = "이미 사용 중인 아이디입니다.";
                idMessage.classList.remove("confirm");
                idMessage.classList.add("error");
                checkObj.memberId = false;
                
                
            }else{

                idMessage.innerText = "사용 가능한 아이디입니다.";
                idMessage.classList.remove("error");
                idMessage.classList.add("confirm");
                checkObj.memberId = true;
    
                
            }
        })
        .catch(err => console.log(err))
    
    }

    


})

console.log("signUp.js");

// 유효성 검사 진행 여부 확인 객체
const checkObj = {
    'memberId' : false,
    'memberEmail' : false,
    'memberPw' : false,
    'memberPwConfirm' : false,
    'memberNickname' : false,
    'memberTel' : false,
    
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
    
    } else{

        idMessage.innerText = "유효하지 않은 아이디입니다.";
        idMessage.classList.remove("confirm");
        idMessage.classList.add("error");
        checkObj.memberId = false;


    }


})


// 이메일 유효성 검사
const memberEmail = document.getElementById("memberEmail");
const emailMessage = document.getElementById("emailMessage");

memberEmail.addEventListener('input', ()=>{
    
    if(memberEmail.value == ''){
        emailMessage.innerText = "이메일을 입력해주세요.";
        emailMessage.classList.remove("confirm","error");
        checkObj.memberEmail = false;
        return;
    }

    
    const regEx = /^[\w-]{5,}@[a-z]+(\.[a-z]+){1,2}$/;
    if(regEx.test(memberEmail.value)){ 


        fetch("/dupCheck/email?email=" + memberEmail.value) 
        .then(resp => resp.text())
        .then(count => {
            
            if(count == 1){

                // 유효한 경우 
                emailMessage.innerText = "이미 사용 중인 이메일입니다.";
                emailMessage.classList.remove("confirm");
                emailMessage.classList.add("error");
                checkObj.memberEmail = false;

            }else{

                // 유효하지 않은 경우 
                emailMessage.innerText = "사용 가능한 이메일입니다.";
                emailMessage.classList.remove("error");
                emailMessage.classList.add("confirm");
                checkObj.memberEmail = true;

            }
        })
        .catch(err => console.log(err)) 

    } 
    
})


// 비밀번호 유효성 검사
const memberPw = document.getElementById("memberPw");
const memberPwConfirm = document.getElementById("memberPwConfirm");
const pwMessage = document.getElementById("pwMessage");
const pwConfirmMessage = document.getElementById("pwConfirmMessage");


memberPw.addEventListener("input", ()=>{
    
    if(memberPw.value.length == 0){
        pwMessage.innerText = "영어,숫자,특수문자(!) 6~20글자 사이로 입력하세요.";
        pwMessage.classList.remove("confirm","error");
        checkObj.memberPw = false;
        return;
    }
    
    
    const regEx = /^[\w!]{6,20}$/;
    if(regEx.test(memberPw.value)){ 
        checkObj.memberPw = true;

        // 비밀번호가 유효. 비밀번호 확인이 입력 안 된 경우
        if(memberPwConfirm.value == ''){
            pwMessage.innerText = "사용 가능한 비밀번호 입니다.";
            pwMessage.classList.remove("error");
            pwMessage.classList.add("confirm");

        } else{ // 비밀번호 확인까지 입력한 경우

            // 비밀번호 비밀번호 확인이 같을 경우
            if(memberPw.value == memberPwConfirm.value){
                pwMessage.innerText = "비밀번호가 일치합니다.";
                pwMessage.classList.add("confirm");
                pwMessage.classList.remove('error');
                checkObj.memberPwConfirm = true;

            }else{
                // 비밀번호 비밀번호 확인이 다를 경우
                pwMessage.innerText = "비밀번호가 서로 일치하지 않습니다.";
                pwMessage.classList.add("error");
                pwMessage.classList.remove('confirm');
                checkObj.memberPwConfirm = false;
            }
        }

    } else{
        // 유효하지 않은 경우 
        pwMessage.innerText = "유효한 비밀번호를 입력하세요.";
        pwMessage.classList.remove("confirm");
        pwMessage.classList.add("error");
        checkObj.memberPw = false;
    }
})

// 비밀번호 확인 유효성 검사
memberPwConfirm.addEventListener('input', ()=>{

    // 비밀번호가 입력되지 않은 경우
    if(memberPw.value == ''){
        alert("비밀번호를 입력하세요.");
        memberPw.focus();
        memberPwConfirm.value = '';
        return;
    }

    // 비밀번호가 유효한 경우
    if(checkObj.memberPw){

        // 비밀번호 비밀번호 확인이 같을 경우
        if(memberPw.value == memberPwConfirm.value){
            pwConfirmMessage.innerText = "비밀번호가 일치합니다.";
            pwConfirmMessage.classList.add("confirm");
            pwConfirmMessage.classList.remove('error');
            checkObj.memberPwConfirm = true;

        }else{
            // 비밀번호 비밀번호 확인이 다를 경우
            pwConfirmMessage.innerText = "비밀번호가 서로 일치하지 않습니다.";
            pwConfirmMessage.classList.add("error");
            pwConfirmMessage.classList.remove('confirm');
            checkObj.memberPwConfirm = false;
        }
    } else{ 
        // 비밀번호가 유효하지 않은 경우
        checkObj.memberPwConfirm = false;
    }
})

// 닉네임 유효성 검사
const memberNickname = document.getElementById("memberNickname");
const nickMessage = document.getElementById("nickMessage");

memberNickname.addEventListener('input', ()=>{
    
    if(memberNickname.value == ''){
        nickMessage.innerText = "한글,영어,숫자로만 4~10글자로 입력해주세요.";
        nickMessage.classList.remove("confirm");
        nickMessage.classList.add("error");
        checkObj.memberNickname = false;
        return;
    }

    
    const regEx = /^[가-힣a-zA-Z0-9]{4,10}$/;
    if(regEx.test(memberNickname.value)){
       

        fetch("/dupCheck/nickname?nickname=" + memberNickname.value)

        .then(resp => resp.text()) 
        .then(count => {
                            
            if(count == 1){

                nickMessage.innerText = "중복된 닉네임 입니다.";
                nickMessage.classList.remove("confirm");
                nickMessage.classList.add("error");
                checkObj.memberNickname = false;

                

            }else{

                nickMessage.innerText = "사용 가능한 닉네임 입니다.";
                nickMessage.classList.remove("error");
                nickMessage.classList.add("confirm");
                checkObj.memberNickname = true;

            }
        })
        .catch(err => console.log(err)) 

        
    } else{

        nickMessage.innerText = "유효한 닉네임을 입력하세요.";
        nickMessage.classList.remove("confirm");
        nickMessage.classList.add("error");
        checkObj.memberNickname = false;


    }
    
})


// 전화번호 유효성 검사
const memberTel = document.getElementById("memberTel");
const telMessage = document.getElementById("telMessage");

memberTel.addEventListener('input', ()=>{
   
    if(memberTel.value == ''){
        telMessage.innerText = "전화번호를 입력해주세요.";
        telMessage.classList.remove("confirm","error");
        checkObj.memberTel = false;
        return;
    }

    const regEx = /^0(1[01]|2|[3-6][1-5]|70)\d{7,8}$/;

    if(regEx.test(memberTel.value)){
        
        telMessage.innerText = "유효한 전화번호입니다.";
        telMessage.classList.remove("error");
        telMessage.classList.add("confirm");
        checkObj.memberTel = true;
    } else{
         
        telMessage.innerText = "유효하지 않은 전화번호입니다.";
        telMessage.classList.remove("confirm");
        telMessage.classList.add("error");
        checkObj.memberTel = false;
    }


    
})


document.getElementById("signFrm").addEventListener("submit", e=>{

    for(let key in checkObj){

        if(!checkObj[key]){


            switch(key){
                case 'memberId' : alert("아이디가 유효하지 않습니다."); break;
                case 'memberEmail' : alert("이메일이 유효하지 않습니다."); break;
                case 'memberPw' : alert("비밀번호가 유효하지 않습니다."); break;
                case 'memberPwConfirm' : alert("비밀번호 확인이 유효하지 않습니다."); break;
                case 'memberNickname' : alert("닉네임이 유효하지 않습니다."); break;
                case 'memberTel' : alert("전화번호가 유효하지 않습니다."); break;
                
            }

            console.log(checkObj)
            document.getElementById(key).focus();


            e.preventDefault()

            console.log(checkObj)

            return;
        }
        
        
    }


})
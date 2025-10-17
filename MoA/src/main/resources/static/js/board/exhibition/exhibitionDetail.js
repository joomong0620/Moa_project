console.log("exhibitionDetail.js loaded...");



// ----------------------------------
// 게시글 수정
//
// 게시글 버튼 수정 클릭시
// /board2/3/1500/update?cp=1 (GET) 요청 보내기: location.href = "주소" (GET 방식 요청)
if (document.getElementById("updateBtn") != null){ // 로그인 안했으면 수정버튼 안보임 => null처리 필요
    document.getElementById("updateBtn").addEventListener("click", ()=>{
        console.log("updateBtn clicked... ")
        // 주소 예시: 현 상세화면 주소 http://localhost:8088/board/3/903?cp=1
        //location.href = location.pathname.replace('board', 'board2') + "/update"; // -> 게시글 수정 처리하는 controller 만들어야 한다.
        // http://localhost:8088/board2/3/927/update
        location.href = location.pathname.replace('board', 'board2') + '/update' + location.search; // location.search = '?cp=1'
        // eg: '/board2/3/1512/update?cp=1'
        // http://localhost:8088/board2/3/904/update?cp=1
        
    })
}


// ---------------------------------
//
// 게시글 삭제 버튼이 클릭 되었을 때
//
 // 로그인 안했으면 삭제버튼 안보임 => null처리 필요 => 옵셔날 체이닝으로 처리
document.getElementById("deleteBtn")?.addEventListener("click", ()=>{

    console.log("deleteBtn clicked... ")

    if(confirm("정말 삭제 하시겠습니까?")) {

        // /board2/3/1500/delete (GET 방식)
        location.href = location.pathname.replace('board', 'board2') + "/delete"; // -> 게시글 삭제 처리하는 controller 만들어야 한다.
        // http://localhost/board2/3/1512/delete

    }
})


// -----------------------------------------------------------
// 목록으로
const goToListBtn = document.getElementById("goToListBtn");

goToListBtn.addEventListener("click", ()=>{

    console.log("goToListBtn clicked... ")

    // // 현:   http://localhost/board/3/1364?cp=6
    // // 이전: http://localhost/board/3?cp=6
    // location.href = '/board/' + communityCode + location.search;
    // // communityCode: 는 exhibitionDetail.html에서 전역변수로 선언되어 있으므로 그냥 갖다 쓰면 된다.
    // // location.search: 쿼리스트링만 반환
    // // location.href = location.pathname.split("/").slice(0, -1).join('/') + location.search; => 현 주소에서 exhibitNo = 1364 떨구기

    // 하단 게시판 검색인 경우 : /board/3
    // 상단 통합 검색인 경우 : /board/search (Not Used here)
    
    // URL 내장 객체 : 주소 관련 정보를 나타내는 객체
    // URL.searchParams : 쿼리스트링만 별도 객체로 반환
    const params = new URL(location.href).searchParams;
    console.log("params : " + params);

    let url;
    if (params.get("key") == 'all') { // header의 통합 검색 일때 (Not Used here)
        url = "/board/search";
    } else {
        url = '/board/' + communityCode; // 목록으로; communityCode는 exhibitionDetail.html에서 전역변수로 선언되어있어서 여기서 사용가능
    }

    location.href = url + location.search; // location.search: search 있으면 있는 대로, 없으면 없는대로

})



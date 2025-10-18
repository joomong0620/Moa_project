console.log("exhibitionDetail.js loaded...");



// ----------------------------------
// 게시글 버튼 수정 클릭시
if (document.getElementById("updateBtn") != null){ 
    document.getElementById("updateBtn").addEventListener("click", ()=>{

        location.href = location.pathname.replace('board', 'board2') + '/update' + location.search; // location.search = '?cp=1'
        
    })
}


// ---------------------------------
// 게시글 삭제 버튼이 클릭 되었을 때
document.getElementById("deleteBtn")?.addEventListener("click", ()=>{

    console.log("deleteBtn clicked... ")

    if(confirm("정말 삭제 하시겠습니까?")) {

        location.href = location.pathname.replace('board', 'board2') + "/delete";

    }
})


// -----------------------------------------------------------
// 목록으로
const goToListBtn = document.getElementById("goToListBtn");

goToListBtn.addEventListener("click", ()=>{

    console.log("goToListBtn clicked... ")

    const params = new URL(location.href).searchParams;
    console.log("params : " + params);

    let url;
    if (params.get("key") == 'all') { // header의 통합 검색 일때 (Not Used here)
        url = "/board/search";
    } else {
        url = '/board/' + communityCode; // 목록으로; communityCode는 전역변수
    }

    location.href = url + location.search; // location.search: search 있으면 있는 대로, 없으면 없는대로

})



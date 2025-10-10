//package edu.og.moa.board.freeboard.Controller;
//
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.SessionAttributes;
//
//import edu.og.moa.board.freeboard.model.service.FreeBoardService;
//
//@Controller
//@RequestMapping("/board")
//@SessionAttributes("loginMember")
//public class FreeBoardController {
//
//    @Autowired
//    private FreeBoardService service;  // 이름은 FreeBoardService지만, 사실상 전체 게시판 조회에 사용
//
//    @GetMapping("/{boardCode:[0-9]+}")
//    public String selectBoardList(@PathVariable("boardCode") int boardCode,
//                                  @RequestParam(value="cp", required=false, defaultValue="1") int cp,
//                                  Model model,
//                                  @RequestParam Map<String, Object> paramMap) {
//        
//        Map<String, Object> map = service.selectFreeBoardList(boardCode, cp);
//        model.addAttribute("map", map);
//        model.addAttribute("boardCode", boardCode);
//        
//        System.out.println("=== 게시판 종류 ===");
//        service.selectBoardTypeList().forEach(row -> System.out.println(row));
//        
//        
//        System.out.println("=== 게시판 코드 " + boardCode + " 글 목록 ===");
//        if (map.get("boardList") != null) {
//            ((java.util.List<?>) map.get("boardList")).forEach(System.out::println);
//        } else {
//            System.out.println("게시글 없음");
//        }
//        return "board/freeboard/freeboardList"; // 뷰 이름은 공통으로
// 
//    }
//    
//    
//}
//
//
//

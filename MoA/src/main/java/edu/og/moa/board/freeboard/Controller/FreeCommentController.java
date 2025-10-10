package edu.og.moa.board.freeboard.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.og.moa.board.freeboard.model.dto.Comment;
import edu.og.moa.board.freeboard.model.service.FreeBoardCommentService;
import edu.og.moa.member.model.dto.Member;
import jakarta.servlet.http.HttpSession;

@RestController
public class FreeCommentController {

   @Autowired
   private FreeBoardCommentService service;

   
   // 댓글 목록 조회
   @GetMapping(value = "/comment", produces = "application/json; charset=UTF-8")
   // 한글 반환 시 인코딩 깨짐 문제 발생 -> produces 속성 작성!
   public List<Comment> select(@RequestParam("boardNo") int boardNo) {
      return service.select(boardNo); // HttpMessageConverter List -> JSON 변환
   }

   // 댓글 삽입
   @PostMapping("/comment")
   public int insert(@RequestBody Comment comment) {
     
      // 요청 데이터(JSON)를 HttpMessageConverter가 해석해서 Java 객체(Comment)에 대입
      return service.insert(comment);
   }

   // 댓글 삭제
   @DeleteMapping("/comment")
   public int delete(@RequestBody Comment comment) {
     
      return service.delete(comment);
   }

   // 댓글 수정
   @PutMapping("/comment")
   public int update(@RequestBody Comment comment) {
      
      return service.update(comment);

   }
}

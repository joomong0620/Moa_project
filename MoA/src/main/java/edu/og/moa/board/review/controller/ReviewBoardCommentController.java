package edu.og.moa.board.review.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import edu.og.moa.board.review.model.dto.ReviewComment;
import edu.og.moa.board.review.model.service.ReviewBoardCommentService;
import edu.og.moa.member.model.dto.Member;
import jakarta.servlet.http.HttpSession;

@RestController
public class ReviewBoardCommentController {

    @Autowired
    private ReviewBoardCommentService service;

    // 댓글 목록 조회
    @GetMapping(value = "/reviewboard/comment", produces = "application/json; charset=UTF-8")
    public List<ReviewComment> selectCommentList(@RequestParam("boardNo") int boardNo) {
        return service.selectCommentList(boardNo);
    }

    // 댓글 등록
    @PostMapping("/reviewboard/comment")
    public int insertComment(@RequestBody ReviewComment comment, HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) return 0;
        comment.setMemberNo(loginMember.getMemberNo());
        return service.insertComment(comment);
    }

    // 댓글 수정
    @PutMapping("/reviewboard/comment")
    public int updateComment(@RequestBody ReviewComment comment, HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) return 0;
        comment.setMemberNo(loginMember.getMemberNo());
        return service.updateComment(comment);
    }

    // 댓글 삭제
    @DeleteMapping("/reviewboard/comment")
    public int deleteComment(@RequestBody ReviewComment comment, HttpSession session) {
        Member loginMember = (Member) session.getAttribute("loginMember");
        if (loginMember == null) return 0;
        comment.setMemberNo(loginMember.getMemberNo());
        return service.deleteComment(comment);
    }
}

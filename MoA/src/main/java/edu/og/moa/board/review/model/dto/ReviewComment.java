package edu.og.moa.board.review.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewComment {

    private int commentNo;             // 댓글 번호
    private int boardNo;               // 게시글 번호
    private int memberNo;              // 작성자 회원번호
    private String commentContent;     // 댓글 내용
    private String createDate;        // 작성일
    private String commentDelFl;       // 삭제 여부

    // MEMBER JOIN
    private String memberNickname;     // 댓글 작성자 닉네임
    private String profileImg;         // 댓글 작성자 프로필 이미지
}

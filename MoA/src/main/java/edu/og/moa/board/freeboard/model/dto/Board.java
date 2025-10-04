package edu.og.moa.board.freeboard.model.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Board {

    private int boardNo;          // 게시글 번호
    private String boardTitle;    // 게시글 제목
    private String boardContent;  // 게시글 내용
    private String boardCreateDate;  // 작성일
    private String boardUpdateDate;  // 수정일
    private int boardCount;        // 조회수
    private String boardDelFl;    // 삭제 여부
    private int memberNo;         // 작성자 회원번호
    private int communityCode;    // 게시판 종류
    private int qCode;            // 문의 코드
    private int boardCode;   // boardCode 필드 추가

    // 서브쿼리용
    private int commentCount;     // 댓글 수
    private int likeCount;        // 좋아요 수 (있다면)

    // 회원 JOIN
    private String memberNickname;
    private String profileImg;
    private String thumbnail; 
    
    // 이미지 목록 (1:N)
    private List<BoardImage> imageList;

    // 댓글 목록 (1:N)
    private List<Comment> commentList;
}

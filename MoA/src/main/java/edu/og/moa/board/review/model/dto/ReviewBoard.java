package edu.og.moa.board.review.model.dto;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewBoard {

    private int boardNo;               // 게시글 번호
    private String boardTitle;         // 게시글 제목
    private String boardContent;       // 게시글 내용
    private String createDate;         // 작성일
    private String bUpdateDate;        // 수정일
    private int boardCount;            // 조회수
    private String boardDelFl;         // 삭제 여부 (Y/N)
    private int memberNo;              // 작성자 회원번호
    private int communityCode;         // 게시판 코드
    private Integer qCode;             // 문의 코드
    
    private String memberNickname;     // 작성자 닉네임
    private String profileImg;         // 프로필 이미지 경로
    private Double avgStar;            // 평균 별점
    private Integer star;              // 개별 리뷰 별점
    private String thumbnailPath;      // 대표 썸네일 이미지 경로
    private String categoryName;       // 게시판 카테고리명
    
    private List<ReviewImage> imageList;   // 첨부 이미지 목록

    private List<ReviewComment> commentList;  // 댓글 목록
    
    private String showTitle;
}

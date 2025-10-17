package edu.og.moa.board.review.model.service;

import java.util.List;
import java.util.Map;

import edu.og.moa.board.review.model.dto.ReviewBoard;
import edu.og.moa.board.review.model.dto.ReviewComment;
import edu.og.moa.board.review.model.dto.ReviewImage;

public interface ReviewBoardService {

    // 리뷰 목록 조회 (페이징 포함)
    Map<String, Object> selectReviewList(int boardCode, int cp);

    // 리뷰 상세 조회
    ReviewBoard selectReviewDetail(Map<String, Object> map);

    // 리뷰 등록
    int insertReviewBoard(ReviewBoard board);
    
    // 리뷰 수정
    int updateReviewBoard(ReviewBoard board);

    // 리뷰 삭제
    int deleteReviewBoard(int reviewNo);

    // 댓글 목록 조회
    List<ReviewComment> selectCommentList(int reviewNo);

    // 댓글 등록
    int insertComment(ReviewComment comment);

    // 댓글 삭제
    int deleteComment(int commentNo);
    
    // 별점
    int upsertStar(int boardNo, int memberNo, String starValue);

    // 게시글 평균 별점
    Double selectAverageStar(int boardNo);
    
    // 이미지 수정
    int insertReviewImage(ReviewImage img);
    
    // 이미지 삭제
	int deleteReviewImage(int imgNo);

	int updateReviewReadCount(int reviewNo);
}

package edu.og.moa.board.review.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import edu.og.moa.board.review.model.dto.ReviewBoard;
import edu.og.moa.board.review.model.dto.ReviewComment;
import edu.og.moa.board.review.model.dto.ReviewImage;

@Mapper
public interface ReviewBoardMapper {

    // 게시판 글 수 조회
    int getListCount(int boardCode);

    // 게시판 목록 조회 (RowBounds로 페이징)
    List<ReviewBoard> selectReviewList(int boardCode, RowBounds rowBounds);

    // 게시글 상세 조회
    ReviewBoard selectReviewDetail(Map<String, Object> map);

    // 게시글 등록
    int insertReviewBoard(ReviewBoard board);

    // 게시글 수정
    int updateReviewBoard(ReviewBoard board);

    // 게시글 삭제
    int deleteReviewBoard(int reviewNo);

    // 댓글 목록 조회
    List<ReviewComment> selectCommentList(int reviewNo);

    // 댓글 등록
    int insertComment(ReviewComment comment);

    // 댓글 삭제
    int deleteComment(int commentNo);

    // 별점 확인
    int checkStarExists(int boardNo, int memberNo);

    // 별점 등록
    int insertStar(int boardNo, int memberNo, String starValue);

    // 별점 수정
    int updateStar(int boardNo, int memberNo, String starValue);

    // 평균 별점 조회
    Double selectAverageStar(int boardNo);
    
    int updateReviewReadCount(int reviewNo);

    // 별점 수정
	int upsertStar(Map<String, Object> paramMap);

	// 이미지 수정
	int insertReviewImage(ReviewImage img);

	// 이미지 삭제
	int deleteReviewImage(int imgNo);
    
    
}

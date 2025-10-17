package edu.og.moa.board.review.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.og.moa.board.freeboard.model.dto.Pagination;
import edu.og.moa.board.review.model.dao.ReviewBoardMapper;
import edu.og.moa.board.review.model.dto.ReviewBoard;
import edu.og.moa.board.review.model.dto.ReviewComment;
import edu.og.moa.board.review.model.dto.ReviewImage;

@Service
public class ReviewBoardServiceImpl implements ReviewBoardService {

    @Autowired
    private ReviewBoardMapper mapper;

    // 리뷰 목록 조회
    @Override
    public Map<String, Object> selectReviewList(int boardCode, int cp) {

        // 전체 게시글 수 조회
        int listCount = mapper.getListCount(boardCode);

        // Pagination 객체 생성
        Pagination pagination = new Pagination(cp, listCount);

        // offset 계산
        int offset = (pagination.getCurrentPage() - 1) * pagination.getLimit();

        // RowBounds 생성
        RowBounds rowBounds = new RowBounds(offset, pagination.getLimit());

        // 현재 페이지 게시글 목록 조회
        List<ReviewBoard> boardList = mapper.selectReviewList(boardCode, rowBounds);

        // 반환 데이터 구성
        Map<String, Object> map = new HashMap<>();
        map.put("pagination", pagination);
        map.put("boardList", boardList);

        return map;
    }

    // 리뷰 상세 조회
    @Override
    public ReviewBoard selectReviewDetail(Map<String, Object> map) {
        return mapper.selectReviewDetail(map);
    }

    // 리뷰 등록
    @Override
    public int insertReviewBoard(ReviewBoard board) {
        int result = mapper.insertReviewBoard(board);

        if (result > 0 && board.getStar() != null && board.getStar() > 0) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("boardNo", board.getBoardNo());
            paramMap.put("memberNo", board.getMemberNo());
            paramMap.put("starValue", board.getStar());
            mapper.upsertStar(paramMap);
        }

        // 이미지 저장 로직 수정
        if (result > 0 && board.getImageList() != null && !board.getImageList().isEmpty()) {
            for (int i = 0; i < board.getImageList().size(); i++) {
                ReviewImage img = board.getImageList().get(i);
                img.setBoardNo(board.getBoardNo());
                img.setImgOrder(i);
                System.out.println("안녕하세");
                System.out.println(img);
                mapper.insertReviewImage(img);
            }
        }

        return result;
    }


    // 리뷰 수정
    @Override
    public int updateReviewBoard(ReviewBoard board) {
        return mapper.updateReviewBoard(board);
    }

    // 리뷰 삭제
    @Override
    public int deleteReviewBoard(int reviewNo) {
        return mapper.deleteReviewBoard(reviewNo);
    }

    // 댓글 목록 조회
    @Override
    public List<ReviewComment> selectCommentList(int reviewNo) {
        return mapper.selectCommentList(reviewNo);
    }

    // 댓글 등록
    @Override
    public int insertComment(ReviewComment comment) {
        return mapper.insertComment(comment);
    }

    // 댓글 삭제
    @Override
    public int deleteComment(int commentNo) {
        return mapper.deleteComment(commentNo);
    }

    // 별점 등록/수정
    @Override
    public int upsertStar(int boardNo, int memberNo, String starValue) {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("boardNo", boardNo);
        paramMap.put("memberNo", memberNo);
        paramMap.put("starValue", starValue);

        return mapper.upsertStar(paramMap);
    }

    // 평균 별점 조회
    @Override
    public Double selectAverageStar(int boardNo) {
        return mapper.selectAverageStar(boardNo);
    }

   // 이미지 수
    @Override
    public int insertReviewImage(ReviewImage img) {
        return mapper.insertReviewImage(img);
    }

    // 조회수
	@Override
	public int updateReviewReadCount(int reviewNo) {
		return mapper.updateReviewReadCount(reviewNo);
	}
	
	// 이미지 삭제
	@Override
	public int deleteReviewImage(int imgNo) {
	    return mapper.deleteReviewImage(imgNo);
	}
}

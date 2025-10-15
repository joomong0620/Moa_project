package edu.og.moa.board.performance.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import edu.og.moa.board.performance.model.dto.PerformanceBoard;
import edu.og.moa.board.performance.model.dto.PerformanceBoardImage;

@Mapper
public interface PerformanceMapper {

	// 게시글 중에서 커뮤니티 코드가 4인 삭제되지 않은 게시글 수 조회
	public int getPmListCount(String type);

	// 현재 페이지에 해당하는 부분에 대한 게시글 목록 조회
	public List<PerformanceBoard> selectPmTypeList(String type, RowBounds rowBounds);

	// 상세 페이지
	public PerformanceBoard selectPmDetail(Map<String, Object> map);

	// 좋아요 여부
	public int boardLikeCheck(Map<String, Object> map);

	// 조회수 증가 서비스
	public int updateReadCount(int boardNo);

	// 좋아요 추가
	public int insertBoardLike(Map<String, Integer> paramMap);

	// 좋아요 삭제
	public int deleteBoardLike(Map<String, Integer> paramMap);

	// 공연 이미지 썸네일 조회(KSY)
	public List<PerformanceBoardImage> selectPerformanceList();
}

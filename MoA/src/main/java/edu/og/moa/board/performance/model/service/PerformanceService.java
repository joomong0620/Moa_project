package edu.og.moa.board.performance.model.service;

import java.util.Map;

import edu.og.moa.board.performance.model.dto.PerformanceBoard;

public interface PerformanceService {

	// 공연 장르별 목록 조회
	Map<String, Object> selectPmTypeList(String type, int cp);

	// 상세 페이지
	PerformanceBoard selectPmDetail(Map<String, Object> map);

	// 좋아요 여부
	int boardLikeCheck(Map<String, Object> map);

	// 조회수 증가 서비스
	int updateReadCount(int boardNo);

	// 좋아요 처리
	int like(Map<String, Integer> paramMap);
	
	
	
}

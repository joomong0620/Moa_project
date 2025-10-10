package edu.og.moa.board.performance.model.service;

import java.util.Map;

public interface PerformanceService {

	// 공연 장르별 목록 조회
	Map<String, Object> selectPmTypeList(int type, int cp);
	
	
	
}

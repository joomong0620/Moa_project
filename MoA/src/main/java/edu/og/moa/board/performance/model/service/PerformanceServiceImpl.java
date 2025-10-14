package edu.og.moa.board.performance.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.og.moa.board.freeboard.model.dto.Pagination;
import edu.og.moa.board.performance.model.dao.PerformanceMapper;
import edu.og.moa.board.performance.model.dto.PerformanceBoard;

@Service
public class PerformanceServiceImpl implements PerformanceService{

	@Autowired
	private PerformanceMapper mapper;

	
	// 공연 장르별 목록 조회
	@Override
	public Map<String, Object> selectPmTypeList(String type, int cp) {
		
		// 게시글 중에서 커뮤니티 코드가 4인 삭제되지 않은 게시글 수 조회
		int pmListCount = mapper.getPmListCount(type);
		
		// Pagination 객체 생성
		Pagination pagination = new Pagination(cp, pmListCount);
		
		// 1) offset 계산
		int offset = (pagination.getCurrentPage() - 1) * pagination.getLimit();
		
		// 2) RowBounds 객체 생성
		RowBounds rowBounds = new RowBounds(offset, pagination.getLimit());
		
		// 목록으로 가져오기
		List<PerformanceBoard> pmTypeList = mapper.selectPmTypeList(type, rowBounds);
		
		// 4. pagination, pmTypeList를 Map 담아서 반환
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pagination", pagination);
		map.put("pmTypeList", pmTypeList);
		
		return map;
	}


	// 상세 페이지
	@Override
	public PerformanceBoard selectPmDetail(Map<String, Object> map) {
		return mapper.selectPmDetail(map);
	}

	// 좋아요 여부
	@Override
	public int boardLikeCheck(Map<String, Object> map) {
		return mapper.boardLikeCheck(map);
	}

	
	// 조회수 증가 서비스
	@Override
	public int updateReadCount(int boardNo) {
		return mapper.updateReadCount(boardNo);
	}

	
	// 좋아요 처리
	@Override
	public int like(Map<String, Integer> paramMap) {
		
		int result = 0;
		
		if (paramMap.get("check") == 0) {
			// 좋아요 한 적이 없을 때
			
			result = mapper.insertBoardLike(paramMap);
		} else {
			// 좋아요 한 적이 있을 때
			result = mapper.deleteBoardLike(paramMap);
		}
		
		// 에러날 경우
		if(result == 0) return -1;
		
		return 0;
	}
	
}

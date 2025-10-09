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
	public Map<String, Object> selectPmTypeList(int type, int cp) {
		
		// 게시글 중에서 커뮤니티 코드가 4인 삭제되지 않은 게시글 수 조회
		int pmListCount = mapper.getPmListCount(type);
		
		Pagination pagination = new Pagination(cp, pmListCount);
		
		int offset = (pagination.getCurrentPage() - 1) * pagination.getLimit();
		
		RowBounds rowBounds = new RowBounds(offset, pagination.getLimit());
		
		List<PerformanceBoard> pmTypeList = mapper.selectPmTypeList(type, rowBounds);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pagination", pagination);
		map.put("pmTypeList", pmTypeList);
		
		return map;
	}
	
}

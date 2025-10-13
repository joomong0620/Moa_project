package edu.og.moa.board.performance.model.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import edu.og.moa.board.performance.model.dto.PerformanceBoard;

@Mapper
public interface PerformanceMapper {

	// 게시글 중에서 커뮤니티 코드가 4인 삭제되지 않은 게시글 수 조회
	public int getPmListCount(String type);

	// 현재 페이지에 해당하는 부분에 대한 게시글 목록 조회
	public List<PerformanceBoard> selectPmTypeList(String type, RowBounds rowBounds);
	
	

}

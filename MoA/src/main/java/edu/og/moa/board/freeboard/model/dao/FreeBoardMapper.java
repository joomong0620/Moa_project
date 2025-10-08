package edu.og.moa.board.freeboard.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import edu.og.moa.board.freeboard.model.dto.Board;

@Mapper
public interface FreeBoardMapper {

	//게시판 종류 조회
	public List<Map<String, Object>> selectBoardTypeList();

	// 게시판의 삭제되지 않은 게시글 수 조회
	public int getListCount(int boardCode);

	// 게시판에서 현재 페이징 해당하는 부분에 대한 게시글 목록 조회
	public List<Board> selectFreeBoardList(int boardCode, RowBounds rowBounds);


}

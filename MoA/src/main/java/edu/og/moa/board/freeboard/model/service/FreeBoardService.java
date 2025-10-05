package edu.og.moa.board.freeboard.model.service;

import java.util.List;
import java.util.Map;



public interface FreeBoardService {
	
	
	/** 게시판 종류 조회
	 * @return
	 */
	List<Map<String, Object>> selectBoardTypeList();
	
	/** 자유게시글 목록 조회
	 * @param boardCode
	 * @param cp
	 * @return
	 */
	Map<String, Object> selectFreeBoardList(int boardCode, int cp);

	

}

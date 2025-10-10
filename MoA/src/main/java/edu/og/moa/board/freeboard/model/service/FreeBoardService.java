package edu.og.moa.board.freeboard.model.service;

import java.util.List;
import java.util.Map;

import edu.og.moa.board.freeboard.model.dto.Board;



public interface FreeBoardService {
	
	
	/** 게시판 종류 조회
	 * @return
	 */
	List<Map<String, Object>> selectBoardTypeList();
	


	/** 자유게시판 목록 조회
	 * @param boardCode
	 * @param cp
	 * @return
	 */
	Map<String, Object> selectFreeBoardList(int boardCode, int cp);

	
	/** 자유게시판 상세 조회 
	 * @param boardNo
	 * @return
	 */
	Board selectFreeBoardDetail(Map<String, Object> map);

	
	/** 좋아요 여부 확인
	 * @param map
	 * @return
	 */
	int boardLikeCheck(Map<String, Object> map);
	
	/** 좋아요 처리 서비스
	 * @param paramMap
	 * @return count
	 */
	int like(Map<String, Integer> paramMap);


	/** 조회수 증가
	 * @param boardNo
	 * @return count
	 */
	int updateReadCount(int boardNo);

	/** DB 이미지(파일) 목록 조회
	 * @return list
	 */
	List<String> selectImageList();

	

	

}

package edu.og.moa.board.exhibition.model.service;

import java.util.List;
import java.util.Map;

import edu.og.moa.board.exhibition.model.dto.AuthorDB;
import edu.og.moa.board.exhibition.model.dto.BoardImgDB;
import edu.og.moa.board.exhibition.model.dto.Exhibition;

public interface ExhibitionService {
	
	/** 전시게시글 목록조회
	 * @param communityCode
	 * @param cp
	 * @return map
	 */
	Map<String, Object> selectExhibitionList(int communityCode, int cp);

	
	/** 전시게시글 상세조회
	 * @param map
	 * @return result
	 */
	Exhibition selectExhibition(Map<String, Object> map);
	
	
//	/** DB 이미지 조회
//	 * @return
//	 */
//	List<BoardImgDB> selectImageList(); // Exhibition DTO select시에 그안에서 implicit하게 수행되므로 여기서 선언하면 안됨
//	
//	/** AUTHOR DB 조회
//	 * @return
//	 */
//	List<AuthorDB> selectAuthorList(); // Exhibition DTO select시에 그안에서 implicit하게 수행되므로 여기서 선언하면 안됨
	
	/** DB 이미지(파일) 목록 조회
	 * @return
	 */
	List<String> selectImageListAll();	
	
	/** DB 이미지 조회
	 * @return
	 */
	List<BoardImgDB> selectImageListIndep();
	
	/** AUTHOR DB 조회
	 * @return
	 */
	List<AuthorDB> selectAuthorListIndep();	
	
	/** 검색용 전시게시글 목록조회
	 * @param paramMap
	 * @param cp
	 * @return map
	 */
	Map<String, Object> selectExhibitionList(Map<String, Object> paramMap, int cp);


	/** 좋아요 여부 확인
	 * @param map
	 * @return
	 */
	int exhibitionLikeCheck(Map<String, Object> map);


	/** 조회수 증가
	 * @param boardNo
	 * @return
	 */
	int updateReadCount(int boardNo);





}

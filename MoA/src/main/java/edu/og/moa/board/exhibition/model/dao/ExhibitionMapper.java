package edu.og.moa.board.exhibition.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.RowBounds;

import edu.og.moa.board.exhibition.model.dto.AuthorDB;
import edu.og.moa.board.exhibition.model.dto.BoardDB;
import edu.og.moa.board.exhibition.model.dto.BoardImgDB;
import edu.og.moa.board.exhibition.model.dto.Exhibition;

@Mapper
public interface ExhibitionMapper {

	
	// 특정 게시판의 삭제되지 않은 게시글 수 조회
	public int getListCount(int communityCode);

	// 특정 게시판에서 현재 페이지에 해당하는 부분에 대한 게시글 목록 조회
	//public List<BoardDB> selectExhibitionList(int communityCode, RowBounds rowBounds);
	public List<Exhibition> selectExhibitionList(int communityCode, RowBounds rowBounds);

	// 전시게시글 상세조회 
	public Exhibition selectExhibition(Map<String, Object> map);
	
	// public List<BoardImgDB> selectImageList(); // Exhibition DTO select시에 그안에서 implicit하게 수행되므로 여기서 선언하면 안됨
	// public List<AuthorDB> selectAuthorList(); // Exhibition DTO select시에 그안에서 implicit하게 수행되므로 여기서 선언하면 안됨	
	
	// DB 이미지(파일) 목록 조회
	public List<String> selectImageListAll();

	// DB 이미지 조회 (개별/명시적)
	public List<BoardImgDB> selectImageListIndep();
	
	// AUTHOR DB 조회 (개별/명시적)
	public List<AuthorDB> selectAuthorListIndep();	
	
	// 좋아요 여부 확인
	public int exhibitionLikeCheck(Map<String, Object> map);	
	
	
	
	
	// 게시글 수 조회(검색)
	public int getSearchListCount(Map<String, Object> paramMap);

	// 게시글 목록 조회(검색) 
	public List<Exhibition> selectSearchExhibitionList(Map<String, Object> paramMap, RowBounds rowBounds);
	

	// 조회수 증가
	public int updateReadCount(int boardNo);




	                 




}

package edu.og.moa.board.exhibition.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.og.moa.board.exhibition.model.dao.ExhibitionMapper;
import edu.og.moa.board.exhibition.model.dto.AuthorDB;
import edu.og.moa.board.exhibition.model.dto.BoardDB;
import edu.og.moa.board.exhibition.model.dto.BoardImgDB;
import edu.og.moa.board.exhibition.model.dto.Exhibition;
import edu.og.moa.board.exhibition.model.dto.JsonBoardImage;
import edu.og.moa.board.exhibition.model.dto.PaginationDB;

@Service
public class ExhibitionServiceImpl implements ExhibitionService{

	@Autowired
	private ExhibitionMapper mapper;
	
	// 전시게시글 목록조회
	@Override
	public Map<String, Object> selectExhibitionList(int communityCode, int cp) {
	      // 1. 특정 게시판의 삭제되지 않은 게시글 수 조회
	      int listCount = mapper.getListCount(communityCode);

	      // 2. 1번의 조회 결과 + cp를 이용해서 Pagination 객체 생성
	      // -> 내부 필드에 계산된 값이 초기화됨
	      PaginationDB pagination = new PaginationDB(cp, listCount);

	      // 3. 특정 게시판에서 현재 페이지에 해당하는 부분에 대한 게시글 목록 조회
	      // -> 어떤 게시판(boardCode)에서
	      //    몇 페이지(pagination.currentPage)에 대한
	      //    게시글 몇 개 (pagination.limit) 조회

	      // 1) offset 계산
	      int offset = (pagination.getCurrentPage() - 1) * pagination.getLimit();

	      // 2) RowBounds 객체 생성
	      RowBounds rowBounds = new RowBounds(offset, pagination.getLimit());

	      List<BoardDB> exhibitionList = mapper.selectExhibitionList(communityCode, rowBounds); 

	      // 4. pagination, boardList를 Map에 담아서 반환
	      Map<String, Object> map = new HashMap<String, Object>();
	      map.put("pagination", pagination);
	      map.put("exhibitionList", exhibitionList);
	      
	      // current, future, past로  exhibitionList를 그룹핑해서 역시 map에 담아 던져라
	      // (exhibitionListCurrent, exhibitionListFuture, exhibitionListPast) => 카드목록 조회에 사용
	      // to-do
	      return map;
	   }

	
	// 검색용 전시게시글 목록조회
	@Override
	public Map<String, Object> selectExhibitionList(Map<String, Object> paramMap, int cp) {
	      // 1. 특정 게시판의 삭제되지 않고 검색 조건이 일치하는 게시글 수 조회
	      //int listCount = mapper.getListCount(paramMap);
	      int listCount = mapper.getSearchListCount(paramMap);

	      // 2. 1번의 조회 결과 + cp를 이용해서 Pagination 객체 생성
	      // -> 내부 필드에 계산된 값이 초기화됨 
	      PaginationDB pagination = new PaginationDB(cp, listCount);

	      // 3. 특정 게시판에서 현재 페이지에 해당하는 부분에 대한 게시글 목록 조회
	      // + 검색 조건이 일치하는 글만 조회

	      int offset = (pagination.getCurrentPage() - 1) * pagination.getLimit();

	      RowBounds rowBounds = new RowBounds(offset, pagination.getLimit());


	      List<BoardDB> exhibitionList = mapper.selectSearchExhibitionList(paramMap, rowBounds);

	      // 4. pagination, boardList를 Map에 담아서 반환
	      Map<String, Object> map = new HashMap<String, Object>();
	      map.put("pagination", pagination);
	      map.put("exhibitionList", exhibitionList);

	      return map;
	}


	// 전시게시글 상세조회
	@Override
	public Exhibition selectExhibition(Map<String, Object> map) {
		// 
		return mapper.selectExhibition(map);
	}

	
//	// DB 이미지 조회 
//	@Override
//	public List<BoardImgDB> selectImageList() { // Exhibition DTO select시에 그안에서 implicit하게 수행되므로 여기서 선언하면 안됨
//		return mapper.selectImageList();
//	}	
//
//	// AUTHOR DB 조회
//	@Override
//	public List<AuthorDB> selectAuthorList() { // Exhibition DTO select시에 그안에서 implicit하게 수행되므로 여기서 선언하면 안됨
//		return mapper.selectAuthorList();
//	}	
//		
	// DB 이미지(파일) 목록 조회
	@Override
	public List<String> selectImageListAll() {
		// 
		return mapper.selectImageListAll();
	}

	// DB 이미지 조회
	@Override
	public List<BoardImgDB> selectImageListIndep() {
		return mapper.selectImageListIndep();
	}	

	// AUTHOR DB 조회
	@Override
	public List<AuthorDB> selectAuthorListIndep() {
		return mapper.selectAuthorListIndep();
	}


	// 좋아요 여부 확인
	@Override
	public int exhibitionLikeCheck(Map<String, Object> map) {
		// 
		return mapper.exhibitionLikeCheck(map);
	}


	// 조회수 증가
	@Override
	public int updateReadCount(int boardNo) {
		// 
		return mapper.updateReadCount(boardNo);
	}
	
}

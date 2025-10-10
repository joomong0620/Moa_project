package edu.og.moa.board.freeboard.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.og.moa.board.freeboard.model.dao.FreeBoardMapper;
import edu.og.moa.board.freeboard.model.dto.Board;
import edu.og.moa.board.freeboard.model.dto.Pagination;

@Service
public class FreeBoardServiceImpl implements FreeBoardService {

	@Autowired
	private FreeBoardMapper mapper;
	
	// 게시판 종류 조회
	@Override
	public List<Map<String, Object>> selectBoardTypeList() {
		return mapper.selectBoardTypeList();
	}
	
	// 게시판 목록 조회
	@Override
	public Map<String, Object> selectFreeBoardList(int boardCode, int cp) {

		int listCount = mapper.getListCount(boardCode);

		Pagination pagination = new Pagination(cp, listCount);

		// 1) offset 계산
		int offset = (pagination.getCurrentPage() - 1) * pagination.getLimit();

		// 2) RowBounds 객체 생성
		RowBounds rowBounds = new RowBounds(offset, pagination.getLimit());
		
		List<Board> boardList = mapper.selectFreeBoardList(boardCode, rowBounds);
		
		
		// 4. pagination, boardList를 Map에 담아서 반환
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pagination", pagination);
		map.put("boardList", boardList);
		
		
		return map;
	}

	// 게시판 상세 조회
	@Override
	public Board selectFreeBoardDetail(Map<String, Object> map) {
		return mapper.selectFreeBoardDetail(map);
	}

	//좋아요 여부 확인
	@Override
	public int boardLikeCheck(Map<String, Object> map) {
		return mapper.boardLikeCheck(map);
	}
	
	
	// 좋아요 처리
	@Override
	public int like(Map<String, Integer> paramMap) {
		int result = 0;

		if(paramMap.get("check") == 0) { // 좋아요 X 상태
			result = mapper.insertBoardLike(paramMap);

		} else { 
			result = mapper.deleteBoardLike(paramMap);
		}

		if(result == 0) return -1;

		return mapper.countBoardLike(paramMap.get("boardNo"));
	}

	//조회수 증가
	@Override
	public int updateReadCount(int boardNo) {
		return mapper.updateReadCount(boardNo);
	}

	// DB 이미지 파일 목록 조회
	@Override
	public List<String> selectImageList() {
		return mapper.selectImageListAll();
	}


	
	

}

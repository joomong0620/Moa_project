package edu.og.moa.board.freeboard.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.og.moa.board.freeboard.model.dto.Board;
import edu.og.moa.board.freeboard.model.dto.BoardImage;

@Mapper
public interface FreeBoardMapper2 {

	// 게시글 삽입
	int FreeboardInsert(Board board);

	// 이미지 리스트 (1~5개) 삽입
	int insertImageList(List<BoardImage> uploadList);

	// 게시글 수정
	int FreeboardUpdate(Board board);

	// deleteList에 존재하는지 확인
	int FreecheckImage(Map<String, Object> deleteMap);

	// 게시글 이미지 삭제
	int FreeimageDelete(Map<String, Object> deleteMap);

	// 이미지 수정
	int imageUpdate(BoardImage img);

	// 이미지 삽입
	int imageInsert(BoardImage img);

	// 게시글 삭제
	int FreeboardDelete(int boardNo);
	
	

}

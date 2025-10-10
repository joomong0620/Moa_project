package edu.og.moa.board.exhibition.model.dao;

import edu.og.moa.board.exhibition.model.dto.Board;

public interface JsonExhibitionMapper {

	// json 데이터 BOARD 테이블에 insert
	public int jsonBoardInsert(Board board); // 로직은 JsonExhibitionServiceImpl.java로  

}

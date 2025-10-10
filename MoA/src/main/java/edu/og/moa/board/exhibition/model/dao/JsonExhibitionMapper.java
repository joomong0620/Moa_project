package edu.og.moa.board.exhibition.model.dao;

import edu.og.moa.board.exhibition.model.dto.BoardDB;

public interface JsonExhibitionMapper {

	// json 데이터 BOARD 테이블에 insert
	public int jsonBoardInsert(BoardDB board); // 로직은 JsonExhibitionServiceImpl.java로  

}

package edu.og.moa.board.exhibition.model.service;

import java.io.IOException;

import edu.og.moa.board.exhibition.model.dto.BoardDB;

public interface JsonExhibitionService {

	// json 데이터 BOARD 테이블에 insert
	int jsonBoardInsert(BoardDB board) throws IllegalStateException, IOException;

	


}

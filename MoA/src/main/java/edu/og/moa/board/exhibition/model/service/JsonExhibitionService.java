package edu.og.moa.board.exhibition.model.service;

import java.io.IOException;

import edu.og.moa.board.exhibition.model.dto.Board;

public interface JsonExhibitionService {

	// json 데이터 BOARD 테이블에 insert
	int jsonBoardInsert(Board board) throws IllegalStateException, IOException;

	


}

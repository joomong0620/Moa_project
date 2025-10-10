package edu.og.moa.board.freeboard.model.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.og.moa.board.freeboard.model.dto.Board;

public interface FreeBoardService2 {

	/** 게시글 삽입
	 * @param board
	 * @param images
	 * @return boardNo
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	int FreeboardInsert(Board board, List<MultipartFile> 
	images) throws IllegalStateException, IOException;

	/** 게시글 수정
	 * @param board
	 * @param images
	 * @param deleteList
	 * @return
	 * @throws IOException 
	 * @throws IllegalStateException 
	 */
	int FreeboardUpdate(Board board, List<MultipartFile> images, String deleteList) throws IllegalStateException, IOException;

	
	
	/** 게시글 삭제
	 * @param boardNo
	 * @return return
	 */
	int FreeboardDelete(int boardNo);

}

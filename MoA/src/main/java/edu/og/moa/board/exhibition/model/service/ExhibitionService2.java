package edu.og.moa.board.exhibition.model.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import edu.og.moa.board.exhibition.model.dto.Exhibition;

public interface ExhibitionService2 {
	
	
	
	/** 게시글 삭제
	 * @param map
	 * @return result (0:실패, 1:성공)
	 */
	int exhibitionDelete(int exhibitNo);

	
	/** 게시글 삽입
	 * @param exhibition
	 * @param images
	 * @param webPath
	 * @param filePath
	 * @return boardNo // insert하는 보드넘버 가져오겠다 (boardNo === exhibitNo )
	 */
	//int exhibtionInsert(Exhibition exhibition, List<MultipartFile> images, String webPath, String filePath) throws IllegalStateException, IOException;
	int exhibtionInsert(Exhibition exhibition, List<MultipartFile> images) throws IllegalStateException, IOException;



	/** 게시글 수정
	 * @param exhibition
	 * @param images
	 * @param deleteList
	 * @return result (성공한 행의 갯수: 0 실패, 1 성공)
	 */
	int exhibitionUpdate(Exhibition exhibition, List<MultipartFile> images, String deleteList) throws IllegalStateException, IOException;

}

package edu.og.moa.board.freeboard.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString

public class BoardImage {
	 	private int imgNo;        // 이미지 번호 (SEQ_IMAGE_NO)
	    private int boardNo;      // 게시글 번호 (SEQ_BOARD_NO)
	    private String imgPath;   // 이미지 경로
	    private String imgOrig;   // 원본 이미지명
	    private String imgRename; // 변경된 이미지명
	    private int imgOrder;     // 이미지 순서 번호
	
}

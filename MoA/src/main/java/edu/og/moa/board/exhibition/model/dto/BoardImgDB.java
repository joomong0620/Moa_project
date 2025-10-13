package edu.og.moa.board.exhibition.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardImgDB {
	
	private int imgNo;
	private int boardNo; // boardNo === exhibitNo
	private String imgPath;
	private String imgOrig;
	private String imgRename;
	private int imgOrder;
	
}

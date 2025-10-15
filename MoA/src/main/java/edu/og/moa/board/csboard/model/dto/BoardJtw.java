package edu.og.moa.board.csboard.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class BoardJtw {
	
	private int boardNo;
	private String boardTitle;
	private String boardContent;
	private String boardCreateDate;
	private String boardUpdateDate;
	private String boardCount;
	private String boardDelFl;
	private int memberNo;
	private int communityCode;
	private int qCode;
	
	
	// BoardType join
	private String communityName;
	
	// Question join
	private String qName;
	

	
	
	

}

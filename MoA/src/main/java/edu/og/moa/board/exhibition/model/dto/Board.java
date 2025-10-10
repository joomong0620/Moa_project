package edu.og.moa.board.exhibition.model.dto;


import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Board {
	private int boardNo;
	private String boardTitle;
	private String boardContent;
	private String bCreateDate;
	private String bUpdateDate;
	private int boardCount; // 게시글 조회수
	private String boardDelFl; 
	private int memberNo;
	private int communityCode;
	private int qCode;
	
}


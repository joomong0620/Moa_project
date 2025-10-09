package edu.og.moa.board.performance.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PerformanceComment {

	private int commentNo;
	private int memberNo;
	private int boardNo;
	private String cCreateDate;
	private String commentContent;
	private String commentDelFl;
	
	// 부모 댓글 번호
	private int pCommnet;
	
	
}

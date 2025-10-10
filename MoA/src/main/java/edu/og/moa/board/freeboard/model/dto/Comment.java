package edu.og.moa.board.freeboard.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Comment {
	
	private int commentNo;
	private int memberNo;
	private int boardNo;
	private String commentCreateDate;
	private String commentContent;
	private String commentDeleteFlag;
	private int parentNo;
	private String memberNickname;
	private String profileImage;
	
}
package edu.og.moa.board.exhibition.model.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Exhibition {
	private int boardNo; // 개설글 번호
	private String exhibitSubTitle;
	private String exhibitDate;
	private String exhibitContact;
	private String exhibitAudience;
	private int exhibitCharge;
	private int institutionNo;
	private int genreNo;
}

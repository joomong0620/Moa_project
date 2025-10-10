package edu.og.moa.board.exhibition.model.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Contributor {
	private int contributorNo;
	private String exhibitHost;
	private String exhibitSupport;
	private int boardNo; // 개설글 번호
}

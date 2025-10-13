package edu.og.moa.board.exhibition.model.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ContributorDB {
	private int contributorNo;
	private String exhibitHost;
	private String exhibitSupport;
	private int boardNo; // 개설글 번호
}

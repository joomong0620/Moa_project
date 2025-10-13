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
public class AuthorDB {
	private int authorNo; // // DB에서 자동 증가 by SEQ_AUTHOR_NO
	private String authorName;
	private int boardNo;
}

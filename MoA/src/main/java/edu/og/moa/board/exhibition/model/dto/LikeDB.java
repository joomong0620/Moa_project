package edu.og.moa.board.exhibition.model.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LikeDB {
	private int boardNo; 	// 게시글번호
	private int memberNo; 	// 회원번호
}

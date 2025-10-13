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
public class LikeDB {
	private int boardNo; 	// 게시글번호
	private int memberNo; 	// 회원번호
}

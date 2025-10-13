package edu.og.moa.board.exhibition.model.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChattingRoomDB {
	private int chattingNo;
	private String chCreateDate;
	private int openMember; // 개설자 번호 === memberNo
}

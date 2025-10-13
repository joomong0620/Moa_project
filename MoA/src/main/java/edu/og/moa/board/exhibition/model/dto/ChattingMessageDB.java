package edu.og.moa.board.exhibition.model.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChattingMessageDB {
	private int messageNo;
	private String messageContent;
	private String readFl;
	private String sendTime;
	private int chattingNo;
	private int memberNo;
}

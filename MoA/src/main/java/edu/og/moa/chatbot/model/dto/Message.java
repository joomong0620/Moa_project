package edu.og.moa.chatbot.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Message { // chatting-mapper.xml에 있는 sql구문의 조회 값들...(resutMap필요)

	private int messageNo;
	private String messageContent;
	private String readFlag;
	private int senderNo;
	private int targetNo;
	private int chattingNo;
	private String sendTime;
	
}
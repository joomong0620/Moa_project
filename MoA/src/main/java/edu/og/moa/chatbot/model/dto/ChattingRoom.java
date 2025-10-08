package edu.og.moa.chatbot.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChattingRoom { // chatting-mapper.xml에 있는 sql구문의 조회 값들...(resutMap필요)
	private int chattingNo;
	private String lastMessage;
	private String sendTime;
	private int targetNo;
	private String targetNickName;
	private String targetProfile;
	private int notReadCount;
}


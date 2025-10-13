package edu.og.moa.chatting.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Messagekjy {
	
	// CHATTING_MESSAGE 테이블 컬럼
    private int messageNo;           // 메시지 번호
    private String messageContent;   // 메시지 내용
    private String readFlag;         // 읽음 여부 (Y/N)
    private String sendTime;         // 보낸 시간
    private int chattingNo;          // 채팅방 번호
    private int memberNo;            // 보낸 사람 회원번호
    
    // WebSocket 메시지 전송/수신 시 사용
    private int senderNo;            // 보낸 사람 번호
    private String senderName;       // 보낸 사람 이름
    private String senderProfile;    // 보낸 사람 프로필
    private int targetNo;            // 받는 사람 번호 (개인채팅용)
}

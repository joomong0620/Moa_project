package edu.og.moa.chatting.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class chattingMemberkjy {
	 // CHAT_MEMBER 테이블 컬럼
    private int chattingNo;          // 채팅방 번호
    private int memberNo;            // 회원번호
    
    // 채팅방 참여자 목록 조회 시 필요한 정보
    private String memberNickname;   // 회원 닉네임
    private String profileImage;     // 프로필 이미지
    private String memberEmail;      // 이메일
}

package edu.og.moa.chatting.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class chattingRoomkjy {
	
	// CHATTING_ROOM 테이블 컬럼
    private int chattingNo;          // 채팅방 번호
    private String chCreateDate;     // 채팅방 생성일
    private int openMember;          // 개설자 회원번호
    
    // 팀 채팅방 생성 요청 시 사용
    private String roomName;         // 팀 채팅방 이름
    private List<Integer> memberList; // 참여자 목록
    
    // 채팅방 목록 조회 시 필요한 정보
    private int memberCount;         // 참여 인원 수 (2명=개인, 3명이상=팀)
    private int targetNo;            // 상대방 회원번호 (개인채팅용, memberCount=2일때만)
    private String targetNickName;   // 상대방 닉네임
    private String targetProfile;    // 상대방 프로필 이미지
    private String lastMessage;      // 마지막 메시지
    private String sendTime;         // 마지막 메시지 시간
    private int notReadCount;        // 읽지 않은 메시지 수
    private String roomImage;        // 채팅방 이미지 (개인=상대프로필, 팀=그룹이미지)   // 팀 채팅방 이미지
}

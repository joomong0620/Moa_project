package edu.og.moa.chatting.model.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.og.moa.chatting.model.dto.chattingRoomkjy;
import edu.og.moa.chatting.model.dto.Messagekjy;
import edu.og.moa.member.model.dto.Member;

@Mapper
public interface ChattingMapper {
	
	//메세지 삽입
	public int insertMessage(Messagekjy msg);

	
	
	// 채팅 목록 조회
	public List<chattingRoomkjy> selectRoomList(int memberNo);


	// 채팅 상대 조회
	public List<Member> selectTarget(Map<String, Object> map);



	// 기존 채팅방 조회(개인)
	public Integer checkChattingNo(Map<String, Integer> map);


	// 없으면 새로운 채팅방 생성(개인)
	public void insertPersonalChatRoom(Map<String, Integer> map);


	// 두 명 모두 CHAT_MEMBER 테이블에 등록(개인)
	public int insertChatMember(Map<String, Object> param1);


	// 팀 채팅방 생성
	public void createTeamRoom(Map<String, Object> roomMap);


	// 현재 팀 채팅방 no
	public int getCurrentChattingNo();


	// 멤버 전체를 CHAT_MEMBER에 등록
	public int insertTeamMember(Map<String, Object> param);


	// 채팅 읽음 표시
	public int updateReadFlag(Map<String, Object> paramMap);

	
	// 채팅방 메세지 목록 조회
	public List<Messagekjy> selectMessageList(int parseInt);


    // 채팅방 나가기
	public int exitRoom(Map<String, Object> paramMap);


	// 채팅방에 남은 인원 확인 
	public int getRemainingMemberCount(int parseInt);


	// 남은 인원이 0명이면 채팅방 삭제
	public void deleteEmptyRoom(int parseInt);
	
    int deleteChatMessages(int chattingNo);
    
    
    int deleteChatRoom(int chattingNo);



	public List<Integer> getChattingRoomMembers(int chattingNo);
	
}

package edu.og.moa.chatting.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.og.moa.chatting.model.dao.ChattingMapper;
import edu.og.moa.chatting.model.dto.chattingRoomkjy;
import edu.og.moa.chatting.model.dto.Messagekjy;
import edu.og.moa.common.utility.Util;
import edu.og.moa.member.model.dto.Member;

@Service
public class ChattingServiceImpl implements ChattingService{

	@Autowired
	private ChattingMapper mapper;
	
	// 메세지 삽입
	@Override
	public int insertMessage(Messagekjy msg) {
		// XSS 방지 처리
		msg.setMessageContent( Util.XSSHandling( msg.getMessageContent() ) );
		
		return mapper.insertMessage(msg);
	}

	// 채팅 목록 조회
	@Override
	public List<chattingRoomkjy> selectRoomList(int memberNo) {
	    System.out.println("ChattingServiceImpl.selectRoomList() 호출됨");
	    System.out.println("memberNo = " + memberNo);
	    
	    List<chattingRoomkjy> list = mapper.selectRoomList(memberNo);
	    System.out.println("mapper 결과 = " + list);

	    if(list != null) {
	        for (chattingRoomkjy r : list) {
	            System.out.println("채팅방 이름: " + r.getRoomName() + ", 인원수: " + r.getMemberCount());
	        }
	    } else {
	        System.out.println("mapper 결과 null");
	    }
	    return list;
	}

	// 채팅 상대 검색
	@Override
	public List<Member> selectTarget(Map<String, Object> map) {
		return mapper.selectTarget(map);
	}
	
	
	// 개인 채팅방 확인 및 없으면 생성
	@Override
	public int checkChattingNo(Map<String, Integer> map) {
	    System.out.println("=== checkChattingNo 시작 ===");
	    System.out.println("loginMemberNo: " + map.get("loginMemberNo"));
	    System.out.println("targetNo: " + map.get("targetNo"));
	    
	    // 1. 두 사람 사이의 기존 채팅방 번호 조회
	    Integer chattingNo = mapper.checkChattingNo(map);
	    
	    if (chattingNo != null) {
	        System.out.println("기존 채팅방 발견: " + chattingNo);
	        return chattingNo;
	    }
	    
	    System.out.println("새 채팅방 생성 시작");
	    
	    // 2. 없으면 새로 채팅방 생성
	    mapper.insertPersonalChatRoom(map);
	    
	    // selectKey로 map에 chattingNo가 세팅됨
	    int newChattingNo = map.get("chattingNo");
	    System.out.println("생성된 채팅방 번호: " + newChattingNo);
	    
	    // 3. 두 명 모두 CHAT_MEMBER 테이블에 등록
	    Map<String, Object> param1 = new HashMap<>();
	    param1.put("chattingNo", newChattingNo);
	    param1.put("memberNo", map.get("loginMemberNo"));
	    int result1 = mapper.insertChatMember(param1);
	    System.out.println("멤버1 추가 결과: " + result1);
	    
	    Map<String, Object> param2 = new HashMap<>();
	    param2.put("chattingNo", newChattingNo);
	    param2.put("memberNo", map.get("targetNo"));
	    int result2 = mapper.insertChatMember(param2);
	    System.out.println("멤버2 추가 결과: " + result2);
	    
	    System.out.println("=== checkChattingNo 완료 ===");
	    return newChattingNo;
	}

	// 팀 채팅방 생성
	@Override
	public int createTeamRoom(List<Integer> memberList) {
	    System.out.println("=== createTeamRoom 시작 ===");
	    System.out.println("멤버 리스트: " + memberList);
	    
	    // 1. 팀 채팅방 생성
	    Map<String, Object> roomMap = new HashMap<>();
	    roomMap.put("openMember", memberList.get(0));  // 첫 번째 멤버를 방장으로
	    
	    mapper.createTeamRoom(roomMap);
	    
	    // selectKey로 roomMap에 chattingNo가 세팅됨
	    int chattingNo = (int) roomMap.get("chattingNo");
	    System.out.println("생성된 팀 채팅방 번호: " + chattingNo);
	    
	    // 2. 멤버 전체를 CHAT_MEMBER에 등록
	    for (int memberNo : memberList) {
	        Map<String, Object> param = new HashMap<>();
	        param.put("chattingNo", chattingNo);
	        param.put("memberNo", memberNo);
	        int result = mapper.insertTeamMember(param);
	        System.out.println("멤버 " + memberNo + " 추가 결과: " + result);
	    }
	    
	    System.out.println("=== createTeamRoom 완료 ===");
	    return chattingNo;
	}


	
	// 채팅방 읽음 표시
	@Override
	public int updateReadFlag(Map<String, Object> paramMap) {
		return mapper.updateReadFlag(paramMap);
	}

	
	// 채팅방 메세지 목록 조회 
	@Override
	public List<Messagekjy> selectMessageList(Map<String, Object> paramMap) {
	    int chattingNo = Integer.parseInt(String.valueOf(paramMap.get("chattingNo")));
	    
	    // 수정: int로 바로 전달
	    List<Messagekjy> messageList = mapper.selectMessageList(chattingNo);
	    
	    return messageList;
	}

	
	// 채팅방 나가기
    @Transactional 
	@Override
	public int exitRoom(Map<String, Object> paramMap) {
		
		int result = mapper.exitRoom(paramMap);
	    // 채팅방에 남은 인원 확인
	    int remainingCount = mapper.getRemainingMemberCount(
	        Integer.parseInt(String.valueOf(paramMap.get("chattingNo")))
	    );
	 // 남은 인원이 0명이면 채팅방 삭제
	    if (remainingCount == 0) {
	        mapper.deleteEmptyRoom(
	            Integer.parseInt(String.valueOf(paramMap.get("chattingNo")))
	        );
	        System.out.println("빈 채팅방 삭제 완료");
	    }
	    
	    System.out.println("=== exitRoom 완료 ===");
	    return result;
	}

 // ChattingServiceImpl
    @Override
    public List<Integer> getChattingRoomMembers(int chattingNo) {
        return mapper.getChattingRoomMembers(chattingNo);
    }


}

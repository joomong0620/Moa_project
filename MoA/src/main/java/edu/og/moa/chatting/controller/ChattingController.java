package edu.og.moa.chatting.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import edu.og.moa.chatting.model.dto.chattingRoomkjy;
import edu.og.moa.chatting.model.dto.Messagekjy;
import edu.og.moa.chatting.model.service.ChattingService;
import edu.og.moa.member.model.dto.Member;


@Controller
public class ChattingController {
	
	@Autowired
	private ChattingService service;
	
	// 채팅 페이지 전환
	@GetMapping("/chatting")
	public String chatting(@SessionAttribute("loginMember") Member loginMember
			, Model model) {
		// 채팅 목록 조회
		List<chattingRoomkjy> roomList = service.selectRoomList (loginMember.getMemberNo());
		model.addAttribute("roomList", roomList);
		System.out.println(roomList);
		return "chatting/chatting";
	}
	
	
	//채팅 상대 검색
	@GetMapping(value="/chatting/selectTarget", produces="application/json; charset=UTF-8")
	@ResponseBody
	public List<Member> selectTarget(
	        @RequestParam(value = "query", required = false) String query,
	        @SessionAttribute("loginMember") Member loginMember) {
	    
	    Map<String, Object> map = new HashMap<>();
	    map.put("query", query);
	    map.put("memberNo", loginMember.getMemberNo());

	    // 전체 회원 리스트 반환
	    List<Member> targetList = service.selectTarget(map);

	    return targetList;
	}
	
	// 채팅방 입장(없으면 생성)
	@PostMapping("/chatting/enter")
	@ResponseBody
	public int chattingEnter(
	        @RequestBody List<Integer> memberList,
	        @SessionAttribute("loginMember") Member loginMember) {

	    if (!memberList.contains(loginMember.getMemberNo())) {
	        memberList.add(loginMember.getMemberNo());
	    }

	    if (memberList.size() == 2) {
	        Map<String, Integer> map = new HashMap<>();
	        int targetNo = memberList.get(0).equals(loginMember.getMemberNo()) ? memberList.get(1) : memberList.get(0);
	        map.put("targetNo", targetNo);
	        map.put("loginMemberNo", loginMember.getMemberNo());
	        return service.checkChattingNo(map);
	    } else {
	        return service.createTeamRoom(memberList);
	    }
	}
	
	// 채팅방 목록 조회
	@GetMapping(value="/chatting/roomList", produces="application/json; charset=UTF-8")
	@ResponseBody
	public List<chattingRoomkjy> selectRoomList(@SessionAttribute("loginMember")Member loginMember) {
		return service.selectRoomList(loginMember.getMemberNo());
	}
	
	// 채팅방 읽음 표시
	@PutMapping("/chatting/updateReadFlag")
	@ResponseBody
	public int updateReadFlag(@RequestBody Map<String, Object> paramMap) {
		return service.updateReadFlag(paramMap);
	}
	
	// 채팅방 메세지 목록 조회
	@GetMapping(value="/chatting/selectMessageList", produces="application/json; charset=UTF-8")
	@ResponseBody
	public List<Messagekjy> selectMessageList(@RequestParam Map<String, Object> paramMap){
		return service.selectMessageList(paramMap);
		
	}
	
	// 채팅방 나가기
	@PostMapping("/chatting/exitRoom")
	@ResponseBody
	public Map<String, Object> exitRoom(@RequestBody Map<String, Object> paramMap) {
	    Map<String, Object> result = new HashMap<>();
	    
	    try {
	        int exitResult = service.exitRoom(paramMap);
	        
	        if (exitResult > 0) {
	            result.put("success", true);
	            result.put("message", "채팅방을 나갔습니다.");
	        } else {
	            result.put("success", false);
	            result.put("message", "채팅방 나가기에 실패했습니다.");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	        result.put("success", false);
	        result.put("message", "오류가 발생했습니다.");
	    }
	    
	    return result;
	}

}
	
	
	


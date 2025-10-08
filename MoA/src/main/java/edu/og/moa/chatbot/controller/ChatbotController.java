package edu.og.moa.chatbot.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import edu.og.moa.chatbot.model.dto.ChattingRoom;
import edu.og.moa.chatbot.model.dto.Message;
import edu.og.moa.board.exhibition.model.dto.Member;
import lombok.extern.slf4j.Slf4j;



@RequestMapping("/chatbot")
@Controller
@Slf4j
@SessionAttributes("loginMember") 
public class ChatbotController {

	//@Autowired
	//private ChattingService service;
	
	
	@GetMapping("/jsonChatbot")  
	public String jsonChatbot( Model model	) throws IOException {
		
		
		// 1) 기본적으로 테스트를 위해 프론트로 넘겨줄 loginMember mock 기본값
		Member loginMember = new Member();
		loginMember.setMemberNickname("한국문화정보원");
		loginMember.setProfileImage("/images/board/exhibition/member/penguin.jpeg"); 
		loginMember.setMemberNo(3); // 임의할당 for testing
		model.addAttribute("loginMember", loginMember); 
    	
    	// 2) boardCode 값 mock
		model.addAttribute("exhibitionCode", 3);  //boardCode === exhibitionCode === communityCode
		model.addAttribute("exhibitionName", "전시게시판");  //boardName === exhibitionName === communityName
		
		
		// 3) roomList mock
		List<ChattingRoom> roomList = new ArrayList<>();
		ChattingRoom chattingRoom = new ChattingRoom();
		
		// mock value setting
		chattingRoom.setChattingNo(10);
		chattingRoom.setLastMessage("머라는 거야?");
		chattingRoom.setSendTime("2025-10-07 16:49:30");
		chattingRoom.setTargetNo(11); //  나챗봇 회원번호
		chattingRoom.setTargetNickName("나챗봇");
		chattingRoom.setTargetProfile("/images/member/chatbot01.jpg");
		chattingRoom.setNotReadCount(7);
		roomList.add(chattingRoom);
		
		model.addAttribute("roomList", roomList);
		// 4) messageList mock
		Map<String, Object> paramMap = new HashMap<>();
		List<Message> messageList =  new ArrayList<>();
		Message message = new Message();
		// mock value setting
		message.setMessageNo(1000);
		message.setMessageContent("이건 대화 컨텍스트 세팅이에요");
		message.setReadFlag("Y");
		message.setSenderNo(3); // 나 전시게시판 "한국문화정보원"이야...
		message.setTargetNo(11); // 너 챗봇이야?
		message.setChattingNo(10);
		message.setSendTime("2025-10-07 18:00:30");
		messageList.add(message);
		
		model.addAttribute("messageList", messageList);
		
		return "chatbot/chatbot";
	}	
	
	
}


package edu.og.moa.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import edu.og.moa.model.dto.Member;
import edu.og.moa.model.service.MemberService;

@RequestMapping("/member")
@Controller
public class MemberController {
	
	@Autowired
	private MemberService service;
	
	
	// 회원가입 화면 이동
	@GetMapping("/signUp")
	public String signUp() {
		
		
		return "member/signUp";
	}
	
	
	@PostMapping("/signUp")
	public String signUp(Member inputMember, String[] memberAddr
			
			
			
			) {
		
		
		int result = service.signUp(inputMember);
		
		
		
		return null;
		
		
		
	}
	
	
	
	

}

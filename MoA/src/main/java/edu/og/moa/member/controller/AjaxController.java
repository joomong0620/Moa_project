package edu.og.moa.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import edu.og.moa.member.model.service.AjaxService;

@RestController
public class AjaxController {
	
	@Autowired
	private AjaxService service;
	
	
	// 아이디 중복 검사
	@GetMapping("/dupCheck/memberId")
	@ResponseBody
	public int dupCheckId(@RequestParam("memberId") String memberId) {
		
		return service.dupCheckId(memberId);
		
	}
	
	// 이메일 중복 검사
	@GetMapping("/dupCheck/email")
	@ResponseBody
	// int 반환 시 : pom.xml에 jackson-databind 추가.  이거 이유 질문하기
	public int checkEmail(String email /* vscode 에서 키 값 email 이다 */) {
			
		return service.checkEmail(email);
	}
		
	// 닉네임 중복검사
	@GetMapping("/dupCheck/nickname")
	@ResponseBody
	public int checkNickname(String nickname) {
			
		return service.checkNickname(nickname);
	}


}

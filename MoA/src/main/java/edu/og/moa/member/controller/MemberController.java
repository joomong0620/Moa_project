package edu.og.moa.member.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.og.moa.member.model.dto.Member;
import edu.og.moa.member.model.service.MemberService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@RequestMapping("/member")
@Controller
@SessionAttributes("loginMember")
public class MemberController {
	
	@Autowired
	private MemberService service;
	
	
	// 로그인
	@PostMapping("/login")
	public String login(@ModelAttribute Member inputMember, Model model, 
			
			@RequestParam (value="saveId", required=false) String saveId,
			
			HttpServletResponse resp,
			
			RedirectAttributes ra
			
					
			
			
			) {
		
		Member loginMember = service.login(inputMember);
		
		
		
		if(loginMember != null) {
		
			
			model.addAttribute("loginMember", loginMember);
			
			Cookie cookie = new Cookie("saveId", loginMember.getMemberId());
			
			if(saveId != null) {
				
				cookie.setMaxAge(60*60*24*30);
						
				
			}else {
				cookie.setMaxAge(0);
			}
			
			cookie.setPath("/");
			resp.addCookie(cookie);
			
			return "redirect:/";
			
		}else {
			
			ra.addFlashAttribute("message", "입력 정보가 올바르지 않습니다.");
			
		}
		
		return "redirect:/login";
		
		
		
	}
	
	// 로그인 페이지 이동
	@GetMapping("/login")
	public String login() {
		
		return "member/login";
	}
	
	
	// 회원가입 화면 이동
	@GetMapping("/signUp")
	public String signUp() {
		
		
		return "member/signUp";
	}
	
	
	@PostMapping("/signUp")
	public String signUp(Member inputMember, String[] memberAddr
			
			
			
			) {
		
		
		int result = service.signUp(inputMember);
		
		if(result > 0){
	        System.out.println("회원가입 성공!");
	    }else{
	        System.out.println("회원가입 실패...");
	    }
		
		
		
		return "common/main";
		
		
		
	}
	
	
	
	

}
